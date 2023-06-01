package jeext.controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jeext.controller.core.Access;
import jeext.controller.core.HTTPMethod;
import jeext.controller.core.Path;
import jeext.controller.core.annotations.WebController;
import jeext.controller.core.annotations.WebMapping;
import jeext.controller.core.exceptions.InvalidInitMethod;
import jeext.controller.core.exceptions.InvalidParameter;
import jeext.controller.core.mapping.Mapping;
import jeext.controller.core.mapping.MappingCollection;
import jeext.controller.core.mapping.exceptions.InvalidMappingMethod;
import jeext.controller.util.BooleanEnum;
import jeext.controller.util.JMap;
import jeext.controller.util.exceptions.UnhandledException;
import jeext.util.exceptions.PassedNull;
import jeext.util.exceptions.UnhandledDevException;
import models.permission.Permission;

// TODO maybe add file to params, and check if servlets do indeed work fine with the new allow servlet thing, along side their filter
/**
 * 
 * 
 * the brain behind all of this, no not really the brain, they all do smth
 * manages all of the mappings
 * explain how it works
 * 
 * MENTION dont shit and call a servlet /res and what not
 * MENTION not really tomcat specific since its how war files are
 * ^^^ put in jeext
 */
@WebServlet("/controller/*")
public final class Controller extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	/**
	 * <p>A {@link JMap} of all the URLs and their corresponding
	 * {@link HttpServlet}
	 * <p>Do note that only the {@link HttpServlet} that
	 * use the {@link WebServlet} {@link Annotation}
	 * are known and managed, do not use the "web.xml" file
	 * to define your {@link HttpServlet}s
	 */
	private static JMap <Path, Class<?>> servlets;
	
	/**
	 * <p>A {@link Set} of all of the {@link WebController}
	 * defined by the user
	 * <p>It is populated by {@link #loadFromRoot(ServletContext)}
	 * <p>Once the set is populated it becomes immutable
	 */
	private static Set <Class <?>> webControllers;
	
	/**
	 * A {@link JMap} of all the URLs and their corresponding
	 * {@link MappingCollection}
	 */
	private static JMap <Path, MappingCollection> mappings;

	
	/**
	 * <p>Utility function to write a quick and simple response to the user
	 * <p>Useful when debugging for example
	 * 
	 * @param response
	 * @param text	{@link Object} to be casted to a {@link String} to be written
	 * 
	 * @throws IOException if
	 * the {@link HttpServletResponse}'s {@link PrintWriter} throws one
	 */
	public static void writeSimpleText (HttpServletResponse response, Object text) throws IOException {
		response.getWriter().write("<!DOCTYPE html><html><head></head><body><p>" +text +"</p></body></html>");
	}
	
	/**
	 * <p>Called upon by the {@link Listener} when
	 * the server first starts-up
	 * <p>It loads all the {@link WebController}s and
	 * {@link HttpServlet}s trough the
	 * {@link #loadFromRoot(ServletContext)} method then
	 * loads the individual {@link WebMapping}s
	 * from each one of the {@link WebController}
	 * <p>If for some reason the {@link #loadFromRoot(ServletContext)}
	 * method does not work and is unable to load the {@link WebController}
	 * or produces errors/exceptions, remove the {@link #loadFromRoot(ServletContext)}
	 * method and replace its process manually by adding your
	 * {@link WebController}'s {@link Class}es to it. 
	 * If that is the case however, please
	 * do contact the {@link jeext} framework dev, thank you.
	 * 
	 * @author <a href="https://github.com/telos-matter">telos_matter</a> 
	 */
   	public static void load (ServletContext context) {
   		webControllers = new HashSet <> ();
   		servlets = new JMap <> ();
   		loadFromRoot(context);
   		
   		webControllers = Collections.unmodifiableSet(webControllers);
   		
//   		System.out.println("Loaded webControllers: " +webControllers.size());
//   		for (var controller : webControllers) {
//   			System.out.println("-> " +controller); // To debug if needed
//   		}

//   		System.out.println("Loaded servlets: " +servlets.size());
//   		for (var entry : servlets.entrySet()) {
//   			System.out.println("-> " +entry); // To debug if needed
//   		}
   		
   		mappings = new JMap <> ();
   		loadMappings();
   		
   		System.out.println("Loaded mappings: " +mappings.size());
   		for (var entry: mappings.entrySet()) {
   			System.out.println("-> " +entry); // To debug if needed
   		}
   	}
   	
   	/**
   	 * Reads the content of the root package
   	 * and calls upon {@link #loadFromPackage(String, File[])}
   	 * to load the {@link WebController} and {@link HttpServlet} inside it
   	 * 
   	 * @see #load(ServletContext)
   	 */
    private static void loadFromRoot (ServletContext context) {
    	String root_path = String.format("%sWEB-INF%sclasses", context.getRealPath("/"), File.separator);
    	File root = new File (root_path);
    	File [] content = root
    			.listFiles(
    					(File file) -> {return !(file.isDirectory() && file.getName().equals("jeext"));}
    			);
    	
    	loadFromPackage("", content);
    }
    
    /**
     * Iterates over the content of a package
     * and calls upon {@link #loadClass(String, Set)}
     * for every Java class file inside it, recursively
     * 
     * @see #loadFromRoot(ServletContext)
     */
    private static void loadFromPackage (String dir, File [] content) {
    	for (File file : content) {
    		
    		if (file.isFile() && file.getName().endsWith(".class")) {
    			loadClass(((dir.isBlank())? "" : dir +'.') +file.getName()); 
    			
    		} else if (file.isDirectory()) {
    			loadFromPackage(((dir.isBlank())? "" : dir +'.') +file.getName(), file.listFiles());
    		}
    	}
    }
    
    /**
     * Tries to load the specified {@link Class} if it
     * exists and
     * only adds it to the {@link Set} if it has
     * the {@link WebController} {@link Annotation}
     * or the {@link HttpServlet} {@link Annotation}
	 *
     * @see #loadFromPackage(String, File[])
     */
    private static void loadClass (String name) {
        try {
        	name = name.substring(0, name.lastIndexOf('.'));
        	Class <?> clazz = Class.forName(name);
           
        	if (clazz.isAnnotationPresent(WebController.class)) {
        		webControllers.add(clazz);
        	}
        	if (clazz.isAnnotationPresent(WebServlet.class)) {
        		loadServlet(clazz);
        	}
        } catch (ClassNotFoundException e) {}
    }
    
    /**
     * Loads the specified {@link HttpServlet}
     * {@link Class} along side its URL patterns
     */
    private static void loadServlet (Class <?> clazz) {
    	WebServlet webServlet = clazz.getAnnotation(WebServlet.class);
    	
    	servlets.putAll(
    			Arrays.stream(webServlet.value())
    			.collect(Collectors.toMap(
	    					(String path) -> {return new Path(path);}
	    					,(String path) -> {return clazz;}
    					)));
    	
    	servlets.putAll(
    			Arrays.stream(webServlet.urlPatterns())
    			.collect(Collectors.toMap(
    						(String path) -> {return new Path(path);}
    						,(String path) -> {return clazz;}
    					)));
    }
   	
    /**
     * <p>Collects information necessary from every {@link WebMapping}
     * that exists in the {@link WebController}s, and passes
     * them on to {@link Mapping#Mapping(Class, Method, Access, Permission[], Boolean)}
     * to create a new {@link Mapping}
     * 
     * @throws InvalidMappingMethod if any of the {@link Mapping}s fails
     * a requirement ({@link Mapping}s' URL should start
     * with `/` and should not start with neither `/controller`
     * nor `/res`), or if a {@link Mapping}s' URL already exists
     * 
     * @see #load(ServletContext)
     */
   	private static void loadMappings () {
   		for (Class <?> webController : webControllers) {
   			for (Method method : webController.getDeclaredMethods()) {
   				if (method.isAnnotationPresent(WebMapping.class)) {
   					
   					WebController webControllerAnnotation = webController.getAnnotation(WebController.class);
   					WebMapping webMappingAnnotation = method.getAnnotation(WebMapping.class);
   					
   					String path;
   					HTTPMethod httpMethod = webMappingAnnotation.method();
   					
   					Access access;
   					Permission [] permissions;
   					Boolean anyPermission;
   					
					if (webMappingAnnotation.inherit()) {
						path = webControllerAnnotation.value() +webMappingAnnotation.value();
						
						access = (webMappingAnnotation.access() == Access.DEFAULT)? webControllerAnnotation.access() : webMappingAnnotation.access();
						permissions = (webMappingAnnotation.permissions().length == 0)? webControllerAnnotation.permissions() : webMappingAnnotation.permissions();
						anyPermission = (webMappingAnnotation.anyPermission() == BooleanEnum.NULL)? webControllerAnnotation.anyPermission().getBoolean() : webMappingAnnotation.anyPermission().getBoolean();						
				
					} else {
						path = webMappingAnnotation.value();
						
						access = webMappingAnnotation.access();
						permissions = webMappingAnnotation.permissions();
						anyPermission = webMappingAnnotation.anyPermission().getBoolean();
					}
   					
   					if (!path.startsWith("/") || path.startsWith("/res") || path.startsWith("/controller")) {
   						throw new InvalidMappingMethod(webController, method, "The path (" +path + ") must always start with `/` and should NOT start with neither `/res` nor `/controller`.");
   					}
   					
   					if (servlets.containsKeyEquals(path)) {
   						throw new InvalidMappingMethod(webController, method, "The path `" +path +"` is already taken by this servlet `" +servlets.getEquals(path) +"`.");
   					}
   					
   					MappingCollection collection = mappings.getEquals(path);
   					if (collection == null) {
   						collection = new MappingCollection();
   						mappings.put(new Path(path), collection);
   					}
   					
   					if (collection.methodExists(httpMethod)) {
   						throw new InvalidMappingMethod(webController, method, "There already exists a WebMapping with the `" +httpMethod +"` method for this path: `" +path +"`.");
   					}
   					
   					Mapping mapping = new Mapping(webController, method, access, permissions, anyPermission);
   					collection.putMapping(httpMethod, mapping);
   				}
   			}
   		}
   	}
   	
   	/**
   	 * Calls upon {@link #initControllers()}, which means
   	 * all of the {@link WebController} are initialized
   	 * (if they have an init method) at once when the very first
   	 * request is made to any of the {@link Mapping}s
   	 */
	@Override
	public void init () throws ServletException {
		super.init();
		
		initControllers();
	}
	
	/**
	 * <p>Called upon by {@link #init()}
	 * <p>Iterates over the {@link WebController}s
	 * and call their init method (if they have one)
	 * <p>The init method should be public, static,
	 * must take no argument and have a return type
	 * of {@link Void}
	 * 
	 * @throws InvalidInitMethod if any of the requirements are not met
	 * @throws InvocationTargetException wrapped inside an {@link UnhandledException}
	 * if the init method throws any {@link Exception}s
	 * @throws SecurityException wrapped inside an {@link UnhandledException}
	 * if it's thrown by the init method caller
	 */
	private static void initControllers () {
   		for (Class <?> controller : webControllers) {
   			try {
   				Method init = controller.getMethod("init", null);
   				
   				int modifiers = init.getModifiers();
   				if ((! Modifier.isPublic(modifiers)) ||
   						(! Modifier.isStatic(modifiers)) ||
   						(! void.class.equals(init.getReturnType()))) {
   					throw new InvalidInitMethod(controller, "The method should be public, static and have a return type of void.");
   				}
   				
   				if (init.getParameterCount() != 0) {
   					throw new InvalidInitMethod(controller, "The method should take no parameters.");
   				}
   				
   				try {
   					init.invoke(null, null);
   				} catch (InvocationTargetException e) {
   					throw new UnhandledException(e);
   				} catch (IllegalAccessException | IllegalArgumentException e) {
   					throw new UnhandledDevException(e);
   				}
   				
   			} catch (NoSuchMethodException e) {
   				continue;
   			} catch (SecurityException e) {
   				throw new UnhandledException(e);
   			}
   		}
    }

	/**
	 * Automatically called upon by the {@link Filter} when it forwards
	 * a request to the {@link Controller} and calls the appropriate
	 * {@link Mapping}
	 */
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Mapping mapping = (Mapping) request.getAttribute("mapping");
		invokeMapping(mapping, request, response);
	}
	
	/**
	 * <p>Called upon by the {@link #service(HttpServletRequest, HttpServletResponse)}
	 * with the appropriate
	 * {@link Mapping} 
	 * <p>It then calls upon that {@link Mapping}s' {@link Mapping#invoke(HttpServletRequest, HttpServletResponse)}
	 * <p>It handles the {@link InvalidParameter}
	 * by sending a {@link HttpServletResponse#SC_BAD_REQUEST}
	 * error
	 * 
	 * @throws UnhandledException that wraps an {@link Exception}
	 * if the {@link Mapping} throws any {@link Exception}s
	 * that should be taken care of by the developer, which
	 * would send a {@link HttpServletRequest#SC_INTERNAL_SERVER_ERROR} error
	 * @throws IOException if {@link HttpServletResponse#sendError(int)} throws one
	 */
	private static void invokeMapping (Mapping mapping, HttpServletRequest request, HttpServletResponse response) throws IOException {
		PassedNull.check(mapping, Mapping.class);
		
		try {

			mapping.invoke(request, response);
			
		} catch (InvalidParameter e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			
		} catch (Throwable cause) {
			throw new UnhandledException(cause);
		}
	}
	
	/**
	 * @return	whether or not there exists
	 * a {@link HttpServlet} that handles this
	 * path
	 */
	public static boolean servletExists (String path) {
		return servlets.containsKeyEquals(path);
	}
	
	/**
	 * @return	whether or not there exists
	 * a {@link WebMapping} that handles this
	 * path
	 */
	public static boolean mappingExists (String path) {
		return mappings.containsKeyEquals(path);
	}

	/**
	 * @return	the {@link Mapping} that takes care of that
	 * {@link Path} for that {@link HTTPMethod}, or <code>null</code>
	 * if there is none
	 */
	public static Mapping getMapping (String path, HTTPMethod method) {
		MappingCollection mappingCollection = mappings.getEquals(path);
		if (mappingCollection != null) {
			return mappingCollection.getMapping(method);
		}
		
		return null;
	}
	
	/**
	 * <p>Here just to provide access to the {@link WebController}s
	 * if needed
	 * <p>Keep in mind, the {@link Set} is immutable
	 */
	public static Set <Class <?>> getWebControllers() {
		return webControllers;
	}
	
}
