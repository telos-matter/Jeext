package controllers;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jeext.controller.Controller;
import jeext.controller.core.HTTPMethod;
import jeext.controller.core.annotations.*;
import jeext.controller.core.param.annotations.Name;
import jeext.controller.core.param.annotations.composer.Composed;
import jeext.controller.core.param.consumers.annotations.*;
import jeext.controller.core.param.types.FileType;
import jeext.controller.core.param.validators.annotations.*;
import jeext.dao.Manager;
import jeext.util.exceptions.UnsupportedType;
import models.User;
import models.permission.Permission;

/**
 * TODO explain  
 *
 */
@WebController
public class Index {
	
	@WebMapping(value = "/", method = HTTPMethod.POST)
	public static void hello (
				@Max (1024 * 1024 * 5) FileType file,
				HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Controller.writeSimpleText(response, "file size= " +file.getLength());
	}
	
}
