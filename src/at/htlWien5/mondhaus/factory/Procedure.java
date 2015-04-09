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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class Procedure {

	private static final boolean DEBUG = false;

	private static final int UNDEF = -1;
	private static final int HAVEALL = 0;
	public static final int ADD = 1;
	public static final int GETITEM = 2;
	public static final int GETITEMS = 3;
	public static final int SETAT = 4;
	public static final int REMOVE = 5;

	private Connection conn = null;
	private String schema = null;
	private CallableStatement sqlCallStmt = null;
	private List<String> procedures = new ArrayList<String>();
	private List<String> views = new ArrayList<String>();
	private List<String> viewsPrefix = new ArrayList<String>();
	private List<boolean[]> viewsProcedureType = new ArrayList<boolean[]>();
	private List<String[]> viewsProcedureKeyColumns = new ArrayList<String[]>();
	private List<String[]> viewsProcedureColumns = new ArrayList<String[]>();

	public Procedure(java.sql.Connection conn, String schema)
			throws SQLException, SQLProcedureException {
		setConnection(conn, schema);
	}

	private void setConnection(Connection conn, String schema)
			throws SQLException, SQLProcedureException {
		this.conn = conn;
		this.schema = schema;
		DatabaseMetaData dbMetaData = conn.getMetaData();
		ResultSet resSet = dbMetaData.getProcedures(conn.getCatalog(),
				this.schema, "%");
		while (resSet.next()) {
			procedures.add(resSet.getString(3)); // procedure name
		}
		resSet.close();
		String[] procParts = null;
		String procPrefix = null;
		String procPostfix = null;
		int viewIndex = -1;
		int psLength = procedures.size();
		for (int psI = 0; psI < psLength; psI++) {
			procParts = procedures.get(psI).split("_");
			if (procParts.length == 3 && procParts[1].equals("f")) { // more
																		// than
																		// prefix
																		// f
																		// postfix
				procPrefix = procParts[0];
				procPostfix = procParts[2];
				if (procPostfix.length() > 3 && procPostfix.startsWith("add")) {
					procPostfix = procPostfix
							.substring(3, procPostfix.length());
					viewIndex = getViewIndex(procPrefix, procPostfix);
					if (viewsPrefix.get(viewIndex).equals(procPrefix)) {
						viewsProcedureType.get(viewIndex)[ADD] = true;
					}
				} else if (procPostfix.length() > 5
						&& procPostfix.startsWith("set")
						&& procPostfix.endsWith("At")) {
					procPostfix = procPostfix.substring(3,
							procPostfix.length() - 2);
					viewIndex = getViewIndex(procPrefix, procPostfix);
					if (viewsPrefix.get(viewIndex).equals(procPrefix)) {
						viewsProcedureType.get(viewIndex)[SETAT] = true;
					}
				} else if (procPostfix.length() > 8
						&& procPostfix.startsWith("getItems")) {
					procPostfix = procPostfix
							.substring(8, procPostfix.length());
					viewIndex = getViewIndex(procPrefix, procPostfix);
					if (viewsPrefix.get(viewIndex).equals(procPrefix)) {
						viewsProcedureType.get(viewIndex)[GETITEMS] = true;
					}
				} else if (procPostfix.length() > 7
						&& procPostfix.startsWith("getItem")) {
					procPostfix = procPostfix
							.substring(7, procPostfix.length());
					viewIndex = getViewIndex(procPrefix, procPostfix);
					if (viewsPrefix.get(viewIndex).equals(procPrefix)) {
						viewsProcedureType.get(viewIndex)[GETITEM] = true;
					}
				} else if (procPostfix.length() > 6
						&& procPostfix.startsWith("remove")) {
					procPostfix = procPostfix
							.substring(6, procPostfix.length());
					viewIndex = getViewIndex(procPrefix, procPostfix);
					if (viewsPrefix.get(viewIndex).equals(procPrefix)) {
						viewsProcedureType.get(viewIndex)[REMOVE] = true;
					}
				} else {
					procPostfix = null;
					viewsProcedureType
							.get(getViewIndex(procPrefix, procPostfix))[UNDEF] = true;
				}
			}
			// if(proc.indexOf('_', 0) && proc.lastIndexOf('_') && );
		}

		int viewsSize = views.size();
		if (viewsSize != viewsProcedureType.size()) {
			throw new SQLProcedureException(
					"SQLProcedureException: Procedures views[" + views.size()
							+ "] " + "missmatch viewsProcedureType["
							+ viewsProcedureType.size() + "]");
		}

		List<String> colNames = null;

		for (int vI = 0; vI < viewsSize; vI++) {
			boolean[] types = viewsProcedureType.get(vI);
			if (types[ADD] && types[GETITEM] && types[GETITEMS] && types[SETAT]
					&& types[REMOVE]) {
				types[HAVEALL] = true;
				// get views keys
				resSet = dbMetaData.getProcedureColumns(conn.getCatalog(),
						schema, getProcedureName(views.get(vI), GETITEM), "%");
				colNames = new ArrayList<String>();
				while (resSet.next()) {
					colNames.add(resSet.getString(4));
				}
				viewsProcedureKeyColumns.add(vI, colNames.toArray(new String[0]));
				// get views columns
				resSet = dbMetaData.getProcedureColumns(conn.getCatalog(),
						schema, getProcedureName(views.get(vI), ADD), "%");
				colNames = new ArrayList<String>();
				while (resSet.next()) {
					colNames.add(resSet.getString(4));
				}
				viewsProcedureColumns.add(vI, colNames.toArray(new String[0]));

				System.out.println("Procedure.java: view:" + views.get(vI)
						+ " have all Procedures.");
			} else {
				System.out.println("Procedure.java: view:" + views.get(vI)
						+ " miss Procedure: " + ((types[ADD]) ? "" : "add%, ")
						+ ((types[GETITEM]) ? "" : "getItem%, ")
						+ ((types[GETITEMS]) ? "" : "getItems%, ")
						+ ((types[SETAT]) ? "" : "set%At, ")
						+ ((types[REMOVE]) ? "" : "remove%"));
				this.views.remove(vI);
				this.viewsPrefix.remove(vI);
				this.viewsProcedureType.remove(vI);
				viewsSize--;
			}
		}
	}

	public String[] getColumnNames(String view) 
		throws SQLProcedureException
	{
		return viewsProcedureColumns.get(getViewIndex(view));
	}

	public String[] getKeyColumnNames(String view)
		throws SQLProcedureException
	{
		return viewsProcedureKeyColumns.get(getViewIndex(view));
	}

	public boolean isColumnKey(String view, String column)
		throws SQLProcedureException
	{
		return Arrays.asList(getKeyColumnNames(view)).contains(column);
	}

	private int getViewIndex(String view) 
		throws SQLProcedureException
	{
		int iView = views.indexOf(view);
		if (iView < 0) {
			throw new SQLProcedureException("SQLProcedureException: view:'"
					+ view + "' ist nicht verfügbar.");
		}
		return iView;
	}

	private int getViewIndex(String prefix, String view) {
		int indexOfView = -1;
		if ((indexOfView = this.views.indexOf(view)) < 0) {
			this.views.add(view);
			indexOfView = this.views.indexOf(view);
			this.viewsPrefix.add(indexOfView, prefix);
			boolean[] types = new boolean[6];
			for (boolean t : types) {
				t = false;
			}
			this.viewsProcedureType.add(indexOfView, types);

		}
		return indexOfView;
	}

	public String[] getNames() {
		return (String[]) procedures.toArray();
	}

	private String getProcedureName(String view, int type)
			throws SQLProcedureException {
		String procedure = null;
		int iView = getViewIndex(view);
		switch (type) {
		case ADD:
			procedure = viewsPrefix.get(iView) + "_f_add" + view;
			break;
		case GETITEM:
			procedure = viewsPrefix.get(iView) + "_f_getItem" + view;
			break;
		case GETITEMS:
			procedure = viewsPrefix.get(iView) + "_f_getItems" + view;
			break;
		case SETAT:
			procedure = viewsPrefix.get(iView) + "_f_set" + view + "At";
			break;
		case REMOVE:
			procedure = viewsPrefix.get(iView) + "_f_remove" + view;
			break;
		default:
			throw new SQLProcedureException(
					"SQLProcedureException: Procedure Type:" + type
							+ " ist unbekannt.");
		}
		return procedure;
	}

	public ResultSet invokeView(String view, int type, Object... args)
			throws SQLException, SQLProcedureException {
		return invoke(getProcedureName(view, type), args);
	}

	/*
	 * The actual implementation is not prepared to execute PROCEDURES with more
	 * than one signature per name.
	 */
	public ResultSet invoke(String procedure, Object... args)
			throws SQLException, SQLProcedureException {
		List columnNames = new ArrayList();
		List columnSQLTypes = new ArrayList();
		List columnSQLTypeNames = new ArrayList();
		List columnJavaClasses = new ArrayList();
		DatabaseMetaData dbMetaData = conn.getMetaData();
		ResultSet rs = dbMetaData.getProcedures(conn.getCatalog(), this.schema,
				procedure);
		int rows = 0;
		// try if there is a record for the Procedure
		while (rs.next()) {
			rows++;
		}

		if (rs != null) {
			rs.close();
		}

		if (rows == 0) {
			throw new SQLProcedureException("SQLProcedureException: Procedure "
					+ procedure + " is not defined in schema " + this.schema);
		} else if (rows > 1) {
			throw new SQLProcedureException(
					"SQLProcedureException: Procedure "
							+ procedure
							+ " in schema "
							+ this.schema
							+ " has more than one signature defined, this is actually not supported.");
		}

		rs = dbMetaData.getProcedureColumns(conn.getCatalog(), this.schema,
				procedure, "%");

		// /Object[] argsClone = args.clone();
		String sqlArgsPrepare = "";
		rows = 0;
		while (rs.next()) {
			rows++;
			sqlArgsPrepare = sqlArgsPrepare + "?,";

			String columnName = rs.getString(4);
			columnNames.add(rs.getString(4));

			int columnDataType = rs.getInt(6);
			columnSQLTypes.add(rs.getInt(6));
			columnJavaClasses.add(rows < args.length ? args[rows - 1]
					.getClass() : null);

			String columnReturnTypeName = rs.getString(7);
			columnSQLTypeNames.add(rs.getString(7));

		}

		if (rs != null) {
			rs.close();
		}

		if (rows != args.length) {
			throw new SQLProcedureException(
					"SQLProcedureException: SQL PROCEDURE "
							+ procedure
							+ "("
							+ rows
							+ ") missmatch in length of parameter to Java invoke args["
							+ args.length + "] !");
		}

		if (rows > 0) {
			sqlArgsPrepare = sqlArgsPrepare.substring(0,
					sqlArgsPrepare.length() - 1);
		}
		String sqlCallPrepare = "{call " + this.schema + "." + procedure + " ("
				+ sqlArgsPrepare + ")}";

		if (DEBUG) {
			System.out.println("callPrepare:" + sqlCallPrepare);
		}
		sqlCallStmt = conn.prepareCall(sqlCallPrepare);

		if (DEBUG) {
			System.out.println("callProcedure:" + procedure);
		}

		for (int i = 0; i < args.length; i++) {
			sqlCallStmt.setObject(i + 1, args[i]);
		}

		return sqlCallStmt.executeQuery();
	}

	public class SQLProcedureException extends Exception {
		SQLProcedureException(String message) {
			super(message);
		}
	}

	private static void printResultSet(ResultSet resSet) throws SQLException {
		System.out.println("printResultSet()");
		ResultSetMetaData resSetMeta = resSet.getMetaData();
		int cols = resSetMeta.getColumnCount();
		System.out.println("<table>");
		System.out.print("<tr>");
		for (int c = 1; c <= cols; c++) {
			// resSetMeta.getColumnDisplaySize(c);
			System.out.print("<th>");
			System.out.print(resSetMeta.getColumnLabel(c));
			System.out.print("</th>");
		}
		System.out.println("</tr>");

		while (resSet.next()) {
			System.out.print("<tr>");
			for (int c = 1; c <= cols; c++) {
				System.out.print("<td>");
				System.out.print(resSet.getString(c));
				System.out.print("</td>");
			}
			System.out.println("</tr>");
		}
		System.out.println("</table>");
	}

	public static void main(String[] args) {
		Procedure procMondhaus = null;

		try {
			Connection conn = DriverManager
					.getConnection("jdbc:mysql://localhost/mondhaus?"
							+ "user=mondhausadmin&password=mond€pass");
			procMondhaus = new Procedure(conn, "mondhaus");

			procMondhaus.invoke("k_f_addKunden",
			// parameters:
					1000001, // p_pnr INT,
					"NachnameK", // p_nachname VARCHAR(20),
					"VornameK", // p_vorname VARCHAR(15),
					1000001, // p_a_anr INT,
					1, // p_geschlecht TINYINT(1) columns of k_kunden
					Date.valueOf("2015-03-29"), // k_seit DATE,
					1, // k_status TINYINT(1),
					1000001 // k_p_pnr INT
					);

			procMondhaus.invokeView("Kunden", ADD,
			// parameters:
					1000002, // p_pnr INT,
					"NachnameK", // p_nachname VARCHAR(20),
					"VornameK", // p_vorname VARCHAR(15),
					1000002, // p_a_anr INT,
					1, // p_geschlecht TINYINT(1) columns of k_kunden
					Date.valueOf("2015-03-29"), // k_seit DATE,
					1, // k_status TINYINT(1),
					1000002 // k_p_pnr INT
					);

			ResultSet resSet = procMondhaus.invokeView("Kunden", GETITEM,
			// parameters:
					1000002 // p_pnr INT,
					);
			printResultSet(resSet);
			if (resSet != null) {
				resSet.close();
			}

			// -----------------------------
			Class[] methodParameterClasses = { String.class, int.class,
					Object[].class };

			Method procedureInvoke = Procedure.class.getMethod("invokeView",
					methodParameterClasses);

			Object[] methodParameters = { "Kunden", Procedure.GETITEM,
					new Object[] { 1000002 } };

			resSet = (ResultSet) procedureInvoke.invoke(procMondhaus,
					methodParameters);

			printResultSet(resSet);

			if (resSet != null) {
				resSet.close();
			}

			// -----------------------------

			procMondhaus.invokeView("Kunden", REMOVE,
			// parameters:
					1000001 // p_pnr INT,
					);

			procMondhaus.invokeView("Kunden", REMOVE,
			// parameters:
					1000002 // p_pnr INT,
					);

		} catch (SQLException e) {
			// here we can handle some errors
			// but in this case we only print some messages
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
			System.out.println("Stack Trace:");
			e.printStackTrace(System.out);
		} catch (SQLProcedureException e) {
			// here we can handle some errors
			// but in this case we only print some messages
			System.out.println("ProcedureError: " + e.getMessage());
		} catch (NoSuchMethodException e) {
			System.out.println("NoSuchMethodException: " + e.getMessage());
			e.printStackTrace();
		} catch (SecurityException e) {
			System.out.println("SecurityException: " + e.getMessage());
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			System.out.println("IllegalAccessException: " + e.getMessage());
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			System.out.println("IllegalArgumentException: " + e.getMessage());
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			System.out.println("InvocationTargetException: " + e.getMessage());
			e.printStackTrace();
		}

	}
}
