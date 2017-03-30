package com.daffodil.documentumie.iebusiness.export.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.daffodil.documentumie.dctm.exception.DDfException;
import com.daffodil.documentumie.filehandler.FTPFileHandler;
import com.daffodil.documentumie.filehandler.LocalSystemFileHandler;
import com.daffodil.documentumie.filehandler.SFTPFileHandler;
import com.daffodil.documentumie.fileutil.logger.IELogger;
import com.daffodil.documentumie.iebusiness.IEMainAbstractUIControl;
import com.daffodil.documentumie.iebusiness.SwingWorker;
import com.daffodil.documentumie.iebusiness.export.bean.ExportServiceBean; 
import com.daffodil.documentumie.iebusiness.export.bean.ExportUIInfoBean;
import com.daffodil.documentumie.iebusiness.export.model.ExportProcessor; 
import com.daffodil.documentumie.iebusiness.export.view.ExportMainUIControl; 
import com.sun.org.apache.xml.internal.utils.StopParseException;

public class ExportController {

    private ExportMainUIControl mExportMainUIControl;

    private ExportProcessor mExportProcessor;

    private ExportServiceBean mExportServiceBean;
    myProgressBarRunnable myProgressBarRunnable = null;
    
    public ExportController (IEMainAbstractUIControl ieExportMainUIControl, ExportProcessor exportProcessor) {
    	setExportMainUIControl((ExportMainUIControl) ieExportMainUIControl);
    	setExportProcessor(exportProcessor);
    	initialize();
    	installListener();
    	myProgressBarRunnable = new myProgressBarRunnable();
    }

    public ExportMainUIControl getExportMainUIControl () {
        return mExportMainUIControl;
    }

    public void setExportMainUIControl (ExportMainUIControl val) {
        this.mExportMainUIControl = val;
    }

    public ExportProcessor getExportProcessor () {
        return mExportProcessor;
    }

    public void setExportProcessor (ExportProcessor val) {
        this.mExportProcessor = val;
    }

    public ExportServiceBean getExportServiceBean () {
        return mExportServiceBean;
    }

    public void setExportServiceBean (ExportServiceBean val) {
        this.mExportServiceBean = val;
    }
    
    public void startExport (){
		PopulationWorker populationWorker = new PopulationWorker();
		populationWorker.start();
	}
    
    public void doExport() {
    	try {
			getExportProcessor().doExport();
		} catch (DDfException e) {
			getExportMainUIControl().getIELogger().writeLog("while calling doExport " + e.getMessage() + e.getCause(), IELogger.DEBUG);
			getExportMainUIControl().getIEMessageUtility().showMessageDialog(e.getMessage(), e.getCause());
			getExportProcessor().setExportCancelled(true); //TODO check  ***************
		}
    }
    
    private void initialize(){
		
    	ExportServiceBean exportServiceBean= new ExportServiceBean();
		ExportUIInfoBean exportUiInfoBean = getExportMainUIControl().getExportUIInfoBen();
		exportServiceBean.setRepository(exportUiInfoBean.getRepository());
		exportServiceBean.setUsername(exportUiInfoBean.getUserName());
		exportServiceBean.setDomain(exportUiInfoBean.getDomain());
		exportServiceBean.setObjectType(exportUiInfoBean.getObjectType());
		exportServiceBean.setAllVersion(exportUiInfoBean.isAllVersion());
		exportServiceBean.setOnlyMetadata(exportUiInfoBean.isOnlyMetadata());
		exportServiceBean.setAvailableAttribute(exportUiInfoBean.getAvailableAttribute());
		exportServiceBean.setSelectedAttribute(exportUiInfoBean.getSelectedAttribute());
		exportServiceBean.setMetaDataFilePath(exportUiInfoBean.getMetaDataFilePath());
		exportServiceBean.setOutPutFile(exportUiInfoBean.getOutPutFile());
	//	exportServiceBean.setMatchCase(exportUiInfoBean.isMatchCase());
		exportServiceBean.setDqlText(exportUiInfoBean.getDqlText());
		exportServiceBean.setReportType(exportUiInfoBean.getReportType());
		exportServiceBean.setExportType(exportUiInfoBean.getExportType());
		exportServiceBean.setExportIntoZIP(exportUiInfoBean.isExportIntoZIP());
		
		// Below code is added by Harsh for the implementation of the FTP functionality on 12/29/2011
		
		String exportTo = exportUiInfoBean.getExportToLocation();
		if(exportTo!=null)
		{
			if(exportTo.equalsIgnoreCase("ftp"))
			{
				exportServiceBean.setMetaDataFilePath(exportUiInfoBean.getFtpMetadataFile());
				exportServiceBean.setOutPutFile(exportUiInfoBean.getFtpOutputFile());
				String ftpUrl = exportUiInfoBean.getFtpURL();
				String ftpuserName =exportUiInfoBean.getFtpUserName();
				String ftpPassword = exportUiInfoBean.getFtpPassword();
				if(ftpUrl!=null && ftpUrl.startsWith("sftp")){
					ftpUrl =  ftpUrl.substring(ftpUrl.indexOf(":")+3,ftpUrl.length());
				SFTPFileHandler ffh = new SFTPFileHandler(ftpUrl, ftpuserName, ftpPassword);
				mExportMainUIControl.getCSServiceProvider().setContentFileHandler(ffh);
				}
				else if(ftpUrl!=null && ftpUrl.startsWith("ftp")){
					ftpUrl =  ftpUrl.substring(ftpUrl.indexOf(":")+3,ftpUrl.length());
					FTPFileHandler ffh = new FTPFileHandler(ftpUrl, ftpuserName, ftpPassword);
					try {
						ffh.connect();
					} catch (DDfException e) {
						// TODO Auto-generated catch block
						System.out.println("**********************************************");
						e.printStackTrace();
					}
					mExportMainUIControl.getCSServiceProvider().setContentFileHandler(ffh);
				}
				//SFTPFileHandler ffh = new SFTPFileHandler(ftpUrl, ftpuserName, ftpPassword);
				/*try {
					ffh.connect();
				} catch (DDfException e) {
					e.printStackTrace();
				}*/
				//mExportMainUIControl.getCSServiceProvider().setContentFileHandler(ffh);
			}
			else if(exportTo.equalsIgnoreCase("file")){
				LocalSystemFileHandler lfh = new LocalSystemFileHandler();
				mExportMainUIControl.getCSServiceProvider().setContentFileHandler(lfh);
			}
		}
		
		mExportProcessor.setCsServiceProvider(mExportMainUIControl.getCSServiceProvider());
		mExportProcessor.setMetadataWriter(mExportMainUIControl.getMetadataWriter());
		mExportProcessor.setExportServiceBean(exportServiceBean);
		mExportProcessor.setIELogger(mExportMainUIControl.getIELogger());
    }
    
 
    private void installListener() {
		getExportMainUIControl().registerActionListener(
				new UIControlActionListener());
		getExportProcessor().registerPropertyChangeListener(
				new ProcessorPropertyChangeListener());
	}

	private class UIControlActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equalsIgnoreCase("Stop")) {
				performStopAction();
			} else if (e.getActionCommand().equalsIgnoreCase("View Log")) {
				performViewLogAction();
			} else if (e.getActionCommand().equalsIgnoreCase("Finish")) {
				System.out.println("*************************555555*********************");
				performFinishAction();
			}
		}
	}

	private void performFinishAction() {
		getExportMainUIControl().writeConfigBean();
		System.out.println("******************6666****************************");
		int i = getExportMainUIControl().promptUserFinishProcess();
		System.out.println("***********************************777***********");
		if (i == JOptionPane.YES_OPTION) {
		getExportMainUIControl().showIEIntroUIControl();
		System.out.println("*******************453543***************************");
		}else{//close session
			System.exit(0);
		}
		
	}

	private void performStopAction() {
		getExportProcessor().setRequestedForExportStopped(true);
		//getExportMainUIControl().renderOperationFinishControl();
		int i = getExportMainUIControl().promptUserStopExport();
		if (i == JOptionPane.YES_OPTION) {
			getExportMainUIControl().setStopButtonEnabled(false);
			getExportMainUIControl().renderOperationFinishControl();
			getExportProcessor().setExportCancelled(true);
//			getExportMainUIControl().writeResumeConfigBean();
		}
		getExportProcessor().setRequestedForExportStopped(false);
	}

	private void performViewLogAction() {
		getExportMainUIControl().showLog(getExportProcessor().getLogFilePath_export());
	}

	private class PopulationWorker extends SwingWorker {
		public Object construct() {
			doExport();
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
				getExportMainUIControl().setinitialExportProgressValue((Integer) arg0
						.getNewValue());
			} else if (prpName.equalsIgnoreCase("progressBarCurrentValue")) {
				myProgressBarRunnable.setValue((Integer) arg0.getNewValue(),
						getExportProcessor().getCurrentlyProcessingFileName());
				SwingUtilities.invokeLater(myProgressBarRunnable);
			} else if (prpName.equalsIgnoreCase("exportFinished")) {
				if ((Boolean) arg0.getNewValue()) {
					getExportMainUIControl().setStopButtonEnabled(false);
					getExportMainUIControl().renderOperationFinishControl();
					getExportMainUIControl().setExportProcessFinished("Export Process Finished");
				}
			} else if (prpName.equalsIgnoreCase("processMessage")) {
				String msg = (String) arg0.getNewValue();
				if (msg != null) {
					getExportMainUIControl().setStopButtonEnabled(true);
					getExportMainUIControl().showMessageDialog(msg);
					// mainUIControl.enableInputComponents(true);
					getExportMainUIControl().setViewButtonEnabled(false);
					getExportMainUIControl().setFinishButtonEnabled(false);
					getExportProcessor().setProcessMessage(null);
				}
			}else if (prpName.equalsIgnoreCase("error")) {
				String msg = (String) arg0.getNewValue();
				if (msg != null) {
					getExportMainUIControl().setStopButtonEnabled(false);
					getExportMainUIControl().showMessageDialog(msg);
					getExportMainUIControl().setViewButtonEnabled(true);
					getExportMainUIControl().setFinishButtonEnabled(true);
					getExportProcessor().setError(null);
				}
			}
		}
	}

	private class myProgressBarRunnable implements Runnable {
		private int value = 0;

		private String fileName = "";

		public void setValue(int val, String pFileName) {
			value = val;
			fileName = pFileName;
		}

		public void run() {
			getExportMainUIControl().updateExportProgressBar(value, fileName);
		}
	}

}

