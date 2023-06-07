package jeext.controller.core.param.validators;

import jeext.controller.core.param.types.FileType;
import jeext.controller.core.param.validators.annotations.Max;

/**
 * The implementation of the {@link Max} {@link Validator}
 * for {@link FileType}
 */
public class MaxFileValidator implements Validator {

	public static MaxFileValidator GET (long value, boolean strict) {
		return new MaxFileValidator (value, strict);
	}
	
	private long value;
	private boolean strict;
	
	private MaxFileValidator (long value, boolean strict) {
		this.value = value;
		this.strict = strict;
	}

	@Override
	public boolean validate (Object fileType) {
		if (fileType == null) {
			return true;
		}
		
		long objectValue = ((FileType) fileType).getLength();
		return (strict)? objectValue < value : objectValue <= value;
	}
	
}
