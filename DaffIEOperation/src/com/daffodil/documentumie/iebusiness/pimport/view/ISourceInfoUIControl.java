/*
 * ISourceInfoUIControl.java
 *
 * Created on 26 June 2008, 15:10
 */

package com.daffodil.documentumie.iebusiness.pimport.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import com.daffodil.documentumie.dctm.exception.DDfException;
import com.daffodil.documentumie.fileutil.logger.IELogger;
import com.daffodil.documentumie.fileutil.metadata.MetadataProcessorFactory;
import com.daffodil.documentumie.fileutil.metadata.MetadataUtility;
import com.daffodil.documentumie.fileutil.metadata.exception.DMetadataReadException;
import com.daffodil.documentumie.iebusiness.AbstractUIControl;
import com.daffodil.documentumie.iebusiness.DButton;
import com.daffodil.documentumie.iebusiness.IEUtility;
import com.daffodil.documentumie.iebusiness.pimport.bean.ImportUIInfoBean;

import com.daffodil.documentumie.iebusiness.pimport.model.ImportConstant;


/**
 *
 * @author  Administrator
 */
public class ISourceInfoUIControl extends AbstractUIControl {

	String[] worksheetList;
	String imageLocation;
	String showMessage;
	StringBuffer errorMessage;
	PropertyChangeSupport propertyChangeSupport;
	String value;
	String extension;
	// Variable added by Harsh for implementing FTP functionality on 12/22/2011
	String importFrom;
	private  FTPClient ftp;
	String ftpDownloadedMetadataPath;
	
	/** Creates new form ISourceInfoUIControl */
	public ISourceInfoUIControl() {
		propertyChangeSupport = new PropertyChangeSupport(this);
		initComponents();
		initlizeUI();
	}
	ButtonGroup buttonGroup;
	ButtonGroup buttonGroupFTP;// Added by Harsh for FTP functionality on 12/21/2011
	@Override
	protected void initUI() {
		super.initUI();
		buttonGroup = new ButtonGroup();
		buttonGroup.add(csv_JRadioButton);
		buttonGroup.add(xls_JRadioButton);
		buttonGroup.add(xml_JRadioButton);
		worksheet_JComboBox.setEnabled(false);
		// Below code is added by Harsh for FTP functionality on 12/21/2011
		buttonGroupFTP = new ButtonGroup();
		buttonGroupFTP.add(fileSystem_JRadioButton);
		buttonGroupFTP.add(ftp_JRadioButton);
		
	}

	// TODO work for handeling of all files extensions.
	
	//method to show all object types of selected repository
	private void showObjectType() {
		objectType_JComboBox.removeAllItems();
		try {
			List list = getCSServiceProvider().getAvailableObjecTypes();
			for (int i = 0; i < list.size(); i++) {
				objectType_JComboBox.addItem(list.get(i));
			}
		} catch (DDfException e) {
			if(errorMessage == null){
				errorMessage = new StringBuffer();
			}
			errorMessage.append(e.getMessage());
			getIEMessageUtility().showMessageDialog(e.getMessage(), e.getCause());
		}

		// Call getRepoListMethod from Services.

	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
		
	
//		metadatatFileBrowse_JButton.setEnabled(true);
//		metadataFile_JTextField.setEnabled(true);
	}
	// Below importFrom method is written by Harsh for FTP functionality on 12/22/2011 
	public String getImportFrom() {
		return importFrom;
	}

	public void setImportFrom(String importFrom) {
		this.importFrom = importFrom;
	}

	protected void installListener() {
		metadatatFileBrowse_JButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				extension = getExtension();   // to get extesnion  from source 
				String storedImportFilePath = metadataFile_JTextField.getText();
				String importFilePath = IEUtility.chooseImportMetadataFile(ISourceInfoUIControl.this, extension, getImportUIInfoBean().getMetadataInputFile());
				if ("".equalsIgnoreCase(importFilePath)) {
					metadataFile_JTextField.setText(storedImportFilePath);
				}else{
					metadataFile_JTextField.setText(importFilePath);
				}
				metadataFile_JTextField.grabFocus();
				checkFileExistance(metadataFile_JTextField.getText());
				if(getExtension().equalsIgnoreCase("xls") && !(metadataFile_JTextField.getText()==null || "".equalsIgnoreCase(metadataFile_JTextField.getText()))){
					worksheetPopulate();
				}
			}
		});

		metadataFile_JTextField.addPropertyChangeListener("value",
				new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent arg0) {
				worksheet_JComboBox.removeAllItems();
				worksheet_JComboBox.setEnabled(false);

				String file_path = (String) metadataFile_JTextField
				.getText();
				boolean existance = checkFileExistance(file_path);
				if(!existance){
					if(errorMessage == null){
						errorMessage = new StringBuffer();
					}
					errorMessage.append("input File does not existed");
					getIEMessageUtility().showMessageDialog(errorMessage.toString(), null);
					errorMessage = null;
				}else{
					if(getExtension().equalsIgnoreCase("xls") && !(metadataFile_JTextField.getText()==null || "".equalsIgnoreCase(metadataFile_JTextField.getText()))){
					worksheetPopulate();}
				}
			}
		});

		csv_JRadioButton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				setExtension("csv");
				metadataFile_JTextField.setText("");
				worksheet_JComboBox.setSelectedItem("");
				worksheet_JComboBox.setEnabled(false);
			}
		});

		xls_JRadioButton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				setExtension("xls");
				metadataFile_JTextField.setText("");			
				worksheet_JComboBox.setEnabled(false);
				worksheet_JComboBox.setSelectedItem("");
			}
		});

		xml_JRadioButton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				setExtension("xml");
				metadataFile_JTextField.setText("");
				worksheet_JComboBox.setSelectedItem("");
				worksheet_JComboBox.setEnabled(false);
			}
		});
		// Below code is added by Harsh for the FTP functionality on 12/21/2011
		
		ftp_JRadioButton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {				
				System.out.println("FTP checked");
				errorMessage = null;
				setImportFrom("ftp");
				metadataFile_JLabel.setVisible(false);
				metadataFile_jPanel.setVisible(false);
				worksheet_JLabel.setVisible(false);
				worksheet_JComboBox.setVisible(false);
				updateExisting_JLabel.setVisible(false);
				updateExisting_JCheckBox.setVisible(false);
				ftpUrl_JLabel.setVisible(true);
				ftpUrl_JTextField.setVisible(true);
				user_JLabel.setVisible(true);
				user_JTextField.setVisible(true);
				password_JLabel.setVisible(true);
				password_JPasswordField.setVisible(true);
				metadataFileFTP_JLabel.setVisible(true);
				metadata_JTextField.setVisible(true);
				worksheet_JTextField.setVisible(true);
				worksheetFTP_JLabel.setVisible(true);
				
			}
		});
		
		fileSystem_JRadioButton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {				
				System.out.println("File System checked");
				errorMessage = null;
				setImportFrom("file");
				metadataFile_JLabel.setVisible(true);
				metadataFile_jPanel.setVisible(true);
				worksheet_JLabel.setVisible(true);
				worksheet_JComboBox.setVisible(true);
				updateExisting_JLabel.setVisible(true);
				updateExisting_JCheckBox.setVisible(true);
				ftpUrl_JLabel.setVisible(false);
				ftpUrl_JTextField.setVisible(false);
				ftpUrl_JTextField.setText("");
				user_JLabel.setVisible(false);
				user_JTextField.setVisible(false);
				user_JTextField.setText("");
				password_JLabel.setVisible(false);
				password_JPasswordField.setVisible(false);
				password_JPasswordField.setText("");
				metadataFileFTP_JLabel.setVisible(false);
				metadata_JTextField.setVisible(false);
				metadata_JTextField.setText("");
				worksheetFTP_JLabel.setVisible(false);
				worksheet_JTextField.setText("");
				worksheet_JTextField.setVisible(false);
			}
		});
		
		// Above code is added by Harsh for FTP functionality on 12/21/2011

	}

	/**
	 * Add a property change listener for a specific property.
	 * 
	 * @param propertyName
	 *            The name of the property to listen on.
	 * @param listener
	 *            The <code>PropertyChangeListener</code> to be added.
	 */
	public void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	/**
	 * Remove a property change listener for a specific property.
	 * 
	 * @param propertyName
	 *            The name of the property that was listened on.
	 * @param listener
	 *            The <code>PropertyChangeListener</code> to be removed
	 */
	public void removePropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(propertyName,
				listener);
	}

	private boolean checkFileExistance(String file_path) {
		boolean exist_not;
		if (file_path == null || "".equals(file_path != null ? file_path.trim() : "")) {
			return true;
		}
		File contentfile = new File(file_path);
		exist_not = contentfile.exists();
		return exist_not;
	}

	private void worksheetPopulate()
	{
		worksheet_JComboBox.removeAllItems();
		worksheet_JComboBox.setEnabled(false);
		String inputText = (String) metadataFile_JTextField.getText();
		worksheetList = MetadataUtility.getExcelSheets(inputText);
		worksheet_JComboBox.addItem("");
		for (int i = 0; i < worksheetList.length; i++) {
			worksheet_JComboBox.addItem(worksheetList[i]);
		}
		worksheet_JComboBox.setEnabled(true);
	}

	@Override
	protected void preInilize() {
		getIELogger().writeLog("passing preInilize", IELogger.DEBUG);

		// Below code is modified by harsh for implementation of the FTP functionality on 12/26/2011
		showObjectType();
		String objecttype = getImportUIInfoBean().getObjectType();
		String metadataFile = getImportUIInfoBean().getMetadataInputFile();
		String worksheet = getImportUIInfoBean().getWorksheet();
		objectType_JComboBox.setSelectedItem(objecttype);
		String reportType = getImportUIInfoBean().getExtension();
		if(reportType == null|| "".equalsIgnoreCase(reportType.trim()) || reportType.equalsIgnoreCase("csv") ){
			csv_JRadioButton.setSelected(true);
			setExtension("csv");
		}else{
			if("xls".equalsIgnoreCase(reportType.trim())){
			xls_JRadioButton.setSelected(true);
			setExtension("xls");
		}else{if(reportType.equalsIgnoreCase("xml")){
			xml_JRadioButton.setSelected(true);
			setExtension("xml");
		}
		}
		}
		metadataFile_JTextField.setText(metadataFile);
		worksheet_JComboBox.setSelectedItem(worksheet);
		updateExisting_JCheckBox.setSelected(getImportUIInfoBean().isUpdateExisting());
	}

	HashMap inputAttributes = null;

	private List getMetadataAttrbuteList() {
		getIELogger().writeLog("passing getMetadataAttrbuteList", IELogger.DEBUG);
		List metadataAttributes = new ArrayList() ;
		try {
			inputAttributes = getMetadataReader().getAttributes();
			
			for (Iterator iterator = inputAttributes.entrySet().iterator(); iterator
			.hasNext();) {
				Map.Entry entry = (Map.Entry) iterator.next();
				String key = entry.getKey().toString();
				metadataAttributes.add(key);
			}
			return metadataAttributes;
		} catch (DMetadataReadException e) {
			if(errorMessage == null){
				errorMessage = new StringBuffer();
			}
			errorMessage.append(e.getMessage());
			getIEMessageUtility().showMessageDialog(e.getMessage(), e.getCause());
		}finally{
			File file = new File("xlsql_config.xml");
			if(file.exists()){
			file.delete();
			}

			File file1 = new File("Xlsql.log");
			if(file1.exists()){
			file1.delete();
			}
		}
		return metadataAttributes;
	}
	
	protected void postValidateAction() {
		getIELogger().writeLog("passing postValidateAction", IELogger.DEBUG);
		super.postValidateAction();
		String ext = getExtension();
		if(extension.equalsIgnoreCase("xls")){
			initExcelmetadataReader(ext);
		}if(extension.equalsIgnoreCase("csv")){
			initCSVMetadataReader(ext);
		}if(extension.equalsIgnoreCase("xml")){
			initXMLMetadataReader(ext);
		}
		
		List metadataAttributes = new ArrayList(); 
			metadataAttributes = getMetadataAttrbuteList();
			getIELogger().writeLog("inputAttributes "+inputAttributes, IELogger.DEBUG);
			getImportUIInfoBean().setSelectedAttributesOfMetadata(inputAttributes);
		
		
		try {
			int objectType= getCSServiceProvider().validInputHeader((String) objectType_JComboBox.getSelectedItem());
			getImportUIInfoBean().setObjectHirerchy(objectType);
		} catch (DDfException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
			/*if(metadataAttributes == null){
			//TODo
//			getIEMessageUtility().showMessageDialog("Please Close the Input File", null);
		}*/
	/*	Object[] inputArray = validInputFileHeader(metadataAttributes, (String) objectType_JComboBox.getSelectedItem());
		int objectHirerchy = (Integer) inputArray[1];
		boolean validXls = true;
		if (!(Boolean) inputArray[0]) {
			errorMessage  = new StringBuffer();
			errorMessage.append((String)inputArray[2]);
			getIEMessageUtility().showMessageDialog(errorMessage.toString(), null);
			validXls = false;
		}*/
		//TODO set metadat attributes 
//		if(validXls){
//			errorMessage = null;
//			getImportUIInfoBean().setObjectHirerchy(objectHirerchy);
//		}
			
			getIELogger().writeLog("ending postValidateAction", IELogger.DEBUG);
	}

	public void initExcelmetadataReader(String ext) {
		HashMap map = new HashMap();
		
		// Below code is updated by Harsh for FTP implementation on 26/12/2011
		if(getImportFrom().equalsIgnoreCase("file"))
		{		
			map.put("File_Name", metadataFile_JTextField.getText());
			map.put("Table_Name", (String) worksheet_JComboBox.getSelectedItem());
			map.put("extension", ext);
		}
		else if(getImportFrom().equalsIgnoreCase("ftp"))
		{
			System.out.println("File_Name"+ ftpDownloadedMetadataPath);
			System.out.println("Table_Name:"+ (String) worksheet_JTextField.getText()+":");
			map.put("File_Name", ftpDownloadedMetadataPath);
			map.put("Table_Name", (String) worksheet_JTextField.getText());
			map.put("extension", ext);
		}
		getMainUIControl().setMetadataReader(MetadataProcessorFactory.getMetadataReader(map));
	}

	private void initXMLMetadataReader(String ext) {
		// Below code is updated by Harsh for FTP implementation on 26/12/2011
		HashMap map = new HashMap();
		if(getImportFrom().equalsIgnoreCase("file"))
		{
			String path = metadataFile_JTextField.getText();
			String reportFileLocation = path.substring(0, path.lastIndexOf('\\')+1);
			String reportFileName = path.substring(path.lastIndexOf('\\')+1, path.length()-4);
			
			map.put("File_Name", reportFileLocation);
			map.put("Table_Name", reportFileName);
			map.put("extension", ext);
		}
		else if(getImportFrom().equalsIgnoreCase("ftp"))
		{
			map.put("File_Name", ftpDownloadedMetadataPath.substring(0, ftpDownloadedMetadataPath.lastIndexOf('\\')+1));
			map.put("Table_Name", ftpDownloadedMetadataPath.substring(ftpDownloadedMetadataPath.lastIndexOf('\\')+1, ftpDownloadedMetadataPath.length()-4));
			map.put("extension", ext);
		}
		getMainUIControl().setMetadataReader(MetadataProcessorFactory.getMetadataReader(map));
	}

	private void initCSVMetadataReader(String ext) {
		HashMap map = new HashMap();
		if(getImportFrom().equalsIgnoreCase("file"))
		{
		String path = metadataFile_JTextField.getText();
		String reportFileLocation = path.substring(0, path.lastIndexOf('\\')+1);
		String reportFileName = path.substring(path.lastIndexOf('\\')+1, path.length());		

		map.put("File_Name", reportFileLocation);
		map.put("Table_Name", reportFileName);
		map.put("extension", ext);
		}
		else if(getImportFrom().equalsIgnoreCase("ftp"))
		{
			map.put("File_Name", ftpDownloadedMetadataPath.substring(0, ftpDownloadedMetadataPath.lastIndexOf('\\')+1));
			map.put("Table_Name", ftpDownloadedMetadataPath.substring(ftpDownloadedMetadataPath.lastIndexOf('\\')+1, ftpDownloadedMetadataPath.length()-4));
			map.put("extension", ext);
		}
		getMainUIControl().setMetadataReader(MetadataProcessorFactory.getMetadataReader(map));
	}

	@Override
	public void postInilize() {
		getIELogger().writeLog("passing PostInilize", IELogger.DEBUG);
		// The method is updated by Harsh for implementation of FTP functionality on 12/26/2011
		
		if(getImportFrom().equalsIgnoreCase("file"))
		{
			getImportUIInfoBean().setObjectType((String) objectType_JComboBox.getSelectedItem());
			getImportUIInfoBean().setMetadataInputFile(metadataFile_JTextField.getText());
			getImportUIInfoBean().setWorksheet((String) worksheet_JComboBox.getSelectedItem());
			getImportUIInfoBean().setExtension(getExtension());
			getImportUIInfoBean().setUpdateExisting(updateExisting_JCheckBox.isSelected());
			getImportUIInfoBean().setImportFromLocation("file");
		}
		else if(getImportFrom().equalsIgnoreCase("ftp"))
		{
			getImportUIInfoBean().setObjectType((String) objectType_JComboBox.getSelectedItem());
			getImportUIInfoBean().setMetadataInputFile(ftpDownloadedMetadataPath);
			getImportUIInfoBean().setWorksheet((String) worksheet_JTextField.getText());
			getImportUIInfoBean().setExtension(getExtension());
			getImportUIInfoBean().setUpdateExisting(updateExisting_JCheckBox.isSelected());
			getImportUIInfoBean().setFtpURL(ftpUrl_JTextField.getText());
			getImportUIInfoBean().setFtpPassword(new String(password_JPasswordField.getPassword()));
			getImportUIInfoBean().setFtpUserName(user_JTextField.getText());
			getImportUIInfoBean().setFtpMetadataFile(metadata_JTextField.getText());
			getImportUIInfoBean().setFtpWorksheet(worksheet_JTextField.getText());
			System.out.println("worksheet_JTextField.getText()"+worksheet_JTextField.getText());
			getImportUIInfoBean().setImportFromLocation("ftp");
		}
	
		
		// Below attributes are added by harsh for the FTP functionality on 26/12/2011.
		
		
		getIELogger().writeLog("End of PostInilize", IELogger.DEBUG);
	}

	private ImportUIInfoBean getImportUIInfoBean(){
		return (ImportUIInfoBean)getUiInfoBean();
	}



	@Override
	public StringBuffer validateUIInputs() {
		// The below validations have been modified by Harsh for the FTP functionality on 12/22/2011
		
		getIELogger().writeLog("passing validateUIInputs", IELogger.DEBUG);

		String objecttype = (String) objectType_JComboBox.getSelectedItem();
		String metadataFile = metadataFile_JTextField.getText();
		String worksheet = (String) worksheet_JComboBox.getSelectedItem();
		// Below 3 variables are added by Harsh for implementing the FTP functionality
		String ftpUrlStr = ftpUrl_JTextField.getText();
		String userNameStr = user_JTextField.getText();
		String passwordStr = new String(password_JPasswordField.getPassword());
		String metadataStr = metadata_JTextField.getText();
		String worksheetStr = worksheet_JTextField.getText();
		errorMessage = null;
		
			 
		
		if(objecttype == null || "".equalsIgnoreCase(objecttype.trim())){
			if(errorMessage == null){
				errorMessage  = new StringBuffer();
				errorMessage.append("Please Enter ");
			}

			errorMessage.append("Object Type");
		}
		else if(fileSystem_JRadioButton!=null && fileSystem_JRadioButton.isSelected())
		{
			System.out.println("File System Radio button Validation");
			if(metadataFile == null || "".equalsIgnoreCase(metadataFile.trim())){
				if(errorMessage == null){
					errorMessage  = new StringBuffer();
					errorMessage.append("Please Enter ");
				}
				else
					errorMessage.append(", ");

				errorMessage.append("Metadata File");

			}
			if(getExtension().equalsIgnoreCase("xls")){
				if(worksheet == null || "".equalsIgnoreCase(worksheet.trim())){
					if(errorMessage == null){
						errorMessage  = new StringBuffer();
						errorMessage.append("Please Enter ");
					}
					else{
						errorMessage.append(", ");
					}
					errorMessage.append("WorkSheet");
				}
			}
		}
		else if(ftp_JRadioButton!=null && ftp_JRadioButton.isSelected())
		{
			System.out.println("FTP Radio button Validation");
			if(ftpUrlStr == null || "".equalsIgnoreCase(ftpUrlStr.trim()))
			{
				if(errorMessage == null){
					errorMessage  = new StringBuffer();
					errorMessage.append("Please Enter ");
				}
				else{
					errorMessage.append(", ");
				}
				errorMessage.append("Ftp Url");
				ftpUrl_JTextField.requestFocus();
			}
			else if(userNameStr == null || "".equalsIgnoreCase(userNameStr.trim()))
			{
				if(errorMessage == null){
					errorMessage  = new StringBuffer();
					errorMessage.append("Please Enter ");
				}
				else{
					errorMessage.append(", ");
				}
				errorMessage.append("User Name");
				user_JTextField.requestFocus();
			}
			//passwordStr
			else if(passwordStr == null || "".equalsIgnoreCase(passwordStr.trim()))
			{
				if(errorMessage == null){
					errorMessage  = new StringBuffer();
					errorMessage.append("Please Enter ");
				}
				else{
					errorMessage.append(", ");
				}
				errorMessage.append("Password");
				password_JPasswordField.requestFocus();
			}
			else if(metadataStr == null || "".equalsIgnoreCase(metadataStr.trim()))
			{
				if(errorMessage == null){
					errorMessage  = new StringBuffer();
					errorMessage.append("Please Enter ");
				}
				else{
					errorMessage.append(", ");
				}
				errorMessage.append("Metadata file location");
				metadata_JTextField.requestFocus();
			}
			else if(worksheetStr == null || "".equalsIgnoreCase(worksheetStr.trim()))
			{
				if(errorMessage == null){
					errorMessage  = new StringBuffer();
					errorMessage.append("Please Enter ");
				}
				else{
					errorMessage.append(", ");
				}
				errorMessage.append("Worksheet name");
				worksheet_JTextField.requestFocus();
			}
			else{
				if(ftpUrlStr.startsWith("sftp"))
				{
					com.daffodil.documentumie.filehandler.SFTPFileHandler ffh = null;
					ftpUrlStr =  ftpUrlStr.substring(ftpUrlStr.indexOf(":")+3,ftpUrlStr.length());
					ffh = new com.daffodil.documentumie.filehandler.SFTPFileHandler(ftpUrlStr, userNameStr, passwordStr);
					try{								
						boolean connectStatus = ffh.connect();				
						if(connectStatus)
						{
							try {
								String localFilePath = ffh.getFile(metadataStr);
								ftpDownloadedMetadataPath = localFilePath.toString();
								boolean metaStatus = checkFileExistance(localFilePath);
								if(!metaStatus)
								{
									errorMessage  = new StringBuffer();
									errorMessage.append("Metadata download Not successful");
								}
							} catch (Exception e) {
								errorMessage  = new StringBuffer();
								errorMessage.append(e.getMessage());
							}
						}
					}catch(Exception e)
					{
						errorMessage  = new StringBuffer();
						errorMessage.append(e.getMessage());
					}
				}
				else if(ftpUrlStr.startsWith("ftp"))
				{
					com.daffodil.documentumie.filehandler.FTPFileHandler ffh = null;
					ftpUrlStr =  ftpUrlStr.substring(ftpUrlStr.indexOf(":")+3,ftpUrlStr.length());
					ffh = new com.daffodil.documentumie.filehandler.FTPFileHandler(ftpUrlStr, userNameStr, passwordStr);
					try{								
						boolean connectStatus = ffh.connect();				
						if(connectStatus)
						{
							try {
								String localFilePath = ffh.getFile(metadataStr);
								ftpDownloadedMetadataPath = localFilePath.toString();
								boolean metaStatus = checkFileExistance(localFilePath);
								if(!metaStatus)
								{
									errorMessage  = new StringBuffer();
									errorMessage.append("Metadata download Not successful");
								}
							} catch (Exception e) {
								errorMessage  = new StringBuffer();
								errorMessage.append(e.getMessage());
							}
						}
					}catch(Exception e)
					{
						errorMessage  = new StringBuffer();
						errorMessage.append(e.getMessage());
					}
				}
				/*com.daffodil.documentumie.filehandler.SFTPFileHandler ffh = new com.daffodil.documentumie.filehandler.SFTPFileHandler(ftpUrlStr, userNameStr, passwordStr);
				try{								
				boolean connectStatus = ffh.connect();				
				 //String status = getConnectionStatus(userNameStr,passwordStr,ftpUrlStr);
				 if(connectStatus)
				 {
					 try {
						 String localFilePath = ffh.getFile(metadataStr);
						 ftpDownloadedMetadataPath = localFilePath;
						 boolean metaStatus = checkFileExistance(localFilePath);
						if(!metaStatus)
						{
							errorMessage  = new StringBuffer();
							 errorMessage.append("Metadata download Not successful");
						}
					} catch (Exception e) {
						errorMessage  = new StringBuffer();
						 errorMessage.append(e.getMessage());
					}
				 }
				}catch(Exception e)
				{
					errorMessage  = new StringBuffer();
					 errorMessage.append(e.getMessage());
				}*/
				 /*else{
					 errorMessage  = new StringBuffer();
					 errorMessage.append(status);
				 }*/
				 //System.out.println("FTP status "+status);
			}
		}
		else{
			if(errorMessage == null){
				errorMessage  = new StringBuffer();
				errorMessage.append("Please Select From where you want to Import");
			}
			else{
				errorMessage  = new StringBuffer();
				errorMessage.append("Please Select From where you want to Import ");
			}

			//errorMessage.append("From where you want to Import");
		}
		getIELogger().writeLog("Ending validateUIInputs", IELogger.DEBUG);
		return errorMessage;
		}
	

		@Override
		public String getImageLocation() {
			if(imageLocation==null){
				imageLocation = "/com/daffodil/documentumie/iebusiness/pimport/view/images/heading_i_source.jpg";
			}
			return imageLocation;
		}


		@Override
		public String getShowMessage() {
			if(showMessage==null){
				showMessage = "Select the object type, metadata file and sheet to import the " + "\n" + "content.";
			}
			return showMessage;
		}

		// <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
		private void initComponents() {
			java.awt.GridBagConstraints gridBagConstraints;

			objectType_JLabel = new javax.swing.JLabel();
			metadataFile_JLabel = new javax.swing.JLabel();
			worksheet_JLabel = new javax.swing.JLabel();
			objectType_JComboBox = new javax.swing.JComboBox();
			worksheet_JComboBox = new javax.swing.JComboBox();
			metadataFile_jPanel = new javax.swing.JPanel();
			metadataFile_JTextField = new javax.swing.JFormattedTextField();
			metadatatFileBrowse_JButton = new DButton(){
				protected int getButtonWidth() {
					return 20;
				}
			};
			inputType_JPanel = new javax.swing.JPanel();
			inputType_JLabel = new javax.swing.JLabel();
			csv_JRadioButton = new javax.swing.JRadioButton();
			xls_JRadioButton = new javax.swing.JRadioButton();
			xml_JRadioButton = new javax.swing.JRadioButton();
			updateExisting_JLabel = new JLabel();
			updateExisting_JCheckBox = new JCheckBox();
			// Below  objects are added for FTP functionality by Harsh
			
			fileSystem_JRadioButton = new javax.swing.JRadioButton();
			ftp_JRadioButton = new javax.swing.JRadioButton();
			importFrom_JPanel = new javax.swing.JPanel();
			importFrom_JLabel = new javax.swing.JLabel();
			ftpUrl_JLabel = new javax.swing.JLabel();
			ftpUrl_JTextField = new javax.swing.JTextField();
			user_JLabel = new javax.swing.JLabel();
			user_JTextField = new javax.swing.JTextField();
			password_JLabel = new javax.swing.JLabel();
			password_JPasswordField = new javax.swing.JPasswordField();
			metadataFileFTP_JLabel = new javax.swing.JLabel();
			metadata_JTextField = new javax.swing.JTextField();
			worksheetFTP_JLabel = new javax.swing.JLabel();
			worksheet_JTextField = new javax.swing.JTextField();
			
			// Above objects are added for FTP functionality by Harsh on 12/21/2011
			setPreferredSize(new java.awt.Dimension(370, 400));
			setLayout(new java.awt.GridBagLayout());

			inputType_JLabel.setText("Input Type");
			inputType_JLabel.setPreferredSize(new java.awt.Dimension(80, 20));
			gridBagConstraints = new java.awt.GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 1;
			gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
			add(inputType_JLabel, gridBagConstraints);
			
			// Added for the FTP functionality by Harsh on 12/20/2011 
			importFrom_JLabel.setText("Import From");
			importFrom_JLabel.setPreferredSize(new java.awt.Dimension(80,20));
			gridBagConstraints = new java.awt.GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 2;
			gridBagConstraints.insets = new java.awt.Insets(5, 4, 0, 0);
			add(importFrom_JLabel, gridBagConstraints);
			// Above code is Added for the FTP functionality by Harsh on 12/20/2011 

			inputType_JPanel.setPreferredSize(new java.awt.Dimension(179, 20));
			inputType_JPanel.setLayout(new java.awt.GridBagLayout());
			inputType_JPanel.setOpaque(false);

			csv_JRadioButton.setOpaque(false);
			csv_JRadioButton.setText("CSV");
			// csv_JRadioButton.setActionCommand("CSV");
			csv_JRadioButton.setPreferredSize(new java.awt.Dimension(50, 20));
			gridBagConstraints = new java.awt.GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 5);
			inputType_JPanel.add(csv_JRadioButton, gridBagConstraints);

			xls_JRadioButton.setOpaque(false);
			xls_JRadioButton.setText("Excel");
			xls_JRadioButton.setPreferredSize(new java.awt.Dimension(50, 20));
			/*xls_JRadioButton.addActionListener(new java.awt.event.ActionListener() {
	            public void actionPerformed(java.awt.event.ActionEvent evt) {
	                xls_JRadioButtonActionPerformed(evt);
	            }
	        });*/
			gridBagConstraints = new java.awt.GridBagConstraints();
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.insets = new java.awt.Insets(0,10, 0, 5);
			inputType_JPanel.add(xls_JRadioButton, gridBagConstraints);

			xml_JRadioButton.setText("XML");
			xml_JRadioButton.setOpaque(false);
			xml_JRadioButton.setPreferredSize(new java.awt.Dimension(50, 20));
			gridBagConstraints = new java.awt.GridBagConstraints();
			gridBagConstraints.gridx = 2;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.insets = new java.awt.Insets(0,10, 0, 5);
			inputType_JPanel.add(xml_JRadioButton, gridBagConstraints);

			gridBagConstraints = new java.awt.GridBagConstraints();
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 1;
			gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 20);
			add(inputType_JPanel, gridBagConstraints);
			
			/*
			 * Below code is added by Harsh for FTP implementation on 12/21/2011
			 */

			importFrom_JPanel.setPreferredSize(new java.awt.Dimension(200, 20));
			importFrom_JPanel.setLayout(new java.awt.GridBagLayout());
			importFrom_JPanel.setOpaque(false);

			fileSystem_JRadioButton.setOpaque(false);
			fileSystem_JRadioButton.setText("File System");
			fileSystem_JRadioButton.setPreferredSize(new java.awt.Dimension(50, 20));
			gridBagConstraints = new java.awt.GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.insets = new java.awt.Insets(0, -20, 15, 5);
			importFrom_JPanel.add(fileSystem_JRadioButton, gridBagConstraints);

			ftp_JRadioButton.setOpaque(false);
			ftp_JRadioButton.setText("FTP");
			ftp_JRadioButton.setPreferredSize(new java.awt.Dimension(50, 20));

			gridBagConstraints = new java.awt.GridBagConstraints();
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.insets = new java.awt.Insets(0,10, 0, 5);
			importFrom_JPanel.add(ftp_JRadioButton, gridBagConstraints);

	
			gridBagConstraints = new java.awt.GridBagConstraints();
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 2;
			gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 20);
			add(importFrom_JPanel, gridBagConstraints);
			
			// Above code is added by Harsh for FTP implementation

			objectType_JLabel.setText("Object Type");
			objectType_JLabel.setPreferredSize(new java.awt.Dimension(80, 20));
			gridBagConstraints = new java.awt.GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
			add(objectType_JLabel, gridBagConstraints);

			metadataFile_JLabel.setText("Metadata File");
			metadataFile_JLabel.setPreferredSize(new java.awt.Dimension(80, 20));
			gridBagConstraints = new java.awt.GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 3;
			gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
			add(metadataFile_JLabel, gridBagConstraints);

			worksheet_JLabel.setText("Work Sheet");
			worksheet_JLabel.setPreferredSize(new java.awt.Dimension(80, 20));
			gridBagConstraints = new java.awt.GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 4;
			gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
			add(worksheet_JLabel, gridBagConstraints);

			objectType_JComboBox.setBackground(new java.awt.Color(255, 255, 255));
			objectType_JComboBox.setPreferredSize(new java.awt.Dimension(180, 20));


			gridBagConstraints = new java.awt.GridBagConstraints();
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
			add(objectType_JComboBox, gridBagConstraints);

			worksheet_JComboBox.setBackground(new java.awt.Color(255, 255, 255));
			worksheet_JComboBox.setPreferredSize(new java.awt.Dimension(180, 20));
			gridBagConstraints = new java.awt.GridBagConstraints();
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 4;
			gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
			add(worksheet_JComboBox, gridBagConstraints);

			metadataFile_jPanel.setOpaque(false);
			metadataFile_jPanel.setPreferredSize(new java.awt.Dimension(180, 21));
			metadataFile_jPanel.setLayout(new java.awt.GridBagLayout());

			metadataFile_JTextField.setPreferredSize(new java.awt.Dimension(164, 20));
			gridBagConstraints = new java.awt.GridBagConstraints();
			metadataFile_JTextField.setEditable(false);
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			metadataFile_jPanel.add(metadataFile_JTextField, gridBagConstraints);

			metadatatFileBrowse_JButton.setText("...");
			metadatatFileBrowse_JButton.setPreferredSize(new java.awt.Dimension(15, 20));
			gridBagConstraints = new java.awt.GridBagConstraints();
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 0;
			metadataFile_jPanel.add(metadatatFileBrowse_JButton, gridBagConstraints);

			gridBagConstraints = new java.awt.GridBagConstraints();
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 3;
			gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
			add(metadataFile_jPanel, gridBagConstraints);

			updateExisting_JLabel.setText("Update Existing");
			updateExisting_JLabel.setPreferredSize(new java.awt.Dimension(90, 20));
			gridBagConstraints = new java.awt.GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 5;
			add(updateExisting_JLabel, gridBagConstraints);

			updateExisting_JCheckBox.setOpaque(false);
			updateExisting_JCheckBox.setPreferredSize(new java.awt.Dimension(50, 20));
			gridBagConstraints = new java.awt.GridBagConstraints();
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 5;
			gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints.insets = new java.awt.Insets(0,22, 0, 0);
			add(updateExisting_JCheckBox, gridBagConstraints);
			
			// Below code is added by Harsh for the FTP functionality on 12/21/2011
			
			ftpUrl_JLabel.setText("Ftp Url");
			ftpUrl_JLabel.setPreferredSize(new java.awt.Dimension(85,20));
			gridBagConstraints = new java.awt.GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 6;
			gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
			add(ftpUrl_JLabel, gridBagConstraints);
			ftpUrl_JLabel.setVisible(false);
			
			ftpUrl_JTextField.setOpaque(false);
			ftpUrl_JTextField.setPreferredSize(new java.awt.Dimension(180,20));
			gridBagConstraints = new java.awt.GridBagConstraints();
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 6;
			gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
			add(ftpUrl_JTextField, gridBagConstraints);
			ftpUrl_JTextField.setVisible(false);
			
			user_JLabel.setText("User Name");
			user_JLabel.setPreferredSize(new java.awt.Dimension(85,20));
			gridBagConstraints = new java.awt.GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 7;
			gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
			add(user_JLabel, gridBagConstraints);
			user_JLabel.setVisible(false);
			
			user_JTextField.setOpaque(false);
			user_JTextField.setPreferredSize(new java.awt.Dimension(180,20));
			gridBagConstraints = new java.awt.GridBagConstraints();
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 7;
			gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
			add(user_JTextField, gridBagConstraints);
			user_JTextField.setVisible(false);
			
			password_JLabel.setText("Password");
			password_JLabel.setPreferredSize(new java.awt.Dimension(85,20));
			gridBagConstraints = new java.awt.GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 8;
			gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
			add(password_JLabel, gridBagConstraints);
			password_JLabel.setVisible(false);
			
			password_JPasswordField.setOpaque(false);
			password_JPasswordField.setPreferredSize(new java.awt.Dimension(180,20));
			gridBagConstraints = new java.awt.GridBagConstraints();
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 8;
			gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
			add(password_JPasswordField, gridBagConstraints);
			password_JPasswordField.setVisible(false);
			
			metadataFileFTP_JLabel.setText("Metadata File");
			metadataFileFTP_JLabel.setPreferredSize(new java.awt.Dimension(85,20));
			gridBagConstraints = new java.awt.GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 9;
			gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
			add(metadataFileFTP_JLabel, gridBagConstraints);
			metadataFileFTP_JLabel.setVisible(false);
			
			metadata_JTextField.setOpaque(false);
			metadata_JTextField.setPreferredSize(new java.awt.Dimension(180,20));
			gridBagConstraints = new java.awt.GridBagConstraints();
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 9;
			gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
			add(metadata_JTextField, gridBagConstraints);
			metadata_JTextField.setVisible(false);
			
			worksheetFTP_JLabel.setText("Worksheet");
			worksheetFTP_JLabel.setPreferredSize(new java.awt.Dimension(85,20));
			gridBagConstraints = new java.awt.GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 10;
			gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
			add(worksheetFTP_JLabel, gridBagConstraints);
			worksheetFTP_JLabel.setVisible(false);
			
			worksheet_JTextField.setOpaque(false);
			worksheet_JTextField.setPreferredSize(new java.awt.Dimension(180,20));
			gridBagConstraints = new java.awt.GridBagConstraints();
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 10;
			gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
			add(worksheet_JTextField, gridBagConstraints);
			worksheet_JTextField.setVisible(false);
			
			// Above code is added by Harsh for the FTP functionality on 12/21/2011
			
		}// </editor-fold>//GEN-END:initComponents

		// Variables declaration - do not modify//GEN-BEGIN:variables
		private javax.swing.JFormattedTextField metadataFile_JTextField;
		private javax.swing.JLabel metadataFile_JLabel;
		private javax.swing.JPanel metadataFile_jPanel;
		private DButton metadatatFileBrowse_JButton;
		private javax.swing.JComboBox objectType_JComboBox;
		private javax.swing.JLabel objectType_JLabel;
		private javax.swing.JComboBox worksheet_JComboBox;
		private javax.swing.JLabel worksheet_JLabel;
		private javax.swing.JRadioButton xls_JRadioButton;
		private javax.swing.JRadioButton xml_JRadioButton;
		private JRadioButton csv_JRadioButton;
		private JPanel inputType_JPanel;
		private JLabel inputType_JLabel;
		private JLabel updateExisting_JLabel;
		private JCheckBox updateExisting_JCheckBox;
		
		
		// End of variables declaration//GEN-END:variables
		
		// Below variable are added by Harsh for implementation of the FTP functionality
		private javax.swing.JRadioButton fileSystem_JRadioButton;
		private javax.swing.JRadioButton ftp_JRadioButton;
		private javax.swing.JLabel importFrom_JLabel;
		private JPanel importFrom_JPanel;
		private javax.swing.JLabel ftpUrl_JLabel;
		private javax.swing.JTextField ftpUrl_JTextField;
		private javax.swing.JLabel user_JLabel;
		private javax.swing.JTextField user_JTextField;
		private javax.swing.JLabel password_JLabel;
		private javax.swing.JPasswordField password_JPasswordField;
		private javax.swing.JLabel metadataFileFTP_JLabel;
		private javax.swing.JTextField metadata_JTextField;
		private javax.swing.JLabel worksheetFTP_JLabel;
		private javax.swing.JTextField worksheet_JTextField;
		
		// FTP functionality variables end Harsh.

		@Override
		protected StringBuffer errorMessage() {
			return errorMessage;
		}

		
		
		
		// TODO move this code before import 
		/*public Object[] validInputFileHeader(List inputAttrList, String type){
			int objectType;
			try {
				objectType = getCSServiceProvider().validInputHeader(type);
				boolean boolVal = false;
				String msg = null;
				if (objectType == ImportConstant.SUPER_THAN_SYSOBJECT) {
					boolVal = true;
				} else if (objectType == ImportConstant.DM_FOLDER) {

					if ((inputAttrList.contains("r_folder_path"))
							&& (inputAttrList.contains("object_name"))) {
						boolVal = true;
					} else {
						boolVal = false;
						msg = "r_folder_path and object_name column must be there in input metadata excel file for object Type dm_folder";
					}
				} else if (objectType == ImportConstant.SYSOBJECT_OR_CHILD) {
					boolean updateExisting = updateExisting_JCheckBox.isSelected();
					if ((inputAttrList.contains("r_folder_path"))
							&& (inputAttrList.contains("file_source_location__"))
							&& (inputAttrList.contains("object_name"))){
						if(updateExisting){
							boolVal = true;
						}else{
							if((inputAttrList.contains("is_minor__"))){
								boolVal = true;
							}else{
								boolVal = false;
								msg = "is_minor__ attribute is mandatory attribute. This attribute is not exists in input metadata excel file.";
							}
						}
					}else{if((inputAttrList.contains("is_minor__"))){
						boolVal = false;
						msg = "object_name, r_folder_path and file_source_location__ attribute are mandatory attributes. These attributes does not exists in input metadata excel file.";
					}else{
						boolVal = false;
						msg = "object_name, r_folder_path , file_source_location__ attribute and is_minor__ are mandatory attributes. These attributes does not exists in input metadata excel file.";
					}
					}
					//return new Object[] { boolVal, objectType, msg };
				}
				return new Object[] { boolVal, objectType, msg };
			} catch (DDfException e) {
				String msg = e.getMessage();
				getIEMessageUtility().showMessageDialog(e.getMessage(), e.getCause());
			}
			return null;

		}*/

	}
