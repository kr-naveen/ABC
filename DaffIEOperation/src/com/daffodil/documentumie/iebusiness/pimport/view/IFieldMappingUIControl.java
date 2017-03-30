/*
 * IFieldMappingUIControl.java
 *
 * Created on 26 June 2008, 15:38
 */

package com.daffodil.documentumie.iebusiness.pimport.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.table.DefaultTableModel;

import com.daffodil.documentumie.dctm.exception.DDfException;
import com.daffodil.documentumie.fileutil.logger.IELogger;
import com.daffodil.documentumie.fileutil.metadata.exception.DMetadataReadException;
import com.daffodil.documentumie.iebusiness.AbstractUIControl;
import com.daffodil.documentumie.iebusiness.DButton;
import com.daffodil.documentumie.iebusiness.pimport.bean.ImportUIInfoBean;
import com.daffodil.documentumie.iebusiness.pimport.model.ImportConstant;

/**
 * 
 * @author Administrator
 */
public class IFieldMappingUIControl extends AbstractUIControl {

	String imageLocation;
	String showMessage;
	List repoAttributeList;
	List attributeListfromMetadata;
	StringBuffer errorMessage;

	/**
	 * Creates new form IFieldMappingUI Control
	 * 
	 * @throws DMetadataReadException
	 */

	public IFieldMappingUIControl() {
		initComponents();
		initlizeUI();
	}

	@Override
	protected void initUI() {
		super.initUI();
		attribute_JComboBox = new JComboBox();
	}

	protected void installListener() {
		remove_JButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				int row[] = fieldMapping_JTable.getSelectedRows();
				String selectedAttr = null;
				System.out.println("fieldMapping_JTable size : " + fieldMapping_JTable.getRowCount());
				for (int i = row.length - 1; i >= 0; i--) {
					System.out.println("selected row no " + row[i]);
					selectedAttr = (String) fieldMapping_JTable.getModel().getValueAt(i, 0);
					defaultTableModel.removeRow(row[i]);
					getIELogger().writeLog( i+" selectedAttr : "+selectedAttr, IELogger.DEBUG);
					removeDatafromMatadataMap(selectedAttr);
				}
				fieldMapping_JTable.setModel(defaultTableModel);
			}

			private void removeDatafromMatadataMap(String selectedAttr) {
				HashMap map = getImportUIInfoBean().getSelectedAttributesOfMetadata();
				map.remove(selectedAttr);
				getImportUIInfoBean().setSelectedAttributesOfMetadata(map);
			}
		});
	}

	// method to get the all available attribute of selected object type from
	// repository
	private void getAttributefromRepository() {
		repoAttributeList = new ArrayList();
		try {
			List attributes = getCSServiceProvider().getAttributes(getImportUIInfoBean().getObjectType());
			for (int i = 0; i < attributes.size(); i++) {
				
				 if(((String)attributes.get(i)).startsWith("r_") ||
				  ((String)attributes.get(i)).startsWith("i_")){ continue; }
				 
				if (attributes.get(i).equals("object_name")) {
					continue;
				}
				if (attributes.get(i).equals("set_file__")) {
					continue;
				}
				else {
					repoAttributeList.add(attributes.get(i));
				}
			}

		} catch (DDfException e) {
			if (errorMessage == null) {
				errorMessage = new StringBuffer();
			}
			errorMessage.append(e.getMessage());
			getIEMessageUtility().showMessageDialog(e.getMessage(), e.getCause());
		}

	}

	private void showFieldInTable() {
		attributeListfromMetadata = getAttributeOfMetadataFile();
		getIELogger().writeLog("attributeListfromMetadata " + attributeListfromMetadata, IELogger.DEBUG);
//		HashMap mappAttribute = getImportUIInfoBean().getMappedAttributs();
		int i = 0;
		/*
		 * if (mappAttribute != null) { for (Iterator iterator =
		 * mappAttribute.entrySet().iterator(); iterator .hasNext();) {
		 * Map.Entry entry = (Map.Entry) iterator.next(); String key =
		 * entry.getKey().toString();
		 * if(((String)key).equalsIgnoreCase("object_name")){ continue;
		 * }if(((String)key).equalsIgnoreCase("file_source_location__")){
		 * continue; }if(((String)key).equalsIgnoreCase("r_folder_path")){
		 * continue; }if(((String)key).equalsIgnoreCase("is_minor__")){
		 * continue; }if(((String)key).equalsIgnoreCase("Import_Status")){
		 * continue; }if(((String)key).equalsIgnoreCase("Error_Description")){
		 * continue; }if(((String)key).equalsIgnoreCase("Export_Status")){
		 * continue; } defaultTableModel.isCellEditable(i, 0);
		 * if(repoAttributeList.contains(key)){ defaultTableModel.addRow(new
		 * Object[]{key,key}); }else{if
		 * (((String)key).equalsIgnoreCase("object_name")) { }
		 * 
		 * defaultTableModel.addRow(new Object[]{key,null}); } // } i++; } }
		 */
		for (Iterator iterator1 = attributeListfromMetadata.iterator(); iterator1.hasNext();) {
			Object source = (Object) iterator1.next();
			if (((String) source).equalsIgnoreCase("object_name")) {
				continue;
			}
			if (((String) source).equalsIgnoreCase("file_source_location__")) {
				continue;
			}
			if (((String) source).equalsIgnoreCase("r_folder_path")) {
				continue;
			}
			if (((String) source).equalsIgnoreCase("is_minor__")) {
				continue;
			}
			if (((String) source).equalsIgnoreCase("Import_Status")) {
				continue;
			}
			if (((String) source).equalsIgnoreCase("Error_Description")) {
				continue;
			}
			if (((String) source).equalsIgnoreCase("Export_Status")) {
				continue;
			}

//			defaultTableModel.isCellEditable(i, 0);
			if (repoAttributeList.contains(source)) {
				defaultTableModel.addRow(new Object[] { source, source });
			} else {
				defaultTableModel.addRow(new Object[] { source, null });
			}
			// }
			i++;
		}
	}

	private void showattrInCombo() {
		attribute_JComboBox.removeAllItems();

		attribute_JComboBox.addItem("");
		for (int i = 0; i < repoAttributeList.size(); i++) {
			attribute_JComboBox.addItem(repoAttributeList.get(i));
		}

		fieldMapping_JTable.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(attribute_JComboBox));
	}

	HashMap attributeMap = null;

	@Override
	protected void postValidateAction() {
		super.postValidateAction();
		getIELogger().writeLog("passing postValidateAction", IELogger.DEBUG);
		attributeMap = new HashMap();
		int i = fieldMapping_JTable.getRowCount();
		for (int j = 0; j < i; j++) {
			String attribute1 = (String) fieldMapping_JTable.getModel().getValueAt(j, 0);
			String attribute2 = (String) fieldMapping_JTable.getModel().getValueAt(j, 1);
			attributeMap.put(attribute1, attribute2);
		}
		System.out.println("attributeMap : " + attributeMap);
		int objectHirerchy = getImportUIInfoBean().getObjectHirerchy();
		getIELogger().writeLog("obj hirerchy"+objectHirerchy, IELogger.DEBUG);
		getIELogger().writeLog("Sysobject_or_child "+ImportConstant.SYSOBJECT_OR_CHILD , IELogger.DEBUG);

		if (/*objectHirerchy == ImportConstant.SUPER_THAN_SYSOBJECT ||*/
		 objectHirerchy == ImportConstant.SYSOBJECT_OR_CHILD || objectHirerchy
		 == ImportConstant.DM_FOLDER) {

		attributeMap.put("object_name", "object_name");
		attributeMap.put("file_source_location__", "file_source_location__");
		attributeMap.put("r_folder_path", "r_folder_path");
//		attributeMap.put("is_minor__", "is_minor__");
		
//		 attributeMap.put("In_house_Number", "In_house_Number");
//		 attributeMap.put("Image_Path", "Image_Path");
		}
	}

	@Override
	public void postInilize() {
		getIELogger().writeLog("passing postInilize", IELogger.DEBUG);
		getIELogger().writeLog("attributeMap : " + attributeMap, IELogger.DEBUG);
		getImportUIInfoBean().setMappedAttributs(attributeMap);
	}

	@Override
	protected void preInilize() {
		boolean fileExistanceFlag = checkFileExistance(getImportUIInfoBean().getSourceFileLocation());
		if (!fileExistanceFlag) {
			if (errorMessage == null) {
				errorMessage = new StringBuffer();
			}
			getIEMessageUtility().showMessageDialog(errorMessage.toString(), null);
			errorMessage.append("Source input File does not exist");
			errorMessage = null;
		} else {
			
			int i = ((DefaultTableModel) fieldMapping_JTable.getModel()).getRowCount();
			for (int j = 0; j < i; j++) {
				((DefaultTableModel) fieldMapping_JTable.getModel()).removeRow(0);
			}
			getIELogger().writeLog("Passing preinilize of IFieldMapping", IELogger.DEBUG);
			getAttributefromRepository();
			showattrInCombo();
			showFieldInTable();
		}
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

	private List getAttributeOfMetadataFile() {

		List attributeListfromMetadata = new ArrayList();

		for (Iterator iterator = getImportUIInfoBean().getSelectedAttributesOfMetadata().entrySet().iterator(); iterator.hasNext();) {
			Map.Entry entry = (Map.Entry) iterator.next();
			String key = entry.getKey().toString();

			attributeListfromMetadata.add(key);
		}
		return attributeListfromMetadata;
	}

	@Override
	public StringBuffer validateUIInputs() {
		errorMessage= null;
		int i = fieldMapping_JTable.getRowCount();
		List repoMappedattribute = new ArrayList();
		List metaMappedAttr = new ArrayList();

		for (int j = 0; j < i; j++) {
			String attribute1 = (String) fieldMapping_JTable.getModel().getValueAt(j, 0);
			String attribute2 = (String) fieldMapping_JTable.getModel().getValueAt(j, 1);
			if (metaMappedAttr.contains(attribute1)) {
				if (errorMessage == null) {
					errorMessage = new StringBuffer();
				}
				errorMessage.append(attribute1 + " already added.");
			} else {
				if ("".equals(attribute1) || attribute1 == null) {
					if (errorMessage == null) {
						errorMessage = new StringBuffer();
					}
					errorMessage.append(" Source attribute can not blank.");
				}
			}

			if ("".equals(attribute2) || attribute2 == null) {
				if (errorMessage == null) {
					errorMessage = new StringBuffer();
				}
				errorMessage.append(" Attribute " + attribute1 + " does not has maping.");
			} else if (repoMappedattribute.contains(attribute2)) {
				if (errorMessage == null) {
					errorMessage = new StringBuffer();
				}
				errorMessage.append(" Destination " + attribute2 + " already added.");
			}

			if (errorMessage == null) {
				boolean check = true; // checkDatatype(attribute1,
				// attribute2);
				if (!check) {
					errorMessage = new StringBuffer();
					errorMessage.append(" DataType mismatch of attribute " + attribute1);
				}
			}
		}
		return errorMessage;
	}

	@Override
	public String getImageLocation() {
		imageLocation = "/com/daffodil/documentumie/iebusiness/pimport/view/images/heading_i_fields.jpg";
		return imageLocation;
	}

	@Override
	public String getShowMessage() {
		showMessage = "Provide the source field mapping to destination fields.";
		return showMessage;
	}

	private ImportUIInfoBean getImportUIInfoBean() {
		return (ImportUIInfoBean) getUiInfoBean();
	}

	// method to check the datatype of excel attributes and mapped attributes of
	// repository
	private boolean checkDatatype(String source, String dest) {
		boolean flag = false;
		String sourceDatatype = (String) getImportUIInfoBean().getSelectedAttributesOfMetadata().get(source);
		int inputDataType = 0;
		if (sourceDatatype.equalsIgnoreCase("VARCHAR")) {
			inputDataType = ImportConstant.VARCHAR;
		}
		if (sourceDatatype.equalsIgnoreCase("CHAR")) {
			inputDataType = ImportConstant.CHAR;
		}
		if (sourceDatatype.equalsIgnoreCase("NUMBER")) {
			inputDataType = ImportConstant.NUMBER;
		}
		if (sourceDatatype.equalsIgnoreCase("INEGER")) {
			inputDataType = ImportConstant.VARCHAR;
		}
		if (sourceDatatype.equalsIgnoreCase("FLOAT")) {
			inputDataType = ImportConstant.FLOAT;
		}
		if (sourceDatatype.equalsIgnoreCase("DOUBLE")) {
			inputDataType = ImportConstant.DOUBLE;
		}
		if (sourceDatatype.equalsIgnoreCase("CURRENCY")) {
			inputDataType = ImportConstant.CURRENCY;
		}
		if (sourceDatatype.equalsIgnoreCase("DATETIME")) {
			inputDataType = ImportConstant.DATETIME;
		}
		if (sourceDatatype.equalsIgnoreCase("LOGICAL")) {
			inputDataType = ImportConstant.LOGICAL;
		}

		int destDatatype = 0;

		try {
			destDatatype = getCSServiceProvider().getDataType(dest, getImportUIInfoBean().getObjectType());
		} catch (DDfException e) {
			getIELogger().writeLog(e.getMessage() + e.getCause(), IELogger.DEBUG);
		}
		if ((destDatatype == getCSServiceProvider().STRING || destDatatype == getCSServiceProvider().DMID || destDatatype == getCSServiceProvider().UNDEFINED) && (inputDataType == ImportConstant.VARCHAR || inputDataType == ImportConstant.CHAR)) {
			flag = true;
		} else {
			if ((destDatatype == getCSServiceProvider().INTEGER || destDatatype == getCSServiceProvider().DOUBLE) && (inputDataType == ImportConstant.NUMBER || inputDataType == ImportConstant.INTEGER || inputDataType == ImportConstant.FLOAT || inputDataType == ImportConstant.DOUBLE)) {
				flag = true;
			} else {
				if (destDatatype == getCSServiceProvider().TIME && inputDataType == ImportConstant.DATETIME) {
					flag = true;
				} else {
					if (destDatatype == getCSServiceProvider().BOOLEAN && inputDataType == ImportConstant.LOGICAL) {
						flag = true;
						// TODO check for boolean for csv
					} else {
						flag = false;

					}
				}
			}
		}
		return flag;
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed" desc="Generated
	// Code">//GEN-BEGIN:initComponents
	private void initComponents() {
		java.awt.GridBagConstraints gridBagConstraints;
		remove_JButton = new DButton() {
			protected int getButtonWidth() {
				return 10;
			}
		};
		defaultTableModel = new DefaultTableModel() {

			public boolean isCellEditable(int row, int column) {
				if (column == 0) {
					return false;
				}
				return super.isCellEditable(row, column);
			}

		};
		defaultTableModel.addColumn("Source");
		defaultTableModel.addColumn("Destination");

		jScrollPane = new javax.swing.JScrollPane();
		fieldMapping_JTable = new javax.swing.JTable(defaultTableModel);
		attribute_JComboBox = new javax.swing.JComboBox();

		setPreferredSize(new java.awt.Dimension(370, 355));
		setLayout(new java.awt.GridBagLayout());

		fieldMapping_JTable.setOpaque(false);

		jScrollPane.setViewportView(fieldMapping_JTable);

		// *************************************************************

		// *************************************************************
		remove_JButton.setText("X");
		remove_JButton.setPreferredSize(new java.awt.Dimension(15, 15));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.anchor = gridBagConstraints.NORTH;
		gridBagConstraints.insets = new java.awt.Insets(38, 0, 0, 10);
		add(remove_JButton, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(20, 20, 20, 10);
		add(jScrollPane, gridBagConstraints);
	}// </editor-fold>//GEN-END:initComponents

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JTable fieldMapping_JTable;
	private javax.swing.JScrollPane jScrollPane;
	private javax.swing.table.DefaultTableModel defaultTableModel;
	private javax.swing.JComboBox attribute_JComboBox;
	private DButton remove_JButton;

	// End of variables declaration//GEN-END:variables
	@Override
	protected StringBuffer errorMessage() {
		return errorMessage;
	}

}
