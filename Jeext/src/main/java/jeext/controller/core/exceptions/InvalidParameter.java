package jeext.controller.core.exceptions;

import jeext.controller.core.param.Param;

// TODO document
// Not runtimeexcpetion intentionally
public class InvalidParameter extends Exception {
	private static final long serialVersionUID = 1L;

	public final Param param;
	public final String reason;
	
	public InvalidParameter(Param param, String reason) {
		this.param = param;
		this.reason = reason;
	}
	
	public InvalidParameter (String reason) {
		this(null, reason);
	}
	
	public InvalidParameter () {
		this (null, null);
	}

}
