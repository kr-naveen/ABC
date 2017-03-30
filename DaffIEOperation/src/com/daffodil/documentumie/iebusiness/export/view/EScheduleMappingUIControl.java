package com.daffodil.documentumie.iebusiness.export.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.daffodil.documentumie.fileutil.logger.IELogger;
import com.daffodil.documentumie.fileutil.metadata.apiimpl.XMLMetadataReader;
import com.daffodil.documentumie.fileutil.metadata.exception.DMetadataReadException;
import com.daffodil.documentumie.iebusiness.AbstractUIControl;
import com.daffodil.documentumie.iebusiness.DButton;
import com.daffodil.documentumie.iebusiness.export.bean.ExportUIInfoBean;
import com.daffodil.documentumie.scheduleie.configurator.DaffIESchedularConfigurator;

public class EScheduleMappingUIControl extends AbstractUIControl 
{
	
	String imageLocation;
	String showMessage;
	List repoAttributeList;
	List attributeListfromMetadata;
	StringBuffer errorMessage;
	DaffIESchedularConfigurator config = new DaffIESchedularConfigurator();

	XMLMetadataReader mtrdr = new XMLMetadataReader(config.getDocumentumHome()
			.replace("\\", "/")
			+ "/DaffIE/", "EScheduleConfigBean");

	// DaffIESchedularConfigurator config=new DaffIESchedularConfigurator();

	/**
	 * Creates new form IFieldMappingUI Control
	 * 
	 * @throws DMetadataReadException
	 */

	public EScheduleMappingUIControl() 
	{
		System.out.println("Export Schedule modify");
		initComponents();
		initlizeUI();
	}

	@Override
	protected void initUI() {
		super.initUI();
		attribute_JComboBox = new JComboBox();
	}

	private String getSecondryXmlFile() {
		String userDir = config.getDocumentumHome().replace("\\", "/");
		String fileDir = userDir + "/DaffIE";
		String fileName = fileDir + "/" + "ScheduleExportConfigBean.xml";
		System.out.println("Secondry file name" + fileName);
		return fileName;
	}

	protected void installListener() {
		remove_JButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				int row[] = fieldMapping_JTable.getSelectedRows();
				String selectedAttr = null;
				System.out.println("fieldMapping_JTable size : "+ fieldMapping_JTable.getRowCount());
				for (int i = row.length - 1; i >= 0; i--) 
				{
					System.out.println("selected row no " + row[i]);
					selectedAttr = (String) fieldMapping_JTable.getModel().getValueAt(i, 0);
					defaultTableModel.removeRow(row[i]);
					removeDataFromXML(row[i]);
				}
				fieldMapping_JTable.setModel(defaultTableModel);
			}

			private void removeDataFromXML(int index) {
				String filepath = mtrdr.returnFileName();
				String secondFilePath = getSecondryXmlFile();
				System.out.println("FilePath" + filepath);
				mtrdr.removeXmlDataFromFile(index, filepath);
				mtrdr.removeXmlDataFromFile(index, secondFilePath);

			}
			
		});
	}

	// method to get the all available attribute of selected object type from
	// repository

	private void showFieldInTable() {

		/**
		 * 1.rows count 2.values of the rows
		 * 
		 */
		List list = new ArrayList();
		System.out.println("Total no of rows" + mtrdr.getRowsCountExXML());

		list = mtrdr.getExRowsXML();
		for (int i = 0; i < mtrdr.getRowsCountExXML(); i++) {
			HashMap list1 = new HashMap();
			list1 = (HashMap) list.get(i);
			Object[] ObjectX = (Object[]) list1.values().toArray(new Object[0]);

			defaultTableModel.addRow(ObjectX);
		}

	}

	HashMap attributeMap = null;

	@Override
	protected void postValidateAction() 
	{
		super.postValidateAction();
		getIELogger().writeLog("passing postValidateAction", IELogger.DEBUG);
		attributeMap = new HashMap();
		int i = fieldMapping_JTable.getRowCount();
	}

	@Override
	public void postInilize() {
		getIELogger().writeLog("passing postInilize", IELogger.DEBUG);
		getIELogger()
				.writeLog("attributeMap : " + attributeMap, IELogger.DEBUG);
	//	getImportUIInfoBean().setMappedAttributs(attributeMap);
	}

	@Override
	protected void preInilize() 
	{
	
			int i = ((DefaultTableModel) fieldMapping_JTable.getModel())
					.getRowCount();
			for (int j = 0; j < i; j++)
			{
				((DefaultTableModel) fieldMapping_JTable.getModel())
						.removeRow(0);
			}
			/*getIELogger().writeLog("Passing preinilize of IFieldMapping",
					IELogger.DEBUG);*/
			
			showFieldInTable();
		
	}

	@Override
	public StringBuffer validateUIInputs() {
		errorMessage = null;
		int i = fieldMapping_JTable.getRowCount();
		List repoMappedattribute = new ArrayList();
		List metaMappedAttr = new ArrayList();

		for (int j = 0; j < i; j++) {
			String attribute1 = (String) fieldMapping_JTable.getModel()
					.getValueAt(j, 0);
			String attribute2 = (String) fieldMapping_JTable.getModel()
					.getValueAt(j, 1);
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
				errorMessage.append(" Attribute " + attribute1
						+ " does not has maping.");
			} else if (repoMappedattribute.contains(attribute2)) {
				if (errorMessage == null) {
					errorMessage = new StringBuffer();
				}
				errorMessage.append(" Destination " + attribute2
						+ " already added.");
			}

			if (errorMessage == null) {
				boolean check = true; // checkDatatype(attribute1,
				// attribute2);
				if (!check) {
					errorMessage = new StringBuffer();
					errorMessage.append(" DataType mismatch of attribute "
							+ attribute1);
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
		showMessage = "Modify Schedule.";
		return showMessage;
	}

	private ExportUIInfoBean getImportUIInfoBean() 
	{
		return (ExportUIInfoBean) getUiInfoBean();
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed" desc="Generated
	// Code">//GEN-BEGIN:initComponents
	private HashMap getTableColumns() {

		// XMLMetadataReader mtrdr=new
		// XMLMetadataReader("E:\\Harsh Crop Project\\Schedule Task\\DaffIE\\"
		// ,"IScheduleConfigBean");
		HashMap hmap = new HashMap();

		hmap = mtrdr.getExColumnXml();
		return hmap;
	}

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
		HashMap hmap = getTableColumns();
		
		for (Iterator iterator = hmap.entrySet().iterator(); iterator.hasNext();) {
			Map.Entry entry = (Map.Entry) iterator.next();
			String key = entry.getKey().toString();
			
			defaultTableModel.addColumn(key);
		
		}
		jScrollPane = new javax.swing.JScrollPane();
		fieldMapping_JTable = new javax.swing.JTable(defaultTableModel);
		fieldMapping_JTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
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
