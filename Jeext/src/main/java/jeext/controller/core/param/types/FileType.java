package jeext.controller.core.param.types;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;

import javax.sql.rowset.serial.SerialBlob;

import jakarta.servlet.http.Part;
import jeext.controller.util.exceptions.UnhandledException;

public class FileType {
	
	private final byte [] content;
	private final String submittedName;
	
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
	
	public byte [] getContent () {
		return this.content;
	}

	public String getSubmittedName() {
		return submittedName;
	}
	
	public Blob asBlob () {
		try {
			return new SerialBlob(content);
		} catch (SQLException e) {
			throw new UnhandledException(e);
		}
	}
	
}
