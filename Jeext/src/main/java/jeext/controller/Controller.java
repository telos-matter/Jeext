package jeext.controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jeext.controller.core.Access;
import jeext.controller.core.HttpMethod;
import jeext.controller.core.Path;
import jeext.controller.core.annotations.GetMapping;
import jeext.controller.core.annotations.PostMapping;
import jeext.controller.core.annotations.WebController;
import jeext.controller.core.annotations.WebMapping;
import jeext.controller.core.exceptions.InvalidInitMethod;
import jeext.controller.core.exceptions.InvalidParameter;
import jeext.controller.core.mapping.Mapping;
import jeext.controller.core.mapping.MappingCollection;
import jeext.controller.core.mapping.exceptions.InvalidMappingMethod;
import jeext.controller.core.param.validators.Validator;
import jeext.controller.core.util.BooleanEnum;
import jeext.controller.core.util.JMap;
import jeext.controller.core.util.exceptions.UnhandledException;
import jeext.models_core.Permission;
import jeext.util.exceptions.FailedRequirement;
import jeext.util.exceptions.PassedNull;
import jeext.util.exceptions.UnhandledDevException;
import jeext.util.exceptions.UnsupportedType;

// TODO make sure you switched all references from /resources to /res

/**
 * 
 * 
 * the brain behind all of this
 * manages all of the mappings
 * explain how it works
 * link to github
 * 
 * TODO make sure its all switched to /controller
 * 
 * @ author telos_matter
 * @ version 2.0.0
 */
@WebServlet("/controller/*")
public final class Controller extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	/**
	 * <p>A {@link Set} of all of the {@link WebController}
	 * defined by the user
	 * <p>It is populated by {@link #loadFromRoot(ServletContext)}
	 * TODO mention that it is immutable
	 */
	private static Set <Class <?>> webControllers;
	
	
	/**
	 * A {@link Map} that contains all of the {@link Mapping}s
	 * that are of the {@link PostMapping} type, keyed by
	 * their final URL ({@link PostMapping#value()})
	 */
	private static JMap <Path, MappingCollection> mappings;
	
	
	// NOTICE: will only add webserlvets, dont use web.xml
	private static JMap <Path, Class<?>> servlets;
	
	/**
	 * <p>Utility function to write a quick and simple response to the user
	 * <p>Useful when debugging for example
	 * 
	 * @param response
	 * @param text	{@link Object} to be casted to a {@link String} to be written
	 * 
	 * @throws IOException wrapped inside an {@link UnhandledException} if
	 * the {@link HttpServletResponse}'s {@link PrintWriter} throws one
	 */
	public static void writeSimpleText (HttpServletResponse response, Object text) {
		try {
			response.getWriter().write("<!DOCTYPE html><html><head></head><body><p>" +text +"</p></body></html>");
		} catch (IOException e) {
			throw new UnhandledException(e);
		}
	}
	
	/**
	 * <p>Called upon by the {@link Listener} when
	 * the server first starts-up
	 * <p>It loads all the {@link WebController}s found
	 * in the {@link jeext.controllers} package trough the
	 * {@link #loadFromRoot(ServletContext)} method then
	 * loads the individual {@link GetMapping}s and {@link PostMapping}s
	 * from each one of the {@link WebController}
	 * <p>To add additional <i>external</i> {@link WebController}
	 * (i.e. those that are
	 * not in the {@link jeext.controllers} package) simply
	 * add them to {@link #webControllers} {@link Set}
	 * after the {@link #loadFromRoot(ServletContext)}
	 * method
	 * <p>If for some reason the {@link #loadFromRoot(ServletContext)}
	 * method does not work and is unable to load the {@link WebController}
	 * or produces errors/exceptions, remove the {@link #loadFromRoot(ServletContext)}
	 * method and replace its process manually by initializing
	 * the {@link #webControllers} {@link Set} and adding to it
	 * your {@link WebController}. If that is the case however, please
	 * do contact the dev, thank you.
	 * 
	 * {@link #doPost(HttpServletRequest, HttpServletResponse)}
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
   		
//   		System.out.println("Loaded mappings: " +mappings.size());
//   		for (var entry: mappings.entrySet()) {
//   			System.out.println("-> " +entry); // To debug if needed
//   		}
   	}
   	
   	/**
   	 * Reads the content of the `controllers` package
   	 * and calls upon {@link #loadFromPackage(String, File[])}
   	 * to load the {@link WebController} inside it
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
     * for every Java class file inside it
     * 
     * @return a {@link Set} of all the classes
     * loaded by {@link #loadClass(String, Set)}
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
     * <p>Collects information necessary from every {@link Mapping}
     * of the specified type
     * (either {@link GetMapping} or {@link PostMapping})
     * that exists in the {@link WebController}s, and passes
     * them on to {@link Mapping#Mapping(Class, Method, Access, Permission[], Boolean)}
     * to create a new {@link Mapping}
     * 
     * @throws InvalidMappingMethod if any of the {@link Mapping}s fails
     * a requirement ({@link Mapping}s' URL should start
     * with `/` and should not start with neither `/controllers`
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
   					HttpMethod httpMethod = webMappingAnnotation.method();
   					
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

//	/**
//	 * Called upon by the {@link Filter} when it forwards
//	 * a request to the {@link Controller} to call the appropriate
//	 * {@link Mapping}
//	 */
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Mapping mapping = (Mapping) request.getAttribute("mapping");
		invokeMapping(mapping, request, response);
	}
	
	/**
	 * <p>Called upon by either {@link #doGet(HttpServletRequest, HttpServletResponse)}
	 * or {@link #doPost(HttpServletRequest, HttpServletResponse)}
	 * with the appropriate {@link Map} that contains the requested
	 * {@link Mapping} 
	 * <p>It then calls upon that {@link Mapping}s' {@link Mapping#invoke(HttpServletRequest, HttpServletResponse)}
	 * <p>It also sends a {@link HttpServletResponse#SC_BAD_REQUEST}
	 * error if any of the sent parameters are not validated
	 * by their {@link Validator}
	 * 
	 * @throws UnhandledException that wraps an {@link Exception}
	 * if the {@link Mapping} throws any {@link Exception}s
	 * that should be taken care of by the developer
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
	
	public static boolean servletExists (String path) {
		return servlets.containsKeyEquals(path);
	}
	
	public static boolean mappingExists (String path) {
		return mappings.containsKeyEquals(path);
	}

	/**
	 * MENTION ofc throws null poiterExpection if there is no such path, and asln should only be used from filter, which checks before 
	 * @param path
	 * @param method
	 * @return
	 */
	public static Mapping getMapping (String path, HttpMethod method) {
		return mappings.getEquals(path).getMapping(method);
	}
	
	/**
	 * Here just to provide access to the {@link WebController}s
	 * if needed
	 * MENTION its immutable
	 */
	public static Set <Class <?>> getWebControllers() {
		return webControllers;
	}
	
}
