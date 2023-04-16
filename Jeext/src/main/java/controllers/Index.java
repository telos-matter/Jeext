package controllers;

import java.io.IOException;

import controllers.controller.core.annotations.*;
import controllers.controller.core.param.consumers.annotations.*;
import controllers.controller.core.param.validators.annotations.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.core.Permission;

/**
 * TODO example, links   
 *
 * @author telos_matter
 * @version 2.0.0
 */
@WebController
public class Index {
	
	@GetMapping(value= "/", permissions= {Permission.ROOT})
	public static void hello (@Required(false) @Default("world") String name, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setAttribute("name", name);
		
		request.getRequestDispatcher("/WEB-INF/jsps/index.jsp").forward(request, response);
	}
	
}
