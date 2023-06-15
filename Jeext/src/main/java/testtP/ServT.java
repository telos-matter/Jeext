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
		System.out.println("In m");
		PartTest.testPart(request);
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
