package controllers.controller.core.util;

import java.lang.annotation.Annotation;

import util.exceptions.UnsupportedType;

/**
 *  The {@link Boolean} class represented
 *  as an {@link Enum} in order to be used
 *  in {@link Annotation}s, since {@link Annotation}s
 *  don't allow for objects
 */
public enum BooleanEnum {
	
	/**
	 * Representation of {@link Boolean#TRUE}
	 */
	TRUE,
	/**
	 * Representation of {@link Boolean#FALSE}
	 */
	FALSE,
	/**
	 * Representation of <code>null</code>
	 */
	NULL;
	
	public Boolean getBoolean() {
		switch (this) {
		case TRUE: return Boolean.TRUE;
		case FALSE: return Boolean.FALSE;
		case NULL: return null;
		
		default: throw new UnsupportedType(this); // Java being Java
		}
	}
	
}
