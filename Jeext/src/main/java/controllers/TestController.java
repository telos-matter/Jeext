package controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jeext.controller.core.HTTPMethod;
import jeext.controller.core.annotations.WebController;
import jeext.controller.core.annotations.WebMapping;
import jeext.controller.core.param.annotations.composer.Composed;
import jeext.controller.core.param.validators.annotations.Required;
import models.User;
import testtP.PartTest;

@WebController
public class TestController {

	@WebMapping(value = "/test", method = HTTPMethod.POST)
	public static void name(
//			@Composed(requireAll = false) User id,
			HttpServletRequest request, HttpServletResponse response) {
		System.out.println("in test");
		PartTest.testPart(request);
//		System.out.println(id);
	}
	
}
