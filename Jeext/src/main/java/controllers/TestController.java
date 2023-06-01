package controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jeext.controller.core.annotations.WebController;
import jeext.controller.core.annotations.WebMapping;
import jeext.controller.core.param.annotations.composer.Composed;
import jeext.controller.core.param.validators.annotations.Required;

@WebController
public class TestController {

	@WebMapping("/test")
	public static void name(
			@Required(false) @Composed(requireAll = false) TestModel id, HttpServletRequest request, HttpServletResponse response) {
		System.out.println("in");
		System.out.println(id);
	}
	
}
