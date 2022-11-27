package controllers.controller;


import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import models.User;

import java.io.IOException;

import controllers.controller.core.Mapping;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;

@WebFilter(urlPatterns = {"/*"})
public class Filter extends HttpFilter {
	private static final long serialVersionUID = 1L;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {}
	
	// NOTICE: the sync no cause problems when accessing resources?
	
	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;
		
		final String path = request.getRequestURI().replaceFirst(request.getContextPath(), "");
		request.setAttribute("path", path);
			
		System.out.println("Filtering: " +path);
		
		HttpSession session = request.getSession();
		synchronized (session.getId().intern()) {
			
			User user = (User) session.getAttribute("user");
			
			Mapping mapping;
			String method = request.getMethod();
			if (method.equals("GET")) {
				mapping = Controller.getGetMapping(path);
				
			} else if (method.equals("POST")) {
				mapping = Controller.getPostMapping(path);
				
			} else {
				response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
				return;
			}
			
			if (mapping == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
			
			if (!mapping.canAccess(user)) {
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
				return;
			}
			
			if (!mapping.hasPermission(null)) {
				response.sendError(HttpServletResponse.SC_FORBIDDEN);
				return;
			}
			
//			if (user == null) {
//				response.sendRedirect(request.getContextPath() +"/login");
//				return;
//			}
			
//			Role role = UserManager.getRole(user);
//			
//			if (role == null) {
//				session.removeAttribute("user");
//				session.invalidate();
//				ServletManager.setError(500, "This account is not yet completed. Please try again later.", request, response);
//				return;
//			}
//			
//			if (PermissionManager.canNotAccess(user, role, path)) {
//				ServletManager.setError(403, "You don't have access rights to this content.", request, response);
//				return;
//			}
//			
//			request.setAttribute("menuLinks", PermissionManager.getMenuLinks(user, role, path));
//			request.setAttribute("role", role.toString());
//			
//			chain.doFilter(request, response);		
//			return;
			
			chain.doFilter(request, response);
		}
	}
	
	@Override
	public void destroy() {}

}
