package com.daffodil.documentumie.businessoperationprocessorbean;

import java.util.Map;

import com.daffodil.documentumie.dctm.api.CSServices;
import com.daffodil.documentumie.fileutil.logger.IELogger;
import com.daffodil.documentumie.fileutil.metadata.api.MetadataReader;
import com.daffodil.documentumie.fileutil.metadata.api.MetadataWriter;

public class ExportProcessorBean {

	private CSServices cServicesProvider  = null;
	private MetadataWriter metadataWriter  = null;
	private MetadataReader metadataReader = null;
	private String objectType;
	private boolean updateExisting; 
	private boolean continueImport;
	private int objectTypeHierarchy;
	private String objectId;
	private IELogger IELogger=null;
	private String metaDataFilePath;
	private String outputFile;
	private Map metadataMap;
	
	public CSServices getcServicesProvider() {
		return cServicesProvider;
	}
	public void setcServicesProvider(CSServices cServicesProvider) {
		this.cServicesProvider = cServicesProvider;
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
	public void setIELogger(IELogger iELogger) {
		IELogger = iELogger;
	}
	public String getMetaDataFilePath() {
		return metaDataFilePath;
	}
	public void setMetaDataFilePath(String metaDataFilePath) {
		this.metaDataFilePath = metaDataFilePath;
	}
	public String getOutputFile() {
		return outputFile;
	}
	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}
	public Map getMetadataMap() {
		return metadataMap;
	}
	public void setMetadataMap(Map metadataMap) {
		this.metadataMap = metadataMap;
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

	
	
	
}
