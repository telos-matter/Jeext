package controllers.controller;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import controllers.controller.core.Access;
import controllers.controller.core.Mapping;
import controllers.controller.core.annotations.GetMapping;
import controllers.controller.core.annotations.PostMapping;
import controllers.controller.core.annotations.WebController;
import controllers.controller.core.util.BooleanEnum;
import controllers.controller.exceptions.InvalidInitMethod;
import controllers.controller.exceptions.InvalidMappingMethod;
import controllers.controller.exceptions.InvalidParam;
import controllers.controller.exceptions.UnhandledUserException;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.core.Permission;
import util.exceptions.FailedRequirement;
import util.exceptions.PassedNull;
import util.exceptions.UnhandledException;
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
	
	private static Set <Class <?>> controllers;
	
	private static Map <String, Mapping> getMappings;
	private static Map <String, Mapping> postMappings;
	
	public static void writeSimpleText (HttpServletResponse response, Object text) {
		try {
			response.getWriter().write("<html><head></head><body><p>" +text +"</p></body></html>");
		} catch (IOException e) {
			throw new UnhandledUserException(e); // TODO, we sure about this?
		}
	}
	
	// TODO specify how to add individual controllers
   	public static void load (ServletContext context) {
   		loadControllers(context);
   		
   		getMappings = new HashMap <> ();
   		postMappings = new HashMap <> ();
   		
   		loadMappings(GetMapping.class, getMappings);
   		loadMappings(PostMapping.class, postMappings);
   	}
   	
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
    
    private static void loadClass (String name, Set <Class <?>> set) {
        try {
        	Class <?> clazz = Class.forName(name.substring(0, name.lastIndexOf('.')));
           
        	if (clazz.isAnnotationPresent(WebController.class)) {
        		set.add(clazz);
        	}
        } catch (ClassNotFoundException e) {}
    }
   	
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
   	
	@Override
	public void init () throws ServletException {
		super.init();
		
		initControllers();
	}
	
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
   					throw new UnhandledUserException(e);
   				} catch (IllegalAccessException | IllegalArgumentException e) {
   					throw new UnhandledException(e);
   				}
   				
   			} catch (NoSuchMethodException e) {
   				continue;
   			} catch (SecurityException e) {
   				throw new UnhandledUserException(e);
   			}
   		}
    }

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		invokeMapping(getMappings, request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		invokeMapping(postMappings, request, response);
	}
	
	private static void invokeMapping (Map <String, Mapping> map, HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			
			map.get((String) request.getAttribute("path")).invoke(request, response);
			
		} catch (Throwable cause) {
			
			if (cause instanceof InvalidParam) { // TODO: make catch invalidParam then rest throw
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			} else {
				throw new UnhandledUserException(cause);
			}
			
		}
	}
	
	public static Mapping getGetMapping (String path) {
		return getMappings.get(path);
	}
	
	public static Mapping getPostMapping (String path) {
		return postMappings.get(path);
	}

}
