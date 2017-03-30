package com.daffodil.documentumie.fileutil.metadata.apiimpl;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import com.daffodil.documentumie.fileutil.logger.IELogger;
import com.daffodil.documentumie.fileutil.metadata.api.MetadataReader;
import com.daffodil.documentumie.fileutil.metadata.exception.DMetadataReadException;

public class ExcelMetadataReader implements MetadataReader {

	private Connection con = null;

	private ResultSet rs = null;

	private Statement stmt = null;

	private ResultSetMetaData metadata = null;

	int noofColumn;

	String fileName;

	String sheetName;

	public ExcelMetadataReader(String path, String sheet) {
		this.fileName = path;
		this.sheetName = sheet;
	}

	private void initConnection() throws DMetadataReadException {
		
		if(System.getProperty("os.name").contains("Window"))
		{
			try {
				Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
				String loc = "jdbc:odbc:Driver={MicroSoft Excel Driver (*.xls)};ReadOnly=0;DBQ="
						+ fileName;
				System.out.println("Filename:-"+fileName);
				con = DriverManager.getConnection(loc);
			} catch (SQLException e) {
				System.out.println(e.getMessage());
				throw new DMetadataReadException(
						"Exception while inilizing connection " + e.getMessage()
						+ e.getCause());

			} catch (ClassNotFoundException e) {
				throw new DMetadataReadException(
						"Exception while inilizing connection " + e.getMessage()
						+ e.getCause());

			}
		}
		else{

		}
	}

	public HashMap getAttributes() throws DMetadataReadException {
		if(System.getProperty("os.name").contains("Window"))
		{
		initConnection();
		String sql = "Select top 1 * from [" + sheetName + "$]";
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			metadata = rs.getMetaData();
			noofColumn = metadata.getColumnCount();
			HashMap attributesMap = new HashMap();
			if (rs.next()) {
				for (int i = 1; i <= noofColumn; i++) {
					attributesMap.put(metadata.getColumnName(i), metadata.getColumnTypeName(i));
				System.out.println("*************");			
				}
			}
			closeConnection();
			return attributesMap;

		} catch (SQLException e) {
			throw new DMetadataReadException(
					"Exception while getting attributes " + e.getMessage()
							+ e.getCause());

		}
		}
		else{
			HashMap attributesMap = new HashMap();
			attributesMap = getMetadataHeader(fileName, sheetName);
			return  attributesMap;
		}

	}

	public List getRowCount(List attrName, String whereClause)
			throws DMetadataReadException {
		initConnection();

		String sql = preparesSQl(attrName, whereClause);
		System.out.println("SQL for getting row count--->"+sql);
		// String sql = "Select * from ["+sheetName+"$]";
		List metadatRows = new ArrayList();

		try {
			stmt = con.createStatement();

			rs = stmt.executeQuery(sql);

			metadata = rs.getMetaData();
			noofColumn = metadata.getColumnCount();

			while (rs.next()) {
				HashMap map = new HashMap();
				for (int i = 1; i <= noofColumn; i++) {
					String field = metadata.getColumnName(i);
					map.put(field, rs.getString(i));
				}
				metadatRows.add(map);
			}
			closeConnection();

			return metadatRows;
		} catch (SQLException e) {
			throw new DMetadataReadException("Exception while getting rows "
					+ e.getMessage() + e.getCause());
		}
	}

	public List getRows(List attrName, String whereClause)
			throws DMetadataReadException {
		if(System.getProperty("os.name").contains("Window"))
		{
			initConnection();

			String sql = preparesSQl(attrName, whereClause);
			System.out.println("SQL---->"+sql);
			// String sql = "Select * from ["+sheetName+"$]";
			List metadatRows = new ArrayList();

			try {
				stmt = con.createStatement();
				rs = stmt.executeQuery(sql);
				 System.out.println("rs-->"+rs);
				metadata = rs.getMetaData();
				noofColumn = metadata.getColumnCount();
          System.out.println("no of columns-->"+noofColumn);
				while (rs.next()) {
					HashMap map = new HashMap();
					for (int i = 1; i <= noofColumn; i++) {
						String field = metadata.getColumnName(i);
						System.out.println("field-->"+field);
						map.put(field, rs.getString(i));
					}
					System.out.println("map=--------------->>>"+map);
					metadatRows.add(map);
				}
				closeConnection();

				return metadatRows;
			} catch (SQLException e) {
				System.out.println( e.getMessage() +"<-ERROR->"+ e.getCause());
				throw new DMetadataReadException("Exception while getting rows "
						+ e.getMessage() + e.getCause());
			}
		}else{
			
			List metadatRows = getMetadataRows(fileName, sheetName);
			return metadatRows;
		}
	}

	private String preparesSQl(List attrName, String whereClause) {
		StringBuffer sql = null;
		sql = new StringBuffer();
		sql.append("select ");
		for (int i = 0; i < attrName.size(); i++) {
			sql.append(attrName.get(i));
			if (i < attrName.size() - 1) {
				sql.append(",");
			}
			sql.append(" ");
		}
		sql.append(" from ");
		sql.append("[" + sheetName + "$]");
		if (whereClause != null && !"".equalsIgnoreCase(whereClause)) {
			sql.append(" where " + whereClause);
		}
		String sql1 = sql.toString();
		return sql1;

	}

	public int getRowsCount() throws DMetadataReadException {
		int noofrows = 0;
		initConnection();
		String sql = "Select count(*) from [" + sheetName + "$]";

		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			if (rs.next()) {
				noofrows = rs.getInt(1);
			}
			closeConnection();
			return noofrows;
		} catch (SQLException e) {
			throw new DMetadataReadException(
					"Exception while getting row count" + e.getMessage()
							+ e.getCause());

		}

	}

	private void closeConnection() throws DMetadataReadException {
		if (rs != null) {
			try {
				rs.close();
				rs = null;
			} catch (SQLException e) {
				throw new DMetadataReadException(
						"Exception while closing result set " + e.getMessage()
								+ e.getCause());
			}
		}
		if (stmt != null) {
			try {
				stmt.close();
				stmt = null;
			} catch (SQLException e) {
				throw new DMetadataReadException(
						"Exception while closing statement" + e.getMessage()
								+ e.getCause());
			}
		}
		if (con != null) {
			try {
				con.close();
				con = null;
			} catch (SQLException e) {
				throw new DMetadataReadException(
						"Exception while closing connection " + e.getMessage()
								+ e.getCause());
			}
		}
	}
	public HashMap getMetadataHeader(String fileName,String sheetName) throws DMetadataReadException
	{
		File inputWorkbook = new File(fileName);
		Workbook workbook;
		HashMap attributesMap = new HashMap();
		try {
			workbook = Workbook.getWorkbook(inputWorkbook);
			Sheet sheet = workbook.getSheet(sheetName);
			for (int j = 0; j < sheet.getRows(); j++) 
			{
				for (int i = 0; i < sheet.getColumns(); i++) 
				{
					Cell cell = sheet.getCell(i, j);
					attributesMap.put(cell.getContents(), i);
						//System.out.print("["+cell.getContents()+","+cell.getType()+"]");
				}
				break;
			}
			workbook.close();
		} catch (BiffException e) {
			throw new DMetadataReadException(e.getMessage()+ e.getCause());					
		}
		catch(IOException e)
		{
			throw new DMetadataReadException(e.getMessage()+ e.getCause());					
		}
		return attributesMap;
	}
	
	public List getMetadataRows(String fileName,String sheetName)throws DMetadataReadException
	{
		File inputWorkbook = new File(fileName);
		Workbook w;
		int noofColumn;
		
		HashMap colNameMap = getMetadataHeader(fileName, sheetName);
		List colList = getMetadataList(colNameMap);
		noofColumn = colNameMap.size();
		List metadatRows = new ArrayList();
		try {
			w = Workbook.getWorkbook(inputWorkbook);
			Sheet sheet = w.getSheet(sheetName);
			int numRows = sheet.getRows();
			//System.out.println("numRows"+numRows);
			for (int j = 1; j < numRows; j++) {
				HashMap attributesMap = new HashMap();
				for (int i = 0; i < noofColumn; i++) 
				{
					attributesMap.put(sheet.getCell(i, 0).getContents(), sheet.getCell(i, j).getContents());
				}
				metadatRows.add(attributesMap);
			}
			
		}catch (BiffException e) {
			throw new DMetadataReadException(e.getMessage()+ e.getCause());					
		}
		catch(IOException e)
		{
			throw new DMetadataReadException(e.getMessage()+ e.getCause());					
		}
		return metadatRows;
	}
	public List getMetadataList(HashMap colNameMap)
	{
		List list = new ArrayList();
		for (Iterator iterator = colNameMap.entrySet().iterator(); iterator.hasNext();) {
			Map.Entry entery = (Map.Entry) iterator.next();
			String key = entery.getKey().toString();
			list.add(key);
		}
		return list;
	}
}
