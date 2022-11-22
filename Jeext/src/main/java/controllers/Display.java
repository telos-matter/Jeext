package controllers;


import controllers.controller.core.annotations.*;
import controllers.controller.core.validators.annotations.*;
import controllers.controller.core.consumers.annotations.*;
import controllers.controller.exceptions.InvalidMappingMethod;
import controllers.controller.exceptions.UnsupportedType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import util.User;

@WebController()
public class Display {


	@GetMapping("/run")
	public static void hello (@Required(false) @Flag Boolean age, HttpServletRequest request, HttpServletResponse response) {
	
		System.out.println(age);
	
	}
	
	public static void main (String args []) {
		
	}
	
}
