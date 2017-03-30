package com.daffodil.documentumie.dctm.api;



import java.util.List;
import java.util.Map;

import com.daffodil.documentumie.businessoperationprocessorbean.ImportProcessorBean;
import com.daffodil.documentumie.dctm.exception.DDfException;
import com.daffodil.documentumie.filehandler.ContentFileHandler;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.IDfId;

public interface CSServices {

	int BOOLEAN = 1;
	int STRING = 2;
	int TIME = 3;
	int INTEGER = 4;
	int DOUBLE = 5;
	int DMID = 6;
	int UNDEFINED = 7;
	
	// Following method is added for the implementation of the FTP functionality by Harsh
	public void setContentFileHandler(ContentFileHandler cfh);
	public ContentFileHandler getContentFileHandler();

	public void login(String repoName, String userName, String password,
			String domain) throws DDfException;

	public Object createObject(String r_object_type) throws DDfException;

	public Object getObjectIdByQual(String string) throws DDfException;

	public Object getObjectByObjectNamePath(String objName, String r_folder_path)
			throws DDfException;

	public List getAvailableRepositories() throws DDfException;

	public List getAvailableObjecTypes() throws DDfException;

	public List getAttributes(String objectType) throws DDfException;

	public List getAllDm_SysObjectObjectTypes() throws DDfException;

	public List executeDQL(String dQL) throws DDfException;

	public List getAllVersion(Object obj) throws DDfException;

	public String getFolderPath(Object obj) throws DDfException;

	public int getRowCount(String dQL) throws DDfException;

	public String getDocumentumExtension(String dosExtension)
			throws DDfException;

	public String getDOSExtension(String objId) throws DDfException;

	public void exportObject(Object objId, String destPath, String fileName,String destFTP)
			throws DDfException;

	public List getRenditions(Object obj) throws DDfException;

	public Boolean checkCabinet(String cabinetName) throws DDfException;

	public void createNewCabinet(String cabinetName) throws DDfException;

	public Boolean checkFolder(String folderpath) throws DDfException;

	public void createNewFolder(String path, String folderName)
			throws DDfException;

	public int validInputHeader(String type) throws DDfException;

	public Object[] validateObject(Map metadataMap, String objType)
			throws DDfException;

	public String importSuperThanSys(ImportProcessorBean importProcessorBean)
			throws DDfException;

	// public void importContent(Object id, HashMap map, boolean updateExisting,
	// String isMinorVersion) throws DDfException;

	public String importFolder(ImportProcessorBean importProcessorBean)
			throws DDfException;

	// public void creatFile(String destFolderPath, HashMap map, String
	// objectType) throws DDfException;

	public int getDataType(String attrName, String objType) throws DDfException;

	public void closeSession() throws DDfException;

	public List executeDQLObject(String dqlString, String objectType)
			throws DDfException;

	public String getFileNameByID(Object docId, boolean version,
			String extension) throws DDfException;

	public String getDosExtensionById(Object objId) throws DDfException;

	public String importSysOrChild(ImportProcessorBean importProcessorBean)
			throws DDfException;

	public boolean checkMainObjectIdForRsume(String mainObjectId)
			throws DDfException;

	public void createAcl(String aclName, String formType, String region, String amcId)
			throws DDfException;
	public IDfSession getSession() throws DDfException;
	//public void exportToFTP(String destPath, String fileName , String ftpPath) throws DDfException;
	//This LIne Added By Bablu
	//public IDfSysObject getObjectById(IDfId obj);
}
