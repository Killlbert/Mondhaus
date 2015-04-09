/*
 *  The code is distributed on an "AS IS" basis.
 *  The code is developed for education purposes
 *  as a text-example
 *  and not to compile for production environments.
 *  The author is not responsible for any harm or damage.
 *  
 *  License: GNU LESSER GENERAL PUBLIC LICENSE Version 3, 29 June 2007
 *  http://www.gnu.org/licenses/lgpl-3.0.de.html
 *  Copyright (C) 2015 Michael Baumgartner.
 * */

package at.htlWien5.mondhaus.tutorial;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class FormTutor
 */
@WebServlet("/FormTutor")
public class FormTutor extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FormTutor() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	      // Set the response message's MIME type
	      response.setContentType("text/html;charset=UTF-8");
	      // Allocate a output writer to write the response message into the network socket
	      PrintWriter writer = response.getWriter();
	 
	      // Write the response message, in an HTML page
	      try {
	    	  writer.println("<!doctype html>"); 
	    	  writer.println("<html>");
	    	  writer.println("<head>");
	    	  writer.println("<meta charset='utf-8'>");
	    	  writer.println("<title>Formular Beispiel</title>");
	    	  writer.println("</head>");
	    	  writer.println("<body>"); 
	    	  //writer.println("<form action='senden.html'>"); 
	    	  writer.println("<form action='/mondhaus/FormTutor'>"); 
	    	  writer.println("<ul>");
	    	  writer.println("<li>"); 
	    	  writer.println("<label for='name'>Benutzername:</label>");
	    	  writer.println("<input type='text' name='name' value=''>");
	    	  writer.println("</li>");
	    	  writer.println("<li>");
	    	  writer.println("<label for='pass'>Kennwort:</label>");
	    	  writer.println("<input type='password' name='pass' value=''>"); 
	    	  writer.println("</li>"); 
	    	  writer.println("<li>"); 
	    	  writer.println("<input type='submit' value='Einloggen'>"); 
	    	  writer.println("</li>"); 
	    	  writer.println("</ul>"); 
	    	  writer.println("</form>"); 
	    	  writer.println("</body>"); 
	    	  writer.println("</html>");
	    	  
	         boolean haveParameter = false;
	         String name = request.getParameter("name");
	         if (name != null && (name = name.trim()).length() != 0) {
	            writer.println("Name;");
	            writer.println(" = " + name + "<br />");
	            haveParameter = true;
	         }
	 
	         String pass = request.getParameter("pass");
	         if (pass != null && (pass = pass.trim()).length() != 0) {
	            writer.println("pass");
	            writer.println(" = " + pass);
	            haveParameter = true;
	         }
	 
	         if (!haveParameter) {
	            writer.println("Keine Parameter verfügbar");
	         }
	      } finally {
	         writer.close();  // Always close the output writer
	      }
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
