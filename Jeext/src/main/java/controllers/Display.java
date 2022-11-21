package controllers;

import controllers.controller.annotations.GetMapping;
import controllers.controller.annotations.Parammmm;
import controllers.controller.annotations.WebController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebController()
public class Display {


	@GetMapping("/run")
	public static void hello (String name, HttpServletRequest request, HttpServletResponse response) {
		System.out.println("hello " +name);
	}
	
	
}
