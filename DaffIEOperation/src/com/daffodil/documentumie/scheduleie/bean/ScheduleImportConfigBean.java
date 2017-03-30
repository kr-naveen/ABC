package com.daffodil.documentumie.scheduleie.bean;

import java.util.HashMap;

public class ScheduleImportConfigBean {
	
	private String repository = null;
	private String password = null;
	private String UserName = null;
	private String Domain  = null;
	private String objectType = null;
	private String filterParam = null;
	private String isLive = null;
	private HashMap mappedAttributs = new HashMap();
	private String worksheet = null;
	private String selectedAttributesOfMetadata = null;
	private String metadataInputFile = null;
	private String sql = null;
	private String recordNo = null;
	private String chunkNo = null;
	private String extension = null;
	private String noOfRows = null;
	private String totalBytesOfFile = null;
	private String objectHirerchy = null;
	private String upDateExisting = null;
	private String importFrom = null;
	private String ftpURL = null;
	private String ftpUserName = null;
	private String ftpPassword = null;
	private String ftpMetadataPath = null;
	private String ftpMetaSheet = null;
	private String scheduleName = null;
	
	
	
	public String getScheduleName() {
		return scheduleName;
	}
	public void setScheduleName(String scheduleName) {
		this.scheduleName = scheduleName;
	}
	public String getUpDateExisting() {
		return upDateExisting;
	}
	public void setUpDateExisting(String upDateExisting) {
		this.upDateExisting = upDateExisting;
	}
	public String getRepository() {
		return repository;
	}
	public void setRepository(String repository) {
		this.repository = repository;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getUserName() {
		return UserName;
	}
	public void setUserName(String userName) {
		UserName = userName;
	}
	public String getDomain() {
		return Domain;
	}
	public void setDomain(String domain) {
		Domain = domain;
	}
	public String getObjectType() {
		return objectType;
	}
	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}
	
	public String getFilterParam() {
		return filterParam;
	}
	public void setFilterParam(String filterParam) {
		this.filterParam = filterParam;
	}
	public String getIsLive() {
		return isLive;
	}
	public void setIsLive(String isLive) {
		this.isLive = isLive;
	}

	public String getSelectedAttributesOfMetadata() {
		return selectedAttributesOfMetadata;
	}
	public void setSelectedAttributesOfMetadata(String selectedAttributesOfMetadata) {
		this.selectedAttributesOfMetadata = selectedAttributesOfMetadata;
	}
	public String getMetadataInputFile() {
		return metadataInputFile;
	}
	public void setMetadataInputFile(String metadataInputFile) {
		this.metadataInputFile = metadataInputFile;
	}
	public String getSql() {
		return sql;
	}
	public void setSql(String sql) {
		this.sql = sql;
	}
	
	public String getExtension() {
		return extension;
	}
	public void setExtension(String extension) {
		this.extension = extension;
	}
	public String getRecordNo() {
		return recordNo;
	}
	public void setRecordNo(String recordNo) {
		this.recordNo = recordNo;
	}
	public String getChunkNo() {
		return chunkNo;
	}
	public void setChunkNo(String chunkNo) {
		this.chunkNo = chunkNo;
	}
	public String getNoOfRows() {
		return noOfRows;
	}
	public void setNoOfRows(String noOfRows) {
		this.noOfRows = noOfRows;
	}
	public String getTotalBytesOfFile() {
		return totalBytesOfFile;
	}
	public void setTotalBytesOfFile(String totalBytesOfFile) {
		this.totalBytesOfFile = totalBytesOfFile;
	}
	public String getObjectHirerchy() {
		return objectHirerchy;
	}
	public void setObjectHirerchy(String objectHirerchy) {
		this.objectHirerchy = objectHirerchy;
	}
	public String getWorksheet() {
		return worksheet;
	}
	public void setWorksheet(String worksheet) {
		this.worksheet = worksheet;
	}
	public void setMappedAttributs(HashMap mappedAttributs) {
		System.out.println("in..........  "+mappedAttributs);
		this.mappedAttributs = mappedAttributs;
	}
	public HashMap getMappedAttributs() {
		return mappedAttributs;
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
	public String getFtpMetaSheet() {
		return ftpMetaSheet;
	}
	public void setFtpMetaSheet(String ftpMetaSheet) {
		this.ftpMetaSheet = ftpMetaSheet;
	}
	
	
	
}
