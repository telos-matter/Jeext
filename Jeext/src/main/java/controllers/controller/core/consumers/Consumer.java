package controllers.controller.core.consumers;

@FunctionalInterface
public interface Consumer {

	public Object consume (Object object);
	
}
