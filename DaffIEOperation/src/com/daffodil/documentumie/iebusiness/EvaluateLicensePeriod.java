package com.daffodil.documentumie.iebusiness;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
/**
 * This class is to check the License validity
 * @author joyti
 *
 */
public class EvaluateLicensePeriod {
	private static String PREF_KEY= "DaffIELc";
	/**
	 * This is the method to evaluate the validity
	 * @param fileName 
	 */
	private boolean isEvaluationValid(String fileName){
		boolean licenseValid = false;
		boolean isexist = (fileName.startsWith("51") && fileName.endsWith("6489"));
		if(isexist){
			licenseValid = checkValidity(fileName);
		}else{
			licenseValid = false;
		}
//		return licenseValid;
		return true;
	}

	/**
	 * This is the method to check the validity of License
	 * @param fileName
	 * @return
	 */
	@SuppressWarnings("deprecation")
	private boolean checkValidity(String fileName) {
			String date = fileName.substring(2, 4);
			String month = fileName.substring(10, 12);
			String year = fileName.substring(6, 8) + fileName.substring(14, 16);
//			 "51" + instDate+"45"+ yr.substring(0, 2)+"76"+  mnt +"72" +yr.substring(2, 4)+ "6489";
			GregorianCalendar calendar = (GregorianCalendar) Calendar.getInstance();
			calendar.set(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(date));
//			System.out.println("Evaluation expire on "+ calendar.get(calendar.DATE)+"/"+ calendar.get(calendar.MONTH)+"/"+ calendar.get(calendar.YEAR));
			calendar.add(Calendar.DATE, 10);
			System.out.println("Evaluation expire on "+ calendar.get(calendar.DATE)+"/"+ calendar.get(calendar.MONTH)+"/"+ calendar.get(calendar.YEAR));
			GregorianCalendar c = new GregorianCalendar();
			System.out.println("Evaluation expire on 2 "+ c.get(calendar.DATE)+"/"+ c.get(c.MONTH)+"/"+ c.get(c.YEAR));
						
			int d = calendar.compareTo(new GregorianCalendar());
			//System.out.println("Diff"+d);
			if(d > 0){
				return true;
			}
		return false;
	}

	/**
	 * this is the method to write the properties file 
	 * @param preferences 
	 */
	@SuppressWarnings("deprecation")
	private void writePreferences(Preferences preferences) {
		GregorianCalendar cal = new GregorianCalendar(); 
		
		int dat = cal.get(cal.DATE);

		int month = cal.get(cal.MONTH);

		int year = cal.get(cal.YEAR);
		
		String yr = String.valueOf(year);
		
		String instDate = null;
		String mnt = null;
		
		if(dat <10){
			instDate = "0"+dat;
		}else{
			instDate = String.valueOf(dat);
		}
		if(month <10){
			mnt = "0"+month;
		}else{
			mnt = String.valueOf(mnt);
		}

		String installationDate = "51" + instDate+"45"+ yr.substring(0, 2)+"76"+  mnt +"72" +yr.substring(2, 4)+ "6489";
		preferences.put(PREF_KEY, installationDate);
	}

	/**
	 * This is the method to read the properties file
	 * @return  DaffIE.jar
	 */
	public boolean readFile(){
		boolean isValid = false;
		Preferences preferences = Preferences.userRoot();
		String fileName = preferences.get(PREF_KEY, "0");
		System.out.println("Prefrence Filename is"+fileName);
		if(fileName.equalsIgnoreCase("0")){
			writePreferences(preferences);
			isValid = true;
		}else{
			int preLength = fileName.length();
			if(preLength == 20){ 
				isValid = isEvaluationValid(fileName);
			}else{
				isValid = false;
			}
		}
		return isValid;
	}
	public static void main(String arg[])
	{
		EvaluateLicensePeriod lp = new EvaluateLicensePeriod();
		lp.readFile();
	}
}
