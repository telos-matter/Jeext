package jeext.controller.core.param.consumers;
// MENTION not using the fact that it is a functionainterface
// all consumer only consume if if null
@FunctionalInterface
public interface Consumer {

	public Object consume (Object object);
	
}
