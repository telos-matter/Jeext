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
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


import controllers.Display;
import controllers.controller.annotations.GetMapping;
import controllers.controller.annotations.PostMapping;
import controllers.controller.annotations.WebController;
import controllers.controller.core.Mapping;
import controllers.controller.exceptions.InvalidMappingMethod;
import controllers.controller.exceptions.InvalidPath;
import controllers.controller.exceptions.PathDuplicate;
import controllers.controller.exceptions.UnsupportedType;
import controllers.pack_a.ClassA;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import util.StringManager;

@WebServlet("/")
public final class Controller extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static Set <Class <?>> controllers;
	
	private static List <Mapping> getMappings;
	private static List <Mapping> postMappings;
	
	public static void main (String [] args) {
//		Pattern pathVariable_regex = Pattern.compile("(?<=\\{)\\w+(?=\\})");

//		String pattern =  "/foo/\\w*\\-lol/view";  //   "/foo/*-lol/view";
//		String s = "/foo/ajsdkfh85345-843-lol/view";
//		
//		System.out.println(Pattern.matches(pattern, s));
		
		
//		String pattern =  "(?<=\\{)\\w+(?=\\})";
//		String s = "/foo/{l}-lol/view";
//		
//		System.out.println(Pattern.matches(pattern, s));
		
		new Controller().init();
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

    
   	private static void loadMappings (Class <? extends Annotation> type, List <Mapping> mappings_list) {
   		for (Class <?> controller : controllers) {
   			for (Method method : controller.getDeclaredMethods()) {
   				if (method.isAnnotationPresent(type)) {
   					
   					String method_path;
   					
   					if (type == GetMapping.class) {
   						method_path = method.getAnnotation(GetMapping.class).value();
   					} else if (type == PostMapping.class) {
   						method_path = method.getAnnotation(PostMapping.class).value();
   					} else {
   						throw new UnsupportedType(type);
   					}
   					
   					mappings_list.add(Mapping.validateMapping(controller, method, method_path, mappings_list));
   				}
   			}
   		}
   	}
//   	
//	public static <T> List <T> assertEntities (String id_parameter, Class <T> type, HttpServletRequest request, HttpServletResponse response) throws IOException {
//	List <Integer> ids = assertParameters (id_parameter, Integer.class, request, response);
//	if (ids == null) {
//		return null;
//	} else {
//		List <T> entities = new ArrayList <> ();
//		for (Integer id : ids) {
//			T entity = Manager.find(type, id);
//			if (entity == null) {
//				setError(404, "The requested " +type.getSimpleName() +" doesn't exist.", request, response);
//				return null;
//			}
//			entities.add(entity);
//		}
//		return entities;
//	}
//}
//
//public static <T> List <T> assertParameters (String name, Class <T> type, HttpServletRequest request, HttpServletResponse response) throws IOException {
//	String [] request_parameters = request.getParameterValues(name);
//	if (request_parameters == null) {
//		setError(400, "The HTTP request is incomplete.", request, response);
//		return null;
//	} else if (Integer.class.equals(type)) {
//		List <T> parameters = new ArrayList <T> ();
//		Integer parameter = null;
//		for (String request_parameter : request_parameters) {
//			parameter = StringManager.parseInt(request_parameter);
//			if (parameter == null) {
//				setError(400, "The HTTP request is corrupted.", request, response);
//				return null;
//			}
//			parameters.add((T) parameter);
//		}
//		return parameters;
//	} else if (Boolean.class.equals(type)) {
//		List <T> parameters = new ArrayList <T> ();
//		Boolean parameter = null;
//		for (String request_parameter : request_parameters) {
//			parameter = StringManager.parseBool(request_parameter);
//			if (parameter == null) {
//				setError(400, "The HTTP request is corrupted.", request, response);
//				return null;
//			}
//			parameters.add((T) parameter);
//		}
//		return parameters;
//	} else if (Float.class.equals(type)) {
//		List <T> parameters = new ArrayList <T> ();
//		Float parameter = null;
//		for (String request_parameter : request_parameters) {
//			parameter = StringManager.parseFloat(request_parameter);
//			if (parameter == null) {
//				setError(400, "The HTTP request is corrupted.", request, response);
//				return null;
//			}
//			parameters.add((T) parameter);
//		}
//		return parameters;
//	} else if (Date.class.equals(type)) {
//		List <T> parameters = new ArrayList <T> ();
//		Date parameter = null;
//		for (String request_parameter : request_parameters) {
//			parameter = StringManager.parseDate(request_parameter);
//			if (parameter == null) {
//				setError(400, "The HTTP request is corrupted.", request, response);
//				return null;
//			}
//			parameters.add((T) parameter);
//		}
//		return parameters;
//	} else {
//		System.out.println("An unsupported type (" +type +") parameter in assertParameters.");
//		setError(500, "An unsupported type (" +type +") parameter in assertParameters.", request, response);
//		return null;
//	}
//}
//
//public static <T> T assertEntity (Class <T> type, HttpServletRequest request, HttpServletResponse response) throws IOException {
//	return assertEntity("id", type, request, response);
//}
//
//public static <T> T assertEntity (String id_parameter, Class <T> type, HttpServletRequest request, HttpServletResponse response) throws IOException {
//	Integer id = assertParameter (id_parameter, Integer.class, request, response);
//	if (id == null) {
//		return null;
//	} else {
//		T entity = Manager.find(type, id);
//		if (entity == null) {
//			setError(404, "The requested " +type.getSimpleName() +" doesn't exist.", request, response);
//		}
//		return entity;
//	}
//}
//
//public static <T> T assertParameter (String name, Class <T> type, HttpServletRequest request, HttpServletResponse response) throws IOException {
//	String request_parameter = request.getParameter(name);
//	if (request_parameter == null) {
//		setError(400, "The HTTP request is incomplete.", request, response);
//		return null;
//	} else if (Integer.class.equals(type)) {
//		Integer parameter = StringManager.parseInt(request_parameter);
//		if (parameter == null) {
//			setError(400, "The HTTP request is corrupted.", request, response);
//			return null;
//		} else {
//			return (T) parameter;
//		}
//	} else if (Boolean.class.equals(type)) {
//		Boolean parameter = StringManager.parseBool(request_parameter);
//		if (parameter == null) {
//			setError(400, "The HTTP request is corrupted.", request, response);
//			return null;
//		} else {
//			return (T) parameter;
//		}
//	} else if (Float.class.equals(type)) {
//		Float parameter = StringManager.parseFloat(request_parameter);
//		if (parameter == null) {
//			setError(400, "The HTTP request is corrupted.", request, response);
//			return null;
//		} else {
//			return (T) parameter;
//		}
//	} else if (Date.class.equals(type)) {
//		Date parameter = StringManager.parseDate(request_parameter);
//		if (parameter == null) {
//			setError(400, "The HTTP request is corrupted.", request, response);
//			return null;
//		} else {
//			return (T) parameter;
//		}
//	} else {
//		System.out.println("An unsupported type (" +type +") parameter in assertParameter.");
//		setError(500, "An unsupported type (" +type +") parameter in assertParameter.", request, response);
//		return null;
//	}
//}
//
//public static <T> T getEntity (Class <T> type, HttpServletRequest request) {
//	return getEntity("id", type, request);
//}
//
//	public static <T> T getEntity (String id_parameter, Class <T> type, HttpServletRequest request) {
//		Integer id = getParameter (id_parameter, Integer.class, request);
//		if (id == null) {
//			return null;
//		} else {
//			return Manager.find(type, id);
//		}
//	}

    
	@Override
	public void init () {
		loadControllers();

		getMappings = new LinkedList <> ();
		postMappings = new LinkedList <> ();
		
		loadMappings(GetMapping.class, getMappings);
		loadMappings(PostMapping.class, postMappings);
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		final String url = request.getRequestURI().replaceFirst(request.getContextPath(), "");

		invokeMapping(url, getMappings, request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		final String url = request.getRequestURI().replaceFirst(request.getContextPath(), "");

		invokeMapping(url, postMappings, request, response);
	}
	
	private static void invokeMapping (String url, List <Mapping> mappings, HttpServletRequest request, HttpServletResponse response) {
		Mapping mapping = Mapping.getMapping(url, mappings);
		
		if (mapping != null) {
			
			mapping.invoke(request, response);
			
		} else {
			try {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
			} catch (IOException e) {
				System.out.println("? huh");
				e.printStackTrace();
			}
		}
	}


}
