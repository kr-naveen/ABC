package com.daffodil.documentumie.dctm.apiimpl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.daffodil.documentumie.businessoperationprocessorbean.ImportProcessorBean;
import com.daffodil.documentumie.dctm.SessionHandler;
import com.daffodil.documentumie.dctm.api.CSServices;
import com.daffodil.documentumie.dctm.exception.DDfException;
import com.daffodil.documentumie.filehandler.ContentFileHandler;
import com.daffodil.documentumie.fileutil.logger.IELogger;
import com.daffodil.documentumie.fileutil.properties.PropertyFileLoader;
import com.documentum.com.DfClientX;
import com.documentum.com.IDfClientX;
import com.documentum.fc.client.IDfACL;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfDocbaseMap;
import com.documentum.fc.client.IDfDocument;
import com.documentum.fc.client.IDfFolder;
import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.client.IDfType;
import com.documentum.fc.client.IDfValidator;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.DfList;
import com.documentum.fc.common.DfTime;
import com.documentum.fc.common.IDfAttr;
import com.documentum.fc.common.IDfId;
import com.documentum.fc.common.IDfList;
import com.documentum.fc.common.IDfTime;

public class CSServicesProvider implements CSServices {

	private SessionHandler mSessionHandler;

	private ArrayList docBaseList;

	private String[] loginInfo = new String[3];

	private ContentFileHandler contentFH;

	public CSServicesProvider() {
		mSessionHandler = new SessionHandler();
	}
	///private IELogger logger=new IELogger();
	public void login(String repoName, String userName, String password,
			String domain) throws DDfException {
		loginInfo[0] = repoName;
		loginInfo[1] = userName;
		loginInfo[2] = password;
		try {
			mSessionHandler.login(repoName, userName, password, domain);
		} catch (DDfException e) {
			throw new DDfException(e.getMessage(), e.getCause());
		}
	}

	public Object createObject(String r_object_type) throws DDfException {
		IDfDocument document = null;
		try {
			document = (IDfDocument) mSessionHandler.getIDfSession().newObject(
					r_object_type);

		} catch (DfException e) {
			throw new DDfException(
					"Exception while creating new object for object type"
							+ r_object_type + ": " + e.getMessage()
							+ e.getCause());
		}
		return document;
	}

	public IDfId getObjectIdByQual(String strQual) throws DDfException {
		IDfId id;
		if (strQual.equals("000")) {
			return new DfId("000");
		}
		try {
			id = mSessionHandler.getIDfSession().getIdByQualification(strQual);
			return id;
		} catch (DfException e) {
			throw new DDfException("Exception while getting object id. PARAM: "
					+ strQual + " " + e.getMessage() + e.getCause());
		}

	}
	
	public Object getObjectByObjectNamePath(String objName, String r_folder_path) {
		return null;
	}

	public List getAvailableRepositories() throws DDfException {

		if (docBaseList != null) {
			return docBaseList;
		} else
			try {
				docBaseList = new ArrayList();
				IDfDocbaseMap docBaseMap;
				docBaseMap = mSessionHandler.getClient().getDocbaseMap();
				int noOfDocBase = docBaseMap.getDocbaseCount();
				for (int i = 0; i < noOfDocBase; i++) {
					docBaseList.add(docBaseMap.getDocbaseName(i));
				}
			} catch (DfException e) {
				throw new DDfException(
						"Exception while getting repositories from docbase "
								+ e.getMessage() + e.getCause());
			}
		return docBaseList;
	}

	public List getAvailableObjecTypes() throws DDfException {

		List objectTypeList = new ArrayList();
		List obtype = new ArrayList();
		// String dql = "Select distinct name from dm_type ORDER BY name";
		String superObjectTypeName = PropertyFileLoader
				.loadUtilityConfigPropertyFile().getProperty(
						"SUPER_OBJECT_TYPE_NAME");

		String dql = null;
		if (superObjectTypeName == null) {
			//System.out.println("superObjectTypeName--->"+superObjectTypeName);
			//logger.writeLog("superObjectTypeName--->"+superObjectTypeName, logger.INFO);
			dql = "Select distinct name from dm_type ORDER BY name";
		} else {
			//System.out.println("ELSE CONDITION   superObjectTypeName--->"+superObjectTypeName);
			//logger.writeLog("ELSE CONDITION   superObjectTypeName--->"+superObjectTypeName,logger.INFO);
			dql = "Select distinct name from dm_type where super_name='"
					+ superObjectTypeName + "' ORDER BY name";
		}
		try {
			// dql="Select distinct name from dm_type where super_name like 'obo_%' ORDER BY name";
			objectTypeList = executeDQL(dql);
			//System.out.println("objectTypeList--->"+objectTypeList);
			//logger.writeLog("objectTypeList--->"+objectTypeList,logger.INFO);
			
		} catch (DDfException e) {
			throw new DDfException("Exception while getting object type list "
					+ e.getMessage() + e.getCause());
		}
		for (int i = 0; i < objectTypeList.size(); i++) {
			obtype.add(((Object[]) objectTypeList.get(i))[0]);
		}
		return obtype;
	}

	public List getAttributes(String objectType) throws DDfException {
		List attributeList = new ArrayList();
		List attList = new ArrayList();
		// String dql = "select distinct attr_name from dmi_dd_attr_info where
		// type_name='" + objectType + "' ORDER BY attr_name";
		// String
		// dql="SELECT attr_name FROM dmi_dd_attr_info WHERE type_name = '"+objectType+"' AND attr_name NOT IN (SELECT attr_name FROM dmi_dd_attr_info WHERE type_name = 'dm_document')";
		String dql = "select distinct attr_name from dmi_dd_attr_info where type_name='"
				+ objectType + "'  ORDER BY attr_name";
		// String dql =
		// "select distinct attr_name from dmi_dd_attr_info where type_name='" +
		// objectType +
		// "' and attr_name not like 'a_%' and attr_name not like 'i_%' and attr_name not like 'r_%' ORDER BY attr_name";
		try {
			attributeList = executeDQL(dql);
		} catch (DDfException e) {
			throw new DDfException(
					"Exception while getting attribute list of object type "
							+ objectType + " " + e.getMessage() + e.getCause());
		}

		for (int i = 0; i < attributeList.size(); i++) {
			attList.add(((Object[]) attributeList.get(i))[0]);
		}
		return attList;
	}

	public List getAllDm_SysObjectObjectTypes() {
		return null;
	}

	public List executeDQL(String dQL) throws DDfException {
		List list = new ArrayList();
		IDfSession session = mSessionHandler.getIDfSession();
		IDfCollection collection = null; // Collection for the result

		IDfQuery q = getClientX().getQuery(); // Create query object
		q.setDQL(dQL);

		try {
			collection = q.execute(session, IDfQuery.DF_READ_QUERY);

		} catch (DfException e) {
			e.printStackTrace();
		}

		try {

			while (collection.next()) {
				int counter = collection.getAttrCount();
				// add all values in list inplace of object array
				// String val = null;
				for (int i = 0; i < counter; i++) {
					Object[] values = new Object[counter];
					IDfAttr attr = collection.getAttr(i);
					String attrName = attr.getName();

					if (attr.getDataType() == attr.DM_BOOLEAN) {

						values[i] = collection.getBoolean(attrName);
					}
					if (attr.getDataType() == attr.DM_DOUBLE) {
						values[i] = collection.getDouble(attrName);
					}
					if (attr.getDataType() == attr.DM_ID) {
						values[i] = collection.getId(attrName);
					}
					if (attr.getDataType() == attr.DM_INTEGER) {
						values[i] = collection.getInt(attrName);
					}
					if (attr.getDataType() == attr.DM_STRING) {
						values[i] = collection.getString(attrName);
					}
					if (attr.getDataType() == attr.DM_TIME) {
						values[i] = collection.getTime(attrName);
					}
					if (attr.getDataType() == attr.DM_UNDEFINED) {

					}
					list.add(values);
				}
			}
			collection.close();
		} catch (DfException e) {
			throw new DDfException("Exception while executing DQL "
					+ e.getMessage() + e.getCause());
		}
		return list;
	}
	/*//Added By Bablu
		public IDfSysObject getObjectById(IDfId obj)
		{
			IDfSession session = null;
			try {
				session = getSession();
			} catch (DDfException e) {
				
				e.printStackTrace();
			}
			IDfSysObject sysobj = null; 
			try {
				sysobj =(IDfSysObject) session.getObject(obj);
			} catch (DfException e) {
				
				e.printStackTrace();
			}
			return sysobj;
		}*/
	public List getAllVersion(Object obj) throws DDfException {
		IDfClientX clientx = new DfClientX();
		IDfId id = clientx.getId((String) obj);
		IDfSysObject doc = null;
		try {
			doc = (IDfSysObject) mSessionHandler.getIDfSession().getObject(id);
			List versionList = new ArrayList();

			IDfCollection versions = doc.getVersions(null);
			while (versions.next()) {
				int counter = versions.getAttrCount();

				HashMap objectMap = new HashMap();
				for (int i = 0; i < counter; i++) {

					IDfAttr attr = versions.getAttr(i);
					String attrName = attr.getName();

					objectMap.put(attrName, versions.getString(attrName));

				}

				versionList.add(objectMap);
			}
			// List versList = sortVersions(versionList,doc);
			// List versList = versionList;
			return versionList;
		} catch (DfException e) {
			throw new DDfException("Exception while getting All Version of object "
		+ e.getMessage() + e.getCause());
		}
	}

	public String getFolderPath(Object objId) throws DDfException {
		IDfClientX clientx = new DfClientX();
		IDfId id = clientx.getId((String) objId);
		IDfSysObject docSysObject = null;
		try {
			docSysObject = (IDfSysObject) mSessionHandler.getIDfSession()
					.getObject(id);
			IDfFolder idfolder = (IDfFolder) mSessionHandler.getIDfSession()
					.getObject(docSysObject.getFolderId(0));
			if (idfolder != null) {
				String folderPath = idfolder.getFolderPath(0);
				folderPath = folderPath + "/";
				return folderPath;
			}
		} catch (DfException e) {
			throw new DDfException(
					"Exception while geting folder hierarchy form repositor for export process "
							+ e.getMessage(), e.getCause());
		}
		return null;
	}

	public int getRowCount(String dQL) {
		return 0;
	}

	public String getDocumentumExtension(String dosExtension)
			throws DDfException {
		dosExtension = dosExtension.toLowerCase();
		if (dosExtension.equals("tiff")) {
			dosExtension = "tif";
		}
		List list;
		String dql = "select name from dm_format where dos_extension = '"
				+ dosExtension + "'";
		try {
			list = executeDQL(dql);
		} catch (DDfException e) {
			throw new DDfException(
					"Exception while getting documentum extension for "
							+ dosExtension + " " + e.getMessage()
							+ e.getCause());
		}
		String ext = null;
		if (list.size() > 0) {
			ext = (String) (((Object[]) list.get(0))[0]);
		}
		return ext;
	}

	public String getDOSExtension(String documentumExtension)
			throws DDfException {
		String dql = "select dos_extension from dm_format where name  = '"
				+ documentumExtension + "'";
		List dosEx;
		dosEx = executeDQL(dql);

		String ext = null;
		if (dosEx.size() > 0) {
			ext = (String) (((Object[]) dosEx.get(0))[0]);
		}
		return ext;

	}

	public String getDosExtensionById(Object objId) throws DDfException {
		IDfClientX clientx = new DfClientX();
		IDfId id = clientx.getId((String) objId);
		IDfSysObject docSysObject;
		String dosExtension;
		String documentumExtension = null;
		try {
			docSysObject = (IDfSysObject) mSessionHandler.getIDfSession()
					.getObject(id);

			documentumExtension = docSysObject.getContentType();
			dosExtension = getDOSExtension(documentumExtension);
		} catch (DfException e) {
			throw new DDfException(
					"Exception while getting DOS extension  for "
							+ documentumExtension + " " + e.getMessage()
							+ e.getCause());
		}
		return dosExtension;
	}

	public List getRenditions(Object id) throws DDfException {

		List renditionList = new ArrayList();
		IDfSysObject docSysObject;
		try {
			IDfClientX clientx = new DfClientX();
			IDfId objId = clientx.getId((String) id);
			docSysObject = (IDfSysObject) mSessionHandler.getIDfSession()
					.getObject(objId);
			IDfCollection renditionObjects = docSysObject.getRenditions(null);

			renditionObjects.next();

			while (renditionObjects.next()) {

				String renditionExtension = renditionObjects
						.getString("full_format");
				renditionList.add(renditionExtension);
			}
		} catch (DfException e) {
			throw new DDfException("Exception while getting Renditions "
					+ e.getMessage() + e.getCause());
		}
		return renditionList;
	}

	// method to export any content from repository to destination
	public void exportObject(Object objId, String tempDir, String fileName,
			String destinationPath) throws DDfException {
		try {
			//System.out.println("TempDirPath" + tempDir);
			//System.out.println("fileName" + fileName);
			
			//logger.writeLog("TempDirPath" + tempDir,logger.INFO);
			//logger.writeLog("fileName" + fileName,logger.INFO);
			
			IDfClientX clientx = new DfClientX();
			IDfId id = clientx.getId((String) objId);
			IDfSysObject docSysObject = null;
			docSysObject = (IDfSysObject) mSessionHandler.getIDfSession()
					.getObject(id);
			if (!docSysObject.isCheckedOut()) {
//				if (docSysObject.getContentSize() != 0
//						&& docSysObject.getString("a_content_type") != null) {
					docSysObject.getFile(tempDir + File.separator + fileName);
					//System.out.println(tempDir + File.separator + fileName);
					//logger.writeLog(tempDir + File.separator + fileName, logger.INFO);
					contentFH.saveFile(tempDir + File.separator + fileName,
							destinationPath);
					contentFH.deleteFile(tempDir + File.separator + fileName);
			//	}
			}

		} catch (DfException e) {
			throw new DDfException("object is checked out " + e.getMessage());
		}

	}

	private SessionHandler getSessionHandler() {
		return mSessionHandler;
	}

	public IDfClientX getClientX() {
		IDfClientX clientx = new DfClientX();
		return clientx;
	}

	public Object[] validateObject(Map metadataMap, String objType)
			throws DDfException {
		//System.out.println("CSServicesProvider.validateObject()");
		IDfPersistentObject pObj = null;
		IDfValidator v = null;
		String validMsg = "";
		boolean flag = true;
		try {
			pObj = mSessionHandler.getIDfSession().newObject(objType);

			v = pObj.getValidator();
			v.setTimePattern(IDfTime.DF_TIME_PATTERN18);

			int count = metadataMap.size();
			String innerMsg = null;
			String attrName = null;
			String value = null;
			IDfList valueList = new DfList();
			for (Iterator iterator = metadataMap.entrySet().iterator(); iterator
					.hasNext();) {
				Map.Entry entry = (Map.Entry) iterator.next();
				attrName = entry.getKey().toString();
				value = metadataMap.get(attrName).toString();
				valueList.removeAll();
				attrName = attrName.toLowerCase();
				if ("set_file__".equalsIgnoreCase(attrName)) {
					continue;
				}
				if ("acl_name".equalsIgnoreCase(attrName)) {
					continue;
				}
				if ("processorError".equalsIgnoreCase(attrName)) {
					continue;
				}
				if ("PostProcessorError".equalsIgnoreCase(attrName)) {
					continue;
				}

				if ("file_source_location__".equalsIgnoreCase(attrName)) {
					continue;
				}

				if (attrName.startsWith("r_") || attrName.startsWith("i_")) {
					continue;
				}
				try {
					if (pObj.isAttrRepeating(attrName)) {
						int start = 0;
						int end = 0;
						int len = 0;
						if (value != null) {
							len = value.length();
						}
						String repeatingValue = null;
						one: for (int i = 0; i < len; i++) {
							if (value.contains("#")) {
								end = value.indexOf('#');
								repeatingValue = value.substring(start, end);
								valueList.appendString(repeatingValue);
							}
							value = value.substring(end + 1);
							if (!value.contains("#")) {
								repeatingValue = value;
								valueList.appendString(repeatingValue);
								break one;
							}
						}
					} else {
						valueList.appendString(value);
					}

					v.validateAttrRules(attrName, valueList, null);

				} catch (DfException e) {
					innerMsg = innerMsg + e.getMessage();
					// throw new DDfException(validMsg);
				}
				if (innerMsg != null && !"".equals(innerMsg)) {
					innerMsg = innerMsg + " for attribute " + attrName + ";";
					validMsg = validMsg + innerMsg;
				}
			}
			try {
				v.validateAllAttrRules(null, false);
			} catch (DfException e) {
				validMsg = validMsg + " " + e.getMessage();
				// throw new DDfException(validMsg);
			}

			try {
				v.validateAll(null, false);
			} catch (DfException e) {
				validMsg = validMsg + " " + e.getMessage();
				// throw new DDfException(validMsg);
			}

			try {
				v.validateAllObjRules(null);
			} catch (DfException e) {
				validMsg = validMsg + " " + e.getMessage();
				// throw new DDfException(validMsg);
			}

		} catch (DfException e) {
			validMsg = validMsg + e.getMessage();
			// throw new DDfException(validMsg);
		}

		if (validMsg != null && !"".equals(validMsg)) {
			flag = false;
		}
		//System.out.println("validMsg : " + validMsg);
		return new Object[] { flag, validMsg };
	}

	public Boolean checkCabinet(String cabinetName) throws DDfException {
		IDfFolder myCabinet;
		try {
			myCabinet = mSessionHandler.getIDfSession().getFolderByPath(
					"/" + cabinetName);

			if (myCabinet == null) {
				return false;
			}
		} catch (DfException e) {
			throw new DDfException(
					"Exception while checking cabinet existance "
							+ e.getMessage() + e.getCause());
		}
		return true;
	}

	public void createNewCabinet(String cabinetName) throws DDfException {
		IDfFolder myCabinet;
		try {
			myCabinet = (IDfFolder) mSessionHandler.getIDfSession().newObject(
					"dm_cabinet");

			myCabinet.setObjectName(cabinetName);
			myCabinet.save();
		} catch (DfException e) {
			throw new DDfException("Exception while creating new cabinet "
					+ e.getMessage() + e.getCause());
		}
	}

	public Boolean checkFolder(String folderpath) throws DDfException {
		IDfSysObject sysObj;
		try {
			sysObj = (IDfSysObject) mSessionHandler.getIDfSession()
					.getObjectByPath(folderpath);

			if (sysObj == null) {
				return false;
			}
		} catch (DfException e) {
			throw new DDfException("Exception while checking folder existance "
					+ e.getMessage() + e.getCause());
		}
		return true;
	}

	public void createNewFolder(String path, String folderName)
			throws DDfException {
		IDfFolder folder;
		try {
			folder = (IDfFolder) mSessionHandler.getIDfSession().newObject(
					"dm_folder");
			folder.setObjectName(folderName);
			folder.link(path);
			folder.save();
		} catch (DfException e) {
			throw new DDfException("Exception while creating new folder "
					+ folderName + " " + e.getMessage() + e.getCause());
		}
	}

	public int validInputHeader(String subType) throws DDfException {
		int objectType = 1;
		IDfType type;

		try {
			while (!"dm_persistentObject".equalsIgnoreCase(subType)) {
				if ("dm_folder".equalsIgnoreCase(subType)) {
					objectType = 2;
					break;
				} else if ("dm_sysobject".equalsIgnoreCase(subType)) {
					objectType = 3;
					break;
				} else if (subType == null || "".equals(subType)) {
					break;
				}
				type = getSessionHandler().getIDfSession().getType(subType);
				String typeStr = type.getSuperName();
				subType = typeStr;
			}
			//System.out.println("objectTypeHierarchy : " + objectType);
			//logger.writeLog("objectTypeHierarchy : " + objectType, logger.INFO);
			return objectType;
		} catch (DfException e) {
			throw new DDfException("Exception while checking object hierarchy "
					+ e.getMessage() + e.getCause());
		}
	}

	public String importSuperThanSys(ImportProcessorBean importProcessorBean)
			throws DDfException {
		try {
			IDfPersistentObject persistentObject = (IDfPersistentObject) mSessionHandler
					.getIDfSession().newObject(
							importProcessorBean.getObjectType());
			persistentObject.save();
			updateAttribute(importProcessorBean.getMetadataMap(),
					persistentObject);
			persistentObject.save();
			return persistentObject.getObjectId().toString();
		} catch (DfException e) {
			throw new DDfException(
					"Exception in importsuperThanSys object type "
							+ e.getMessage() + e.getCause());
		}
	}

	public String importFolder(ImportProcessorBean importProcessorBean)
			throws DDfException {
		try {
			IDfPersistentObject persistentObject = (IDfPersistentObject) mSessionHandler
					.getIDfSession().newObject(
							importProcessorBean.getObjectType());
			persistentObject.save();
			updateAttribute(importProcessorBean.getMetadataMap(),
					persistentObject);
			persistentObject.save();
			return persistentObject.getObjectId().toString();
		} catch (DfException e) {
			throw new DDfException(
					"Exception in importsuperThanSys object type "
							+ e.getMessage() + e.getCause());
		}
	}

	private String createNewSysOrChildObj(
			ImportProcessorBean importProcessorBean) throws DDfException {
		//System.out.println("CSServicesProvider.createNewSysOrChildObj()");
		//logger.writeLog("CSServicesProvider.createNewSysOrChildObj()", logger.INFO);
		String srcPath = (String) importProcessorBean.getMetadataMap().get("file_source_location__");
		String objectId = null;
		try {
			IDfSysObject sysObject = (IDfSysObject) mSessionHandler
					.getIDfSession().newObject(
							importProcessorBean.getObjectType());
			//Start Of Old code Commented By Naveen_20-05-2016
		/*	updateSysObject(
					(String) importProcessorBean.getMetadataMap().get(
							"r_folder_path"),
					importProcessorBean.getMetadataMap(), srcPath, sysObject,
					false, importProcessorBean.isSupportingDoc());
			System.out.println("========="
					+ (String) importProcessorBean.getMetadataMap().get(
							"r_folder_path"));*/
			//End Of Old Code Commented By Naveen_20-05-2016
			//Start of New Code Added By Naveen_20-05-2016
			//Start xyz1 by naveen
			boolean check=CheckupdateSysObject(
			importProcessorBean.getMetadataMap(), srcPath, sysObject);
			//System.out.println("--This is Middle"+check);
			//logger.writeLog("--This is Middle"+check, logger.INFO);
			
			if(check==false){
			updateSysObject(
					(String) importProcessorBean.getMetadataMap().get(
							"r_folder_path"),
					importProcessorBean.getMetadataMap(), srcPath, sysObject,
					false, importProcessorBean.isSupportingDoc());
			//System.out.println("---This is Naveen Stop");
			//System.out.println("========="+ (String) importProcessorBean.getMetadataMap().get("r_folder_path"));
			//logger.writeLog("========="+ (String) importProcessorBean.getMetadataMap().get("r_folder_path"),logger.INFO);
			}
			else
			{
				updateVersionSysObject((String) importProcessorBean.getMetadataMap().get(
						"r_folder_path"),
				importProcessorBean.getMetadataMap(), srcPath, sysObject,
				false, importProcessorBean.isSupportingDoc());
		//System.out.println("---This is Naveen Stop");
		//System.out.println("========="+ (String) importProcessorBean.getMetadataMap().get("r_folder_path"));
				//logger.writeLog("========="+ (String) importProcessorBean.getMetadataMap().get("r_folder_path"), logger.INFO);
			}
			//End of New Code Added By Naveen_20-05-2016

			objectId = sysObject.getObjectId().toString();
		} catch (DfException e) {
			throw new DDfException("Exception while creating new sys object "
					+ importProcessorBean.getObjectType() + "" + e.getMessage()
					+ e.getCause());
		}
		//System.out.println("object id in cs : " + objectId);
		//logger.writeLog("object id in cs : " + objectId, logger.INFO);
		return objectId;
	}

	//Start of New Methods Added By Naveen_20-05-2016
	private boolean CheckupdateSysObject(Map map,String srcPath, IDfSysObject sysObject) throws DDfException {
		String objectId = null;
		try {
			//System.out.println((String) map.get("object_name"));
			sysObject.setObjectName((String) map.get("object_name"));
			String extn = null;

			if (srcPath != null && !"".equals(srcPath.trim())) {
				extn = getDocumentumExtension(srcPath.substring((srcPath
						.lastIndexOf(".") + 1)));
           // System.out.println(extn);
            //System.out.println((String) sysObject.getType().getName());
           // System.out.println((String) map.get("r_folder_path"));
			sysObject.setFileEx(srcPath, extn, 0, null);
			List list;
			String dql = "select * from "+sysObject.getType().getName()+" where object_name= '"+(String) map.get("object_name")+"'  "
					+ "and a_content_type='"+extn+"' and folder('"+(String) map.get("r_folder_path")+"')";
			try {
				list = executeDQL(dql);
			} catch (DDfException e) {
				throw new DDfException(
						"Exception while getting documentum extension for "
								+ e.getMessage()
								+ e.getCause());
			}
			
			if (list.size() > 0) {
				return true;
			}
			}

		} catch (DfException e) {
			throw new DDfException(e.getMessage() + " " + e.getCause());
		}
		return false;
	}
	private String updateVersionSysObject(String destFolderPath, Map map,
			String srcPath, IDfSysObject sysObject, boolean updateExisting,
			boolean isSupportingDoc) throws DDfException {
		String objectId = null;
		try {
			sysObject.setObjectName((String) map.get("object_name"));
			String extn = null;

			if (srcPath != null && !"".equals(srcPath.trim())) {
				extn = getDocumentumExtension(srcPath.substring((srcPath
						.lastIndexOf(".") + 1)));
				sysObject.setContentType(extn);

				sysObject.setFileEx(srcPath, extn, 0, null);
				if (!updateExisting) {
					sysObject.link(destFolderPath);
				}
				/*
				 * IDfACL aclObj =
				 * getSessionHandler().getIDfSession().getACL(getSessionHandler
				 * ().getIDfSession().getDocbaseOwnerName(), (String)
				 * map.get("acl_name")); sysObject.setACL(aclObj);
				 */
				/*
				 * sysObject.save(); System.out.println("aftre first save ");
				 */
				//System.out.println("In updateSysObject of CS Service Provider"+ sysObject);
				updateAttribute(map, sysObject);
				modifyFile(map,srcPath,sysObject, extn);
				
			}

		} catch (DfException e) {
			throw new DDfException(e.getMessage() + " " + e.getCause());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return objectId;
	}
	  private boolean modifyFile(Map map,String srcPath,IDfSysObject sysObject,String extn) throws Exception {
		  IDfSession session = null;
			
			try {
				session = mSessionHandler.getIDfSession();
			} catch (DDfException e) {
				throw new DDfException(
						"Exception while geting attributes from Rpository fro export process. "
								+ e.getMessage() + e.getCause());
			}
		  IDfCollection collection = null; // Collection for the result

			IDfQuery q = getClientX().getQuery(); // Create query object
			String dql = "select * from "+sysObject.getType().getName()+" where object_name= '"+(String) map.get("object_name")+"'  "
					+ "and a_content_type='"+extn+"' and folder('"+(String) map.get("r_folder_path")+"')";
			q.setDQL(dql);

			try {
				collection = q.execute(session, IDfQuery.DF_READ_QUERY);
			} catch (DfException e) {
				throw new DDfException(
						"Exception while geting attributes from Rpository fro export process. "
								+ e.getMessage() + e.getCause());
			}
            collection.next();
			IDfSysObject document = (IDfSysObject)session.getObject(collection.getId("r_object_id"));
	        document.checkout();
	        document.checkout();
	        document.setFile(srcPath);	      
	        document.checkin(false, null); // When a null version label is provided,
	                                    // DFC automatically gives the new version
	                                    // an implicit version label (1.1, 1.2, etc.)
	                                 // and the symbolic label "CURRENT". 
	        return true;
	    }
	//End of New Methods Added By Naveen_20-05-2016
	private String updateSysObject(String destFolderPath, Map map,
			String srcPath, IDfSysObject sysObject, boolean updateExisting,
			boolean isSupportingDoc) throws DDfException {
		String objectId = null;
		try {
			sysObject.setObjectName((String) map.get("object_name"));
			String extn = null;

			if (srcPath != null && !"".equals(srcPath.trim())) {
				extn = getDocumentumExtension(srcPath.substring((srcPath
						.lastIndexOf(".") + 1)));
				sysObject.setContentType(extn);

				sysObject.setFileEx(srcPath, extn, 0, null);
				if (!updateExisting) {
					sysObject.link(destFolderPath);
				}
				/*
				 * IDfACL aclObj =
				 * getSessionHandler().getIDfSession().getACL(getSessionHandler
				 * ().getIDfSession().getDocbaseOwnerName(), (String)
				 * map.get("acl_name")); sysObject.setACL(aclObj);
				 */
				/*
				 * sysObject.save(); System.out.println("aftre first save ");
				 */
				//System.out.println("In updateSysObject of CS Service Provider"+ sysObject);
				updateAttribute(map, sysObject);
				sysObject.save();
			}

		} catch (DfException e) {
			throw new DDfException(e.getMessage() + " " + e.getCause());
		}
		return objectId;
	}

	private void updateAttribute(Map map, IDfPersistentObject perObj)
			throws DDfException {
		//System.out.println("CSServicesProvider.updateAttribute()");
		//System.out.println("Map size is:" + map.size());
		for (Iterator iterator = map.entrySet().iterator(); iterator.hasNext();) {
			Map.Entry entry = (Map.Entry) iterator.next();
			if (entry.getKey() != null) { // if column name is not null

				String attribute = entry.getKey().toString();
				// System.out.println("Aattribute:-"+attribute+": Value:"+
				// map.get(attribute)+":");
				String value = ((String) map.get(attribute));
				// System.out.println("Aattribute:-"+attribute+": Value:"+value+":");
				attribute = (attribute.toLowerCase()).trim();
				try {
					if ("set_file__".equalsIgnoreCase(attribute)) {
						continue;
					}
					if ("acl_name".equalsIgnoreCase(attribute)) {
						continue;
					}
					if ("processorerror".equalsIgnoreCase(attribute)) {
						continue;
					}
					if ("PostProcessorError".equalsIgnoreCase(attribute)) {
						continue;
					}

					if ("file_source_location__".equalsIgnoreCase(attribute)) {
						continue;
					}

					if (attribute.startsWith("r_")
							|| attribute.startsWith("i_")) {
						continue;
					}
					if ("is_minor__".equalsIgnoreCase(attribute)) {
						continue;
					}

					if (IDfType.DF_TIME == perObj.getAttrDataType(attribute)) {
						perObj.setTime(attribute, new DfTime(value,
								IDfTime.DF_TIME_PATTERN18));
					} else {
						// work for repeating attribute
						if (perObj.isAttrRepeating(attribute)) {
							int start = 0;
							int end = 0;
							int len = 0;
							if (value != null) {
								len = value.length();
							}
							String repeatingValue = null;
							int valueIndex = 0;
							one: for (int i = 0; i < len; i++) {
								if (value.contains("#")) {
									end = value.indexOf('#');
									repeatingValue = value
											.substring(start, end);
									perObj.setRepeatingString(attribute,
											valueIndex++, repeatingValue);
								}
								value = value.substring(end + 1);
								if (!value.contains("#")) {
									repeatingValue = value;

									perObj.setRepeatingString(attribute,
											valueIndex, repeatingValue);
									break one;
								}
							}
						} else {
							perObj.setString(attribute, value); // update
						}

					}
				} catch (DfException e) {
					throw new DDfException(
							"Exception during metadata import process "
									+ e.getMessage() + e.getCause());
				}

			}
		}
	}

	public int getDataType(String attrName, String objType) throws DDfException {
		IDfSession session = mSessionHandler.getIDfSession();
		int attrtype = 0;
		try {
			IDfType objectType = session.getType(objType);
			attrtype = objectType.getTypeAttrDataType(attrName);
		} catch (DfException e) {
			throw new DDfException(e.getMessage() + e.getCause());
		}

		switch (attrtype) {
		case IDfAttr.DM_BOOLEAN:
			return BOOLEAN;
		case IDfAttr.DM_DOUBLE:
			return DOUBLE;
		case IDfAttr.DM_ID:
			return DMID;
		case IDfAttr.DM_INTEGER:
			return INTEGER;
		case IDfAttr.DM_STRING:
			return STRING;
		case IDfAttr.DM_TIME:
			return TIME;
		case IDfAttr.DM_UNDEFINED:
			return UNDEFINED;
		}
		return attrtype;
	}
	//6/15/2016 start naveen editing place
	public List executeDQLObject(String dqlString, String objectType)
				throws DDfException {
		
			List list = new ArrayList();
			IDfSession session = null;
			IDfType type = null;
			IDfCollection mcollection=null;
			try {
				session = mSessionHandler.getIDfSession();
				type = session.getType(objectType);
				//System.out.println("Selected type--->"+type+"--objectType--"+objectType);
				String dql ="Select r_object_id from "+objectType;
				IDfQuery ql=getClientX().getQuery();
				ql.setDQL(dql);
				mcollection=ql.execute(session, IDfQuery.DF_READ_QUERY);
			} catch (DfException e) {
				throw new DDfException(	"-------Exception while geting attributes from Rpository for export process. "
								+ e.getMessage() + e.getCause());
			} 
			
			if(mcollection!=null){
				try {
					while(mcollection.next()){
						String object_id=mcollection.getString("r_object_id");
						//System.out.println(object_id);
						String sdql="";
						if(org.apache.commons.lang3.StringUtils.containsIgnoreCase(dqlString," where")){
							 sdql=dqlString+" And (r_object_id='"+object_id+"')";
						}
						else
						{
							sdql=dqlString+" Where (r_object_id='"+object_id+"')";
						}
						try {
							IDfQuery q = getClientX().getQuery(); // Create query object
					      //  System.out.println("created query object");
							q.setDQL(sdql);
							IDfCollection collection = q.execute(session, IDfQuery.DF_READ_QUERY);
							
							while (collection.next()) {
								int counter = collection.getAttrCount();
								HashMap map = new HashMap();
							//	System.out.println("No of attributes----"+counter);
								for (int i = 0; i < counter; i++) {
								try{
									IDfAttr attr = collection.getAttr(i);
									String attrName = attr.getName();
									if (type.isTypeAttrRepeating(attrName)) {
										String repeatValue = collection.getAllRepeatingStrings(attrName, "#");
										map.put(attrName, repeatValue);
									} else {							
											map.put(attrName, collection.getString(attrName));	
									}
									}catch (com.documentum.fc.client.impl.typeddata.NoSuchAttributeException e) {
										//System.out.println(e.getMessage()+"-"+collection.getAttr(0).getName()+"-"+collection.getString(collection.getAttr(0).getName()));
									}
								}
								list.add(map);
								
							}
							collection.close();							
						} catch (Exception e) {
							//System.out.println("EXCEPTION------>"+e);
							throw new DDfException(
									"++++++++++Exception while geting attributes from Rpository fro export process. "
											+ e.getMessage() + e.getCause());
						}
					}
					mcollection.close();				
					//System.out.println("LIST-----"+list.size());
					return list;
				} catch (DfException e) {
					System.out.println(this.getClass().getName()+"="+e.getMessage());
				}
			}

			return list;

		}

	//6/15/2016 end Naveen editing place
	
	//6/24/2016
	@Override
	 public int getTotalObject(String objectType) throws DDfException {
		IDfSession session = null;
		IDfType type = null;
		IDfCollection mcollection=null;
		try {
			session = mSessionHandler.getIDfSession();
			type = session.getType(objectType);
			//System.out.println("Selected type--->"+type+"--objectType--"+objectType);
			String dql ="Select count(*) from "+objectType;
			IDfQuery ql=getClientX().getQuery();
			ql.setDQL(dql);
			mcollection=ql.execute(session, IDfQuery.DF_READ_QUERY);
			if(mcollection.next()){
				int i=mcollection.getInt(mcollection.getAttr(0).getName());
				return i;
			}
		} catch (DfException e) {
			throw new DDfException(	"-------Exception while geting attributes from Rpository for export process. "
							+ e.getMessage() + e.getCause());
		} 

		return 0;
	}
	@Override
	public List executeDQLObject(String dqlString, String objectType,
			int lower, int upper) throws DDfException {
		List list = new ArrayList();
		IDfSession session = null;
		IDfType type = null;
		IDfCollection mcollection=null;
		try {
			session = mSessionHandler.getIDfSession();
			type = session.getType(objectType);
			//System.out.println("Selected type--->"+type+"--objectType--"+objectType);
			String dql ="Select r_object_id from "+objectType;
			IDfQuery ql=getClientX().getQuery();
			ql.setDQL(dql);
			mcollection=ql.execute(session, IDfQuery.DF_READ_QUERY);
		} catch (DfException e) {
			throw new DDfException(	"-------Exception while geting attributes from Rpository for export process. "
							+ e.getMessage() + e.getCause());
		} 
		int Rcounter=0;
		if(mcollection!=null){
			try {
				while(mcollection.next()){
		//Start of Logic
				if(Rcounter<lower ||Rcounter>upper)	{
					Rcounter++;
					continue;
				}
				else
				{
					String object_id=mcollection.getString("r_object_id");
					//System.out.println(object_id);
					String sdql="";
					if(org.apache.commons.lang3.StringUtils.containsIgnoreCase(dqlString," where")){
						 sdql=dqlString+" And (r_object_id='"+object_id+"')";
					}
					else
					{
						sdql=dqlString+" Where (r_object_id='"+object_id+"')";
					}
					try {
						IDfQuery q = getClientX().getQuery(); // Create query object
				      //  System.out.println("created query object");
						q.setDQL(sdql);
						IDfCollection collection = q.execute(session, IDfQuery.DF_READ_QUERY);
						
						while (collection.next()) {
							int counter = collection.getAttrCount();
							HashMap map = new HashMap();
						//	System.out.println("No of attributes----"+counter);
							for (int i = 0; i < counter; i++) {
							try{
								IDfAttr attr = collection.getAttr(i);
								String attrName = attr.getName();
								if (type.isTypeAttrRepeating(attrName)) {
									String repeatValue = collection.getAllRepeatingStrings(attrName, "#");
									map.put(attrName, repeatValue);
								} else {							
										map.put(attrName, collection.getString(attrName));	
								}
								}catch (com.documentum.fc.client.impl.typeddata.NoSuchAttributeException e) {
									//System.out.println(e.getMessage()+"-"+collection.getAttr(0).getName()+"-"+collection.getString(collection.getAttr(0).getName()));
								}
							}
							list.add(map);
							
						}
						collection.close();							
					} catch (Exception e) {
						//System.out.println("EXCEPTION------>"+e);
						throw new DDfException(
								"++++++++++Exception while geting attributes from Rpository fro export process. "
										+ e.getMessage() + e.getCause());
					}
		
				}
		//end of Logic	
				Rcounter++;
				}
				mcollection.close();				
				System.out.println("LIST-----"+list.size());
				return list;
			} catch (DfException e) {
				System.out.println(this.getClass().getName()+"="+e.getMessage());
			}
		}

		return list;

	}
	//6/24/2016
//Old Code 6/15/2016
/*	public List executeDQLObject(String dqlString, String objectType)
			throws DDfException {
		List list = new ArrayList();
		IDfSession session = null;
		IDfType type = null;
		try {
			session = mSessionHandler.getIDfSession();
			type = session.getType(objectType);
			System.out.println("Selected type--->"+type+"--objectType--"+objectType);
		} catch (DfException e) {
			throw new DDfException(	"-------Exception while geting attributes from Rpository for export process. "
							+ e.getMessage() + e.getCause());
		} 
		IDfCollection collection = null; // Collection for the result
		IDfQuery q = getClientX().getQuery(); // Create query object
        System.out.println("created query object");
		q.setDQL(dqlString);
	
		System.out.println(dqlString);
		
		try {
			collection = q.execute(session, IDfQuery.DF_READ_QUERY);
			System.out.println("*collection*"+collection);
		} catch (DfException e) {
			throw new DDfException(
					"****Exception while geting attributes from Rpository during export process. "
							+ e.getMessage() + e.getCause());
		}

		try {
			
			while (collection.next()) {
				int counter = collection.getAttrCount();
				// map.clear();
				HashMap map = new HashMap();
				System.out.println("No of attributes----"+counter);
				for (int i = 0; i < counter; i++) {
					
					try{
					IDfAttr attr = collection.getAttr(i);
					String attrName = attr.getName();
					//System.out.println("attrName*******"+attrName);
					if (type.isTypeAttrRepeating(attrName)) {
						//System.out.println("inside if");
						String repeatValue = collection.getAllRepeatingStrings(attrName, "#");
						map.put(attrName, repeatValue);
					} else {
						
							System.out.println("inside else------"+"<<<<---attrName---->>>"+attrName+"***********collection.getString(attrName)*************"+collection.getString(attrName));
							map.put(attrName, collection.getString(attrName));
						
						
					}
					}catch (com.documentum.fc.client.impl.typeddata.NoSuchAttributeException e) {
						System.out.println(e.getMessage()+"-"+collection.getAttr(0).getName()+"-"+collection.getString(collection.getAttr(0).getName()));
					}
				}
				list.add(map);
				
			}
			
			collection.close();
			System.out.println("LIST-----"+list);
			return list;
		} catch (Exception e) {
			System.out.println("EXCEPTION------>"+e);
			throw new DDfException(
					"++++++++++Exception while geting attributes from Rpository fro export process. "
							+ e.getMessage() + e.getCause());
		}

	}
	*/
//Old Code 6/15/2016
	public String getFileNameByID(Object docId, boolean version,
			String extension) throws DDfException {
		String fileName = "";
		/*
		 * File Name logic has been changed on 29 July 2013 by Shubham Soti;
		 * File Name now onwards will be object id of the object as file name
		 * can consist of special characters so to avoid this situation, object
		 * id will be considered
		 */
		IDfClientX clientx = new DfClientX();
		IDfId myId = clientx.getId((String) docId);
		try {
			IDfSysObject sysObj;
			sysObj = (IDfSysObject) (mSessionHandler.getIDfSession())
					.getObject(myId);
			// fileName = sysObj.getObjectName();
			fileName = (String) docId;
			if (version) {
				fileName = fileName + "_v_" + sysObj.getImplicitVersionLabel();
			}
			fileName += "." + extension;
		} catch (DfException e) {
			throw new DDfException("Exception while geting File name from id "
					+ e.getMessage() + e.getCause());
		}
		return fileName;
	}

	public void closeSession() throws DDfException {
		mSessionHandler.closeSession();
	}

	public String importSysOrChild(ImportProcessorBean importProcessorBean)
			throws DDfException {
		String desPath = (String) importProcessorBean.getMetadataMap().get("r_folder_path");// destFolderPath.subSequence(0,
		//System.out.println("Dest path*******" + desPath);
		// destFolderPath.lastIndexOf("/")-2);
		String srcPath = (String) importProcessorBean.getMetadataMap().get("file_source_location__");
		//System.out.println("file_source_location__Path   " + srcPath);
		srcPath = contentFH.getFile(srcPath);
		//System.out.println("Source Path By Pallavi is" + srcPath);
		importProcessorBean.getMetadataMap().put("file_source_location__",srcPath);

		// Added by Shubhra
		String ObjectType = (String) importProcessorBean.getObjectType();
		IDfId checkedInId = null;
		IDfSysObject sysObject = null;
		IDfId id = null;
		/*
		 * try{ id = getObject(ObjectType,importProcessorBean.getMetadataMap());
		 * System.out.println("first Object's ID in importSysOrChild methos: " +
		 * id); }catch(Exception ex){
		 * System.out.println(" Exception occured :"+ex); }
		 */
		// Above part added by Shubhra

		// IDfId id = getIdByPath(desPath, (String)
		// importProcessorBean.getMetadataMap().get("object_name"));

		//System.out.println("Object's ID in importSysOrChild methos: " + id);
		String objectId = null;
		if (id == null) { // .isNull()) {
			objectId = createNewSysOrChildObj(importProcessorBean);
		} /*
		 * else { //IDfSysObject sysObject = null; try { sysObject =
		 * (IDfSysObject) mSessionHandler.getIDfSession().getObject(id); } catch
		 * (DfException e2) { e2.printStackTrace(); throw new
		 * DDfException("Exception while geting object " +
		 * importProcessorBean.getMetadataMap().get("object_name") +
		 * e2.getMessage() + e2.getCause()); } if
		 * (importProcessorBean.isUpdateExisting()) { try { objectId =
		 * updateSysObject(desPath, importProcessorBean.getMetadataMap(),
		 * srcPath, sysObject, importProcessorBean.isUpdateExisting(),
		 * importProcessorBean.isSupportingContains()); } catch (DDfException e)
		 * { e.printStackTrace(); throw new
		 * DDfException("Exception while updating object " +
		 * importProcessorBean.getMetadataMap().get("object_name") +
		 * e.getMessage() + e.getCause()); } } else { String minorVersion =
		 * (String) importProcessorBean.getMetadataMap().get("is_minor__"); try
		 * { sysObject.checkout(); String extn =
		 * getDocumentumExtension(srcPath.substring((srcPath.lastIndexOf(".") +
		 * 1))); sysObject.setContentType(extn); sysObject.setFileEx(srcPath,
		 * extn, 0, null); if ("yes".equalsIgnoreCase(minorVersion)) { String
		 * currentversion = sysObject.getVersionPolicy().getNextMinorLabel();
		 * checkedInId = sysObject.checkin(false, currentversion + ",CURRENT");
		 * } else { if ("no".equalsIgnoreCase(minorVersion) ||
		 * "".equalsIgnoreCase(minorVersion) || minorVersion == null) { String
		 * currentversion = sysObject.getVersionPolicy().getNextMajorLabel();
		 * //sysObject.checkin(false, currentversion + ",CURRENT"); checkedInId
		 * = sysObject.checkin(false, null); } else { try {
		 * sysObject.cancelCheckout(); } catch (DfException e) { throw new
		 * DDfException("Invalid value in column is_minor__" + e.getMessage(),
		 * e.getCause()); } } }
		 * 
		 * sysObject.save(); objectId = checkedInId.toString();
		 * updateAttribute(importProcessorBean.getMetadataMap(), sysObject); }
		 * catch (DfException e) {
		 * 
		 * try { sysObject.cancelCheckout(); } catch (DfException e1) { throw
		 * new DDfException("Exception while cancelling check out" +
		 * e1.getMessage(), e1.getCause()); } throw new
		 * DDfException("Exception while creating version of object" +
		 * e.getMessage(), e.getCause()); } } }
		 */
		// contentFH.deleteFile(srcPath);// Deleting the downloaded content from
		// the local file system by Harsh
		importProcessorBean.setObjectId(objectId);
		/*
		 * if (importProcessorBean.isSupportingContains()) {
		 * importSupportingDocuments(importProcessorBean); }
		 */
		return objectId;
	}

	private void importSupportingDocuments(
			ImportProcessorBean importProcessorBean) throws DDfException {
		ArrayList<ImportProcessorBean> impoSuppArrayList = importProcessorBean
				.getSupportings();
		ImportProcessorBean importProcessorBean2 = null;
		for (int i = 0; i < impoSuppArrayList.size(); i++) {
			importProcessorBean2 = impoSuppArrayList.get(i);
			String folderPath = importProcessorBean2.getMetadataMap()
					.get("r_folder_path").toString();
			boolean flag = checkFolder(folderPath);
			if (!flag) {
				createNewFolder(folderPath.substring(0,
						folderPath.lastIndexOf("/")), folderPath.substring(
						folderPath.lastIndexOf("/") + 1, folderPath.length()));
			}
			// importSysOrChild(importProcessorBean2);
			// importSupportingDocuments(importProcessorBean,
			// importProcessorBean2);
		}
	}

	private void importSupportingDocuments(
			ImportProcessorBean mainImportProcessorBean,
			ImportProcessorBean suppImportProcessorBean) throws DDfException {
		// IDfId supportingId=new DfId(importProcessorBean2.getObjectId());
		// System.out.println("supportingId : "+supportingId);
		Map suppMetadataMap = suppImportProcessorBean.getMetadataMap();
		IDfSysObject sysObj = null;
		try {
			// sysObj = (IDfSysObject)
			// getSessionHandler().getIDfSession().getObject(supportingId);
			sysObj = (IDfSysObject) getSessionHandler().getIDfSession()
					.newObject(suppImportProcessorBean.getObjectType());
			sysObj.setObjectName(suppMetadataMap.get("object_name").toString());
			sysObj.link(suppMetadataMap.get("r_folder_path").toString());
			sysObj.setFile(suppMetadataMap.get("file_source_location__").toString());
			sysObj.setContentType(getDocumentumExtension("tif"));
		} catch (DfException e1) {
			throw new DDfException("Error while uploading supporting doc.",
					e1.getCause());
		}
		for (Iterator iterator = suppMetadataMap.entrySet().iterator(); iterator
				.hasNext();) {
			Map.Entry entry = (Map.Entry) iterator.next();
			if (entry.getKey() != null) { // if coloumn name is null

				String attribute = entry.getKey().toString();
				String value = ((String) suppMetadataMap.get(attribute)).trim();
				attribute = (attribute.toLowerCase()).trim();
				try {
					// String attrName = null;
					if (value.startsWith("<main.") && value.endsWith(">")) {
						IDfSysObject mainObj = (IDfSysObject) getSessionHandler()
								.getIDfSession().getObject(
										new DfId(mainImportProcessorBean
												.getObjectId()));
						String mainAttribute = value.substring(
								attribute.indexOf("<main.") + 1,
								value.length() - 1);
						value = mainObj.getString(mainAttribute);
					}

					if (IDfType.DF_TIME == sysObj.getAttrDataType(attribute)) {
						sysObj.setTime(attribute, new DfTime(value,
								IDfTime.DF_TIME_PATTERN18));
					} else {
						// work for repeating attribute
						if (sysObj.isAttrRepeating(attribute)) {
							int start = 0;
							int end = 0;
							int len = 0;
							if (value != null) {
								len = value.length();
							}
							String repeatingValue = null;
							int valueIndex = 0;
							one: for (int i = 0; i < len; i++) {
								if (value.contains("#")) {
									end = value.indexOf('#');
									repeatingValue = value
											.substring(start, end);
									sysObj.setRepeatingString(attribute,
											valueIndex++, repeatingValue);
								}
								value = value.substring(end + 1);
								if (!value.contains("#")) {
									repeatingValue = value;

									sysObj.setRepeatingString(attribute,
											valueIndex, repeatingValue);
									break one;
								}
							}
						} else {
							sysObj.setString(attribute, value); // update
						}
					}
					sysObj.save();
				} catch (DfException e) {
					throw new DDfException(
							"Exception during metadata import process "
									+ e.getMessage() + e.getCause());
				}

			}
		}

	}

	private IDfId getIdByPath(String path, String obname) throws DDfException {
		IDfId id = null;
		int pathSepIndex = path.lastIndexOf('/');
		if (pathSepIndex == -1) {
			id = getObjectIdByQual("000");
		}

		StringBuffer bufQual = new StringBuffer(32);
		if (pathSepIndex == 0) {
			bufQual.append(" dm_cabinet where object_name='");
			bufQual.append(path.substring(1));
			bufQual.append("'");

		} else {
			bufQual.append(" dm_sysobject where FOLDER('");
			bufQual.append(path.substring(0, pathSepIndex));
			bufQual.append("') ");
			bufQual.append(" and object_name='");
			bufQual.append(obname);
			bufQual.append("'");
		}

		id = getObjectIdByQual(bufQual.toString());
		return id;
	}

	public boolean checkMainObjectIdForRsume(String mainObjectId)
			throws DDfException {
		boolean flag = false;

		IDfId id = getClientX().getId(mainObjectId);
		try {
			IDfSysObject sysObject = (IDfSysObject) getSessionHandler()
					.getIDfSession().getObject(id);
			if (sysObject == null) {
				flag = false;
			} else {
				flag = true;
			}
		} catch (DfException e) {
			throw new DDfException(
					"Object is not existed for resume on repository "
							+ e.getMessage(), e.getCause());
		}
		return flag;
	}

	/**
	 * 
	 */
	public void createAcl(String aclName, String formType, String region,
			String amcId) throws DDfException {
		//System.out.println("CSServicesProvider.createAcl()");
		try {
			IDfACL aclObj = getSessionHandler().getIDfSession().getACL(
					getSessionHandler().getIDfSession().getDocbaseOwnerName(),
					aclName);
			//System.out.println("aclObj " + aclObj);
			if (aclObj == null) {
				//System.out.println("acl obj is null");
				aclObj = (IDfACL) getSessionHandler().getIDfSession()
						.newObject("dm_acl");
				aclObj.setACLClass(3);

				aclObj.setDomain("dm_dbo");

				aclObj.setDescription(aclName);

				aclObj.setObjectName(aclName);
				setPermissions(aclObj, formType, region, amcId);
				aclObj.save();
			}
		} catch (DfException e) {
			throw new DDfException(e.getMessage(), e.getCause());
		}

	}

	/**
	 * This method to set the permission on ACL
	 * 
	 * @param formtype
	 * @param session
	 * @param createdAcl
	 * @param regionShortName
	 * @param amcShortName
	 * 
	 * @throws DfException
	 * @throws DDfException
	 */
	private void setPermissions(IDfACL createdAcl, String formType,
			String regionshortname, String amcShortName) throws DfException,
			DDfException {
		//System.out.println("CSServicesProvider.setPermissions() " + formType);
		String groupName = null;
		groupName = "dm_world";
		createdAcl.grant(groupName, 1, null);

		if (formType.equalsIgnoreCase("purchase")) {
			groupName = amcShortName + "_purchase_" + regionshortname + "_del1";
			if (getSessionHandler().getIDfSession().getGroup(groupName) != null) {
				createdAcl.grant(groupName, 6, null);
			}
			groupName = amcShortName + "_purchase_" + regionshortname + "_del2";
			if (getSessionHandler().getIDfSession().getGroup(groupName) != null) {
				createdAcl.grant(groupName, 6, null);
			}
			groupName = amcShortName + "_purchase_tl";
			if (getSessionHandler().getIDfSession().getGroup(groupName) != null) {
				createdAcl.grant(groupName, 6, null);
			}
			groupName = amcShortName + "_purchase_qc";
			if (getSessionHandler().getIDfSession().getGroup(groupName) != null) {
				createdAcl.grant(groupName, 6, null);
			}
			groupName = amcShortName + "_purchase_bh";
			if (getSessionHandler().getIDfSession().getGroup(groupName) != null) {
				createdAcl.grant(groupName, 7, null);
			}
		} else if (formType.equalsIgnoreCase("redemption")) {
			groupName = amcShortName + "_redemption_" + regionshortname + "_de";
			if (getSessionHandler().getIDfSession().getGroup(groupName) != null) {
				createdAcl.grant(groupName, 6, null);
			}
			groupName = amcShortName + "_redemption_tl";
			if (getSessionHandler().getIDfSession().getGroup(groupName) != null) {
				createdAcl.grant(groupName, 6, null);
			}
			groupName = amcShortName + "_redemption_qc";
			if (getSessionHandler().getIDfSession().getGroup(groupName) != null) {
				createdAcl.grant(groupName, 6, null);
			}
			groupName = amcShortName + "_redemption_bh";
			if (getSessionHandler().getIDfSession().getGroup(groupName) != null) {
				createdAcl.grant(groupName, 7, null);
			}
		} else if (formType.equalsIgnoreCase("switch")) {
			groupName = amcShortName + "_switch_" + regionshortname + "_de";
			if (getSessionHandler().getIDfSession().getGroup(groupName) != null) {
				createdAcl.grant(groupName, 6, null);
			}
			groupName = amcShortName + "_switch_tl";
			if (getSessionHandler().getIDfSession().getGroup(groupName) != null) {
				createdAcl.grant(groupName, 6, null);
			}
			groupName = amcShortName + "_switch_qc";
			if (getSessionHandler().getIDfSession().getGroup(groupName) != null) {
				createdAcl.grant(groupName, 6, null);
			}
			groupName = amcShortName + "_switch_bh";
			if (getSessionHandler().getIDfSession().getGroup(groupName) != null) {
				createdAcl.grant(groupName, 7, null);
			}
		}
		groupName = "dm_owner";
		createdAcl.grant(groupName, 7, null);

	}

	@Override
	public void setContentFileHandler(ContentFileHandler cfh) {

		contentFH = cfh;
	}

	@Override
	public ContentFileHandler getContentFileHandler() {
		return contentFH;
	}

	@Override
	public IDfSession getSession() throws DDfException {
		// TODO Auto-generated method stub
		return mSessionHandler.getIDfSession();
	}

	public IDfId getObject(String ObjectType, Map map) throws DDfException {
		/*
		 * System.err.println("getobject function is calling on cssserviceprovider"
		 * );
		 */
		// IDfSysObject sysObject=null;
		IDfId ObjId = null;
		IDfSession session = null;
		try {
			session = mSessionHandler.getIDfSession();
		} catch (DDfException e) {
			throw new DDfException("Exception while fetching the session. "
					+ e.getMessage() + e.getCause());
		}

		//System.out.println(" ObjectType value in CSServicesProvider : "+ ObjectType);

		String mobile_no = null;
		if (ObjectType.contains("cust")) {
			mobile_no = (String) map.get("mobile_no");
		} else if (ObjectType.contains("agent")) {
			mobile_no = (String) map.get("cap_mobile");
		} else if (ObjectType.contains("mer")) {
			mobile_no = (String) map.get("mobile_number");
		} else if (ObjectType.contains("dist")) {
			mobile_no = (String) map.get("cap_mobile");
		} else if (ObjectType.contains("nokia_employee")) {
			mobile_no = (String) map.get("emp_mob_no");
		} else if (ObjectType.contains("nokia_emp_closure")) {
			mobile_no = (String) map.get("mobile_no");
		}

		// String account_id=(String)map.get("account_id");
		String bank_name = (String) map.get("nokia_bank");
		String form_type = (String) map.get("form_type");
		IDfCollection co = null;
		IDfQuery query = new DfClientX().getQuery();

		// String
		// queryStr="Select * from "+ObjectType+" where cap_mobile='"+mobile_no+"' and cap_id='"+account_id+"' and nokia_bank='"+bank_name+"' and is_close='0'";
		String queryStr = "Select * from " + ObjectType + " where cap_mobile='"
				+ mobile_no + "' and nokia_bank='" + bank_name
				+ "' and form_type='" + form_type
				+ "' and is_close='0' and nokia_status <> 'Dummy_status'";
		System.err.println("Query inside getObject:" + queryStr);
		query.setDQL(queryStr);
		try {
			co = query.execute(session, IDfQuery.DF_READ_QUERY);
			if (co != null && co.next()) {
				// String ss = co.getString("r_object_id");
				ObjId = new DfId(co.getString("r_object_id"));
				//System.out.println("r_object_id in getObject method :" + ObjId);
				/*
				 * if(ss!=null && !(ss.equalsIgnoreCase(""))){
				 * sysObject=(IDfSysObject)session.getObject( new
				 * DfId(co.getString("r_object_id")));
				 * System.out.println(" Sys Object name **** "
				 * +sysObject.getObjectName()); }
				 */
			}

			co.close();
		} catch (DfException e) {
			// throw new DDfException("Exception while getting object " +
			// importProcessorBean.getMetadataMap().get("object_name")+
			// e.getMessage() + e.getCause());
			throw new DDfException("Exception while getting object ");
		}
		return ObjId;
	}
	

}
