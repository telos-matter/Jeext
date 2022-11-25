package util;

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

	public static Character toUpperCase (Character c) {
		return (c == null) ? null : Character.toUpperCase(c);
	}
	
	public static Character toLowerCase (Character c) {
		return (c == null) ? null : Character.toLowerCase(c);
	}
	
	
	public static String trim (String s) {
		return (s == null) ? null : s.trim();
	}
	
}
