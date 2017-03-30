package com.daffodil.documentumie.fileutil.metadata.apiimpl;

import java.io.File;
import java.io.IOException;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import com.daffodil.documentumie.fileutil.logger.IELogger;
import com.daffodil.documentumie.fileutil.metadata.api.MetadataWriter;
import com.daffodil.documentumie.fileutil.metadata.exception.DMetadataReadException;
import com.daffodil.documentumie.fileutil.metadata.exception.DMetadataWriteException;

public class ExcelMetadataWriter implements MetadataWriter {

	String fileName;

	String sheetName;

	private Connection con = null;

	private Statement stmtWriter = null;

	private WritableSheet worksheet = null;
	private WritableWorkbook workbook = null;
	private int rowCounter = 1;
	private List headerList = new ArrayList();

	public ExcelMetadataWriter(String filename, String sheet,
			WritableSheet worksheet, WritableWorkbook workbook) {
		this.fileName = filename;
		this.sheetName = sheet;
		this.worksheet = worksheet;
		this.workbook = workbook;
	}

	public ExcelMetadataWriter(String filename, String sheet) {
		this.fileName = filename;
		this.sheetName = sheet;
	}

	private void initConnection() throws DMetadataWriteException {
		if (System.getProperty("os.name").contains("Window")) {
			try {
				Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
				String loc = "jdbc:odbc:Driver={MicroSoft Excel Driver (*.xls)};ReadOnly=0;DBQ="
						+ fileName;
				con = DriverManager.getConnection(loc);
			} catch (SQLException e) {
				throw new DMetadataWriteException(
						"Exception while inilizing connection "
								+ e.getMessage() + e.getCause());

			} catch (ClassNotFoundException e) {
				throw new DMetadataWriteException(
						"Exception while inilizing connection "
								+ e.getMessage() + e.getCause());

			}
		} else {

		}
	}

	public void wirteAttributes(List attributes) throws DMetadataWriteException {
		if (System.getProperty("os.name").contains("Window")) {
			initConnection();
			StringBuffer sql = new StringBuffer();
			sql.append("create table [" + sheetName + "$] (");
			for (int i = 0; i < attributes.size(); i++) {
				sql.append(attributes.get(i) + " varchar");
				if (i < attributes.size() - 1) {
					sql.append(", ");
				}
			}
			sql.append(")");
			String sqlTest = sql.toString();
			sqlTest = sqlTest.replaceAll("'", "''");
			System.out.println("sql in writing attr : " + sqlTest);
			try {
				stmtWriter = con.createStatement();
				stmtWriter.execute(sqlTest);
			} catch (SQLException e) {
				throw new DMetadataWriteException(
						"Exception while creating sheet " + e.getMessage()
								+ e.getCause());
			}
			closeConnection();
		} else {
			writeAttributesLinux(attributes);
		}
	}

	//
	// public void writeRow(String[] row) throws DMetadataWriteException {
	// initConnection();
	// String sql;
	// sql = "update ";// write a query to create heade or fields.
	// try {
	//
	// stmtWriter = con.createStatement();
	// int i = stmtWriter.executeUpdate(sql);
	// } catch (SQLException e) {
	// throw new DMetadataWriteException("exception in getrows method "
	// + e.getMessage() + e.getCause());
	// }
	// }

	private String prepareSQl(Map map) {
		System.out.println("map in sql : " + map);
		StringBuffer sql;
		int size = 1;
		sql = new StringBuffer();
		sql.append("insert into [" + sheetName + "$] (");
		for (Iterator iterator = map.entrySet().iterator(); iterator.hasNext();) {
			Map.Entry entry = (Map.Entry) iterator.next();
			String key = entry.getKey().toString();

			sql.append(key);

			if (size < map.size()) {
				sql.append(", ");
				size++;
			}
		}
		sql.append(") values (");
		size = 1;
		for (Iterator iterator = map.entrySet().iterator(); iterator.hasNext();) {
			Map.Entry entry = (Map.Entry) iterator.next();
			String key = entry.getKey().toString();
			String value = (String) map.get(key);
			if (value != null) {
				value = value.trim();
			}
			/*
			 * if(value.contains()){
			 * 
			 * }
			 */
			if (value != null) {
				value = value.replaceAll("'", "''");
			}
			sql.append("'" + value + "'");
			if (size < map.size()) {
				sql.append(", ");
				size++;
			}
		}
		sql.append(")");
		return sql.toString();
	}

	public void writeRow(Map map) throws DMetadataWriteException {
		if (System.getProperty("os.name").contains("Window")) {
			initConnection();
			try {
				stmtWriter = con.createStatement();
				String sql = prepareSQl(map);
				System.out.println("sql : " + sql);
				int i = stmtWriter.executeUpdate(sql);
				closeConnection();
			} catch (SQLException e) {
				e.printStackTrace();
				System.out.println(e.getCause() + e.getMessage());
				throw new DMetadataWriteException(
						"Exception while writing row " + e.getMessage()
								+ e.getCause());
			}

			closeConnection();
		} else {
			writeRowLinux(map);
		}
	}

	/*
	 * public void writeTestRow(String sql) throws DMetadataWriteException {
	 * initConnection(); try { stmtWriter = con.createStatement();
	 * System.out.println("Called"); int i = stmtWriter.executeUpdate(sql);
	 * closeConnection(); } catch (SQLException e) { e.printStackTrace();
	 * System.out.println(e.getCause()+e.getMessage()); throw new
	 * DMetadataWriteException( "Exception while writing row " + e.getMessage()
	 * + e.getCause()); } catch(Exception e) {
	 * System.out.println(e.getCause()+e.getMessage()); } closeConnection(); }
	 */

	private void closeConnection() throws DMetadataWriteException {

		if (stmtWriter != null) {
			try {
				stmtWriter.close();
				stmtWriter = null;
			} catch (SQLException e) {
				throw new DMetadataWriteException(
						"Exception while closing statement" + e.getMessage()
								+ e.getCause());
			}
		}
		if (con != null) {
			try {
				con.close();
				con = null;
			} catch (SQLException e) {
				throw new DMetadataWriteException(
						"Exception while closing connection " + e.getMessage()
								+ e.getCause());
			}
		}
	}

	public void writeAttributesLinux(List attributes)
			throws DMetadataWriteException {
		for (int i = 0; i < attributes.size(); i++) {
			Label label = new Label(i, 0, attributes.get(i).toString());
			try {
				worksheet.addCell(label);
				headerList.add(attributes.get(i).toString());
			} catch (RowsExceededException e) {
				throw new DMetadataWriteException(
						"Exception while writting rows" + e.getMessage()
								+ e.getCause());

			} catch (WriteException e) {
				throw new DMetadataWriteException(
						"Exception while writting rows" + e.getMessage()
								+ e.getCause());
			}
		}
		/*
		 * try { workbook.write(); workbook.close(); } catch (Exception e) { //
		 * TODO Auto-generated catch block e.printStackTrace(); }
		 */
	}

	public void writeRowLinux(Map map) throws DMetadataWriteException {
		int colCount = 0;

		// List headerList1 = headerList;

		for (Iterator iterator = map.entrySet().iterator(); iterator.hasNext();) {
			Map.Entry entry = (Map.Entry) iterator.next();
			String attribNameHead = entry.getKey().toString();
			for (int p = 0; p < headerList.size(); p++) {
				if (attribNameHead.equalsIgnoreCase(headerList.get(p)
						.toString())) {
					colCount = p;
					break;
				}
			}
			Label label = null;
			if (entry.getValue() != null) {
				label = new Label(colCount, rowCounter, entry.getValue()
						.toString());
			} else {
				label = new Label(colCount, rowCounter, null);
			}
			colCount = colCount + 1;
			try {
				worksheet.addCell(label);
			} catch (RowsExceededException e) {
				throw new DMetadataWriteException(
						"Exception while writting rows" + e.getMessage()
								+ e.getCause());

			} catch (WriteException e) {
				throw new DMetadataWriteException(
						"Exception while writting rows" + e.getMessage()
								+ e.getCause());
			}

		}

		rowCounter = rowCounter + 1;
	}

	public List getMetadataHeader(String fileName, String sheetName)
			throws DMetadataReadException {
		File inputWorkbook = new File(fileName);
		Workbook workbook;
		List attributesList = new ArrayList();
		try {
			workbook = Workbook.getWorkbook(inputWorkbook);
			Sheet sheet = workbook.getSheet(sheetName);
			System.out
					.println("Number Of Header Columns in the Metadata Sheet is"
							+ sheet.getColumns());
			for (int j = 0; j < sheet.getRows(); j++) {
				for (int i = 0; i < sheet.getColumns(); i++) {
					Cell cell = sheet.getCell(i, j);
					attributesList.add(cell.getContents());
					System.out.print("[" + cell.getContents() + ","
							+ cell.getType() + "]");
				}
				break;
			}
			workbook.close();
		} catch (BiffException e) {
			throw new DMetadataReadException(e.getMessage() + e.getCause());
		} catch (IOException e) {
			throw new DMetadataReadException(e.getMessage() + e.getCause());
		}
		return attributesList;
	}
	//
	// public static void main(String[] args) {
	// String fileName=
	// "D:\\DATA\\jyoti\\SSSL- IE Utility\\Import Data\\Test File_Import_15032010_0326.xls";
	// // ExcelMetadataWriter ex=new ExcelMetadataWriter(fileName,"Sheet3");
	// // try {
	// // ex.initConnection();
	// // } catch (DMetadataWriteException e) {
	// // // TODO Auto-generated catch block
	// // e.printStackTrace();
	// // }
	// String sql
	// ="create table [Sheet3$] (r_folder_path varchar, Image_Path varchar, PAN_NO varchar,"
	// +
	// " TRANSACTION_VALUE varchar, FUND_ID varchar, MAKER_DATE varchar, AMOUNT varchar, "
	// +
	// "acl_name varchar, AMC_ID varchar, INVESTOR_NAME varchar, object_name varchar,"
	// +
	// " BRANCH_NAME varchar, BROKER_CODE varchar, FUND_NAME varchar, time_stamp varchar,"
	// +
	// " MAKER_ID varchar, In_house_Number varchar, FOLIO_NUMBER varchar, Import_Status varchar, "
	// +
	// "Error_Description varchar)";
	//
	// System.out.println("sql in writing attr : "+sql);
	// try {
	// Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
	// String loc =
	// "jdbc:odbc:Driver={MicroSoft Excel Driver (*.xls)};ReadOnly=0;DBQ="
	// + fileName;
	// Connection con = DriverManager.getConnection(loc);
	// Statement stmtWriter = con.createStatement();
	// stmtWriter.execute(sql.toString());
	// } catch (SQLException e) {
	// e.printStackTrace();
	// } catch (ClassNotFoundException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
}
