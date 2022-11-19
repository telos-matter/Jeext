package controllers.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


import controllers.Display;
import controllers.controller.annotations.GetMapping;
import controllers.controller.annotations.PostMapping;
import controllers.controller.annotations.WebController;
import controllers.controller.exceptions.InvalidMappingMethod;
import controllers.controller.exceptions.InvalidPath;
import controllers.controller.exceptions.PathDuplicate;
import controllers.controller.exceptions.UnsupportedType;
import controllers.controller.path.Path;
import controllers.pack_a.ClassA;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/")
public final class Controller extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static Set <Class <?>> controllers;
	
	private static Map <Path, Method> getMappings;
	private static Map <Path, Method> postMappings;
	
	public static void main (String [] a) {
		
		getMappings.forEach((path, method) -> {System.out.println(path);});

	}

    private void loadControllers () {
//		System.out.println(getServletContext().getRealPath(parent_name));

    	controllers = new HashSet <> ();
    	controllers.add(Display.class);   
    	controllers.add(ClassA.class);
    	
//    	String package_name = Controller.class.getPackageName();
//    	String parent_name = (package_name.contains("."))? package_name.substring(0, package_name.lastIndexOf('.')) : package_name;
//    	
//    	controllers = loadPackage(parent_name, 
//    			new BufferedReader(
//    					new InputStreamReader(
//    							ClassLoader.getSystemClassLoader().
//    							getResourceAsStream(parent_name))).
//    			lines().
//    			filter(name -> !name.equals("controller")).
//    			collect(Collectors.toList()));
    }
    
    private static Set <Class <?>> loadPackage (String name, List <String> content) {
    	Set <Class <?>> classes = new HashSet <> ();
    	
    	final String _name;
    	if (name.isEmpty()) {
    		_name = name;
    	} else {
    		_name = name +'.';
    	}
    	
    	content.forEach(element -> {
    		if (element.endsWith(".class")) {
    			
    			loadClassIntoSet(_name +element, classes); 
    			
    		} else {
    			
    	    	classes.addAll(
    	    			loadPackage(_name +element,
		    	    					new BufferedReader(
		    							new InputStreamReader(
										ClassLoader.getSystemClassLoader().
										getResourceAsStream((_name +element).
										replaceAll("[.]", "/")))).
		    	    					lines().
		    	    					collect(Collectors.toList())));
    		
    		}
    	});
    	
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
	
    
//   	private static void loadGetMappings () {
//   		getMappings = new HashMap <> ();
//   		
//   		for (Class <?> controller : controllers) {
//   			for (Method method : controller.getDeclaredMethods()) {
//   				if (method.isAnnotationPresent(GetMapping.class)) {
//   					String path = controller.getAnnotation(WebController.class).value() +method.getAnnotation(GetMapping.class).value();
//   					
//   					if (validateMapping(controller, method, path, getMappings)) {
//   						//getMappings.put(path, method);
//   					}
//   					
//   				}
//   			}
//   		}
//   	}
//   	
//   	private static void loadPostMappings () {
//   		postMappings = new HashMap <> ();
//   		
//   		for (Class <?> controller : controllers) {
//   			for (Method method : controller.getDeclaredMethods()) {
//   				if (method.isAnnotationPresent(PostMapping.class)) {
//   					String path = controller.getAnnotation(WebController.class).value() +method.getAnnotation(PostMapping.class).value();
//   					
//   					if (validateMapping(controller, method, path, postMappings)) {
//   						//postMappings.put(path, method);
//   					}
//   					
//   				}
//   			}
//   		}
//   	}
   	
   	private static void loadMappings (Class <? extends Annotation> type, Map <Path, Method> map) {
   		for (Class <?> controller : controllers) {
   			for (Method method : controller.getDeclaredMethods()) {
   				
//   				System.out.println(method.getName());
//   				
//   				for (Parameter param : method.getParameters()) {
//   					System.out.println(param.getName() +"->" +param.getType().getName());
//   				}
//   				
//   				for (Class <?> clazz : method.getParameterTypes()) {
//   					System.out.println(clazz.getName());
//   				}
   				
   				
   				if (method.isAnnotationPresent(type)) {
   					
   					String path;
   					
   					if (type == GetMapping.class) {
   						path = controller.getAnnotation(WebController.class).value() +method.getAnnotation(GetMapping.class).value();
   					} else if (type == PostMapping.class) {
   						path = controller.getAnnotation(WebController.class).value() +method.getAnnotation(PostMapping.class).value();
   					} else {
   						throw new UnsupportedType(type);
   					}
   					
   					if (validateMapping(controller, method, path, map)) {
   						map.put(new Path(path), method);
   					}
   					
   				}
   			}
   		}
   	}
   	
   	private static boolean validateMapping (Class <?> controller, Method method, String path, Map <Path, Method> map) {
   		if ((! Modifier.isStatic(method.getModifiers())) || (! Modifier.isPublic(method.getModifiers()))) {
   			throw new InvalidMappingMethod(controller, method);
   		}
   		{
   			Parameter [] params = method.getParameters();
   			
   			if ((params.length < 2) ||
   					(params[0].getType() != HttpServletRequest.class) ||
   					(params[1].getType() != HttpServletResponse.class)) {
   				throw new InvalidMappingMethod(controller, method);
   			}
   		}
   		if (! path.startsWith("/")) {
			throw new InvalidPath(controller, method, path);
		}
		if (map.containsKey(new Path(path))) {
			throw new PathDuplicate(controller, method, path);
		}
		
		return true;
   	}

    
	@Override
	public void init () {
		loadControllers();

		getMappings = new HashMap <> ();
		postMappings = new HashMap <> ();
		
		loadMappings(GetMapping.class, getMappings);
		loadMappings(PostMapping.class, postMappings);
		
//		loadGetMappings();
//		loadPostMappings();
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		final String URN = request.getRequestURI().replaceFirst(request.getContextPath(), "");

		System.out.println("Calling: " +URN);
		
		try {
			Method method = getMappings.get(new Path(URN));
			if (method != null) {
				
				method.invoke(null, "hihi");
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		
		
		
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
	
	}


}
