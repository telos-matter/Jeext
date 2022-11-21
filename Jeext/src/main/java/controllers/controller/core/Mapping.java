package controllers.controller.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.xmlbeans.impl.xb.xsdschema.impl.PatternDocumentImpl;

import controllers.controller.Controller;
import controllers.controller.annotations.Parammmm;
import controllers.controller.annotations.WebController;
import controllers.controller.exceptions.InvalidMappingMethod;
import controllers.controller.exceptions.InvalidPath;
import controllers.controller.exceptions.PathDuplicate;
import controllers.controller.exceptions.UnspecifiedParam;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class Mapping {

//	private static Pattern pathVariable_regex = Pattern.compile("(?<=\\{)\\w+(?=\\})");
	
	private String raw_path;
	private String path;
	
	private Method method;
	
	private Param [] params;
	
	public boolean matches (String url) {
		return false;
	}
	
	public static Mapping validateMapping (Class <?> controller, Method method, String method_path, List <Mapping> mapping_list) {
		String controller_path = controller.getAnnotation(WebController.class).value();
   		if (controller_path.isBlank()) {
   			controller_path = "";
   		}

   		// TODO: controller_path should not contain path param
   		// TODO: paths shouldn't contain *
   		
   		if (method_path.isBlank()) {
   			method_path = "";
   		}
   		
		if ((! Modifier.isStatic(method.getModifiers())) || (! Modifier.isPublic(method.getModifiers()))) {
   			throw new InvalidMappingMethod(controller, method);
   		}
		
		Parameter [] params = method.getParameters();
		
//		params[0].getName();
		
		
		if ((params.length < 2) ||
				(params[params.length -2].getType() != HttpServletRequest.class) ||
				(params[params.length -1].getType() != HttpServletResponse.class)) {
			throw new InvalidMappingMethod(controller, method);
		}
		
		for (int i = 0; i < params.length -2; i++) {
			
			System.out.println(params[i].getName());
			
			if (! params[i].isAnnotationPresent(Parammmm.class)) {
				//throw new UnspecifiedParam(controller, method, params[i]); // TODO:  idk todo or del
			}
		}
		
		String raw_path = controller_path +method_path;
		
   		if (! raw_path.startsWith("/")) {
			throw new InvalidPath(controller, method, raw_path);
		}
   		
   		// TODO: add path variables

   		
		if (getMapping (raw_path, mapping_list) != null) {
			throw new PathDuplicate(controller, method, raw_path);
		}
		
		Mapping mapping = new Mapping ();
		mapping.raw_path = raw_path;
		mapping.method = method;
		mapping.params = new Param [params.length -2];
		
		for (int i = 0; i < params.length -2; i++) {
			mapping.params[i] = new Param(method, params[i]);
		}
		
		return mapping;
   	}
	
	public static Mapping getMapping (String url, List <Mapping> list) {
		for (Mapping mapping : list) {
			if (mapping.raw_path.equals(url)) {
				return mapping;
			}
		}
		
		return null;
	}

	public void invoke (HttpServletRequest request, HttpServletResponse response) {
		Object [] parameters = new Object [params.length +2];
		
		parameters[parameters.length -2] = request;
		parameters[parameters.length -1] = response;
		
		for (int i = 0; i < parameters.length -2; i++) {
			parameters[i] = params[i].getParam(request);
		}
		
		try {
			method.invoke(null, parameters);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
