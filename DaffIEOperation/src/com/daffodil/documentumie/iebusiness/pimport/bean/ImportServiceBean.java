package com.daffodil.documentumie.iebusiness.pimport.bean;

import java.util.HashMap;


public class ImportServiceBean {
	String userName;
	String Domain;
	String repository;
	String objectType;
	boolean isLive;
	HashMap mappedAttributs;
	String metadataFilePath;
	String sheetName;
	String reportFilePath;
	String logFilePath;
	String sql;
	boolean updateExisting;
	int objectHirerchy;
	String extension;
	
	

	
	/***** fiels for resume process  *********/
	
	private boolean resume;
	private String sourceFileLocation;
	private String objectName;
	private int recordNo;
	private String r_folder_path;
	private boolean isSchedule;
	
	
	

	
    public boolean isSchedule() {
		return isSchedule;
	}

	public void setSchedule(boolean isSchedule) {
		this.isSchedule = isSchedule;
	}

	
    public ImportServiceBean () {
    	
    }

  	public String getRepository() {
		return repository;
	}


	public void setRepository(String repository) {
		this.repository = repository;
	}


	public String getObjectType() {
		return objectType;
	}


	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}


	public boolean isLive() {
		return isLive;
	}


	public void setisLive(boolean isLive) {
		this.isLive = isLive;
	}

	public HashMap getMappedattributes() {
		return mappedAttributs;
	}

	public void setMappedattributes(HashMap hashMap) {
		this.mappedAttributs = hashMap;
	}

	public String getSheetName() {
		// TODO Auto-generated method stub
		return sheetName;
	}


	public String getMetadataFilePath() {
		return metadataFilePath;
	}


	public void setMetadataFilePath(String metadataFilePath) {
		this.metadataFilePath = metadataFilePath;
	}


	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}

	public String getReportFilePath() {
		return reportFilePath;
	}

	public void setReportFilePath(String reportFilePath) {
		this.reportFilePath = reportFilePath;
	}

	public String getLogFilePath() {
		return logFilePath;
	}

	public void setLogFilePath(String logFilePath) {
		this.logFilePath = logFilePath;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	
	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public boolean isUpdateExisting() {
		return updateExisting;
	}

	public void setUpdateExisting(boolean updateExisting) {
		this.updateExisting = updateExisting;
	}

	public int getObjectHirerchy() {
		return objectHirerchy;
	}

	public void setObjectHirerchy(int objectHirerchy) {
		this.objectHirerchy = objectHirerchy;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public String getDomain() {
		return Domain;
	}

	public void setDomain(String domain) {
		Domain = domain;
	}

	public String getSourceFileLocation() {
		return sourceFileLocation;
	}

	public void setSourceFileLocation(String sourceFileLocation) {
		this.sourceFileLocation = sourceFileLocation;
	}

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	public int getRecordNo() {
		return recordNo;
	}

	public void setRecordNo(int recordNo) {
		this.recordNo = recordNo;
	}

	public String getR_folder_path() {
		return r_folder_path;
	}

	public void setR_folder_path(String r_folder_path) {
		this.r_folder_path = r_folder_path;
	}

	public boolean isResume() {
		return resume;
	}

	public void setResume(boolean resume) {
		this.resume = resume;
	}

}