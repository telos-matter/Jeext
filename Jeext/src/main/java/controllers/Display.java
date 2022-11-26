package controllers;


import java.time.LocalDate;

import controllers.controller.Controller;
import controllers.controller.core.annotations.*;
import controllers.controller.core.validators.annotations.*;
import controllers.controller.core.consumers.annotations.*;
import controllers.controller.exceptions.InvalidMappingMethod;
import controllers.controller.exceptions.UnsupportedType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.User;

@WebController
public class Display {

	@GetMapping("/login")
	public static void hello (@Required(false) @Default("Hello!") String name, HttpServletRequest request, HttpServletResponse response) {
	
		Controller.writeSimpleText(response, name);
	
	}
	
	public static void main (String args []) {
		
	}
	
}
