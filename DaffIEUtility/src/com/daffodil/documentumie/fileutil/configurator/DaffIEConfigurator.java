package com.daffodil.documentumie.fileutil.configurator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import sun.security.action.GetLongAction;

import com.daffodil.documentumie.fileutil.configurator.bean.ConfigBean;
import com.daffodil.documentumie.fileutil.configurator.bean.ExportConfigBean;
import com.daffodil.documentumie.fileutil.configurator.bean.ImportConfigBean;
import com.daffodil.documentumie.fileutil.configurator.exception.DConfigReadException;
import com.daffodil.documentumie.fileutil.configurator.exception.DConfigWriteException;

/**
 * This class is reading attributes of ImportConfigBean class from
 * ImportConfig.xml.
 */
public class DaffIEConfigurator {
	static Document document;

	public static int IMPORT = 1;
	public static int EXPORT = 2;

	private static String OS_NAME = System.getProperty("os.name");
	private static String USER_NAME = System.getProperty("user.name");
	//private static String WINDOWS = "Windows XP";
	 private static String WINDOWS = System.getProperty("os.name");
	 //change by pallavi**********
	

	// Function to read XML
	static public ConfigBean read(int operation) throws DConfigReadException {
		System.out.println("DaffIEConfigurator.read()");
		Class objClass = null;
		ConfigBean configBean = null;
		if (operation == IMPORT) {
			configBean = new ImportConfigBean();
			objClass = ImportConfigBean.class;
		} else {
			configBean = new ExportConfigBean();
			objClass = ExportConfigBean.class;
		}

		HashMap map = readFile(operation);
		if(map == null){
			if (operation == IMPORT) {
				return new ImportConfigBean();
			}else{
				return new ExportConfigBean();
			}

		}

		Iterator i = map.keySet().iterator();
		while (i.hasNext()) {
			String attr = (String) i.next();

			Method m = null; 
			try {
				m = objClass.getMethod("set" + attr, String.class);

				m.invoke(configBean, map.get(attr));
			} catch (SecurityException e) {
				throw new DConfigReadException(
						"Exception while getting method from bean class", e
						.getCause());
			} catch (NoSuchMethodException e) {
				throw new DConfigReadException("No method found " + "set"
						+ attr
						+ "(). Exception while getting method from bean class",
						e.getCause());
			} catch (IllegalArgumentException e) {
				throw new DConfigReadException(
						"Exception while invoking method on bean. may be due to wrong parameter." + e.getMessage(),
						e.getCause());
			} catch (IllegalAccessException e) {
				throw new DConfigReadException(
						"Exception while invoking method on bean.", e
						.getCause());
			} catch (InvocationTargetException e) {
				throw new DConfigReadException(
						"Exception while invoking method on bean.", e
						.getCause());
			}
		}
		configBean.setLoggerFileName(getLogFileName());
		return configBean;
	}

	private static HashMap readFile(int operation) throws DConfigReadException {
		HashMap map = new HashMap();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;

		try {
			builder = factory.newDocumentBuilder();

			if (operation == IMPORT) {
				if (OS_NAME.contains(WINDOWS)) { // Windows XP
					File dctm = new File("C:/Documentum/" + USER_NAME + "/DaffIE/ImportConfig.xml");
					if (!dctm.exists()) {
						return null;
					} else {
						document = builder.parse(dctm);
					}
				} else {// Linux
					File dctm = new File("/usr/Documentum/" + USER_NAME + "/DaffIE/ImportConfig.xml");
					if (!dctm.exists()) {
						return null;
					} else {
						document = builder.parse(dctm);
					}
				}

			} else { // For Export Purpose
				if (OS_NAME.contains(WINDOWS)) {
					File dctm = new File("C:/Documentum/" + USER_NAME + "/DaffIE/ExportConfig.xml");
					if (!dctm.exists()) {
						return null;
					} else {
						document = builder.parse(dctm);
					}
				} else {// Linux
					File dctm = new File("/usr/Documentum/" + USER_NAME + "/DaffIE/ExportConfig.xml");
					if (!dctm.exists()) {
						return null;
					} else {
						document = builder.parse(dctm);
					}
				}
			}

		} catch (ParserConfigurationException e) {
			throw new DConfigReadException("Exception while creating document builder.\n" + e.getMessage(), e.getCause());
		} catch (SAXException e) {
			throw new DConfigReadException("Exception while Parsing configuration file.\n" + e.getMessage(), e.getCause());
		} catch (IOException e) {
			throw new DConfigReadException("Exception while Parsing configuration file.\n" + e.getMessage(), e.getCause());
		}

		Element rootElement = document.getDocumentElement();

		NodeList importConfigBeanAttributes = rootElement.getChildNodes();

		for (Node importConfigBeanAttribute = ((Node) importConfigBeanAttributes).getFirstChild(); importConfigBeanAttribute != null; importConfigBeanAttribute = importConfigBeanAttribute.getNextSibling()) {
			if (importConfigBeanAttribute instanceof Element) {
				// System.out.println("new ImportExportMain().showIEUI(0); :
				// "+importConfigBeanAttribute.getNodeName());
				// System.out.println("importConfigBeanAttribute.getTextContent().
				// : "+importConfigBeanAttribute.getTextContent());
				map.put(importConfigBeanAttribute.getNodeName(), importConfigBeanAttribute.getTextContent());
			}
		}
		return map;
	}

	static public void write(ConfigBean configBean, int operation) throws DConfigWriteException {
		Class objClass = null;
		File file = null;

		if (operation == IMPORT) {
			objClass = ImportConfigBean.class;

			if (OS_NAME.contains(WINDOWS)) {
				file = new File("C:/Documentum/" + USER_NAME + "/DaffIE/ImportConfig.xml");
			} else {
				file = new File("/usr/Documentum/" + USER_NAME + "/DaffIE/ImportConfig.xml");
			}

		} else {
			objClass = ExportConfigBean.class;

			if (OS_NAME.contains(WINDOWS)) {
				file = new File("C:/Documentum/" + USER_NAME + "/DaffIE/ExportConfig.xml");
			} else {
				file = new File("/usr/Documentum/" + USER_NAME + "/DaffIE/ExportConfig.xml");
			}
		}

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new DConfigWriteException("Exception while Write configuration file.\n" + e.getMessage(), e.getCause());
		}

		DOMImplementation impl = builder.getDOMImplementation();
		String commonAttrib = null;

		HashMap mapWriter = new HashMap();
		Method[] m = objClass.getMethods();
		for (int i = 0; i < m.length; i++) {
			String name = m[i].getName();
			try {
				if (name.startsWith("get") && name != "getClass") {
					Object val;

					val = m[i].invoke(configBean);

					mapWriter.put(name.substring(3), val);
				}
			} catch (IllegalArgumentException e) {
				throw new DConfigWriteException("Exception while invoking method on bean. may be due to wrong parameter." + e.getMessage(), e.getCause());
			} catch (InvocationTargetException e) {
				throw new DConfigWriteException("Exception while invoking method on bean." + e.getMessage(), e.getCause());
			} catch (IllegalAccessException e) {
				throw new DConfigWriteException("Exception while invoking method on bean." + e.getMessage(), e.getCause());
			}
		}

		// Read Existing File
		HashMap mapReader = new HashMap();

		// Check file is already existing or not
		if (!file.exists()) {
			try {
				createXMLFile(configBean, operation);
			} catch (IllegalArgumentException e) {
				throw new DConfigWriteException("Exception while invoking method on bean. may be due to wrong parameter.", e.getCause());
			}
		} else {

			try {
				document = builder.parse(file);
			} catch (IOException e) {
				throw new DConfigWriteException("Exception while Write configuration file.\n" + e.getMessage(), e.getCause());
			} catch (SAXException e) {
				throw new DConfigWriteException("Exception while Write configuration file.\n" + e.getMessage(), e.getCause());
			}

			Element rootElement = document.getDocumentElement();

			NodeList importConfigBeanAttributes = rootElement.getChildNodes();

			for (Node importConfigBeanAttribute = ((Node) importConfigBeanAttributes).getFirstChild(); importConfigBeanAttribute != null; importConfigBeanAttribute = importConfigBeanAttribute.getNextSibling()) {
				if (importConfigBeanAttribute instanceof Element)
					mapReader.put(importConfigBeanAttribute.getNodeName(), importConfigBeanAttribute.getTextContent());
			}

			Iterator i = mapWriter.keySet().iterator();
			Iterator j = mapReader.keySet().iterator();

			while (i.hasNext()) {
				commonAttrib = (String) i.next();
				if (mapReader.containsKey(commonAttrib)) {
					if (mapReader.get(commonAttrib).equals(mapWriter.get(commonAttrib))) {
					} else {

						Element oldElement = (Element) document.getElementsByTagName(commonAttrib).item(0);
						// Remove the node
						rootElement.removeChild(oldElement);

						// new code end
						Element newAttribName = document.createElement(commonAttrib);
						Text newAttribValue = document.createTextNode(mapWriter.get(commonAttrib).toString());
						newAttribName.appendChild(newAttribValue);
						rootElement.appendChild(newAttribName);
					}
				} else {

					if (mapWriter.get(commonAttrib) != null) {
						Element newAttribName = document.createElement(commonAttrib);
						Text newAttribValue = document.createTextNode((String) mapWriter.get(commonAttrib));
						newAttribName.appendChild(newAttribValue);
						rootElement.appendChild(newAttribName);
					}

				}
			}

			TransformerFactory xformFactory = TransformerFactory.newInstance();
			Transformer idTransform;
			try {
				idTransform = xformFactory.newTransformer();
			} catch (TransformerException e) {
				throw new DConfigWriteException("Exception while Write configuration file.\n" + e.getMessage(), e.getCause());
			}
			Source input = new DOMSource(document);
			Result output;
			try {
				output = new StreamResult(new FileOutputStream(file));
			} catch (FileNotFoundException e) {
				throw new DConfigWriteException("Exception while Write configuration file.\n" + e.getMessage(), e.getCause());
			}
			try {
				idTransform.transform(input, output);
			} catch (TransformerException e) {
				throw new DConfigWriteException("Exception while Write configuration file.\n" + e.getMessage(), e.getCause());
			}
		}

	}

	private static void createXMLFile(ConfigBean configBean, int operation) throws DConfigWriteException {

		boolean dctmExist = true;
		File dctm = null;
		if (OS_NAME.contains(WINDOWS)) {
			dctm = new File("C:/Documentum/" + USER_NAME + "");
			if (!dctm.exists()) {
				dctmExist = dctm.mkdir();
			}
			if (dctmExist) {
				File daffIE = new File("C:/Documentum/" + USER_NAME + "/DaffIE");
				if (!daffIE.exists()) {
					daffIE.mkdir();
				}
			}
		} else {
			dctm = new File("/usr/Documentum/" + USER_NAME + "");
			if (!dctm.exists()) {
				dctmExist = dctm.mkdir();
			}
			if (dctmExist) {
				File daffIE = new File("/usr/Documentum/" + USER_NAME + "/DaffIE");
				if (!daffIE.exists()) {
					daffIE.mkdir();
				}
			}
		}

		Class objClass = null;
		String fileName = null;
		String rootTag = null;
		if (operation == IMPORT) {
			objClass = ImportConfigBean.class;
			if (OS_NAME.contains(WINDOWS)) {
				fileName = "C:/Documentum/" + USER_NAME + "/DaffIE/ImportConfig.xml";
			} else {
				fileName = "/usr/Documentum/" + USER_NAME + "/DaffIE/ImportConfig.xml";
			}
			rootTag = "Import";
		} else {
			objClass = ExportConfigBean.class;
			if (OS_NAME.contains(WINDOWS)) {
				fileName = "C:/Documentum/" + USER_NAME + "/DaffIE/ExportConfig.xml";
			} else {
				fileName = "/usr/Documentum/" + USER_NAME + "/DaffIE/ExportConfig.xml";
			}
			rootTag = "Export";
		}

		HashMap mapWriter = new HashMap();
		Method[] m = objClass.getMethods();
		for (int i = 0; i < m.length; i++) {
			String name = m[i].getName();
			try {

				if (name.startsWith("get") && name != "getClass") {
					Object val;
					val = m[i].invoke(configBean);

					mapWriter.put(name.substring(3), val);
				}
			} catch (InvocationTargetException e) {
				throw new DConfigWriteException("Exception while invoking method on bean.", e.getCause());
			} catch (IllegalAccessException e) {
				throw new DConfigWriteException("Exception while invoking method on bean.", e.getCause());
			}
		}

		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			DocumentBuilder builder;

			builder = factory.newDocumentBuilder();

			DOMImplementation impl = builder.getDOMImplementation();

			document = impl.createDocument(null, rootTag, null);
		} catch (ParserConfigurationException e) {
			throw new DConfigWriteException("Exception while Write configuration file.\n" + e.getMessage(), e.getCause());
		}
		Element root = document.getDocumentElement();
		Iterator iterator = mapWriter.keySet().iterator();

		while (iterator.hasNext()) {
			String attr = (String) iterator.next();
			if (mapWriter.get(attr) != null) {
				Element attrib_name = document.createElement(attr);
				Text attrib_value = document.createTextNode((mapWriter.get(attr)).toString());
				attrib_name.appendChild(attrib_value);
				root.appendChild(attrib_name);
			}
		}

		// Serialize the document onto System.out
		TransformerFactory xformFactory = TransformerFactory.newInstance();
		Transformer idTransform;
		try {
			idTransform = xformFactory.newTransformer();
			Source input = new DOMSource(document);
			Result output = new StreamResult(new FileOutputStream(fileName));
			idTransform.transform(input, output);
		} catch (TransformerException e) {
			throw new DConfigWriteException("Exception while Write configuration file.\n" + e.getMessage(), e.getCause());
		} catch (FileNotFoundException e) {
			throw new DConfigWriteException("Exception while Write configuration file.\n" + e.getMessage(), e.getCause());
		}
	}

	// public static void main(String[] args) {
	// try {
	// read(1);
	// } catch (DConfigReadException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }

	private static String getLogFileName() {
		File logFile=null;
		if (OS_NAME.contains(WINDOWS)) { // Windows XP
			 logFile = new File("C:/Documentum/" + USER_NAME + "/DaffIE/IEUtilityLog.log");
		} else {// Linux
			 logFile = new File("/usr/Documentum/" + USER_NAME + "/DaffIE/IEUtilityLog.log");
		}
		if(!logFile.exists()){
			try {
				logFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return logFile.getPath();
	}
}