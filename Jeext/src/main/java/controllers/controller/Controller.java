package controllers.controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import controllers.controller.core.Access;
import controllers.controller.core.annotations.GetMapping;
import controllers.controller.core.annotations.PostMapping;
import controllers.controller.core.annotations.WebController;
import controllers.controller.core.exceptions.InvalidInitMethod;
import controllers.controller.core.exceptions.InvalidParameter;
import controllers.controller.core.mapping.Mapping;
import controllers.controller.core.mapping.exceptions.InvalidMappingMethod;
import controllers.controller.core.param.validators.Validator;
import controllers.controller.core.util.BooleanEnum;
import controllers.controller.core.util.exceptions.UnhandledException;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.core.Permission;
import util.exceptions.FailedRequirement;
import util.exceptions.PassedNull;
import util.exceptions.UnhandledDevException;
import util.exceptions.UnsupportedType;

// TODO make sure you switched all references from /resources to /res

/**
 * 
 * 
 * the brain behind all of this
 * manages all of the mappings
 * explain how it works
 * link to github
 * 
 * @ author telos_matter
 * @ version 2.0.0
 */
@WebServlet("/controllers/*")
public final class Controller extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	/**
	 * <p>A {@link Set} of all of the {@link WebController}
	 * defined by the user
	 * <p>It is populated by {@link #loadControllers(ServletContext)}
	 */
	private static Set <Class <?>> controllers;
	
	/**
	 * A {@link Map} that contains all of the {@link Mapping}s
	 * that are of the {@link GetMapping} type, keyed by
	 * their final URL ({@link GetMapping#value()})
	 */
	private static Map <String, Mapping> getMappings;
	/**
	 * A {@link Map} that contains all of the {@link Mapping}s
	 * that are of the {@link PostMapping} type, keyed by
	 * their final URL ({@link PostMapping#value()})
	 */
	private static Map <String, Mapping> postMappings;
	
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
	 * in the {@link controllers} package trough the
	 * {@link #loadControllers(ServletContext)} method then
	 * loads the individual {@link GetMapping}s and {@link PostMapping}s
	 * from each one of the {@link WebController}
	 * <p>To add additional <i>external</i> {@link WebController}
	 * (i.e. those that are
	 * not in the {@link controllers} package) simply
	 * add them to {@link #controllers} {@link Set}
	 * after the {@link #loadControllers(ServletContext)}
	 * method
	 * <p>If for some reason the {@link #loadControllers(ServletContext)}
	 * method does not work and is unable to load the {@link WebController}
	 * or produces errors/exceptions, remove the {@link #loadControllers(ServletContext)}
	 * method and replace its process manually by initializing
	 * the {@link #controllers} {@link Set} and adding to it
	 * your {@link WebController}. If that is the case however, please
	 * do contact the dev, thank you.
	 * 
	 * {@link #doPost(HttpServletRequest, HttpServletResponse)}
	 * 
	 * @author <a href="https://github.com/telos-matter">telos_matter</a> 
	 */
   	public static void load (ServletContext context) {
   		loadControllers(context);
   		
//   		for (Class <?> controller : controllers) {
//   			System.out.println("Loaded: " +controller); // To debug if needed
//   		}
   		
   		getMappings = new HashMap <> ();
   		postMappings = new HashMap <> ();
   		
   		loadMappings(GetMapping.class, getMappings);
   		loadMappings(PostMapping.class, postMappings);
   	}
   	
   	/**
   	 * Reads the content of the `controllers` package
   	 * and calls upon {@link #loadPackage(String, File[])}
   	 * to load the {@link WebController} inside it
   	 * 
   	 * @see #load(ServletContext)
   	 */
    private static void loadControllers (ServletContext context) {
    	String root_path = String.format("%sWEB-INF%sclasses%scontrollers", context.getRealPath("/"), File.separator, File.separator);
    	File root = new File (root_path);
    	File [] content = root.listFiles(
    			(File file) -> {
    				return !(file.isDirectory() && file.getName().equals("controller"));
    				}
    			);
    	
    	controllers = loadPackage("controllers", content);
    }
    
    /**
     * Iterates over the content of a package
     * and calls upon {@link #loadClass(String, Set)}
     * for every Java class file inside it
     * 
     * @return a {@link Set} of all the classes
     * loaded by {@link #loadClass(String, Set)}
     * 
     * @see #loadControllers(ServletContext)
     */
    private static Set <Class <?>> loadPackage (String dir, File [] content) {
    	Set <Class <?>> classes = new HashSet <> ();
    	
    	for (File file : content) {
    		if (file.isFile() && file.getName().endsWith(".class")) {
    			loadClass(dir +'.' +file.getName(), classes); 
    		} else if (file.isDirectory()) {
    			classes.addAll(loadPackage(dir +'.' +file.getName(), file.listFiles()));
    		}
    	}
    	
    	return classes;
    }
    
    /**
     * Tries to load the specified {@link Class} if it
     * exists and
     * only adds it to the {@link Set} if it has
     * the {@link WebController} {@link Annotation}
	 *
     * @see #loadPackage(String, File[])
     */
    private static void loadClass (String name, Set <Class <?>> set) {
        try {
        	Class <?> clazz = Class.forName(name.substring(0, name.lastIndexOf('.')));
           
        	if (clazz.isAnnotationPresent(WebController.class)) {
        		set.add(clazz);
        	}
        } catch (ClassNotFoundException e) {}
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
   	private static void loadMappings (Class <? extends Annotation> type, Map <String, Mapping> map) {
   		for (Class <?> controller : controllers) {
   			for (Method method : controller.getDeclaredMethods()) {
   				if (method.isAnnotationPresent(type)) {
   					
   					WebController controllerAnnotation = controller.getAnnotation(WebController.class);
   					
   					String path;
   					Access access;
   					Permission [] permissions;
   					Boolean anyPermission;
   					
   					if (type == GetMapping.class) {
   						GetMapping mappingAnnotation = method.getAnnotation(GetMapping.class);
   						
   						path = controllerAnnotation.value() +mappingAnnotation.value();
   						
   						access = (mappingAnnotation.access() == Access.DEFAULT)? controllerAnnotation.access() : mappingAnnotation.access();
   						permissions = (mappingAnnotation.permissions().length == 0)? controllerAnnotation.permissions() : mappingAnnotation.permissions();
   						anyPermission = (mappingAnnotation.anyPermission() == BooleanEnum.NULL)? controllerAnnotation.anyPermission().getBoolean() : mappingAnnotation.anyPermission().getBoolean();
   						
   					} else if (type == PostMapping.class) {
   						PostMapping mappingAnnotation = method.getAnnotation(PostMapping.class);
   						
   						path = controllerAnnotation.value() +mappingAnnotation.value();
   						
   						access = (mappingAnnotation.access() == Access.DEFAULT)? controllerAnnotation.access() : mappingAnnotation.access();
   						permissions = (mappingAnnotation.permissions().length == 0)? controllerAnnotation.permissions() : mappingAnnotation.permissions();
   						anyPermission = (mappingAnnotation.anyPermission() == BooleanEnum.NULL)? controllerAnnotation.anyPermission().getBoolean() : mappingAnnotation.anyPermission().getBoolean();

   					} else {
   						throw new UnsupportedType(type);
   					}
   					
   					if (!path.startsWith("/") || path.startsWith("/res") || path.startsWith("/controllers")) {
   						throw new InvalidMappingMethod(controller, method, "The path '" +path + "' must always start with '/' and should NOT start with neither '/res' nor '/controllers'");
   					}
   					
   					if (map.containsKey(path)) {
   						throw new InvalidMappingMethod(controller, method, "The path '" +path +"' already exists");
   					}
   					
   					Mapping mapping = new Mapping(controller, method, access, permissions, anyPermission);
   					map.put(path, mapping);
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
   		for (Class <?> controller : controllers) {
   			try {
   				Method init = controller.getMethod("init", null);
   				
   				int modifiers = init.getModifiers();
   				if ((! Modifier.isPublic(modifiers)) ||
   						(! Modifier.isStatic(modifiers)) ||
   						(! void.class.equals(init.getReturnType()))) {
   					throw new InvalidInitMethod(controller, "The method should be public, static and have a return type of void");
   				}
   				
   				if (init.getParameterCount() != 0) {
   					throw new InvalidInitMethod(controller, "The method should take no parameters");
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
	 * Called upon by the {@link Filter} when it forwards
	 * a request to the {@link Controller} to call the appropriate
	 * {@link Mapping}
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		invokeMapping(getMappings, request, response);
	}
	
	/**
	 * Called upon by the {@link Filter} when it forwards
	 * a request to the {@link Controller} to call the appropriate
	 * {@link Mapping}
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		invokeMapping(postMappings, request, response);
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
	private static void invokeMapping (Map <String, Mapping> map, HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			
			map.get((String) request.getAttribute("path")).invoke(request, response);
			
		} catch (InvalidParameter e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			
		} catch (Throwable cause) {
			throw new UnhandledException(cause);
			
		}
	}
	
	/**
	 * Mainly used in the {@link Filter}
	 * to retrieve the appropriate {@link Mapping}
	 */
	public static Mapping getGetMapping (String path) {
		return getMappings.get(path);
	}
	
	/**
	 * Mainly used in the {@link Filter}
	 * to retrieve the appropriate {@link Mapping}
	 */
	public static Mapping getPostMapping (String path) {
		return postMappings.get(path);
	}

	/**
	 * Here just to provide access to the {@link WebController}s
	 * if needed
	 */
	public static Set <Class <?>> getControllers() {
		return controllers;
	}
	
}
