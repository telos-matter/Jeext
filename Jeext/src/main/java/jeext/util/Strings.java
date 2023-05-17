package jeext.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * <p>A class that facilitates treating everything {@link String}.
 * <p>All methods tolerate <code>null</code> by design.
 */
public class Strings {

	/**
	 * @return <code>true</code> if s1 and s2 are equal,
	 * <code>false</code> if they are not and <code>null</code> if
	 * either one of them is <code>null</code>.
	 */
	public static Boolean equals (String s1, String s2) {
		return (s1 == null || s2 == null) ? null : s1.equals(s2);
	}
	
	/**
	 * Negates {@link #equals(String, String)} but
	 * preserves <code>null</code>
	 */
	public static Boolean notEquals (String s1, String s2) {
		Boolean result = equals(s1, s2);
		return (result == null) ? null : !result;
	}

	/**
	 * @return <code>true</code> if s1 and s2 are equal with the case ignored,
	 * <code>false</code> if they are not and <code>null</code> if
	 * either one of them is <code>null</code>.
	 */
	public static Boolean equalsIgnoreCase (String s1, String s2) {
		return (s1 == null || s2 == null) ? null : s1.equalsIgnoreCase(s2);
	}

	/**
	 * Negates {@link #equalsIgnoreCase(String, String)} but
	 * preserves <code>null</code>
	 */
	public static Boolean notEqualsIgnoreCase (String s1, String s2) {
		Boolean result = equalsIgnoreCase(s1, s2);
		return (result == null) ? null : !result;
	}
	
	/**
	 * Same functionality as {@link String#isEmpty()}, but returns
	 * <code>null</code> of the given {@link String} is <code>null</code>
	 */
	public static Boolean isEmpty (String s) {
		return (s == null) ? null : s.isEmpty();
	}
	
	/**
	 * Negates {@link #isEmpty(String)} but
	 * preserves <code>null</code>
	 */
	public static Boolean isNotEmpty (String s) {
		Boolean result = isEmpty(s);
		return (result == null) ? null : !result;
	}
	
	/**
	 * Same functionality as {@link String#isBlank()}, but returns
	 * <code>null</code> if the given {@link String} is <code>null</code>
	 */
	public static Boolean isBlank (String s) {
		return (s == null) ? null : s.isBlank();
	}
	
	/**
	 * Negates {@link #isBlank(String)} but
	 * preserves <code>null</code>
	 */
	public static Boolean isNotBlank (String s) {
		Boolean result = isBlank(s);
		return (result == null) ? null : !result;
	}
	
	/**
	 * @return <code>true</code> if the length of the given
	 * {@link String} is equal to length, <code>false</code>
	 * if not, and <code>null</code> if the given {@link String} is <code>null</code>
	 */
	public static Boolean length (String s, int length) {
		if (s == null) {
			return null;
		}
		return s.length() == length;
	}
	
	/**
	 * Negates {@link #length(String, int)} but
	 * preserves <code>null</code>
	 */
	public static Boolean notLength (String s, int length) {
	    Boolean result = length(s, length);
	    return (result == null) ? null : !result;
	}
	
	/**
	 * @return <code>true</code> if the given {@link String}s' length
	 * is less than or equal to max and greater than or equal to min,
	 * <code>false</code> if not, and <code>null</code> if the given
	 * {@link String} is <code>null</code>
	 */
	public static Boolean lengthWithin (String s, int min, int max) {
		if (s == null) {
			return null;
		}
		return s.length() >= min && s.length() <= max;
	}

	/**
	 * Negates {@link #lengthWithin(String, int, int)} but
	 * preserves <code>null</code>
	 */
	public static Boolean lengthNotWithin (String s, int min, int max) {
	    Boolean result = lengthWithin(s, min, max);
	    return (result == null) ? null : !result;
	}
	
	/**
	 * @return <code>true</code> if the given {@link String}s' length
	 * is greater than or equal to min, <code>false</code> if not, and <code>null</code>
	 * if the given {@link String} is <code>null</code>
	 */
	public static Boolean lengthGreater (String s, int min) {
	    if (s == null) {
	        return null;
	    }
	    return s.length() >= min;
	}
	
	/**
	 * @return <code>true</code> if the given {@link String}s' length
	 * is less than or equal to max, <code>false</code> if not, and <code>null</code>
	 * if the given {@link String} is <code>null</code>
	 */
	public static Boolean lengthLess (String s, int max) {
	    if (s == null) {
	        return null;
	    }
	    return s.length() <= max;
	}
	
	/**
	 * @return a {@link String} version of the given {@link String}
	 * where only the first character is in uppercase, or <code>null</code>
	 * if the given {@link String} is <code>null</code>
	 */
	public static String forceCapitalize (String s) {
		if (s == null) {
			return null;
		}
		return (s.isEmpty()) ? s : Character.toUpperCase(s.charAt(0)) + s.substring(1).toLowerCase();
	}
	
	/**
	 * @return a capitalized {@link String} version of the
	 * given {@link String}, or <code>null</code> if the given {@link String}
	 * is <code>null</code>
	 */
	public static String capitalize (String s) {
		if (s == null) {
			return null;
		}
		return (s.isEmpty()) ? s : Character.toUpperCase(s.charAt(0)) + s.substring(1);
	}
	
	/**
	 * Same functionality as {@link String#toUpperCase()}, but returns 
	 * <code>null</code> if the given {@link String} is <code>null</code>
	 */
	public static String toUpperCase (String s) {
		return (s == null) ? null : s.toUpperCase();
	}
	
	/**
	 * Same functionality as {@link String#toLowerCase()}, but returns 
	 * <code>null</code> if the given {@link String} is <code>null</code>
	 */
	public static String toLowerCase (String s) {
		return (s == null) ? null : s.toLowerCase();
	}

	/**
	 * Same functionality as {@link Character#toUpperCase(char)}, but returns 
	 * <code>null</code> if the given {@link Character} is <code>null</code>
	 */
	public static Character toUpperCase (Character c) {
		return (c == null) ? null : Character.toUpperCase(c);
	}
	
	/**
	 * Same functionality as {@link Character#toLowerCase(char)}, but returns 
	 * <code>null</code> if the given {@link Character} is <code>null</code>
	 */
	public static Character toLowerCase (Character c) {
		return (c == null) ? null : Character.toLowerCase(c);
	}
	
	/**
	 * Same functionality as {@link String#trim()}, but returns 
	 * <code>null</code> if the given {@link String} is <code>null</code>
	 */
	public static String trim (String s) {
		return (s == null) ? null : s.trim();
	}
	
	/**
	 * @return a hashed {@link String} version
	 * of the given {@link String} with the given algorithm, or
	 * <code>null</code> if the given {@link String} is <code>null</code>
	 * @throws NullPointerException if the given algorithm is <code>null</code>
	 * @throws NoSuchAlgorithmException wrapped inside a {@link RuntimeException}
	 * if the specified algorithm isn't available
	 */
	public static String hash (String s, String algorithm) {
		Objects.requireNonNull(algorithm);
		
		if (s == null) {
			return null;
		}
		
		try {
			MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
			messageDigest.update(s.getBytes());
			
			byte digestedBytes [] = messageDigest.digest();
			
			StringBuffer digestedString = new StringBuffer(); // TODO: personal / better way?
			for (int i = 0; i < digestedBytes.length; i++) {
				digestedString.append(Integer.toString((digestedBytes[i] & 0xff) + 0x100, 16).substring(1));
			}
			
			return digestedString.toString();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Calls upon {@link #hash(String, String)} with 
	 * the MD5 algorithm specified
	 * @see #hash(String, String)
	 */
	public static String hash (String s) {
		return hash(s, "MD5");
	}

	/**
	 * Test if the given input {@link String} matches
	 * the given regex pattern with the specified flags. Flags
	 * can be <code>null</code> to indicate no flags.
	 * @return <code>true</code> if the given input {@link String}
	 * matches the given regex pattern with the specified flags, <code>false</code> if
	 * not, and <code>null</code> if the given input {@link String} is
	 * <code>null</code>
	 * @throws NullPointerException if the given regex {@link String} is <code>null</code>
	 * @throws IllegalArgumentException if unknown flags are set
	 * @throws PatternSyntaxException if the given regex {@link String} syntax is invalid
	 * @see Pattern
	 */
	public static Boolean matches (String regex, String input, Integer flags) {
		Objects.requireNonNull(regex);
		
		if (input == null) {
			return null;
		}

		if (flags == null) {
			return Pattern.compile(regex).matcher(input).find();				
		} else {
			return Pattern.compile(regex, flags).matcher(input).find();				
		}
		
	}
	
	/**
	 * Negates {@link #matches(String, String, Integer)} but
	 * preserves <code>null</code>
	 */
	public static Boolean notMatches (String regex, String input, Integer flags) {
	    Boolean result = matches(regex, input, flags);
	    return (result == null) ? null : !result;
	}
	
}
