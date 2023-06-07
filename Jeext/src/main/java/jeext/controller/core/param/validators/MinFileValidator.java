package jeext.controller.core.param.validators;

import jeext.controller.core.param.types.FileType;
import jeext.controller.core.param.validators.annotations.Min;

/**
 * The implementation of the {@link Min} {@link Validator}
 * for {@link FileType}
 */
public class MinFileValidator implements Validator {

	public static MinFileValidator GET (long value, boolean strict) {
		return new MinFileValidator (value, strict);
	}
	
	private long value;
	private boolean strict;
	
	private MinFileValidator (long value, boolean strict) {
		this.value = value;
		this.strict = strict;
	}

	@Override
	public boolean validate (Object fileType) {
		if (fileType == null) {
			return true;
		}
		
		long objectValue = ((FileType) fileType).getLength();
		return (strict)? objectValue > value : objectValue >= value;
	}
	
}
