package util;

import java.util.regex.Pattern;

public class Regexes {

	public static boolean matches (String regex, String input) {
		if (input == null) {
			return false;
		}
		
		return Pattern.compile(regex).matcher(input).find();				
	}
	
	public static boolean notMatches (String regex, String input) {
		return ! matches (regex, input);
	}
	
	public static boolean length (String input, int length) {
		if (input == null) {
			return false;
		}
		return input.length() == length;
	}
	
	public static boolean notLength (String input, int length) {
		return ! length (input, length);
	}
	
	public static boolean length (int min, int max, String input) {
		if (input == null) {
			return false;
		}
		if (max == -1) {
			return input.length() >= min;
		} else {
			return input.length() >= min && input.length() <= max;
		}
	}
	
	public static boolean length (int min, String input) {
		return length (min, -1, input);
	}
	
	public static boolean notLength (int min, int max, String input) {
		return ! length (min, max, input);
	}
	
	public static boolean notLength (int min, String input) {
		return ! length (min, input);
	}
	
	public static boolean isEmpty (String input) {
		return (input == null) ? true : input.isEmpty();
	}
	
	public static boolean isNotEmpty (String input) {
		return ! isEmpty(input);
	}

}
