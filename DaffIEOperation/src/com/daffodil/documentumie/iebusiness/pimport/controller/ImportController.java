package com.daffodil.documentumie.iebusiness.pimport.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.daffodil.documentumie.dctm.exception.DDfException;
import com.daffodil.documentumie.filehandler.FTPFileHandler;
import com.daffodil.documentumie.filehandler.LocalSystemFileHandler;
import com.daffodil.documentumie.filehandler.SFTPFileHandler;
import com.daffodil.documentumie.fileutil.logger.IELogger;
import com.daffodil.documentumie.iebusiness.IEMainAbstractUIControl;
import com.daffodil.documentumie.iebusiness.pimport.bean.ImportServiceBean;
import com.daffodil.documentumie.iebusiness.pimport.model.ImportProcessor;
import com.daffodil.documentumie.iebusiness.pimport.view.ImportMainUIControl;
import com.daffodil.documentumie.iebusiness.SwingWorker;

public class ImportController {

	private ImportMainUIControl mainUIControl;

	private ImportProcessor importProcessor;

	MyProgressBarRunnable myProgressBarRunnable = null;

	public ImportController(IEMainAbstractUIControl mainAbstractUIControl,
			ImportProcessor importProcessor) {

		setImportMainUIControl(mainAbstractUIControl);
		setImportProcessor(importProcessor);
		inilize();
		installListener();
		myProgressBarRunnable = new MyProgressBarRunnable();
	}

	private void inilize(){
		//		ImportBusinessOperationProcessor importBusinessOperationProcessor=new ImportBusinessOperationProcessorImpl();
		
		ImportServiceBean importServiceBean = new ImportServiceBean();
		
		importServiceBean.setUserName(((ImportMainUIControl) getImportMainUIControl()).getimportUIInfoBean().getUserName());
		importServiceBean.setRepository(((ImportMainUIControl) getImportMainUIControl()).getimportUIInfoBean().getRepository());
		importServiceBean.setDomain((((ImportMainUIControl) getImportMainUIControl()).getimportUIInfoBean().getDomain()));
		importServiceBean.setMappedattributes(((ImportMainUIControl) getImportMainUIControl()).getimportUIInfoBean().getMappedAttributs());
		importServiceBean.setisLive(((ImportMainUIControl) getImportMainUIControl()).getimportUIInfoBean().getIsLive());
		importServiceBean.setMetadataFilePath(((ImportMainUIControl) getImportMainUIControl()).getimportUIInfoBean().getMetadataInputFile());
		importServiceBean.setSheetName(((ImportMainUIControl) getImportMainUIControl()).getimportUIInfoBean().getWorksheet());
		importServiceBean.setObjectType(((ImportMainUIControl) getImportMainUIControl()).getimportUIInfoBean().getObjectType());
		importServiceBean.setisLive(((ImportMainUIControl) getImportMainUIControl()).getimportUIInfoBean().getIsLive());
		importServiceBean.setSql(((ImportMainUIControl) getImportMainUIControl()).getimportUIInfoBean().getSql());
		importServiceBean.setObjectHirerchy(((ImportMainUIControl) getImportMainUIControl()).getimportUIInfoBean().getObjectHirerchy());
		importServiceBean.setUpdateExisting(((ImportMainUIControl) getImportMainUIControl()).getimportUIInfoBean().isUpdateExisting());
		importServiceBean.setExtension((((ImportMainUIControl) getImportMainUIControl()).getimportUIInfoBean().getExtension()));
		
		if ((((ImportMainUIControl) getImportMainUIControl()).getimportUIInfoBean().isResume())) {
			importServiceBean.setResume(true);
			importServiceBean.setObjectName((((ImportMainUIControl) getImportMainUIControl()).getimportUIInfoBean().getObjectName()));
			importServiceBean.setObjectHirerchy((((ImportMainUIControl) getImportMainUIControl()).getimportUIInfoBean().getObjectHirerchy()));
			importServiceBean.setRecordNo((((ImportMainUIControl) getImportMainUIControl()).getimportUIInfoBean().getRecordNo()));
			importServiceBean.setR_folder_path((((ImportMainUIControl) getImportMainUIControl()).getimportUIInfoBean().getR_folder_path()));
			importServiceBean.setSourceFileLocation((((ImportMainUIControl) getImportMainUIControl()).getimportUIInfoBean().getSourceFileLocation()));
		}
		// Below code is added by Harsh for the implementation of the FTP functionality on 12/27/2011 
			System.out.println("Import From in import Controller--"+((ImportMainUIControl) getImportMainUIControl()).getimportUIInfoBean().getImportFromLocation());
		String importFrom = ((ImportMainUIControl) getImportMainUIControl()).getimportUIInfoBean().getImportFromLocation();
		if(importFrom!=null)
		{
			if(importFrom.equalsIgnoreCase("ftp"))
			{
				//file
				
				String ftpUrl = ((ImportMainUIControl) getImportMainUIControl()).getimportUIInfoBean().getFtpURL();
				String userName = ((ImportMainUIControl) getImportMainUIControl()).getimportUIInfoBean().getFtpUserName();
				String password = ((ImportMainUIControl) getImportMainUIControl()).getimportUIInfoBean().getFtpPassword();
				System.out.println("------------------------------Import Controller Called-------------ftpUrl=:"+ftpUrl+":-->userName:"+ userName+":-->password:"+ password);
				if(ftpUrl!=null && ftpUrl.startsWith("sftp")){
					ftpUrl =  ftpUrl.substring(ftpUrl.indexOf(":")+3,ftpUrl.length());
				SFTPFileHandler ffh = new SFTPFileHandler(ftpUrl, userName, password);
				getImportMainUIControl().getCSServiceProvider().setContentFileHandler(ffh);
				}
				else if(ftpUrl!=null && ftpUrl.startsWith("ftp")){
					ftpUrl =  ftpUrl.substring(ftpUrl.indexOf(":")+3,ftpUrl.length());
					FTPFileHandler ffh = new FTPFileHandler(ftpUrl, userName, password);
					
					try {
						ffh.connect();
					} catch (DDfException e) {
						e.printStackTrace();
					}
					getImportMainUIControl().getCSServiceProvider().setContentFileHandler(ffh);
					}
				/*try {
					ffh.connect();
				} catch (DDfException e) {
					e.printStackTrace();
				}*/
				
			}
			else if(importFrom.equalsIgnoreCase("file")){
				LocalSystemFileHandler lfh = new LocalSystemFileHandler();
				getImportMainUIControl().getCSServiceProvider().setContentFileHandler(lfh);
			}
		}
			// Above code is added by Harsh for the implementation of the FTP functionality on 12/27/2011
		importProcessor.setCsServiceprovider(getImportMainUIControl().getCSServiceProvider());
		importProcessor.setImportServiceBean(importServiceBean);
		importProcessor.setMetadatawriter(getImportMainUIControl().getMetadataWriter());
		importProcessor.setMetadatreader(getImportMainUIControl().getMetadataReader());
		importProcessor.setMainUIControl((ImportMainUIControl) getImportMainUIControl());
		importProcessor.setIELogger(getImportMainUIControl().getIELogger());
			
		
//		importProcessor.setImportBusinessOperationProcessor(importBusinessOperationProcessor);
	}

	public ImportMainUIControl getImportMainUIControl() {
		return mainUIControl;
	}

	public void setImportMainUIControl(
			IEMainAbstractUIControl mainAbstractUIControl) {
		this.mainUIControl = (ImportMainUIControl) mainAbstractUIControl;
	}

	public ImportProcessor getImportProcessor() {
		return importProcessor;
	}

	public void setImportProcessor(ImportProcessor val) {
		this.importProcessor = val;
	}

	public void startImport (){
		PopulationWorker populationWorker = new PopulationWorker();
		populationWorker.start();
	}

	public void doImport() {
		try {
				getImportProcessor().doImport();
			}  catch (DDfException e) {
				getImportMainUIControl().getIELogger().writeLog("while calling doImport " + e.getMessage() + e.getCause(), IELogger.DEBUG);
				getImportMainUIControl().getIEMessageUtility().showMessageDialog(e.getMessage(), e.getCause());
		}
	}

	private void installListener() {
		getImportMainUIControl().registerActionListener(
				new UIControlActionListener());
		getImportProcessor().registerPropertyChangeListener(
				new ProcessorPropertyChangeListener());
	}

	private class UIControlActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equalsIgnoreCase("Stop")) {
				performStopAction();
			} else if (e.getActionCommand().equalsIgnoreCase("View Log")) {
				performViewLogAction();
			} else if (e.getActionCommand().equalsIgnoreCase("Finish")) {
				performFinishAction();
			}
		}
	}

	private void performFinishAction() {
		// TODO prompt user to : Would you like to do more Import or Export ?
		getImportMainUIControl().writeConfigBean();
		int i = getImportMainUIControl().promptUserFinishProcess();
		if (i == JOptionPane.YES_OPTION) {
			getImportMainUIControl().showIEIntroUIControl();	
			if(((ImportMainUIControl) getImportMainUIControl()).getimportUIInfoBean().isResume()){
				File resumeFile = new File(((ImportMainUIControl) getImportMainUIControl()).getimportUIInfoBean().getResumeConfigFileLocation());
				System.out.println("resumeFile : "+resumeFile);
				if(resumeFile.exists()){
					resumeFile.delete();
				}
				}
		}else{
			System.exit(0);
		}
		
	}

	private void performStopAction() {
		importProcessor.setRequestedForImportStopped(true);
		int i = getImportMainUIControl().promptUserStopImport();
		if (i == JOptionPane.YES_OPTION) {
			getImportMainUIControl().setStopButtonEnabled(false);
			getImportMainUIControl().renderOperationFinishControl();
			importProcessor.setImportCancelled(true);
			if(((ImportMainUIControl) getImportMainUIControl()).getimportUIInfoBean().isResume()){
			File resumeFile = new File(((ImportMainUIControl) getImportMainUIControl()).getimportUIInfoBean().getResumeConfigFileLocation());
			if(resumeFile.exists()){
				resumeFile.delete();
			}
			}
		}
		importProcessor.setRequestedForImportStopped(false);
	}

	private void performViewLogAction() {
		getImportMainUIControl().showLog(importProcessor.getLogFilePath_import());
	}

	private class PopulationWorker extends SwingWorker {
		public Object construct() {
			doImport();
			return this;
		}

		public void start() {
			super.start();
		}

		public void finished() {

		}
	}

	private class ProcessorPropertyChangeListener implements
	PropertyChangeListener {

		public void propertyChange(PropertyChangeEvent arg0) {
			
			String prpName = arg0.getPropertyName();
			if (prpName.equalsIgnoreCase("progressBarMaxValue")) {
				getImportMainUIControl().setinitialImportProgressValue((Integer) arg0
						.getNewValue());
				getImportMainUIControl().setViewButtonEnabled(false);
				getImportMainUIControl().setFinishButtonEnabled(false);
				getImportMainUIControl().setStopButtonEnabled(true);
			} else if (prpName.equalsIgnoreCase("progressBarCurrentValue")) {
				myProgressBarRunnable.setValue((Integer) arg0.getNewValue(),importProcessor.getCurrentlyProcessingFileName());
				SwingUtilities.invokeLater(myProgressBarRunnable);
			} else if (prpName.equalsIgnoreCase("importFinished")) {
				if ((Boolean) arg0.getNewValue()) {
					getImportMainUIControl().setStopButtonEnabled(false);
					getImportMainUIControl().renderOperationFinishControl();
					getImportMainUIControl().setImportProcessFinished(importProcessor
							.getImportFinishStatus());
					
				}
			} else if (prpName.equalsIgnoreCase("processMessage")) {
				String msg = (String) arg0.getNewValue();
				if (msg != null) {
					getImportMainUIControl().setStopButtonEnabled(true);
					getImportMainUIControl().showMessageDialog(msg);
					getImportMainUIControl().setViewButtonEnabled(false);
					getImportMainUIControl().setFinishButtonEnabled(false);
					importProcessor.setProcessMessage(null);
				}
			}else if (prpName.equalsIgnoreCase("error")) {
				String msg = (String) arg0.getNewValue();
				if (msg != null) {
					getImportMainUIControl().setStopButtonEnabled(false);
					getImportMainUIControl().showMessageDialog(msg);
					getImportMainUIControl().setViewButtonEnabled(true);
					getImportMainUIControl().setFinishButtonEnabled(true);
					importProcessor.setError(null);
				}
			}
		}
	}

	private class MyProgressBarRunnable implements Runnable {
		private int value = 0;

		private String fileName = "";

		public void setValue(int val, String pFileName) {
			value = val;
			fileName = pFileName;
		}

		public void run() {
			getImportMainUIControl().updateImportProgressBar(value, fileName);
		}
	}

}
