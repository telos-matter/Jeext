package controllers.controller.core.param.consumers;

@FunctionalInterface
public interface Consumer {

	public Object consume (Object object);
	
}
