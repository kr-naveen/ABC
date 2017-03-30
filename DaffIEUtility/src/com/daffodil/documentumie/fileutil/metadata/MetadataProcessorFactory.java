package com.daffodil.documentumie.fileutil.metadata;

import java.util.Map;

import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import com.daffodil.documentumie.fileutil.metadata.api.MetadataReader;
import com.daffodil.documentumie.fileutil.metadata.api.MetadataWriter;
import com.daffodil.documentumie.fileutil.metadata.apiimpl.CSVMetadataReader;
import com.daffodil.documentumie.fileutil.metadata.apiimpl.CSVMetadataWriter;
import com.daffodil.documentumie.fileutil.metadata.apiimpl.ExcelMetadataReader;
import com.daffodil.documentumie.fileutil.metadata.apiimpl.ExcelMetadataWriter;
import com.daffodil.documentumie.fileutil.metadata.apiimpl.XMLMetadataReader;
import com.daffodil.documentumie.fileutil.metadata.apiimpl.XMLMetadataWriter;

public class MetadataProcessorFactory {

	public static MetadataReader getMetadataReader(Map param) {

		String extension = (String) param.get("extension");
		String filePath = (String) param.get("File_Name");
		String fileName = (String) param.get("Table_Name");

		// TODO check for file extension return excel if filename has extension
		// .xls
		if (extension.equalsIgnoreCase("xls")) {
			MetadataReader metadatatreader = new ExcelMetadataReader(filePath,
					fileName);
			return metadatatreader;
		}
		if (extension.equalsIgnoreCase("csv")) {
			MetadataReader metadatatreader = new CSVMetadataReader(filePath,
					fileName);
			return metadatatreader;
		}
		if (extension.equalsIgnoreCase("xml")) {
			MetadataReader metadatareader = new XMLMetadataReader(filePath,
					fileName);
			return metadatareader;
		}
		return null;

		// TODO check for file extension return excel if filename has extension
		// .xls

	}

	public static MetadataWriter getMetadataWriter(Map param) {

		String extension = (String) param.get("extension");
		String filePath = (String) param.get("File_Name");
		String fileName = (String) param.get("Table_Name");

		// TODO check for file extension return excel if filename has extension
		// .xls
		if (extension.equalsIgnoreCase("xls")) {
			MetadataWriter metadataWriter = new ExcelMetadataWriter(filePath,
					fileName);
			return metadataWriter;
		}
		if (extension.equalsIgnoreCase("csv")) {
			MetadataWriter metadataWriter = new CSVMetadataWriter(filePath,
					fileName);
			return metadataWriter;
		}
		if (extension.equalsIgnoreCase("xml")) {
			MetadataWriter metadataWriter = new XMLMetadataWriter(filePath,
					fileName);
			return metadataWriter;
		}
		return null;

	}
	
	public static MetadataWriter getMetadataWriterForLinux(Map param) {
		String extension = (String) param.get("extension");
		String filePath = (String) param.get("File_Name");
		String fileName = (String) param.get("Table_Name");
		WritableSheet worksheet = (WritableSheet) param.get("worksheet");
		WritableWorkbook workbook = (WritableWorkbook) param.get("workbook");
		// TODO check for file extension return excel if filename has extension
		// .xls
		if (extension.equalsIgnoreCase("xls")) {
			MetadataWriter metadataWriter = new ExcelMetadataWriter(filePath,
					fileName,worksheet,workbook);
			return metadataWriter;
		}
		if (extension.equalsIgnoreCase("csv")) {
			MetadataWriter metadataWriter = new CSVMetadataWriter(filePath,
					fileName);
			return metadataWriter;
		}
		if (extension.equalsIgnoreCase("xml")) {
			MetadataWriter metadataWriter = new XMLMetadataWriter(filePath,
					fileName);
			return metadataWriter;
		}
		return null;
	}

}
