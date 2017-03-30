/*
 * EFilterCriteriaUIControl.java
 *
 * Created on 09 July 2008, 12:46
 */

package com.daffodil.documentumie.iebusiness.export.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import com.daffodil.documentumie.dctm.exception.DDfException;
import com.daffodil.documentumie.iebusiness.AbstractUIControl;
import com.daffodil.documentumie.iebusiness.DButton;
import com.daffodil.documentumie.iebusiness.export.bean.ExportUIInfoBean;
import com.daffodil.documentumie.iebusiness.export.model.ExportConstant;

/**
 * 
 * @author Administrator
 */
public class EFilterCriteriaUIControl extends AbstractUIControl {

	/**
	 * 
	 */
	public EFilterCriteriaUIControl() {
		initComponents();
		initlizeUI();
	}

	private static final long serialVersionUID = 1L;
	String BEGINS_WITH = "Begins With";
	String CONTAINS = "Contains";
	String DOES_NOT_CONTAINS = "Does Not Contains";
	String ENDS_WITH = "Ends With";
	String GREATOR_THAN = "Greater Than(>)";
	String GREATOR_THAN_OR_EQUAL = "Greater Than or Equal(>=)";
	String NOT_EQUAL = "Not Equal(!=)";
	String EQUAL = "Equal(=)";
	String LESS_THAN = "Less Than (<)";
	String LESS_THAN_OR_EQUAL = "Less Than or Equal(<=)";
	String NULL = "Null";
	String NOT_NULL = "Not Null";
	String IN="IN";
	private FilterParm currentFilterParm;

	/** Creates new form EFilterCriteriaUIControl */

	@Override
	protected void initUI() {
		super.initUI();
		prepareSearchCriteriaInputs(operator1_JComboBox);
		prepareSearchCriteriaInputs(operator2_JComboBox);
		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(and_JRadioButton);
		buttonGroup.add(or_JRadioButton);
		showAndOrInCombo();
		// and_JRadioButton.setSelected(true);
	}

	protected void installListener() {
//		attribute1_JComboBox.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent evt) {
//				// attribute1SelectionListener();
//			}
//		});

//		operator1_JComboBox.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent evt) {
//				// operator1SelectionListener();
//			}
//		});
//
//		attribute2_JComboBox.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent evt) {
//				// attribute2SelectionListener();
//			}
//		});
//
//		operator2_JComboBox.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent evt) {
//				// operator2SelectionListener();
//			}
//		});
//
//		criteria1_JTextField.addPropertyChangeListener("value", new PropertyChangeListener() {
//
//			public void propertyChange(PropertyChangeEvent evt) {
//				// active2Criteriasecond();
//			}
//		});

		and_JRadioButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				attribute2_JComboBox.setEnabled(true);
				operator2_JComboBox.setEnabled(true);
				criteria2_JTextField.setEnabled(true);
			}
		});

		or_JRadioButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				attribute2_JComboBox.setEnabled(true);
				operator2_JComboBox.setEnabled(true);
				criteria2_JTextField.setEnabled(true);
			}
		});

		add2Table_JButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				String msg = currentFilterParm.validateInputs();
				if (msg != null) {
					showMessageDialog(msg);
					// attribute1_JComboBox.setSelectedItem(null);
					// attribute2_JComboBox.setSelectedItem(null);
					// operator1_JComboBox.setSelectedItem(null);
					// operator2_JComboBox.setSelectedItem(null);
				} else {
					addRowTable(currentFilterParm);
					int index = currentFilterParm.getIndex();
					currentFilterParm = new FilterParm();
					currentFilterParm.setIndex((index + 1));
					and_JRadioButton.setSelected(true);
					attribute1_JComboBox.setSelectedItem(null);
					attribute2_JComboBox.setSelectedItem(null);
					operator1_JComboBox.setSelectedItem(null);
					operator2_JComboBox.setSelectedItem(null);
					criteria1_JTextField.setText("");
					criteria2_JTextField.setText("");
					and_JRadioButton.setSelected(false);
					or_JRadioButton.setSelected(false);
				}
			}

			private void addRowTable(FilterParm filterParm) {
				if (filterParm.getIndex() == 0) {
					filterParm.setRowOperator(null);
				} 
//				else {
//					filterParm.setRowOperator("AND");
//					showAndOrInCombo();
//				}
				
				ArrayList list = getExportUIInfoBean().getFilterParam();
				if (filterParm.getIndex() == list.size()) {
					if(filterParm.getIndex() != 0){
						filterParm.setRowOperator("AND");
						}
					list.add(filterParm);
					((DefaultTableModel) criteria_JTable.getModel()).addRow(new Object[] { filterParm.getIndex(), filterParm.getRowOperator(), filterParm.getSQL() });
				} else {
					((DefaultTableModel) criteria_JTable.getModel()).removeRow(filterParm.getIndex());
					((DefaultTableModel) criteria_JTable.getModel()).insertRow(filterParm.getIndex(), new Object[] { filterParm.getIndex(), filterParm.getRowOperator(), filterParm.getSQL() });
				}
				and_JRadioButton.setSelected(false);
				or_JRadioButton.setSelected(false);
			}
		});

		edit_JButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				edit2TableData();
			}
		});

		delete_JButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				deleteFromTable();
				and_JRadioButton.setSelected(false);
				or_JRadioButton.setSelected(false);
			}
		});

		deleteAll_JButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				int selectedRow = criteria_JTable.getRowCount();
				if (selectedRow != -1) {
					int i = getIEMessageUtility().showConfirmationMessageDialog("Are you sure ? You want to delete all rows from table.");
					if (i == JOptionPane.YES_OPTION) {
						deleteAllFromTable();
						and_JRadioButton.setSelected(false);
						or_JRadioButton.setSelected(false);
					}
				} else {
					showMessageDialog("No any row in Table.");
				}
			}
		});
		
		andOrOption_JComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				int selectedRow = criteria_JTable.getSelectedRow();
				if (selectedRow != -1) {
					FilterParm parm = (FilterParm) getExportUIInfoBean().getFilterParam().get(selectedRow);
					parm.setRowOperator((String)andOrOption_JComboBox.getSelectedItem());
				}
			}
		});
	}

	private void showAndOrInCombo() {
		andOrOption_JComboBox.removeAllItems();
		andOrOption_JComboBox.addItem("AND");
		andOrOption_JComboBox.addItem("OR");
		criteria_JTable.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(andOrOption_JComboBox));
	}

	private void edit2TableData() {
		attribute1_JComboBox.setEnabled(true);
		attribute1_JComboBox.setSelectedItem("");
		operator1_JComboBox.setEnabled(true);
		operator1_JComboBox.setSelectedItem("");
		criteria1_JTextField.setEnabled(true);
		criteria1_JTextField.setText("");
		if (criteria_JTable.getRowCount() == 0) {
			showMessageDialog("No data in table.");
		} else {
			int selectedRow = criteria_JTable.getSelectedRow();
			if (selectedRow != -1) {

				FilterParm parm = (FilterParm) getExportUIInfoBean().getFilterParam().get(selectedRow);
				setCurrentFilterParm(parm);
				attribute1_JComboBox.setSelectedItem(parm.getOperand1());
				attribute1_JComboBox.setEnabled(true);
				operator1_JComboBox.setSelectedItem(parm.getOperator1());
				operator1_JComboBox.setEnabled(true);
				criteria1_JTextField.setText(parm.getValue1());
				criteria1_JTextField.setEnabled(true);

				attribute2_JComboBox.setSelectedItem(parm.getOperand2());
				attribute2_JComboBox.setEnabled(true);
				operator2_JComboBox.setSelectedItem(parm.getOperator2());
				operator2_JComboBox.setEnabled(true);
				criteria2_JTextField.setText(parm.getValue2());
				criteria2_JTextField.setEnabled(true);

				if ("AND".equalsIgnoreCase(parm.getLogicalOperation())) {
					and_JRadioButton.setSelected(true);
				} else {
					or_JRadioButton.setSelected(true);
				}
			} else {
				showMessageDialog("Select a row.");
			}
		}
	}

	private void deleteFromTable() {
		if (criteria_JTable.getRowCount() == 0) {
			showMessageDialog("No data in table.");
		} else {
			int selectedRow = criteria_JTable.getSelectedRow();
			if (selectedRow != -1) {
				ArrayList rows;
				rows = getExportUIInfoBean().getFilterParam();
				int size = rows.size();

				rows.remove(selectedRow);
				((DefaultTableModel) criteria_JTable.getModel()).removeRow(selectedRow);

				if (getCurrentFilterParm().getIndex() == selectedRow) {
					FilterParm parm1 = new FilterParm();
					parm1.setIndex(size - 1);
					parm1.setRowOperator("AND");
					setCurrentFilterParm(parm1);
				} else {
					getCurrentFilterParm().setIndex(getCurrentFilterParm().getIndex() - 1);
				}

				if (selectedRow < (size - 1)) {
					for (int j = 0; j < (size - 1); j++) {
						FilterParm parm1 = (FilterParm) rows.get(j);
						if (j == 0) {
							parm1.setRowOperator(null);
							((DefaultTableModel) criteria_JTable.getModel()).setValueAt(null, j, 1);
						}
						int index;
						index = parm1.getIndex();
						if (selectedRow < index) {
							parm1.setIndex(index - 1);
							((DefaultTableModel) criteria_JTable.getModel()).setValueAt(index - 1, j, 0);
						}
					}
				}
				attribute1_JComboBox.setSelectedItem("");
				attribute1_JComboBox.setEnabled(true);
				operator1_JComboBox.setSelectedItem("");
				operator1_JComboBox.setEnabled(true);
				criteria1_JTextField.setText("");
				criteria1_JTextField.setEnabled(true);
				attribute2_JComboBox.setSelectedItem("");
				attribute2_JComboBox.setEnabled(false);
				operator2_JComboBox.setSelectedItem("");
				operator2_JComboBox.setSelectedItem(null);
				operator2_JComboBox.setEnabled(false);
				criteria2_JTextField.setText("");
				criteria2_JTextField.setEnabled(false);
				and_JRadioButton.setSelected(false);
				or_JRadioButton.setSelected(false);
			} else {
				showMessageDialog("No row selected.");
			}
		}
	}

	private void deleteAllFromTable() {
//		if (criteria_JTable.getRowCount() == 0) {
//			showMessageDialog("No data in table.");
//		} else {
//			int selectedRow = criteria_JTable.getSelectedRow();
//			if (selectedRow != -1) {
//				List list = getExportUIInfoBean().getFilterParam();
//				for (int j = 0; j < list.size(); j++) {
//					((DefaultTableModel) criteria_JTable.getModel()).removeRow(0);
//				}
//				list.clear();
		List list = getExportUIInfoBean().getFilterParam();
		for (int j = 0; j < list.size(); j++) {
			((DefaultTableModel) criteria_JTable.getModel()).removeRow(0);
		}
		list.clear();
				attribute1_JComboBox.setSelectedItem("");
				attribute1_JComboBox.setEnabled(true);
				operator1_JComboBox.setSelectedItem("");
				operator1_JComboBox.setEnabled(true);
				criteria1_JTextField.setText("");
				criteria1_JTextField.setEnabled(true);
				attribute2_JComboBox.setSelectedItem("");
				attribute2_JComboBox.setEnabled(false);
				operator2_JComboBox.setSelectedItem("");
				operator2_JComboBox.setEnabled(false);
				criteria2_JTextField.setText("");
				criteria2_JTextField.setEnabled(false);
				and_JRadioButton.setSelected(false);
				or_JRadioButton.setSelected(false);
				getCurrentFilterParm().setIndex(0);
	}
//
//	private void attribute1SelectionListener() {
//		if (attribute1_JComboBox.getSelectedItem() == null || "".equalsIgnoreCase((String) attribute1_JComboBox.getSelectedItem())) {
//			operator1_JComboBox.setSelectedItem("");
//			operator1_JComboBox.setSelectedItem(null);
//			operator1_JComboBox.setEnabled(false);
//			criteria1_JTextField.setText("");
//			criteria1_JTextField.setEnabled(false);
//			attribute2_JComboBox.setSelectedItem("");
//			attribute2_JComboBox.setEnabled(false);
//			operator2_JComboBox.setSelectedItem("");
//			operator2_JComboBox.setEnabled(false);
//			criteria2_JTextField.setText("");
//			criteria2_JTextField.setEnabled(false);
//		} else {
//			operator1_JComboBox.setEnabled(true);
//		}
//	}
//
//	private void operator1SelectionListener() {
//		if (operator1_JComboBox.getSelectedItem() == null) {
//			criteria1_JTextField.setText("");
//			criteria1_JTextField.setEnabled(false);
//		} else {
//			if (operator1_JComboBox.getSelectedItem().toString().compareToIgnoreCase("NULL") == 0 || operator1_JComboBox.getSelectedItem().toString().compareToIgnoreCase("Not Null") == 0) {
//				criteria1_JTextField.setEnabled(false);
//				criteria1_JTextField.setText("");
//				attribute2_JComboBox.setEnabled(true);
//			} else {
//				criteria1_JTextField.setEnabled(true);
//			}
//		}
//	}
//
//	private void active2Criteriasecond() {
//		if (criteria1_JTextField.getText() == null || "".equalsIgnoreCase(criteria1_JTextField.getText())) {
//			operator2_JComboBox.setSelectedItem("");
//			operator2_JComboBox.setEnabled(false);
//			criteria2_JTextField.setText("");
//			criteria2_JTextField.setEnabled(false);
//		} else {
//			attribute2_JComboBox.setEnabled(true);
//			// criteria2_JTextField.setText("");
//		}
//	}
//
//	private void operator2SelectionListener() {
//		if (operator2_JComboBox.getSelectedItem() == null) {
//			criteria2_JTextField.setText("");
//			criteria2_JTextField.setEnabled(false);
//		} else {
//			if (operator2_JComboBox.getSelectedItem().toString().compareToIgnoreCase("NULL") == 0 || operator2_JComboBox.getSelectedItem().toString().compareToIgnoreCase("Not Null") == 0) {
//				criteria2_JTextField.setText("");
//				criteria2_JTextField.setEnabled(false);
//			} else {
//				criteria2_JTextField.setEnabled(true);
//			}
//		}
//	}
//
//	private void attribute2SelectionListener() {
//		if (attribute2_JComboBox.getSelectedItem() == null) {
//			operator2_JComboBox.setEnabled(false);
//			criteria2_JTextField.setText("");
//			criteria2_JTextField.setEnabled(false);
//		} else {
//			operator2_JComboBox.setEnabled(true);
//			criteria2_JTextField.setText("");
//		}
//	}

	private void prepareSearchCriteriaInputs(JComboBox operator) {
		operator.removeAllItems();
		operator.addItem(null);
		operator.addItem(BEGINS_WITH);
		operator.addItem(CONTAINS);
		// operator.addItem("Does Not Contains");
		operator.addItem(ENDS_WITH);
		operator.addItem(GREATOR_THAN);
		operator.addItem(GREATOR_THAN_OR_EQUAL);
		operator.addItem(NOT_EQUAL);
		operator.addItem(EQUAL);
		operator.addItem(LESS_THAN);
		operator.addItem(LESS_THAN_OR_EQUAL);
		operator.addItem(NULL);
		operator.addItem(NOT_NULL);
		operator.addItem(IN);
	}

	private ExportUIInfoBean getExportUIInfoBean() {
		return (ExportUIInfoBean) getUiInfoBean();
	}

	@Override
	public String getImageLocation() {

		String imageLocation = "/com/daffodil/documentumie/iebusiness/export/view/images/heading_filter.jpg";
		return imageLocation;
	}

	@Override
	public String getShowMessage() {
		String showMessage = "Specify the filter criteria to filter the importing contents form \nmetadata file.";
		return showMessage;
	}

	StringBuffer sql = new StringBuffer();

	@Override
	protected void postValidateAction() {
		/*
		 * super.postValidateAction();
		 * 
		 * int i = criteria_JTable.getRowCount(); for (int j = 0; j <i; j++) {
		 * if(j>0){ sql.append((String) criteria_JTable.getModel().getValueAt(j,
		 * 1)); } sql.append((String) criteria_JTable.getModel().getValueAt(j,
		 * 2)); }
		 */

		super.postValidateAction();
		sql = new StringBuffer();
		/*
		 * int i = criteria_JTable.getRowCount(); for (int j = 0; j < i; j++) {
		 * if (j > 0) { sql.append((String)
		 * criteria_JTable.getModel().getValueAt( j, 1)); } sql.append((String)
		 * criteria_JTable.getModel().getValueAt(j, 2)); }
		 */
		ArrayList list = getExportUIInfoBean().getFilterParam();
		for (int i = 0; i < list.size(); i++) {
			FilterParm filterParm = (FilterParm) list.get(i);
			if (i == 0) {
				sql.append(" (");
				sql.append(filterParm.getSQL());
				sql.append(") ");
			} else {
				sql.append(filterParm.getRowOperator());
				sql.append(" (");
				sql.append(filterParm.getSQL());
				sql.append(") ");
			}
		}

	}

	@Override
	protected void postInilize() {
		getExportUIInfoBean().setDqlText(sql.toString());
	}

	@Override
	protected void preInilize() {
		currentFilterParm = new FilterParm();
		List list = getExportUIInfoBean().getFilterParam();
		if (list != null) {
			DefaultTableModel tableModel = ((DefaultTableModel) criteria_JTable.getModel());
			// for (int i = 0; i < tableModel.getRowCount(); i++) {
			for (int j = (tableModel.getRowCount() - 1); j >= 0; j--) {
				tableModel.removeRow(j);
			}

			currentFilterParm.setIndex(list.size());
			for (int i = 0; i < list.size(); i++) {
				FilterParm parm = (FilterParm) list.get(i);
				Object[] row = new Object[] { parm.getIndex(), parm.getRowOperator(), parm.getSQL() };
				((DefaultTableModel) criteria_JTable.getModel()).addRow(row);
			}
		} else {
			currentFilterParm.setIndex(0);
			getExportUIInfoBean().setFilterParam(new ArrayList());
		}

//		currentFilterParm.setRowOperator("AND");

		attributeShow();
		attribute1_JComboBox.setEnabled(true);
		attribute1_JComboBox.setSelectedItem("");
		operator1_JComboBox.setEnabled(true);
		operator1_JComboBox.setSelectedItem(null);
		criteria1_JTextField.setText("");
		criteria1_JTextField.setEnabled(true);
		attribute2_JComboBox.setEnabled(false);
		attribute2_JComboBox.setSelectedItem(null);
		operator2_JComboBox.setEnabled(false);
		operator2_JComboBox.setSelectedItem(null);
		criteria2_JTextField.setEnabled(false);
		criteria2_JTextField.setText("");
	}

	private void attributeShow() {
		attribute1_JComboBox.removeAllItems();
		attribute2_JComboBox.removeAllItems();

		attribute1_JComboBox.addItem("");
		attribute2_JComboBox.addItem("");
		List selectedAttributes = getExportUIInfoBean().getSelectedAttribute();

		for (Iterator iter = selectedAttributes.iterator(); iter.hasNext();) {
			String element = (String) iter.next();
			if ("r_object_id".equalsIgnoreCase(element)) {
				continue;
			}
			attribute1_JComboBox.addItem(element);
			attribute2_JComboBox.addItem(element);
		}
	}

	@Override
	protected StringBuffer validateUIInputs() {
		/*StringBuffer filercriteriaerror=null;
		if(criteria_JTable.getRowCount() == 0)
		{
			filercriteriaerror=new StringBuffer();
			filercriteriaerror.append("Please Select filter criteria ");
		}*/
		return null;
	}

	private class FilterParm {
		private int index;

		private String rowOperator;

		private String operand1;

		private String operand2;

		private String operator1;

		private String operator2;

		private String value1;

		private String value2;

		private String SQL;

		private String logicalOperation;

		public FilterParm() {

		}

		public FilterParm(int index, String operand1, String operand2, String operator1, String operator2, String value1, String value2) {
			super();
			this.index = index;
			this.operand1 = operand1;
			this.operand2 = operand2;
			this.operator1 = operator1;
			this.operator2 = operator2;
			this.value1 = value1;
			this.value2 = value2;
		}

		public String validateInputs() {
			String msg = null;
			String attribute1 = (String) attribute1_JComboBox.getSelectedItem();
			String operator1 = (String) operator1_JComboBox.getSelectedItem();
			String criteria1 = criteria1_JTextField.getText();
			
			if(criteria1.contains(",")){
                String[] addedStr = criteria1.split(",");
                String newStr = "";
                for(int i=0; i<addedStr.length; i++)
                {
                    System.out.println(addedStr[i]);
                    String modCriteria = "";
                    if(i==0)
                    {
                        modCriteria = addedStr[i]+"'"+",";
                    }else if(i==(addedStr.length-1))
                    {
                        modCriteria = "'"+addedStr[i];
                    }else
                    {
                        modCriteria = "'"+addedStr[i]+"'"+",";
                    }
                   
                    //System.out.println("Modified Criteria is  :"+modCriteria);
                   
                    newStr = newStr.concat(modCriteria);
                   
                }
                criteria1 = newStr;
                System.out.println("Final Criteria : "+ criteria1);
            }
			String logicalOption = (and_JRadioButton.isSelected() ? "AND" : "OR");

			String attribute2 = (String) attribute2_JComboBox.getSelectedItem();
			String operator2 = (String) operator2_JComboBox.getSelectedItem();
			String criteria2 = criteria2_JTextField.getText();

			String filter1 = "";
			if (!("".equals(attribute1) || attribute1 == null)) {
				if (operator1 != null && !"".equalsIgnoreCase(operator1)) {
					if (!(operator1.equalsIgnoreCase("NULL") || operator1.equalsIgnoreCase("NOT NULL"))) {
						if ((criteria1 != null && !criteria1.trim().equalsIgnoreCase(""))) {
							msg = checkDataType(attribute1, criteria1);
							if (filter1 == null) {
								filter1 = "";
							}
							filter1 = attribute1 + getFilter(attribute1, operator1, criteria1);
						} else {
							if (msg == null) {
								msg = "";
							}
							msg = "Provide first criteria value ";
						}
					} else {
						if (filter1 == null) {
							filter1 = "";
						}
						filter1 = attribute1 + getFilter(attribute1, operator1, criteria1);
					}
				} else {
					if (msg == null) {
						msg = "";
					}
					msg = "Select first operator";
				}
			} else {
				if (msg == null) {
					msg = "";
				}
				msg = "Select first attribute";
			}

			String filter2 = null;

			if (!("".equals(attribute2) || attribute2 == null)) {// TODO
																	// after
																	// listener
																	// on
																	// criteria
																	// u have to
																	// cahnge
																	// this code
																	// as itis
				if (operator2 != null && !"".equalsIgnoreCase(operator2)) {
					if (!(operator2.equalsIgnoreCase("NULL") || operator2.equalsIgnoreCase("NOT NULL"))) {
						if ((criteria2 != null && !criteria2.trim().equalsIgnoreCase(""))) {
							if (filter2 == null) {
								filter2 = "";
							}
							filter2 = attribute2 + getFilter(attribute2, operator2, criteria2);
						} else {
							if (msg == null) {
								msg = "";
							}
							msg = "Provide second criteria value ";
						}
					} else {
						if (filter2 == null) {
							filter2 = "";
						}
						filter2 = attribute2 + getFilter(attribute2, operator2, criteria2);
					}
				} else {
					if (msg == null) {
						msg = "";
					}
					msg = "Select second operator";
				}
			} /*
				 * else { //***************************************************
				 * check if (msg == null) { msg = ""; } msg = "Select second
				 * attribute"; }
				 */
			if (msg != null) {
				return msg;
			}
			if (filter1 == null && filter2 == null) {
				return "Invalid filter";
			}
			if ((filter1 != null) && (filter2 != null)) {
				getCurrentFilterParm().setSQL(("(" + filter1 + " " + logicalOption + " " + filter2 + ")"));
			}

			if (filter1 != null && filter2 == null) {
				setSQL(("(" + filter1 + ")"));
			}

			if (filter1 == null && filter2 != null) {
				setSQL(("(" + filter2 + ")"));
			}
			setOperand1(attribute1);
			setOperand2(attribute2);

			setOperator1(operator1);
			setOperator2(operator2);

			setValue1(criteria1);
			setValue2(criteria2);

			setLogicalOperation(logicalOption);

			return null;
		}

		private String checkDataType(String attr, String criteria) {
			int attr_dataType = 0;
			String errorMessage = null;
			boolean status;
			try {
				attr_dataType = getCSServiceProvider().getDataType(attr, getExportUIInfoBean().getObjectType());
			} catch (DDfException e) {
				errorMessage = e.getMessage();
			}
			switch (attr_dataType) {
			case ExportConstant.BOOLEAN:
				boolean data_typeBool;
				if (criteria.compareToIgnoreCase("False") != 0) {
					/**
					 * Parsing String "False" returns false if Value entered is
					 * "false" then it is considered to be Valid
					 */
					data_typeBool = Boolean.valueOf(criteria);
					if (!data_typeBool) {
						status = false;
						errorMessage = "DataType mismatch, please provide value of same dataType as of attribute selected";
						getMainUIControl().showMessageDialog("Datatype of \"" + attr + "\" and " + criteria + " is incompatible. ");
					}
				} else
					break;

			case ExportConstant.INTEGER:
				try {
					Integer.valueOf(criteria);
				} catch (NumberFormatException numExp) {
					status = false;
					errorMessage = "DataType mismatch, please provide value of same dataType as of attribute selected";
					getMainUIControl().showMessageDialog("Datatype of " + attr + " and " + criteria + "is incompatible. ");
				}
				break;

			case ExportConstant.STRING:
				// String data_typeString;
				/**
				 * Value provided is always String
				 */
				break;

			case ExportConstant.DMID:
				if (criteria.length() != 16) {
					status = false;
					errorMessage = "DataType mismatch, please provide value of same dataType as of attribute selected";
					getMainUIControl().showMessageDialog("Datatype of " + attr + " and " + criteria + "is incompatible. ");
				} else {

				}
				break;

			case ExportConstant.TIME:
				try {
					Date.parse(criteria);
				} catch (RuntimeException e) {
					status = false;
					errorMessage = "DataType mismatch, please provide value of same dataType as of attribute selected";
					getMainUIControl().showMessageDialog("Datatype of " + attr + " and " + criteria + "is incompatible. ");
				}
				break;

			case ExportConstant.DOUBLE:
				try {
					Double.valueOf(criteria);
				} catch (NumberFormatException numExp) {
					status = false;
					errorMessage = "DataType mismatch, please provide value of same dataType as of attribute selected";
					getMainUIControl().showMessageDialog("Datatype of " + attr + " and " + criteria + "is incompatible. ");
				}
				break;
			default:// DM_UNDEFINED
				status = false;
				errorMessage = "DataType mismatch, please provide value of same dataType as of attribute selected";
				getMainUIControl().showMessageDialog("Datatype of " + attr + " and " + criteria + "is incompatible. ");
				break;
			}

			return errorMessage;

		}

		private String getFilter(String operand, String operator, String value) {
			if (NULL.equalsIgnoreCase(operator)) {
				return " is null";
			} else if (NOT_NULL.equalsIgnoreCase(operator)) {
				return " != null";
			} else if (EQUAL.equalsIgnoreCase(operator)) {
				return " = '" + value + "'";
			} else if (NOT_EQUAL.equalsIgnoreCase(operator)) {
				return " != '" + value + "'";
			} else if (GREATOR_THAN.equalsIgnoreCase(operator)) {
				return " > '" + value + "'";
			} else if (GREATOR_THAN_OR_EQUAL.equalsIgnoreCase(operator)) {
				return " >= '" + value + "'";
			} else if (LESS_THAN.equalsIgnoreCase(operator)) {
				return " < '" + value + "'";
			} else if (LESS_THAN_OR_EQUAL.equalsIgnoreCase(operator)) {
				return " <= '" + value + "'";
			} else if (ENDS_WITH.equalsIgnoreCase(operator)) {
				return " like '%" + value + "'";
			} else if (BEGINS_WITH.equalsIgnoreCase(operator)) {
				return " like '" + value + "%'";
			} else if (CONTAINS.equalsIgnoreCase(operator)) {
				return " like '%" + value + "%'";
			}
			else if(IN.equalsIgnoreCase(operator))
			{
				//String sepratevalue="";
				return " in ('"+value+"')";
			}
			// else if (DOES_NOT_CONTAINS.equalsIgnoreCase(operator)){
			// return " ";
			// }
			return null;
		}

		public int getIndex() {
			return index;
		}

		public void setIndex(int index) {
			this.index = index;
		}

		public String getOperand1() {
			return operand1;
		}

		public void setOperand1(String operand1) {
			this.operand1 = operand1;
		}

		public String getOperand2() {
			return operand2;
		}

		public void setOperand2(String operand2) {
			this.operand2 = operand2;
		}

		public String getOperator1() {
			return operator1;
		}

		public void setOperator1(String operator1) {
			this.operator1 = operator1;
		}

		public String getOperator2() {
			return operator2;
		}

		public void setOperator2(String operator2) {
			this.operator2 = operator2;
		}

		public String getValue1() {
			return value1;
		}

		public void setValue1(String value1) {
			this.value1 = value1;
		}

		public String getValue2() {
			return value2;
		}

		public void setValue2(String value2) {
			this.value2 = value2;
		}

		public String getSQL() {
			return SQL;
		}

		public void setSQL(String sql) {
			this.SQL = sql;
		}

		public String getRowOperator() {
			return rowOperator;
		}

		public void setRowOperator(String rowOperator) {
			this.rowOperator = rowOperator;
		}

		public String getLogicalOperation() {
			return logicalOperation;
		}

		public void setLogicalOperation(String logicalOperation) {
			this.logicalOperation = logicalOperation;
		}
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed" desc="Generated Code">
	private void initComponents() {
		java.awt.GridBagConstraints gridBagConstraints;
		deafultmodel = new DefaultTableModel() {

			public boolean isCellEditable(int row, int column) {
				if (row != 0 && column == 1) {
					return true;
				} else {
					// return super.isCellEditable(row, column);
					return false;
				}
			}
		};

		deafultmodel.addColumn("Sr.No.");
		deafultmodel.addColumn("Operator");
		deafultmodel.addColumn("Criteria");
		attribute1group_JPanel = new javax.swing.JPanel();
		attribute1_JComboBox = new javax.swing.JComboBox();
		attribute1_JLabel = new javax.swing.JLabel();
		operator1_JComboBox = new javax.swing.JComboBox();
		criteria1_JTextField = new javax.swing.JFormattedTextField();
		andOrButton_JPanel = new javax.swing.JPanel();
		and_JRadioButton = new javax.swing.JRadioButton();
		or_JRadioButton = new javax.swing.JRadioButton();
		attribute2group_JPanel = new javax.swing.JPanel();
		attribute2_JLabel = new javax.swing.JLabel();
		attribute2_JComboBox = new javax.swing.JComboBox();
		operator2_JComboBox = new javax.swing.JComboBox();
		criteria2_JTextField = new javax.swing.JFormattedTextField();
		Add_JPanel = new javax.swing.JPanel();
		add2Table_JButton = new DButton() {
			protected int getButtonWidth() {
				return 80;
			}
		};
		exportFilter_JPanel = new javax.swing.JPanel();
		jScrollPane = new javax.swing.JScrollPane();
		criteria_JTable = new javax.swing.JTable(deafultmodel);
		editDelete_JPanel = new javax.swing.JPanel();
		delete_JButton = new DButton() {
			protected int getButtonWidth() {
				return 80;
			}
		};
		deleteAll_JButton = new DButton() {
			protected int getButtonWidth() {
				return 90;
			}
		};
		edit_JButton = new DButton() {
			protected int getButtonWidth() {
				return 80;
			}
		};
		andOrOption_JComboBox = new JComboBox();

		setMaximumSize(new java.awt.Dimension(370, 355));
		setPreferredSize(new java.awt.Dimension(370, 355));
		setLayout(new java.awt.GridBagLayout());

		attribute1group_JPanel.setOpaque(false);
		attribute1group_JPanel.setPreferredSize(new java.awt.Dimension(330, 21));
		attribute1group_JPanel.setLayout(new java.awt.GridBagLayout());

		attribute1_JComboBox.setMaximumSize(new java.awt.Dimension(50, 20));
		attribute1_JComboBox.setPreferredSize(new java.awt.Dimension(115, 20));
		attribute1_JComboBox.setMinimumSize(new java.awt.Dimension(40, 20));
		attribute1_JComboBox.setBackground(new java.awt.Color(255, 255, 255));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 5.0;
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
		attribute1group_JPanel.add(attribute1_JComboBox, gridBagConstraints);

		attribute1_JLabel.setPreferredSize(new java.awt.Dimension(50, 20));
		attribute1_JLabel.setText("Attribute");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
		attribute1group_JPanel.add(attribute1_JLabel, gridBagConstraints);

		operator1_JComboBox.setMaximumSize(new java.awt.Dimension(50, 20));
		operator1_JComboBox.setPreferredSize(new java.awt.Dimension(95, 20));
		operator1_JComboBox.setMinimumSize(new java.awt.Dimension(40, 20));
		operator1_JComboBox.setBackground(new java.awt.Color(255, 255, 255));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 3.0;
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
		attribute1group_JPanel.add(operator1_JComboBox, gridBagConstraints);

		criteria1_JTextField.setMaximumSize(new java.awt.Dimension(50, 20));
		criteria1_JTextField.setPreferredSize(new java.awt.Dimension(75, 20));
		criteria1_JTextField.setMinimumSize(new java.awt.Dimension(40, 20));
		// criteria1_JTextField.setPreferredSize(new java.awt.Dimension(50,
		// 20));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 3;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
		gridBagConstraints.weightx = 6.0;
		attribute1group_JPanel.add(criteria1_JTextField, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 0);
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		add(attribute1group_JPanel, gridBagConstraints);

		andOrButton_JPanel.setOpaque(false);
		andOrButton_JPanel.setPreferredSize(new java.awt.Dimension(330, 21));
		andOrButton_JPanel.setLayout(new java.awt.GridBagLayout());

		and_JRadioButton.setOpaque(false);
		and_JRadioButton.setPreferredSize(new java.awt.Dimension(80, 20));
		and_JRadioButton.setText("AND");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		gridBagConstraints.weightx = 6.0;
		andOrButton_JPanel.add(and_JRadioButton, gridBagConstraints);

		or_JRadioButton.setOpaque(false);
		or_JRadioButton.setPreferredSize(new java.awt.Dimension(80, 20));
		or_JRadioButton.setText("OR");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.weightx = 10.0;
		andOrButton_JPanel.add(or_JRadioButton, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
		add(andOrButton_JPanel, gridBagConstraints);

		attribute2group_JPanel.setOpaque(false);
		attribute2group_JPanel.setPreferredSize(new java.awt.Dimension(330, 21));
		attribute2group_JPanel.setLayout(new java.awt.GridBagLayout());

		attribute2_JLabel.setPreferredSize(new java.awt.Dimension(50, 20));
		attribute2_JLabel.setText("Attribute");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
		attribute2group_JPanel.add(attribute2_JLabel, gridBagConstraints);

		attribute2_JComboBox.setOpaque(false);
		attribute2_JComboBox.setMaximumSize(new java.awt.Dimension(50, 20));
		attribute2_JComboBox.setPreferredSize(new java.awt.Dimension(115, 20));
		attribute2_JComboBox.setMinimumSize(new java.awt.Dimension(40, 20));
		attribute2_JComboBox.setBackground(new java.awt.Color(255, 255, 255));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 5.0;
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
		attribute2group_JPanel.add(attribute2_JComboBox, gridBagConstraints);

		operator2_JComboBox.setOpaque(false);
		operator2_JComboBox.setMaximumSize(new java.awt.Dimension(50, 20));
		operator2_JComboBox.setPreferredSize(new java.awt.Dimension(95, 20));
		operator2_JComboBox.setMinimumSize(new java.awt.Dimension(40, 20));
		operator2_JComboBox.setBackground(new java.awt.Color(255, 255, 255));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 3.0;
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
		attribute2group_JPanel.add(operator2_JComboBox, gridBagConstraints);

		criteria2_JTextField.setMaximumSize(new java.awt.Dimension(50, 20));
		criteria2_JTextField.setPreferredSize(new java.awt.Dimension(75, 20));
		criteria2_JTextField.setMinimumSize(new java.awt.Dimension(40, 20));
		// criteria2_JTextField.setPreferredSize(new java.awt.Dimension(50,
		// 20));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 3;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
		gridBagConstraints.weightx = 6.0;
		attribute2group_JPanel.add(criteria2_JTextField, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		add(attribute2group_JPanel, gridBagConstraints);

		Add_JPanel.setOpaque(false);
		Add_JPanel.setPreferredSize(new java.awt.Dimension(330, 21));
		Add_JPanel.setLayout(new java.awt.GridBagLayout());

		add2Table_JButton.setText("Add");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.CENTER;
		gridBagConstraints.weightx = 6.0;
		Add_JPanel.add(add2Table_JButton, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new java.awt.Insets(5, 20, 0, 20);
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		add(Add_JPanel, gridBagConstraints);

		exportFilter_JPanel.setOpaque(false);
		exportFilter_JPanel.setPreferredSize(new java.awt.Dimension(330, 180));
		exportFilter_JPanel.setLayout(new java.awt.GridBagLayout());

		jScrollPane.setOpaque(false);
		criteria_JTable.setOpaque(false);
		jScrollPane.setMaximumSize(new java.awt.Dimension(320, 179));
		jScrollPane.setPreferredSize(new java.awt.Dimension(329, 179));
		jScrollPane.setViewportView(criteria_JTable);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.insets = new java.awt.Insets(0, 20, 0, 20);
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		exportFilter_JPanel.add(jScrollPane, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 4.0;
		add(exportFilter_JPanel, gridBagConstraints);

		editDelete_JPanel.setOpaque(false);
		editDelete_JPanel.setPreferredSize(new java.awt.Dimension(330, 21));
		editDelete_JPanel.setLayout(new java.awt.GridBagLayout());

		delete_JButton.setText("Delete");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
		editDelete_JPanel.add(delete_JButton, gridBagConstraints);

		deleteAll_JButton.setText("Delete All");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.weightx = 8.0;
		editDelete_JPanel.add(deleteAll_JButton, gridBagConstraints);

		edit_JButton.setText("Edit");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		gridBagConstraints.weightx = 6.0;
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
		editDelete_JPanel.add(edit_JButton, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 5;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new java.awt.Insets(5, 20, 20, 5);
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		add(editDelete_JPanel, gridBagConstraints);

	}// </editor-fold>

	// Variables declaration - do not modify
	private javax.swing.JPanel Add_JPanel;
	// private javax.swing.JButton add_JButton;
	private DButton add2Table_JButton;
	private javax.swing.JRadioButton and_JRadioButton;
	private javax.swing.JPanel andOrButton_JPanel;
	private javax.swing.JComboBox attribute1_JComboBox;
	private javax.swing.JLabel attribute1_JLabel;
	private javax.swing.JPanel attribute1group_JPanel;
	private javax.swing.JComboBox attribute2_JComboBox;
	private javax.swing.JLabel attribute2_JLabel;
	private javax.swing.JPanel attribute2group_JPanel;
	private javax.swing.JFormattedTextField criteria1_JTextField;
	private javax.swing.JFormattedTextField criteria2_JTextField;
	private javax.swing.JTable criteria_JTable;
	private javax.swing.JButton deleteAll_JButton;
	private javax.swing.JButton delete_JButton;
	private javax.swing.JPanel editDelete_JPanel;
	private javax.swing.JButton edit_JButton;
	private javax.swing.JPanel exportFilter_JPanel;
	private javax.swing.JScrollPane jScrollPane;
	private javax.swing.JComboBox operator1_JComboBox;
	private javax.swing.JComboBox operator2_JComboBox;
	private javax.swing.JRadioButton or_JRadioButton;
	private DefaultTableModel deafultmodel;
	private JComboBox andOrOption_JComboBox;

	public FilterParm getCurrentFilterParm() {
		return currentFilterParm;
	}

	public void setCurrentFilterParm(FilterParm currentFilterParm) {
		this.currentFilterParm = currentFilterParm;
	}

	@Override
	protected StringBuffer errorMessage() {
		return null;
	}
}
