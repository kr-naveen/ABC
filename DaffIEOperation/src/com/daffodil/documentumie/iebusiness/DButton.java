package com.daffodil.documentumie.iebusiness;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;

public abstract class DButton extends JButton {

	public DButton() {
		super();
		setBakcgroundAndForegroundColor();
	}

	public DButton(Action a) {
		super(a);
		setBakcgroundAndForegroundColor();
	}

	public DButton(Icon icon) {
		super(icon);
		setBakcgroundAndForegroundColor();
	}

	public DButton(String text, Icon icon) {
		super(text, icon);
		setBakcgroundAndForegroundColor();

	}

	public DButton(String text) {
		super(text);
		setBakcgroundAndForegroundColor();
	}

	private void setBakcgroundAndForegroundColor() {
		setBackground(new Color(182, 183, 125));
		setForeground(new Color(255, 255, 255));
		setPreferredSize(new Dimension(getButtonWidth(),20));
	}
	
	protected abstract int getButtonWidth();
}
