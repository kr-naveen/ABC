package com.daffodil.documentumie.fileutil.metadata.apiimpl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import com.daffodil.documentumie.fileutil.metadata.api.MetadataWriter;
import com.daffodil.documentumie.fileutil.metadata.exception.DMetadataWriteException;

public class XMLMetadataWriter implements MetadataWriter {

	private Connection con = null;

	private Statement stmtWriter = null;

	int noofColumn;

	String filePath;

	String tableName;

	File xmlFile = null;

	public XMLMetadataWriter(String path, String fileName){
		this.filePath = path;
		this.tableName = fileName;
	}

	private void initConnection() throws DMetadataWriteException{

	}

	String rootChild = null;
	public void wirteAttributes(List attributes) throws DMetadataWriteException {
		String root;
		root = attributes.get(0).toString();
		rootChild = attributes.get(1).toString();
		Document document = null;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = null;

		FileOutputStream fout;
		String commonAttrib = null;
		DOMImplementation impl = null;
		try {
			builder = factory.newDocumentBuilder();
			impl = builder.getDOMImplementation();

			xmlFile = new File(filePath+"\\"+tableName); // chnage "/" with "\\" on 20-05-16
			if(!xmlFile.exists()){
				xmlFile.createNewFile();
			} 

			fout = new FileOutputStream(xmlFile,true);
			document = impl.createDocument(null, root, null);
			TransformerFactory xformFactory = TransformerFactory.newInstance();
			Transformer idTransform = null;
			idTransform = xformFactory.newTransformer();

			Source input = new DOMSource(document);
			Result output = null;
			input = new DOMSource(document);
			output = new StreamResult(fout);
			idTransform.transform(input, output);

		}catch (IOException e) {
			throw new DMetadataWriteException(
					"Exception while creating file " + e.getMessage()
					+ e.getCause());
		}catch (ParserConfigurationException e) {
			throw new DMetadataWriteException(
					"Exception while creating file " + e.getMessage()
					+ e.getCause());
		} catch (TransformerConfigurationException e) {
			throw new DMetadataWriteException(
					"Exception while creating file " + e.getMessage()
					+ e.getCause());
		} catch (TransformerException e) {
			throw new DMetadataWriteException(
					"Exception while creating file " + e.getMessage()
					+ e.getCause());
		}


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

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		Document document;
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();

			document = builder.parse(xmlFile);

			Element rootElement = document.getDocumentElement();

			TransformerFactory xformFactory = TransformerFactory.newInstance();
			Transformer idTransform = null;
			idTransform = xformFactory.newTransformer();

			Source input = new DOMSource(document);
			Result output = null;
			FileOutputStream fout;

			fout = new FileOutputStream(xmlFile);


			Element	newAttribName=null;
			newAttribName= document.createElement(rootChild);
			Text newAttribValue = null;// = document.createTextNode("hello");
			rootElement.appendChild(newAttribName);
			String commonAttrib = null;
			Iterator i = map.keySet().iterator();
			while (i.hasNext()) {
				commonAttrib = (String) i.next();

				// new code end
				Element	newAttribName1 = document
				.createElement(commonAttrib);
				newAttribValue = document.createTextNode(map.get(commonAttrib).toString());
				newAttribName1.appendChild(newAttribValue);
				newAttribName.appendChild(newAttribName1);
			}//rootElement.appendChild(newAttribName);


			output = new StreamResult(fout);

			idTransform.transform(input, output);

		} catch (TransformerException e) {
			throw new DMetadataWriteException(
					"Exception while writing file " + e.getMessage()
					+ e.getCause());	
		} catch (FileNotFoundException e) {
			throw new DMetadataWriteException(
					"Exception while writing file " + e.getMessage()
					+ e.getCause());	} catch (ParserConfigurationException e1) {
					} catch (SAXException e) {
						throw new DMetadataWriteException(
								"Exception while writing file " + e.getMessage()
								+ e.getCause());
					} catch (IOException e) {
						throw new DMetadataWriteException(
								"Exception while writing file " + e.getMessage()
								+ e.getCause());
					}

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
