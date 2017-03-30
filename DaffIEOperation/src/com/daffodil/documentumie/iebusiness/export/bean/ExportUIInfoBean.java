package com.daffodil.documentumie.iebusiness.export.bean;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import com.daffodil.documentumie.iebusiness.UIInfoBean;

public class ExportUIInfoBean extends UIInfoBean{

	/*String repository;

	String username;

	String password;

	String domain;
*/
	String ObjectType;

	boolean allVersion;

	boolean onlyMetadata;

	List availableAttribute;

	List selectedAttribute;

	String searchCriteria;

	String metaDataFileName;

	String outPutFile;
	
	ArrayList filterParam;
	
	boolean matchCase;
	
	String dqlText;
	
	String exportType;
	
	boolean ExportIntoZIP;

	String reportType;
	
	/*
	 * Below properties and their accessor and mutator methods are added by Harsh for the implementation of the FTP functionality on 12/29/2011.
	 */
	private String ftpURL;
	private String ftpUserName;
	private String ftpPassword;
	private String ftpMetadataFile;
	private String ftpOutputFile;
	private String exportToLocation;
	
	
	
	
	public String getExportToLocation() {
		return exportToLocation;
	}

	public void setExportToLocation(String exportToLocation) {
		this.exportToLocation = exportToLocation;
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

	public String getFtpMetadataFile() {
		return ftpMetadataFile;
	}

	public void setFtpMetadataFile(String ftpMetadataFile) {
		this.ftpMetadataFile = ftpMetadataFile;
	}

	public String getFtpOutputFile() {
		return ftpOutputFile;
	}

	public void setFtpOutputFile(String ftpOutputFile) {
		this.ftpOutputFile = ftpOutputFile;
	}

	public ExportUIInfoBean() {

	}

	public boolean isAllVersion() {
		return allVersion;
	}

	public void setAllVersion(boolean allVersion) {
		boolean old = this.allVersion;
		this.allVersion = allVersion;
		propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this,
				"allVersion", old, allVersion));
	}

	public List getAvailableAttribute() {
		return availableAttribute;
	}

	public void setAvailableAttribute(List availableAttribute) {
		List old = this.availableAttribute;
		this.availableAttribute = availableAttribute;
		propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this,
				"availableAttribute", old, availableAttribute));
	}

	/*public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}
*/
	public String getMetaDataFilePath() {
		return metaDataFileName;
	}

	public void setMetaDataFilePath(String metaDataFileName) {
		String old = this.metaDataFileName;
		this.metaDataFileName = metaDataFileName;
		propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this,
				"metaDataFileName", old, metaDataFileName));
	}

	public String getObjectType() {
		return ObjectType;
	}

	public void setObjectType(String objectType) {
		String old = this.ObjectType;
		ObjectType = objectType;
		propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this,
				"ObjectType", old, ObjectType));
	}

	public boolean isOnlyMetadata() {
		return onlyMetadata;
	}

	public void setOnlyMetadata(boolean onlyMetadata) {
		boolean old = this.onlyMetadata;
		this.onlyMetadata = onlyMetadata;
		propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this,
				"onlyMetadata", old, onlyMetadata));
	}

	public String getOutPutFile() {
		return outPutFile;
	}

	public void setOutPutFile(String outPutFile) {
		String old = this.outPutFile;
		this.outPutFile = outPutFile;
		propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this,
				"outPutFile", old, outPutFile));
	}

	/*public String getPassword() {
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
*/
	public String getSearchCriteria() {
		return searchCriteria;
	}

	public void setSearchCriteria(String searchCriteria) {
		String old = this.searchCriteria;
		this.searchCriteria = searchCriteria;
		propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this,
				"searchCriteria", old, searchCriteria));
	}

	public List getSelectedAttribute() {
		return selectedAttribute;
	}

	public void setSelectedAttribute(List selectedAttribute) {
		List old = this.selectedAttribute;
		this.selectedAttribute = selectedAttribute;
		propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this,
				"selectedAttribute", old, selectedAttribute));
	}

	/*public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
*/
	public ArrayList getFilterParam() {
		return filterParam;
	}

	public void setFilterParam(ArrayList filterParam) {
		ArrayList old = this.filterParam;
		this.filterParam = filterParam;
		propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this,
				"filterParam", old, filterParam));
	}

	/*public boolean isMatchCase() {
		return matchCase;
	}

	public void setMatchCase(boolean matchCase) {
		this.matchCase = matchCase;
	}
*/
	public String getDqlText() {
		return dqlText;
	}

	public void setDqlText(String dqlText) {
		String old = this.dqlText;
		this.dqlText = dqlText;
		propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this,
				"dqlText", old, dqlText));
	}

	public String getExportType() {
		return exportType;
	}

	public void setExportType(String exportType) {
		String old = this.exportType;
		this.exportType = exportType;
		propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this,
				"exportType", old, exportType));
	}

	public boolean isExportIntoZIP() {
		return ExportIntoZIP;
	}

	public void setExportIntoZIP(boolean exportIntoZIP) {
		boolean old = this.ExportIntoZIP;
		ExportIntoZIP = exportIntoZIP;
		propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this,
				"exportIntoZIP", old, exportIntoZIP));
	}

	public String getReportType() {
		return reportType;
	}

	public void setReportType(String reportType) {
		String old = this.reportType;
		this.reportType = reportType;
		propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this,
				"reportType", old, reportType));
	}

	

}
