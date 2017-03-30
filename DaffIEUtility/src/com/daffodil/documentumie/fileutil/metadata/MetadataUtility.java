package com.daffodil.documentumie.fileutil.metadata;

import java.io.File;
import java.io.IOException;

import jxl.Workbook;
import jxl.read.biff.BiffException;

public class MetadataUtility {

	public static String[] getExcelSheets(String fileName) {
		String[] worksheetList = null;

		File file = new File(fileName);
		Workbook workbook = null;
		try {
			workbook = Workbook.getWorkbook(file);
		} catch (BiffException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		worksheetList = workbook.getSheetNames(); // initailize worksheet
		return worksheetList;
	}

	// public static String[] populateSheet(String inputText) {
	// String[] worksheetList = null;
	// String endStr = inputText != null ? (inputText.length() > 3 ? inputText
	// .substring(inputText.length() - 3)
	// : "")
	// : "";
	// if (endStr.equalsIgnoreCase("xls")) {
	// worksheetList = getExcelSheets(inputText);
	// }
	// return worksheetList;
	// }

}
