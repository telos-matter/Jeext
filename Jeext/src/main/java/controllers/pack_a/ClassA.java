package controllers.pack_a;

import controllers.controller.annotations.GetMapping;
import controllers.controller.annotations.WebController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebController
public class ClassA {

	@GetMapping("/test")
	public static void haha (HttpServletRequest request, HttpServletResponse response) {
		System.out.println("wahahahahah");
	}
	
}
