package com.daffodil.documentumie.iebusiness.pimport.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.ButtonGroup;

import org.apache.log4j.Logger;

import com.daffodil.documentumie.fileutil.logger.IELogger;
import com.daffodil.documentumie.iebusiness.AbstractUIControl;
import com.daffodil.documentumie.iebusiness.DButton;
import com.daffodil.documentumie.iebusiness.pimport.bean.ImportUIInfoBean;
import com.daffodil.documentumie.scheduleie.bean.IScheduleConfigBean;
import com.daffodil.documentumie.scheduleie.bean.ScheduleImportConfigBean;
import com.daffodil.documentumie.scheduleie.configurator.DaffIESchedularConfigurator;
import com.daffodil.documentumie.scheduleie.model.Exception.scheduleFileWriterException;
import com.daffodil.documentumie.scheduleie.model.apiimpl.IEScheduleWriter;
import com.toedter.calendar.JDateChooser;

public class IScheduleProcess<endDateChooser> extends AbstractUIControl {
	//private StringBuffer errorMessage = null; // Commented by Harsh as scheduleErrorMessage contains the Error 
	private String importFrom,ftpDownloadedMetadataPath=null;
	private StringBuffer scheduleErrorMessage = null;
	Logger logger = Logger.getLogger(IELogger.class);
	
	public IScheduleProcess() {
		System.out.println("Shubhra: checking IScheduleProcess");
		logger.info("Shubhra: checking IScheduleProcess");
		initComponents();
		System.out.println("shubhra : After calling initComponents method");
		initlizeUI();
	}
	ButtonGroup buttonGroupFTP;// Added by Harsh for FTP functionality on 1/16/2012
	@Override
	protected void initUI() {
		super.initUI();
		// Below code is added by Harsh for FTP functionality on 1/16/2012
				buttonGroupFTP = new ButtonGroup();
				buttonGroupFTP.add(fileSystem_JRadioButton);
				buttonGroupFTP.add(ftp_JRadioButton);
	}

	protected void installListener()
	{
		System.out.println("shubhra : Starting installListener");
		schedule_JButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				writeScheduleConfigFile();
				writeScheduleImportConfigFile();
				getIEMessageUtility().showMessageDialog("Scheduling of Import Process is successfully completed.",null);
			}
		});
		System.out.println("shubhra : After calling addActionListener");
		logger.info("shubhra : After calling addActionListener");
		
		// Below code is added by Harsh for the FTP functionality on 1/16/2012
		ftp_JRadioButton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				System.out.println("FTP checked");
				scheduleErrorMessage = null;
				setImportFrom("ftp");
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
				scheduleErrorMessage = null;
				setImportFrom("file");
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
				worksheetFTP_JLabel.setVisible(false);
				worksheet_JTextField.setText("");
				worksheet_JTextField.setVisible(false);*/
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
			showMessage = "Schedule Import Process.";
		}
		return showMessage;
	}

	@Override
	protected void postInilize() {
		System.out.println("IScheduleProcess.postInilize().................");
		writeScheduleConfigFile();
		writeScheduleImportConfigFile();
	}

	
	//  This is the method to validate the input fields
	
	protected void postValidateAction() {
		super.postValidateAction();
	}

	@Override
	public StringBuffer validateUIInputs() {
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
		String worksheetStr = worksheet_JTextField.getText();
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
			int min=Integer.parseInt(String.valueOf(min_JComboBox.getSelectedItem()));
			System.out.println("Naveen:"+new Date(startDate.getYear(), startDate.getMonth(), startDate
					.getDate(), hrs, min));
			System.out.println("Naveen:"+new Date(
							currentDate.getYear(), currentDate.getMonth(), currentDate
							.getDate(), currentDate.getHours(), currentDate.getMinutes()));
			int i = (new Date(startDate.getYear(), startDate.getMonth(), startDate
					.getDate(), hrs, min)).compareTo(new Date(
							currentDate.getYear(), currentDate.getMonth(), currentDate
							.getDate(), currentDate.getHours(), currentDate.getMinutes()));
			
//			int hrs = Integer.parseInt(String.valueOf(hr_JComboBox.getSelectedItem()));
//			int i = (new Date(startDate.getYear(), startDate.getMonth(), startDate
//					.getDate(), hrs, 0)).compareTo(new Date(
//							currentDate.getYear(), currentDate.getMonth(), currentDate
//							.getDate(), currentDate.getHours(), currentDate.getMinutes()));
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
			else if(worksheetStr == null || "".equalsIgnoreCase(worksheetStr.trim()))
			{
				if(scheduleErrorMessage == null){
					scheduleErrorMessage  = new StringBuffer();
					scheduleErrorMessage.append("Please Enter ");
				}
				else{
					scheduleErrorMessage.append(", ");
				}
				scheduleErrorMessage.append("Worksheet name");
				worksheet_JTextField.requestFocus();
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
			}else{

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
									scheduleErrorMessage  = new StringBuffer();
									scheduleErrorMessage.append("Metadata download Not successful");
								}
							} catch (Exception e) {
								scheduleErrorMessage  = new StringBuffer();
								scheduleErrorMessage.append(e.getMessage());
							}
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
						if(connectStatus)
						{
							try {
								String localFilePath = ffh.getFile(metadataStr);
								ftpDownloadedMetadataPath = localFilePath.toString();
								boolean metaStatus = checkFileExistance(localFilePath);
								if(!metaStatus)
								{
									scheduleErrorMessage  = new StringBuffer();
									scheduleErrorMessage.append("Metadata download Not successful");
								}
							} catch (Exception e) {
								scheduleErrorMessage  = new StringBuffer();
								scheduleErrorMessage.append(e.getMessage());
							}
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
				scheduleErrorMessage.append("Please Select From where you want to Import");
			}
			else{
				scheduleErrorMessage  = new StringBuffer();
				scheduleErrorMessage.append("Please Select From where you want to Import ");
			}
		}

		return scheduleErrorMessage;
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

	private void writeScheduleConfigFile() {
		IScheduleConfigBean scheduleConfgiBean = new IScheduleConfigBean();

		//Date startDate = startDateChooser.getDate();
		GregorianCalendar cgStartDate = (GregorianCalendar) Calendar.getInstance();
		String hours = String.valueOf(hr_JComboBox.getSelectedItem());
		int hrs = Integer.parseInt(hours);
		int min = Integer.parseInt(String.valueOf(min_JComboBox
				.getSelectedItem()));
		Date startDate = startDateChooser.getDate();

		//cgStartDate.set(startDate.getYear() + 1900, startDate.getMonth(),
				//startDate.getDate(), hrs, min);
				
		String pattern = "MM/dd/yyyy HH:mm";
		SimpleDateFormat format = new SimpleDateFormat(pattern);

		String nextScheduleDate = format.format(cgStartDate.getTime());

		scheduleConfgiBean.setStartDate(nextScheduleDate);
		// Changed by Harsh , initially it was scheduleConfgiBean.setNexSchedule(nextScheduleDate);
		cgStartDate.set(startDate.getYear() + 1900, startDate.getMonth(), startDate
				.getDate());
		scheduleConfgiBean.setNexSchedule(format.format(cgStartDate.getTime()));
		// Changes finished here
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

		scheduleConfgiBean.setScheduleFor("Import");
		try {
			new DaffIESchedularConfigurator().saveAndUpdateSchedule(scheduleConfgiBean);
		} catch (scheduleFileWriterException e) {
			if (scheduleErrorMessage == null) {
				scheduleErrorMessage = new StringBuffer();
			}
			scheduleErrorMessage.append(e.getMessage());
			e.printStackTrace();
		}
	}

	private void writeScheduleImportConfigFile() {
		ScheduleImportConfigBean scheduleImportConfigBean = new ScheduleImportConfigBean();

		scheduleImportConfigBean.setRepository(getImportUIInfoBean()
				.getRepository());
		scheduleImportConfigBean.setUserName(getImportUIInfoBean()
				.getUserName());
		scheduleImportConfigBean.setPassword(getImportUIInfoBean()
				.getPassword());
		scheduleImportConfigBean.setDomain(getImportUIInfoBean().getDomain());
		scheduleImportConfigBean.setObjectType(getImportUIInfoBean()
				.getObjectType());
		scheduleImportConfigBean.setDomain(getImportUIInfoBean().getDomain());
		scheduleImportConfigBean.setMetadataInputFile(getImportUIInfoBean()
				.getMetadataInputFile());
		scheduleImportConfigBean.setExtension("xls");
		scheduleImportConfigBean.setWorksheet(getImportUIInfoBean()
				.getWorksheet());
		scheduleImportConfigBean.setUpDateExisting(getImportUIInfoBean()
				.isUpdateExisting() ? "yes" : "no");
		scheduleImportConfigBean
				.setIsLive(getImportUIInfoBean().getIsLive() ? "yes" : "no");
		scheduleImportConfigBean.setMappedAttributs(getImportUIInfoBean()
				.getMappedAttributs());
		scheduleImportConfigBean.setSql(getImportUIInfoBean().getSql());
		//
		if(getImportFrom().equalsIgnoreCase("ftp"))
		{
			scheduleImportConfigBean.setImportFrom("FTP");
			scheduleImportConfigBean.setFtpURL(ftpUrl_JTextField.getText());
			scheduleImportConfigBean.setFtpUserName(user_JTextField.getText());
			scheduleImportConfigBean.setFtpPassword(new String(password_JPasswordField.getPassword()));
			scheduleImportConfigBean.setFtpMetadataPath(metadata_JTextField.getText());
			scheduleImportConfigBean.setFtpMetaSheet(worksheet_JTextField.getText());
		}
		else{
			scheduleImportConfigBean.setImportFrom("file");
		}
		scheduleImportConfigBean.setScheduleName(scheduleName_JTextField.getText());
		new IEScheduleWriter().writeScheduleXMLFile(scheduleImportConfigBean);

	}

	@Override
	protected void preInilize() {

	}

	private ImportUIInfoBean getImportUIInfoBean() {
		return (ImportUIInfoBean) getUiInfoBean();
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 * @param <endDateChooser>
	 */
	// <editor-fold defaultstate="collapsed" desc="Generated Code">
	private <endDateChooser> void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;
        		
        date_JPanel = new javax.swing.JPanel();
        date_JLabel = new javax.swing.JLabel();
        /*repeat_JLabel = new javax.swing.JLabel();
        frequency_JLabel = new javax.swing.JLabel();
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
		
		// The below objects provide the functionality of the FTP .Added by Harsh on 1/16/2012
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
		scheduleName_JLabel = new javax.swing.JLabel();
		scheduleName_JTextField= new javax.swing.JTextField();
		
		//Added by Shubhra 
		schedule_JButton = new DButton(){
			protected int getButtonWidth() {
				return 80;
			}
		};
		
        setOpaque(false);
        setPreferredSize(new java.awt.Dimension(370, 355));
        setLayout(new java.awt.GridBagLayout());
        
        importFrom_JLabel.setText("Import From");
		importFrom_JLabel.setPreferredSize(new java.awt.Dimension(80,20));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
		add(importFrom_JLabel, gridBagConstraints);
        
        importFrom_JPanel.setPreferredSize(new java.awt.Dimension(200, 20));
		importFrom_JPanel.setLayout(new java.awt.GridBagLayout());
		importFrom_JPanel.setOpaque(false);

		fileSystem_JRadioButton.setOpaque(false);
		fileSystem_JRadioButton.setText("File System");
		fileSystem_JRadioButton.setPreferredSize(new java.awt.Dimension(50, 20));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.insets = new java.awt.Insets(0, -10, 15, 5);
		importFrom_JPanel.add(fileSystem_JRadioButton, gridBagConstraints);

		ftp_JRadioButton.setOpaque(false);
		ftp_JRadioButton.setText("FTP");
		ftp_JRadioButton.setPreferredSize(new java.awt.Dimension(50, 20));

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.insets = new java.awt.Insets(0,20, 15, 5);
		importFrom_JPanel.add(ftp_JRadioButton, gridBagConstraints);


		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 20);
		add(importFrom_JPanel, gridBagConstraints);
		
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

       /* repeat_JComboBox.addItem("Hours");
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
		
		worksheetFTP_JLabel.setText("Worksheet");
		worksheetFTP_JLabel.setPreferredSize(new java.awt.Dimension(80,20));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 10;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
		add(worksheetFTP_JLabel, gridBagConstraints);
		//worksheetFTP_JLabel.setVisible(false);
		
		worksheet_JTextField.setOpaque(false);
		worksheet_JTextField.setPreferredSize(new java.awt.Dimension(180,20));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 10;
		gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
		add(worksheet_JTextField, gridBagConstraints);
		//worksheet_JTextField.setVisible(false);
		
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
	private javax.swing.JLabel importFrom_JLabel;
	private javax.swing.JPanel importFrom_JPanel;
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
	private javax.swing.JLabel scheduleName_JLabel;
	private javax.swing.JTextField scheduleName_JTextField;
	// private javax.swing.JComboBox type_JComboBox;
	// End of variables declaration
	public String getImportFrom() {
		return importFrom;
	}

	public void setImportFrom(String importFrom) {
		this.importFrom = importFrom;
	}
}
