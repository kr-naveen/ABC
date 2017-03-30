package com.daffodil.documentumie.iebusiness;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.daffodil.documentumie.fileutil.logger.IELogger;
import com.daffodil.documentumie.iebusiness.export.view.ExportMainUIControl;
import com.daffodil.documentumie.iebusiness.pimport.view.ImportMainUIControl;

public class ImportExportMain {

	private IEIntroduction mIEIntroduction;

	private ImportMainUIControl mImportMainUIControl;

	private ExportMainUIControl mExportMainUIControl;

	private IEMainFrame mainFrame = null;

	private IEMessageUtility util ; 
	public ImportExportMain() {
		mainFrame = new IEMainFrame();
		
		configureMessageUtil();
		configureIntroUI();
	}

	private void configureMessageUtil(){
		util = new IEMessageUtility(mainFrame); 
	}
	
	/************ method to show the selection option import/export ******************/
	private void configureIntroUI() {
		mIEIntroduction = new IEIntroduction();
		mIEIntroduction.addPropertyChangeListener("opSelected",
				new PropertyChangeListener() {

					public void propertyChange(PropertyChangeEvent evt) {
						boolean bool = ((Boolean) evt.getNewValue())
								.booleanValue();
						if (bool) {
							mIEIntroduction.postInilize();
							String str = mIEIntroduction.getOperation();
							if (str.equalsIgnoreCase("Import")) {
								initImportUIAndShow();
							} else {
								if (str.equalsIgnoreCase("Export")) {									
									initExportUIAndShow();
								}

							}
							mIEIntroduction.setOpSelected(false);
						}

					}
				});
	}

	/***************  method to Initialize the Import UI ***********************/
	private void initImportUIAndShow() {
		if (mImportMainUIControl == null) {
			mImportMainUIControl = new ImportMainUIControl();
			mImportMainUIControl.setIEMessageUtil(util);
			mImportMainUIControl.setMainFrame(mainFrame);
			mImportMainUIControl.getIELogger().writeLog("Import UI Initlized", IELogger.DEBUG);
			mImportMainUIControl.addPropertyChangeListener("uIIndex",
					new PropertyChangeListener() {
						public void propertyChange(PropertyChangeEvent arg0) {
							int val = ((Integer) arg0.getNewValue()).intValue() ;
							if (0 > val) {
								mImportMainUIControl.getIELogger().writeLog("UIIndex "+val, IELogger.DEBUG);
								mainFrame.showUIControl(mIEIntroduction,"Daffodil Import-Export");
							}
						}
					});
			
			mImportMainUIControl.registerActionListener(new ActionListener(){

				public void actionPerformed(ActionEvent e) {
					if (e.getActionCommand().equalsIgnoreCase("Finish")) {
						mImportMainUIControl = null;
					}
				}});
		}

		mImportMainUIControl.showUI();
		mainFrame.showUIControl(mImportMainUIControl, "Daffodil Import Process");
	}
	
	/***************  method to Initialize the Export UI ***********************/
	private void initExportUIAndShow() {
		if (mExportMainUIControl == null) {
			mExportMainUIControl = new ExportMainUIControl();
			mExportMainUIControl.setIEMessageUtil(util);
			mExportMainUIControl.setMainFrame(mainFrame);
		//	mExportMainUIControl.getIELogger().writeLog("Export UI Initlized     ", IELogger.DEBUG);
			mExportMainUIControl.addPropertyChangeListener("uIIndex",
					new PropertyChangeListener() {
						public void propertyChange(PropertyChangeEvent arg0) {
							int val = ((Integer) arg0.getNewValue()).intValue() ;
							if (0 > val) {
//								mExportMainUIControl.getIELogger().writeLog("UIIndex "+val, IELogger.DEBUG);
								mainFrame.showUIControl(mIEIntroduction,"Daffodil Import-Export");
							}
						}
					});
			
			mExportMainUIControl.registerActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					if (e.getActionCommand().equalsIgnoreCase("Finish")) {
						mExportMainUIControl = null;
					}
				}});
		}

		mExportMainUIControl.showUI();
		mainFrame.showUIControl(mExportMainUIControl, "Daffodil Export Process");
	}

	/***************  method to show the UI  ***********************/
	public void showIEUI(int i) {
//		if(i == 1){ // COmmeted as we are not taking care of licenses.
		mainFrame.showUIControl(mIEIntroduction, "Daffodil Import-Export");
		//System.out.println("Image Path"+this.getClass().getResource("/com/daffodil/documentumie/iebusiness/images/daffodilLogo.jpg"));
		ImageIcon imageIcon = new ImageIcon(this.getClass().getResource("/com/daffodil/documentumie/iebusiness/images/daffodilLogo.jpg"));
		//System.out.println("Image Path"+this.getClass().getResource("/com/daffodil/documentumie/iebusiness/images/daffodilLogo.jpg"));
		Image image = imageIcon.getImage();
		mainFrame.setIconImage(image);
//		}else{
//			util.showMessageDialog("Your evaluation period has been expired.", null);
//			System.exit(0);
//		}
	}

	public ExportMainUIControl getExportMainUIControl() {
		return mExportMainUIControl;
	}

	public void setExportMainUIControl(ExportMainUIControl val) {
		this.mExportMainUIControl = val;
	}

	public IEIntroduction getIEIntroduction() {
		return mIEIntroduction;
	}

	public void setIEIntroduction(IEIntroduction val) {
		this.mIEIntroduction = val;
	}

	public ImportMainUIControl getImportMainUIControl() {
		return mImportMainUIControl;
	}

	public void setImportMainUIControl(ImportMainUIControl val) {

		this.mImportMainUIControl = val;
	}

	private JFrame getMainFrame() {
		return mainFrame;
	}

}
