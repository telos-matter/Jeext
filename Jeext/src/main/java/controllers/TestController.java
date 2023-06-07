package controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jeext.controller.core.annotations.WebController;
import jeext.controller.core.annotations.WebMapping;
import jeext.controller.core.param.annotations.composer.Composed;
import jeext.controller.core.param.validators.annotations.Required;
import models.User;

@WebController
public class TestController {

	@WebMapping("/test")
	public static void name(
			@Composed(requireAll = false) User id, HttpServletRequest request, HttpServletResponse response) {
		System.out.println("in");
		System.out.println(id);
	}
	
}
