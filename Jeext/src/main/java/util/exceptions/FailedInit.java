package util.exceptions;

public class FailedInit extends FailedRequirement {
	private static final long serialVersionUID = 1L;
	
	public FailedInit (Class <?> type, String reason) {
		super ("Can't initialize: " +type +"\n\tReason: " +reason);
	}

}
