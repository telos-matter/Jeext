package util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Strings {

	public static boolean equals (String s1, String s2) {
		return (s1 == null || s2 == null) ? false : s1.equals(s2);
	}
	
	public static boolean equalsIgnoreCase (String s1, String s2) {
		return (s1 == null || s2 == null) ? false : s1.equalsIgnoreCase(s2);
	}
	
	public static String forceCapitalize (String s) {
		if (s == null) {
			return null;
		}
		return (s.isEmpty()) ? s : Character.toUpperCase(s.charAt(0)) + s.substring(1).toLowerCase();
	}
	
	public static String capitalize (String s) {
		if (s == null) {
			return null;
		}
		return (s.isEmpty()) ? s : Character.toUpperCase(s.charAt(0)) + s.substring(1);
	}
	
	public static String toUpperCase (String s) {
		return (s == null) ? null : s.toUpperCase();
	}
	
	public static String toLowerCase (String s) {
		return (s == null) ? null : s.toLowerCase();
	}
	
	public static String trim (String s) {
		return (s == null) ? null : s.trim();
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
	
	public static Date parseDate (String s) {
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
