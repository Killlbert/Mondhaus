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
package at.htlWien5.mondhaus.factory;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import at.htlWien5.mondhaus.factory.Procedure.SQLProcedureException;

/**
 * Servlet implementation class Invoke
 */
@WebServlet("/List")
public class List extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public List() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Connection conn = null;
		Procedure p = null;
		
		PrintWriter writer = response.getWriter();
		writer.println("<html>");
		writer.println("<head><title>List</title></head>");
		writer.println("<body>");
		
		
        String parmNameView = "view";
        String view = request.getParameter(parmNameView);
		writer.println(" param "+parmNameView+"="+view);

        String parmNameType = "type";
        String type = request.getParameter(parmNameType);
		writer.println(" param "+parmNameType+":"+type);

        String parmNameParams = "params";
        String params1= request.getParameter(parmNameParams);
        String[] params = params1.length()>0?request.getParameter(parmNameParams).split(","):new String[0];
		writer.println(" param "+parmNameParams+":"+params+"{");
		for(String s : params){
			writer.println(s+", ");
		}
		writer.println("}");
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException ex) {
			System.out.println("Class not found:" + ex.getLocalizedMessage());
		}

		try {
			conn = DriverManager
					.getConnection("jdbc:mysql://localhost/mondhaus?"
							+ "user=mondhausadmin&password=mond€pass");
			p = new Procedure(conn, "mondhaus");
			
			Class[] methodParameterClasses={
				String.class,
				int.class,
				Object[].class 
			};
			Method procedureInvoke = Procedure.class.getMethod ("invokeView", methodParameterClasses);
			Object[] methodParameters={
				view,
				Integer.parseInt(type),
				params
			};
			
			ResultSet resSet= (ResultSet)procedureInvoke.invoke(p, methodParameters);

			ResultSetMetaData resSetMeta= resSet.getMetaData();
			int cols= resSetMeta.getColumnCount();
			writer.println("<table>");
			writer.println("<tr>");
			for(int c=1; c<=cols; c++){
				//resSetMeta.getColumnDisplaySize(c);
				writer.println("<th>");
				writer.println(resSetMeta.getColumnLabel(c));
				writer.println("</th>");
			}
			writer.println("</tr>");
			
			while(resSet.next()){
				writer.println("<tr>");
				for(int c=1; c<=cols; c++){
					writer.println("<td>");
					writer.println(resSet.getString(c));
					writer.println("</td>");
				}
				writer.println("</tr>");
			}
			writer.println("</table>");

		   
		} catch (SQLException e) {
		    System.out.println("SQLException: " + e.getMessage());
		    System.out.println("SQLState: " + e.getSQLState());
		    System.out.println("VendorError: " + e.getErrorCode());
			e.printStackTrace();
		} catch (SQLProcedureException e) {
		    System.out.println("SQLProcedureException: " + e.getMessage());
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			System.out.println("NoSuchMethodException:" + e.getMessage());
			e.printStackTrace();
		} catch (SecurityException e) {
			System.out.println("SecurityException:" + e.getMessage());
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			System.out.println("IllegalAccessException:" + e.getMessage());
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			System.out.println("IllegalArgumentException:" + e.getMessage());
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			System.out.println("InvocationTargetException:" + e.getMessage());
			e.printStackTrace();
		}
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
