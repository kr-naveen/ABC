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

public class Log {
	private static String logFilePath = null;

	private static File file;

	public static void logFile(String msg) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
			bw.write(msg);
			bw.newLine();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void setLogFilePath(String pLogFilePath) throws IOException {
		logFilePath = pLogFilePath;
		file = new File(logFilePath);
		if(!file.exists()){
			file.createNewFile();
		}
	}
	
	public static void logCommit() {
		file = null;
		logFilePath = null;
	}

}
