package controllers;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jeext.controller.Controller;
import jeext.controller.core.annotations.*;
import jeext.controller.core.param.annotations.Name;
import jeext.controller.core.param.consumers.annotations.*;
import jeext.controller.core.param.validators.annotations.*;
import jeext.dao.Manager;
import models.User;
import models.permission.Permission;

/**
 * TODO explain  
 *
 */
@WebController
public class Index {
	
	@WebMapping("/")
	public static void hello (
				@Required(false) @Default("world") String name,
				@Required(false) @Default("12") Integer age,
				@Required(false) @Default("1") @Name("id") User user ,
				@Composed() User userr,
				HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		request.setAttribute("name", name);
//		
//		request.getRequestDispatcher("/WEB-INF/jsps/index.jsp").forward(request, response);
		
		System.out.println("?");
		user = Manager.find(User.class, "1");
		
//		Collections
		List<E>
		
		Controller.writeSimpleText(response, "name: " +name +", age: " +age +"\nuser: "+user);
	}
	
}
