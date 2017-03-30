package com.daffodil.documentumie.iebusiness;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class IEMessageUtility {
/** Return value from class method if YES is chosen. */
private static final int YES_OPTION = 0;
/** Return value from class method if NO is chosen. */
private static final int NO_OPTION = 1;

private int action ; 
private IEMessageDialog dialog ;
private JDialog mainDialog; 
private JFrame mainFrame ;
private javax.swing.JTable fieldMapping_JTable;

	/** Creates new form IEMessageUtil */
	public IEMessageUtility(JFrame frame) {
		mainFrame = frame;
		init();
		installListener();
	}
	public IEMessageUtility(javax.swing.JTable Mapping_JTable) 
	{
		fieldMapping_JTable = Mapping_JTable;
		init();
		installListener();
	}

	private void init(){
		dialog = new IEMessageDialog();
	}
	
	private void installListener() {
		dialog.registerListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				if("yes".equalsIgnoreCase(e.getActionCommand())){
					action = YES_OPTION;	
					mainDialog.setVisible(false);
				}else if("no".equalsIgnoreCase(e.getActionCommand())){
					action = NO_OPTION;
					mainDialog.setVisible(false);
				}else if("ok".equalsIgnoreCase(e.getActionCommand())){
					mainDialog.setVisible(false);
				}else{
					String text = ((JButton) e.getSource()).getText();
					if(">>".equalsIgnoreCase(text)){
					mainDialog.setPreferredSize(new java.awt.Dimension(mainDialog.getWidth(), 300));
					}else{
					mainDialog.setPreferredSize(new java.awt.Dimension(mainDialog.getWidth(), 100));
					}
					
					dialog.doDetailWork();
					mainDialog.pack();
					mainDialog.repaint();
					mainDialog.setVisible(true);
				}
			}});
	}
	
	public void showMessageDialog(String msg, Throwable detail){
		dialog.setMessage(msg, detail);
		
		int size = msg.length();
		if(detail== null){
			dialog.setLevel(1);
			dialog.setImage("/com/daffodil/documentumie/iebusiness/images/exclamation-down.jpg");
			showDialog("Message.",size,1);
		}else{
			dialog.setLevel(2);
			dialog.setImage("/com/daffodil/documentumie/iebusiness/images/error.jpg");
			showDialog("Error.",size,2);
		}
		
	}

	public int showConfirmationMessageDialog(String msg){
		int size = msg.length();
		dialog.setMessage(msg, null);
		dialog.setLevel(3);
		dialog.setImage("/com/daffodil/documentumie/iebusiness/images/question.jpg");
		showDialog("Confirmation.",size,3);
		return action;
	}

	private void showDialog (String titl, int a,int b){
		if(mainDialog==null){
			mainDialog = new JDialog(mainFrame,titl);
			mainDialog.setModal(true);
			mainDialog.setMinimumSize(new java.awt.Dimension(210, 100));
			mainDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
			mainDialog.setResizable(false);
		}
		
		mainDialog.setMaximumSize(new java.awt.Dimension(800,300));
		int cal = a*5;
		System.out.println("msg length : "+a+" "+cal);
		int row=100;
//		if(a>100){
//			cal=250;
//			row=a;
//		}
		mainDialog.getContentPane().add(dialog);
		
		if(b==2){
			mainDialog.setPreferredSize(new java.awt.Dimension((cal+160),row));
			mainDialog.setMaximumSize(new java.awt.Dimension(500,300));
		}else{
		mainDialog.setPreferredSize(new java.awt.Dimension((cal+110),row));
		}
		
		mainDialog.pack();
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) (dim.getWidth() - mainDialog.getWidth());
		int y = (int) (dim.getHeight() - mainDialog.getHeight());
		mainDialog.setLocation(x / 2, y / 2);
		mainDialog.setVisible(true);
		
//		dialog.setMainDialog(mainDialog);
	}
}


