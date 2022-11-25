package util;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class Parser {

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
	
	public static Boolean parseBool (String s) {
		if (s == null) {
			return null;
		}
		s = s.toLowerCase();
		if (s.equals("true") || s.equals("1") || s.equals("yes") || s.equals("y")) {
			return true;
		} else if (s.equals("false") || s.equals("0") || s.equals("no") || s.equals("n")) {
			return false;
		} else {
			return null;
		}
	}
	
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
	
	public static Character parseChar(String s) {
		return (s == null)? null : (s.length() == 1)? s.charAt(0) : null;
	}
	
	public static LocalDate parseDate (String s) {
		if (s == null) {
			return null;
		}
		try {
			return LocalDate.parse(s, Dates.DATE_FORMATER);
		} catch (DateTimeParseException  e) {
			return null;
		}
	}
}
