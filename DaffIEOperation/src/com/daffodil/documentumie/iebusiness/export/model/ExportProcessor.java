package com.daffodil.documentumie.iebusiness.export.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import jxl.Workbook;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import com.daffodil.documentumie.businessoperationprocessorbean.ExportProcessorBean;
import com.daffodil.documentumie.dctm.api.CSServices;
import com.daffodil.documentumie.dctm.exception.DDfException;
import com.daffodil.documentumie.fileutil.configurator.DaffIEConfigurator;
import com.daffodil.documentumie.fileutil.configurator.exception.DConfigReadException;
import com.daffodil.documentumie.fileutil.logger.IELogger;
import com.daffodil.documentumie.fileutil.metadata.MetadataProcessorFactory;
import com.daffodil.documentumie.fileutil.metadata.api.MetadataWriter;
import com.daffodil.documentumie.fileutil.metadata.exception.DMetadataWriteException;
import com.daffodil.documentumie.fileutil.properties.PropertyFileLoader;
import com.daffodil.documentumie.iebusiness.IEUtility;
import com.daffodil.documentumie.iebusiness.Log;
import com.daffodil.documentumie.iebusiness.export.bean.ExportServiceBean;
import com.daffodil.documentumie.iebusiness.export.processor.ExportBusinessOperationProcessor;
import com.daffodil.documentumie.iebusiness.pimport.model.ImportBusinessOperationProcessor;
import com.daffodil.documentumie.scheduleie.bean.EScheduleConfigBean;
import com.documentum.fc.client.distributed.replica.impl.refresh.operation.SysObjectCopyOperation;

public class ExportProcessor {

	private ExportServiceBean exportServiceBean;	
	private ExportProcessorBean exportProcessorBean;
	private CSServices csServiceProvider;
	private MetadataWriter metadataWriter;
	private IELogger IELogger;
	private int success = 0;
	private int failure = 0;
	private int totalCounter = 0;
	private int processCounter = 0;
	private static boolean exportCanceled = false;
	private boolean requestedForExportStopped = false;

	private int progressBarMaxValue = 0;

	private int progressBarCurrentValue = 0;

	private boolean exportFinished = false;

	private String currentlyProcessingFileName = null;

	private String processMessage;

	private List File_for_ZIP;

	PropertyChangeSupport propertyChangeSupport;

	private String error;
	
	private String reportFileGlobalPath=null;
	
	private EScheduleConfigBean eScheduleConfigBean = null;

	private WritableWorkbook workbook =null;
	
	public String getError() {
		return error;
	}
	
	public void setEScheduleConfigBean(EScheduleConfigBean scheduleConfigBean) {
		eScheduleConfigBean = scheduleConfigBean;
	}

	public void setError(String error) {
		String old = this.error;
		this.error = error;
		propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this,
				"error", old, error));

	}

	public ExportProcessor () {
		propertyChangeSupport = new PropertyChangeSupport(this);
		if(File_for_ZIP == null){
			File_for_ZIP = new ArrayList();
		}
	}

	public void doExport() throws DDfException{	
		
		setExportProcessorBean(new ExportProcessorBean());
		// The below commented code is the default code	
		//String path = getExportServiceBean().getMetaDataFilePath();

	
	/*	setLogFilePath_export(path.replace('\\', '/')
				+ "/Export_" + IEUtility.getDateAndTimeString() + ".log");*/
		
		File tempDir = new File("temps");
		if(!tempDir.exists()){
			tempDir.mkdirs();
		}
		String path =tempDir.getAbsolutePath();
		setLogFilePath_export(path+ "/Export_" + IEUtility.getDateAndTimeString() + ".log");
				

		try {
			Log.setLogFilePath(getLogFilePath_export());
			
		} catch (IOException e) {
			setError(e.getMessage() + " " + e.getCause());
			throw new DDfException(e.getMessage()+ " on location " + path, e.getCause());

		}

		Log.logFile("User name: \t" + getExportServiceBean().getUsername()
				+ "\n"); // User Name
		Log.logFile("Repository name: \t"
				+ getExportServiceBean().getRepository() + "\n"); // Rep
		// Name
		Log.logFile("Start time: \t" + IEUtility.getTime() + "\n"); // Start
		// Time

		String extension = getExportServiceBean().getReportType();
		if(extension.equalsIgnoreCase("xls")){
			creaeXLSFile(path, extension);
		}if(extension.equalsIgnoreCase("csv")){
			System.out.println("creaeCSVFile");
			creaeCSVFile(path, extension);
		}if(extension.equalsIgnoreCase("xml")){
			creaeXMLFile(path, extension);
		}

		/**
		 * Prepare DQL to get no. of Records for the export
		 */
		String dqlString = ""; 
		dqlString = prepareDql();
		System.out.println("DQL String"+dqlString);
		List metadatalist =  getCsServiceProvider().executeDQLObject(dqlString, exportServiceBean.getObjectType());
		System.out.println("---metadatalist-------"+metadatalist);
	
		getIELogger().writeLog("metadatalist= "+metadatalist, IELogger.DEBUG); //---------------------------------
		
		Object id;
		success = 0;
		failure = 0;
		totalCounter =0;
		totalCounter = metadatalist.size();
		setProgressBarMaxValue(totalCounter);
		try {
			List attrs = exportServiceBean.getSelectedAttribute();
		
			
			List attributesForExport = new ArrayList();
			for (int i = 0; i < attrs.size(); i++) {
				if(attrs.get(i).equals("r_folder_path")){
					continue;
				}
				else{
					attributesForExport.add(attrs.get(i));
				}
				getIELogger().writeLog("attributesForExport is   "+ attrs.get(i), IELogger.DEBUG);
			}
			attributesForExport.add("r_folder_path");
			attributesForExport.add("file_source_location__");
			attributesForExport.add("Export_Status");
			attributesForExport.add("Error_Description");
			
			
			if (extension.equalsIgnoreCase("xml")) {
				List inputValue = new ArrayList();
				inputValue.add("Export");
				inputValue.add(exportServiceBean.getObjectType());
				getMetadataWriter().wirteAttributes(inputValue);
				getIELogger().writeLog("inputValue is   "+ inputValue, IELogger.DEBUG);

			}else{
				System.out.println("************************************************************");
				getMetadataWriter().wirteAttributes(attributesForExport);
			}
			
			getIELogger().writeLog("Writing Attributes in Report File "+ attributesForExport, IELogger.DEBUG); 
			
		attributesForExport.clear();
		} catch (DMetadataWriteException e) {
			getIELogger().writeLog("Exception while writing Attributes in Report File "+ e.getMessage() + e.getCause(), IELogger.DEBUG);
		}

		// Export Process start
		int noOfRecordsForExport = metadatalist.size();
		getIELogger().writeLog("noOfRecordsForExport in Report File "+ noOfRecordsForExport, IELogger.DEBUG); 
		/*if(noOfRecordsForExport > 100){
			noOfRecordsForExport = 100;
		}*/
		String processorType = PropertyFileLoader.loadUtilityConfigPropertyFile().getProperty(getExportServiceBean().getObjectType() + "_exportProcessor");
		System.out.println("getExportServiceBean().getObjectType()--"+getExportServiceBean().getObjectType());
		System.out.println("Processor type in Export Processor is--"+processorType);
		
		getIELogger().writeLog("getExportServiceBean().getObjectType()-- "+ getExportServiceBean().getObjectType(), IELogger.DEBUG);
		getIELogger().writeLog("Processor type in Export Processor is---- "+ processorType, IELogger.DEBUG);
		ExportBusinessOperationProcessor businessProcessor = null;
		if (processorType != null) {
			businessProcessor = LoadProcessor(processorType, businessProcessor);
		}
		
		for(int objectDoc = 0 ; objectDoc < noOfRecordsForExport ; objectDoc++){
			while (isRequestedForExportStopped()) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			if (!isExportCancelled()) {
				System.out.println("*exportServiceBean.isOnlyMetadata()"+exportServiceBean.isOnlyMetadata());
				System.out.println("*exportServiceBean.isAllVersion()"+exportServiceBean.isAllVersion());
				System.out.println("*exportServiceBean.isExportIntoZIP()"+exportServiceBean.isExportIntoZIP());
				processCounter = objectDoc+1;
				HashMap map = (HashMap) metadatalist.get(objectDoc);
				setProgressBarCurrentValue(processCounter);
				
				getIELogger().writeLog("processCounter is- "+ processCounter, IELogger.DEBUG);
				
				id = map.get("r_object_id");
				getIELogger().writeLog("map "+ map, IELogger.DEBUG); 
				getIELogger().writeLog("id "+ id, IELogger.DEBUG); 
				
				getExportProcessorBean().setcServicesProvider(getCsServiceProvider());
				getExportProcessorBean().setMetaDataFilePath(getExportServiceBean().getMetaDataFilePath());
				getExportProcessorBean().setOutputFile(getExportServiceBean().getOutPutFile());
				getExportProcessorBean().setMetadataMap(map);				
				if (processorType != null) {
					businessProcessor.preExportProcess(getExportProcessorBean());
				}
				
				if (exportServiceBean.isOnlyMetadata()) {
					System.out.println("*exportServiceBean.isOnlyMetadata()"+exportServiceBean.isOnlyMetadata());
					String object_name = (String) map.get("object_name");
					getIELogger().writeLog("object_name is-- "+ object_name, IELogger.DEBUG);
					setCurrentlyProcessingFileName(object_name);
					map.put("Export_Status", "");
					map.put("Error_Description", "");
					getIELogger().writeLog("map "+ map, IELogger.DEBUG); 
					writeToReportFile(map);
					success++;
				}/*else {			
					exportContent(id, map);
				}*/
				if(exportServiceBean.isAllVersion()){ //ADDED BY PALLAVI FOR EXPORTING TO ALL VERSION
					exportContent(id, map);
				
				}
				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
					getIELogger().writeLog(e.getMessage() + e.getCause(),IELogger.ERROR);
				}
				map.clear();
			}
			
		}
		
		
	  if(exportServiceBean.isExportIntoZIP()){            //ADDED BY PALLAVI FOR EXPORTING TO ZIP
			exportInZIP();
		}
		Log.logFile("Total Record: \t" + totalCounter + "\n");
		if (isExportCancelled()) {
			Log.logFile("Export process stopped on row \t"
					+ processCounter + "\n");
		}
		Log.logFile("No of failure: \t" + failure + "\n");
		Log.logFile("No of sucess: \t" + success + "\n");
		Log.logFile("End time: \t" + IEUtility.getTime() + "\n"); // End
		// Time
		Log.logCommit();
		// The below code closes the workbook opened for export metadata in case of Operating System other than Windows
				/*try {
					if(workbook!=null){
					workbook.write();
					workbook.close();
					}
				} catch (WriteException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}*/
		//Below code is written by Harsh for exporting the Metadata file to FTP
		
		try{
			csServiceProvider.getContentFileHandler().saveFile(reportFileGlobalPath, getExportServiceBean().getMetaDataFilePath());
			csServiceProvider.getContentFileHandler().deleteFile(reportFileGlobalPath);
		}catch( DDfException e)
		{
			throw new DDfException(e.getMessage()); 
		}
		
		// The above code is written by Harsh for the implementation of the FTP functionality
		setExportFinished(true);
	}
	private ExportBusinessOperationProcessor LoadProcessor(String processorType, ExportBusinessOperationProcessor businessProcessor) {
		Class c;
		try {
			c = Class.forName(processorType);
			businessProcessor = (ExportBusinessOperationProcessor) c.newInstance();
		} catch (ClassNotFoundException e) {
			getIELogger().writeLog("Load Processor Error" + e.getMessage(), IELogger.DEBUG);
			e.printStackTrace();
		} catch (InstantiationException e) {
			getIELogger().writeLog("Load Processor Error" + e.getMessage(), IELogger.DEBUG);
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			getIELogger().writeLog("Load Processor Error" + e.getMessage(), IELogger.DEBUG);
			e.printStackTrace();
		}
		return businessProcessor;
	}

	private void creaeXMLFile(String path, String extension) {
		String reportFileLocation = path+"\\";
		String reportFileName = "Export_" + IEUtility.getDateAndTimeString()+ ".xml";
		reportFileGlobalPath = reportFileLocation+reportFileName;
		System.out.println("--reportFileGlobalPath-->"+reportFileGlobalPath);
		initXMLMetadataWriter(reportFileLocation, reportFileName, extension);
	}

	private void initXMLMetadataWriter(String reportFileLocation, String reportFileName, String extension) {
		HashMap map = new HashMap();
		map.put("File_Name", reportFileLocation);
		map.put("Table_Name", reportFileName);
		map.put("extension", extension);
		setMetadataWriter(MetadataProcessorFactory.getMetadataWriter(map));		
	}


	private void creaeCSVFile(String path, String extension) {
		String reportFileLocation = path+"\\";
		String reportFileName = "Export_" + IEUtility.getDateAndTimeString() + ".csv";
		reportFileGlobalPath =reportFileLocation+reportFileName;
		System.out.println("reportFileName-->"+reportFileName+"--reportFileLocation-->"+reportFileLocation);
		initCSVMetadataWriter(reportFileLocation, reportFileName, extension);

	}

	private void initCSVMetadataWriter(String reportFileLocation, String reportFileName, String extension) {
		HashMap map = new HashMap();
		map.put("File_Name", reportFileLocation);
		map.put("Table_Name", reportFileName);
		map.put("extension", extension);
		setMetadataWriter(MetadataProcessorFactory.getMetadataWriter(map));

	}

	private void creaeXLSFile(String path, String extension) {
		WritableSheet worksheet = null;
		String reportFileName = "";
		if(System.getProperty("os.name").contains("Window"))
		{
		reportFileName = path + "\\Export_" + IEUtility.getDateAndTimeString() + ".xls";
		}
		else{
			reportFileName = path + "/Export_" + IEUtility.getDateAndTimeString() + ".xls";
		}
		reportFileGlobalPath = reportFileName;
		
		getIELogger().writeLog("reportFileName "+ reportFileName, IELogger.DEBUG); 
		
		try {
			workbook = Workbook.createWorkbook(new File(reportFileName));
			worksheet = workbook.createSheet(exportServiceBean.getObjectType(), 0);
			if(System.getProperty("os.name").contains("Window"))
			{
				initExcelMetadataWriter(reportFileName, exportServiceBean.getObjectType(), extension);
				workbook.write();
				workbook.close();
			}
			else{
				initExcelMetadataWriterForLinux(reportFileName, exportServiceBean.getObjectType(), extension,worksheet,workbook);
			}
		} catch (IOException e) {
			getIELogger().writeLog("Exception -- "+ e.getMessage()+" "+e.getCause(), IELogger.DEBUG);
			e.printStackTrace();
		}catch (WriteException e) {
			getIELogger().writeLog("Exception -- "+ e.getMessage()+" "+e.getCause(), IELogger.DEBUG);
			e.printStackTrace();
		}
	}

	private void initExcelMetadataWriter(String filePath, String sheet, String extension) {
		HashMap map1 = new HashMap();
		map1.put("extension", extension);
		map1.put("File_Name", filePath);
		map1.put("Table_Name", sheet);
		setMetadataWriter(MetadataProcessorFactory.getMetadataWriter(map1));
	}
	private void initExcelMetadataWriterForLinux(String filePath, String sheet, String extension,WritableSheet worksheet,WritableWorkbook workbook) {
		HashMap map = new HashMap();
		map.put("File_Name", filePath);
		map.put("Table_Name", sheet);
		map.put("extension", extension);
		map.put("worksheet",worksheet);
		map.put("workbook",workbook);
		setMetadataWriter(MetadataProcessorFactory.getMetadataWriterForLinux(map));
	}
	String logFilePath;
	public String getLogFilePath_export() {
		return logFilePath;
	}

	private void setLogFilePath_export(String filePath) {
		this.logFilePath = filePath;

	}

	private String prepareDql() {
		List attributes = getExportServiceBean().getSelectedAttribute();
		String whereClause = getExportServiceBean().getDqlText();
		String objectType = getExportServiceBean().getObjectType();

		StringBuffer dql = new StringBuffer();
		dql.append("select ");
		for (int i = 0; i < attributes.size(); i++) {
			dql.append(attributes.get(i));
			if(i<attributes.size()-1){
				dql.append(", ");
			}
		}
		dql.append(" from " + objectType);
		if(whereClause != null && !"".equalsIgnoreCase(whereClause)){
			dql.append(" where " + whereClause);
		}
		return dql.toString();
	}

	private void exportContent(Object doc_id, HashMap objectMap) {
		//List zip = new ArrayList();

		try {
			if(!getExportServiceBean().isAllVersion()){
				exportAccordingToType(doc_id, objectMap, false);
				/*objectMap.put("Export_Status", "Export Proceess Successfuly Completed");
				objectMap.put("Error_Description", "");
				writeToExcelFile(objectMap);
				success++;*/
				getIELogger().writeLog("objectMap "+ objectMap, IELogger.DEBUG); 
				success++;
			}else{
				if(getExportServiceBean().isAllVersion()){
					Object id;
					List allVersion =  csServiceProvider.getAllVersion(doc_id);
					for(int objectDoc = 0 ; objectDoc < allVersion.size() ; objectDoc++){
						HashMap map = (HashMap) allVersion.get(objectDoc);
						id = map.get("r_object_id");
						exportAccordingToType(id, objectMap, true);
						
						getIELogger().writeLog("getExportServiceBean().isAllVersion()-- "+ getExportServiceBean().isAllVersion(), IELogger.DEBUG);
						
						/*objectMap.put("Export_Status", "Export Proceess Successfuly Completed");
						objectMap.put("Error_Description", "");
						writeToExcelFile(objectMap);
						success++;*/
						getIELogger().writeLog("objectMap "+ objectMap, IELogger.DEBUG); 
						success++;
					}
				}
			}
		} catch (DDfException e) {
			objectMap.put("Export_Status", "Export Proceess Failed");
			objectMap.put("Error_Description", e.getMessage());
			failure++; 
			writeToReportFile(objectMap);
		}
	}

	private void exportAccordingToType(Object doc_id, HashMap objectMap, boolean version){
		String fileName = null;
		String dosExtension;
		System.out.println("DOC ID is:-"+doc_id.toString());
		/*if(doc_id.toString().equals("090f42408002c41b"))
		{
			doc_id = "090f424080042e0e";
		}*/
		//String destPath = getDestinationFolderHierarchy(doc_id);
		String exportType = getExportServiceBean().getExportType();
		String folderPath = getFolderHierchyFromRepo(doc_id);
		// Below code is modified by harsh for the implementation of the FTP functionality on 12/30/2011
		String metadataPath = getExportServiceBean().getMetaDataFilePath();
		String contentPath  = getExportServiceBean().getOutPutFile();
		File makePath = new File("temps");
		String tempDirPath = makePath.getAbsolutePath();
		String outputDirStruct = getExportProcessorBean().getOutputFile();
		
		//String destPath = contentPath+"/"+folderPath;// Initially this was the destination path 
		String destPath = contentPath;// Now this is the destination path
		
		System.out.println("Metadata File Path Export Processor is:"+metadataPath+":");
		System.out.println("Output Directory Path is:"+contentPath+":");
		System.out.println("Dest Path in Export Processor is"+destPath);
		
		
		getIELogger().writeLog("exportType is  -- "+ exportType, IELogger.DEBUG);
		getIELogger().writeLog("folderPath is -- "+ folderPath, IELogger.DEBUG);
		
		getIELogger().writeLog("Metadata File Path Export Processor -- "+ metadataPath, IELogger.DEBUG);
		getIELogger().writeLog("Output Directory Path is:-- "+ contentPath, IELogger.DEBUG);
		getIELogger().writeLog("Dest Path in Export Processor is -- "+ destPath, IELogger.DEBUG);
		
		
		
		try{
			if(exportType.equalsIgnoreCase("native")){
				dosExtension = csServiceProvider.getDosExtensionById(doc_id);
				fileName = csServiceProvider.getFileNameByID(doc_id, version, dosExtension);
				setCurrentlyProcessingFileName(fileName);
				csServiceProvider.exportObject(doc_id, tempDirPath, fileName,destPath);
				
				File_for_ZIP.add(fileName);
				objectMap.put("r_folder_path", folderPath);
				if(!getExportServiceBean().isExportIntoZIP()){
				objectMap.put("file_source_location__", metadataPath);
				//objectMap.put("file_source_location__", tempDirPath+"/"+fileName);//---------------------------------------------
				}
				objectMap.put("Export_Status", "Export Proceess Successfuly Completed");
				objectMap.put("Error_Description", "");
				
				System.out.println("***objectMap for native path***"+objectMap);
				getIELogger().writeLog("objectMap for native path  -- "+ objectMap, IELogger.DEBUG);
				
				writeToReportFile(objectMap);

			}if(exportType.equalsIgnoreCase("rendition")){
				List renditionsList = csServiceProvider.getRenditions(doc_id);
				for (int i = 0; i < renditionsList.size(); i++) {
					String renditionExtensiion = (String) renditionsList.get(i);
					dosExtension = csServiceProvider.getDOSExtension(renditionExtensiion);
					fileName = csServiceProvider.getFileNameByID(doc_id, version, dosExtension);
					setCurrentlyProcessingFileName(fileName);
					csServiceProvider.exportObject(doc_id, tempDirPath, fileName,destPath);
					File_for_ZIP.add(fileName);
					objectMap.put("r_folder_path", folderPath);
					if(!getExportServiceBean().isExportIntoZIP()){
						objectMap.put("file_source_location__", metadataPath);
					//objectMap.put("file_source_location__", tempDirPath+"/"+fileName);//--------------------------------------
					}
					objectMap.put("Export_Status", "Export Proceess Successfuly Completed");
					objectMap.put("Error_Description", "");
					
					System.out.println("***objectMap for rendition***"+objectMap);
					getIELogger().writeLog("objectMap for rendition  -- "+ objectMap, IELogger.DEBUG);
					
					writeToReportFile(objectMap);
				}
			}if(exportType.equalsIgnoreCase("both")){
				dosExtension = csServiceProvider.getDosExtensionById(doc_id);
				fileName = csServiceProvider.getFileNameByID(doc_id, version, dosExtension);
				setCurrentlyProcessingFileName(fileName);
				csServiceProvider.exportObject(doc_id, tempDirPath, fileName,destPath);
				objectMap.put("r_folder_path", folderPath);
				if(!getExportServiceBean().isExportIntoZIP()){
					objectMap.put("file_source_location__", metadataPath);
				//objectMap.put("file_source_location__", tempDirPath+"/"+fileName);//----------------------------
				}
				objectMap.put("Export_Status", "Export Proceess Successfuly Completed");
				objectMap.put("Error_Description", "");
				writeToReportFile(objectMap);
				File_for_ZIP.add(fileName);

				List renditionsList = csServiceProvider.getRenditions(doc_id);
				for (int i = 0; i < renditionsList.size(); i++) {
					String renditionExtensiion = (String) renditionsList.get(i);
					dosExtension = csServiceProvider.getDOSExtension(renditionExtensiion);
					fileName = csServiceProvider.getFileNameByID(doc_id, version, dosExtension);
					setCurrentlyProcessingFileName(fileName);
					csServiceProvider.exportObject(doc_id, tempDirPath, fileName,destPath);
					objectMap.put("r_folder_path", folderPath);
					if(!getExportServiceBean().isExportIntoZIP()){
						objectMap.put("file_source_location__", metadataPath);
					//objectMap.put("file_source_location__", tempDirPath+"/"+fileName);//--------------------------------------
					}
					objectMap.put("Export_Status", "Export Proceess Successfuly Completed");
					objectMap.put("Error_Description", "");
					
					getIELogger().writeLog("objectMap for both  -- "+ objectMap, IELogger.DEBUG);
					System.out.println("***objectMap for both***"+objectMap);
					
					writeToReportFile(objectMap);
					File_for_ZIP.add(fileName);
				}
			}
		}
		catch (DDfException e) {
			objectMap.put("r_folder_path", folderPath);
			if(!getExportServiceBean().isExportIntoZIP()){
				objectMap.put("file_source_location__", metadataPath);
			//objectMap.put("file_source_location__", tempDirPath+fileName);//---------------------------------------
			}
			objectMap.put("Export_Status", "Export Proceess Failed");
			objectMap.put("Error_Description", e.getMessage());
			writeToReportFile(objectMap);
			getIELogger().writeLog("Exception -- "+ e.getMessage()+" "+e.getCause(), IELogger.DEBUG);
		}
	}

	private void writeToReportFile(HashMap map) {
		try {
			getMetadataWriter().writeRow(map);
	     			
		} catch (DMetadataWriteException e) {
			e.getMessage();
			e.getCause();
			e.printStackTrace();
		getIELogger().writeLog(e.getMessage()+" "+e.getCause(), IELogger.DEBUG);
		}
	}

	private String getDestinationFolderHierarchy(Object obj){
		String folderPath = getFolderHierchyFromRepo(obj);
		String exportLocation = null;
		if(getExportServiceBean().isExportIntoZIP()){
			exportLocation = "c:/Documentum";
		}else{
			//exportLocation = (getExportServiceBean().getOutPutFile()).replace('\\', '/');
			exportLocation = (getExportServiceBean().getOutPutFile());
			
		}
		getIELogger().writeLog("exportLocation -- "+ exportLocation, IELogger.DEBUG);
		
		//exportLocation += File.separator;
		/*exportLocation += "/_temp_";
		File f1 = new File(exportLocation);
		// if directory doesn't exist, create it

		if (!f1.exists()) {
			f1.delete();
		}
		//exportLocation += File.separator;
		exportLocation += folderPath.replace("\\", "/");
		File f = new File(exportLocation);
		// if directory doesn't exist, create it

		if (!f.exists()) {

			f.mkdirs();
		}*/

		return exportLocation;
	}

	private String getFolderHierchyFromRepo(Object obj){
		try {
			String folderPath = getCsServiceProvider().getFolderPath(obj);
			return folderPath;
		} catch (DDfException e) {
			getIELogger().writeLog(e.getMessage()+ " " + e.getCause(), IELogger.DEBUG);
		}
		return null;
	}

	private void exportInZIP() throws DDfException {
		if (getExportServiceBean().isExportIntoZIP()) {
			int temp = progressBarCurrentValue;
			setProgressBarCurrentValue(0);
			setCurrentlyProcessingFileName("Creating Zip....");
			setProgressBarCurrentValue(temp);
			String destZipLocation = getExportServiceBean().getOutPutFile()+File.separator+"_temp_"+".zip";
			String srcFolderLocation = getExportServiceBean().getOutPutFile();// "c:/Documentum/_temp_";
			System.out.println("temp--"+temp);
			System.out.println("destZipLocation---"+destZipLocation+"    srcFolderLocation--"+srcFolderLocation);
			
			try {
				CreatingZip(srcFolderLocation, destZipLocation);
				File folder = new File(srcFolderLocation);
				if (folder.exists()) {
					folder.delete();
				}
			} catch (Exception e) {
				System.out.println("EXCEPTION"+e);
				//getIELogger().writeLog("Exception -- "+ e.getMessage()+" "+e.getCause(), IELogger.DEBUG);
				throw new DDfException(e.getMessage(), e.getCause());
				
			}
			int temp1 = progressBarCurrentValue;
			setProgressBarCurrentValue(0);
			setCurrentlyProcessingFileName("Zip successfully created");
			setProgressBarCurrentValue(temp1);
			
		}
	}

	private void CreatingZip(String srcFolder, String destZipFile) throws Exception {
		ZipOutputStream zip = null;
		FileOutputStream fileWriter = null;

		fileWriter = new FileOutputStream(destZipFile);
		zip = new ZipOutputStream(fileWriter);

		addFolderToZip("", srcFolder, zip);
		zip.flush();
		zip.close();
		fileWriter.flush();
		fileWriter.close();
	}

	private void addFileToZip(String path, String srcFile, ZipOutputStream zip)
	throws Exception {

		File folder = new File(srcFile);
		if (folder.isDirectory()) {
			addFolderToZip(path, srcFile, zip);
		} else {
			byte[] buf = new byte[1024];
			int len;
			FileInputStream in = new FileInputStream(srcFile);
			zip.putNextEntry(new ZipEntry(path + "/" + folder.getName()));
			while ((len = in.read(buf)) > 0) {
				zip.write(buf, 0, len);
			}
			in.close();
		}
		
		if (folder.exists()) {
			folder.delete();
		}
		folder = null;
	}

	private void addFolderToZip(String path, String srcFolder, ZipOutputStream zip)
	throws Exception {
		File folder = new File(srcFolder);
		if(folder==null || !folder.exists())
		{
			folder.mkdirs();
		}
		for (String fileName : folder.list()) {
			if (path.equals("") && !fileName.endsWith("zip")) {
				addFileToZip(folder.getName(), srcFolder + "/" + fileName, zip);
			} else {
				System.out.println(path + "/" + folder.getName()+ srcFolder + "/" + fileName);
				//addFileToZip(path + "/" + folder.getName(), srcFolder + "/" + fileName, zip);
			}
		}
	}

	public static boolean isExportCancelled() {
		return exportCanceled;
	}

	public static void setExportCancelled(boolean exportCancelled) {
		ExportProcessor.exportCanceled = exportCancelled;
	}

	public CSServices getCsServiceProvider() {
		return csServiceProvider;
	}

	public void setCsServiceProvider(CSServices csserviceProvider) {
		this.csServiceProvider = csserviceProvider;
	}

	public ExportServiceBean getExportServiceBean() {
		return exportServiceBean;
	}

	public void setExportServiceBean(ExportServiceBean exportServiceBean) {
		this.exportServiceBean = exportServiceBean;
	}

	public MetadataWriter getMetadataWriter() {
		return metadataWriter;
	}

	public void setMetadataWriter(MetadataWriter metadataWriter) {
		this.metadataWriter = metadataWriter;
	}

	/*public IELogger getIELogger() {
		return IELogger;
	}

	public void setIELogger(IELogger logger) {
		IELogger = logger;
	}*/
	
	
	public IELogger getIELogger() {
		try {
			return new IELogger(DaffIEConfigurator.read(2));
		} catch (DConfigReadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void setIELogger(IELogger ieLogger) {
		this.IELogger = ieLogger;
	}
	

	public void setRequestedForExportStopped(boolean b) {
		this.requestedForExportStopped = b;
	}

	public boolean isRequestedForExportStopped() {
		return requestedForExportStopped;
	}

	String logFilePath_export;

	public void registerPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	public void setProcessMessage(String pProcessMessage) {
		String oldValue = this.processMessage;
		this.processMessage = pProcessMessage;

		propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this,
				"processMessage", oldValue, pProcessMessage));
	}

	public long getProgressBarMaxValue() {
		return progressBarMaxValue;
	}

	public void setProgressBarMaxValue(int progressBarMaxValue) {
		int oldValue = this.progressBarMaxValue;
		this.progressBarMaxValue = progressBarMaxValue;
		propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this,
				"progressBarMaxValue", oldValue, progressBarMaxValue));
	}

	public long getProgressBarCurrentValue() {
		return progressBarCurrentValue;
	}

	public void setProgressBarCurrentValue(int progressBarCurrentValue) {
		int oldValue = this.progressBarCurrentValue;
		this.progressBarCurrentValue = progressBarCurrentValue;
		propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this,
				"progressBarCurrentValue", oldValue, progressBarCurrentValue));
	}

	public boolean isExportFinished() {
		return exportFinished;
	}

	public void setExportFinished(boolean exportFinished) {
		boolean oldValue = this.exportFinished;
		this.exportFinished = exportFinished;
		propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this,
				"exportFinished", oldValue, exportFinished));
	}

	public String getCurrentlyProcessingFileName() {
		return currentlyProcessingFileName;
	}

	public void setCurrentlyProcessingFileName(
			String currentlyProcessingFileName) {
		this.currentlyProcessingFileName = currentlyProcessingFileName;
	}

	public ExportProcessorBean getExportProcessorBean() {
		return exportProcessorBean;
	}

	public void setExportProcessorBean(ExportProcessorBean exportProcessorBean) {
		this.exportProcessorBean = exportProcessorBean;
	}

}

