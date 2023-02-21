package util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

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
	 * @return <code>true</code> if s1 and s2 are equal with the case ignored,
	 * <code>false</code> if they are not and <code>null</code> if
	 * either one of them is <code>null</code>.
	 */
	public static Boolean equalsIgnoreCase (String s1, String s2) {
		return (s1 == null || s2 == null) ? null : s1.equalsIgnoreCase(s2);
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
	 * Same functionality as {@link String}.toUpperCase method, but returns 
	 * <code>null</code> if the given {@link String} is <code>null</code>
	 */
	public static String toUpperCase (String s) {
		return (s == null) ? null : s.toUpperCase();
	}
	
	/**
	 * Same functionality as {@link String}.toLowerCase method, but returns 
	 * <code>null</code> if the given {@link String} is <code>null</code>
	 */
	public static String toLowerCase (String s) {
		return (s == null) ? null : s.toLowerCase();
	}

	/**
	 * Same functionality as {@link Character}.toUpperCase method, but returns 
	 * <code>null</code> if the given {@link Character} is <code>null</code>
	 */
	public static Character toUpperCase (Character c) {
		return (c == null) ? null : Character.toUpperCase(c);
	}
	
	/**
	 * Same functionality as {@link Character}.toLowerCase method, but returns 
	 * <code>null</code> if the given {@link Character} is <code>null</code>
	 */
	public static Character toLowerCase (Character c) {
		return (c == null) ? null : Character.toLowerCase(c);
	}
	
	/**
	 * Same functionality as {@link String}.trim method, but returns 
	 * <code>null</code> if the given {@link String} is <code>null</code>
	 */
	public static String trim (String s) {
		return (s == null) ? null : s.trim();
	}
	
	/**
	 * @return a hashed {@link String} version
	 * of the given {@link String} with the given algorithm, or
	 * <code>null</code> if the given {@link String} is <code>null</code>
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
	
}
