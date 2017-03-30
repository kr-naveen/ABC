
package com.daffodil.documentumie.iebusiness;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Toolkit;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class IEMainFrame extends JFrame {

	JPanel mainPanel; 
	public IEMainFrame() {
		super();
		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setResizable(false); 
	}

	private void initMainPanel() {
		mainPanel = new JPanel(new GridBagLayout());
		mainPanel.setPreferredSize(new Dimension(550, 500));
		getContentPane().add(mainPanel);
	}

//	public IEMainFrame(String title) throws HeadlessException {
//		super(title);
//		Thread.dumpStack();
//		ImageIcon imageIcon = new ImageIcon(this.getClass().getResource("/com/daffodil/documentumie/iebusiness/images/daffodilLogo.jpg"));
//		Image image = imageIcon.getImage();
//		setIconImage(image);
//	}
	
	GridBagConstraints innerUIGridBagConstraints = null;
	public void showUIControl (JPanel panel, String title){
		if(mainPanel== null){
			initMainPanel();
			setPreferredSize(new Dimension(550, 500));
			pack();
			Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
			int x = (int) (dim.getWidth() - getWidth());
			int y = (int) (dim.getHeight() - getHeight());
			setLocation(x / 2, y / 2);
			setVisible(true);
			setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		}
		mainPanel.removeAll();
		setTitle(title);
		
		if (innerUIGridBagConstraints == null) {
			innerUIGridBagConstraints = new java.awt.GridBagConstraints();
			innerUIGridBagConstraints.gridx = 0;
			innerUIGridBagConstraints.gridy = 0;
			innerUIGridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
			innerUIGridBagConstraints.weightx = 1.0;
			innerUIGridBagConstraints.weighty = 1.0;
			innerUIGridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 0);
		}

		mainPanel.add(panel,innerUIGridBagConstraints);
		mainPanel.repaint();
		
		setVisible(true);
	}
}
