package controllers;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Date;


import controllers.controller.core.annotations.GetMapping;
import controllers.controller.core.annotations.WebController;
import controllers.controller.core.validators.annotations.Required;
import controllers.controller.exceptions.InvalidMappingMethod;
import controllers.controller.exceptions.UnsupportedType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import util.User;

@WebController()
public class Display {


	@GetMapping("/run")
	public static void hello (String name, @Required(false) String last_name, HttpServletRequest request, HttpServletResponse response) {
		System.out.println("hello " +name +" " +last_name);
	}
	
}
