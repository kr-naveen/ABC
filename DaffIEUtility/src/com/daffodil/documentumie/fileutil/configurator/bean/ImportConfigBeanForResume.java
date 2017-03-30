package com.daffodil.documentumie.fileutil.configurator.bean;

import java.util.HashMap;

public class ImportConfigBeanForResume extends ImportConfigBean{
	private String userName;
	private String metadataFileLocation;
	private String sourceFileLocation;
	private String objectName;
	private String recordNo;
	private String r_folder_path;
	private String filterCriteria;
	private HashMap map = new HashMap();
	private String objectHirerchy;
	private String workSheet;
	
	
	public String getMetadataFileLocation() {
		return metadataFileLocation;
	}
	public void setMetadataFileLocation(String metadataFilleLocation) {
		this.metadataFileLocation = metadataFilleLocation;
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
	public String getRecordNo() {
		return recordNo;
	}
	public void setRecordNo(String recordNo) {
		this.recordNo = recordNo;
	}
	public String getR_folder_path() {
		return r_folder_path;
	}
	public void setR_folder_path(String r_folder_path) {
		this.r_folder_path = r_folder_path;
	}
	public String getFilterCriteria() {
		return filterCriteria;
	}
	public void setFilterCriteria(String filterCriteria) {
		this.filterCriteria = filterCriteria;
	}
	public HashMap getMap() {
		return map;
	}
	public void setMap(HashMap map) {
		this.map = map;
	}
	
	public String getObjectHirerchy() {
		return objectHirerchy;
	}
	public void setObjectHirerchy(String objectHirerchy) {
		this.objectHirerchy = objectHirerchy;
	}
	public String getWorkSheet() {
		return workSheet;
	}
	public void setWorkSheet(String workSheet) {
		this.workSheet = workSheet;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
}