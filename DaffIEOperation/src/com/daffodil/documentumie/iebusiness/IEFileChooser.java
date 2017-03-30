package com.daffodil.documentumie.iebusiness;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;


public class IEFileChooser extends JFileChooser{
	private Color bcakgroundColor = new Color(182, 183, 125);
	private Color foreGroundColor = new Color(255, 255, 255);

	static IEFileChooser tfc = null;

	public IEFileChooser(){
		super();
		mySetOpaque(this);
	}

	protected void mySetOpaque(JComponent jcomp) {
		Component[] comps = jcomp.getComponents();
		for (Component c : comps) {
//			System.out.println("Component class " + c.getClass().getName());
			if (c instanceof JPanel) {
				((JPanel) c).setOpaque(false);
				mySetOpaque((JComponent) c);
//				System.out.println(((JPanel) c).getComponentCount()+ "   "/*+((JPanel) c).findComponentAt(0,0)*/);
			}if (c instanceof JButton) {
				c.setSize(80,20);
				c.setBackground(bcakgroundColor);
				c.setForeground(foreGroundColor);
			}if (c instanceof JScrollPane) {
				c.setBackground(bcakgroundColor);
				c.setForeground(foreGroundColor);
			}if (c instanceof JToggleButton) {
				c.setBackground(bcakgroundColor);
				c.setForeground(foreGroundColor);
			}if (c instanceof JComboBox) {
				c.setBackground(Color.white);
			}
		}
	}

	public void paintComponent(Graphics g) {

		Dimension dim = getSize();
		Graphics2D g2 = (Graphics2D) g;
		Insets inset = getInsets();
		int vWidth = dim.width - (inset.left + inset.right);
		int vHeight = dim.height - (inset.top + inset.bottom);

		paintGradient(g2, inset.left,
				inset.top, dim.width, dim.height, dim.height);
	}

	private void paintGradient(Graphics2D g2d, int x, int y,
			int w, int h, int height) {
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		GradientPaint GP = new GradientPaint(0, h * 1 / 4, bcakgroundColor, 0, h*3/4, foreGroundColor, true);
		g2d.setPaint(GP);

		g2d.setPaint(GP);
		g2d.fillRect(1, 1, w, h);

	}



}
