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
import controllers.controller.exceptions.InvalidInitMethod;
import controllers.controller.exceptions.InvalidParam;
import controllers.controller.exceptions.InvalidPath;
import controllers.controller.exceptions.UnhandledUserException;
import controllers.controller.exceptions.UnsupportedType;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.core.Permission;

@WebServlet("/controllers/*")
public final class Controller extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static Set <Class <?>> controllers;
	
	private static Map <String, Mapping> getMappings;
	private static Map <String, Mapping> postMappings;
	
	public static void writeSimpleText (HttpServletResponse response, String text) {
		try {
			response.getWriter().write("<html><head></head><body><p>" +text +"</p></body></html>");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void writeSimpleText (HttpServletResponse response, Object object) {
		writeSimpleText(response, "" +object);
	}
	
    // TODO: wait why is classes in web-inf
    // NOTICE: controllers should be on the root
    // NOTICE: tomcat specific implementation
	// NOTICE: ~very~ static implementation, no changing names of package nothing
    // NOTICE: /resources & /controllers is taken
	// NOTICE: use the path attr in request not uri
	
	// TODO: add auto resend params
	
	/**
     * The -parameters option should be added to the compiler / or have the name annotation on every mapping parameter
     */
   	public static void load (ServletContext context) {
   		loadControllers(context);
   		
   		getMappings = new HashMap <> ();
   		postMappings = new HashMap <> ();
   		
   		loadMappings(GetMapping.class, getMappings);
   		loadMappings(PostMapping.class, postMappings);
   	}
   	
    private static void loadControllers (ServletContext context) {
    	controllers = loadPackage(
		    			"controllers",
		    			(new File (
		    					String.format("%sWEB-INF%sclasses%scontrollers", context.getRealPath("/"), File.separator, File.separator))
	    					).listFiles(
	    							(File file) -> {return !(file.isDirectory() && file.getName().equals("controller"));}
	    							));
    }
    
    private static Set <Class <?>> loadPackage (String dir, File [] content) {
    	Set <Class <?>> classes = new HashSet <> ();
    	
    	for (File element : content) {
    		if (element.isFile() && element.getName().endsWith(".class")) {
    			loadClassIntoSet(dir +'.' +element.getName(), classes); 
    			
    		} else if (element.isDirectory()) {
    			classes.addAll(loadPackage(dir +'.' +element.getName(), element.listFiles()));
    			
    		}
    	}
    	
    	return classes;
    }
    
    private static void loadClassIntoSet (String name, Set <Class <?>> set) {
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
   					
   					if (type == GetMapping.class) {
   						GetMapping mappingAnnotation = method.getAnnotation(GetMapping.class);
   						
   						path = controllerAnnotation.value() +mappingAnnotation.value();
   						
   						access = (mappingAnnotation.access() == Access.DEFAULT)? controllerAnnotation.access() : mappingAnnotation.access();
   						permissions = (mappingAnnotation.permission().length == 0)? controllerAnnotation.permission() : mappingAnnotation.permission();
   						
   					} else if (type == PostMapping.class) {
   						PostMapping mappingAnnotation = method.getAnnotation(PostMapping.class);
   						
   						path = controllerAnnotation.value() +mappingAnnotation.value();
   						
   						access = (mappingAnnotation.access() == Access.DEFAULT)? controllerAnnotation.access() : mappingAnnotation.access();
   						permissions = (mappingAnnotation.permission().length == 0)? controllerAnnotation.permission() : mappingAnnotation.permission();
   						
   					} else {
   						throw new UnsupportedType(type);
   					}
   					
   					if (!path.startsWith("/") || path.startsWith("/resources") || path.startsWith("/controllers")) {
   						throw new InvalidPath(controller, method, path);
   					}
   					
   					map.put(path, new Mapping(controller, method, access, permissions));
   				}
   			}
   		}
   	}
   	
	@Override
	public void init () {
		initControllers();
	}
	
	private static void initControllers () {
		
   		for (Class <?> controller : controllers) {
   			try {
   				Method init = controller.getMethod("init", null);
   				
   				if ((! Modifier.isPublic(init.getModifiers())) ||
   						(! Modifier.isStatic(init.getModifiers())) ||
   						(! void.class.equals(init.getReturnType()))) {
   					throw new InvalidInitMethod(controller, "Init should have the public and static modifiers, and a return type of void");
   				}
   				
   				if (init.getParameterCount() != 0) {
   					throw new InvalidInitMethod(controller, "Init should expect no parameters");
   				}
   				
   				try {
   					init.invoke(null, null);
   				} catch (IllegalAccessException e) {
   					e.printStackTrace();
   				} catch (IllegalArgumentException e) {
   					e.printStackTrace();
   				} catch (InvocationTargetException e) {
   					throw new UnhandledUserException(e);
   				}
   				
   			} catch (NoSuchMethodException e) {
   				continue;
   			} catch (SecurityException e) {
   				e.printStackTrace();
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
			
			if (cause instanceof InvalidParam) {
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
