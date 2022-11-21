package controllers.controller;

import java.io.BufferedReader;
import java.io.File;
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
import controllers.controller.core.Mapping;
import controllers.controller.core.annotations.GetMapping;
import controllers.controller.core.annotations.PostMapping;
import controllers.controller.core.annotations.WebController;
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
import util.Strings;

@WebServlet("/")
public final class Controller extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static Set <Class <?>> controllers;
	
	private static List <Mapping> getMappings;
	private static List <Mapping> postMappings;
	
    private void loadControllers () {
    	
    	/**
    	 * Manually add your controllers here for the time being
    	 * + The -parameters option should be added to the compiler
    	 */
    	
    	controllers = new HashSet <> ();
    	
    	controllers.add(Display.class);   
    	controllers.add(ClassA.class);
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
   						throw new UnsupportedType(type, false);
   					}
   					
   					mappings_list.add(Mapping.validateMapping(controller, method, method_path, mappings_list));
   				}
   			}
   		}
   	}
    
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
				e.printStackTrace();
			}
		}
	}


}
