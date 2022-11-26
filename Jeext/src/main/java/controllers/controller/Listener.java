package controllers.controller;

import dao.Factory;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

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
