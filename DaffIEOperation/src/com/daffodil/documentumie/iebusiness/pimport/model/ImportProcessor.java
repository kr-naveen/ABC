package com.daffodil.documentumie.iebusiness.pimport.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.log4j.Logger;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import com.daffodil.documentumie.businessoperationprocessorbean.ImportProcessorBean;
import com.daffodil.documentumie.dctm.api.CSServices;
import com.daffodil.documentumie.dctm.exception.DDfException;
import com.daffodil.documentumie.filehandler.ContentFileHandler;
import com.daffodil.documentumie.fileutil.configurator.DaffIEResumeConfigurator;
import com.daffodil.documentumie.fileutil.configurator.bean.ImportConfigBeanForResume;
import com.daffodil.documentumie.fileutil.logger.IELogger;
import com.daffodil.documentumie.fileutil.metadata.MetadataProcessorFactory;
import com.daffodil.documentumie.fileutil.metadata.api.MetadataReader;
import com.daffodil.documentumie.fileutil.metadata.api.MetadataWriter;
import com.daffodil.documentumie.fileutil.metadata.exception.DMetadataReadException;
import com.daffodil.documentumie.fileutil.metadata.exception.DMetadataWriteException;
import com.daffodil.documentumie.fileutil.properties.PropertyFileLoader;
import com.daffodil.documentumie.iebusiness.IEMainAbstractUIControl;
import com.daffodil.documentumie.iebusiness.IEUtility;
import com.daffodil.documentumie.iebusiness.Log;
import com.daffodil.documentumie.iebusiness.pimport.bean.ImportServiceBean;
import com.daffodil.documentumie.iebusiness.pimport.view.ImportMainUIControl;
import com.daffodil.documentumie.iebusiness.pimport.model.ImportBusinessOperationProcessor;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfId;

public class ImportProcessor {
	private ImportServiceBean importServiceBean;

	private MetadataReader metadatreader;

	private MetadataWriter metadatawriter;

	private CSServices csserviceprovider;

	private ImportMainUIControl mainUIControl;

	private ImportConfigBeanForResume importConfigBeanForResume;

	private IELogger IELogger;

	private PropertyChangeSupport propertyChangeSupport;

	private ImportProcessorBean importProcessorBean;

	private String logFilePath_import = null;

	private boolean importCancelled = false;

	private boolean requestedForImportStopped = false;

	private int progressBarMaxValue = 0;

	private int progressBarCurrentValue = 0;

	private boolean importFinished = false;

	private String currentlyProcessingFileName = null;

	private String processMessage;

	private boolean liveRun;

	private int Successful = 0;

	private int failure = 0;

	private int totalCounter = 0;

	private int processedCounter = 0;

	private String error;

	private Map<String, String> sftpLocationMap = new HashMap<String, String>();

	private WritableWorkbook workbook = null;

	private int haultProcessing = 0;

	public String getError() {
		return error;
	}

	public void setError(String error) {
		String old = this.error;
		this.error = error;
		propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this,
				"error", old, error));
	}

	public ImportProcessor() {
		propertyChangeSupport = new PropertyChangeSupport(this);
	}

	public void doImport() throws DDfException {
		setVarInitialValues();

		setImportProcessorBean(new ImportProcessorBean());
		String path = importServiceBean.getMetadataFilePath();
		int length = path.length();
		String substr = path.substring(0, length - 4);
		ArrayList FTPLocations = new ArrayList();
		setLogFilePath_import(substr.replace('\\', '/') + "_Import_"
				+ IEUtility.getDateAndTimeString() + ".log");

		try {
			Log.setLogFilePath(getLogFilePath_import());
		} catch (IOException e1) {
			e1.printStackTrace();
			setError(e1.getMessage() + " " + e1.getCause());
			throw new DDfException(e1.getMessage(), e1.getCause());
			// return;
		}

		Log.logFile("User name: \t" + getImportServiceBean().getUserName()
				+ "\n"); // User
		// Name
		Log.logFile("Repository name: \t"
				+ getImportServiceBean().getRepository() + "\n"); // Rep
		// Name
		Log.logFile("Start time: \t" + IEUtility.getTime() + "\n"); // Start

		Map metadtaAndMappedAttributesmap = importServiceBean.getMappedattributes();

		List inputMetadataAttrList = new ArrayList();

		getIELogger().writeLog("metadtaAndMappedAttributesmap "+ metadtaAndMappedAttributesmap, IELogger.DEBUG);

		for (Iterator iterator = metadtaAndMappedAttributesmap.entrySet().iterator(); iterator.hasNext();) {
			Map.Entry entry = (Map.Entry) iterator.next();
			String key = entry.getKey().toString();
			inputMetadataAttrList.add(key);
		}
		/*
		 * if (!(getImportServiceBean().isUpdateExisting())) {
		 * inputMetadataAttrList.add("is_minor__"); }
		 */

		List rows = new ArrayList();

		try {

			getIELogger().writeLog("inputMetadataAttrList " + inputMetadataAttrList,IELogger.DEBUG);
			
			getIELogger().writeLog("importServiceBean.getSql() " + importServiceBean.getSql(),IELogger.DEBUG);
			// importServiceBean.getSql()
			rows = getMetadatReader().getRows(inputMetadataAttrList,importServiceBean.getSql());
		} catch (DMetadataReadException e) {
			getIELogger().writeLog("Exception while reading rows From Metadata File "+ e.getMessage() + e.getCause(), IELogger.DEBUG);
		}

		Map metadataMap = null;
		Map mapForImportProcessor = new HashMap();
		// Map metadtaMapForReportFile = null;
		// Map metadataMapForImport = null;

		setProgressBarMaxValue(rows.size());
		totalCounter = rows.size();

		// check resum process
		boolean resume = false;
		resume = getImportServiceBean().isResume();
		int row = 0;
		int noOfRecordsForImport = rows.size();
		getIELogger().writeLog("no of records : " + noOfRecordsForImport,IELogger.DEBUG);
		int resumeRecordNo = getImportServiceBean().getRecordNo();

		HashMap rMap = new HashMap();
		for (Iterator iterator = metadtaAndMappedAttributesmap.entrySet()
				.iterator(); iterator.hasNext();) {
			Map.Entry entery = (Map.Entry) iterator.next();
			String key = entery.getKey().toString();
			rMap.put(key, metadtaAndMappedAttributesmap.get(key));
		}

		// PropertyFileLoader.loadUtilityConfigPropertyFile().getProperty("repo_folder_path");

		String processorType = PropertyFileLoader.loadUtilityConfigPropertyFile().getProperty(getImportServiceBean().getObjectType()+ "_importProcessor");
		System.out.println("load property name...................."+getImportServiceBean().getObjectType()+ "_importProcessor");
		System.out.println("getImportServiceBean().getObjectType()-------->>"+ getImportServiceBean().getObjectType());
		
		System.out.println("processorType----> " + processorType);
		ImportBusinessOperationProcessor businessProcessor = null;
		if (processorType != null) {
			businessProcessor = LoadProcessor(processorType, businessProcessor);
		}
		/*
		 * The below code is changed on 1/28/2012 for movement of documents in
		 * the Processed and Unprocessed folder
		 */
		String metadataFileSourceLocation = null;
		String repoLocation = null;
		String object_name = null;
		String batchprocessorType = PropertyFileLoader.loadUtilityConfigPropertyFile().getProperty(getImportServiceBean().getObjectType()+ "_batchProcessor");
		System.out.println("getImportServiceBean().getObjectType()_batchProcessor"+ getImportServiceBean().getObjectType());
		System.out.println("Batch processorType-----> " + batchprocessorType);
		BatchProcessor batchProcessor = null;
		if (batchprocessorType != null) {
			batchProcessor = LoadBatchProcessor(batchprocessorType,batchProcessor);
		}
		
		/*
		 * The Above code is changed on 1/28/2012 for movement of documents in
		 * the Processed and Unprocessed folder
		 */

		main: for (row = 0; row < noOfRecordsForImport; row++) {
			System.out.println("getImportProcessorBean()----"+getImportProcessorBean());
			getIELogger().writeLog(row + " rMap ---- " + rMap, IELogger.DEBUG);
			getImportProcessorBean().reset();
			haultProcessing = haultProcessing + 1;
			StringBuffer errormsg = new StringBuffer();
			while (isRequestedForImportStopped()) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if (isImportCancelled()) {
				break;
			} else if (haultProcessing <= 500) {// This hault Processing
												// condition is added for
												// haulting execution after 500
												// records
				if (row == 0) {
					if (resume) {
						row = resumeRecordNo;
					}
				}
				metadataMap = (HashMap) (rows.get(row));
				/*
				 * The below code is changed on 1/29/2012 for movement of
				 * documents in the Processed and Unprocessed folder
				 */
				for (Iterator iterator = metadataMap.entrySet().iterator(); iterator.hasNext();) {
					Map.Entry entry = (Map.Entry) iterator.next();

					if (entry.getKey() != null&& entry.getKey().toString().equalsIgnoreCase("file_source_location__")) {
						System.out.println("entry.getKey().toString() in Import"+ entry.getKey().toString());
						System.out.println("entry.getValue().toString() in Import"+ entry.getValue().toString());
						metadataFileSourceLocation = (String) entry.getValue();
						System.out.println("metadataFileSourceLocation*************>>"+metadataFileSourceLocation);
					}

					if (entry.getKey() != null && entry.getKey().toString().equalsIgnoreCase("r_folder_path")) {
						System.out.println("entry.getKey().toString() in Import"+ entry.getKey().toString());
						System.out.println("entry.getValue().toString() in Import"+ entry.getValue().toString());
						repoLocation = (String) entry.getValue();
					}

					if (entry.getKey() != null&& entry.getKey().toString().equalsIgnoreCase("object_name")) {
						System.out.println("entry.getKey().toString() in Import"+ entry.getKey().toString());
						System.out.println("entry.getValue().toString() in Import"+ entry.getValue().toString());
						object_name = (String) entry.getValue();
					}
				}
				File sourceFil = new File(metadataFileSourceLocation);
				if (object_name != null) {
					metadataMap.put("object_name", object_name);
				} else if (sourceFil != null && sourceFil.exists()) {
					metadataMap.put("object_name", sourceFil.getName());
				} else {
					metadataMap.put("object_name", null);
				}
				/*
				 * The Above code is changed on 1/29/2012 for movement of
				 * documents in the Processed and Unprocessed folder
				 */
				if (repoLocation != null) {
					metadataMap.put("r_folder_path", repoLocation);
				} else {
					metadataMap.put("r_folder_path",PropertyFileLoader.loadUtilityConfigPropertyFile().getProperty("repo_folder_path"));
				}
System.out.println("errormsg.toString()--"+errormsg.toString());
				getIELogger().writeLog(row + " metadataMap ---- " + metadataMap,IELogger.DEBUG);

				getImportProcessorBean().setMetadataMap(metadataMap);
				getImportProcessorBean().setMetadtaAndMappedAttributesMap(metadtaAndMappedAttributesmap);
				getImportProcessorBean().setObjectType(getImportServiceBean().getObjectType());
				getImportProcessorBean().setUpdateExisting(getImportServiceBean().isUpdateExisting());
				getImportProcessorBean().setObjectTypeHierarchy(getCsServiceprovider().validInputHeader(getImportServiceBean().getObjectType()));
				getImportProcessorBean().setMetaDataFilePath(getImportServiceBean().getMetadataFilePath());
				getImportProcessorBean().setCServicesProvider(getCsServiceprovider());
				getImportProcessorBean().setIELogger(getIELogger());
				getImportProcessorBean().setMetadataReader(getMetadatReader());
				getImportProcessorBean().setMetadataWriter(getMetadataWriter());

				if (processorType != null) {
					importProcessorBean = businessProcessor.preImportProcess(getImportProcessorBean());
				}

				metadataMap = getImportProcessorBean().getMetadataMap();
				getIELogger().writeLog("metadataMap : -- " + metadataMap,IELogger.DEBUG);
				String attrDateValue = (String) metadataMap.get("date"); // Added
																			// for
																			// Distributor
																			// Date
																			// Column
				if (attrDateValue != null) {
					metadataMap.remove("date");
					metadataMap.put("date_Reg", attrDateValue);
				}
				List reportFileHeader = getReportFileHeader(metadataMap);

				reportFileHeader.remove("processorError");

				reportFileHeader.add("Import_Status");
				reportFileHeader.add("Error_Description");
				reportFileHeader.add("Post_Process_Error");
				//following line is added by pallavi
				reportFileHeader.add("r_object_id");

				getIELogger().writeLog("reportFileHeader : " + reportFileHeader,IELogger.DEBUG);

				if (resume) {
					if (row == resumeRecordNo) {
						createReportFile(substr, reportFileHeader);
					}
				} else {
					if (row == 0) {
						createReportFile(substr, reportFileHeader);
					}
				}
				processedCounter = row + 1;
				setProgressBarCurrentValue(row + 1);
				String processorError = (String) metadataMap.get("processorError");
				getIELogger().writeLog("Processor error " + processorError,IELogger.DEBUG);
				if (processorError == null || "".equals(processorError)) {

					metadtaAndMappedAttributesmap = getImportProcessorBean().getMetadtaAndMappedAttributesMap();
					getIELogger().writeLog("metadtaAndMappedAttributesmap ...... "+ metadtaAndMappedAttributesmap,IELogger.DEBUG);
					getIELogger().writeLog("metadataMap ...... " + metadataMap,IELogger.DEBUG);
					metadtaAndMappedAttributesmap.put("r_folder_path","r_folder_path");
					metadtaAndMappedAttributesmap.put("object_name","object_name");
					for (Iterator iterator = metadtaAndMappedAttributesmap.entrySet().iterator(); iterator.hasNext();) {
						Map.Entry entery = (Map.Entry) iterator.next();
						String key = entery.getKey().toString();
						mapForImportProcessor.put(metadtaAndMappedAttributesmap.get(key),metadataMap.get(key));
					}
					getImportProcessorBean().setMetadataMap(mapForImportProcessor);
					getIELogger().writeLog("mapForImportProcessor : " + mapForImportProcessor,IELogger.DEBUG);
					mapForImportProcessor.remove("processorError");
					// Object[] objHierArray = isInputValid(reportFileHeader,
					// getImportProcessorBean().getObjectTypeHierarchy());

					/*
					 * boolean isValid = ((Boolean)
					 * objHierArray[0]).booleanValue();
					 * getIELogger().writeLog("isValid " + isValid,
					 * IELogger.DEBUG); if (!isValid) { // populate message
					 * getMainUIControl().showMessageDialog((String)
					 * objHierArray[1]); break main; }
					 */
					setCurrentlyProcessingFileName((String) mapForImportProcessor.get("object_name"));
					// setProgressBarCurrentValue(row + 1);
					System.out.println("---errormsg.toString()--"+errormsg.toString());
					// validate dctm attributes
					getIELogger().writeLog("errormsg " + errormsg,IELogger.DEBUG);
					try {
						/*
						 * Object[] validMsg =
						 * getCsServiceprovider().validateObject
						 * (mapForImportProcessor,
						 * getImportServiceBean().getObjectType());
						 * getIELogger().writeLog("validMsg " + validMsg,
						 * IELogger.DEBUG);
						 */
						getIELogger().writeLog("rows  " + rows.get(row).toString(),IELogger.DEBUG);
						// if ((Boolean) validMsg[0])
						{
							if (getImportProcessorBean().getObjectTypeHierarchy() == ImportConstant.SYSOBJECT_OR_CHILD) {
								getIELogger().writeLog("sysobject type ",IELogger.DEBUG);
								/*
								 * String message =
								 * validateContentFile(mapForImportProcessor);
								 * getIELogger().writeLog("message  " + message,
								 * IELogger.DEBUG); if (message != null ||
								 * "".equalsIgnoreCase(message)) {
								 * errormsg.append(message); }
								 */
							}
							if ("".equalsIgnoreCase(errormsg.toString())) {
								if (importServiceBean.isLive()) {
									getIELogger().writeLog("is minor "+ (String) metadataMap.get("is_minor__")+ "  version",IELogger.DEBUG);
									String[] msg = null;
									getIELogger().writeLog("before doImportOnLiveRun "+ getImportProcessorBean().getMetadataMap(),IELogger.DEBUG);
									msg = doImportOnLiveRun(getImportProcessorBean());
									if (msg[1] != null && !"".equals(msg[1])) {
										System.out.println("inside this "+ msg[1]);
										errormsg.append(msg[1]);
									} else {
										getImportProcessorBean().setObjectId(msg[0]);
									}
								}
							}
						} /*
						 * else { errormsg.append(validMsg[1]); }
						 */
					} catch (Exception e) {
						e.printStackTrace();
						System.out.println("Error at 314" + e.getMessage());
						getIELogger().writeLog("Exception in Live Run" + e.toString() + " -->"+ e.getMessage(), IELogger.DEBUG);
						errormsg.append(e.getMessage());
					}
					if ("".equalsIgnoreCase(errormsg.toString())) {
						System.out.println(" <---importProcessorBean-********************--> : "+ getImportProcessorBean());
						if (processorType != null) {
							// create and write excel file
							getIELogger().writeLog("before calling postprocessor ..",IELogger.DEBUG);

							try {
								System.out.println("****Before calling postImportProcess****");
								importProcessorBean = businessProcessor.postImportProcess(getImportProcessorBean());
								System.out.println("****After calling postImportProcess****"+importProcessorBean);
							} catch (Exception e) {
								System.out.println("**** postImportProcess Exception****");
								e.printStackTrace();
								System.out.println(e.getMessage()+ e.getCause());
								errormsg.append(e.getMessage() + e.getCause());
							}
							metadataMap.put("Post_Process_Error", metadataMap.get("PostProcessorError")); 
							//following line is added by pallavi
							System.out.println("importProcessorBean.getObjectId()-->"+importProcessorBean.getObjectId());
							metadataMap.put("r_object_id", importProcessorBean.getObjectId()); 
						}
					}
				} else {
					errormsg.append(metadataMap.get("processorError"));
					setCurrentlyProcessingFileName((String) metadataMap.get("object_name"));
				}
				getIELogger().writeLog("metadtaMapForReportFile " + metadataMap,IELogger.DEBUG);
				try {
					if ("".equalsIgnoreCase(errormsg.toString())) {
						Successful++;
				        metadataMap.put("Import_Status","Import Process Successfully Completed");
						metadataMap.put("Error_Description", "null");
						metadataMap.put("r_object_id", importProcessorBean.getObjectId()); // Added by pallavi
						if (batchProcessor != null) {
							ContentFileHandler cfh = getCsServiceprovider().getContentFileHandler();
							System.out.println("file source location----->"+ metadataFileSourceLocation);
							
							System.out.println("****Content File Handler*****"+cfh);
							
							Map map = new HashMap();
							map.put("contentFileHandler", cfh);
				            map.put("metadata_file_src_Location",metadataFileSourceLocation);
							map.put("local_file_src_Location",getImportProcessorBean().getMetadataMap().get("file_source_location__"));
							map.put("exception", null);
							sftpLocationMap.put(metadataFileSourceLocation,metadataFileSourceLocation);
							batchProcessor.preBatchCompletion(map);
						}
					} else {
						getIELogger().writeLog("map after import inside exception "+ metadataMap, IELogger.DEBUG);
						metadataMap.put("Import_Status","Import Process Failed");
						failure++;
						if (batchProcessor != null) {
							ContentFileHandler cfh = getCsServiceprovider().getContentFileHandler();
						
							Map map = new HashMap();
							map.put("contentFileHandler", cfh);
							// This cfh contains the credential of the FTP
							// connectivity
							map.put("metadata_file_src_Location",metadataFileSourceLocation);
							map.put("local_file_src_Location",getImportProcessorBean().getMetadataMap().get("file_source_location__"));
							map.put("exception", "Import Process Failed");
							sftpLocationMap.put(metadataFileSourceLocation,metadataFileSourceLocation);
							try {
								batchProcessor.preBatchCompletion(map);
							} catch (Exception e) {
								errormsg.append(" " + e.getMessage()+ e.getCause());
							}
						}
						metadataMap.put("Error_Description",errormsg.toString());
					}
					// counter for port processor error.
					metadataMap.remove("processorError");
					metadataMap.remove("PostProcessorError");
					// metadataMap = mandatoryAttributesRemoval(metadataMap);
					getMetadataWriter().writeRow(metadataMap);
				} catch (DMetadataWriteException e) {
					getIELogger().writeLog("Exception while writing rows in Report File "+ e.getMessage() + e.getCause(),IELogger.ERROR);
				}
				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
					getIELogger().writeLog(e.getMessage() + e.getCause(),IELogger.ERROR);
				}
			}
			if (resume) {
				resume = false;
			}
		}
				
		Log.logFile("Total Record: \t" + totalCounter + "\n");
		if (isImportCancelled()) {
			Log.logFile("Import process stopped on row \t" + processedCounter
					+ "\n");
			try {
				if (getImportConfigBeanForResume() == null) {
					setImportConfigBeanForResume();
				}
				getImportConfigBeanForResume().setRepoName(importServiceBean.getRepository());
				getImportConfigBeanForResume().setUserName(importServiceBean.getUserName());
				getImportConfigBeanForResume().setFilterCriteria(importServiceBean.getSql());
				getImportConfigBeanForResume().setLiveRun(importServiceBean.isLive() ? "yes" : "no");
				getImportConfigBeanForResume().setRecordNo(String.valueOf(row));
				getImportConfigBeanForResume().setObjectType(importServiceBean.getObjectType());
				System.out.println("before .. "+ importServiceBean.getMappedattributes());
				getImportConfigBeanForResume().setMap(rMap);
				// getImportConfigBeanForResume().setR_folder_path((String)
				// map.get("r_folder_path"));
				// getImportConfigBeanForResume().setSourceFileLocation((String)
				// map.get("file_source_location__"));
				getImportConfigBeanForResume().setUpdateExisting(importServiceBean.isUpdateExisting() ? "yes" : "no");
				getImportConfigBeanForResume().setObjectHirerchy(String.valueOf(importServiceBean.getObjectHirerchy()));
				getImportConfigBeanForResume().setMetadataFileLocation(importServiceBean.getMetadataFilePath());
				getImportConfigBeanForResume().setWorkSheet(importServiceBean.getSheetName());
				DaffIEResumeConfigurator.createXMLFile(getImportConfigBeanForResume());
			} catch (Exception e) {
				IELogger.writeLog(e.getMessage() + e.getCause(), IELogger.DEBUG);
			}
		}

		/*
		 * The below Method is added for Batch Processor by Harsh for the
		 * implementation of the FTP functionality on 1/5/2012. It moves content
		 * in Processed and UnProcessed folders.
		 */
		try {

		} catch (Exception e) {
			IELogger.writeLog(e.getMessage() + e.getCause(), IELogger.DEBUG);
		}

		// The below finally block closes the workbook generated as report.
		finally {
			try {
				if (workbook != null && !System.getProperty("os.name").contains("Window")) {
					workbook.write();
					workbook.close();
				}
			} catch (WriteException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		Log.logFile("No of failure: \t" + failure + "\n");
		Log.logFile("No of sucess: \t" + Successful + "\n");
		// Post processor failure :
		Log.logFile("End time: \t" + IEUtility.getTime() + "\n"); // End
		Log.logCommit();

		setImportFinished(true);

	}

	// This method has been created by Harsh for the implementation of the FTP
	// functionality on 1/5/2012
	public void executeBatchProcessor(Map sftpMap) throws DDfException {
		String processorType = PropertyFileLoader.loadUtilityConfigPropertyFile().getProperty(getImportServiceBean().getObjectType()+ "_batchProcessor");
		System.out.println("getImportServiceBean().getObjectType()_batchProcessor"+ getImportServiceBean().getObjectType());
		System.out.println("Batch processorType---> " + processorType);
		BatchProcessor batchProcessor = null;
		if (processorType != null) {
			batchProcessor = LoadBatchProcessor(processorType, batchProcessor);
		}
		if (batchProcessor != null) {
			ContentFileHandler cfh = getCsServiceprovider().getContentFileHandler();
			String metadataLocalPath = importServiceBean.getMetadataFilePath();
			System.out.println("metadataLocalPath-->>" + metadataLocalPath);
			Map map = new HashMap();
			map.put("contentFileHandler", cfh);
			map.put("sftpFileLocations", sftpMap);
			// This cfh contains the credential of the FTP connectivity
			/*
			 * map.put("metadataLocalPath", metadataLocalPath);
			 * map.put("whereClause", importServiceBean.getSql());
			 * map.put("sheet", importServiceBean.getSheetName());
			 */

			System.out.println("importServiceBean.getSql()"+ importServiceBean.getSql());
			batchProcessor.postBatchCompletion(map);
		}

	}

	// This method has been created by Harsh for the implementation of the FTP
	// functionality on 1/5/2012
	private BatchProcessor LoadBatchProcessor(String processorType,BatchProcessor batchProcessor) {
		Class c;
		try {
			c = Class.forName(processorType);
			batchProcessor = (BatchProcessor) c.newInstance();
		} catch (ClassNotFoundException e) {
			getIELogger().writeLog(
					"Load Batch Processor Error" + e.getMessage(),
					IELogger.DEBUG);
			e.printStackTrace();
		} catch (InstantiationException e) {
			getIELogger().writeLog(
					"Load Batch Processor Error" + e.getMessage(),
					IELogger.DEBUG);
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			getIELogger().writeLog(
					"Load Batch Processor Error" + e.getMessage(),
					IELogger.DEBUG);
			e.printStackTrace();
		}
		return batchProcessor;
	}

	private ImportBusinessOperationProcessor LoadProcessor(String processorType,ImportBusinessOperationProcessor businessProcessor) {
		Class c;
		try {
			c = Class.forName(processorType);
			businessProcessor = (ImportBusinessOperationProcessor) c
					.newInstance();
			System.out.println("--businessProcessor-->"+businessProcessor);
		} catch (ClassNotFoundException e) {
			getIELogger().writeLog("Load Processor Error" + e.getMessage(),
					IELogger.DEBUG);
			e.printStackTrace();
		} catch (InstantiationException e) {
			getIELogger().writeLog("Load Processor Error" + e.getMessage(),
					IELogger.DEBUG);
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			getIELogger().writeLog("Load Processor Error" + e.getMessage(),
					IELogger.DEBUG);
			e.printStackTrace();
		}
		return businessProcessor;
	}

	private void createReportFile(String substr, List reportFileHeader) {
		// calculate attributes dcattributesList
		createReportFile(substr);
		try {
			if (getImportServiceBean().getExtension().equalsIgnoreCase("xml")) {
				List inputValue = new ArrayList();
				inputValue.add("Import");
				inputValue.add(importServiceBean.getObjectType());
				getMetadataWriter().wirteAttributes(inputValue);
			} else {
				getMetadataWriter().wirteAttributes(reportFileHeader);
			}
		} catch (DMetadataWriteException e) {
			getIELogger().writeLog(
					"Exception while writing Attributes in Report File "
							+ e.getMessage() + e.getCause(), IELogger.DEBUG);
		} /*
		 * finally { if (getImportServiceBean().getExtension()
		 * .equalsIgnoreCase("xml")) { File file = new
		 * File("/xlsql_config.xml"); file.delete(); File file1 = new
		 * File("/xlsql.log"); file1.delete(); } }
		 */
	}

	private Object[] isInputValid(List reportFileHeaderList,
			int objectTypeHierarchy) {
		getIELogger().writeLog("objectTypeHierarchy : " + objectTypeHierarchy,
				IELogger.DEBUG);

		boolean boolVal = false;
		String msg = null;
		if (objectTypeHierarchy == ImportConstant.SUPER_THAN_SYSOBJECT) {
			boolVal = true;
		} else if (objectTypeHierarchy == ImportConstant.DM_FOLDER) {

			if ((reportFileHeaderList.contains("r_folder_path"))
					&& (reportFileHeaderList.contains("object_name"))) {
				boolVal = true;
			} else {
				boolVal = false;
				msg = "file_destination_location__ and object_name column must be there in input metadata for object Type dm_folder.";
			}
		} else if (objectTypeHierarchy == ImportConstant.SYSOBJECT_OR_CHILD) {
			boolean updateExisting = getImportServiceBean().isUpdateExisting();
			getIELogger().writeLog("updateExisting : " + updateExisting,
					IELogger.DEBUG);
			if ((reportFileHeaderList.contains("r_folder_path"))
					&& (reportFileHeaderList.contains("file_source_location__"))
					&& (reportFileHeaderList.contains("object_name"))) {
				if (updateExisting) {
					boolVal = true;
				} else {

					if ((reportFileHeaderList.contains("is_minor__"))) {
						boolVal = true;
					} else {
						boolVal = false;
						msg = "is_minor__ attribute is mandatory attribute. This attribute does not exists in provided metadata.";
					}
				}
			} else {

				if ((reportFileHeaderList.contains("is_minor__"))) {
					boolVal = false;
					// msg = "object_name, file_destination_location__ and
					// file_source_location__ are mandatory attributes. These
					// attributes does not exists in input metadata excel
					// file.";
					msg = "object_name, file_destination_location__ and file_source_location__  are mandatory attributes. These attributes does not exists in provided metadata.";
				} else {
					boolVal = false;
					msg = "object_name, file_destination_location__ and file_source_location__  and is_minor__ are mandatory attributes. These attributes does not exists in provided metadata.";
				}
			}
			getIELogger().writeLog(msg, IELogger.DEBUG);
			return new Object[] { boolVal, msg };
		}
		return null;

	}

	private List<String> getReportFileHeader(Map metadtaMapForReportFile) {
		List reportFileHeaderList = new ArrayList<String>();

		for (Iterator iterator = metadtaMapForReportFile.entrySet().iterator(); iterator
				.hasNext();) {
			Map.Entry entry = (Map.Entry) iterator.next();
			reportFileHeaderList.add(entry.getKey().toString());
		}
		return reportFileHeaderList;
	}

	private String createReportFile(String substr) {
		String extension = getImportServiceBean().getExtension();
		if (extension.equalsIgnoreCase("xls")) {
			createXLSFile(substr, extension);
		}
		if (extension.equalsIgnoreCase("csv")) {
			createCSVFile(substr, extension);
		}
		if (extension.equalsIgnoreCase("xml")) {
			createXMLFile(substr, extension);
		}
		return extension;
	}

	private void createXMLFile(String path, String extension) {
		String reportFileLocation = path.substring(0,
				path.lastIndexOf('\\') + 1);
		String substr = path.substring(path.lastIndexOf('\\') + 1,
				path.length());
		String reportFileName = substr + "_Import_"
				+ IEUtility.getDateAndTimeString();
		initXMLMetadataWriter(reportFileLocation, reportFileName, extension);

	}

	private void initXMLMetadataWriter(String reportFileLocation,
			String reportFileName, String extension) {
		HashMap map = new HashMap();
		map.put("File_Name", reportFileLocation);
		map.put("Table_Name", reportFileName);
		map.put("extension", extension);
		setMetadatawriter(MetadataProcessorFactory.getMetadataWriter(map));
	}

	private void createCSVFile(String path, String extension) {
		String reportFileLocation = path.substring(0,
				path.lastIndexOf('\\') + 1);
		String substr = path.substring(path.lastIndexOf('\\') + 1,
				path.length());
		String reportFileName = substr + "_Import_"
				+ IEUtility.getDateAndTimeString() + ".csv";
		initCSVMetadataWriter(reportFileLocation, reportFileName, extension);
	}

	private void initCSVMetadataWriter(String reportFileLocation,
			String reportFileName, String extension) {
		HashMap map = new HashMap();
		map.put("File_Name", reportFileLocation);
		map.put("Table_Name", reportFileName);
		map.put("extension", extension);
		setMetadatawriter(MetadataProcessorFactory.getMetadataWriter(map));
	}

	private void createXLSFile(String substr, String extension) {
		Sheet wSheet = null;
		Workbook wBook = null;
		workbook = null;
		WritableSheet worksheet = null;

		try {
			wBook = Workbook.getWorkbook(new File(importServiceBean
					.getMetadataFilePath()));
			wSheet = wBook.getSheet(importServiceBean.getSheetName());
			String reportFileName = substr + "_Import_"
					+ IEUtility.getDateAndTimeString() + ".xls";
			workbook = Workbook.createWorkbook(new File(reportFileName));
			worksheet = workbook.createSheet(wSheet.getName(), 0);

			if (System.getProperty("os.name").contains("Window")) {
				initExcelMetadataWriter(reportFileName, wSheet.getName(),
						extension);
				workbook.write();
				workbook.close();
			} else {
				initExcelMetadataWriterForLinux(reportFileName,
						wSheet.getName(), extension, worksheet, workbook);
			}
		} catch (WriteException e) {
			getIELogger().writeLog(
					"Exception while creating Report File " + e.getMessage()
							+ e.getCause(), IELogger.DEBUG);
		} catch (IOException e) {
			getIELogger().writeLog(
					"Exception while creating Report File " + e.getMessage()
							+ e.getCause(), IELogger.DEBUG);
		} catch (BiffException e) {
			getIELogger().writeLog(
					"Exception while creating Report File " + e.getMessage()
							+ e.getCause(), IELogger.DEBUG);
		}
	}

	private void setVarInitialValues() {
		logFilePath_import = null;
		importCancelled = false;
		importFinished = false;
		currentlyProcessingFileName = null;
		setRequestedForImportStopped(false);
	}

	private String validateContentFile(Map metadataMapForImport) {
		getIELogger().writeLog("ImportProcessor.validateContentFile()",
				IELogger.DEBUG);
		String errormsg = null;
		boolean validFile = checkContentFileExistance((String) metadataMapForImport.get("file_source_location__"));
		// boolean validFile = checkContentFileExistance((String) map
		// .get("s"));
		if (!validFile) {
			errormsg = "Content File does not exist.";
			return errormsg;
		}

		String docuextn = checkValidExtension((String) metadataMapForImport.get("file_source_location__"));
		if (docuextn == null) {
			errormsg = "extension not existed in Repository";

			return errormsg;

		}
		return errormsg;

	}

	private void initExcelMetadataWriter(String filePath, String sheet,
			String extension) {
		HashMap map = new HashMap();
		map.put("File_Name", filePath);
		map.put("Table_Name", sheet);
		map.put("extension", extension);
		setMetadatawriter(MetadataProcessorFactory.getMetadataWriter(map));
	}

	// This method is created for creating Excel File on Linux machine by Harsh
	// on 2/14/2012
	private void initExcelMetadataWriterForLinux(String filePath, String sheet,
			String extension, WritableSheet worksheet, WritableWorkbook workbook) {
		HashMap map = new HashMap();
		map.put("File_Name", filePath);
		map.put("Table_Name", sheet);
		map.put("extension", extension);
		map.put("worksheet", worksheet);
		map.put("workbook", workbook);
		setMetadatawriter(MetadataProcessorFactory
				.getMetadataWriterForLinux(map));
	}

	private String checkValidExtension(String srcpath) {
		String documentumExtension = null;
		if (srcpath != null && !"".equals(srcpath.trim())) {
			String dosExtension = srcpath
					.substring((srcpath.lastIndexOf(".") + 1));
			try {
				documentumExtension = getCsServiceprovider()
						.getDocumentumExtension(dosExtension);
			} catch (DDfException e) {
				return (e.getMessage() + " " + e.getCause());
			}

		}
		return documentumExtension;
	}

	private boolean checkContentFileExistance(String file_path) {
		getIELogger().writeLog(
				"ImportProcessor.checkContentFileExistance() " + file_path,
				IELogger.DEBUG);
		boolean isExist;
		if (file_path == null
				|| "".equals(file_path != null ? file_path.trim() : "")) {
			return true;
		}
		File contentfile = new File(file_path);
		isExist = contentfile.exists();
		getIELogger().writeLog("exist_not " + isExist, IELogger.DEBUG);
		return isExist;

	}

	private String[] doImportOnLiveRun(ImportProcessorBean importProcessorBean) {
		getIELogger().writeLog("passing doImportOnLiveRun", IELogger.DEBUG);
		getIELogger().writeLog(
				"ObjectTypeHierarchy() "
						+ importProcessorBean.getObjectTypeHierarchy(),
				IELogger.DEBUG);
		String errormsg = null;
		String folderPath = null;
		String objectId = null;
		// String srcPath = (String) map.get("file_source_location__");
		// String dosExt = srcPath.substring((srcPath.lastIndexOf(".") + 1));
		if (importProcessorBean.getObjectTypeHierarchy() == ImportConstant.SUPER_THAN_SYSOBJECT) {
			try {
				objectId = csserviceprovider
						.importSuperThanSys(importProcessorBean);
			} catch (DDfException e) {
				errormsg = e.getMessage();
			}
		} else {
			folderPath = (String) importProcessorBean.getMetadataMap().get("r_folder_path");
			System.out.println("r_folder_path in doImportLiveRun is"+ folderPath);
			String objectName = (String) importProcessorBean.getMetadataMap().get("object_name");
			String destFolderPath = null;
			try {
				destFolderPath = createFolderHierarcy(folderPath);
				if (destFolderPath != null) {
					if (importProcessorBean.getObjectTypeHierarchy() == ImportConstant.SYSOBJECT_OR_CHILD) {
						while (isRequestedForImportStopped()) {
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						if (!isImportCancelled()) {
							objectName = null;
							getIELogger().writeLog(
									"before calling import in csservice ",
									IELogger.DEBUG);
							objectId = csserviceprovider
									.importSysOrChild(importProcessorBean);
							getIELogger().writeLog("obj id : " + objectId,
									IELogger.DEBUG);
						}

						if (isImportCancelled()) {
							if (getImportConfigBeanForResume() == null) {
								setImportConfigBeanForResume();
							}
							getImportConfigBeanForResume().setMap(
									importServiceBean.getMappedattributes());
							getImportConfigBeanForResume()
									.setMetadataFileLocation(
											importServiceBean
													.getMetadataFilePath());
							getImportConfigBeanForResume().setObjectName(
									(String) importProcessorBean
											.getMetadataMap()
											.get("object_name"));
							getImportConfigBeanForResume().setR_folder_path(
									folderPath);
							getIELogger().writeLog(
									"when import cancelled before setting the objectid :"
											+ objectId, IELogger.DEBUG);
							importProcessorBean.setObjectId(objectId);
							// getImportConfigBeanForResume().setSourceFileLocation(srcPath);
						}
					} else {
						try {
							objectId = csserviceprovider
									.importFolder(importProcessorBean);
						} catch (DDfException e) {
							errormsg = e.getMessage();
							IELogger.writeLog(e.getMessage(), IELogger.INFO);
						}
					}
				} else {
					errormsg = "Error on creation of the Destination Folder Path in Repository";
				}
			} catch (DDfException e) {
				errormsg = e.getMessage();
				IELogger.writeLog(e.getMessage(), IELogger.INFO);
			}
		}
		String returnData[] = new String[2];
		getIELogger().writeLog("obj id before : " + objectId, IELogger.DEBUG);
		returnData[0] = objectId;
		returnData[1] = errormsg;
		return returnData;
	}

	private String createFolderHierarcy(String folderpath) throws DDfException {
		String cabinetName = ""; // addded cabinet name
		String pathArray[] = folderpath.split("/");

		if (pathArray.length < 2) {
			throw new DDfException("Invalid value given for r_folder_path.");
		}
		cabinetName = pathArray[1];
		boolean a = csserviceprovider.checkCabinet(cabinetName);
		if (!a) {
			csserviceprovider.createNewCabinet(cabinetName);
		}

		int i = 2; // first is the cabinet name
		int last = pathArray.length - 2;// last value is the file name
		String[] folderArray = new String[last];

		for (int k = 0; k < last; k++) {
			folderArray[k] = pathArray[i];
			i++;
		}
		String generatedPath = "/" + cabinetName;
		String folderPath = "/" + cabinetName;
		for (int k = 0; k < folderArray.length; k++) {
			generatedPath = folderPath;
			folderPath = generatedPath + "/" + folderArray[k];
			boolean b = csserviceprovider.checkFolder(folderPath);
			if (!b) {
				csserviceprovider
						.createNewFolder(generatedPath, folderArray[k]);
			}
		}
		return folderPath;
	}

	/*
	 * public Object[] validInputExcelFileHeader(List inputAttrList, String
	 * type) { getIELogger().writeLog("passing
	 * ImportProcessor.validInputExcelFileHeader()", IELogger.DEBUG); int
	 * objectType; try { objectType = csserviceprovider.validInputHeader(type);
	 * boolean boolVal = false; String msg = null; if (objectType ==
	 * ImportConstant.SUPER_THAN_SYSOBJECT) { boolVal = true; } else if
	 * (objectType == ImportConstant.DM_FOLDER) {
	 * 
	 * if (inputAttrList.contains("r_folder_path") &&
	 * inputAttrList.contains("object_name")) { boolVal = true; } else { boolVal
	 * = false; msg = "file_destination_location__ and object_name column must
	 * be there in input metadata excel file for object Type dm_folder"; } }
	 * else if (objectType == ImportConstant.SYSOBJECT_OR_CHILD) { boolean
	 * updateExisting = false; if (inputAttrList.contains("r_folder_path") &&
	 * inputAttrList.contains("file_source_location__") &&
	 * inputAttrList.contains("object_name")) { System.out.println("inside
	 * 1.."); if (updateExisting) { System.out.println("inside 2..."); if
	 * (inputAttrList.contains("is_minor__")) { boolVal = true; } else { boolVal
	 * = false; msg = "is_minor__ attribute is mandatory attribute. This
	 * attribute is not exists in input metadata excel file."; } } else {
	 * boolVal = true; } } else { boolVal = false; msg = "object_name,
	 * file_destination_location__ and file_source_location__ attribute are
	 * mandatory attributes. These attributes does not exists in input metadata
	 * excel file."; } } return new Object[] { boolVal, objectType, msg }; }
	 * catch (DDfException e) { String msg = e.getMessage(); } return null; }
	 */

	public void setRequestedForImportStopped(boolean b) {
		this.requestedForImportStopped = b;

	}

	public boolean isRequestedForImportStopped() {
		return requestedForImportStopped;
	}

	public void setImportCancelled(boolean importCanceled) {
		this.importCancelled = importCanceled;

	}

	public String getLogFilePath_import() {
		return logFilePath_import;
	}

	public void setLogFilePath_import(String logFilePath_import) {
		this.logFilePath_import = logFilePath_import;
	}

	public void registerPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	public void setProcessMessage(String pProcessMessage) {
		String oldValue = this.processMessage;
		this.processMessage = pProcessMessage;

		propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this,
				"processMessage", oldValue, pProcessMessage));

	}

	public int getImportFinishStatus() {
		if (isImportCancelled() && isLiveRun()) {
			return ImportConstant.IMPORT_STOPPED_LIVE_RUN;
		} else if (isImportCancelled() && !isLiveRun()) {
			return ImportConstant.IMPORT_STOPPED_TEST_RUN;
		} else if (!isImportCancelled() && isLiveRun()) {
			return ImportConstant.IMPORT_FINISHED_LIVE_RUN;
		} else if (!isImportCancelled() && !isLiveRun()) {
			return ImportConstant.IMPORT_FINISHED_TEST_RUN;
		}
		return 0;
	}

	public boolean isImportCancelled() {
		return importCancelled;
	}

	public boolean isLiveRun() {
		this.liveRun = importServiceBean.isLive();
		return liveRun;
	}

	public ImportServiceBean getImportServiceBean() {
		return importServiceBean;
	}

	public void setImportServiceBean(ImportServiceBean importServiceBean) {
		this.importServiceBean = importServiceBean;
	}

	public MetadataReader getMetadatReader() {
		return metadatreader;
	}

	public void setMetadatreader(MetadataReader metadatreader) {
		this.metadatreader = metadatreader;
	}

	public MetadataWriter getMetadataWriter() {
		return metadatawriter;
	}

	public void setMetadatawriter(MetadataWriter metadatawriter) {
		this.metadatawriter = metadatawriter;
	}

	public CSServices getCsServiceprovider() {
		return csserviceprovider;
	}

	public void setCsServiceprovider(CSServices csserviceprovider) {
		this.csserviceprovider = csserviceprovider;
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

	public boolean isImportFinished() {
		return importFinished;
	}

	public void setImportFinished(boolean importFinished) {
		boolean oldValue = this.importFinished;
		this.importFinished = importFinished;
		propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this,
				"importFinished", oldValue, importFinished));
	}

	public String getCurrentlyProcessingFileName() {
		return currentlyProcessingFileName;
	}

	public void setCurrentlyProcessingFileName(
			String currentlyProcessingFileName) {
		this.currentlyProcessingFileName = currentlyProcessingFileName;
	}

	public ImportMainUIControl getMainUIControl() {
		return mainUIControl;
	}

	public void setMainUIControl(IEMainAbstractUIControl mainUIControl) {
		this.mainUIControl = (ImportMainUIControl) mainUIControl;
	}

	public IELogger getIELogger() {
		return IELogger;
	}

	public void setIELogger(IELogger ieLogger) {
		this.IELogger = ieLogger;
	}

	public ImportConfigBeanForResume getImportConfigBeanForResume() {
		return importConfigBeanForResume;
	}

	public void setImportConfigBeanForResume() {
		importConfigBeanForResume = new ImportConfigBeanForResume();
	}

	// public ImportBusinessOperationProcessor
	// getImportBusinessOperationProcessor() {
	// return ImportBusinessOperationProcessor;
	// }
	//
	// public void setImportBusinessOperationProcessor(
	// ImportBusinessOperationProcessor importBusinessOperationProcessor) {
	// ImportBusinessOperationProcessor = importBusinessOperationProcessor;
	// }

	public ImportProcessorBean getImportProcessorBean() {
		return importProcessorBean;
	}

	public void setImportProcessorBean(ImportProcessorBean importProcessorBean) {
		this.importProcessorBean = importProcessorBean;
	}

	public Map mandatoryAttributesRemoval(Map mapAttribute) throws DDfException {

		mapAttribute.remove("r_folder_path");

		return mapAttribute;
	}

}
