/*
 * EDestinationInfoUIControl.java
 *
 * Created on 09 July 2008, 13:16
 */

package com.daffodil.documentumie.iebusiness.export.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.net.SocketException;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;

import org.apache.commons.net.ftp.FTPClient;

import com.daffodil.documentumie.iebusiness.AbstractUIControl;
import com.daffodil.documentumie.iebusiness.DButton;
import com.daffodil.documentumie.iebusiness.IEUtility;
import com.daffodil.documentumie.iebusiness.export.bean.ExportUIInfoBean;

/**
 *
 * @author  Administrator
 */
public class EDestinationInfoUIControl extends AbstractUIControl{

	/**
	 * 
	 */

	private String showMessage;
	private String imageLocation;
	private StringBuffer errorMessage;
	private  FTPClient ftp;
	private static final long serialVersionUID = 1L;
	String exportTo;// Variable added by Harsh for implementing FTP functionality on 12/23/2011
	/** Creates new form EDestinationInfoUIControl */
	public EDestinationInfoUIControl() {
		
		initComponents();
		initUI() ;// This method has been called by Harsh for implementation of the FTP functionality.
		installLocalComponentListener();
	}
	
	// The initUI() method is overridden for the FTP functionality by Harsh on 12/23/2011
	@Override
	protected void initUI() {
		super.initUI();
		ButtonGroup buttonGroupFTP;
		buttonGroupFTP = new ButtonGroup();
		buttonGroupFTP.add(fileSystem_JRadioButton);
		buttonGroupFTP.add(ftp_JRadioButton);
	}
	
	public String getExportTo() {
		return exportTo;
	}

	public void setExportTo(String exportTo) {
		this.exportTo = exportTo;
	}

	private void installLocalComponentListener() {

		metadataFile_JButton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				String metadataPath = metadataFile_JTextField.getText();
				String path = IEUtility.showDirectoryChooser(EDestinationInfoUIControl.this);
				if ("".equalsIgnoreCase(path)) {
					metadataFile_JTextField.setText(metadataPath);
				}else{
				metadataFile_JTextField.setText(path);}
			}

		});

		outputFile_JButton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				String outputPath = outputFile_JTextField.getText();
				String path = IEUtility.showDirectoryChooser(EDestinationInfoUIControl.this);
				if ("".equalsIgnoreCase(path)) {
					outputFile_JTextField.setText(outputPath);
				}else{
					outputFile_JTextField.setText(path);
				}
			}
		});

		metadataFile_JTextField.addPropertyChangeListener("value",
				new PropertyChangeListener() {

			public void propertyChange(PropertyChangeEvent arg0) {
				String path = (String) metadataFile_JTextField.getText();
				if(/*path != null || */!"".equalsIgnoreCase(path)){
						boolean bol = checkExistenceOfDirectory(path);
						if(!bol){if(errorMessage == null){
							errorMessage = new StringBuffer();
							errorMessage.append("Metadata Direcotry does not existed.");
							getIEMessageUtility().showMessageDialog("Metadata Direcotry does not existed.", null);
						}
						}else{
							errorMessage = null;
						}
					}
				}
			});

		outputFile_JTextField.addPropertyChangeListener("value",
				new PropertyChangeListener() {

			public void propertyChange(PropertyChangeEvent arg0) {
				String path = outputFile_JTextField.getText();
					if(/*path != null ||*/ !"".equalsIgnoreCase(path)){
				boolean bol = checkExistenceOfDirectory(path);
				if(!bol){
					if(errorMessage == null){
						errorMessage = new StringBuffer();
						errorMessage.append("Output Direcotry does not existed.");
						getIEMessageUtility().showMessageDialog("Output Direcotry does not existed.", null);
					}
				}else{
					errorMessage = null;
				}
					}
			}
		});
		
		// The below Listeners are implemented by Harsh for the implementation of the FTP functionality on 12/23/2011
		ftp_JRadioButton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {				
				System.out.println("FTP checked in Export");
				errorMessage = null;
				setExportTo("ftp");
				metadataFile_JLabel.setVisible(false);
				metadataFile_JButton.setVisible(false);
				metadataFile_JTextField.setVisible(false);
				outputFile_JButton.setVisible(false);
				outputFile_JLabel.setVisible(false);
				outputFile_JTextField.setVisible(false);
				ftpUrl_JLabel.setVisible(true);
				ftpUrl_JTextField.setVisible(true);
				user_JLabel.setVisible(true);
				user_JTextField.setVisible(true);
				password_JLabel.setVisible(true);
				password_JPasswordField.setVisible(true);
				metadataFTP_JLabel.setVisible(true);
				metadataFTP_JTextField.setVisible(true);
				outputFTP_JLabel.setVisible(true);
				outputFTP_JTextField.setVisible(true);
				
			}
		});
		
		fileSystem_JRadioButton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {				
				System.out.println("File System checked in Export");
				errorMessage = null;
				setExportTo("file");
				metadataFile_JLabel.setVisible(true);
				metadataFile_JButton.setVisible(true);
				metadataFile_JTextField.setVisible(true);
				outputFile_JButton.setVisible(true);
				outputFile_JLabel.setVisible(true);
				outputFile_JTextField.setVisible(true);
				ftpUrl_JLabel.setVisible(false);
				ftpUrl_JTextField.setVisible(false);
				ftpUrl_JTextField.setText("");
				user_JLabel.setVisible(false);
				user_JTextField.setVisible(false);
				user_JTextField.setText("");
				password_JLabel.setVisible(false);
				password_JPasswordField.setVisible(false);
				password_JPasswordField.setText("");
				metadataFTP_JLabel.setVisible(false);
				metadataFTP_JTextField.setVisible(false);
				metadataFTP_JTextField.setText("");
				outputFTP_JLabel.setVisible(false);
				outputFTP_JTextField.setVisible(false);
				outputFTP_JTextField.setText("");
			}
		});
		//The above Listeners are implemented by Harsh for the implementation of the FTP functionality on 12/23/2011
		
		}

		private boolean checkExistenceOfDirectory(String path) {
			String outputLocation = path;
			File file = new File(outputLocation);
			if(file.isDirectory())
			{
				return true;
			}else
				return false;
		}

		/** This method is called from within the constructor to
		 * initialize the form.
		 * WARNING: Do NOT modify this code. The content of this method is
		 * always regenerated by the Form Editor.
		 */
		// <editor-fold defaultstate="collapsed" desc="Generated Code">                          
		private void initComponents() {
			//Below Button Group is Added by Harsh for FTP functionality on 12/23/2011
	
			exportTo_JPanel = new javax.swing.JPanel();
			exportTo_JLabel = new javax.swing.JLabel();
			fileSystem_JRadioButton = new javax.swing.JRadioButton();
			ftp_JRadioButton = new javax.swing.JRadioButton();
			ftpUrl_JLabel = new javax.swing.JLabel();
			ftpUrl_JTextField = new javax.swing.JTextField();
			user_JLabel = new javax.swing.JLabel();
			user_JTextField = new javax.swing.JTextField();
			password_JLabel = new javax.swing.JLabel();
			password_JPasswordField = new javax.swing.JPasswordField();
			metadataFTP_JLabel = new javax.swing.JLabel();
			metadataFTP_JTextField = new javax.swing.JTextField();
			outputFTP_JLabel = new javax.swing.JLabel();
			outputFTP_JTextField = new javax.swing.JTextField();
			// Above Button Group is added by Harsh for FTP functionality on 12/23/2011
			java.awt.GridBagConstraints gridBagConstraints;

			metadataFile_JLabel = new javax.swing.JLabel();
			metadataFile_JTextField = new javax.swing.JFormattedTextField();
			
			metadataFile_JButton = new DButton(){
				protected int getButtonWidth() {
					return 20;
				}
			};
			outputFile_JLabel = new javax.swing.JLabel();
			outputFile_JTextField = new javax.swing.JFormattedTextField();
			outputFile_JButton = new DButton(){
				protected int getButtonWidth() {
					return 20;
				}
			};
			setPreferredSize(new java.awt.Dimension(330, 355));
			setRequestFocusEnabled(false);
			setLayout(new java.awt.GridBagLayout());
			setOpaque(false);
			
			// Added for the FTP functionality by Harsh on 12/20/2011 
				exportTo_JLabel.setText("Export To");
				exportTo_JLabel.setPreferredSize(new java.awt.Dimension(80,20));
				gridBagConstraints = new java.awt.GridBagConstraints();
				gridBagConstraints.gridx = 0;
				gridBagConstraints.gridy = 0;
				gridBagConstraints.insets = new java.awt.Insets(5, 4, 0, 0);
				add(exportTo_JLabel, gridBagConstraints);
				
				exportTo_JPanel.setPreferredSize(new java.awt.Dimension(200, 20));
				exportTo_JPanel.setLayout(new java.awt.GridBagLayout());
				exportTo_JPanel.setOpaque(false);

				fileSystem_JRadioButton.setOpaque(false);
				fileSystem_JRadioButton.setText("File System");
				fileSystem_JRadioButton.setPreferredSize(new java.awt.Dimension(50, 20));
				gridBagConstraints = new java.awt.GridBagConstraints();
				gridBagConstraints.gridx = 0;
				gridBagConstraints.gridy = 0;
				gridBagConstraints.insets = new java.awt.Insets(0, -20, 15, 5);
				exportTo_JPanel.add(fileSystem_JRadioButton, gridBagConstraints);

				ftp_JRadioButton.setOpaque(false);
				ftp_JRadioButton.setText("FTP");
				ftp_JRadioButton.setPreferredSize(new java.awt.Dimension(50, 20));

				gridBagConstraints = new java.awt.GridBagConstraints();
				gridBagConstraints.gridx = 1;
				gridBagConstraints.gridy = 0;
				gridBagConstraints.insets = new java.awt.Insets(0,10, 0, 0);
				exportTo_JPanel.add(ftp_JRadioButton, gridBagConstraints);

		
				gridBagConstraints = new java.awt.GridBagConstraints();
				gridBagConstraints.gridx = 1;
				gridBagConstraints.gridy = 0;
				gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 20);
				add(exportTo_JPanel, gridBagConstraints);
			// Above code is Added for the FTP functionality by Harsh on 12/23/2011 
				
			metadataFile_JLabel.setText("Metadata File ");
			metadataFile_JLabel.setPreferredSize(new java.awt.Dimension(80, 20));
			gridBagConstraints = new java.awt.GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 1;
			gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
			add(metadataFile_JLabel, gridBagConstraints);

			metadataFile_JTextField.setPreferredSize(new java.awt.Dimension(165, 20));
			gridBagConstraints = new java.awt.GridBagConstraints();
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 1;
			gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
			add(metadataFile_JTextField, gridBagConstraints);

			metadataFile_JButton.setText("...");
			metadataFile_JButton.setPreferredSize(new java.awt.Dimension(15, 20));
			gridBagConstraints = new java.awt.GridBagConstraints();
			gridBagConstraints.gridx = 2;
			gridBagConstraints.gridy = 1;
			gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
			//gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
			add(metadataFile_JButton, gridBagConstraints);

			outputFile_JLabel.setText("Output File ");
			outputFile_JLabel.setPreferredSize(new java.awt.Dimension(80, 20));
			gridBagConstraints = new java.awt.GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 2;
			gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
			add(outputFile_JLabel, gridBagConstraints);

			outputFile_JTextField.setPreferredSize(new java.awt.Dimension(165, 20));
			gridBagConstraints = new java.awt.GridBagConstraints();
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 2;
			gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
			add(outputFile_JTextField, gridBagConstraints);

			outputFile_JButton.setText("...");
			outputFile_JButton.setPreferredSize(new java.awt.Dimension(15, 20));
			gridBagConstraints = new java.awt.GridBagConstraints();
			gridBagConstraints.gridx = 2;
			gridBagConstraints.gridy = 2;
			gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
			//gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
			add(outputFile_JButton, gridBagConstraints);
			
			//Below code is added by Harsh for implementation of the FTP functionality on 12/23/2011
			ftpUrl_JLabel.setText("Ftp Url");
			ftpUrl_JLabel.setPreferredSize(new java.awt.Dimension(85,20));
			gridBagConstraints = new java.awt.GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 3;
			gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
			add(ftpUrl_JLabel, gridBagConstraints);
			ftpUrl_JLabel.setVisible(false);
			
			ftpUrl_JTextField.setOpaque(false);
			ftpUrl_JTextField.setPreferredSize(new java.awt.Dimension(180,20));
			gridBagConstraints = new java.awt.GridBagConstraints();
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 3;
			gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
			add(ftpUrl_JTextField, gridBagConstraints);
			ftpUrl_JTextField.setVisible(false);
			
			user_JLabel.setText("User Name");
			user_JLabel.setPreferredSize(new java.awt.Dimension(85,20));
			gridBagConstraints = new java.awt.GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 4;
			gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
			add(user_JLabel, gridBagConstraints);
			user_JLabel.setVisible(false);
			
			user_JTextField.setOpaque(false);
			user_JTextField.setPreferredSize(new java.awt.Dimension(180,20));
			gridBagConstraints = new java.awt.GridBagConstraints();
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 4;
			gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
			add(user_JTextField, gridBagConstraints);
			user_JTextField.setVisible(false);
			
			password_JLabel.setText("Password");
			password_JLabel.setPreferredSize(new java.awt.Dimension(85,20));
			gridBagConstraints = new java.awt.GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 5;
			gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
			add(password_JLabel, gridBagConstraints);
			password_JLabel.setVisible(false);
			
			password_JPasswordField.setOpaque(false);
			password_JPasswordField.setPreferredSize(new java.awt.Dimension(180,20));
			gridBagConstraints = new java.awt.GridBagConstraints();
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 5;
			gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
			add(password_JPasswordField, gridBagConstraints);
			password_JPasswordField.setVisible(false);
			
			metadataFTP_JLabel.setText("Metadata File");
			metadataFTP_JLabel.setPreferredSize(new java.awt.Dimension(85,20));
			gridBagConstraints = new java.awt.GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 6;
			gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
			add(metadataFTP_JLabel, gridBagConstraints);
			metadataFTP_JLabel.setVisible(false);
			
			metadataFTP_JTextField.setOpaque(false);
			metadataFTP_JTextField.setPreferredSize(new java.awt.Dimension(180,20));
			gridBagConstraints = new java.awt.GridBagConstraints();
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 6;
			gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
			add(metadataFTP_JTextField, gridBagConstraints);
			metadataFTP_JTextField.setVisible(false);
			
			outputFTP_JLabel.setText("Output File");
			outputFTP_JLabel.setPreferredSize(new java.awt.Dimension(85,20));
			gridBagConstraints = new java.awt.GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 7;
			gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
			add(outputFTP_JLabel, gridBagConstraints);
			outputFTP_JLabel.setVisible(false);
			
			outputFTP_JTextField.setOpaque(false);
			outputFTP_JTextField.setPreferredSize(new java.awt.Dimension(180,20));
			gridBagConstraints = new java.awt.GridBagConstraints();
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 7;
			gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
			add(outputFTP_JTextField, gridBagConstraints);
			outputFTP_JTextField.setVisible(false);
			
			//Above code is added by Harsh for the implementation of the FTP functionality on 12/23/2011
			
		}// </editor-fold>                        


		// Variables declaration - do not modify                     
		private DButton metadataFile_JButton;
		private javax.swing.JLabel metadataFile_JLabel;
		private javax.swing.JFormattedTextField metadataFile_JTextField;
		private DButton outputFile_JButton;
		private javax.swing.JLabel outputFile_JLabel;
		private javax.swing.JFormattedTextField outputFile_JTextField;
		// End of variables declaration        
		// Below variable are added by Harsh for implementation of the FTP functionality on 12/23/2011
		private javax.swing.JRadioButton fileSystem_JRadioButton;
		private javax.swing.JRadioButton ftp_JRadioButton;
		private javax.swing.JLabel exportTo_JLabel;
		private JPanel exportTo_JPanel;
		private javax.swing.JLabel ftpUrl_JLabel;
		private javax.swing.JTextField ftpUrl_JTextField;
		private javax.swing.JLabel user_JLabel;
		private javax.swing.JTextField user_JTextField;
		private javax.swing.JLabel password_JLabel;
		private javax.swing.JPasswordField password_JPasswordField;
		private javax.swing.JLabel metadataFTP_JLabel;
		private javax.swing.JTextField metadataFTP_JTextField;
		private javax.swing.JLabel outputFTP_JLabel;
		private javax.swing.JTextField outputFTP_JTextField;

		private ExportUIInfoBean getExportUIInfoBean() {
			return (ExportUIInfoBean) getUiInfoBean();
		}


		@Override
		public String getImageLocation() {
			if(imageLocation==null){
				imageLocation = "/com/daffodil/documentumie/iebusiness/export/view/images/heading_e_choose_des.jpg";
			}
			return imageLocation;
		}

		@Override
		public String getShowMessage() {
			if(showMessage==null){
				showMessage = "Select the destination location.";
			}
			return showMessage;
		}

		@Override
		protected void postInilize() {
//			getIELogger().writeLog("passing Postinilize...EDestinationInfoUIControl", IELogger.DEBUG);
			if(getExportTo().equalsIgnoreCase("file"))
			{
				getExportUIInfoBean().setMetaDataFilePath(metadataFile_JTextField.getText());
				getExportUIInfoBean().setOutPutFile(outputFile_JTextField.getText());
				getExportUIInfoBean().setExportToLocation("file");
			}
			else if(getExportTo().equalsIgnoreCase("ftp"))
			{
				getExportUIInfoBean().setFtpMetadataFile(metadataFTP_JTextField.getText());
				getExportUIInfoBean().setFtpOutputFile(outputFTP_JTextField.getText());
				getExportUIInfoBean().setFtpPassword(new String(password_JPasswordField.getPassword()));
				getExportUIInfoBean().setFtpURL(ftpUrl_JTextField.getText());
				getExportUIInfoBean().setFtpUserName(user_JTextField.getText());
				getExportUIInfoBean().setExportToLocation("ftp");
			}
		}

		@Override
		protected void preInilize() {
//			getIELogger().writeLog("passing Prenilize...EDestinationInfoUIControl", IELogger.DEBUG);
			String metaDataFilePath = getExportUIInfoBean().getMetaDataFilePath();
			String outPutFilePath = getExportUIInfoBean().getOutPutFile();

			//Setting values into form.
			metadataFile_JTextField.setText(metaDataFilePath);
			outputFile_JTextField.setText(outPutFilePath);

		}

		@Override
		protected StringBuffer validateUIInputs() {
			StringBuffer destInfoErrorMsg = null;
			String metadata = metadataFile_JTextField.getText();
			String output = outputFile_JTextField.getText();
			// Below Validations are modified by Harsh for the implementation of the FTP functionality on 12/23/2011

			String ftpUrlStr = ftpUrl_JTextField.getText();
			String userNameStr = user_JTextField.getText();
			String passwordStr = new String(password_JPasswordField.getPassword());
			String metadataStr = metadataFTP_JTextField.getText();
			String outputStr = outputFTP_JTextField.getText();
			
			errorMessage = null;
			if(fileSystem_JRadioButton!=null && fileSystem_JRadioButton.isSelected())
			{
				if(metadata == null || "".equalsIgnoreCase(metadata)){
					if(destInfoErrorMsg == null){
						destInfoErrorMsg = new StringBuffer();
						destInfoErrorMsg.append("Please provide");
					}
					destInfoErrorMsg.append("Metadata File Path");
				}
	
	
				if(output == null || "".equalsIgnoreCase(output)){
					if(destInfoErrorMsg == null){
						destInfoErrorMsg = new StringBuffer();
						destInfoErrorMsg.append("Please provide");
					}
					destInfoErrorMsg.append("Output File Path");
				}
			}
			else if(ftp_JRadioButton!=null && ftp_JRadioButton.isSelected())
			{
				System.out.println("FTP Radio button Validation");
				if(ftpUrlStr == null || "".equalsIgnoreCase(ftpUrlStr.trim()))
				{
					if(destInfoErrorMsg == null){
						destInfoErrorMsg  = new StringBuffer();
						destInfoErrorMsg.append("Please Enter ");
					}
					
					destInfoErrorMsg.append("Ftp Url");
					ftpUrl_JTextField.requestFocus();
				}
				else if(userNameStr == null || "".equalsIgnoreCase(userNameStr.trim()))
				{
					if(destInfoErrorMsg == null){
						destInfoErrorMsg  = new StringBuffer();
						destInfoErrorMsg.append("Please Enter ");
					}
					
					destInfoErrorMsg.append("User Name");
					user_JTextField.requestFocus();
				}
				else if(passwordStr == null || "".equalsIgnoreCase(passwordStr.trim()))
				{
					if(destInfoErrorMsg == null){
						destInfoErrorMsg  = new StringBuffer();
						destInfoErrorMsg.append("Please Enter ");
					}
					
					destInfoErrorMsg.append("Password");
					password_JPasswordField.requestFocus();
				}
				else if(metadataStr == null || "".equalsIgnoreCase(metadataStr.trim()))
				{
					if(destInfoErrorMsg == null){
						destInfoErrorMsg  = new StringBuffer();
						destInfoErrorMsg.append("Please Specify ");
					}
					
					destInfoErrorMsg.append("the Metadata File Location");
					metadataFTP_JTextField.requestFocus();
				}
				else if(outputStr == null || "".equalsIgnoreCase(outputStr.trim()))
				{
					if(destInfoErrorMsg == null){
						destInfoErrorMsg  = new StringBuffer();
						destInfoErrorMsg.append("Please Specify ");
					}
					
					destInfoErrorMsg.append("the Output Location");
					outputFTP_JTextField.requestFocus();
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
								destInfoErrorMsg  = new StringBuffer();
								destInfoErrorMsg.append("Invalid Username or Password. Unable to connect.");
							}
						}catch(Exception e)
						{
							destInfoErrorMsg  = new StringBuffer();
							destInfoErrorMsg.append(e.getMessage());
						}
					}
					else if(ftpUrlStr.startsWith("ftp"))
					{
						com.daffodil.documentumie.filehandler.FTPFileHandler ffh = null;
						ftpUrlStr =  ftpUrlStr.substring(ftpUrlStr.indexOf(":")+3,ftpUrlStr.length());
						ffh = new com.daffodil.documentumie.filehandler.FTPFileHandler(ftpUrlStr, userNameStr, passwordStr);
						try{								
							boolean connectStatus = ffh.connect();				
							if(!connectStatus)
							{
								destInfoErrorMsg  = new StringBuffer();
								destInfoErrorMsg.append("Invalid Username or Password. Unable to connect.");
							}
						}catch(Exception e)
						{
							destInfoErrorMsg  = new StringBuffer();
							destInfoErrorMsg.append(e.getMessage());
						}
					}
					/*com.daffodil.documentumie.filehandler.SFTPFileHandler ffh = new com.daffodil.documentumie.filehandler.SFTPFileHandler(ftpUrlStr, userNameStr, passwordStr);
					try{								
						boolean connectStatus = ffh.connect();	
						if(!connectStatus)
						{
							destInfoErrorMsg  = new StringBuffer();
							destInfoErrorMsg.append("Invalid Username or Password. Unable to connect.");
						}
					}catch (Exception e) {
						destInfoErrorMsg  = new StringBuffer();
						destInfoErrorMsg.append(e.getMessage());
					}*/
				}
			}
			else{
				if(destInfoErrorMsg == null){
					destInfoErrorMsg  = new StringBuffer();
					destInfoErrorMsg.append("Please Select  where you want to Export");
				}
				
			}
			return destInfoErrorMsg;
		}

		@Override
		protected StringBuffer errorMessage() {
			return errorMessage;
		}
		
	}
