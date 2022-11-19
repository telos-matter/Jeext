package controllers;

import controllers.controller.annotations.GetMapping;
import controllers.controller.annotations.Param;
import controllers.controller.annotations.WebController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebController("/play")
public class Display {


	@GetMapping("/fun")
	public static void hello (HttpServletRequest request, HttpServletResponse response, @Param String name) {
		System.out.println("hello");
	}
	
	
}
