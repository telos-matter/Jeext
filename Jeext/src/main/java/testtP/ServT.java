package testtP;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

@MultipartConfig (
		  fileSizeThreshold = 1024 * 1024 * 1,
		  maxFileSize = 1024 * 1024 * 10,
		  maxRequestSize = 1024 * 1024 * 50
		)
@WebServlet("/t")
public class ServT extends HttpServlet{
// TODO delete on comiti comity
	private static void m (HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		System.out.println("In method");
		System.out.println("params:");
		for (var entry : request.getParameterMap().entrySet()) {
			System.out.println("\t"+entry);
		}
		Part p = request.getPart("file"); // first one if they have same name
		System.out.println("special p:" +p);
		System.out.println(p.getSize());
		System.out.println("name " +p.getSubmittedFileName());
		System.out.println("stream? " +toStr(p.getInputStream()));
		
		// is a file always contained within a single part?
		//a single file is always within a single part
		// params are also in their own parts
		if (request.getContentType().startsWith("multipart")) {
			Collection<Part> parts = request.getParts();
			System.out.println("Parts: " +parts);
			if (parts != null) {
				System.out.println("size: " +parts.size());
				for (Part part : parts) {
					System.out.println("--------------");
					System.out.println("single part name: " +part.getName());
					System.out.println("single part: " +part);
					System.out.println("sub file name: " +part.getSubmittedFileName());
					System.out.println("content type " +part.getContentType());
					System.out.println("size " +part.getSize());
					System.out.println("headers" +part.getHeaderNames());
					for (String name : part.getHeaderNames()) {
						System.out.println("\t->" +part.getHeader(name));
					}
					System.out.println("stream: " +toStr(part.getInputStream()));
				}
			}
		} else {
			System.out.println("No multipart");
		}
	}
	
	private static String toStr (InputStream stream) {
		String s = "";
		try {
			for (byte b : stream.readAllBytes()) {
				s += b;
//				s += "" +Integer.toHexString(b & 0xFF);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return s;
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("GET");
		m(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("POST");
		m(req, resp);
	}
	
}
