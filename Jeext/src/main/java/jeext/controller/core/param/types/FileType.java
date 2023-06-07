package jeext.controller.core.param.types;

import java.io.IOException;
import java.io.InputStream;

import jakarta.servlet.http.Part;

public class FileType {
	
	private InputStream content;
	private String submittedName;
	private long length; // bytes
	
	/**
	 * 
	 * @param part
	 * @throws IOException	If an error occurs in retrieving the content as an InputStream
	 */
	public FileType (Part part) throws IOException {
		this.content = part.getInputStream();
		this.submittedName = part.getSubmittedFileName();
		this.length = part.getSize();
	}
	
	public long getLength () {
		return length;
	}
	
}
