package com.daffodil.documentumie.scheduleie.controller;

import java.util.HashMap;

import com.daffodil.documentumie.dctm.apiimpl.CSServicesProvider;
import com.daffodil.documentumie.dctm.exception.DDfException;
import com.daffodil.documentumie.filehandler.FTPFileHandler;
import com.daffodil.documentumie.filehandler.LocalSystemFileHandler;
import com.daffodil.documentumie.filehandler.SFTPFileHandler;
import com.daffodil.documentumie.fileutil.configurator.DaffIEConfigurator;
import com.daffodil.documentumie.fileutil.configurator.bean.ImportConfigBean;
import com.daffodil.documentumie.fileutil.configurator.exception.DConfigReadException;
import com.daffodil.documentumie.fileutil.logger.IELogger;
import com.daffodil.documentumie.fileutil.metadata.MetadataProcessorFactory;
import com.daffodil.documentumie.fileutil.metadata.api.MetadataReader;
import com.daffodil.documentumie.iebusiness.pimport.bean.ImportServiceBean;
import com.daffodil.documentumie.iebusiness.pimport.model.ImportProcessor;
import com.daffodil.documentumie.scheduleie.bean.IScheduleConfigBean;
import com.daffodil.documentumie.scheduleie.bean.ScheduleImportConfigBean;

public class ScheduleImportController {
	private ImportProcessor importProcessor = null;
	private IELogger ieLogger = null;
	private CSServicesProvider csServicesProvider = null;
	private MetadataReader metadataReader = null;

	public ScheduleImportController(ScheduleImportConfigBean schedulImportConfigBean,IScheduleConfigBean scheduleConfgiBean) {
		csServicesProvider = new CSServicesProvider();

		try {
			csServicesProvider.login(schedulImportConfigBean.getRepository(),
					schedulImportConfigBean.getUserName(),
					schedulImportConfigBean.getPassword(),
					schedulImportConfigBean.getDomain());
		} catch (DDfException e) {
			getIeLogger().writeLog(
					"while creating session from docbase " + e.getMessage()
							+ e.getCause(), IELogger.DEBUG);
			e.printStackTrace();
			System.exit(0);
		}

		importProcessor = new ImportProcessor();

		ImportConfigBean configBean = null;
		try {
			configBean = (ImportConfigBean) DaffIEConfigurator.read(DaffIEConfigurator.IMPORT);
			ieLogger = new IELogger(configBean);
		} catch (DConfigReadException e) {
			e.printStackTrace();
			// throw new
			// SceduleFileReaderException(e.getMessage(),e.getCause());
		}
		setIeLogger(ieLogger);

		setImportProcessor(importProcessor);
		initiallize(schedulImportConfigBean);
	//	importProcessor.setIScheduleCongigBean(scheduleConfgiBean);
		doImport();
	}

	private void doImport() {
		try {
			getImportProcessor().doImport();
		} catch (DDfException e) {
			getIeLogger().writeLog(
					"while calling doImport " + e.getMessage() + e.getCause(),
					IELogger.DEBUG);
		}

	}

	private void initiallize(ScheduleImportConfigBean schedulImportConfigBean) 
	{
		ImportServiceBean importServiceBean = new ImportServiceBean();

		importServiceBean.setRepository(schedulImportConfigBean.getRepository());
		importServiceBean.setUserName(schedulImportConfigBean.getUserName());
		importServiceBean.setObjectType(schedulImportConfigBean.getObjectType());
		importServiceBean.setMetadataFilePath(schedulImportConfigBean.getMetadataInputFile());
		importServiceBean.setSheetName(schedulImportConfigBean.getWorksheet());
		importServiceBean.setExtension("xls");
		
		importServiceBean.setUpdateExisting(schedulImportConfigBean.getUpDateExisting() == null
						|| "".equalsIgnoreCase(schedulImportConfigBean.getUpDateExisting())
						|| schedulImportConfigBean.getUpDateExisting().equalsIgnoreCase("no") ? false : true);
		
		importServiceBean.setisLive(schedulImportConfigBean.getIsLive() == null
						|| "".equalsIgnoreCase(schedulImportConfigBean.getIsLive())
						|| schedulImportConfigBean.getIsLive().equalsIgnoreCase("no") ? false : true);
		
		importServiceBean.setSql(schedulImportConfigBean.getSql());
		// importServiceBean.setDailyBases(schedulImportConfigBean.isDailyBases());
		importServiceBean.setMappedattributes(schedulImportConfigBean.getMappedAttributs());
		importServiceBean.setSchedule(true);

		initExcelmetadataReader(schedulImportConfigBean.getMetadataInputFile(),schedulImportConfigBean.getWorksheet(), "xls");
		/*
		 * The below functionality is added by Harsh for the implementation of the FTP on 1/16/2012
		 */
		
		String typeFileSystem = schedulImportConfigBean.getImportFrom();
		if(typeFileSystem!=null && typeFileSystem.equalsIgnoreCase("ftp"))
		{			
			String ftpUrl = schedulImportConfigBean.getFtpURL();
			if(ftpUrl!=null && ftpUrl.startsWith("ftp"))
			{
				FTPFileHandler ffh=null;
				ftpUrl =  ftpUrl.substring(ftpUrl.indexOf(":")+3,ftpUrl.length());
				ffh = new FTPFileHandler(ftpUrl, schedulImportConfigBean.getFtpUserName(), schedulImportConfigBean.getFtpPassword());
				try {
					Boolean connect = ffh.connect();
					if(connect)
					{
						ffh.getFile(schedulImportConfigBean.getFtpMetadataPath());
					}
					//
				} catch (DDfException e) {
					e.printStackTrace();
				}
				csServicesProvider.setContentFileHandler(ffh);
			}
			else if(ftpUrl!=null && ftpUrl.startsWith("sftp"))
			{
				SFTPFileHandler ffh=null;
				ftpUrl =  ftpUrl.substring(ftpUrl.indexOf(":")+3,ftpUrl.length());
				ffh = new SFTPFileHandler(ftpUrl, schedulImportConfigBean.getFtpUserName(), schedulImportConfigBean.getFtpPassword());
				try {
					ffh.getFile(schedulImportConfigBean.getFtpMetadataPath());
				} catch (DDfException e) {
					e.printStackTrace();
				}
				csServicesProvider.setContentFileHandler(ffh);
			}						
		}
		else if(typeFileSystem!=null && typeFileSystem.equalsIgnoreCase("file"))
		{
			LocalSystemFileHandler lfh = new LocalSystemFileHandler();
			csServicesProvider.setContentFileHandler(lfh);
		}
		
		// FTP settings are finished here.
		importProcessor.setCsServiceprovider(csServicesProvider);
		importProcessor.setImportServiceBean(importServiceBean);
		importProcessor.setIELogger(ieLogger);
		// importProcessor.setMetadatawriter(getMetadataWriter());
		importProcessor.setMetadatreader(getMetadataReader());

	}

	public ImportProcessor getImportProcessor() {
		return importProcessor;
	}

	public void setImportProcessor(ImportProcessor importProcessor) {
		this.importProcessor = importProcessor;
	}

	public IELogger getIeLogger() {
		return ieLogger;
	}

	public void setIeLogger(IELogger ieLogger) {
		this.ieLogger = ieLogger;
	}

	public void initExcelmetadataReader(String metadataFileLocation,
			String worksheet, String extension) {
		HashMap map = new HashMap();
		map.put("File_Name", metadataFileLocation);
		map.put("Table_Name", worksheet);
		map.put("extension", extension);
		setMetadataReader(MetadataProcessorFactory.getMetadataReader(map));
	}

	public MetadataReader getMetadataReader() {
		return metadataReader;
	}

	public void setMetadataReader(MetadataReader metadataReader) {
		this.metadataReader = metadataReader;
	}
}
