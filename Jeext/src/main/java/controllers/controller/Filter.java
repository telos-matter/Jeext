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
	
	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;
		
		final String path = request.getRequestURI().replaceFirst(request.getContextPath(), "");
		
		if (path.startsWith("/resources")) {
			chain.doFilter(request, response);

		} else {
			request.setAttribute("path", path);
			
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
				
				if (mapping.needsPermission()) {
					if (user == null) {
						response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
						return;
						
					} else {
						if (!mapping.hasPermission(user.getPermissions())) {
							response.sendError(HttpServletResponse.SC_FORBIDDEN);
							return;
						}
						
					}
				}
				
				request.getRequestDispatcher("/controllers" + path).forward(request, response);
				return;
			}
			
		}
	}
	
	@Override
	public void destroy() {}

}
