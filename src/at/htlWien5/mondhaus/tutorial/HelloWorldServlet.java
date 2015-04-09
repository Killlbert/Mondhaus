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
 * Servlet implementation class HelloWorldServlet
 */
@WebServlet("/HelloWorldServlet")
public class HelloWorldServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public HelloWorldServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		PrintWriter writer = response.getWriter();
		writer.println("<html>");
		writer.println("<head><title>Hello World Servlet</title></head>");
		writer.println("<body>");
		writer.println(" <h1>Hello World from a Sevlet!</h1>");
		
		writer.println("<body>");
		writer.println("</html>");
		writer.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
