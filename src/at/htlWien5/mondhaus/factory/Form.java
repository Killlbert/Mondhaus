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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import at.htlWien5.mondhaus.factory.Procedure.SQLProcedureException;

/**
 * Servlet implementation class Form
 */
@WebServlet("/Form")
public class Form extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Form() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 * 
	 * call this page: ./Form?view=Kunden&type=2&param=1000002
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Connection conn = null;
		Procedure p = null;
		boolean isFormResponse=false;
        String parmNameView = "view";
        String view = null;
        String parmNameType = "type";
        String type = null;
        String parmNameParams = "params";
        String params= null;
        String[] paramsArray = null;
        String[] paramsKeyArray = null;
        ResultSet resSet= null;
        ResultSetMetaData resSetMeta= null;
		
	    response.setContentType("text/html;charset=UTF-8");
		
		PrintWriter writer = response.getWriter();
		writer.println("<html>");
		writer.println("<head><title>Form</title></head>");
		writer.println("<body>");
		
		Map<String,String[]> requestParams= request.getParameterMap();
		
		for(String requestParam: requestParams.keySet()){
			String[] requestValues= requestParams.get(requestParam);
			System.out.println("<br />"+requestParam+" {");
			for(String s : requestValues){
				System.out.print(s+",");
			}
			System.out.println("} ");
		}
		System.out.println("<br />");

		if(
			requestParams.containsKey(parmNameView)&&
			requestParams.containsKey(parmNameType)&&
			requestParams.containsKey(parmNameParams)
		){
	        view = requestParams.get(parmNameView)[0]; // request.getParameter(parmNameView);	
	        type = requestParams.get(parmNameType)[0];
	        params= requestParams.get(parmNameParams)[0];
	        isFormResponse= false;
		}else if(
			requestParams.containsKey("form_"+parmNameView)&&
			requestParams.containsKey("form_"+parmNameType)&&
			requestParams.containsKey("form_"+parmNameParams)			
		){
	        view = requestParams.get("form_"+parmNameView)[0]; // request.getParameter(parmNameView);	
	        type = requestParams.get("form_"+parmNameType)[0];
	        params= requestParams.get("form_"+parmNameParams)[0];
	        isFormResponse= true;
		}else{			
			writer.println("<h2>Request/Response with unawaited Parameters!</h2>");
			writer.println("<body>");
			writer.println("</html>");
			writer.close();
			return;
		}
		
		writer.println(" param "+parmNameView+"="+view);
		writer.println(" param "+parmNameType+":"+type);
        paramsArray = params.length()>0?params.split(","):new String[0];
		writer.println(" param "+parmNameParams+":"+paramsArray+"{");
		
		for(String s : paramsArray){
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
			
			//set the Record of the  ResultSet for Request 
			if(isFormResponse){
				String[] columnsNames= p.getColumnNames(view);
				List<String> columnValues= new ArrayList<String>();
				String[] columnValuesForAColumnName= null;
				for(String columnName : columnsNames){
					columnValuesForAColumnName=requestParams.get(columnName);
					if(columnValuesForAColumnName!=null && columnValuesForAColumnName.length>0){
						columnValues.add(columnValuesForAColumnName[0]);
					}else{
						writer.println("<h2>Response miss Parameter:"+columnName+" !</h2>");
						writer.println("<body>");
						writer.println("</html>");
						writer.close();
						return;						
					}
				}
				
				Object[] methodParametersRequset={
					view,
					Procedure.SETAT,
					columnValues.toArray()
				};
				
				resSet= (ResultSet)procedureInvoke.invoke(p, methodParametersRequset);
				resSet.close();
			}
			
			//get ResultSet for Request 
			Object[] methodParametersRequset={
				view,
				Integer.parseInt(type),
				paramsArray
			};
			
			resSet= (ResultSet)procedureInvoke.invoke(p, methodParametersRequset);
			//ResultSet resSet= p.invokeView(view, Procedure.GETITEM, keys );
			
			resSetMeta= resSet.getMetaData();
			int cols= resSetMeta.getColumnCount();
			
	        writer.println("<form method='get' action='/mondhaus/Form'>");
//	        writer.println("<form method='get'>");
//			writer.println(
//				"<form method='get' "+
//				" action='/mondhaus/Form"+
//				"?"+parmNameView+"="+view+
//				"&"+parmNameType+"="+type+
//				"&"+parmNameParams+"="+params+
//				"'"+
//				" name='form1' "+
//				">"
//			);

//			writer.println("<input type='text' name='"+parmNameView+"' value='"+view+"' ><br />");
//			writer.println("<input type='text' name='"+parmNameType+"' value='"+type+"' ><br />");
//			writer.println("<input type='text' name='"+parmNameParams+"' value='"+params+"' ><br />");

			writer.println(
				"<input type='text' "+
				" name='"+"form_"+parmNameView+"' "+
				" value='"+view+"' "+
				" >"
			);
			writer.println(
				"<input type='text' "+
				" name='"+"form_"+parmNameType+"' "+
				" value='"+type+"' "+
				" >"
			);
			writer.println(
					"<input type=\"text\" "+
					" name='"+"form_"+parmNameParams+"' "+
					" value='"+params+"' "+
					" >"
				);

			writer.println("<table>");
			if(resSet.next()){
				for(int c=1; c<=cols; c++){

					writer.println("<tr>");
					
					writer.println("<td>");
					writer.println();
					writer.println(
						"<label for='"+resSetMeta.getColumnLabel(c)+"'>"+
							resSetMeta.getColumnLabel(c)+
						":</label>"
					);
					writer.println("</td>");

					writer.println("<td>");
					writer.println(
						"<input type=\"text\" "+
						" name='"+resSetMeta.getColumnLabel(c)+"' "+
						" size='"+resSetMeta.getColumnDisplaySize(c)+"' "+
						" value='"+resSet.getString(c)+"' "+
						((p.isColumnKey(view, resSetMeta.getColumnLabel(c)))?"readonly":"")+
						" >"
					);
					writer.println("</td>");
					
					writer.println("</tr>");
				}
			}else{
				writer.println("<h1> Keine Daten für die Abfrage. <h1>");
			}
			if(resSet.next()){
				writer.println("<h1> mehr als ein Datensatz für die Abfrage! <h1>");
			}

		    //OK <input type="submit" name="Submit" value="Submit">
			writer.println("<input type='submit' value='SEND'>");
//			writer.println(
//					"<input type='submit' "+
//					" value='"+"submit"+"' "+
//					" >"
//			);
		    //Reset <input type="reset" name="Submit2" value="Reset">
			writer.println(
					"<input type='reset' "+
					" value='"+"reset"+"' "+
					" >"
			);
			writer.println("</table>");
			writer.println("</form>");
		   
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
		doGet(request, response);
	}

}
