package controllers;

import java.io.IOException;

import controllers.controller.core.annotations.*;
import controllers.controller.core.validators.annotations.*;
import controllers.controller.core.consumers.annotations.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebController
public class Index {
	
	@GetMapping("/")
	public static void hello (@Required(false) @Default("world") String name, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setAttribute("name", name);
		
		request.getRequestDispatcher("/WEB-INF/jsps/index.jsp").forward(request, response);
	}
	
}
