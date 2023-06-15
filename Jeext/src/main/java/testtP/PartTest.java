package testtP;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;

public class PartTest {

	public static void testPart (HttpServletRequest request) {
		System.out.println("In testPart");
		System.out.println("Params:");
		for (var entry : request.getParameterMap().entrySet()) {
			System.out.println("\t"+entry);
		}
		
		try {
			if (request.getContentType().startsWith("multipart")) {
				System.out.println("It is multipart");
				
				Collection<Part> parts = request.getParts();
				System.out.println("Parts: " +parts);
				
				if (parts != null) {
					System.out.println("Parts size: " +parts.size());
					for (Part part : parts) {
						System.out.println("----------------");
						System.out.println("Part: " +part);
						System.out.println("Name: " +part.getName());
						System.out.println("Submitted file name: " +part.getSubmittedFileName());
						System.out.println("content type " +part.getContentType());
						System.out.println("Size " +part.getSize());
						System.out.println("Headers: " +part.getHeaderNames());
						for (String name : part.getHeaderNames()) {
							System.out.println("\t-> " +part.getHeader(name));
						}
						System.out.println("Stream: `" +toStr(part.getInputStream()) +"`");
					}
				}
			} else {
				System.out.println("No multipart");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static String toStr (InputStream stream) {
		String s = "";
		try {
			for (byte b : stream.readAllBytes()) {
				s += b;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return s;
	}
	
}
