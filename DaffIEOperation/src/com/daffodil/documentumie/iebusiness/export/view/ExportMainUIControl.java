/*
 * MainFrame.java
 *
 * Created on 26 June 2008, 10:35
 */

package com.daffodil.documentumie.iebusiness.export.view;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import com.daffodil.documentumie.fileutil.configurator.DaffIEConfigurator;
import com.daffodil.documentumie.fileutil.configurator.bean.ConfigBean;
import com.daffodil.documentumie.fileutil.configurator.bean.ExportConfigBean;
import com.daffodil.documentumie.fileutil.configurator.exception.DConfigReadException;
import com.daffodil.documentumie.fileutil.logger.IELogger;
import com.daffodil.documentumie.iebusiness.AbstractUIControl;
import com.daffodil.documentumie.iebusiness.IEMainAbstractUIControl;
import com.daffodil.documentumie.iebusiness.LoginUIControl;
import com.daffodil.documentumie.iebusiness.export.bean.ExportUIInfoBean;
import com.daffodil.documentumie.iebusiness.export.controller.ExportController;
import com.daffodil.documentumie.iebusiness.export.model.ExportProcessor;

/**
 *
 * @author  Administrator
 */
public class ExportMainUIControl extends IEMainAbstractUIControl {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int maxIndex = getUICounter();
	
	ExportUIInfoBean exportUiInfoBean;

	ExportConfigBean configBean;

	public ExportMainUIControl(){

	}

	@Override
	protected AbstractUIControl getUI(int index) {
		System.out.println("Index screen"+index);
		if(index == 0 ){
			return new LoginUIControl();
		}
		if(index == 1 ){
			return new ESourceInfoUIControl();
		}
		if(index == 2 ){
			return new EAttributeChooseUIControl();
		}
		if(index == 3 ){
			return new EFilterCriteriaUIControl();
		}
		if(index == 4 ){
			return new EDestinationInfoUIControl();
		}
		if(index == 5 ){
			return new EVerifyUIControl();
		}
		if(index == 6 ){
			return new EPerformUIControl();
		}
		
		if (index == 8) 
		{
			return new EScheduleProcess();
		}
		if (index==9) 
		{
			return new EScheduleMappingUIControl();
			
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
				configBean = (ExportConfigBean) DaffIEConfigurator.read(DaffIEConfigurator.EXPORT);
				setConfigBean(configBean);
				
				setConfigBeanInput(configBean);
			} catch (DConfigReadException e) {
//				getIEMessageUtility().showMessageDialog(e.getMessage(), e.getCause());
				getIELogger().writeLog(e.getMessage(), IELogger.DEBUG);
			}

		}

	}

	@Override
	protected void nextUIControl() 
	{
		//setUIIndex((getUIIndex() + 1));
		if(isSchedule())
		{
			maxIndex = 10;
			abUIControl = new AbstractUIControl[maxIndex];
			setUIIndex(8);
		}
		else if (isModify()) //nity
		{
			maxIndex = 10;
			abUIControl = new AbstractUIControl[maxIndex];
			setUIIndex(9);
			 
		}
		else
		{
			setUIIndex((getUIIndex() + 1));
			}
	}

	@Override
	protected void previousUIControl() {
		//setUIIndex((getUIIndex() - 1));
		if (getUIIndex() == 8) {
			maxIndex = 7;
			setUIIndex(5);
		}else{
		setUIIndex((getUIIndex() - 1));
		}
	}

	private void setConfigBeanInput(ExportConfigBean configBean) {
		exportUiInfoBean = new ExportUIInfoBean();
		exportUiInfoBean.setRepository(configBean.getRepoName());
		exportUiInfoBean.setUserName(configBean.getUserName());
		exportUiInfoBean.setDomain(configBean.getDomain());
		exportUiInfoBean.setObjectType(configBean.getObjectType());
		exportUiInfoBean.setAllVersion(configBean.getAllVersion()==null|| "".equalsIgnoreCase(configBean.getAllVersion()) || configBean.getAllVersion().equalsIgnoreCase("no") ? false : true);
		exportUiInfoBean.setOnlyMetadata(configBean.getMetadataOnly()==null|| "".equalsIgnoreCase(configBean.getMetadataOnly()) || configBean.getMetadataOnly().equalsIgnoreCase("no") ? false : true);
		exportUiInfoBean.setExportType(configBean.getExportType());
		exportUiInfoBean.setReportType(configBean.getReportType());
		exportUiInfoBean.setExportIntoZIP(configBean.getInZip()==null|| "".equalsIgnoreCase(configBean.getInZip()) || configBean.getInZip().equalsIgnoreCase("no") ? false : true);
		addUIInfoBeanListener(exportUiInfoBean);
		setUIInfobean(exportUiInfoBean);
	}

	private void addUIInfoBeanListener(final ExportUIInfoBean exportUiInfoBean){

//		exportUiInfoBean.addPropertyChangeListener("repository",
//		new PropertyChangeListener(){

//		public void propertyChange(PropertyChangeEvent evt) {
//		exportUiInfoBean.setRepository(null);
//		}

//		});
		exportUiInfoBean.addPropertyChangeListener("userName",
				new PropertyChangeListener(){

			public void propertyChange(PropertyChangeEvent evt) {
				exportUiInfoBean.setOutPutFile(null);
				exportUiInfoBean.setMetaDataFilePath(null);
			}

		});
//		/*exportUiInfoBean.addPropertyChangeListener("password",
//		new PropertyChangeListener(){

//		public void propertyChange(PropertyChangeEvent evt) {
//		exportUiInfoBean.setPassword(null);
//		}

//		});*/
//		exportUiInfoBean.addPropertyChangeListener("domain",
//		new PropertyChangeListener(){

//		public void propertyChange(PropertyChangeEvent evt) {
//		exportUiInfoBean.setDomain(null);
//		}

//		});

		exportUiInfoBean.addPropertyChangeListener("ObjectType",
				new PropertyChangeListener(){

			public void propertyChange(PropertyChangeEvent evt) {
				//exportUiInfoBean.setObjectType(null);
				exportUiInfoBean.setAvailableAttribute(null);
				exportUiInfoBean.setSelectedAttribute(null);
			}

		});
//		exportUiInfoBean.addPropertyChangeListener("allVersion",
//		new PropertyChangeListener(){

//		public void propertyChange(PropertyChangeEvent evt) {
////		exportUiInfoBean.setAllVersion(false);
//		}

//		});exportUiInfoBean.addPropertyChangeListener("onlyMetadata",
//		new PropertyChangeListener(){

//		public void propertyChange(PropertyChangeEvent evt) {
////		exportUiInfoBean.setOnlyMetadata(false);
//		}

//		});exportUiInfoBean.addPropertyChangeListener("availableAttribute",
//		new PropertyChangeListener(){

//		public void propertyChange(PropertyChangeEvent evt) {

//		}

//		});exportUiInfoBean.addPropertyChangeListener("selectedAttribute",
//		new PropertyChangeListener(){

//		public void propertyChange(PropertyChangeEvent evt) {
//		exportUiInfoBean.setFilterParam(null);
//		}

//		});exportUiInfoBean.addPropertyChangeListener("searchCriteria",
//		new PropertyChangeListener(){

//		public void propertyChange(PropertyChangeEvent evt) {
//		exportUiInfoBean.setDomain(null);
//		}

//		});exportUiInfoBean.addPropertyChangeListener("metaDataFileName",
//		new PropertyChangeListener(){

//		public void propertyChange(PropertyChangeEvent evt) {

//		}

//		});
//		exportUiInfoBean.addPropertyChangeListener("outPutFile",
//		new PropertyChangeListener(){

//		public void propertyChange(PropertyChangeEvent evt) {

//		}

//		});exportUiInfoBean.addPropertyChangeListener("filterParam",
//		new PropertyChangeListener(){

//		public void propertyChange(PropertyChangeEvent evt) {
//		exportUiInfoBean.setDqlText(null);
//		}

//		});exportUiInfoBean.addPropertyChangeListener("dqlText",
//		new PropertyChangeListener(){

//		public void propertyChange(PropertyChangeEvent evt) {

//		}

//		});
//		exportUiInfoBean.addPropertyChangeListener("exportType",
//		new PropertyChangeListener(){

//		public void propertyChange(PropertyChangeEvent evt) {
//		//	exportUiInfoBean.setExportType(null);
//		}

//		});
//		exportUiInfoBean.addPropertyChangeListener("ExportIntoZIP",
//		new PropertyChangeListener(){

//		public void propertyChange(PropertyChangeEvent evt) {
//		//	exportUiInfoBean.setExportIntoZIP(false);
//		}

//		});
//		exportUiInfoBean.addPropertyChangeListener("reportType",
//		new PropertyChangeListener(){

//		public void propertyChange(PropertyChangeEvent evt) {
//		//	exportUiInfoBean.setReportType(null);
//		}

//		});


	}
	public ExportUIInfoBean getExportUIInfoBen(){
		return exportUiInfoBean;
	}

	@Override
	protected void startProcess() {		
		ExportProcessor exportProcessor = new ExportProcessor();
		ExportController exportController = new ExportController(this , exportProcessor);
		exportController.startExport();
	}

	@Override
	protected int getConfigBeanOperation() {
		return DaffIEConfigurator.EXPORT;
	}

	@Override
	protected ConfigBean getConfigBeanToWrite() {
		ExportConfigBean bean = new ExportConfigBean();
		bean.setUserName(exportUiInfoBean.getUserName());
		bean.setRepoName(exportUiInfoBean.getRepository());
		bean.setDomain(exportUiInfoBean.getDomain());
		bean.setExportType(exportUiInfoBean.getExportType());
		bean.setReportType(exportUiInfoBean.getReportType());
		bean.setObjectType(exportUiInfoBean.getObjectType());
		bean.setAllVersion(exportUiInfoBean.isAllVersion()? "yes" : "no");
		bean.setInZip(exportUiInfoBean.isExportIntoZIP() ? "yes" : "no");
		bean.setMetadataOnly(exportUiInfoBean.isOnlyMetadata() ? "yes" : "no");
		return (ConfigBean)bean;
	}


	public void renderOperationFinishControl(){
		String path = "/com/daffodil/documentumie/iebusiness/export/view/images/export_complete.gif";
		((EPerformUIControl) getUiControl()).renderExportImage(path);
	}
	public void setExportProcessFinished(String str){
		getIEMessageUtility().showMessageDialog(str, null);
		setViewButtonEnabled(true);
		setFinishButtonEnabled(true);
	}

	public void setinitialExportProgressValue(int maxVal) {
		((EPerformUIControl) getUiControl()).setinitialProgressValue(maxVal);
	}

	public void updateExportProgressBar(int val, String fileName) {
		((EPerformUIControl) getUiControl()).updateProgressBar(val, fileName);
	}

	public void writeResumeConfigBean() {
		// TODO Auto-generated method stub

	}
}
