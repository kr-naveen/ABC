/*
 * EAttributeChooseUIControl.java
 *
 * Created on 09 July 2008, 12:08
 */

package com.daffodil.documentumie.iebusiness.export.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import com.daffodil.documentumie.dctm.exception.DDfException;
import com.daffodil.documentumie.iebusiness.AbstractUIControl;
import com.daffodil.documentumie.iebusiness.DButton;
import com.daffodil.documentumie.iebusiness.export.bean.ExportUIInfoBean;

/**
 * 
 * @author Administrator
 */
public class EAttributeChooseUIControl extends AbstractUIControl {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	String imageLocation;

	String showMessage;
	StringBuffer errorMessage;
	/** Creates new form EAttributeChooseUIControl */
	public EAttributeChooseUIControl() {
		initComponents();
		installLocalComponentListener();
	}

	private void installLocalComponentListener() {
		add_JButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				Object[] objects = availableAttr_JList1.getSelectedValues();

				if(AvailableListModel.getSize() == 0){
					getIEMessageUtility().showMessageDialog("Available Attribute List is blank.", null);
				}
				else{
					if (objects == null || objects.length == 0) {
						getIEMessageUtility().showMessageDialog("No Attribute Selected", null);
					} else {
						int sizeselected = selectedAttr_JList.getModel().getSize();
						if (sizeselected == 0) {
							for (int filenameIndex = 0; filenameIndex < objects.length; filenameIndex++) {
								SelectedListModel
								.addElement(objects[filenameIndex]);
								AvailableListModel
								.removeElement(objects[filenameIndex]);
							}
						} else {
							for (int filenameIndex = 0; filenameIndex < objects.length; filenameIndex++) {
								SelectedListModel
								.addElement(objects[filenameIndex]);
								AvailableListModel
								.removeElement(objects[filenameIndex]);
							}

						}

					}
				}

			}
		});

		addAll_JButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {

				int size = AvailableListModel.getSize();
				if(size == 0){
					getIEMessageUtility().showMessageDialog("Available Attribute List is blank.", null);
				}else{
					Object element;
					for (int i = 0; i < size; i++) {
						element = AvailableListModel.getElementAt(i);
						SelectedListModel.addElement(element);
					}
					AvailableListModel.clear();
				}
			}
		});

		remove_JButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				Object[] objects = selectedAttr_JList.getSelectedValues();
				if(SelectedListModel.getSize() == 0){
					getIEMessageUtility().showMessageDialog("Selected Attribute List is blank.", null);
				}else{
					if (objects == null || objects.length == 0) {
						getIEMessageUtility().showMessageDialog("No Attribute selected.", null);
					} else {
						int sizeselected = availableAttr_JList1.getModel()
						.getSize();

						if (sizeselected == 0) { // if selected list is empty
							for (int filenameIndex = 0; filenameIndex < objects.length; filenameIndex++) {
								AvailableListModel
								.addElement(objects[filenameIndex]);
								SelectedListModel
								.removeElement(objects[filenameIndex]);
							}
						}

						else {
							for (int filenameIndex = 0; filenameIndex < objects.length; filenameIndex++) {
								AvailableListModel
								.addElement(objects[filenameIndex]);
								SelectedListModel
								.removeElement(objects[filenameIndex]);
							}

						}

					}
				}
			}
		});

		removeAll_JButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {

				int size = SelectedListModel.getSize();
				if(size == 0){
					getIEMessageUtility().showMessageDialog("Selected Attribute List is blank.", null);
				}else{
					Object element;
					for (int i = 0; i < size; i++) {
						element = SelectedListModel.getElementAt(i);
						AvailableListModel.addElement(element);
					}
					SelectedListModel.clear();
				}
			}
		});

		up_JButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				int[] downIndices = selectedAttr_JList.getSelectedIndices();
				Object[] downElements = selectedAttr_JList.getSelectedValues();
				if(SelectedListModel.getSize() == 0){
					getIEMessageUtility().showMessageDialog("Selected Attribute List is blank.", null);
				}else{
					if(downIndices.length==0){
						getIEMessageUtility().showMessageDialog("Select atleast one attribute.", null);
					}else{
						if (selectedAttr_JList.getSelectedIndex() == 0) {
							getIEMessageUtility().showMessageDialog("Attribute can not be moved further UP", null);
						} else {
							int index_up = downIndices[0] - 1;
							Object element_up = SelectedListModel
							.getElementAt(index_up);
							for (int i = 0; i < downElements.length; i++) {
								SelectedListModel.set(downIndices[i], element_up);
								SelectedListModel.set(index_up, downElements[i]);
								index_up++;
							}
							// a for loop to make values selected after they are moved
							// up
							for (int i = 0; i < downElements.length; i++)
								selectedAttr_JList.setSelectedValue(downElements[i],true);
						}

					}
				}
			}
		});

		down_JButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				int[] upIndices = selectedAttr_JList.getSelectedIndices();
				Object[] upElements = selectedAttr_JList.getSelectedValues();
				int[] selectedIndexes = selectedAttr_JList.getSelectedIndices();
				if(SelectedListModel.getSize() == 0){
					getIEMessageUtility().showMessageDialog("Selected Attribute List is blank.", null);
				}else{
					if(upIndices.length==0){
						getIEMessageUtility().showMessageDialog("Spasswordelect atleast one attribute", null);
					}else{				
						if (selectedIndexes[selectedIndexes.length - 1] == SelectedListModel.getSize() - 1) {
							getIEMessageUtility().showMessageDialog("Attribute can not be moved further DOWN", null);
						} else {
							int index_down = upIndices[upElements.length - 1] + 1;
							Object element_down = SelectedListModel
							.getElementAt(index_down);
							for (int i = upElements.length - 1; i >= 0; i--) {
								SelectedListModel.set(upIndices[i], element_down);
								SelectedListModel.set(index_down, upElements[i]);
								index_down--;
							}
							// a for loop to make values selected after they are moved
							// down
							for (int i = 0; i < upElements.length; i++)
								selectedAttr_JList.setSelectedValue(upElements[i], true);
						}

					}
				}
			}
		});
	}


	@Override
	public String getImageLocation() {
		if (imageLocation == null) {
			imageLocation = "/com/daffodil/documentumie/iebusiness/export/view/images/heading_e_choose_attr.jpg";
		}
		return imageLocation;
	}

	@Override
	public String getShowMessage() {
		if (showMessage == null) {
			showMessage = "Select Attributes.";
		}
		return showMessage;
	}

	List selectedAttributes;

	protected void postValidateAction(){
		List availableAttributes = new ArrayList();
		selectedAttributes = new ArrayList();

		for (int i = 0; i < AvailableListModel.getSize(); i++) {
			availableAttributes.add(AvailableListModel.get(i));
		}
		getExportUIInfoBean().setAvailableAttribute(availableAttributes);//why ? no any need

		selectedAttributes.add("r_object_id");
		for (int i = 0; i < SelectedListModel.getSize(); i++) {
			if(SelectedListModel.get(i).equals("r_object_id")){
				continue;
			}if(SelectedListModel.get(i).equals("set_file__")){
				continue;
			}
			selectedAttributes.add(SelectedListModel.get(i));
		}
	}

	@Override
	protected void postInilize() {
		getExportUIInfoBean().setSelectedAttribute(selectedAttributes);
		//getExportUIInfoBean().setAvailableAttribute(availableAttr_JScrollPane1)
		//AvailableListModel.removeAllElements();

	}

	private void showAttributesInList(){
		SelectedListModel.removeAllElements();
		try {
			List availableAttributes = getCSServiceProvider().getAttributes(
					getExportUIInfoBean().getObjectType());
			AvailableListModel.removeAllElements();
			for (int i = 0; i < availableAttributes.size(); i++) {
				if(availableAttributes.get(i).equals("r_object_id")){
					continue;
				}
				AvailableListModel.addElement(availableAttributes.get(i));
			}
			errorMessage = null;
		} catch (DDfException e) {
			if(errorMessage == null){
				errorMessage = new StringBuffer();
			}
			errorMessage.append(e.getMessage()+e.getCause());
			getIEMessageUtility().showMessageDialog(e.getMessage(), e.getCause());
		}
	}
	@Override
	protected void preInilize() {
		List avilableAttr = getExportUIInfoBean().getAvailableAttribute();
		List selectedAttr = getExportUIInfoBean().getSelectedAttribute();
		if(selectedAttr!=null){
			SelectedListModel.removeAllElements();
			if(avilableAttr!=null){
				AvailableListModel.removeAllElements();

				for (int i = 0; i < avilableAttr.size(); i++) {
					AvailableListModel.addElement(avilableAttr.get(i));
				}
			}
			for (int i = 0; i < selectedAttr.size(); i++) {
				if(selectedAttr.get(i).equals("r_object_id")){
					continue;
				}  //TODO checkS
				SelectedListModel.addElement(selectedAttr.get(i));
		}
		}else{
			showAttributesInList();
		}
	}

	@Override
	protected StringBuffer validateUIInputs() {
		StringBuffer attributeChooseErrorMessage = null;
		if (SelectedListModel.getSize() == 0) {
			if (attributeChooseErrorMessage == null) {
				attributeChooseErrorMessage = new StringBuffer();
				attributeChooseErrorMessage.append(" Please Choose");
			}
			attributeChooseErrorMessage.append(" Selected attribute");
		}
		return attributeChooseErrorMessage;
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

		AvailableListModel = new DefaultListModel();
		SelectedListModel = new DefaultListModel();

		addRemoveButton_JPanel = new javax.swing.JPanel();
		add_JButton = new DButton(){
			protected int getButtonWidth() {
				return 50;
			}};
			remove_JButton = new DButton(){
				protected int getButtonWidth() {
					return 50;
				}
			};
			addAll_JButton = new DButton(){
				protected int getButtonWidth() {
					return 50;
				}
			};
			removeAll_JButton = new DButton(){
				protected int getButtonWidth() {
					return 50;
				}
			};
			availableAttr_JPanel = new javax.swing.JPanel();
			availableAttr_JScrollPane1 = new javax.swing.JScrollPane();
			availableAttr_JList1 = new javax.swing.JList(AvailableListModel);
			selectedAttr_JPanel = new javax.swing.JPanel();
			selectedAttr_JScrollPane = new javax.swing.JScrollPane();
			selectedAttr_JList = new javax.swing.JList(SelectedListModel);
			upDownButton_JPanel = new javax.swing.JPanel();
			up_JButton = new DButton(){
				protected int getButtonWidth() {
					return 50;
				}
			};
			down_JButton = new DButton(){
				protected int getButtonWidth() {
					return 50;
				}
			};

			setPreferredSize(new java.awt.Dimension(330, 355));
			setLayout(new java.awt.GridBagLayout());
			setOpaque(false);

			addRemoveButton_JPanel.setOpaque(false);
			addRemoveButton_JPanel
			.setPreferredSize(new java.awt.Dimension(55, 115));
			addRemoveButton_JPanel.setLayout(new java.awt.GridBagLayout());

			add_JButton.setText(">");
			add_JButton
			.setPreferredSize(new java.awt.Dimension(50, 20));
			gridBagConstraints = new java.awt.GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
			addRemoveButton_JPanel.add(add_JButton, gridBagConstraints);

			remove_JButton.setText("<");
			remove_JButton.setPreferredSize(new java.awt.Dimension(50, 20));
			gridBagConstraints = new java.awt.GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 2;
			gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
			addRemoveButton_JPanel.add(remove_JButton, gridBagConstraints);

			addAll_JButton.setText(">>");
			addAll_JButton.setPreferredSize(new java.awt.Dimension(50, 20));
			gridBagConstraints = new java.awt.GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 1;
			gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
			addRemoveButton_JPanel.add(addAll_JButton, gridBagConstraints);

			removeAll_JButton.setText("<<");
			removeAll_JButton.setPreferredSize(new java.awt.Dimension(50, 20));
			gridBagConstraints = new java.awt.GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 3;
			gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
			addRemoveButton_JPanel.add(removeAll_JButton, gridBagConstraints);

			gridBagConstraints = new java.awt.GridBagConstraints();
			gridBagConstraints.gridx = 2;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
			add(addRemoveButton_JPanel, gridBagConstraints);

			availableAttr_JPanel.setOpaque(false);
			availableAttr_JPanel.setPreferredSize(new java.awt.Dimension(95, 200));
			availableAttr_JPanel.setLayout(new java.awt.GridBagLayout());

			availableAttr_JScrollPane1.setPreferredSize(new java.awt.Dimension(95,

					200));

			availableAttr_JScrollPane1.setViewportView(availableAttr_JList1);

			gridBagConstraints = new java.awt.GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;

			availableAttr_JPanel
			.add(availableAttr_JScrollPane1, gridBagConstraints);

			gridBagConstraints = new java.awt.GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.gridwidth = 2;
			gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
			add(availableAttr_JPanel, gridBagConstraints);

			selectedAttr_JPanel.setOpaque(false);
			selectedAttr_JPanel.setLayout(new java.awt.GridBagLayout());

			selectedAttr_JScrollPane.setPreferredSize(new java.awt.Dimension(95,
					200));

			selectedAttr_JScrollPane.setViewportView(selectedAttr_JList);

			gridBagConstraints = new java.awt.GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
			selectedAttr_JPanel.add(selectedAttr_JScrollPane, gridBagConstraints);

			gridBagConstraints = new java.awt.GridBagConstraints();
			gridBagConstraints.gridx = 3;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.gridwidth = 2;
			gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
			add(selectedAttr_JPanel, gridBagConstraints);

			upDownButton_JPanel.setOpaque(false);
			upDownButton_JPanel.setPreferredSize(new java.awt.Dimension(55, 100));
			upDownButton_JPanel.setLayout(new java.awt.GridBagLayout());

			up_JButton.setText("/\\");
			up_JButton.setPreferredSize(new java.awt.Dimension(50, 20));

			gridBagConstraints = new java.awt.GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
			upDownButton_JPanel.add(up_JButton, gridBagConstraints);

			down_JButton.setText("\\/");
			down_JButton.setPreferredSize(new java.awt.Dimension(50, 20));

			gridBagConstraints = new java.awt.GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 1;
			gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
			upDownButton_JPanel.add(down_JButton, gridBagConstraints);

			gridBagConstraints = new java.awt.GridBagConstraints();
			gridBagConstraints.gridx = 5;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 0);
			add(upDownButton_JPanel, gridBagConstraints);
	}// </editor-fold>

	// Variables declaration - do not modify
	private DButton addAll_JButton;

	private javax.swing.JPanel addRemoveButton_JPanel;

	private DButton add_JButton;

	private javax.swing.JList availableAttr_JList1;

	private javax.swing.JPanel availableAttr_JPanel;

	private javax.swing.JScrollPane availableAttr_JScrollPane1;

	private DButton down_JButton;

	private DButton removeAll_JButton;

	private DButton remove_JButton;

	private javax.swing.JList selectedAttr_JList;

	private javax.swing.DefaultListModel AvailableListModel;

	private javax.swing.JPanel selectedAttr_JPanel;

	private javax.swing.DefaultListModel SelectedListModel;

	private javax.swing.JScrollPane selectedAttr_JScrollPane;

	private javax.swing.JPanel upDownButton_JPanel;

	private DButton up_JButton;

	@Override
	protected StringBuffer errorMessage() {
		// TODO Auto-generated method stub
		return errorMessage;
	}

	// End of variables declaration

}
