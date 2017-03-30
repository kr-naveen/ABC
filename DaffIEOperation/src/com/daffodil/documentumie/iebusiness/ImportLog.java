/**
 Class  ImportLog
 Responsible for:
 Managing Log 

 Attributes: 
 String logFilePath

 Constructors:
 Default
 
 Methods: 
 public String log(String msg,String type)
 
 **/
package com.daffodil.documentumie.iebusiness;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ImportLog {
	private static String logFilePath = null;

	private static File file;

	public static void log(String msg) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
			bw.write(msg);
			bw.newLine();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void setFilePath(String pLogFilePath) {
		logFilePath = pLogFilePath;
		file = new File(logFilePath);

	}
	
	public static void logCommit() {
		file = null;
		logFilePath = null;
	}

}
