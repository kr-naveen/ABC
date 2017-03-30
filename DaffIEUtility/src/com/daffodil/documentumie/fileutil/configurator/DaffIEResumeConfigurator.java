package com.daffodil.documentumie.fileutil.configurator;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Date;
import java.util.Calendar;
import java.util.Map;

import javax.sound.midi.SysexMessage;
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

import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import com.daffodil.documentumie.fileutil.configurator.bean.ImportConfigBeanForResume;
import com.daffodil.documentumie.fileutil.configurator.exception.DConfigReadException;
import com.daffodil.documentumie.fileutil.configurator.exception.DConfigWriteException;

/**
 * This class is reading attributes of ImportConfigBean class from
 * ImportConfig.xml.
 */
public class DaffIEResumeConfigurator {
	static Document document;
	private static String OS_NAME = System.getProperty("os.name");
	private static String USER_NAME = System.getProperty("user.name");
	private static String WINDOWS = "Windows XP";
	private static String DATE_FORMAT = "ddMMyyyy-HHmmss";
	private static HashMap[] filecollection=null;
	static File [] files = null;

	// Function to read XML
	public static HashMap<String, String> findResumableProcesses(String userId) {
		System.out.println("DaffIEResumeConfigurator.findResumableProcesses()");
		ArrayList<String> fileList=new ArrayList<String>();
		HashMap<String, String> map=null;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		String dirName= new String(getDocumentumHome()+"/"+USER_NAME+"/DaffIE");
		File  dirObject=new File(dirName);
		if(!dirObject.exists()){
			return null;
		}

		files=dirObject.listFiles();
//		System.out.println("no of files : "+files.length);
		for(int i=0 ;i<files.length ;i++){
			if(!files[i].toString().contains("ImportConfigBeanForResume")){
				files[i]=null;
			}
//			System.out.println("got resume file");
		}
		int len = files.length;
		if(len == 0){
			return null;
		}else{
			map=new HashMap<String, String>();
		}
		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		File  fileObject;
		for(int i=files.length-1 ;i>=0 ;i--){
//			System.out.println("inside loop");
			if(files[i]!=null){
//					MetadataFileLocation
				try { 
					String fileName = files[i].getName();
					String ext = (fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length()));
					if (ext.equalsIgnoreCase("xml")) {
						Document doc = db.parse(files[i]);
						doc.getDocumentElement().normalize();
						NodeList node1 = doc.getElementsByTagName("UserName");
						Element userElement = (Element) node1.item(0);
						if (userElement != null) {
							String user = ((Node) userElement).getTextContent();
							if (userId.equalsIgnoreCase(user)) {
								NodeList node = doc.getElementsByTagName("MetadataFileLocation");
								Element metaDataLocation = (Element) node.item(0);
								if (metaDataLocation != null) {
									fileObject = new File(((Node) metaDataLocation).getTextContent());
									if (!fileObject.exists()) {
										files[i].delete();
									} else {
										map.put(fileObject.getAbsolutePath(), files[i].getAbsolutePath());

									}
								} 
//								else {
//									// files[i].delete();//----------hata do
//								}
							}
						}
					}
				} catch (SAXException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return map;
	}		 
	public static ImportConfigBeanForResume readFile(String fileName) throws DConfigReadException {
		Class objClass = ImportConfigBeanForResume.class;
		ImportConfigBeanForResume configBean = null;
		configBean = new ImportConfigBeanForResume();
		Method m = null; 
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			document = builder.parse(new File(fileName ));
			document.getDocumentElement().normalize();
		} catch (ParserConfigurationException e) {
			throw new DConfigReadException("Exception while creating document builder.\n"+ e.getMessage(), e.getCause());
		} catch (SAXException e) {
			throw new DConfigReadException("Exception while Parsing configuration file.\n"+ e.getMessage(), e.getCause());
		} catch (IOException e) {
			throw new DConfigReadException("Exception while Parsing configuration file.\n"+ e.getMessage(), e.getCause());
		}

		Element rootElement = document.getDocumentElement();
		NodeList nodes = rootElement.getChildNodes();
		for ( Node node = ((Node) nodes).getFirstChild(); node != null; 
		node = node.getNextSibling()) {
			if(!node.getNodeName().equalsIgnoreCase("map")){
				if (node instanceof Element){
					try {
						m = objClass.getMethod("set" + node.getNodeName(), String.class);
						m.invoke(configBean, node.getTextContent().toString());

					} catch (SecurityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (DOMException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
			NodeList mapNode = document.getElementsByTagName("Map");
			HashMap map= new HashMap();
			Element mapElement = (Element) mapNode.item(0);
			if(mapElement.hasChildNodes()){
				NodeList mapNodeList=mapElement.getChildNodes();
				for ( Node node1 = ((Node) mapNodeList).getFirstChild(); node1 != null; 
				node1 = node1.getNextSibling()) {
					map.put(node1.getNodeName(), node1.getTextContent());
				}
				try {
					m = objClass.getMethod("setMap", HashMap.class);
					m.invoke(configBean, map);
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return configBean;
	}

	public static void createXMLFile(Object configBean)
	throws DConfigWriteException {
		File userDirectory= null;
		File daffIEDir=null;
		String userDir=null;
		String fileDir=null;
		String fileName = null;
		Class objClass = configBean.getClass();
		DateFormat df1 = DateFormat.getDateInstance();
		String rootTag = objClass.getSimpleName();
		userDir = new String (getDocumentumHome()+"/"+USER_NAME+"");
		fileDir = new String(userDir+"/DaffIE");
		fileName = new String(fileDir+"/"+objClass.getSimpleName().toString()+new SimpleDateFormat(DATE_FORMAT).format(new Date()).toString() +".xml");
		userDirectory = new File(userDir);
		if (!userDirectory.exists()) 
			userDirectory.mkdir();
		daffIEDir = new File(fileDir);
		if (!daffIEDir.exists()) 
			daffIEDir.mkdir();
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
				throw new DConfigWriteException("Exception while invoking method on bean.", e.getCause());				}
			catch (IllegalAccessException e){
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
			throw new DConfigWriteException("Exception while Write configuration file.\n"+ e.getMessage(), e.getCause());
		}
		Element root = document.getDocumentElement();
		createNodesResursive(root,mapWriter,configBean);

		TransformerFactory xformFactory = TransformerFactory.newInstance();
		Transformer idTransform;
		try {
			idTransform = xformFactory.newTransformer();
			Source input = new DOMSource(document);
			Result output = new StreamResult(new FileOutputStream(fileName));
			idTransform.transform(input, output);
		}
		catch (TransformerException e) {
			throw new DConfigWriteException("Exception while Write configuration file.\n"+ e.getMessage(), e.getCause());
		}
		catch (FileNotFoundException e){
			throw new DConfigWriteException("Exception while Write configuration file.\n"+ e.getMessage(), e.getCause());
		}
	}		

	private static void createNodesResursive(Element root,HashMap mapWriter,Object configBean)
	{
		Iterator iterator = mapWriter.keySet().iterator();
		Text attrib_value1=null;
		Class objectClass=configBean.getClass();

		while (iterator.hasNext()) {
			Object attr =  iterator.next();
			if (mapWriter.get(attr) != null) {
				Element attrib_name = document.createElement((String) attr);
				if(((String) attr).equalsIgnoreCase("Map")){
					attrib_value1 = document.createTextNode("");
					Method method=null;
					HashMap tempMap=new HashMap();
					try {
						method = objectClass.getMethod("get"+(String) attr);
						System.out.println("method name "+method);
						tempMap =(HashMap) method.invoke(configBean);
						System.out.println("testmap "+tempMap);
					} catch (SecurityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
					createNodesResursive(attrib_name,tempMap,configBean);
				}else
					attrib_value1 = document.createTextNode((mapWriter.get((String) attr)).toString());
				attrib_name.appendChild(attrib_value1);
				root.appendChild(attrib_name);

			}
		}

	}

	public static HashMap readMapFromXML(String fileName,Node node) {
		NodeList nodeList=null;
		HashMap map=null;
		if(node!=null){
			if(node.hasChildNodes()){
				nodeList=node.getChildNodes();
				try{
					for (node  = ((Node) nodeList).getFirstChild(); node != null;node = node.getNextSibling()) {
						map.put(node.getNodeName(), node.getNodeValue());
					} 
				}catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (DOMException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}	
		}
		return map;
	}

	public static String getDocumentumHome() {
		if (OS_NAME.contains(WINDOWS))  // Windows XP
			return("C:/Documentum");
		else  //LINUX
			return("/usr/Documentum");

	}
}