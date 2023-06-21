package jeext.controller.core.param.types;

import java.io.IOException;
import java.io.InputStream;

import jakarta.servlet.http.Part;

public class FileType {
	
	private byte [] content;
	private String submittedName;
	
	/**
	 * 
	 * @param part
	 * @throws IOException	If an error occurs in retrieving the content as an InputStream
	 */
	public FileType (Part part) throws IOException {
		InputStream inputStream = part.getInputStream();
		this.content = inputStream.readAllBytes();
		this.submittedName = part.getSubmittedFileName();
	}
	
	public long getLength () {
		return content.length;
	}
	
}
