package controllers;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jeext.controller.core.annotations.*;
import jeext.controller.core.param.consumers.annotations.*;
import jeext.controller.core.param.validators.annotations.*;
import jeext.models_core.Permission;

/**
 * TODO example, links   
 *
 * @ author telos_matter
 * @ version 2.0.0
 */
@WebController
public class Index {
	
	@WebMapping("/")
	public static void hello (@Required(false) @Default("world") String name, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("?");
		request.setAttribute("name", name);
		
		request.getRequestDispatcher("/WEB-INF/jsps/index.jsp").forward(request, response);
	}
	
}
