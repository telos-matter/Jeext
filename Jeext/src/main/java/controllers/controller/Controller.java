package controllers.controller;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


import controllers.Display;
import controllers.controller.core.Mapping;
import controllers.controller.core.annotations.GetMapping;
import controllers.controller.core.annotations.PostMapping;
import controllers.controller.core.annotations.WebController;
import controllers.controller.exceptions.InvalidInitMethod;
import controllers.controller.exceptions.InvalidMappingMethod;
import controllers.controller.exceptions.InvalidParam;
import controllers.controller.exceptions.InvalidPath;
import controllers.controller.exceptions.UnhandledUserException;
import controllers.controller.exceptions.UnsupportedType;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/")
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
	
    private static void loadControllers () {
    	/**
    	 * Manually add your controllers here for the time being
    	 * The -parameters option should be added to the compiler
    	 */
    	
    	controllers = new HashSet <> ();
    	
    	controllers.add(Display.class);
    }
    
   	private static void loadMappings (Class <? extends Annotation> type, Map <String, Mapping> map) {
   		for (Class <?> controller : controllers) {
   			for (Method method : controller.getDeclaredMethods()) {
   				if (method.isAnnotationPresent(type)) {
   					
   					String path;
   					
   					if (type == GetMapping.class) {
   						path = controller.getAnnotation(WebController.class).value() +method.getAnnotation(GetMapping.class).value();
   					} else if (type == PostMapping.class) {
   						path = controller.getAnnotation(WebController.class).value() +method.getAnnotation(PostMapping.class).value();
   					} else {
   						throw new UnsupportedType(type);
   					}
   					
   					if (! path.startsWith("/")) {
   						throw new InvalidPath(controller, method, path);
   					}
   					
   					map.put(path, new Mapping(controller, method));
   				}
   			}
   		}
   	}
    
   	public static void load () {
   		loadControllers();
   		
   		getMappings = new HashMap <> ();
   		postMappings = new HashMap <> ();
   		
   		loadMappings(GetMapping.class, getMappings);
   		loadMappings(PostMapping.class, postMappings);
   	}
   	
	@Override
	public void init () {
		initControllers();
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		final String path = request.getRequestURI().replaceFirst(request.getContextPath(), "");

		System.out.println("Path: " +path);
		
		invokeMapping(path, getMappings, request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		final String path = request.getRequestURI().replaceFirst(request.getContextPath(), "");

		invokeMapping(path, postMappings, request, response);
	}
	
	private static void invokeMapping (String path, Map <String, Mapping> map, HttpServletRequest request, HttpServletResponse response) throws IOException {
		Mapping mapping = map.get(path);
		
		if (mapping != null) {
			try {
				
				mapping.invoke(request, response);
				
			} catch (Throwable cause) {
				
				if (cause instanceof InvalidParam) {
					response.sendError(HttpServletResponse.SC_BAD_REQUEST);
				} else {
					throw new UnhandledUserException(cause);
				}
				
			}
		} else {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
	}

}
