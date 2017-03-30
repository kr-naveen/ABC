package com.daffodil.documentumie.iebusiness.pimport.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultListModel;
import com.daffodil.documentumie.fileutil.configurator.DaffIEResumeConfigurator;
import com.daffodil.documentumie.fileutil.configurator.bean.ImportConfigBeanForResume;
import com.daffodil.documentumie.fileutil.configurator.exception.DConfigReadException;
import com.daffodil.documentumie.fileutil.logger.IELogger;
import com.daffodil.documentumie.fileutil.metadata.MetadataProcessorFactory;
import com.daffodil.documentumie.iebusiness.AbstractUIControl;
import com.daffodil.documentumie.iebusiness.DButton;
import com.daffodil.documentumie.iebusiness.pimport.bean.ImportUIInfoBean;

public class IResumeProcess extends AbstractUIControl {

	String imageLocation;

	String showMessage;

	StringBuffer errorMessage;

	HashMap<String, String> resumeFileMap = new HashMap<String, String>();

	String resumeFileName = null;



	public IResumeProcess() {

		initComponents();
		initlizeUI();
	}

	@Override
	protected void initUI() {
		super.initUI();
	}

	protected void installListener() {
//		resume_JButton.addActionListener(new ActionListener() {
//
//			public void actionPerformed(ActionEvent arg0) {
////				 System.out.println(".actionPerformed() : " + fileList_JList.getSelectedValue());
////				 System.out.println(".... " + (String) getResumeFileMap().get(fileList_JList.getSelectedValue()));
////				 setResumeFileName((String) getResumeFileMap().get(fileList_JList.getSelectedValue()));
//			}
//		});

		clear_JButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				String fileName = (String) fileList_JList.getSelectedValue();
				System.out.println("filename -- " + fileName);
				if ("".equalsIgnoreCase(fileName) || fileName == null) {
					getIEMessageUtility().showMessageDialog("No any File is selected.", null);
				} else {
					int sizeselected = fileList_JList.getModel().getSize();
					fileListModel.removeElement(fileName);
					String removeFile = (String) getResumeFileMap().get(fileName);
					File resumeFile = new File(removeFile);
					if (resumeFile.exists()) {
						resumeFile.delete();
					}

					if (fileList_JList.getComponentCount() == 0) {
//						resume_JButton.setEnabled(false);
						clear_JButton.setEnabled(false);
					}
				}
			}
		});
	}

	@Override
	protected StringBuffer errorMessage() {
		// TODO Auto-generated method stub
		return errorMessage;
	}

	@Override
	public String getImageLocation() {
		imageLocation = "/com/daffodil/documentumie/iebusiness/images/heading_login.jpg";
		return imageLocation;
	}

	@Override
	public String getShowMessage() {
		showMessage = "Select repository where you want to import data and provide all " + "\n" + "login credentials required for the respective repository.";
		return showMessage;
	}

	@Override
	protected void postInilize() {
		try {
			setResumeFileName((String) getResumeFileMap().get(fileList_JList.getSelectedValue()));
			ImportConfigBeanForResume importConfigBeanForResume = DaffIEResumeConfigurator.readFile(getResumeFileName());
			setResumeInput(importConfigBeanForResume);
		} catch (DConfigReadException e) {
			getIEMessageUtility().showMessageDialog(e.getMessage(), e.getCause());
			getIELogger().writeLog(e.getMessage(), IELogger.DEBUG);
		}
	}

	private void setResumeInput(
		ImportConfigBeanForResume importConfigBeanForResume) {
		String metadataFileLocation = importConfigBeanForResume.getMetadataFileLocation(); 
		String worksheet = importConfigBeanForResume.getWorkSheet();
		getImportUIInfoBean().setRepository(importConfigBeanForResume.getRepoName());
		getImportUIInfoBean().setObjectType(importConfigBeanForResume.getObjectType());
		getImportUIInfoBean().setSql(importConfigBeanForResume.getFilterCriteria());
		getImportUIInfoBean().setIsLive(importConfigBeanForResume.getLiveRun()==null|| "".equalsIgnoreCase(importConfigBeanForResume.getLiveRun()) || importConfigBeanForResume.getLiveRun().equalsIgnoreCase("no")? false : true);
		getImportUIInfoBean().setUpdateExisting(importConfigBeanForResume.getUpdateExisting()==null|| "".equalsIgnoreCase(importConfigBeanForResume.getUpdateExisting()) || importConfigBeanForResume.getUpdateExisting().equalsIgnoreCase("no") ? false : true);
		getImportUIInfoBean().setMappedAttributs(importConfigBeanForResume.getMap());
		getImportUIInfoBean().setMetadataInputFile(metadataFileLocation);
//		getImportUIInfoBean().setObjectName(importConfigBeanForResume.getObjectName());
		getImportUIInfoBean().setR_folder_path(importConfigBeanForResume.getR_folder_path());
		getImportUIInfoBean().setRecordNo(Integer.parseInt(importConfigBeanForResume.getRecordNo()));
		getImportUIInfoBean().setResume(true);
		getImportUIInfoBean().setSourceFileLocation(importConfigBeanForResume.getSourceFileLocation());
		getImportUIInfoBean().setObjectHirerchy(Integer.parseInt(importConfigBeanForResume.getObjectHirerchy()));
		getImportUIInfoBean().setWorksheet(worksheet);
		getImportUIInfoBean().setResumeConfigFileLocation(getResumeFileName());
		setResumeFileName((String) getResumeFileMap().get(fileList_JList.getSelectedValue()));
		String extension = (metadataFileLocation != null && !"".equals(metadataFileLocation
				.trim())) ? metadataFileLocation.substring((metadataFileLocation
						.lastIndexOf(".") + 1)) : "";
				getImportUIInfoBean().setExtension(extension);

				if(extension.equalsIgnoreCase("xls")){
					initExcelmetadataReader(metadataFileLocation, worksheet, extension);
				}if(extension.equalsIgnoreCase("csv")){
					initCSVMetadataReader(metadataFileLocation, worksheet, extension);
				}if(extension.equalsIgnoreCase("xml")){
					initXMLMetadataReader(metadataFileLocation, worksheet, extension);
				}
	}

	public void initExcelmetadataReader(String metadataFileLocation, String worksheet, String extension) {
		HashMap map = new HashMap();
		map.put("File_Name", metadataFileLocation);
		map.put("Table_Name", worksheet);
		map.put("extension", extension);
		getMainUIControl().setMetadataReader(MetadataProcessorFactory.getMetadataReader(map));
	}

	private void initXMLMetadataReader(String metadataFileLocation, String worksheet, String extension) {
		String path = metadataFileLocation;
		String reportFileLocation = path.substring(0, path.lastIndexOf('\\')+1);
		String reportFileName = path.substring(path.lastIndexOf('\\')+1, path.length()-4);
		HashMap map = new HashMap();
		map.put("File_Name", reportFileLocation);
		map.put("Table_Name", reportFileName);
		map.put("extension", extension);
		getMainUIControl().setMetadataReader(MetadataProcessorFactory.getMetadataReader(map));
	}

	private void initCSVMetadataReader(String metadataFileLocation, String worksheet, String extension) {
		String path = metadataFileLocation;
		String reportFileLocation = path.substring(0, path.lastIndexOf('\\')+1);
		String reportFileName = path.substring(path.lastIndexOf('\\')+1, path.length());
		HashMap map = new HashMap();
		map.put("File_Name", reportFileLocation);
		map.put("Table_Name", reportFileName);
		map.put("extension", extension);
		getMainUIControl().setMetadataReader(MetadataProcessorFactory.getMetadataReader(map));
	}

	private ImportUIInfoBean getImportUIInfoBean(){
		return (ImportUIInfoBean)getUiInfoBean();
	}

	@Override
	protected void preInilize() {
		HashMap<String, String> map=null;
		map = DaffIEResumeConfigurator.findResumableProcesses(getImportUIInfoBean().getUserName());
		setResumeFileMap(map);
		showFileList();
	}

	private void showFileList() {
		fileListModel.removeAllElements();
		for (int i = 0; i <getFileList().size(); i++) {
			fileListModel.addElement(getFileList().get(i));
		}
	}


	/*public ImportConfigBeanForResume getImportConfigBeanForResume() {
		return importConfigBeanForResume;
	}

	public void setImportConfigBeanForResume(
			ImportConfigBeanForResume importConfigBeanForResume) {
		this.importConfigBeanForResume = importConfigBeanForResume;
	}
	 */
	@Override
	protected StringBuffer validateUIInputs() {
		if (fileList_JList.isSelectionEmpty()) {
			if (errorMessage == null) {
				errorMessage = new StringBuffer();
			}
			errorMessage.append("No file selected.");
		}
		return errorMessage;
	}

	public List getFileList(){
		List fileList = new ArrayList();
//		HashMap<String, String> map = getFileMap();
		for (Iterator iterator = getResumeFileMap().entrySet().iterator(); iterator
		.hasNext();) {
			Map.Entry entry = (Map.Entry) iterator.next();
			String key = entry.getKey().toString();

			fileList.add(key);
		}

		return fileList;
	}


	private void setResumeConfigInput() {
		// TODO Auto-generated method stub

	}


	private void initComponents() {
		java.awt.GridBagConstraints gridBagConstraints;

		jScrollPane1 = new javax.swing.JScrollPane();
		fileList_JList = new javax.swing.JList();
		fileListModel = new DefaultListModel();
		Resume_JPanel = new javax.swing.JPanel();
//		resume_JButton = new DButton() {
//			protected int getButtonWidth() {
//				return 80;
//			}
//		};
		clear_JButton = new DButton() {
			protected int getButtonWidth() {
				return 80;
			}
		};
		fileList_JLabel = new javax.swing.JLabel();

		setOpaque(false);
		setPreferredSize(new java.awt.Dimension(370, 355));
		setLayout(new java.awt.GridBagLayout());

		jScrollPane1.setPreferredSize(new java.awt.Dimension(200, 200));

		fileList_JList.setModel(fileListModel);
		jScrollPane1.setViewportView(fileList_JList);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.insets = new java.awt.Insets(0, 20, 5, 20);
		add(jScrollPane1, gridBagConstraints);

		fileList_JLabel.setText("Reume File List");
		fileList_JLabel.setPreferredSize(new java.awt.Dimension(80, 20));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.insets = new java.awt.Insets(20, 20, 5, 20);
		add(fileList_JLabel, gridBagConstraints);

		Resume_JPanel.setPreferredSize(new java.awt.Dimension(200, 24));
		Resume_JPanel.setLayout(new java.awt.GridBagLayout());
		Resume_JPanel.setOpaque(false);

//		resume_JButton.setText("Resume");
//		resume_JButton.setPreferredSize(new java.awt.Dimension(80, 20));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.insets = new java.awt.Insets(0, 20, 20, 5);
//		Resume_JPanel.add(resume_JButton, gridBagConstraints);

		clear_JButton.setText("Clear");
		clear_JButton.setPreferredSize(new java.awt.Dimension(80, 20));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 20, 20);
		Resume_JPanel.add(clear_JButton, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		add(Resume_JPanel, gridBagConstraints);

	}// </editor-fold>


	// Variables declaration - do not modify
	private DButton clear_JButton;
	private javax.swing.JLabel fileList_JLabel;
	private javax.swing.JList fileList_JList;
	private javax.swing.JScrollPane jScrollPane1;
//	private DButton resume_JButton;
	private javax.swing.DefaultListModel fileListModel;
	private javax.swing.JPanel Resume_JPanel;
	// End of variables declaration

	public HashMap<String, String> getResumeFileMap() {
		return resumeFileMap;
	}

	public void setResumeFileMap(HashMap<String, String> fileMap) {
		this.resumeFileMap = fileMap;
	}

	public String getResumeFileName() {
		return resumeFileName;
	}

	public void setResumeFileName(String fileName) {
		this.resumeFileName = fileName;
	}

}
