package jeext.controller;

import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jeext.controller.core.Access;
import jeext.controller.core.HTTPMethod;
import jeext.controller.core.annotations.WebMapping;
import jeext.controller.core.mapping.Mapping;
import jeext.controller.core.param.validators.Validator;
import models.User;
import models.permission.Permission;

import java.io.IOException;
import java.util.Map;

import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.MultipartConfigElement;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebFilter;

/**
 * <p>Main essential {@link HttpFilter} of {@link jeext}, user may define
 * additional {@link HttpFilter} as needed
 * <p>And of course may change anything as long as they know what they
 * are doing
 * <p>Do read {@link #doFilter(ServletRequest, ServletResponse, FilterChain)}
 * 
 *  @see Controller
 *  @see #doFilter(ServletRequest, ServletResponse, FilterChain)
 */
@WebFilter({"/*"})
public class Filter extends HttpFilter {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Unused init method
	 */
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {}
	
	/**
	 * <p>The filter makes a distinction between requests made to
	 * resources (such as images, css pages..) or {@link HttpServlet} and requests
	 * made to {@link WebMapping}s
	 * <p>Requests made to resources, whose files should
	 * be in the `res` folder under the root directory
	 * (i.e. the `webapp` folder, sometimes also called
	 * the `webcontent` folder)(To not be confused with
	 * the `res` resource-folder that contains
	 * the persistence.xml file)(This is also
	 * the reason why no mapping's URL should start
	 * with `/res`), are <b>directly</b> served
	 * to the requester and require/do/have no check-up
	 * on {@link Access}, {@link Permission}s or the
	 * authenticity of the requester, and so, the `res`
	 * folder should only contain unclassified files,
	 * as that any one can access them.
	 * <p>The same thing happens to request made to {@link HttpServlet}s,
	 * they are directly forwarded to the corresponding
	 * {@link HttpServlet} and pass trough no check-ups.
	 * <p>Requests made to the {@link WebMapping}s on the
	 * other hand (which will be simply
	 * referred to as requests
	 * from now on, unless of course mentioned otherwise)
	 * pass trough check-ups, validating
	 * the authenticity of the {@link User} trough
	 * the {@link HttpSession}, making sure he
	 * has the {@link Access} rights and
	 * the required {@link Permission}s. They are
	 * then sent/delegated to the {@link Controller} to
	 * call the appropriate {@link Mapping}
	 * <hr>
	 * <p>Requests (also those made to the {@link HttpServlet}s)
	 * receive two attributes; `path` which contains
	 * the URL or path that was requested minus the contextPath
	 * meaning only the part of the URL that is defined
	 * in the different {@link WebMapping}s. And `contextPath`
	 * which contains the contextPath of the application, it
	 * is added as a shorthand for the usual
	 * "${pageContext.request.contextPath}" used in the JSPs
	 * <hr>
	 * <p>Requests are {@link HttpSession} locked/synchronized,
	 * meaning a single user (with a single browser) cannot
	 * make more than one request at a time, and only when the first
	 * one is served, is the second one served. If this behavior
	 * is unwanted simply remove the lock/synchronized statement
	 * <hr>
	 * <p>The filter automatically sends the appropriate error
	 * codes as follows:
	 * <ul>
	 * <li>If a request is made to a none existing {@link WebMapping}
	 * or {@link HttpServlet} then
	 * a {@link HttpServletResponse#SC_NOT_FOUND} error is sent
	 * <li>If a request is made to an existing {@link WebMapping}
	 * but with the wrong {@link HTTPMethod}
	 * a {@link HttpServletResponse#SC_METHOD_NOT_ALLOWED} error
	 * is sent
	 * <li>If the {@link User} does not validate the specified
	 * {@link Access} right, or is <code>null</code> but some
	 * {@link Permission}s are required then a {@link HttpServletResponse#SC_UNAUTHORIZED}
	 * error is sent
	 * <li>Lastly, here, if the requested {@link WebMapping} requires
	 * certain {@link Permission} but the {@link User} (who is not <code>null</code>)
	 * does not satisfy them then a {@link HttpServletResponse#SC_FORBIDDEN} error
	 * is sent
	 * </ul>
	 * <p>The {@link Controller} can also send a {@link HttpServletResponse#SC_BAD_REQUEST}
	 * error if the sent parameters do not validate the different {@link Validator}s
	 * specified by the requested {@link Mapping}
	 * <hr>
	 * <p>The way requests are sent/delegated to the {@link Controller} {@link HttpServlet}
	 * is that the filter appends the requested {@link WebMapping} URL (after the
	 * previously mentioned check-ups) to the end of `/controller` which is 
	 * the {@link Controller}'s URL (`/controllers/*`) and forwards it, this is done
	 * to keep/allow the underlying resources {@link Servlet} capable of handling
	 * requests made to it. This is also the reason why no {@link WebMapping} should
	 * have `/controller` as a URL (although it would technically work,
	 * it is made as such just to avoid any confusion)
	 * <hr>
	 * 
	 * @see Controller
	 * @see Controller#invokeMapping(Map, HttpServletRequest, HttpServletResponse)
	 */
	
	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;
		
		final String path = request.getRequestURI().replaceFirst(request.getContextPath(), "");
		
		if (path.startsWith("/res")) {
			chain.doFilter(request, response);
			return;

		} else {
			request.setAttribute("path", path);
			request.setAttribute("contextPath", request.getContextPath());
			
			HttpSession session = request.getSession();
			synchronized (session.getId().intern()) {
				
				if (Controller.servletExists(path)) {
					chain.doFilter(request, response);
					return;
				}

				// TODO check for classcastexception
				User user = (User) session.getAttribute("user");
				
				Mapping mapping;
				HTTPMethod method = HTTPMethod.valueOf(request.getMethod());
				
				if (Controller.mappingExists(path)) {
					mapping = Controller.getMapping(path, method);
					
				} else {
					response.sendError(HttpServletResponse.SC_NOT_FOUND);
					return;
				}
				
				if (mapping == null) {
					response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
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
				
				System.out.println("\n\n\n\nFilter");
				
				request.setAttribute("mapping", mapping);
				request.getRequestDispatcher(Controller.PATH).forward(request, response);
				return;
			}
			
		}
	}
	
	// TODO check if there are any left comments
	/**
	 * Unused destroy method
	 */
	@Override
	public void destroy() {}

}
