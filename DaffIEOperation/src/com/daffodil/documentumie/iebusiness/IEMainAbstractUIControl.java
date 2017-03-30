package com.daffodil.documentumie.iebusiness;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.daffodil.documentumie.dctm.CSServiceProviderFactory;
import com.daffodil.documentumie.dctm.api.CSServices;
import com.daffodil.documentumie.dctm.exception.DDfException;
import com.daffodil.documentumie.fileutil.configurator.DaffIEConfigurator;
import com.daffodil.documentumie.fileutil.configurator.bean.ConfigBean;
import com.daffodil.documentumie.fileutil.configurator.exception.DConfigWriteException;
import com.daffodil.documentumie.fileutil.logger.IELogger;
import com.daffodil.documentumie.fileutil.metadata.api.MetadataReader;
import com.daffodil.documentumie.fileutil.metadata.api.MetadataWriter;

public abstract class IEMainAbstractUIControl extends JPanel {

	private int uIIndex;
	private MetadataReader metadataReader;
	private MetadataWriter metadataWriter;
	private IELogger iELogger;
	private Object uIInfobean;
	private CSServices cSServiceProvider;
	private IEMessageUtility iEMessageUtility;
	private ConfigBean configBean;
	private int maxIndex = getUICounter();
	protected AbstractUIControl[] abUIControl = new AbstractUIControl[maxIndex];
	private PropertyChangeSupport propertyChangeSupport;
	private IEMainFrame mainFrame;
	ShowLogInformation showLogInformation = null;
	JDialog dialog = null;
	
	private boolean isSchedule = false;
	private boolean isModify=false;
	
	public boolean isModify() {
		return isModify;
	}

	public void setModify(boolean isModify) {
		this.isModify = isModify;
	}

	public boolean isSchedule() {
		return isSchedule;
	}

	public void setSchedule(boolean isSchedule) {
		this.isSchedule = isSchedule;
	}
	
	public IEMainAbstractUIControl() {
		propertyChangeSupport = new PropertyChangeSupport(this);
		initComponents();
		innerInitialize();
		installListener();
		showImages();
	}

	public void setIEMessageUtil(IEMessageUtility util) {
		iEMessageUtility = util;
	}

	protected abstract int getUICounter();

	protected abstract void startProcess();
	
	protected abstract void nextUIControl();
	
	protected abstract void previousUIControl();

	private void installListener() {
		
		updateSchedule_JButton.addActionListener(new ActionListener()
		{

			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				System.out.println("click on modify button");
				abUIControl[uIIndex].hideUI();
				abUIControl[uIIndex].CheckException();
				boolean bool = validateInput(null,null);
				if (bool) 
				{
					setSchedule(false);
					setModify(true);
					nextUIControl();
					setModify(false);
				}
			}
			});

		next_JButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				boolean bool = validateInput(abUIControl[uIIndex].hideUI(),abUIControl[uIIndex].CheckException());

				if (bool) {
					nextUIControl();
				}
			}
		});

		back_JButton.addActionListener(new ActionListener() {     

			public void actionPerformed(ActionEvent arg0) {
				previousUIControl();
			}
		});

		cancel_JButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				int i = promptUserCancelProcess();
				if (i == JOptionPane.YES_OPTION) {
					try {
						getCSServiceProvider().closeSession();
					} catch (DDfException e) {
						getIEMessageUtility().showMessageDialog(e.getMessage(),
								e.getCause());
					}
					System.exit(0);
				}

			}

		});

		start_JButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {

				back_JButton.setVisible(false);
				start_JButton.setVisible(false);
				cancel_JButton.setVisible(false);

				stop_JButton.setVisible(true);
				viewLog_JButton.setVisible(true);
				finish_JButton.setVisible(true);
				schedule_JButton.setVisible(false);
				scheduleProcess_JButton.setVisible(false);
				setStopButtonEnabled(true);
				setViewButtonEnabled(false);
				setFinishButtonEnabled(false);

				boolean bool = validateInput(abUIControl[uIIndex].hideUI(),
						abUIControl[uIIndex].CheckException());
				if (bool) {
					setUIIndex((getUIIndex() + 1));
				}
				startProcess();
			}
		});
		
		schedule_JButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("inside schedule button...............");
				boolean bool = validateInput(abUIControl[uIIndex].hideUI(),
						abUIControl[uIIndex].CheckException());
				if (bool) 
				{
					setModify(false);
					setSchedule(true);
					nextUIControl();
				}
			}
		});

		scheduleProcess_JButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				boolean bool = validateInput(abUIControl[uIIndex].hideUI(),
						abUIControl[uIIndex].CheckException());
				if (bool) {
					int i = promptUserScheduleProcess();
					if (i == JOptionPane.YES_OPTION) {
						showIEIntroUIControl();
					}else{
						System.exit(0);
					}
				}
			}
		});

		addPropertyChangeListener("uIIndex", new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent arg0) {
				showInnerUIControl(((Integer) arg0.getNewValue()).intValue());
			}
		});
	}

	private boolean validateInput(String message, String error) {
		System.out.println(message+" -- "+error);
		if (message != null || error != null) {
			if (message != null) {
				showErrorDialog(message);
			}/*
				 * else{ if(error !=null){ showErrorDialog(error); } }
				 */
			return false;
		}
		return true;
	}

	private void innerInitialize() {
		setUIIndex(-1);

		if (getCSServiceProvider() == null) {
			setCSServiceProvider(CSServiceProviderFactory.getCSServiceProvider("5.3"));
		}

		initialize();
	}

	protected abstract void initialize();

	// protected abstract void setInputInConfigBean();

	public void showUI() {
		setUIIndex(0);
	}

	private void showImages() {
		String imagePath = "/com/daffodil/documentumie/iebusiness/images/ie_documentum.gif";
		GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridheight = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 0);
		documentumImage_JPanel.add(new JLabel(new ImageIcon(this.getClass()
				.getResource(imagePath))), gridBagConstraints);
	}

	private void renderHeadingImage(String path) {

		GridBagConstraints gridBagConstraints = null;
		if (gridBagConstraints == null) {
			gridBagConstraints = new java.awt.GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.gridheight = 1;
			gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints.weightx = 1.0;
			gridBagConstraints.weighty = 1.0;
			gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 0);
		}

		welcomeIE_JPanel.removeAll();
		welcomeIE_JPanel.add(new JLabel(new ImageIcon(this.getClass()
				.getResource(path))), gridBagConstraints);
	}

	protected int getUIIndex() {
		return uIIndex;
	}

	protected void setUIIndex(int index) {
		// maxIndex = index+1;
		int old = this.uIIndex;
		uIIndex = index;
		propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this,
				"uIIndex", old, index));
	}

	/**
	 * Add a property change listener for a specific property.
	 * 
	 * @param propertyName
	 *            The name of the property to listen on.
	 * @param listener
	 *            The <code>PropertyChangeListener</code> to be added.
	 */
	public void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	GridBagConstraints innerUIGridBagConstraints = null;
	AbstractUIControl uiControl;

	/******************* method to show the inner ui on run time ******************/
	private void showInnerUIControl(int ind) {
		System.out.println("index----------------------------"+ind);
		
		if (ind < 0) {
			return;
		}

		if (getUIIndex() == 0) {
			try {
				getCSServiceProvider().closeSession();
			} catch (DDfException e) {
				getIEMessageUtility().showMessageDialog(e.getMessage(),
						e.getCause());
			}
		}

		if (getUIIndex() == (getUICounter() - 5)) {//2
			updateSchedule_JButton.setVisible(false);//true by nity
			next_JButton.setVisible(false);
			start_JButton.setEnabled(true);
			start_JButton.setVisible(true);
			schedule_JButton.setVisible(true);
			schedule_JButton.setEnabled(true);
			scheduleProcess_JButton.setVisible(false);
			scheduleLabel.setVisible(true); //true set by Harsh
		} else {
			if (getUIIndex() >= 0 && getUIIndex() < (getUICounter() - 5)) {//2
				updateSchedule_JButton.setVisible(false);//true by nity
				start_JButton.setVisible(false);
				stop_JButton.setVisible(false);
				viewLog_JButton.setVisible(false);
				finish_JButton.setVisible(false);
				next_JButton.setVisible(true);
				back_JButton.setVisible(true);
				cancel_JButton.setVisible(true);
				schedule_JButton.setVisible(false);
				scheduleProcess_JButton.setVisible(false);
				scheduleLabel.setVisible(false); //false set by Harsh
			}
		}
		
		if (getUIIndex() == 7) {
			updateSchedule_JButton.setVisible(false);//true by nity
			back_JButton.setVisible(true);
			next_JButton.setVisible(true);
			cancel_JButton.setVisible(true);
			start_JButton.setVisible(false);
			schedule_JButton.setVisible(false);
			scheduleProcess_JButton.setVisible(false);
			scheduleLabel.setVisible(false); //false set by Harsh
		} 
		
		if(getUIIndex() == 8){
			updateSchedule_JButton.setVisible(true);//true by nity
			start_JButton.setVisible(false);
			stop_JButton.setVisible(false);
			viewLog_JButton.setVisible(false);
			finish_JButton.setVisible(false);
			next_JButton.setVisible(false);
			back_JButton.setVisible(true);
			cancel_JButton.setVisible(true);
			schedule_JButton.setVisible(false);
			scheduleLabel.setVisible(false); // false set by Harsh
			scheduleProcess_JButton.setVisible(true);  // true set by shubhra
		}
		
		if (getUIIndex()==9) 
		{
			updateSchedule_JButton.setVisible(false);//true by nity
			start_JButton.setVisible(false);
			stop_JButton.setVisible(false);
			viewLog_JButton.setVisible(false);
			finish_JButton.setVisible(false);
			next_JButton.setVisible(false);
			back_JButton.setVisible(true);
			cancel_JButton.setVisible(true);
			schedule_JButton.setVisible(false);
			scheduleLabel.setVisible(false); // false set by Harsh
			scheduleProcess_JButton.setVisible(false);  // true set by shubhra
			
		}
System.out.println("ind----------------------------"+ind+"  Size is"+abUIControl.length);
		uiControl = abUIControl[ind];
		if (uiControl == null) {
			uiControl = getUI(ind);
			uiControl.setMainUIControl(this);
			abUIControl[ind] = uiControl;
		}
		setUiControl(uiControl);
		String msg = uiControl.showIEUI(getUIInfobean());

		if (innerUIGridBagConstraints == null) {
			innerUIGridBagConstraints = new java.awt.GridBagConstraints();
			innerUIGridBagConstraints.gridx = 0;
			innerUIGridBagConstraints.gridy = 0;
			innerUIGridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
			innerUIGridBagConstraints.weightx = 1.0;
			innerUIGridBagConstraints.weighty = 1.0;
			innerUIGridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 0);
		}
		importFlow_JPanel.removeAll();
		importFlow_JPanel.repaint();
		importFlow_JPanel.add(uiControl, innerUIGridBagConstraints);
		// importFlow_JPanel.add(uiControl);

		message_JTextArea.setText(uiControl.getShowMessage());
		renderHeadingImage(uiControl.getImageLocation());
		getMainFrame().invalidate();
		getMainFrame().validate();
		getMainFrame().repaint();
		if (msg != null) {
			showErrorDialog(msg);
		}
	}

	protected abstract AbstractUIControl getUI(int index);

	/**
	 * Remove a property change listener for a specific property.
	 * 
	 * @param propertyName
	 *            The name of the property that was listened on.
	 * @param listener
	 *            The <code>PropertyChangeListener</code> to be removed
	 */
	public void removePropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(propertyName,
				listener);
	}

	public void setViewButtonEnabled(boolean b) {
		viewLog_JButton.setEnabled(b);

	}

	public void setFinishButtonEnabled(boolean b) {
		finish_JButton.setEnabled(b);
	}

	public void registerActionListener(ActionListener listener) {
		stop_JButton.addActionListener(listener);
		viewLog_JButton.addActionListener(listener);
		finish_JButton.addActionListener(listener);
	}

	public int promptUserCancelProcess() {
		return getIEMessageUtility().showConfirmationMessageDialog(
				"Are you sure ? You want to close the Utility.");
	}

	public int promptUserStopImport() {
		return getIEMessageUtility().showConfirmationMessageDialog(
				"Are you sure ? You want to Stop Import process.");
	}

	public int promptUserFinishProcess() {
		return getIEMessageUtility().showConfirmationMessageDialog(
				"Would you like to do more Import or Export ?");
	}

	public int promptUserResumeProcess() {
		return getIEMessageUtility().showConfirmationMessageDialog(
				"Would you like to resume the previous Import Process ?");
	}

	public int promptUserStopExport() {
		return getIEMessageUtility().showConfirmationMessageDialog(
				"Are you sure ? You want to Stop Export process.");
	}

	public void setStopButtonEnabled(boolean b) {
		stop_JButton.setEnabled(b);

	}

	public void showLog(String logfilepath) {

		String log = "";
		String line = null;

		try {
			System.out.println("<-----logfilepath---->"+logfilepath);
			BufferedReader br = null;
			br = new BufferedReader(new FileReader(logfilepath));
			while ((line = br.readLine()) != null) {
				log = log + "\n" + line;
			}
			br.close();
		} catch (IOException e) {
			getIEMessageUtility().showMessageDialog(e.getMessage(),
					e.getCause());
			e.printStackTrace();
		}

		openLogDialog(log);
	}

	private void openLogDialog(String log) {
		if (showLogInformation == null) {
			showLogInformation = new ShowLogInformation() {
				protected void doOnOKPress() {
					dialog.setVisible(false);
				}
			};
		}
		if (dialog == null) {
			dialog = new JDialog(getMainFrame(), "Log File");
		}
		
		showLogInformation.setText(log);

		dialog.getContentPane().add(showLogInformation);
		dialog.setModal(true);
		dialog.pack();
		dialog.setResizable(false);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) (dim.getWidth() - dialog.getWidth());
		int y = (int) (dim.getHeight() - dialog.getHeight());
		dialog.setLocation(x / 2, y / 2);
		dialog.setVisible(true);
	}

	/*
	 * public void setinitialProgressValue(int maxVal) { ((IPerformUIControl)
	 * uiControl).setinitialProgressValue(maxVal); }
	 */

	/*
	 * public void setinitialExportProgressValue(int maxVal) {
	 * ((EPerformUIControl) uiControl).setinitialProgressValue(maxVal); }
	 * 
	 */
	public abstract void renderOperationFinishControl();

	public void showMessageDialog(String msg) {
		getIEMessageUtility().showMessageDialog(msg, null);
	}

	/*
	 * public void updateProgressBar(int val, String fileName) {
	 * ((IPerformUIControl) uiControl).updateProgressBar(val, fileName); }
	 */
	/*
	 * public void updateExportProgressBar(int val, String fileName) {
	 * ((EPerformUIControl) uiControl).updateProgressBar(val, fileName); }
	 */

	private void showErrorDialog(String msg) {
		getIEMessageUtility().showMessageDialog(msg, null);
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	private void initComponents() {
		java.awt.GridBagConstraints gridBagConstraints;

		importMain_JPanel = new javax.swing.JPanel();
		welcomeIE_JPanel = new javax.swing.JPanel();
		documentumImage_JPanel = new javax.swing.JPanel();
		importInfo_JPanel = new javax.swing.JPanel();
		importFlow_JPanel = new javax.swing.JPanel();
		message_JTextArea = new javax.swing.JTextArea();
		importProcess_JPanel = new javax.swing.JPanel();
		scheduleMessage_JPanel=new javax.swing.JPanel();
		back_JButton = new DButton() {

			@Override
			protected int getButtonWidth() {
				return 80;
			}

		};
		next_JButton = new DButton() {

			@Override
			protected int getButtonWidth() {
				return 80;
			}

		};
		cancel_JButton = new DButton() {

			@Override
			protected int getButtonWidth() {
				return 80;
			}

		};
		stop_JButton = new DButton() {

			@Override
			protected int getButtonWidth() {
				return 80;
			}

		};
		viewLog_JButton = new DButton() {

			@Override
			protected int getButtonWidth() {
				return 90;
			}

		};
		finish_JButton = new DButton() {

			@Override
			protected int getButtonWidth() {
				return 80;
			}

		};
		start_JButton = new DButton() {

			@Override
			protected int getButtonWidth() {
				return 80;
			}

		};
		schedule_JButton = new DButton(){

			@Override
			protected int getButtonWidth() {
				return 80;
			}

		};

		scheduleProcess_JButton = new DButton(){

			@Override
			protected int getButtonWidth() {
				return 80;
			}

		};
		
		updateSchedule_JButton=new DButton()
		{

			@Override
			protected int getButtonWidth() {
				// TODO Auto-generated method stub
				return 80;
			}
			
		};

		setLayout(new java.awt.GridBagLayout());
		setPreferredSize(new Dimension(550, 500));

		importMain_JPanel.setBackground(new java.awt.Color(255, 255, 255));
		importMain_JPanel.setLayout(new java.awt.GridBagLayout());

		welcomeIE_JPanel.setOpaque(false);
		welcomeIE_JPanel.setPreferredSize(new java.awt.Dimension(0, 70));
		welcomeIE_JPanel.setLayout(new java.awt.GridBagLayout());
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 3.0;
		gridBagConstraints.weighty = 7.0;
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 0);
		importMain_JPanel.add(welcomeIE_JPanel, gridBagConstraints);

		documentumImage_JPanel.setOpaque(false);
		documentumImage_JPanel
				.setPreferredSize(new java.awt.Dimension(175, 425));
		documentumImage_JPanel.setLayout(new java.awt.GridBagLayout());
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.gridheight = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 38.0;
		gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
		importMain_JPanel.add(documentumImage_JPanel, gridBagConstraints);

		importInfo_JPanel.setOpaque(false);
		importInfo_JPanel.setPreferredSize(new java.awt.Dimension(370, 375));
		importInfo_JPanel.setLayout(new java.awt.GridBagLayout());

		importFlow_JPanel.setOpaque(false);
		importFlow_JPanel.setPreferredSize(new java.awt.Dimension(370, 355));
		importFlow_JPanel.setLayout(new java.awt.GridBagLayout());
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 18.0;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
		importInfo_JPanel.add(importFlow_JPanel, gridBagConstraints);

		message_JTextArea.setColumns(20);
		message_JTextArea.setEditable(false);
		message_JTextArea.setLineWrap(true);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		importInfo_JPanel.add(message_JTextArea, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 2.0;
		gridBagConstraints.weighty = 38.0;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
		importMain_JPanel.add(importInfo_JPanel, gridBagConstraints);

		importProcess_JPanel.setOpaque(false);
		importProcess_JPanel.setPreferredSize(new java.awt.Dimension(110, 50));
		importProcess_JPanel.setLayout(new java.awt.GridBagLayout());

		back_JButton.setText("Back");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
		importProcess_JPanel.add(back_JButton, gridBagConstraints);

		stop_JButton.setText("Stop");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
		importProcess_JPanel.add(stop_JButton, gridBagConstraints);

		next_JButton.setText("Next");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
		importProcess_JPanel.add(next_JButton, gridBagConstraints);

		start_JButton.setText("Start");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
		importProcess_JPanel.add(start_JButton, gridBagConstraints);

		viewLog_JButton.setText("View Log");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
		importProcess_JPanel.add(viewLog_JButton, gridBagConstraints);
		
		updateSchedule_JButton.setText("<html>Modify </html>");
		updateSchedule_JButton.setVisible(false);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
		importProcess_JPanel.add(updateSchedule_JButton, gridBagConstraints);

		cancel_JButton.setText("Cancel");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
		importProcess_JPanel.add(cancel_JButton, gridBagConstraints);

		finish_JButton.setText("Finish");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
		importProcess_JPanel.add(finish_JButton, gridBagConstraints);
		
		schedule_JButton.setText("<html>Schedule</html>");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
		schedule_JButton.show(false);
		importProcess_JPanel.add(schedule_JButton, gridBagConstraints);

		scheduleProcess_JButton.setText("<html>Schedule</html>");
		
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
		importProcess_JPanel.add(scheduleProcess_JButton, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 2.0;
		gridBagConstraints.weighty = 5.0;
		gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
		importMain_JPanel.add(importProcess_JPanel, gridBagConstraints);

		
//		JLabel copyRightLabel = new JLabel("Copyright @ Daffodil Software Ltd.");
//		copyRightLabel.setFont(new Font("Arial",Font.PLAIN,10));
//		copyRightLabel.setHorizontalTextPosition(JLabel.RIGHT);
//		
//		gridBagConstraints = new java.awt.GridBagConstraints();
//		gridBagConstraints.gridx = 1;
//		gridBagConstraints.gridy = 3;
//		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
//		gridBagConstraints.anchor = gridBagConstraints.EAST ;
//		gridBagConstraints.weightx = 2.0;
////		gridBagConstraints.weighty = 5.0;
////		gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 0);
//		importMain_JPanel.add(copyRightLabel, gridBagConstraints);
		
		/*
		 * Below schedule label is added by harsh
		 */
		
		scheduleMessage_JPanel.setOpaque(false);
		scheduleMessage_JPanel.setPreferredSize(new java.awt.Dimension(110, 50));
		scheduleMessage_JPanel.setLayout(new java.awt.GridBagLayout());
		
		scheduleLabel = new JLabel("<html>If you want to Schedule Utility Click on Schedule</html>");
		scheduleLabel.setFont(new Font("Arial",Font.PLAIN,12));
		scheduleLabel.setHorizontalTextPosition(JLabel.RIGHT);
		scheduleLabel.setForeground(Color.RED);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.anchor = gridBagConstraints.EAST ;
		gridBagConstraints.weightx = 2.0;
		gridBagConstraints.weighty = 5.0;
		gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
		scheduleMessage_JPanel.add(scheduleLabel, gridBagConstraints);
		
		schedule_JButton.setText("<html>Schedule</html>");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.anchor = gridBagConstraints.EAST ;
		gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);	
		scheduleMessage_JPanel.add(schedule_JButton, gridBagConstraints);
		
	
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 2.0;
		gridBagConstraints.weighty = 4.0;
		gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
		importMain_JPanel.add(scheduleMessage_JPanel, gridBagConstraints);
			
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 3.0;
		gridBagConstraints.weighty = 1.0;
		add(importMain_JPanel, gridBagConstraints);

		stop_JButton.setVisible(false);
		viewLog_JButton.setVisible(false);
		finish_JButton.setVisible(false);
		start_JButton.setVisible(false);
	}// </editor-fold>//GEN-END:initComponents

	/**
	 * @param args
	 *            the command line arguments
	 */
	private DButton next_JButton;
	private DButton back_JButton;
	private DButton cancel_JButton;
	private javax.swing.JPanel documentumImage_JPanel;
	private javax.swing.JPanel importInfo_JPanel;
	private javax.swing.JPanel importMain_JPanel;
	private javax.swing.JPanel importProcess_JPanel;
	private javax.swing.JPanel importFlow_JPanel;
	private javax.swing.JTextArea message_JTextArea;
	private javax.swing.JPanel welcomeIE_JPanel;
	private javax.swing.JPanel scheduleMessage_JPanel;
	private DButton stop_JButton;
	private DButton updateSchedule_JButton;
	private DButton viewLog_JButton;
	private DButton finish_JButton;
	private DButton start_JButton;
	private DButton schedule_JButton;
	private DButton scheduleProcess_JButton;
	private JLabel scheduleLabel;// This label is introduced by Harsh

	// End of variables declaration//GEN-END:variables

	public Object getUIInfobean() {
		return uIInfobean;

	}

	protected void setUIInfobean(Object uIInfobean) {
		this.uIInfobean = uIInfobean;
	}

	public MetadataReader getMetadataReader() {
		return metadataReader;
	}

	public void setMetadataReader(MetadataReader metadataReader) {
		this.metadataReader = metadataReader;
	}

	public MetadataWriter getMetadataWriter() {
		return metadataWriter;
	}

	public void setMetadataWriter(MetadataWriter metadataWriter) {
		this.metadataWriter = metadataWriter;
	}

	public IELogger getIELogger() {
		return iELogger;
	}

	public void setIELogger(IELogger logger) {
		iELogger = logger;
	}

	public CSServices getCSServiceProvider() {
		return cSServiceProvider;
	}

	public void setCSServiceProvider(CSServices serviceProvider) {
		cSServiceProvider = serviceProvider;
	}

	public ConfigBean getConfigBean() {
		return configBean;
	}

	public void setConfigBean(ConfigBean configBean) {
		this.configBean = configBean;
	}

	public void showIEIntroUIControl() {
		stop_JButton.setVisible(false);
		viewLog_JButton.setVisible(false);
		finish_JButton.setVisible(false);
		back_JButton.setVisible(true);
		next_JButton.setVisible(true);
		cancel_JButton.setVisible(true);

		setUIIndex(-1);

	}

	protected abstract ConfigBean getConfigBeanToWrite();

	protected abstract int getConfigBeanOperation();

	public void writeConfigBean() {

		try {
			DaffIEConfigurator.write(getConfigBeanToWrite(),
					getConfigBeanOperation());
		} catch (DConfigWriteException e) {
			System.out.println("*************************2131321*********************");
			e.printStackTrace();

			getIELogger().writeLog(e.getMessage(), IELogger.ERROR);
		}
	}

	protected AbstractUIControl getUiControl() {
		return uiControl;
	}

	protected void setUiControl(AbstractUIControl uiControl) {
		this.uiControl = uiControl;
	}

	public IEMessageUtility getIEMessageUtility() {
		return iEMessageUtility;
	}

	public void setIEMessageUtility(IEMessageUtility messageUtility) {
		iEMessageUtility = messageUtility;
	}

	public IEMainFrame getMainFrame() {
		return mainFrame;
	}

	public void setMainFrame(IEMainFrame mainFrame) {
		this.mainFrame = mainFrame;
	}
	private int promptUserScheduleProcess() {
		return getIEMessageUtility().showConfirmationMessageDialog(
		"Scheduling of Import/Export Process is successfully completed. Would you like to do more Import or Export ?");
	}

}
