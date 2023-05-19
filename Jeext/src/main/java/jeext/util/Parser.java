package jeext.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

/**
 * <p>A class that facilitates parsing strings to different types.
 * <p>All methods tolerate <code>null</code> by design.
 */
public class Parser {
	
	/**
	 * <p>Parses the given {@link String} {@code s} as the
	 * given {@code type}
	 * <p>The allowed types are those whose
	 * parse method exists in this class
	 * 
	 * @return the {@link String} {@code s} parsed to the specified
	 * {@code type}, or <code>null</code> if {@code s} is <code>null</code>
	 * or cannot be parsed to the {@code type}
	 * 
	 * @throws NullPointerException	if {@code type} is <code>null</code>
	 * @throws IllegalArgumentException if the {@code type} is not supported
	 */
	@SuppressWarnings("unchecked")
	public static <T> T parse (String s, Class <T> type) {
		Objects.requireNonNull(type);
		
		if (s == null) {
			return null;
		}
		
		Object parsed;
		if (String.class.equals(type)) {
			parsed = s;
			
		} else if (Boolean.class.equals(type)) {
			parsed = parseBool(s);
			
		} else if (Integer.class.equals(type)) {
			parsed = parseInt(s);
			
		} else if (Float.class.equals(type)) {
			parsed = parseFloat(s);
			
		} else if (Double.class.equals(type)) {
			parsed = parseDouble(s);
			
		} else if (Long.class.equals(type)) {
			parsed = parseLong(s);
			
		} else if (LocalDate.class.equals(type)) {
			parsed = parseDate(s);
			
		} else if (Character.class.equals(type)) {
			parsed = parseChar(s);
			
		} else if (Short.class.equals(type)) {
			parsed = parseShort(s);
			
		} else if (Byte.class.equals(type)) {
			parsed = parseByte(s);
			
		} else if (LocalTime.class.equals(type)) {
			parsed = parseTime(s);
			
		} else if (LocalDateTime.class.equals(type)) {
			parsed = parseDateTime(s);
			
		} else {
			throw new IllegalArgumentException("Unsupported type `" +type +"`");
		}
		
		return (T) parsed;
	}

	/**
	 * @return an {@link Integer} representation of
	 * the given {@link String}, or <code>null</code>
	 * if the the given {@link String} is <code>null</code>
	 * or cannot be represented as an {@link Integer}
	 */
	public static Integer parseInt (String s) {
		if (s == null) {
			return null;
		}
		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException exception) {
			return null;
		}
	}
	
	/**
	 * <p>Parses the given {@link String} as a {@link Boolean}
	 * <p>It considers 'true', '1', 'yes' and 'y' as <code>true</code> and 
	 * 'false', '0', 'no' and 'n' as <code>false</code>, case insensitive
	 * @return a {@link Boolean} representation of the
	 * given {@link String}, or <code>null</code> if the given {@link String}
	 * is <code>null</code> or cannot be represented as a {@link Boolean}
	 */
	public static Boolean parseBool (String s) {
		if (s == null) {
			return null;
		}
		s = s.toLowerCase();
		if (s.equals("true") || s.equals("1") || s.equals("yes") || s.equals("y")) {
			return Boolean.TRUE;
		} else if (s.equals("false") || s.equals("0") || s.equals("no") || s.equals("n")) {
			return Boolean.FALSE;
		} else {
			return null;
		}
	}
	
	/**
	 * @return a {@link Float} representation of the given 
	 * {@link String}, or <code>null</code> if the given {@link String}
	 * is <code>null</code> or cannot be represented as a {@link Float}
	 */
	public static Float parseFloat (String s) {
		if (s == null) {
			return null;
		}
		try {
			return Float.parseFloat(s);
		} catch (NumberFormatException exception) {
			return null;
		}
	}
	
	/**
	 * @return a {@link Double} representation of the
	 * given {@link String}, or <code>null</code> if the given
	 * {@link String} is <code>null</code> or cannot be represented
	 * as a {@link Double}
	 */
	public static Double parseDouble (String s) {
		if (s == null) {
			return null;
		}
		try {
			return Double.parseDouble(s);
		} catch (NumberFormatException exception) {
			return null;
		}
	}
	
	/**
	 * @return a {@link Long} representation of the given
	 * {@link String}, or <code>null</code> if the given {@link String}
	 * is <code>null</code> or cannot be represented as a {@link Long}
	 */
	public static Long parseLong(String s) {
		if (s == null) {
			return null;
		}
		try {
			return Long.parseLong(s);
		} catch (NumberFormatException exception) {
			return null;
		}
	}
	
	/**
	 * @return a {@link Short} representation of the given
	 * {@link String}, or <code>null</code> if the given {@link String} is
	 * <code>null</code> or cannot be represented as a {@link Short}
	 */
	public static Short parseShort(String s) {
		if (s == null) {
			return null;
		}
		try {
			return Short.parseShort(s);
		} catch (NumberFormatException exception) {
			return null;
		}
	}
	
	/**
	 * @return a {@link Byte} representation of the given {@link String},
	 * or <code>null</code> if the given {@link String} is <code>null</code>
	 * or cannot be represented as a {@link Byte}
	 */
	public static Byte parseByte(String s) {
		if (s == null) {
			return null;
		}
		try {
			return Byte.parseByte(s);
		} catch (NumberFormatException exception) {
			return null;
		}
	}
	
	/**
	 * @return the character in the
	 * given {@link String} as a {@link Character} if the given
	 * {@link String} only contains a single character, otherwise
	 * <code>null</code> if the given {@link String} is <code>null</code> 
	 * or contains no characters or more than 1 character
	 */
	public static Character parseChar(String s) {
		return (s == null)? null : (s.length() == 1)? s.charAt(0) : null;
	}
	
	/**
	 * <p>Parses the given {@link String} as
	 * a {@link LocalDate}
	 * <p>The default {@link DateTimeFormatter} format is
	 * compatible with the HTML values
	 * @return a {@link LocalDate} representation of the given {@link String},
	 * or <code>null</code> if the given {@link String} is <code>null</code>
	 * or cannot be represented as a {@link LocalDate}
	 */
	public static LocalDate parseDate (String s) {
		if (s == null) {
			return null;
		}
		try {
			return LocalDate.parse(s);
		} catch (DateTimeParseException  e) {
			return null;
		}
	}
	
	/**
	 * <p>Parses the given {@link String} as
	 * a {@link LocalTime}
	 * <p>The default {@link DateTimeFormatter} format is
	 * compatible with the HTML values
	 * @return a {@link LocalTime} representation of the given {@link String},
	 * or <code>null</code> if the given {@link String} is <code>null</code>
	 * or cannot be represented as a {@link LocalTime}
	 */
	public static LocalTime parseTime (String s) {
		if (s == null) {
			return null;
		}
		try {
			return LocalTime.parse(s);
		} catch (DateTimeParseException  e) {
			return null;
		}
	}
	
	/**
	 * <p>Parses the given {@link String} as
	 * a {@link LocalDateTime}
	 * <p>The default {@link DateTimeFormatter} format is
	 * compatible with the HTML values
	 * @return a {@link LocalTime} representation of the given {@link String},
	 * or <code>null</code> if the given {@link String} is <code>null</code>
	 * or cannot be represented as a {@link LocalTime}
	 */
	public static LocalDateTime parseDateTime (String s) {
		if (s == null) {
			return null;
		}
		try {
			return LocalDateTime.parse(s);
		} catch (DateTimeParseException  e) {
			return null;
		}
	}
	
}
