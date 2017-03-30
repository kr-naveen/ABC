package com.daffodil.documentumie.iebusiness;

import java.awt.Color;
import java.awt.Component;
import java.io.File;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

public class IEUtility {
	/*public static void showMsgDialog(Object msg, Component component) {
		JOptionPane.showMessageDialog(component, msg);
	}

	public static int showYeNoConfirmationDialog(Object msg, Component component) {
		return JOptionPane.showConfirmDialog(component, msg,
				"Confirmation Message", JOptionPane.YES_NO_OPTION);
	}*/

	public static String getDateAndTimeString() {
		String name = "";
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(new Date());
		name = name
				+ (calendar.get(GregorianCalendar.DATE) < 10 ? "0"
						+ calendar.get(GregorianCalendar.DATE) : calendar
						.get(GregorianCalendar.DATE));
		name = name
				+ (calendar.get(GregorianCalendar.MONTH) + 1 < 10 ? "0"
						+ (calendar.get(GregorianCalendar.MONTH) + 1)
						: calendar.get(GregorianCalendar.MONTH));
		name = name + calendar.get(GregorianCalendar.YEAR) + "_";
		name = name
				+ (calendar.get(GregorianCalendar.HOUR) < 10 ? "0"
						+ calendar.get(GregorianCalendar.HOUR) : calendar
						.get(GregorianCalendar.HOUR));
		name = name + calendar.get(GregorianCalendar.MINUTE);

		return name;
	}

	public static String getTime() {
		String name = "";
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(new Date());
		name = name
				+ (calendar.get(GregorianCalendar.HOUR) < 10 ? ("0" + calendar
						.get(GregorianCalendar.HOUR)) : calendar
						.get(GregorianCalendar.HOUR)) + ":";
		name = name
				+ (calendar.get(GregorianCalendar.MINUTE) < 10 ? ("0" + calendar
						.get(GregorianCalendar.MINUTE))
						: calendar.get(GregorianCalendar.MINUTE));

		if (calendar.get(GregorianCalendar.AM_PM) == GregorianCalendar.AM) {
			name = name + " AM";
		} else {
			name = name + " PM";
		}
		return name;
	}
	
	public static String chooseImportMetadataFile(Component c,
			 final String ext, final String desc) {
	
		String path = "";
		IEFileChooser ieFileChooser = new IEFileChooser();
		ieFileChooser.setAcceptAllFileFilterUsed(false);
		ieFileChooser.setApproveButtonText("Ok");
		ieFileChooser.setFileFilter(new FileFilter() {
			public boolean accept(File f) {
				String fileName = f.getName();
				String extension = (fileName != null && !"".equals(fileName
						.trim())) ? fileName.substring((fileName
						.lastIndexOf(".") + 1)) : "";

				if (f.isDirectory() || extension.equalsIgnoreCase(ext)) {
					return true;
				} else {
					return false;
				}
			}

			public String getDescription() {
				if(ext.equalsIgnoreCase("csv")){
					return "Comma Seperated File (*.csv)";
				}if(ext.equalsIgnoreCase("xls")){
					return "Microsoft Excel WorkBook (*.xls)";
				}if(ext.equalsIgnoreCase("xml")){
					return "Extensible Markup Language  (*.xml)";
				}
				return null;
			}
		});
		ieFileChooser.showOpenDialog(c);

		File file = ieFileChooser.getSelectedFile();
		if (file != null) {
			path = file.getAbsolutePath();
		}
		return path;
	}

	public static String showDirectoryChooser(Component c) {
		String path = "";
		IEFileChooser ieFileChooser = new IEFileChooser();
		ieFileChooser.setBackground(new Color(255, 255, 255));
		ieFileChooser.setForeground(new Color(255, 255, 255));
		ieFileChooser.setAcceptAllFileFilterUsed(false);
		ieFileChooser.setApproveButtonMnemonic('k');
		ieFileChooser.setApproveButtonText("Ok");
		ieFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		ieFileChooser.showOpenDialog(c);

		File file = ieFileChooser.getSelectedFile();
		if (file != null) {
			path = file.getAbsolutePath();
		}
		return path;
	}

}