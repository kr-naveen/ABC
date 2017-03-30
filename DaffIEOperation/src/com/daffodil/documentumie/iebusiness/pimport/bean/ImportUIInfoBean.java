package com.daffodil.documentumie.iebusiness.pimport.bean;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.daffodil.documentumie.iebusiness.UIInfoBean;


public class ImportUIInfoBean extends UIInfoBean{

	private String objectType;
	private String metadataInputFile;
	private String worksheet;
	private String filterCriterai ;
	private HashMap mappedAttributs;
	private HashMap selectedAttributesOfMetadata;
	private List selectedAttributesOfRepo;
	private ArrayList filterParam;
	private int noOfRows;
	private List rows;
	private boolean isLive;
	private String sql;
	private int objectHirerchy;
	private boolean updateExisting;
	private String extension;
	private boolean resume;
	private String resumeConfigFileLocation;
	
	/*  fields for resume process***************/
	
	private String sourceFileLocation;
	private String objectName;
	private int recordNo;
	private String r_folder_path;
	private String main_object_id;
	private int chunkNo;
	private long totalBytesOfFile;
	
	/*
	 * Below properties are added by Harsh for the implementation of the FTP functionality on 12/23/2011.
	 */
	private String ftpURL;
	private String ftpUserName;
	private String ftpPassword;
	private String ftpMetadataFile;
	private String ftpWorksheet;
	private String importFromLocation;
	
	
	
	public String getImportFromLocation() {
		return importFromLocation;
	}

	public void setImportFromLocation(String importFromLocation) {
		this.importFromLocation = importFromLocation;
	}

	public String getFtpMetadataFile() {
		return ftpMetadataFile;
	}

	public void setFtpMetadataFile(String ftpMetadataFile) {
		this.ftpMetadataFile = ftpMetadataFile;
	}

	public String getFtpWorksheet() {
		return ftpWorksheet;
	}

	public void setFtpWorksheet(String ftpWorksheet) {
		this.ftpWorksheet = ftpWorksheet;
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

	// The above accessor and mutator methods are addded by Harsh for FTP functionality on 12/23/2011 
	
	public ImportUIInfoBean () {
   }

	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		String old = this.objectType;
		this.objectType = objectType;
		propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this,
				"objectType", old, objectType));
	}

	public String getMetadataInputFile() {
		return metadataInputFile;
	}

	public void setMetadataInputFile(String metadataInputFile) {
		String old = this.metadataInputFile;
		this.metadataInputFile = metadataInputFile;
		propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this,
				"metadataInputFile", old, metadataInputFile));
	}

	public String getWorksheet() {
		return worksheet;
	}

	public void setWorksheet(String worksheet) {
		String old = this.worksheet;
		this.worksheet = worksheet;
		propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this,
				"worksheet", old, worksheet));
	}

	
	/*public String getMatchCase() {
		return matchCase;
	}

	public void setMatchCase(String matchCase) {
		String old = this.matchCase;
		this.matchCase = matchCase;
		propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this,
				"matchCase", old, matchCase));
	}*/

	public HashMap getMappedAttributs() {
		return mappedAttributs;
	}

	public void setMappedAttributs(HashMap mappedAttribut) {
		Map old = this.mappedAttributs;
		this.mappedAttributs = mappedAttribut;
		propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this,
				"mappedAttributs", old, mappedAttribut));
		
	}

	public String getFilterCriterai() {
		return filterCriterai;
	}

	public void setFilterCriterai(String filterCriterai) {
		String old = this.filterCriterai;
		this.filterCriterai = filterCriterai;
		propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this,
				"filterCriterai", old, filterCriterai));
	}

	public HashMap getSelectedAttributesOfMetadata() {
		return selectedAttributesOfMetadata;
	}

	public void setSelectedAttributesOfMetadata(HashMap selectedAttributesOfMetadata) {
		Map old = this.selectedAttributesOfMetadata;
		this.selectedAttributesOfMetadata = selectedAttributesOfMetadata;
		propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this,
				"selectedAttributesOfMetadata", old, selectedAttributesOfMetadata));
	}

	public List getSelectedAttributesOfRepo() {
		return selectedAttributesOfRepo;
	}

	public void setSelectedAttributesOfRepo(List selectedAttributesOfRepo) {
		List old = this.selectedAttributesOfRepo;
		this.selectedAttributesOfRepo = selectedAttributesOfRepo;
		propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this,
				"selectedAttributesOfRepo", old, selectedAttributesOfRepo));
	}

	public ArrayList getFilterParam() {
		return filterParam;
	}

	public void setFilterParam(ArrayList filterParam) {
		System.out.println("ImportUIInfoBean.setFilterParam()");
		ArrayList old = this.filterParam;
		this.filterParam = filterParam;
		propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this,
				"filterParam", old, filterParam));
	}

	public int getNoOfRows() {
		return noOfRows;
	}

	public void setNoOfRows(int noOfRows) {
		int old = this.noOfRows;
		this.noOfRows = noOfRows;
		propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this,
				"noOfRows", old, noOfRows));
	}

	public List getRows() {
		return rows;
	}

	public void setRows(List rows) {
		List old = this.rows; 
		this.rows = rows;
		propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this,
				"rows", old, rows));
	}

	public boolean getIsLive() {
		return isLive;
	}

	public void setIsLive(boolean isLive) {
		boolean old = this.isLive;
		this.isLive = isLive;
		propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this,
				"isLive", old, isLive));
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		String old = this.sql;
		this.sql = sql;
		propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this,
				"sql", old, sql));
	}

	public int getObjectHirerchy() {
		return objectHirerchy;
	}

	public void setObjectHirerchy(int objectHirerchy) {
		int old = this.objectHirerchy;
		this.objectHirerchy = objectHirerchy;
		propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this,
				"objectHirerchy", old, objectHirerchy));
	}

	public boolean isUpdateExisting() {
		return updateExisting;
	}

	public void setUpdateExisting(boolean updateExisting) {
		boolean old = this.updateExisting;
		this.updateExisting = updateExisting;
		propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this,
				"updateExisting", old, updateExisting));
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		String old = this.extension;
		this.extension = extension;
		propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this,
				"extension", old, extension));
	}

	
	public boolean isResume() {
		return resume;
	}

	public void setResume(boolean resume) {
		this.resume = resume;
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

	public String getMain_object_id() {
		return main_object_id;
	}

	public void setMain_object_id(String main_object_id) {
		this.main_object_id = main_object_id;
	}

	public int getChunkNo() {
		return chunkNo;
	}

	public void setChunkNo(int chunkNo) {
		this.chunkNo = chunkNo;
	}

	public long getTotalBytesOfFile() {
		return totalBytesOfFile;
	}

	public void setTotalBytesOfFile(long totalBytesOfFile) {
		this.totalBytesOfFile = totalBytesOfFile;
	}

	public String getResumeConfigFileLocation() {
		return resumeConfigFileLocation;
	}

	public void setResumeConfigFileLocation(String resumeConfigFileLocation) {
		this.resumeConfigFileLocation = resumeConfigFileLocation;
	}

	

    
}

