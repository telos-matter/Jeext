package controllers;

import java.util.Date;

import controllers.controller.core.annotations.GetMapping;
import controllers.controller.core.annotations.WebController;
import controllers.controller.core.validators.annotations.Required;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import util.User;

@WebController()
public class Display {


	@GetMapping("/run")
	public static void hello (String name, Double d, Integer i, Date da, User u, Boolean b, HttpServletRequest request, HttpServletResponse response) {
		System.out.println("hello " +name);
	}
	
	
}
