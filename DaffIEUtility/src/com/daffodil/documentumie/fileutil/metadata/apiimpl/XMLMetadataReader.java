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
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.log4j.lf5.viewer.configure.MRUFileManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.daffodil.documentumie.fileutil.metadata.api.MetadataReader;
import com.daffodil.documentumie.fileutil.metadata.exception.DMetadataReadException;


import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;


public class XMLMetadataReader implements MetadataReader {

	private Connection con = null;

	private ResultSet rs = null;

	private Statement stmt = null;

	private ResultSetMetaData metadata = null;

	int noofColumn;

	String filePath;

	String tableName;
	DocumentBuilderFactory docBuilderFactory;
	DocumentBuilder docBuilder;
	Document doc;
	NodeList nl;
	Element e;
    int len;

	public XMLMetadataReader(String path, String fileName){
		
		this.filePath = path;
		this.tableName = fileName;
	}

	private void initConnection() throws DMetadataReadException{
		//String driver = "jdbc:ashpool:file://" + filePath;
		String driver = "jdbc:ashpool:file://" +"C:\\Users\\daffodil-50\\Documents\\zzzz\\Export_29062016_057.xml";

		try {
			Class.forName("com.rohanclan.ashpool.jdbc.Driver");
			con = null;
			con = DriverManager.getConnection(driver, "", "");
		} catch (SQLException e) {
			throw new DMetadataReadException(
					"Exception while inilizing connection " + e.getMessage(), e.getCause());

		} catch (ClassNotFoundException e) {
			throw new DMetadataReadException(
					"Exception while inilizing connection " + e.getMessage(), e.getCause());

		}
	}
	public HashMap getAttributes() throws DMetadataReadException {
		/**
		 * read attributes of xml
		 * 
		 */
		
		try {
			
			start();
            len = nl.getLength();
            int l = 0;
            HashMap attributesMap = new HashMap();
            for (int j=0; j < len; j++)
            {
               e = (Element)nl.item(j);
               
               if(e.getTagName().equals("dm_document") && l == 0)
               {
            	 l++;  
               }
               else if(e.getTagName().equals("dm_document") && l != 0)
               {
            	   l = 0;
               }
               else if(l != 0)
               {
            	   attributesMap.put(e.getNodeName(), "undefined");
               }
            }
            return attributesMap;
            
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public int getRowsCountExXML() 
	{
		start();
		nl = doc.getElementsByTagName("EScheduleConfigBean");
		//System.out.println("length = "+nl.getLength());
		return nl.getLength();
	}

	public List getRows(List attrName, String whereClause)throws DMetadataReadException 
	{
		/**
		 * method to get rows of the xml
		 */
		try {
			start();
            len = nl.getLength();
            int l = 0, k = 1;
            List metadatRows = new ArrayList();
        	HashMap attributesMap = new HashMap();
        	
            for (int j=0; j < len; j++)
            {
               e = (Element)nl.item(j);
               if(attributesMap.containsKey(e.getNodeName()))
               {
            	   metadatRows.add(attributesMap);
            	   attributesMap = new HashMap(); 
               }
               if(e.getTagName().equals("dm_document") && l == 0)
               {
            	   l++;
               }
               else if(l != 0)
               {
	               if(!e.getTagName().equals("dm_document"))
	               {
	            	   attributesMap.put(e.getNodeName(), e.getTextContent());
	               }
               }
            }
            metadatRows.add(attributesMap);
            return metadatRows;
            
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// TODO Auto-generated method stub
		return null;
	}
// not required as of now
	/*private String preparesSQl(List attrName, String whereClause) {
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

	/*public HashMap getAttributes() throws DMetadataReadException {
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
	}*/

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
		start();
		nl = doc.getElementsByTagName("dm_document");
		//System.out.println("length = "+nl.getLength());
		return nl.getLength();
	}
	public int getRowsCountXML()
	{
		start();
		nl = doc.getElementsByTagName("IScheduleConfigBean");
		//System.out.println("length = "+nl.getLength());
		return nl.getLength();
	}
	public HashMap getColumnXml()
	{
		
		System.out.println("*************** Inside XML File ****************");
		try {
			
			start();
            len = nl.getLength();
            int l = 0;
            HashMap attributesMap = new HashMap();
            for (int j=0; j < len; j++)
            {
               e = (Element)nl.item(j);
               
               if(e.getTagName().equals("IScheduleConfigBean") && l == 0)
               {
            	 l++;  
               }
               else if(e.getTagName().equals("IScheduleConfigBean") && l != 0)
               {
            	   l = 0;
               }
               else if(l != 0)
               {
            	   attributesMap.put(e.getNodeName(), "undefined");
               }
            }
            return attributesMap;
            
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
		
	}
	
	
	
	
	
	
	
	public boolean removeXmlDataFromFile(int index ,String fileName)
	{
		System.out.println("**********Remove xmlmetadata file****");
		try 
		{
			System.out.println("index-------------------"+index);
			DocumentBuilderFactory factory = DocumentBuilderFactory	.newInstance();
			DocumentBuilder docBuilder = factory.newDocumentBuilder();
			Document doc = docBuilder.parse(new File(fileName));
			
				NodeList list1 = doc.getElementsByTagName("root");
				System.out.println("list1list1"+list1.getLength());
				/*Node node1 = (Node) list1.item(index);
				node1.getParentNode().removeChild(node1);*/
				Node node1 = (Node) list1.item(0);
				NodeList childList1 = node1.getChildNodes();
				Node child3 = (Node) childList1.item(index);
				child3.getParentNode().removeChild(child3);


			try 
			{
				OutputFormat format = new OutputFormat(doc);
				FileWriter fw = new FileWriter(fileName);
				XMLSerializer serial = new XMLSerializer(fw, format);
				serial.asDOMSerializer();
				serial.serialize(doc.getDocumentElement());
				fw.flush();
				fw.close();
			} 
			catch (IOException io) 
			{
				io.printStackTrace();
				return false;
			}

		} 
		catch (ParserConfigurationException pce) 
		{
			pce.printStackTrace();
		} 
		catch (IOException ioe) 
		{
			ioe.printStackTrace();
		} catch (SAXException saxe) {
			saxe.printStackTrace();
		}

		return true;
		
	}
	
	public void updateXmlData()
	{
		
		/**
		 * get the row no
		 * delete perticular node
		 * 
		 */
		System.out.println("****** Inside the updatexmldata ****");
		start();
		
		
		System.out.println("Node Name"+doc.getNodeName());
		nl = doc.getElementsByTagName("IScheduleConfigBean");
		
		System.out.println("index1="+nl.item(2));
	//	e.removeAttributeNode(nl.item(0));
		
		System.out.println(e.getTagName().equals("ScheduleName") );
		
	}
	
	public String returnFileName()
	{
		System.out.println("Path"+this.filePath+this.tableName+".xml");
		return this.filePath+this.tableName+".xml";
	}
	private void start()
	{
		try 
		{
			docBuilderFactory = DocumentBuilderFactory.newInstance();
			docBuilder = docBuilderFactory.newDocumentBuilder();
			//System.out.println("My path"+filePath+""+tableName+".xml");
			doc = docBuilder.parse (new File(filePath+""+tableName+".xml"));
			 // normalize text representation
			doc.getDocumentElement ().normalize ();
			nl = doc.getElementsByTagName("*");
		}
		catch (ParserConfigurationException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (SAXException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/*
	 * Nity
	 * Scheduling methods
	 */
	public List getRowsXML()
	{
		/**
		 * method to get rows of the xml
		 */
		try {
			start();
            len = nl.getLength();
            int l = 0, k = 1;
            List metadatRows = new ArrayList();
        	HashMap attributesMap = new HashMap();
        	
            for (int j=0; j < len; j++)
            {
               e = (Element)nl.item(j);
               
               if(attributesMap.containsKey(e.getNodeName()))
               {
            	   metadatRows.add(attributesMap);
            	   attributesMap = new HashMap(); 
               }
               if(e.getTagName().equals("IScheduleConfigBean") && l == 0)
               {
            	   l++;
               }
               else if(l != 0)
               {
	               if(!e.getTagName().equals("IScheduleConfigBean"))
	               {
	            	   attributesMap.put(e.getNodeName(), e.getTextContent());
	               }
               }
            }
            metadatRows.add(attributesMap);
            System.out.println("Naveen:"+metadatRows);
            return metadatRows;
            
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// TODO Auto-generated method stub
		return null;
	}
	public List getExRowsXML()
	{
		System.out.println("********* Inside get getExRowsXML ******");
		/**
		 * method to get rows of the xml
		 */
		try {
			start();
            len = nl.getLength();
            int l = 0, k = 1;
            List metadatRows = new ArrayList();
        	HashMap attributesMap = new HashMap();
        	
            for (int j=0; j < len; j++)
            {
               e = (Element)nl.item(j);
               
               if(attributesMap.containsKey(e.getNodeName()))
               {
            	   metadatRows.add(attributesMap);
            	   attributesMap = new HashMap(); 
               }
               if(e.getTagName().equals("EScheduleConfigBean") && l == 0)
               {
            	   l++;
               }
               else if(l != 0)
               {
	               if(!e.getTagName().equals("EScheduleConfigBean"))
	               {
	            	   attributesMap.put(e.getNodeName(), e.getTextContent());
	               }
               }
            }
            metadatRows.add(attributesMap);
            return metadatRows;
            
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// TODO Auto-generated method stub
		return null;
	}
	public HashMap getExColumnXml()
	{
		
		System.out.println("*************** Inside XML File ****************");
		try {
			
			start();
            len = nl.getLength();
            int l = 0;
            HashMap attributesMap = new HashMap();
            for (int j=0; j < len; j++)
            {
               e = (Element)nl.item(j);
               
               if(e.getTagName().equals("EScheduleConfigBean") && l == 0)
               {
            	 l++;  
               }
               else if(e.getTagName().equals("EScheduleConfigBean") && l != 0)
               {
            	   l = 0;
               }
               else if(l != 0)
               {
            	   attributesMap.put(e.getNodeName(), "undefined");
               }
            }
            return attributesMap;
            
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
		
	}
}