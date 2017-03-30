package com.daffodil.documentumie.iebusiness.export.bean;

import java.util.List;


public class ExportServiceBean {

	String repository;

	String username;

	String password;

	String domain;

	String ObjectType;

	boolean allVersion;

	boolean onlyMetadata;

	List availableAttribute;

	List selectedAttribute;

	String metaDataFileName;

	String outPutFile;
		
	boolean matchCase;
	
	String dqlText;
	
	String exportType;
	
	boolean ExportIntoZIP;

	String reportType;
	
	boolean dailyBases;
	
	private boolean isSchedule;
	private String importFrom = null;
	private String ftpURL = null;
	private String ftpUserName = null;
	private String ftpPassword = null;
	private String ftpMetadataPath = null;
	private String ftpContentPath = null;
	
	public boolean isSchedule() {
		return isSchedule;
	}

	public void setSchedule(boolean isSchedule) {
		this.isSchedule = isSchedule;
	}
	 
	public ExportServiceBean () {
    	
    }

    public boolean isAllVersion() {
		return allVersion;
	}

	public void setAllVersion(boolean allVersion) {
		this.allVersion = allVersion;
	}

	public List getAvailableAttribute() {
		return availableAttribute;
	}

	public void setAvailableAttribute(List availableAttribute) {
		this.availableAttribute = availableAttribute;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public boolean isMatchCase() {
		return matchCase;
	}

	public void setMatchCase(boolean matchCase) {
		this.matchCase = matchCase;
	}

	public String getMetaDataFilePath() {
		return metaDataFileName;
	}

	public void setMetaDataFilePath(String metaDataFileName) {
		this.metaDataFileName = metaDataFileName;
	}

	public String getObjectType() {
		return ObjectType;
	}

	public void setObjectType(String objectType) {
		ObjectType = objectType;
	}

	public boolean isOnlyMetadata() {
		return onlyMetadata;
	}

	public void setOnlyMetadata(boolean onlyMetadata) {
		this.onlyMetadata = onlyMetadata;
	}

	public String getOutPutFile() {
		return outPutFile;
	}

	public void setOutPutFile(String outPutFile) {
		this.outPutFile = outPutFile;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRepository() {
		return repository;
	}

	public void setRepository(String repository) {
		this.repository = repository;
	}

	public List getSelectedAttribute() {
		return selectedAttribute;
	}

	public void setSelectedAttribute(List selectedAttribute) {
		this.selectedAttribute = selectedAttribute;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getDqlText() {
		return dqlText;
	}

	public void setDqlText(String dqlText) {
		this.dqlText = dqlText;
	}

	public String getExportType() {
		return exportType;
	}

	public void setExportType(String exportType) {
		this.exportType = exportType;
	}

	public boolean isExportIntoZIP() {
		return ExportIntoZIP;
	}

	public void setExportIntoZIP(boolean exportIntoZIP) {
		ExportIntoZIP = exportIntoZIP;
	}

	public String getReportType() {
		return reportType;
	}

	public void setReportType(String reportType) {
		this.reportType = reportType;
	}

	public boolean isDailyBases() {
		return dailyBases;
	}

	public void setDailyBases(boolean dailyBases) {
		this.dailyBases = dailyBases;
	}

	public String getImportFrom() {
		return importFrom;
	}

	public void setImportFrom(String importFrom) {
		this.importFrom = importFrom;
	}

	public String getFtpURL() {
		return ftpURL;
	}

	public void setFtpURL(String ftpURL) {
		this.ftpURL = ftpURL;
	}

	public String getFtpUserName() {
		return ftpUserName;
	}

	public void setFtpUserName(String ftpUserName) {
		this.ftpUserName = ftpUserName;
	}

	public String getFtpPassword() {
		return ftpPassword;
	}

	public void setFtpPassword(String ftpPassword) {
		this.ftpPassword = ftpPassword;
	}

	public String getFtpMetadataPath() {
		return ftpMetadataPath;
	}

	public void setFtpMetadataPath(String ftpMetadataPath) {
		this.ftpMetadataPath = ftpMetadataPath;
	}

	public String getFtpContentPath() {
		return ftpContentPath;
	}

	public void setFtpContentPath(String ftpContentPath) {
		this.ftpContentPath = ftpContentPath;
	}
	
	

}

