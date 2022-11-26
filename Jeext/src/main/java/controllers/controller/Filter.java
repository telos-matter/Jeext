package controllers.controller;

//package controllers.controller;
//
//import jakarta.servlet.http.HttpFilter;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.servlet.http.HttpSession;
//
//import java.io.IOException;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.FilterConfig;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.ServletRequest;
//import jakarta.servlet.ServletResponse;
//
//public class GlobalFilter extends HttpFilter {
//	private static final long serialVersionUID = 1L;
//	
////	@SuppressWarnings("unused")
//	private FilterConfig filterConfig = null;
//
//	@Override
//	public void init(FilterConfig filterConfig) throws ServletException {
//		this.filterConfig = filterConfig;
//	}
//	
//	@Override
//	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
//		HttpServletRequest request = (HttpServletRequest) servletRequest;
//		HttpServletResponse response = (HttpServletResponse) servletResponse;
//		
//		//-------------------------- and at the buttom too
//		//infact wait im not sure where it shoudl be put, where does the 404 generate
//		//add a check if its a 404 but not from me
//		//add a check on wether or not he has acces to the link first if not send mo to message fih u broke ass aint got root acces
//		
//		final String URN = request.getRequestURI().replaceFirst(request.getContextPath(), "");
//		if (!URN.startsWith("/resources")) {
//			
//			request.setAttribute("URN", URN);
//			
//			if (!URN.startsWith("/home") && !URN.equals("/login")) {
//				
//				HttpSession session = request.getSession();
//				final Object lock = session.getId().intern();
//				synchronized (lock) {
//					User user = (User) session.getAttribute("user");
//					
//					if (user == null) {
//						response.sendRedirect(request.getContextPath() +"/login");
//						return;
//					}
//					
//					Role role = UserManager.getRole(user);
//					
//					if (role == null) {
//						session.removeAttribute("user");
//						session.invalidate();
//						ServletManager.setError(500, "This account is not yet completed. Please try again later.", request, response);
//						return;
//					}
//					
//					if (PermissionManager.canNotAccess(user, role, URN)) {
//						ServletManager.setError(403, "You don't have access rights to this content.", request, response);
//						return;
//					}
//					
//					request.setAttribute("menuLinks", PermissionManager.getMenuLinks(user, role, URN));
//					request.setAttribute("role", role.toString());
//					
//					chain.doFilter(request, response);		
//					return;
//				}
//			}
//
//		}
//		
//		chain.doFilter(request, response);
//	}
//	
//	public void destroy() {
//		filterConfig = null;
//	}
//
//}
