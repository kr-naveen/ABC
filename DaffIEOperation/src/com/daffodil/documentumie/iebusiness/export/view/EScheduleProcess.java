package com.daffodil.documentumie.iebusiness.export.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.swing.ButtonGroup;

import com.daffodil.documentumie.iebusiness.AbstractUIControl;
import com.daffodil.documentumie.iebusiness.DButton;
import com.daffodil.documentumie.iebusiness.export.bean.ExportUIInfoBean;
import com.daffodil.documentumie.scheduleie.bean.EScheduleConfigBean;
import com.daffodil.documentumie.scheduleie.bean.ScheduleConfgiBean;
import com.daffodil.documentumie.scheduleie.bean.ScheduleExportConfigBean;
import com.daffodil.documentumie.scheduleie.configurator.DaffIESchedularConfigurator;
import com.daffodil.documentumie.scheduleie.model.Exception.scheduleFileWriterException;
import com.daffodil.documentumie.scheduleie.model.apiimpl.IEScheduleWriter;
//import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JDateChooser;

public class EScheduleProcess extends AbstractUIControl {

	private StringBuffer errorMessage = null;
	private StringBuffer scheduleErrorMessage = null;
	private String exportTo = null;

	public EScheduleProcess() {
		initComponents();
		initlizeUI();
	}
	ButtonGroup buttonGroupFTP;// Added by Harsh for FTP functionality on 1/21/2012
	@Override
	protected void initUI() {
		super.initUI();
		buttonGroupFTP = new ButtonGroup();
		buttonGroupFTP.add(fileSystem_JRadioButton);
		buttonGroupFTP.add(ftp_JRadioButton);
	}

	protected void installListener() {
		schedule_JButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				writeScheduleConfigFile();
				writeScheduleExportConfigFile();
				getIEMessageUtility()
				.showMessageDialog(
						"Scheduling of Export Process is successfully completed.",
						null);
			}
		});
		ftp_JRadioButton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				System.out.println("FTP checked");
				scheduleErrorMessage = null;
				setExportTo("ftp");
				ftpUrl_JLabel.setVisible(true);
				ftpUrl_JTextField.setVisible(true);
				user_JLabel.setVisible(true);
				user_JTextField.setVisible(true);
				password_JLabel.setVisible(true);
				password_JPasswordField.setVisible(true);
				metadataFileFTP_JLabel.setVisible(true);
				metadata_JTextField.setVisible(true);
				contentFTP_JLabel.setVisible(true);
				content_JTextField.setVisible(true);
			}
		});
		fileSystem_JRadioButton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {		
				
				System.out.println("File System checked");
				scheduleErrorMessage = null;
				setExportTo("file");
				ftpUrl_JLabel.setVisible(false);
				ftpUrl_JTextField.setVisible(false);
				ftpUrl_JTextField.setText("");
				user_JLabel.setVisible(false);
				user_JTextField.setVisible(false);
				user_JTextField.setText("");
				password_JLabel.setVisible(false);
				password_JPasswordField.setVisible(false);
				password_JPasswordField.setText("");
				/*metadataFileFTP_JLabel.setVisible(false);
				metadata_JTextField.setVisible(false);
				metadata_JTextField.setText("");
				contentFTP_JLabel.setVisible(false);
				content_JTextField.setVisible(false);
				content_JTextField.setText("");*/
			}
		});
	}

	@Override
	protected StringBuffer errorMessage() {
		return scheduleErrorMessage;
	}

	@Override
	public String getImageLocation() {
		String imageLocation = null;
		if (imageLocation == null) {
			imageLocation = "/com/daffodil/documentumie/iebusiness/pimport/view/images/heading_i_source.jpg";
		}
		return imageLocation;
	}

	@Override
	public String getShowMessage() {
		String showMessage = null;
		if (showMessage == null) {
			showMessage = "Scheduling Export Process.";
		}
		return showMessage;
	}

	@Override
	protected void postInilize() {
		System.out.println("EScheduleProcess.postInilize().................");
		writeScheduleConfigFile();
		writeScheduleExportConfigFile();

	}

	private void writeScheduleConfigFile() {
		EScheduleConfigBean scheduleConfgiBean = new EScheduleConfigBean();

		Date startDate = startDateChooser.getDate();
		GregorianCalendar cgStartDate = (GregorianCalendar) Calendar
				.getInstance();
		String hours = String.valueOf(hr_JComboBox.getSelectedItem());
		int hrs = Integer.parseInt(hours);
		int min = Integer.parseInt(String.valueOf(min_JComboBox
				.getSelectedItem()));

		cgStartDate.set(startDate.getYear() + 1900, startDate.getMonth(),
				startDate.getDate(), hrs, min);
		String pattern = "MM/dd/yyyy HH:mm";
		SimpleDateFormat format = new SimpleDateFormat(pattern);

		String nextScheduleDate = format.format(cgStartDate.getTime());

		scheduleConfgiBean.setStartDate(nextScheduleDate);
		scheduleConfgiBean.setNexSchedule(nextScheduleDate);

		scheduleConfgiBean.setHours(String.valueOf(hr_JComboBox
				.getSelectedItem()));
		scheduleConfgiBean.setMinute(String.valueOf(min_JComboBox
				.getSelectedItem()));
		/*scheduleConfgiBean.setFrequency(String.valueOf(frequency_JTextField
				.getText()));
		scheduleConfgiBean.setRepeat((String) repeat_JComboBox
				.getSelectedItem());*/

		GregorianCalendar cgEndDate = (GregorianCalendar) Calendar
				.getInstance();

		Date endDate = endDateChooser.getDate();
		cgEndDate.set(endDate.getYear() + 1900, endDate.getMonth(), endDate
				.getDate());
		pattern = "MM/dd/yyyy";
		format = new SimpleDateFormat(pattern);

		String endScheduleDate = format.format(cgEndDate.getTime());

		scheduleConfgiBean.setEndDate(endScheduleDate);

		scheduleConfgiBean.setScheduleFor("Export");
		try {
			new DaffIESchedularConfigurator().saveAndUpdateSchedule(scheduleConfgiBean);
		} catch (scheduleFileWriterException e) {
			if (errorMessage == null) {
				errorMessage = new StringBuffer();
			}
			errorMessage.append(e.getMessage());
			e.printStackTrace();
		}
	}

	private void writeScheduleExportConfigFile() {
		ScheduleExportConfigBean scheduleExportConfigBean = new ScheduleExportConfigBean();

		scheduleExportConfigBean.setRepository(getExportUIInfoBean()
				.getRepository());
		scheduleExportConfigBean.setUserName(getExportUIInfoBean()
				.getUserName());
		scheduleExportConfigBean.setPassword(getExportUIInfoBean()
				.getPassword());
		scheduleExportConfigBean.setDomain(getExportUIInfoBean().getDomain());
		scheduleExportConfigBean.setObjectType(getExportUIInfoBean()
				.getObjectType());
		scheduleExportConfigBean.setAllVersion(getExportUIInfoBean()
				.isAllVersion() ? "yes" : "no");
		List attributes = getExportUIInfoBean().getSelectedAttribute();
		System.out.println("attributes.size() " + attributes.size());
		StringBuffer attr = new StringBuffer();
		for (int i = 0; i < attributes.size(); i++) {
			attr.append(attributes.get(i));
			if (i < attributes.size() - 1)
				attr.append(',');
		}
		scheduleExportConfigBean.setAttributeList(attr.toString());
		scheduleExportConfigBean.setDql(getExportUIInfoBean().getDqlText());
		scheduleExportConfigBean.setExportLocation(getExportUIInfoBean()
				.getOutPutFile());
		scheduleExportConfigBean.setExportType(getExportUIInfoBean()
				.getExportType());
		scheduleExportConfigBean.setInZip(getExportUIInfoBean()
				.isExportIntoZIP() ? "yes" : "no");
		scheduleExportConfigBean.setLogFileLocation(getExportUIInfoBean()
				.getMetaDataFilePath());
		scheduleExportConfigBean.setOnlyMetadata(getExportUIInfoBean()
				.isOnlyMetadata() ? "yes" : "no");
		scheduleExportConfigBean.setReportFileLocation(getExportUIInfoBean()
				.getMetaDataFilePath());
		if(getExportTo().equalsIgnoreCase("ftp"))
		{
			scheduleExportConfigBean.setExportTo("ftp");
			scheduleExportConfigBean.setFtpContentPath(content_JTextField.getText());
			scheduleExportConfigBean.setFtpMetadataPath(metadata_JTextField.getText());
			scheduleExportConfigBean.setFtpPassword(new String(password_JPasswordField.getPassword()));
			scheduleExportConfigBean.setFtpURL(ftpUrl_JTextField.getText());
			scheduleExportConfigBean.setFtpUserName(user_JTextField.getText());
		}
		else{
			scheduleExportConfigBean.setExportTo("file");
		}
		scheduleExportConfigBean.setScheduleName(scheduleName_JTextField.getText());
		new IEScheduleWriter().writeScheduleXMLFile(scheduleExportConfigBean);
	}

	@Override
	protected void preInilize() {

	}

	@Override
	protected StringBuffer validateUIInputs() {

		scheduleErrorMessage = null;
		System.out.println("Validating UI Inputs");
		Date startDate = startDateChooser.getDate();
		//String freq = frequency_JTextField.getText();
		Date endDate = endDateChooser.getDate();
		System.out.println("End date... " + endDate);
		System.out.println("start date... " + startDate);
		Date currentDate = new Date();
		System.out.println("current date.. " + currentDate);
		// Below variables are added by Harsh for FTP implementation on 1/16/2012
		String ftpUrlStr = ftpUrl_JTextField.getText();
		String userNameStr = user_JTextField.getText();
		String passwordStr = new String(password_JPasswordField.getPassword());
		String metadataStr = metadata_JTextField.getText();
		String contentStr = content_JTextField.getText();
		String scheduleNameStr = scheduleName_JTextField.getText();

		if(startDate==null || "".equals(startDate))
		{
			if (scheduleErrorMessage == null) {
				scheduleErrorMessage = new StringBuffer();
				scheduleErrorMessage.append("Please Enter ");
			} else {
				scheduleErrorMessage.append(", ");
			}
			scheduleErrorMessage.append("Start Date");
		}
		else if(endDate==null || "".equals(endDate))
		{
			if (scheduleErrorMessage == null) {
				scheduleErrorMessage = new StringBuffer();
				scheduleErrorMessage.append("Please Enter ");
			} else {
				scheduleErrorMessage.append(", ");
			}
			scheduleErrorMessage.append("End Date");
		}
		else{
			int hrs = Integer.parseInt(String.valueOf(hr_JComboBox.getSelectedItem()));
			int i = (new Date(startDate.getYear(), startDate.getMonth(), startDate
					.getDate(), hrs, 0)).compareTo(new Date(
							currentDate.getYear(), currentDate.getMonth(), currentDate
							.getDate(), currentDate.getHours(), currentDate.getMinutes()));
			System.out.println("in validaet i = " + i);
			if (i < 0) {
				if (scheduleErrorMessage == null) {
					scheduleErrorMessage = new StringBuffer();
					scheduleErrorMessage.append("Please Enter ");
				}
				scheduleErrorMessage.append("a valid start date");
			}
			/*else if (freq == null || "".equals(freq)) {
				if (scheduleErrorMessage == null) {
					scheduleErrorMessage = new StringBuffer();
					scheduleErrorMessage.append("Please Enter ");
				} else {
					scheduleErrorMessage.append(", ");
				}
				scheduleErrorMessage.append("Frquency ");
				frequency_JTextField.requestFocus();
			} else {
				try {
					Integer.valueOf(freq);
				} catch (NumberFormatException e) {
					if (scheduleErrorMessage == null) {
						scheduleErrorMessage = new StringBuffer();
						scheduleErrorMessage.append("Please Enter ");
					} else {
						scheduleErrorMessage.append(", ");
					}
					scheduleErrorMessage.append("an integer value in Frequency ");
					frequency_JTextField.setText("");
					frequency_JTextField.requestFocus();
				}
			}*/
			int j = endDate.compareTo(startDate);
			System.out.println("J is"+j);
			if (j < 0) {
				if (scheduleErrorMessage == null) {
					scheduleErrorMessage = new StringBuffer();
					// scheduleErrorMessage.append("Please Enter ");
				} else {
					scheduleErrorMessage.append("and ");
				}
				scheduleErrorMessage
				.append("End Date is less than the Start Date.");
			}		
		}
		if(fileSystem_JRadioButton!=null && fileSystem_JRadioButton.isSelected())
		{	
			if(scheduleNameStr == null || "".equalsIgnoreCase(scheduleNameStr.trim()))
			{
				if(scheduleErrorMessage == null){
					scheduleErrorMessage  = new StringBuffer();
					scheduleErrorMessage.append("Please Enter ");
				}
				else{
					scheduleErrorMessage.append(", ");
				}
				scheduleErrorMessage.append("Schedule name");
				scheduleName_JTextField.requestFocus();
			}
		}
		else if(ftp_JRadioButton!=null && ftp_JRadioButton.isSelected())
		{
			if(ftpUrlStr == null || "".equalsIgnoreCase(ftpUrlStr.trim()))
			{
				if(scheduleErrorMessage == null){
					scheduleErrorMessage  = new StringBuffer();
					scheduleErrorMessage.append("Please Enter ");
				}
				else{
					scheduleErrorMessage.append(", ");
				}
				scheduleErrorMessage.append("Ftp Url");
				ftpUrl_JTextField.requestFocus();
			}
			else if(userNameStr == null || "".equalsIgnoreCase(userNameStr.trim()))
			{
				if(scheduleErrorMessage == null){
					scheduleErrorMessage  = new StringBuffer();
					scheduleErrorMessage.append("Please Enter ");
				}
				else{
					scheduleErrorMessage.append(", ");
				}
				scheduleErrorMessage.append("User Name");
				user_JTextField.requestFocus();
			}
			//passwordStr
			else if(passwordStr == null || "".equalsIgnoreCase(passwordStr.trim()))
			{
				if(scheduleErrorMessage == null){
					scheduleErrorMessage  = new StringBuffer();
					scheduleErrorMessage.append("Please Enter ");
				}
				else{
					scheduleErrorMessage.append(", ");
				}
				scheduleErrorMessage.append("Password");
				password_JPasswordField.requestFocus();
			}
			else if(metadataStr == null || "".equalsIgnoreCase(metadataStr.trim()))
			{
				if(scheduleErrorMessage == null){
					scheduleErrorMessage  = new StringBuffer();
					scheduleErrorMessage.append("Please Enter ");
				}
				else{
					scheduleErrorMessage.append(", ");
				}
				scheduleErrorMessage.append("Metadata file location");
				metadata_JTextField.requestFocus();
			}
			else if(contentStr == null || "".equalsIgnoreCase(contentStr.trim()))
			{
				if(scheduleErrorMessage == null){
					scheduleErrorMessage  = new StringBuffer();
					scheduleErrorMessage.append("Please Enter ");
				}
				else{
					scheduleErrorMessage.append(", ");
				}
				scheduleErrorMessage.append("Content file location");
				content_JTextField.requestFocus();
			}
			else if(scheduleNameStr == null || "".equalsIgnoreCase(scheduleNameStr.trim()))
			{
				if(scheduleErrorMessage == null){
					scheduleErrorMessage  = new StringBuffer();
					scheduleErrorMessage.append("Please Enter ");
				}
				else{
					scheduleErrorMessage.append(", ");
				}
				scheduleErrorMessage.append("Schedule name");
				scheduleName_JTextField.requestFocus();
			}
			else{

				if(ftpUrlStr.startsWith("sftp"))
				{
					com.daffodil.documentumie.filehandler.SFTPFileHandler ffh = null;
					ftpUrlStr =  ftpUrlStr.substring(ftpUrlStr.indexOf(":")+3,ftpUrlStr.length());
					ffh = new com.daffodil.documentumie.filehandler.SFTPFileHandler(ftpUrlStr, userNameStr, passwordStr);
					try{								
						boolean connectStatus = ffh.connect();				
						if(!connectStatus)
						{							
							scheduleErrorMessage  = new StringBuffer();
							scheduleErrorMessage.append("Couldn't Connect to SFTP. Wrong User name or Password");			
						} 

					}catch(Exception e)
					{
						scheduleErrorMessage  = new StringBuffer();
						scheduleErrorMessage.append(e.getMessage());
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
							scheduleErrorMessage  = new StringBuffer();
							scheduleErrorMessage.append("Couldn't Connect to SFTP. Wrong User name or Password");
						}
					}catch(Exception e)
					{
						scheduleErrorMessage  = new StringBuffer();
						scheduleErrorMessage.append(e.getMessage());
					}
				}
			}
		}
		else{
			if(scheduleErrorMessage == null){
				scheduleErrorMessage  = new StringBuffer();
				scheduleErrorMessage.append("Please Select From where you want to Export");
			}
			else{
				scheduleErrorMessage  = new StringBuffer();
				scheduleErrorMessage.append("Please Select From where you want to Export ");
			}
		}

		return scheduleErrorMessage;

	}

	private ExportUIInfoBean getExportUIInfoBean() {
		return (ExportUIInfoBean) getUiInfoBean();
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed" desc="Generated Code">
	private void initComponents() {
		java.awt.GridBagConstraints gridBagConstraints;

		date_JPanel = new javax.swing.JPanel();
		date_JLabel = new javax.swing.JLabel();
		
		/*
		 * repeat_JLabel = new javax.swing.JLabel();
		 * frequency_JLabel = new javax.swing.JLabel();
		frequency_JTextField = new javax.swing.JTextField();
		repeat_JComboBox = new javax.swing.JComboBox();
		*/
		endDate_JLabel = new javax.swing.JLabel();
		
		jSeparator1 = new javax.swing.JSeparator();
		time_JLabel = new javax.swing.JLabel();
		jPanel1 = new javax.swing.JPanel();
		min_JLabel = new javax.swing.JLabel();
		hr_JComboBox = new javax.swing.JComboBox();
		hrs_JLabel = new javax.swing.JLabel();
		min_JComboBox = new javax.swing.JComboBox();
		startDateChooser = new JDateChooser();
		endDateChooser = new JDateChooser();

		// The below objects provide the functionality of the FTP .Added by Harsh on 1/21/2012
		fileSystem_JRadioButton = new javax.swing.JRadioButton();
		ftp_JRadioButton = new javax.swing.JRadioButton();
		exportFrom_JPanel = new javax.swing.JPanel();
		exportFrom_JLabel = new javax.swing.JLabel();
		ftpUrl_JLabel = new javax.swing.JLabel();
		ftpUrl_JTextField = new javax.swing.JTextField();
		user_JLabel = new javax.swing.JLabel();
		user_JTextField = new javax.swing.JTextField();
		password_JLabel = new javax.swing.JLabel();
		password_JPasswordField = new javax.swing.JPasswordField();
		metadataFileFTP_JLabel = new javax.swing.JLabel();
		metadata_JTextField = new javax.swing.JTextField();
		contentFTP_JLabel =  new javax.swing.JLabel();
		content_JTextField = new javax.swing.JTextField();
		scheduleName_JLabel = new javax.swing.JLabel();
		scheduleName_JTextField= new javax.swing.JTextField();
		schedule_JButton = new DButton(){
			protected int getButtonWidth() {
				return 80;
			}
		};

		setOpaque(false);
		setPreferredSize(new java.awt.Dimension(370, 355));
		setLayout(new java.awt.GridBagLayout());

		exportFrom_JLabel.setText("Export To");
		exportFrom_JLabel.setPreferredSize(new java.awt.Dimension(80,20));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
		add(exportFrom_JLabel, gridBagConstraints);

		exportFrom_JPanel.setPreferredSize(new java.awt.Dimension(200, 20));
		exportFrom_JPanel.setLayout(new java.awt.GridBagLayout());
		exportFrom_JPanel.setOpaque(false);

		fileSystem_JRadioButton.setOpaque(false);
		fileSystem_JRadioButton.setText("File System");
		fileSystem_JRadioButton.setPreferredSize(new java.awt.Dimension(50, 20));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.insets = new java.awt.Insets(0, -10, 15, 5);
		exportFrom_JPanel.add(fileSystem_JRadioButton, gridBagConstraints);

		ftp_JRadioButton.setOpaque(false);
		ftp_JRadioButton.setText("FTP");
		ftp_JRadioButton.setPreferredSize(new java.awt.Dimension(50, 20));

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.insets = new java.awt.Insets(0,20, 15, 5);
		exportFrom_JPanel.add(ftp_JRadioButton, gridBagConstraints);


		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 20);
		add(exportFrom_JPanel, gridBagConstraints);

		// The above code is added by Harsh for the implementation of the FTP functionality.

		date_JPanel.setOpaque(false);
		date_JPanel.setPreferredSize(new java.awt.Dimension(180, 21));
		date_JPanel.setLayout(new java.awt.GridBagLayout());

		//        startDateChooser.setEditable(false);
		startDateChooser.setMaximumSize(new java.awt.Dimension(0, 0));
		startDateChooser.setMinimumSize(new java.awt.Dimension(0, 0));
		startDateChooser.setPreferredSize(new java.awt.Dimension(180, 20));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		date_JPanel.add(startDateChooser, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		add(date_JPanel, gridBagConstraints);

		date_JLabel.setText("Date");
		date_JLabel.setPreferredSize(new java.awt.Dimension(80, 20));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
		add(date_JLabel, gridBagConstraints);

		/*repeat_JLabel.setText("Repeat");
		repeat_JLabel.setPreferredSize(new java.awt.Dimension(80, 20));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
		add(repeat_JLabel, gridBagConstraints);*/

		/*frequency_JLabel.setText("Frequency");
		frequency_JLabel.setPreferredSize(new java.awt.Dimension(80, 20));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
		add(frequency_JLabel, gridBagConstraints);

		frequency_JTextField.setPreferredSize(new java.awt.Dimension(180, 20));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
		add(frequency_JTextField, gridBagConstraints);*/

		endDate_JLabel.setText("End Date");
		endDate_JLabel.setPreferredSize(new java.awt.Dimension(80, 20));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 5;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
		add(endDate_JLabel, gridBagConstraints);

		//        endDateChooser.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
		endDateChooser.setPreferredSize(new java.awt.Dimension(180, 20));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 5;
		gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
		add(endDateChooser, gridBagConstraints);

		/*repeat_JComboBox.addItem("Hours");
		repeat_JComboBox.addItem("Days");
		repeat_JComboBox.addItem("Weeks");
		repeat_JComboBox.addItem("Months");
		repeat_JComboBox.addItem("Years");
		repeat_JComboBox.setMinimumSize(new java.awt.Dimension(0, 0));
		repeat_JComboBox.setPreferredSize(new java.awt.Dimension(181, 20));
		repeat_JComboBox.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				//                repeat_JComboBoxActionPerformed(evt);
			}
		});
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
		add(repeat_JComboBox, gridBagConstraints);
		add(jSeparator1, new java.awt.GridBagConstraints());*/

		time_JLabel.setText("Time");
		time_JLabel.setPreferredSize(new java.awt.Dimension(80, 20));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		add(time_JLabel, gridBagConstraints);

		jPanel1.setPreferredSize(new java.awt.Dimension(180, 23));
		jPanel1.setLayout(new java.awt.GridBagLayout());

		min_JLabel.setText("MM");
		min_JLabel.setOpaque(true);
		min_JLabel.setPreferredSize(new java.awt.Dimension(20, 20));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 4;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
		jPanel1.add(min_JLabel, gridBagConstraints);

		int j = 0;
		while (j < 24) {
			hr_JComboBox.addItem(j);
			j++;
		}
		hr_JComboBox.setPreferredSize(new java.awt.Dimension(65, 20));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		jPanel1.add(hr_JComboBox, gridBagConstraints);

		hrs_JLabel.setText("HH");
		hrs_JLabel.setPreferredSize(new java.awt.Dimension(20, 20));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
		jPanel1.add(hrs_JLabel, gridBagConstraints);

		String[] minArray = new String[60];
        for (int i = 0; i < 60; i++) {
        	minArray[i]=i+"";
		}
        
		min_JComboBox.setModel(new javax.swing.DefaultComboBoxModel(minArray));
		min_JComboBox.setPreferredSize(new java.awt.Dimension(65, 20));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 3;
		gridBagConstraints.gridy = 0;
		jPanel1.add(min_JComboBox, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 2;
		add(jPanel1, gridBagConstraints);

		/*
		 * Below code is added by Harsh for FTP implementation on 1/16/2011
		 */
		ftpUrl_JLabel.setText("Ftp Url");
		ftpUrl_JLabel.setPreferredSize(new java.awt.Dimension(80,20));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 6;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
		add(ftpUrl_JLabel, gridBagConstraints);
		ftpUrl_JLabel.setVisible(false);

		ftpUrl_JTextField.setOpaque(false);
		ftpUrl_JTextField.setPreferredSize(new java.awt.Dimension(180,20));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 6;
		gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
		add(ftpUrl_JTextField, gridBagConstraints);
		ftpUrl_JTextField.setVisible(false);

		user_JLabel.setText("User Name");
		user_JLabel.setPreferredSize(new java.awt.Dimension(80,20));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 7;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
		add(user_JLabel, gridBagConstraints);
		user_JLabel.setVisible(false);

		user_JTextField.setOpaque(false);
		user_JTextField.setPreferredSize(new java.awt.Dimension(180,20));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 7;
		gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
		add(user_JTextField, gridBagConstraints);
		user_JTextField.setVisible(false);

		password_JLabel.setText("Password");
		password_JLabel.setPreferredSize(new java.awt.Dimension(80,20));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 8;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
		add(password_JLabel, gridBagConstraints);
		password_JLabel.setVisible(false);

		password_JPasswordField.setOpaque(false);
		password_JPasswordField.setPreferredSize(new java.awt.Dimension(180,20));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 8;
		gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
		add(password_JPasswordField, gridBagConstraints);
		password_JPasswordField.setVisible(false);

		metadataFileFTP_JLabel.setText("Metadata File");
		metadataFileFTP_JLabel.setPreferredSize(new java.awt.Dimension(80,20));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 9;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
		add(metadataFileFTP_JLabel, gridBagConstraints);
		//metadataFileFTP_JLabel.setVisible(false);

		metadata_JTextField.setOpaque(false);
		metadata_JTextField.setPreferredSize(new java.awt.Dimension(180,20));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 9;
		gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
		add(metadata_JTextField, gridBagConstraints);
		//metadata_JTextField.setVisible(false);
		
		contentFTP_JLabel.setText("Content File");
		contentFTP_JLabel.setPreferredSize(new java.awt.Dimension(80,20));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 10;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
		add(contentFTP_JLabel, gridBagConstraints);
		//contentFTP_JLabel.setVisible(false);

		content_JTextField.setOpaque(false);
		content_JTextField.setPreferredSize(new java.awt.Dimension(180,20));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 10;
		gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
		add(content_JTextField, gridBagConstraints);
		//content_JTextField.setVisible(false);
		
		scheduleName_JLabel.setText("Schedule Name");
		scheduleName_JLabel.setPreferredSize(new java.awt.Dimension(80,20));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 11;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
		add(scheduleName_JLabel, gridBagConstraints);
		
		
		scheduleName_JTextField.setOpaque(false);
		scheduleName_JTextField.setPreferredSize(new java.awt.Dimension(180,20));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 11;
		gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
		add(scheduleName_JTextField, gridBagConstraints);

		
	}// </editor-fold>

	private void date_JTextFieldActionPerformed(java.awt.event.ActionEvent evt) {
		// TODO add your handling code here:
	}

	// Variables declaration - do not modify
	private javax.swing.JLabel date_JLabel;
	private javax.swing.JPanel date_JPanel;
	// private javax.swing.JTextField date_JTextField;
	private JDateChooser startDateChooser;
	private JDateChooser endDateChooser;
	private javax.swing.JLabel endDate_JLabel;
	/*private javax.swing.JLabel frequency_JLabel;
	private javax.swing.JTextField frequency_JTextField;*/
	private javax.swing.JComboBox hr_JComboBox;
	private javax.swing.JLabel hrs_JLabel;
	private javax.swing.JSeparator jSeparator1;
	private javax.swing.JComboBox min_JComboBox;
	private javax.swing.JLabel min_JLabel;
	/*private javax.swing.JComboBox repeat_JComboBox;
	private javax.swing.JLabel repeat_JLabel;*/
	private javax.swing.JButton schedule_JButton;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JLabel time_JLabel;
	// The below variables are added by Harsh for the implementation of the FTP functionality on 1/16/2012
	private javax.swing.JRadioButton fileSystem_JRadioButton;
	private javax.swing.JRadioButton ftp_JRadioButton;
	private javax.swing.JLabel exportFrom_JLabel;
	private javax.swing.JPanel exportFrom_JPanel;
	private javax.swing.JLabel ftpUrl_JLabel;
	private javax.swing.JTextField ftpUrl_JTextField;
	private javax.swing.JLabel user_JLabel;
	private javax.swing.JTextField user_JTextField;
	private javax.swing.JLabel password_JLabel;
	private javax.swing.JPasswordField password_JPasswordField;
	private javax.swing.JLabel metadataFileFTP_JLabel;
	private javax.swing.JTextField metadata_JTextField;
	private javax.swing.JLabel contentFTP_JLabel;
	private javax.swing.JTextField content_JTextField;
	private javax.swing.JLabel scheduleName_JLabel;
	private javax.swing.JTextField scheduleName_JTextField;

	// End of variables declaration

	public String getExportTo() {
		return exportTo;
	}

	public void setExportTo(String exportTo) {
		this.exportTo = exportTo;
	}


}
