package com.daffodil.documentumie.scheduleie.configurator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

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

import org.apache.log4j.Logger;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import com.daffodil.documentumie.fileutil.logger.IELogger;
import com.daffodil.documentumie.scheduleie.bean.EScheduleConfigBean;
import com.daffodil.documentumie.scheduleie.bean.IScheduleConfigBean;
import com.daffodil.documentumie.scheduleie.bean.ScheduleImportConfigBean;

import com.daffodil.documentumie.scheduleie.bean.ScheduleExportConfigBean;
import com.daffodil.documentumie.scheduleie.model.Exception.scheduleFileReaderException;
import com.daffodil.documentumie.scheduleie.model.Exception.scheduleFileWriterException;


public class DaffIESchedularConfigurator {

	Document document;
	private static String OS_NAME = System.getProperty("os.name");
//	private static String USER_NAME = System.getProperty("user.name");
	private static String WINDOWS = "Windows XP";

	//static File [] files = null;	
	
	public  Object getSchedules(String type, String fileName) throws scheduleFileReaderException, IOException {
		Logger logger = Logger.getLogger(IELogger.class);
		System.out.println("type.........  "+type);
		//System.out.println("file naem .....  "+fileName);
		logger.info("********* Inside readFile *****");
		logger.info("type.........  "+type +" ****file name .....******"+fileName);	
		ArrayList<Object> listEntries = new ArrayList<Object>();				
		try {			
			document = getXMLDocument(fileName);			
		} catch (Exception e) {
			throw new scheduleFileReaderException("Exception while creating document builder.\n"+ e.getMessage(), e.getCause());
		}		
		Element rootElement = document.getDocumentElement();
		NodeList nodes = rootElement.getChildNodes();
		for ( Node node = ((Node) nodes).getFirstChild(); node != null; node = node.getNextSibling()) {
		
			System.out.println("node  name.. "+node.getNodeName());
			logger.info("node  name.. "+node.getNodeName());
				if (node instanceof Element){
					try {
						System.out.println("set" + node.getNodeName());
						logger.info("set" + node.getNodeName());	
						if(node.getNodeName().equalsIgnoreCase("IScheduleConfigBean"))
						{
							IScheduleConfigBean beanObj = (IScheduleConfigBean) getScheduleConfiguration(node,"IScheduleConfigBean");						
							listEntries.add(beanObj);														
						}
						else if(node.getNodeName().equalsIgnoreCase("EScheduleConfigBean"))
						{
							EScheduleConfigBean beanObj =(EScheduleConfigBean) getScheduleConfiguration(node,"EScheduleConfigBean");							
							listEntries.add(beanObj);							
						}
					} catch (SecurityException e) {
						e.printStackTrace();
					} 
					catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (DOMException e) {
						e.printStackTrace();
					} 
				}			
		}
		System.out.println(listEntries.size());
		return listEntries;
	}
	public Object getScheduleDetails(String type, String fileName,int index) throws scheduleFileReaderException, IOException 
	{
		Logger logger = Logger.getLogger(IELogger.class);
		System.out.println("type.........  "+type);
		System.out.println("file naem .....  "+fileName);
		Class objClass = null;
		Object configBean = null;
		HashMap mapp = null;
		if(type.equalsIgnoreCase("Import"))
		{
		objClass = ScheduleImportConfigBean.class;
		configBean = new ScheduleImportConfigBean();
		}else{
			objClass = ScheduleExportConfigBean.class;
			configBean = new ScheduleExportConfigBean();
		}
		Method m = null; 		
		try {
			document = getXMLDocument(fileName);					
		} catch (IOException e) {
			throw e;
		}
		Element rootElement = document.getDocumentElement();
		NodeList nodes = rootElement.getChildNodes();
		int counter=0;
		for ( Node node = ((Node) nodes).getFirstChild(); node != null; node = node.getNextSibling())
		 {
			if(index>counter){
				counter++;
				continue;
			}
			logger.info("node  name.. "+node.getNodeName());

			if (node instanceof Element){
				try {
					logger.info("set" + node.getNodeName());	
					if(node.getNodeName().equalsIgnoreCase("ScheduleImportConfigBean"))
					{
						for(Node innernode =node.getFirstChild();innernode!= null;innernode = innernode.getNextSibling() )
						{
							//System.out.println("innernode"+innernode.getNodeName()+innernode.getTextContent().toString());
							if(innernode.getNodeName().equalsIgnoreCase("MappedAttributs"))
							{
								mapp = setMappedAttributes(innernode);
								m = objClass.getMethod("setMappedAttributs", HashMap.class);
								m.invoke(configBean, mapp);
								//System.out.println(mapp);
							}
							else{
							m = objClass.getMethod("set" + innernode.getNodeName(), String.class);
							m.invoke(configBean, innernode.getTextContent().toString());
							}
						}
					}
					else if(node.getNodeName().equalsIgnoreCase("ScheduleExportConfigBean"))
					{
						for(Node innernode =node.getFirstChild();innernode!= null;innernode = innernode.getNextSibling() )
						{
							//System.out.println("innernode"+innernode.getNodeName()+innernode.getTextContent().toString());								
							m = objClass.getMethod("set" + innernode.getNodeName(), String.class);
							m.invoke(configBean, innernode.getTextContent().toString());																
						}	
					}
				}
				catch (NoSuchMethodException e) {
					e.printStackTrace();
				}
				catch (DOMException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
			break;			
		 }		
		return configBean;
	}
	
	public  void saveSchedule(Object configBean)
	throws scheduleFileWriterException {
		File userDirectory= null;
		File daffIEDir=null;
		String userDir=null;
		String fileDir=null;
		String fileName = null;
		Class objClass = configBean.getClass();
		String classTag = objClass.getSimpleName();
		String rootTag ="root";
		userDir = new String (getDocumentumHome().replace("\\", "/"));		
		fileDir = new String(userDir+"/DaffIE");
		fileName = new String(fileDir+"/"+objClass.getSimpleName().toString()+".xml");
		System.out.println(fileName);
		
		userDirectory = new File(userDir);
		if (!userDirectory.exists()) 
			userDirectory.mkdir();
		daffIEDir = new File(fileDir);
		if (!daffIEDir.exists()) 
			daffIEDir.mkdir();
		
		HashMap mapWriter=null;
			try {
				mapWriter = getScheduleMap( configBean, objClass);
			} catch (InvocationTargetException e) {
				throw new scheduleFileWriterException("Exception while invoking method on bean.", e.getCause());				}
			catch (IllegalAccessException e){
				throw new scheduleFileWriterException("Exception while invoking method on bean.", e.getCause());
			}	

		try {
			document = getXMLNamespaceDocument( rootTag);
		} catch (ParserConfigurationException e) {
			throw new scheduleFileWriterException("Exception while Write configuration file.\n"+ e.getMessage(), e.getCause());
		}
		Element root = document.getDocumentElement();
		Element classElement = document.createElement(classTag);
		root.appendChild(classElement);
		saveUpdateScheduleDetails(document,classElement,mapWriter,configBean);
		try {
			transformXMLDocument(document,fileName);
		}
		catch (TransformerException e) {
			throw new scheduleFileWriterException("Exception while Write configuration file.\n"+ e.getMessage(), e.getCause());
		}
		catch (FileNotFoundException e){
			throw new scheduleFileWriterException("Exception while Write configuration file.\n"+ e.getMessage(), e.getCause());
		}
	}		

	public static String getDocumentumHome() {
		if (OS_NAME.contains(WINDOWS)){  // Windows XP
			Properties properties=System.getProperties();

			String SceduleFileRootPath = properties.getProperty("user.home");
			System.out.println("SceduleFileRootPath--------------"+SceduleFileRootPath);
			return SceduleFileRootPath;
		}
		else if(OS_NAME.contains("Windows")) // Windows Vista
		{
			Properties properties=System.getProperties();

			String SceduleFileRootPath = properties.getProperty("user.home");
			System.out.println("SceduleFileRootPath--------------"+SceduleFileRootPath);
			return SceduleFileRootPath;
		}
		else  //LINUX
			return("/usr/Documentum");

	}
	public   void saveAndUpdateSchedule(Object configBean) throws scheduleFileWriterException
	{
		String userDir=null;
		String fileDir=null;
		String fileName = null;
		Class objClass = configBean.getClass();
		String classTag = objClass.getSimpleName();
		userDir = new String (getDocumentumHome().replace("\\", "/"));		
		fileDir = new String(userDir+"/DaffIE");
		fileName = new String(fileDir+"/"+objClass.getSimpleName().toString()+".xml");
		System.out.println(fileName);
		File checkExisting = new File(fileName);
		Document doc=null;
		if(checkExisting.exists())
		{
			try {
				HashMap mapWriter =null;				
					try {
						mapWriter = getScheduleMap( configBean, objClass);
					} catch (InvocationTargetException e) {
						throw new scheduleFileWriterException("Exception while invoking method on bean.", e.getCause());				}
					catch (IllegalAccessException e){
						throw new scheduleFileWriterException("Exception while invoking method on bean.", e.getCause());
					}
				
				try {
					doc = getXMLDocument(checkExisting.getAbsolutePath());
				} catch (scheduleFileReaderException e) {
					e.printStackTrace();
				}
				
				Element root = doc.getDocumentElement();
				System.out.println(root);
				Element classNameElement = doc.createElement(classTag);
				root.appendChild(classNameElement);
				//
				saveUpdateScheduleDetails(doc,classNameElement,mapWriter,configBean);
				//System.out.println(staff);
			}
			catch (FileNotFoundException e){
				throw new scheduleFileWriterException("Exception while Write configuration file.\n"+ e.getMessage(), e.getCause());
			}  catch (IOException e) {
				e.printStackTrace();
			}			
			try {
				transformXMLDocument(doc,fileName);
			}
			catch (TransformerException e) {
				throw new scheduleFileWriterException("Exception while Write configuration file.\n"+ e.getMessage(), e.getCause());
			}
			catch (FileNotFoundException e){
				throw new scheduleFileWriterException("Exception while Write configuration file.\n"+ e.getMessage(), e.getCause());
			}
		}
		else{
			saveSchedule(configBean);
		}
		
	}
	private  void saveUpdateScheduleDetails(Document document, Element root,HashMap mapWriter,Object configBean)
	{
		Iterator iterator = mapWriter.keySet().iterator();
		Text attrib_value1=null;
		Class objectClass=configBean.getClass();

		while (iterator.hasNext()) {
			Object attr =  iterator.next();
			System.out.println("attr name update---------------  "+attr);
			if (mapWriter.get(attr) != null) {
				Element attrib_name = document.createElement((String) attr);
				if(((String) attr).equalsIgnoreCase("MappedAttributs")){
					attrib_value1 = document.createTextNode("");
					Method method=null;
					HashMap tempMap=new HashMap();
					try {
						method = objectClass.getMethod("get"+(String) attr);
						tempMap =(HashMap) method.invoke(configBean);
						System.out.println("tempMap     "+tempMap);
					} catch (SecurityException e) {
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					} 
					saveUpdateScheduleDetails(document,attrib_name,tempMap,configBean);
				}else
					attrib_value1 = document.createTextNode((mapWriter.get((String) attr)).toString());
				attrib_name.appendChild(attrib_value1);
				root.appendChild(attrib_name);
			}
		}
	}
	public Document getXMLDocument(String fileName) throws scheduleFileReaderException, IOException 
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			document = builder.parse(new File(fileName ));
			document.getDocumentElement().normalize();
		} catch (ParserConfigurationException e) {
			throw new scheduleFileReaderException("Exception while creating document builder.\n"+ e.getMessage(), e.getCause());
		} catch (SAXException e) {
			throw new scheduleFileReaderException("Exception while Parsing configuration file.\n"+ e.getMessage(), e.getCause());
		} catch (IOException e) {
			throw e;
		}
		return document;
	}
	public Object getScheduleConfiguration(Node node,String type)
	{
		Object beanObj1=null;
		if(type!=null && type.equalsIgnoreCase("IScheduleConfigBean"))
		{
			IScheduleConfigBean	 beanObj = new IScheduleConfigBean();
			for(Node innernode =node.getFirstChild();innernode!= null;innernode = innernode.getNextSibling() )
			{
				//System.out.println("innernode"+innernode.getNodeName()+innernode.getTextContent().toString());
				if(innernode.getNodeName().equalsIgnoreCase("NexSchedule"))
				{
					beanObj.setNexSchedule(innernode.getTextContent().toString());				
				}
				else if(innernode.getNodeName().equalsIgnoreCase("EndDate"))
				{
					beanObj.setEndDate(innernode.getTextContent().toString());
				}
				else if(innernode.getNodeName().equalsIgnoreCase("StartDate"))
				{
					beanObj.setStartDate(innernode.getTextContent().toString());
				}
				else if(innernode.getNodeName().equalsIgnoreCase("Hours"))
				{
					beanObj.setHours(innernode.getTextContent().toString());
				}
				else if(innernode.getNodeName().equalsIgnoreCase("Minute"))
				{
					beanObj.setMinute(innernode.getTextContent().toString());
				}
				else if(innernode.getNodeName().equalsIgnoreCase("ScheduleFor"))
				{
					beanObj.setScheduleFor(innernode.getTextContent().toString());
				}
				else if(innernode.getNodeName().equalsIgnoreCase("Frequency"))
				{
					beanObj.setFrequency(innernode.getTextContent().toString());
				}
				else if(innernode.getNodeName().equalsIgnoreCase("Repeat"))
				{
					beanObj.setRepeat(innernode.getTextContent().toString());
				}
			}
			beanObj1 = beanObj;
		}
		else if(type!=null && type.equalsIgnoreCase("EScheduleConfigBean"))
		{
			EScheduleConfigBean	 beanObj = new EScheduleConfigBean();
			for(Node innernode =node.getFirstChild();innernode!= null;innernode = innernode.getNextSibling() )
			{
				//System.out.println("innernode"+innernode.getNodeName()+innernode.getTextContent().toString());
				if(innernode.getNodeName().equalsIgnoreCase("NexSchedule"))
				{
					beanObj.setNexSchedule(innernode.getTextContent().toString());				
				}
				else if(innernode.getNodeName().equalsIgnoreCase("EndDate"))
				{
					beanObj.setEndDate(innernode.getTextContent().toString());
				}
				else if(innernode.getNodeName().equalsIgnoreCase("StartDate"))
				{
					beanObj.setStartDate(innernode.getTextContent().toString());
				}
				else if(innernode.getNodeName().equalsIgnoreCase("Hours"))
				{
					beanObj.setHours(innernode.getTextContent().toString());
				}
				else if(innernode.getNodeName().equalsIgnoreCase("Minute"))
				{
					beanObj.setMinute(innernode.getTextContent().toString());
				}
				else if(innernode.getNodeName().equalsIgnoreCase("ScheduleFor"))
				{
					beanObj.setScheduleFor(innernode.getTextContent().toString());
				}
				else if(innernode.getNodeName().equalsIgnoreCase("Frequency"))
				{
					beanObj.setFrequency(innernode.getTextContent().toString());
				}
				else if(innernode.getNodeName().equalsIgnoreCase("Repeat"))
				{
					beanObj.setRepeat(innernode.getTextContent().toString());
				}
			}
			beanObj1 = beanObj;
		}
		return beanObj1;
	}
	
	public HashMap setMappedAttributes(Node mapElement)
	{
		HashMap map = new HashMap();
		NodeList mapNodeList=mapElement.getChildNodes();
		for ( Node node1 = ((Node) mapNodeList).getFirstChild(); node1 != null; 
		node1 = node1.getNextSibling()) {
			map.put(node1.getNodeName(), node1.getTextContent());
		}
		return map;
	}
	
	public void transformXMLDocument(Document document,String fileName)  throws scheduleFileWriterException, FileNotFoundException, TransformerException
	{
		TransformerFactory xformFactory = TransformerFactory.newInstance();
		Transformer idTransform;	
		idTransform = xformFactory.newTransformer();
		Source input = new DOMSource(document);
		Result output = new StreamResult(new FileOutputStream(fileName));
		idTransform.transform(input, output);				
	}
	public Document getXMLNamespaceDocument(String rootTag) throws ParserConfigurationException
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder;
		builder = factory.newDocumentBuilder();
		DOMImplementation impl = builder.getDOMImplementation();
		document = impl.createDocument(null, rootTag, null);
		return document;
	}
	public HashMap getScheduleMap(Object configBean,Class objClass)  throws scheduleFileWriterException, IllegalArgumentException, IllegalAccessException, InvocationTargetException
	{
		HashMap mapWriter = new HashMap();
		Method[] m = objClass.getMethods();
		for (int i = 0; i < m.length; i++) {
			String name = m[i].getName();
		
				if (name.startsWith("get") && name != "getClass") {
					Object val;
					val = m[i].invoke(configBean);
					mapWriter.put(name.substring(3), val);
				}
		}		
		return mapWriter;
	}	
	public List getAllScheduleDetails(String type, String fileName) throws scheduleFileReaderException, IOException 
	{
		List lstSchedules = new ArrayList();
		Logger logger = Logger.getLogger(IELogger.class);
		/*System.out.println("type.........  "+type);
		System.out.println("file naem .....  "+fileName);*/
		Class objClass = null;
		Object configBean = null;
		HashMap mapp = null;
		if(type.equalsIgnoreCase("Import"))
		{
		objClass = ScheduleImportConfigBean.class;
		configBean = new ScheduleImportConfigBean();
		}else{
			objClass = ScheduleExportConfigBean.class;
			configBean = new ScheduleExportConfigBean();
		}
		Method m = null; 		
		try {
			System.out.println("File Name:"+fileName);
			document = getXMLDocument(fileName);					
		} catch (IOException e) {
			throw e;
		}
		Element rootElement = document.getDocumentElement();
		NodeList nodes = rootElement.getChildNodes();
		int counter=0;
		for ( Node node = ((Node) nodes).getFirstChild(); node != null; node = node.getNextSibling())
		 {
			
			logger.info("node  name.. "+node.getNodeName());
			System.out.println("getAllScheduleDetails "+node.getNodeName());
			if (node instanceof Element){
				try {
					logger.info("set" + node.getNodeName());	
					System.out.println("set" + node.getNodeName());
					if(node.getNodeName().equalsIgnoreCase("ScheduleImportConfigBean"))
					{
						configBean = new ScheduleImportConfigBean();
						for(Node innernode =node.getFirstChild();innernode!= null;innernode = innernode.getNextSibling() )
						{
							
							//System.out.println("innernode"+innernode.getNodeName()+innernode.getTextContent().toString());
							if(innernode.getNodeName().equalsIgnoreCase("MappedAttributs"))
							{
								mapp = setMappedAttributes(innernode);
								m = objClass.getMethod("setMappedAttributs", HashMap.class);
								m.invoke(configBean, mapp);
								//System.out.println(mapp);
							}
							else{
							m = objClass.getMethod("set" + innernode.getNodeName(), String.class);
							m.invoke(configBean, innernode.getTextContent().toString());
							}
						}
						lstSchedules.add(configBean);
					}
					else if(node.getNodeName().equalsIgnoreCase("ScheduleExportConfigBean"))
					{
						
						configBean = new ScheduleExportConfigBean();
						for(Node innernode =node.getFirstChild();innernode!= null;innernode = innernode.getNextSibling() )
						{
							if(!innernode.getNodeName().equalsIgnoreCase("#text"))
							{
							//System.out.println(innernode.getNodeName()+innernode.getTextContent().toString());			
							m = objClass.getMethod("set" + innernode.getNodeName(), String.class);
							m.invoke(configBean, innernode.getTextContent().toString());
							}
						}	
						lstSchedules.add(configBean);
					}
				}
				catch (NoSuchMethodException e) {
					e.printStackTrace();
				}
				catch (DOMException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
			
		 }		
		System.out.println(" getAllScheduleDetails "+lstSchedules.size());
		return lstSchedules;
	}
}
