package com.daffodil.documentumie.fileutil.metadata.apiimpl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.daffodil.documentumie.fileutil.metadata.api.MetadataReader;
import com.daffodil.documentumie.fileutil.metadata.exception.DMetadataReadException;
import com.daffodil.documentumie.fileutil.metadata.exception.DMetadataWriteException;

public class CSVMetadataReader implements MetadataReader {

	private Connection con = null;

	private ResultSet rs = null;

	private Statement stmt = null;

	private ResultSetMetaData metadata = null;

	int noofColumn;

	String filePath;

	String tableName;

	public CSVMetadataReader(String path, String fileName){
		this.filePath = path;
		this.tableName = fileName;
	}

	private void initConnection() throws DMetadataReadException{
		String driver = "jdbc:odbc:Driver="+
		"{Microsoft Text Driver (*.txt; *.csv)};"+
		"DBQ=" + filePath +
		";Extensions=asc,csv,tab,txt";

		try {
			Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");

			con = DriverManager.getConnection(driver, "", "");
		} catch (SQLException e) {
			throw new DMetadataReadException(
					"Exception while inilizing connection " + e.getMessage()
					+ e.getCause());

		} catch (ClassNotFoundException e) {
			throw new DMetadataReadException(
					"Exception while inilizing connection " + e.getMessage()
					+ e.getCause());

		}
	}

	public HashMap getAttributes() throws DMetadataReadException {
		initConnection();
		String sql = "select * from " + tableName;
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);

			ResultSetMetaData metadata = rs.getMetaData();
			int noOfColumn = metadata.getColumnCount();
			HashMap attributesMap = new HashMap();

			for (int i = 1; i <= noOfColumn; i++) {
				attributesMap.put(metadata.getColumnName(i), metadata.getColumnTypeName(i));
			}
			closeConnection();
			return attributesMap;

		} catch (SQLException e) {
			throw new DMetadataReadException(
					"Exception while getting attributes " + e.getMessage()
					+ e.getCause());

		}
	}

	public List getRows(List attrName, String whereClause)
	throws DMetadataReadException {
		initConnection();

		String sql = preparesSQl(attrName, whereClause);
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
		sql.append(tableName);
		if (whereClause != null && !"".equalsIgnoreCase(whereClause)) {
			sql.append(" where " + whereClause);
		} 
		String sql1 = sql.toString();
		return sql1;

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

	public int getRowsCount() throws DMetadataReadException {
		// TODO Auto-generated method stub
		return 0;
	}
}