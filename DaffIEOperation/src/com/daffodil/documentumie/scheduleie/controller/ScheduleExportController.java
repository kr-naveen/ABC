package com.daffodil.documentumie.scheduleie.controller;

import java.util.ArrayList;
import java.util.List;

import com.daffodil.documentumie.dctm.apiimpl.CSServicesProvider;
import com.daffodil.documentumie.dctm.exception.DDfException;
import com.daffodil.documentumie.filehandler.FTPFileHandler;
import com.daffodil.documentumie.filehandler.LocalSystemFileHandler;
import com.daffodil.documentumie.filehandler.SFTPFileHandler;
import com.daffodil.documentumie.fileutil.configurator.DaffIEConfigurator;
import com.daffodil.documentumie.fileutil.configurator.bean.ExportConfigBean;
import com.daffodil.documentumie.fileutil.configurator.exception.DConfigReadException;
import com.daffodil.documentumie.fileutil.logger.IELogger;
import com.daffodil.documentumie.fileutil.metadata.api.MetadataReader;
import com.daffodil.documentumie.fileutil.metadata.api.MetadataWriter;
import com.daffodil.documentumie.iebusiness.export.bean.ExportServiceBean;
import com.daffodil.documentumie.iebusiness.export.model.ExportProcessor;
import com.daffodil.documentumie.iebusiness.pimport.model.ImportProcessor;
import com.daffodil.documentumie.scheduleie.bean.EScheduleConfigBean;
import com.daffodil.documentumie.scheduleie.bean.ScheduleExportConfigBean;

public class ScheduleExportController {

	private ExportProcessor exportProcessor = null;
	private IELogger ieLogger = null;
	private CSServicesProvider csServicesProvider = null;
	private MetadataReader metadataReader = null;

	public ScheduleExportController(
			ScheduleExportConfigBean schedulExportConfigBean,
			EScheduleConfigBean scheduleConfgiBean) {
		csServicesProvider = new CSServicesProvider();
		try {
			csServicesProvider.login(schedulExportConfigBean.getRepository(),
					schedulExportConfigBean.getUserName(),
					schedulExportConfigBean.getPassword(),
					schedulExportConfigBean.getDomain());
		} catch (DDfException e) {
			getIeLogger().writeLog(
					"while creating session from docbase in export proces "
							+ e.getMessage() + e.getCause(), IELogger.DEBUG);
			e.printStackTrace();
			System.exit(0);
		}
		exportProcessor = new ExportProcessor();

		ExportConfigBean configBean = null;
		try {
			configBean = (ExportConfigBean) DaffIEConfigurator
					.read(DaffIEConfigurator.EXPORT);
			ieLogger = new IELogger(configBean);
		} catch (DConfigReadException e) {
			e.printStackTrace();
			// throw new
			// SceduleFileReaderException(e.getMessage(),e.getCause());
		}
		setIeLogger(ieLogger);

		setExportProcessor(exportProcessor);
		initiallize(schedulExportConfigBean);
		exportProcessor.setEScheduleConfigBean(scheduleConfgiBean);
		doExport();
	}

	private void doExport() {
		try {
			getExportProcessor().doExport();
		} catch (DDfException e) {
			getIeLogger().writeLog("while calling doExport " + e.getMessage() + e.getCause(),IELogger.DEBUG);
		}

	}

	private void initiallize(ScheduleExportConfigBean schedulExportConfigBean) {
		ExportServiceBean exportServiceBean = new ExportServiceBean();

		exportServiceBean
				.setRepository(schedulExportConfigBean.getRepository());
		exportServiceBean.setUsername(schedulExportConfigBean.getUserName());
		exportServiceBean.setPassword(schedulExportConfigBean.getPassword());
		exportServiceBean
				.setObjectType(schedulExportConfigBean.getObjectType());
		exportServiceBean
				.setExportType(schedulExportConfigBean.getExportType());
		exportServiceBean
				.setAllVersion(schedulExportConfigBean.getAllVersion() == null
						|| "".equalsIgnoreCase(schedulExportConfigBean
								.getAllVersion())
						|| schedulExportConfigBean.getAllVersion()
								.equalsIgnoreCase("no") ? false : true);
		exportServiceBean
				.setExportIntoZIP(schedulExportConfigBean.getInZip() == null
						|| "".equalsIgnoreCase(schedulExportConfigBean
								.getInZip())
						|| schedulExportConfigBean.getInZip().equalsIgnoreCase(
								"no") ? false : true);
		exportServiceBean.setOutPutFile(schedulExportConfigBean
				.getExportLocation());
		exportServiceBean.setMetaDataFilePath(schedulExportConfigBean
				.getReportFileLocation());

		List attrList = new ArrayList();
		String[] attrArray = schedulExportConfigBean.getAttributeList().split(
				",");
		System.out.println("attr aray ---  " + attrArray);
		for (int i = 0; i < attrArray.length; i++) {
			//System.out.println("value  " + attrArray[i]);
			attrList.add(attrArray[i]);
		}
		/*System.out.println("attr size....... " + attrList.size());
		System.out.println("attr value ......" + attrList.get(0));*/
		exportServiceBean.setSelectedAttribute(attrList);
		exportServiceBean.setDqlText(schedulExportConfigBean.getDql());
		exportServiceBean
				.setExportType(schedulExportConfigBean.getExportType());
		exportServiceBean.setReportType("xls");

		exportServiceBean.setOnlyMetadata(schedulExportConfigBean
				.getOnlyMetadata() == null
				|| "".equalsIgnoreCase(schedulExportConfigBean
						.getOnlyMetadata())
				|| schedulExportConfigBean.getOnlyMetadata().equalsIgnoreCase(
						"no") ? false : true);
		// exportServiceBean.setDailyBases(schedulExportConfigBean.isDailyBases());
		exportServiceBean.setSchedule(true);
		/*
		 * The below functionality is added by Harsh for the implementation of the FTP on 1/21/2012
		 */
		
		String typeFileSystem = schedulExportConfigBean.getExportTo();
		if(typeFileSystem!=null && typeFileSystem.equalsIgnoreCase("ftp"))
		{			
			String ftpUrl = schedulExportConfigBean.getFtpURL();
			if(ftpUrl!=null && ftpUrl.startsWith("ftp"))
			{
				FTPFileHandler ffh=null;
				ftpUrl =  ftpUrl.substring(ftpUrl.indexOf(":")+3,ftpUrl.length());
				ffh = new FTPFileHandler(ftpUrl, schedulExportConfigBean.getFtpUserName(), schedulExportConfigBean.getFtpPassword());
				try {
					ffh.connect();
				} catch (DDfException e) {
					e.printStackTrace();
				}
				exportServiceBean.setMetaDataFilePath(schedulExportConfigBean.getFtpMetadataPath());
				exportServiceBean.setFtpContentPath(schedulExportConfigBean.getFtpContentPath());
				exportServiceBean.setOutPutFile(schedulExportConfigBean.getFtpContentPath());
				csServicesProvider.setContentFileHandler(ffh);
			}
			else if(ftpUrl!=null && ftpUrl.startsWith("sftp"))
			{
				SFTPFileHandler ffh=null;
				ftpUrl =  ftpUrl.substring(ftpUrl.indexOf(":")+3,ftpUrl.length());
				ffh = new SFTPFileHandler(ftpUrl, schedulExportConfigBean.getFtpUserName(), schedulExportConfigBean.getFtpPassword());
				exportServiceBean.setMetaDataFilePath(schedulExportConfigBean.getFtpMetadataPath());
				exportServiceBean.setFtpContentPath(schedulExportConfigBean.getFtpContentPath());
				exportServiceBean.setOutPutFile(schedulExportConfigBean.getFtpContentPath());
				csServicesProvider.setContentFileHandler(ffh);
			}						
		}
		else if(typeFileSystem!=null && typeFileSystem.equalsIgnoreCase("file"))
		{
			LocalSystemFileHandler lfh = new LocalSystemFileHandler();
			/*exportServiceBean.setMetaDataFilePath(schedulExportConfigBean.getFtpMetadataPath());
			exportServiceBean.setFtpContentPath(schedulExportConfigBean.getFtpContentPath());
			exportServiceBean.setOutPutFile(schedulExportConfigBean.getFtpContentPath());*/
			csServicesProvider.setContentFileHandler(lfh);
		}
		exportProcessor.setCsServiceProvider(csServicesProvider);
		exportProcessor.setExportServiceBean(exportServiceBean);
		exportProcessor.setIELogger(ieLogger);

	}

	public ExportProcessor getExportProcessor() {
		return exportProcessor;
	}

	public void setExportProcessor(ExportProcessor exportProcessor) {
		this.exportProcessor = exportProcessor;
	}

	public IELogger getIeLogger() {
		return ieLogger;
	}

	public void setIeLogger(IELogger ieLogger) {
		this.ieLogger = ieLogger;
	}

	public MetadataReader getMetadataReader() {
		return metadataReader;
	}

	public void setMetadataReader(MetadataReader metadataReader) {
		this.metadataReader = metadataReader;
	}

}
