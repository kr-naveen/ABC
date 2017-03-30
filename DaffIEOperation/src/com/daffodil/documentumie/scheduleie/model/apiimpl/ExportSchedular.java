package com.daffodil.documentumie.scheduleie.model.apiimpl;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.omg.CORBA.DynAnyPackage.Invalid;

import com.daffodil.documentumie.fileutil.logger.IELogger;
import com.daffodil.documentumie.scheduleie.bean.EScheduleConfigBean;
import com.daffodil.documentumie.scheduleie.bean.ScheduleExportConfigBean;
import com.daffodil.documentumie.scheduleie.configurator.DaffIESchedularConfigurator;
import com.daffodil.documentumie.scheduleie.controller.ScheduleExportController;
import com.daffodil.documentumie.scheduleie.model.Exception.scheduleFileReaderException;

public class ExportSchedular implements Runnable {

	 static Logger logger = Logger.getLogger(IELogger.class);
	 boolean runningExport = true;
	 private Long lastModified=null;
	 List<EScheduleConfigBean> eScheduleList=null;
	
	public  boolean isRunningExport() {
		return runningExport;
	}

	public  void setRunningExport(boolean runningExport) {
		this.runningExport = runningExport;
	}

	@Override
	public void run() {
		System.out.println(".................Initiated  Export process...................");
		logger.info("Export runnning....................................");
		Date nextScheduleTime = null;
		
		try {
			lastModified = getScheduleLastModifiedDate();
			eScheduleList = getSchedulesList();
			eScheduleList = getSortedSchedules(eScheduleList);
		} catch (IOException e1) {
			System.out.println(e1.getMessage());
			logger.info(e1.getMessage());
			runningExport = false;
		}
	
		while (runningExport) {
			try {	
				long tempMod = getScheduleLastModifiedDate();
				if(lastModified!=tempMod)
				{
					refreshSchedules();
				}
				if(eScheduleList!=null && eScheduleList.size()>0)
				{
					EScheduleConfigBean  scheduleToRun = eScheduleList.get(0);
					System.out.println(scheduleToRun.getHours()+scheduleToRun.getMinute()+"====="+scheduleToRun.getNexSchedule());
					try{Thread.sleep(1000);}catch(Exception e){}
					boolean isValid = validateNextSchedule(scheduleToRun);
					
					if(isValid)
					{
						String nextSchedule = scheduleToRun.getNexSchedule();
						nextScheduleTime = convertIntoDate(nextSchedule);
						GregorianCalendar cg = (GregorianCalendar) Calendar.getInstance();
						cg.setTime(nextScheduleTime);
						long nextSchDate = cg.getTimeInMillis();
						long currentDate = new GregorianCalendar().getTimeInMillis();
						System.out.println("nextSchDate is"+new Date(nextSchDate));
						System.out.println("currentDate is"+new Date(currentDate));
						long diff = nextSchDate - currentDate;
						if (diff >= 0) {
							System.out.println("Export Thread will wait for "+diff);
							logger.info("Export Thread will wait for "+diff);
							Thread.sleep(diff);
						}
						System.out.println("Thread Wakeup...............");
						//Naveen hided 30-06-2016 
						ScheduleExportController scheduleImportController = new ScheduleExportController(scheduleToRun.getExportConfigBean(), scheduleToRun);
						//Added By Naveen
						//eScheduleList.remove(0);
						//Naveen hided 30-06-2016
					}
					eScheduleList = updateSchedule(eScheduleList, scheduleToRun);
					if(!checkAllEndDatesExpired(eScheduleList))
					{
						logger.info("*****  All the Export Dates have been Expired*****");
						System.out.println("*****  All the Export Dates have been Expired*****");
						runningExport = false;
					}
									}
				
			}catch (/*IO*/Exception e) {
				System.out.println(e.getMessage());
				logger.info(e.getMessage());
				runningExport = false;
			} 
		}		
	}
	private boolean checkAllEndDatesExpired(List<EScheduleConfigBean> eScheduleList) throws ParseException
	{
		String endDatePattern = "MM/dd/yyyy";		
		SimpleDateFormat formatEndDate = new SimpleDateFormat(endDatePattern);
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);  
		cal.set(Calendar.MINUTE, 0);  
		cal.set(Calendar.SECOND, 0);  
		cal.set(Calendar.MILLISECOND, 0);
		Date todayDate = cal.getTime();
		for(int i=0;i<eScheduleList.size();i++)
		{
			EScheduleConfigBean bean = eScheduleList.get(i);
			String endDate = bean.getEndDate();
			
			Date endDateTime = formatEndDate.parse(endDate);
			if(endDateTime.compareTo(todayDate)>=0)
			{
				return true;
			}
		}
		return false;
	}
	private List <EScheduleConfigBean> updateSchedule(List<EScheduleConfigBean> eScheduleList,EScheduleConfigBean  scheduleToRun) throws ParseException
	{
		if(eScheduleList!=null && eScheduleList.size()>0)
		{
			//boolean hasSet=false;
			eScheduleList.remove(0);// Removing the first element from the list
			/*
			 * The below code is adding one day to the  schedule which had run last time
			 */
//			Date currentRunDate = convertIntoDate(scheduleToRun.getNexSchedule());
//			long currentRunDateTime =  currentRunDate.getTime();
//			Long oneDay = (long) (24*60*60*1000);
//			currentRunDateTime = currentRunDateTime+oneDay;
//			currentRunDate = new Date(currentRunDateTime);
//			String modifiedDate = convertDateIntoString(currentRunDate);
//			for(int i=0;i<eScheduleList.size();i++)
//			{
//				EScheduleConfigBean bean = eScheduleList.get(i);
//				String nextDateString =bean.getNexSchedule();
//				Date nextScheduleDate = convertIntoDate(nextDateString);
//				if(nextScheduleDate.compareTo(currentRunDate)>0)
//				{					
//					scheduleToRun.setNexSchedule(modifiedDate);
//					eScheduleList.add(i, scheduleToRun);
//					hasSet=true;
//					break;
//				}
//			}
//			if(!hasSet)
//			{
//				scheduleToRun.setNexSchedule(modifiedDate);
//				int numElement = eScheduleList.size();
//				eScheduleList.add(numElement, scheduleToRun);							
//			}			
		}
		return eScheduleList;
	}
	private boolean validateNextSchedule(EScheduleConfigBean  beanObj) throws ParseException
	{
		String pattern = "MM/dd/yyyy HH:mm";
		SimpleDateFormat format = new SimpleDateFormat(pattern);		
		String endDatePattern = "MM/dd/yyyy";		
		SimpleDateFormat formatEndDate = new SimpleDateFormat(endDatePattern);
		
		Date dateStart = format.parse(beanObj.getStartDate());
		Date dateEnd = formatEndDate.parse( beanObj.getEndDate());
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);  
		cal.set(Calendar.MINUTE, 0);  
		cal.set(Calendar.SECOND, 0);  
		cal.set(Calendar.MILLISECOND, 0);
		Date todayDate = cal.getTime();
		if(dateEnd.compareTo(todayDate)>=0 && System.currentTimeMillis()>=dateStart.getTime())
		{
			return true;
		}
		return false;
	}
	public void getUpdatedNextSchedule(EScheduleConfigBean escheduleConf)
	{
		Date dateObj = Calendar.getInstance().getTime();		
		String hoursStr = escheduleConf.getHours();
		String minStr   = escheduleConf.getMinute();	    
		dateObj.setHours(Integer.parseInt(hoursStr));
		dateObj.setMinutes(Integer.parseInt(minStr));
		String pattern = "MM/dd/yyyy HH:mm";
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		escheduleConf.setNexSchedule(format.format(dateObj));
	}
	private List <EScheduleConfigBean> getSortedSchedules(List<EScheduleConfigBean> escheduleConfigBeanList)
	{
		List <EScheduleConfigBean> finalList = new ArrayList<EScheduleConfigBean>();
		List<EScheduleSort> sortedList = new ArrayList<EScheduleSort>();
		String pattern = "MM/dd/yyyy HH:mm";
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		EScheduleSort sortObj=null;
		for(int i = 0; i < escheduleConfigBeanList.size(); i++)
		{	
			/*String nextSchedule = ibean.getNexSchedule();
			System.out.println("Previous Next Schedule"+nextSchedule);*/
		//	getUpdatedNextSchedule(escheduleConfigBeanList.get(i));
			//System.out.println("Updated Next Schedule"+ibean.getNexSchedule());
		}
		try {
			for(int i = 0; i < escheduleConfigBeanList.size(); i++)
			{
				String nextScheduleStr = escheduleConfigBeanList.get(i).getNexSchedule(); 
				Date nextScheduledDate = format.parse(nextScheduleStr);	
				sortObj =new EScheduleSort(i,escheduleConfigBeanList.get(i),nextScheduledDate);
				sortedList.add(sortObj);
			}
			Collections.sort(sortedList,new java.util.Comparator<EScheduleSort>(){
				@Override
				public int compare(EScheduleSort o1, EScheduleSort o2) {
					return o1.getNextScheduleDate().compareTo(o2.getNextScheduleDate());
				}});
			for (int i = 0; i < sortedList.size(); i++) {
				sortObj = sortedList.get(i);
				finalList.add(sortObj.getConfigBean()); 
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		System.out.println("Naveen Time:"+finalList.get(0).getStartDate());
		return finalList;
	}
	private List<EScheduleConfigBean> getSchedulesList() throws IOException
	{
		String userDir = new String(getDocumentumHome().replace("\\", "/"));
		String fileDir =  new String(userDir + "/DaffIE");
		String fileName = new String(fileDir + "/" + "EScheduleConfigBean" + ".xml");
				
		//System.out.println(fileName);
		logger.info("** fileName ***"+fileName);
		ArrayList<EScheduleConfigBean> escheduleConfigBeanList = (ArrayList<EScheduleConfigBean>) readConfigFile("ExportSchedule", fileName);
		fileName = new String(fileDir + "/" + "ScheduleExportConfigBean" + ".xml");
		ArrayList<ScheduleExportConfigBean> scheduleDetailList = (ArrayList<ScheduleExportConfigBean>) readAllScheduleDetail("Export", fileName);
		for (int i = 0; i < escheduleConfigBeanList.size(); i++) {
			//System.out.println("Naveen:-"+scheduleDetailList.get(i).getScheduleName());
			System.out.println("Naveen :-"+scheduleDetailList.get(i).getScheduleName());
			 escheduleConfigBeanList.get(i).setExportConfigBean(scheduleDetailList.get(i));
			 //nextScheduleDate = escheduleConfigBeanList.get(i).getNexSchedule();
			// System.out.println(nextScheduleDate);
		}
		return escheduleConfigBeanList;
	}
	private List<ScheduleExportConfigBean> readAllScheduleDetail (String type, String fileName)
	 {
		 List<ScheduleExportConfigBean> lst=null;
		 try {
			 lst = new DaffIESchedularConfigurator().getAllScheduleDetails(type,fileName);
		} catch (scheduleFileReaderException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		 System.out.println("readAllScheduleDetail "+lst.size());
		 return lst;
	 }
	private String getDocumentumHome() 
	{
		Properties properties = System.getProperties();
		String SceduleFileRootPath = properties.getProperty("user.home");
		return SceduleFileRootPath;
	}
	private Object readConfigFile(String type, String fileName)
			throws IOException {
		Object objClass = null;
		try {
			logger.info("********* Inside readConfigFile *****");
			objClass = new DaffIESchedularConfigurator().getSchedules(type, fileName);
			// Initially the method readFile() was called. Changed by Naveen for the 
			// support of Multiple scheduling.
		} catch (scheduleFileReaderException e) {
			throw new IOException(e.getCause()+" "+e.getMessage());
		}
		return objClass;
	}	
	//--------------------
	private Date convertIntoDate(String dateNext) throws ParseException
	 {
		 String pattern = "MM/dd/yyyy HH:mm";
		 SimpleDateFormat format = new SimpleDateFormat(pattern);
		 return format.parse(dateNext);
	 }
	private String convertDateIntoString(Date dateNext) throws ParseException
	 {
		 String pattern = "MM/dd/yyyy HH:mm";
		 SimpleDateFormat format = new SimpleDateFormat(pattern);
		 return format.format(dateNext);
	 }
	public Long getScheduleLastModifiedDate() throws IOException
	 {
			String userDir = new String(getDocumentumHome().replace("\\", "/"));
			String fileDir =  new String(userDir + "/DaffIE");
			String fileName = new String(fileDir + "/" + "EScheduleConfigBean" + ".xml");
			File file = new File(fileName);
			if(file!=null)
			{
				return file.lastModified();
			}
			return null;			
	 }
	public void refreshSchedules() throws IOException
	 {
		 try {
				lastModified = getScheduleLastModifiedDate();
				eScheduleList = getSchedulesList();
				eScheduleList = getSortedSchedules(eScheduleList);
			} catch (IOException e1) {
				System.out.println(e1.getMessage());
				logger.info(e1.getMessage());
				runningExport = false;
			}	
	 }

}
