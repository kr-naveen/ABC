package com.daffodil.documentumie.businessoperationprocessorbean;

import java.util.ArrayList;
import java.util.Map;

import com.daffodil.documentumie.dctm.api.CSServices;
import com.daffodil.documentumie.dctm.apiimpl.CSServicesProvider;
import com.daffodil.documentumie.fileutil.logger.IELogger;
import com.daffodil.documentumie.fileutil.metadata.api.MetadataReader;
import com.daffodil.documentumie.fileutil.metadata.api.MetadataWriter;

public class ImportProcessorBean {

	private CSServices cServicesProvider  = null;
	private MetadataWriter metadataWriter  = null;
	private MetadataReader metadataReader = null;
	private IELogger IELogger=null;
	private Map metadataMap;
	private Map metadtaAndMappedAttributesMap;
	private String objectType;
	private boolean updateExisting; 
	private boolean continueImport;
	private int objectTypeHierarchy;
	private String objectId;
	private String metaDataFilePath;
	private boolean isSupportingDoc;
	
	
	private ArrayList<ImportProcessorBean> supportings;
	private boolean supportingContains;
	
	public Map getMetadataMap() {
		return metadataMap;
	}
	public void setMetadataMap(Map metadataMap) {
		this.metadataMap = metadataMap;
	}
	
	public boolean isSupportingContains() {
		return supportingContains;
	}
	public void setSupportingContains(boolean supportingContains) {
		this.supportingContains = supportingContains;
	}
	public ArrayList<ImportProcessorBean> getSupportings() {
		return supportings;
	}
	public void setSupportings(ArrayList<ImportProcessorBean> supportings) {
		this.supportings = supportings;
	}
		
	public boolean isUpdateExisting() {
		return updateExisting;
	}
	public void setUpdateExisting(boolean updateExisting) {
		this.updateExisting = updateExisting;
	}
	public boolean isContinueImport() {
		return continueImport;
	}
	public void setContinueImport(boolean continueImport) {
		this.continueImport = continueImport;
	}
	public Map getMetadtaAndMappedAttributesMap() {
		return metadtaAndMappedAttributesMap;
	}
	public void setMetadtaAndMappedAttributesMap(Map metadtaAndMappedAttributesMap) {
		this.metadtaAndMappedAttributesMap = metadtaAndMappedAttributesMap;
	}
	public String getObjectType() {
		return objectType;
	}
	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}
	public int getObjectTypeHierarchy() {
		return objectTypeHierarchy;
	}
	public void setObjectTypeHierarchy(int objectTypeHierarchy) {
		this.objectTypeHierarchy = objectTypeHierarchy;
	}
	public String getObjectId() {
		return objectId;
	}
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}	
	public void reset() {
		metadataMap = null;
		metadtaAndMappedAttributesMap = null;
		objectType = null;
		updateExisting = false; 
		continueImport = false;
		objectTypeHierarchy = -1;
		objectId = null;		
		supportings = null;
		supportingContains = false;
	}
	public CSServices getCServicesProvider() {
		return cServicesProvider;
	}
	public void setCServicesProvider(CSServices services) {
		cServicesProvider = services;
	}
	public MetadataWriter getMetadataWriter() {
		return metadataWriter;
	}
	public void setMetadataWriter(MetadataWriter metadataWriter) {
		this.metadataWriter = metadataWriter;
	}
	public MetadataReader getMetadataReader() {
		return metadataReader;
	}
	public void setMetadataReader(MetadataReader metadataReader) {
		this.metadataReader = metadataReader;
	}
	public IELogger getIELogger() {
		return IELogger;
	}
	public void setIELogger(IELogger logger) {
		IELogger = logger;
	}
	public String getMetaDataFilePath() {
		return metaDataFilePath;
	}
	public void setMetaDataFilePath(String metaDataFilePath) {
		this.metaDataFilePath = metaDataFilePath;
	}
	public boolean isSupportingDoc() {
		return isSupportingDoc;
	}
	public void setSupportingDoc(boolean isSupportingDoc) {
		this.isSupportingDoc = isSupportingDoc;
	}
}
