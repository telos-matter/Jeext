package util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
	
	public static Date parseDate (String s) { // TODO: make sure this is all good
		if (s == null) {
			return null;
		}
		try {
			return new SimpleDateFormat(Dates.DATE_FORMAT).parse(s);
		} catch (ParseException e) {
			return null;
		}
	}
	
}
