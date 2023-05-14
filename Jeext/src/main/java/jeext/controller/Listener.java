package jeext.controller;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import jeext.dao.Factory;

/**
 * <p>The {@link WebListener} of the application, it
 * calls upon the essential {@link Controller#load(jakarta.servlet.ServletContext)}
 * when the application starts-up
 * <p>Add more listeners as needed
 */
@WebListener
public class Listener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent event) {
		Controller.load(event.getServletContext());
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		Factory.closeFactory();
	}
	
}
