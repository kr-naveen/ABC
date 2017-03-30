package com.daffodil.documentumie.fileutil.metadata.apiimpl;

import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.daffodil.documentumie.fileutil.metadata.api.MetadataWriter;
import com.daffodil.documentumie.fileutil.metadata.exception.DMetadataReadException;
import com.daffodil.documentumie.fileutil.metadata.exception.DMetadataWriteException;

public class CSVMetadataWriter implements MetadataWriter {

	private Connection con = null;

	private Statement stmtWriter = null;

	int noofColumn;

	String filePath;

	String tableName;

	public CSVMetadataWriter(String path, String fileName){
		this.filePath = path;
		this.tableName = fileName;
	}

	private void initConnection() throws DMetadataWriteException{
		String driver = "jdbc:odbc:Driver="+
		"{Microsoft Text Driver (*.txt; *.csv)};"+
		"DBQ=" + filePath +
		";Extensions=asc,csv,tab,txt";

		try {
			Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");

			con = DriverManager.getConnection(driver, "", "");
		} catch (SQLException e) {
			throw new DMetadataWriteException(
					"Exception while inilizing connection " + e.getMessage()
					+ e.getCause());

		} catch (ClassNotFoundException e) {
			throw new DMetadataWriteException(
					"Exception while inilizing connection " + e.getMessage()
					+ e.getCause());

		}
	}
	
	public void wirteAttributes(List attributes) throws DMetadataWriteException {
		initConnection();
		StringBuffer sql = new StringBuffer();
		sql.append("create table " + tableName + "(");
		for (int i = 0; i < attributes.size(); i++) {
			sql.append(attributes.get(i) + " varchar");
			if (i < attributes.size() - 1) {
				sql.append(", ");
			}
		}
		sql.append(")");
		
		try {
			stmtWriter = con.createStatement();
			stmtWriter.execute(sql.toString());
		} catch (SQLException e) {
			throw new DMetadataWriteException(
					"Exception while creating file " + e.getMessage()
							+ e.getCause());
		}
		closeConnection();
	}

	private String prepareSQl(Map map) {
		StringBuffer sql;
		int size = 1;
		sql = new StringBuffer();
		sql.append("insert into " + tableName + "(");
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
			/*if(value.contains()){
				
			}*/
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
		initConnection();
		try {
			stmtWriter = con.createStatement();
			String sql = prepareSQl(map);

			int i = stmtWriter.executeUpdate(sql);
			closeConnection();
		} catch (SQLException e) {
			e.getMessage();
			e.getCause();
			throw new DMetadataWriteException(
					"Exception while writing row " + e.getMessage()
							+ e.getCause());
		}
		closeConnection();
	}

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

}
