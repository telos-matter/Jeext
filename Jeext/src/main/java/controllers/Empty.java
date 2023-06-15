package controllers;

import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jeext.controller.core.annotations.MappingConfig;

@MappingConfig
@MultipartConfig (
		  fileSizeThreshold = 1024 * 1024 * 1,
		  maxFileSize = 1024 * 1024 * 10,
		  maxRequestSize = 1024 * 1024 * 50
		)
@WebServlet("/test")
public class Empty extends HttpServlet {

}
