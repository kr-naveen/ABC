/*
 * MainFrame.java
 *
 * Created on 26 June 2008, 10:35
 */

package com.daffodil.documentumie.iebusiness.pimport.view;


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;

import javax.swing.JOptionPane;

import com.daffodil.documentumie.fileutil.configurator.DaffIEConfigurator;
import com.daffodil.documentumie.fileutil.configurator.DaffIEResumeConfigurator;
import com.daffodil.documentumie.fileutil.configurator.bean.ConfigBean;
import com.daffodil.documentumie.fileutil.configurator.bean.ImportConfigBean;
import com.daffodil.documentumie.fileutil.configurator.exception.DConfigReadException;
import com.daffodil.documentumie.fileutil.logger.IELogger;
import com.daffodil.documentumie.iebusiness.AbstractUIControl;
import com.daffodil.documentumie.iebusiness.IEMainAbstractUIControl;
import com.daffodil.documentumie.iebusiness.LoginUIControl;
import com.daffodil.documentumie.iebusiness.pimport.bean.ImportUIInfoBean;
import com.daffodil.documentumie.iebusiness.pimport.controller.ImportController;
import com.daffodil.documentumie.iebusiness.pimport.model.ImportConstant;
import com.daffodil.documentumie.iebusiness.pimport.model.ImportProcessor;

/**
 *W
 * @author  Administrator
 */
public class ImportMainUIControl extends IEMainAbstractUIControl {
	ImportUIInfoBean importuiinfo;
	ImportProcessor importProcessor;
	private HashMap<String, String> map = null;
	private int maxIndex = getUICounter();
//	private AbstractUIControl[] abUIControl = new AbstractUIControl[maxIndelx];
	/** Creates new form MainFrame */
	public ImportMainUIControl() {
		importProcessor = new ImportProcessor();
	}

	protected AbstractUIControl getUI(int index) {
		if (index == 0) {
			return new LoginUIControl();
		}
		if (index == 1) {
			return new ISourceInfoUIControl();
		}
		if (index == 2) {
			return new IFieldMappingUIControl();
		}
		if (index == 3) {
			return new IInputFilter();
		}
		if (index == 4) {
			return new IRunOptionUIControl();
		}
		if (index == 5) {
			return new IVerifyUIControl();
		}
		if (index == 6) {
			return new IPerformUIControl();
		}
		if (index == 7) {
			return new IResumeProcess();
		}
		if (index == 8) 
		{			
		return new IScheduleProcess();
		}
		if (index == 9) 
		{
			return new IScheduleMappingUIControl();
			
		}
		return null;
	}

	@Override
	protected int getUICounter() {
		return 10;//7
	}

	@Override
	protected void initialize() {
		if(getConfigBean() == null){
			try {
				ImportConfigBean configBean = (ImportConfigBean) DaffIEConfigurator.read(DaffIEConfigurator.IMPORT);
				setConfigBean(configBean);
				setConfigBeanInput(configBean);
			} catch (DConfigReadException e) {
				getIEMessageUtility().showMessageDialog(e.getMessage(), e.getCause());
				getIELogger().writeLog(e.getMessage(), IELogger.DEBUG);
			}
		}

		if(getIELogger() == null){
			setIELogger(new IELogger(getConfigBean()));
		}
	}

	@Override
	protected void nextUIControl() {
		if (getUIIndex() == 0) {
			/*map = new HashMap<String, String>();
			map = checkForResumProcess();
			if (map != null && !map.isEmpty()) {
				int i = promptUserResumeProcess();
				if (i == JOptionPane.YES_OPTION) {
					maxIndex = 8;
					abUIControl = new AbstractUIControl[maxIndex];
					*//** *********** check ***** *//*
					setUIIndex(7);
				} else {
					// abUIControl = new
					// AbstractUIControl[maxIndex];
					setUIIndex((getUIIndex() + 1));
				}
			} else {
//				 abUIControl = new AbstractUIControl[maxIndex];
				setUIIndex((getUIIndex() + 1));
			}*/
			setUIIndex((getUIIndex() + 1));
		} else {
			/*if (getUIIndex() == 7) {
				maxIndex = 7;
				abUIControl = new AbstractUIControl[maxIndex];
				setUIIndex((getUIIndex() - 2));
			} else {*/
				if(isSchedule()){
				maxIndex = 10;
				abUIControl = new AbstractUIControl[maxIndex];
				setUIIndex(8);
				}
				else if (isModify()) 
				{
					System.out.println("inside the ismodify");
					maxIndex = 10;
					abUIControl = new AbstractUIControl[maxIndex];
					setUIIndex(9);
					
				}
				else{
				setUIIndex((getUIIndex() + 1));
				}
//			}
		}
	}

	@Override
	protected void previousUIControl() {
		if (getUIIndex() == 5) {
			setUIIndex((getUIIndex() - 1));
			/*if (map != null && !map.isEmpty()) {
				maxIndex = 8;
				abUIControl = new AbstractUIControl[maxIndex];
				*//** *********** check ***** *//*
				setUIIndex(7);

			} else {
				setUIIndex((getUIIndex() - 1));
			}*/
		} else {
			/*if (getUIIndex() == 7) {
				maxIndex = 7;
				setUIIndex(0);
				getimportUIInfoBean().setResume(false);
			} else {*/
				if (getUIIndex() == 8) {
					maxIndex = 10;
					setUIIndex(5);
				}
				if (getUIIndex()==9)
				{
					maxIndex = 10;
					setUIIndex(8);
				}
				else{
					setUIIndex((getUIIndex() - 1));
				}
//			}
		}
	}
	
	private HashMap checkForResumProcess() {
		map = DaffIEResumeConfigurator.findResumableProcesses(getimportUIInfoBean().getUserName());
		return map;
	}
	
	private void setConfigBeanInput(ImportConfigBean configBean){
		importuiinfo = new ImportUIInfoBean();
		importuiinfo.setRepository(configBean.getRepoName());
		importuiinfo.setUserName(configBean.getUserName());
		importuiinfo.setDomain(configBean.getDomain());
		importuiinfo.setObjectType(configBean.getObjectType());
		importuiinfo.setExtension(configBean.getInputType());
		importuiinfo.setIsLive( configBean.getLiveRun()==null|| "".equalsIgnoreCase(configBean.getLiveRun()) || configBean.getLiveRun().equalsIgnoreCase("no")? false : true);
		importuiinfo.setUpdateExisting(configBean.getUpdateExisting()==null|| "".equalsIgnoreCase(configBean.getUpdateExisting()) || configBean.getUpdateExisting().equalsIgnoreCase("no") ? false : true);

		// private add UIInfo Bean Listener
		addUIInfoBeanListener(importuiinfo);
		setUIInfobean(importuiinfo);

	}

	private void addUIInfoBeanListener(final ImportUIInfoBean importuiinfo){
		importuiinfo.addPropertyChangeListener("userName",
				new PropertyChangeListener(){

					public void propertyChange(PropertyChangeEvent evt) {
						importuiinfo.setMetadataInputFile(null);
					}
			
		});
		importuiinfo.addPropertyChangeListener("objectType",
				new PropertyChangeListener(){

					public void propertyChange(PropertyChangeEvent evt) {
						importuiinfo.setSelectedAttributesOfRepo(null);
						importuiinfo.setMappedAttributs(null);
					}
			
		});
	}
	
	protected void startProcess(){
		ImportController importController = new ImportController(this, importProcessor);
		importController.startImport();
	}

	public ImportUIInfoBean getimportUIInfoBean(){
		return importuiinfo;
	}

	@Override
	protected int getConfigBeanOperation() {
		return DaffIEConfigurator.IMPORT;
	}

	@Override
	protected ConfigBean getConfigBeanToWrite() {
		ImportConfigBean bean = new ImportConfigBean();
		bean.setUserName(importuiinfo.getUserName());
		bean.setRepoName(importuiinfo.getRepository());
		bean.setDomain(importuiinfo.getDomain());
		bean.setObjectType(importuiinfo.getObjectType());
		bean.setLiveRun(importuiinfo.getIsLive()? "yes" : "no");
		bean.setUpdateExisting(importuiinfo.isUpdateExisting()? "yes" : "no");
		bean.setInputType(importuiinfo.getExtension());
		return bean;
	}
	
	public void renderOperationFinishControl(){
		String path = "/com/daffodil/documentumie/iebusiness/pimport/view/images/import_complete.gif";
		((IPerformUIControl) getUiControl()).renderImportImage(path);
	}

	public void setImportProcessFinished(int i) {
		if (i == ImportConstant.IMPORT_FINISHED_LIVE_RUN) {
			getIEMessageUtility().showMessageDialog("Live run completed!!", null);
		} else if (i == ImportConstant.IMPORT_FINISHED_TEST_RUN) {
			getIEMessageUtility().showMessageDialog("Test run completed!!", null);
		} else if (i == ImportConstant.IMPORT_STOPPED_LIVE_RUN) {
			getIEMessageUtility().showMessageDialog("Live run stopped.", null);
		} else if (i == ImportConstant.IMPORT_STOPPED_TEST_RUN) {
			getIEMessageUtility().showMessageDialog("Test run stopped.", null);
		}
		setViewButtonEnabled(true);
		setFinishButtonEnabled(true);
	}
	
	public void setinitialImportProgressValue(int maxVal) {
		((IPerformUIControl) getUiControl()).setinitialProgressValue(maxVal);
	}
	
	public void updateImportProgressBar(int val, String fileName) {
		((IPerformUIControl) getUiControl()).updateProgressBar(val, fileName);
	}

}
