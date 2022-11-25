package util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import controllers.controller.exceptions.UnsupportedType;

public class Parser {

	public static  <T extends Number> T cast (Double value, Class <T> type) {
		if (value == null) {
			return null;
		} else if (Integer.class.equals(type)) {
			return (T) (Integer) value.intValue();
			
		} else if (Float.class.equals(type)) {
			return Parser.parseFloat(parameter);
			
		} else if (Double.class.equals(type)) {
			return Parser.parseDouble(parameter);
			
		} else if (Long.class.equals(type)) {
			return Parser.parseLong(parameter);
			
		} else if (Date.class.equals(type)) {
			return Parser.parseDate(parameter);
			
		} else if (Character.class.equals(type)) {
			return Parser.parseChar(parameter);
			
		} else if (Short.class.equals(type)) {
			return Parser.parseShort(parameter);
			
		} else if (Byte.class.equals(type)) {
			return Parser.parseByte(parameter);
			
		} else {
			throw new UnsupportedType(type);
		}
		return null;
	}
	
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
