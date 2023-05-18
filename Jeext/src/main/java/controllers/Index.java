package controllers;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jeext.controller.Controller;
import jeext.controller.core.annotations.*;
import jeext.controller.core.param.annotations.Name;
import jeext.controller.core.param.consumers.annotations.*;
import jeext.controller.core.param.validators.annotations.*;
import jeext.dao.Manager;
import jeext.util.exceptions.UnsupportedType;
import models.User;
import models.permission.Permission;

/**
 * TODO explain  
 *
 */
@WebController
public class Index {
	
	public static void name(Integer   i) {
		System.out.println("penis" +i);
	}
	
	public static void main(String[] args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
//		Method method = Index.class.getMethods()[1];
//		Parameter parameter = method.getParameters()[0];
//		System.out.println(parameter);
//		System.out.println(parameter.getType());
//		System.out.println(parameter.getParameterizedType());
//		ParameterizedType t = (ParameterizedType) parameter.getParameterizedType();
//		System.out.println(t.getActualTypeArguments().length);
//		System.out.println(parameter.getParameterizedType().getTypeName());
//		System.out.println(parameter.getType().getComponentType());
//		System.out.println(parameter.getType().isArray());
//		System.out.println(parameter.getType().arrayType());
		
		int [] i = new int [10];
		Object  o  = i;
		i = (int[]) o;
		Object [] j = (Object[]) o;
		
//		List<Integer> l = List.of(1,2,69);
//		method.invoke(null, l);
		
//		LocalTime t = LocalTime.now();
//		LocalDateTime l = LocalDateTime.now();
//		System.out.println(t);
//		System.out.println(l);
	}
	
	@WebMapping("/")
	public static void hello (
//				@Required(false) @Default("world") String name,
//				@Required(false) @Default("12") Integer age,
//				@Required(false) @Default("1") @Name("id") User user ,
				Integer age,
				List<Integer> ages,
//				List <Integer> ages_l,
//				@Composed() User userr,
				HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		request.setAttribute("name", name);
//		
//		request.getRequestDispatcher("/WEB-INF/jsps/index.jsp").forward(request, response);
		
//		System.out.println("?");
//		user = Manager.find(User.class, "1");
		
		System.out.println("Age: "+ age);
		System.out.println("Ages: " +ages);
		if (ages != null) {	
			for (var i : ages) {
				System.out.println("-> " +i);
			}
		}
		
//		Collections
//		List<E>
		
		Controller.writeSimpleText(response, "out");
//		Controller.writeSimpleText(response, "name: " +name +", age: " +age +"\nuser: "+user);
	}
	
}
