package jeext.util;

import java.sql.Blob;
import java.util.Base64;

import jeext.controller.core.param.types.FileType;

/**
 * <p>A class that provides some useful methods 
 * for encoding data in general.
 * <p>All methods tolerate <code>null</code> by design.
 */
public class Encoders {

	/**
	 * @return the encoded version of the <code>file</code>s' content
	 * in base 64
	 * or <code>null</code> if its
	 * or its content are <code>null</code>
	 */
	public static String inBase64 (FileType file) {
		if (file == null) {
			return null;
		}
		
		return inBase64(file.getContent());
	}
	
	/**
	 * @return the encoded version of the <code>blob</code>s' data
	 * in base 64
	 * or <code>null</code> if its <code>null</code> or
	 * if something went wrong while trying to retrieve its data
	 */
	public static String inBase64 (Blob blob) {
		if (blob == null) {
			return null;
		}
		
        try {
        	byte[] data = blob.getBytes(1, (int) blob.length());
            return inBase64(data);
        } catch (Exception e) {
            return null;
        }
    }
	
	/**
	 * @return the encoded version of the <code>data</code>
	 * in base 64 or <code>null</code> if its <code>null</code>
	 */
	public static String inBase64 (byte [] data) {
		if (data == null) {
			return null;
		}
		
		return Base64.getEncoder().encodeToString(data);
	}
	
}
