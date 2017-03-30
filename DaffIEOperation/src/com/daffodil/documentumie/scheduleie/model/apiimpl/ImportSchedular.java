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

import com.daffodil.documentumie.fileutil.logger.IELogger;
import com.daffodil.documentumie.scheduleie.bean.IScheduleConfigBean;

import com.daffodil.documentumie.scheduleie.bean.ScheduleImportConfigBean;
import com.daffodil.documentumie.scheduleie.configurator.DaffIESchedularConfigurator;
import com.daffodil.documentumie.scheduleie.controller.ScheduleImportController;
import com.daffodil.documentumie.scheduleie.model.Exception.scheduleFileReaderException;

public class ImportSchedular implements Runnable{
	
	static Logger logger = Logger.getLogger(IELogger.class);
	private boolean running /*= true*/;
	private Long lastModified=null;
	List<IScheduleConfigBean> iScheduleList=null;	
	
	public boolean isRunning() {
		return running;
	}
	public void setRunning(boolean running) {
		this.running = running;
	}
	
	@Override
	public void run() {
		
		Date nextScheduleTime = null;
			
		try {
			lastModified = getScheduleLastModifiedDate();
			iScheduleList = getSchedulesList();
			iScheduleList = getSortedSchedules(iScheduleList);
			
		} catch (IOException e1) {
			System.out.println(e1.getMessage());
			logger.info(e1.getMessage());
			running = false;
		}				
		while (running) {
			try {		
				long tempMod = getScheduleLastModifiedDate();
				if(lastModified!=tempMod)
				{
					refreshSchedules();
				}
				System.out.println("Schedule Size is "+iScheduleList.size());
				//System.out.println("..... before import process");
				logger.info("..... Initiated import process");
				if(iScheduleList!=null && iScheduleList.size()>0)
				{
					IScheduleConfigBean  scheduleToRun = iScheduleList.get(0);
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
							System.out.println("Import Thread will wait for "+diff);
							logger.info("Import Thread will wait for "+diff);
							Thread.sleep(diff);
						}
						ScheduleImportController scheduleImportController = new ScheduleImportController(scheduleToRun.getImportConfigBean(), scheduleToRun);
					}
					iScheduleList = updateSchedule(iScheduleList, scheduleToRun);
					if(!checkAllEndDatesExpired(iScheduleList))
					{
						logger.info("*****  All the Import Dates have been Expired*****");
						System.out.println("*****  All the Import Dates have been Expired*****");
						running = false;
					}
				}							
			}
			catch (/*IO*/Exception e) {
				System.out.println(e.getMessage());
				logger.info(e.getMessage());
				running = false;
			} 
		}		
	}
	private boolean checkAllEndDatesExpired(List<IScheduleConfigBean> iScheduleList) throws ParseException
	{
		String endDatePattern = "MM/dd/yyyy";		
		SimpleDateFormat formatEndDate = new SimpleDateFormat(endDatePattern);
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);  
		cal.set(Calendar.MINUTE, 0);  
		cal.set(Calendar.SECOND, 0);  
		cal.set(Calendar.MILLISECOND, 0);
		Date todayDate = cal.getTime();
		for(int i=0;i<iScheduleList.size();i++)
		{
			IScheduleConfigBean bean = iScheduleList.get(i);
			String endDate = bean.getEndDate();
			
			Date endDateTime = formatEndDate.parse(endDate);
			if(endDateTime.compareTo(todayDate)>=0)
			{
				return true;
			}
		}
		return false;
	}
	private List <IScheduleConfigBean> updateSchedule(List<IScheduleConfigBean> iScheduleList,IScheduleConfigBean  scheduleToRun) throws ParseException
	{
		if(iScheduleList!=null && iScheduleList.size()>0)
		{
			boolean hasSet=false;
			iScheduleList.remove(0);// Removing the first element from the list
			/*
			 * The below code is adding one day to the  schedule which had run last time
			 */
			Date currentRunDate = convertIntoDate(scheduleToRun.getNexSchedule());
			long currentRunDateTime =  currentRunDate.getTime();
			Long oneDay = (long) (24*60*60*1000);
			currentRunDateTime = currentRunDateTime+oneDay;
			currentRunDate = new Date(currentRunDateTime);
			String modifiedDate = convertDateIntoString(currentRunDate);
			for(int i=0;i<iScheduleList.size();i++)
			{
				IScheduleConfigBean bean = iScheduleList.get(i);
				String nextDateString =bean.getNexSchedule();
				Date nextScheduleDate = convertIntoDate(nextDateString);
				if(nextScheduleDate.compareTo(currentRunDate)>0)
				{					
					scheduleToRun.setNexSchedule(modifiedDate);
					iScheduleList.add(i, scheduleToRun);
					hasSet=true;
					break;
				}
			}
			if(!hasSet)
			{
				scheduleToRun.setNexSchedule(modifiedDate);
				int numElement = iScheduleList.size();
				iScheduleList.add(numElement, scheduleToRun);							
			}			
		}
		return iScheduleList;
	}
	private List <IScheduleConfigBean> getSortedSchedules(List<IScheduleConfigBean> ischeduleConfigBeanList)
	{
		List <IScheduleConfigBean> finalList = new ArrayList<IScheduleConfigBean>();
		List<IScheduleSort> sortedList = new ArrayList<IScheduleSort>();
		String pattern = "MM/dd/yyyy HH:mm";
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		IScheduleSort sortObj=null;
		for(int i = 0; i < ischeduleConfigBeanList.size(); i++)
		{	
			getUpdatedNextSchedule(ischeduleConfigBeanList.get(i));
			//System.out.println("Updated Next Schedule"+ibean.getNexSchedule());
		}
		try {
			for(int i = 0; i < ischeduleConfigBeanList.size(); i++)
			{
				String nextScheduleStr = ischeduleConfigBeanList.get(i).getNexSchedule(); 
				Date nextScheduledDate = format.parse(nextScheduleStr);	
				sortObj =new IScheduleSort(i,ischeduleConfigBeanList.get(i),nextScheduledDate);
				sortedList.add(sortObj);
			}
			Collections.sort(sortedList,sortObj);
			for (int i = 0; i < sortedList.size(); i++) {
				sortObj = sortedList.get(i);
				finalList.add(sortObj.getConfigBean());
				/*System.out.println("After Sorting Next Schedule"+sortObj.getConfigBean().getNexSchedule());
				System.out.println("After Sorting Repo Name is "+sortObj.getConfigBean().getImportConfigBean().getRepository());*/ 			
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return finalList;
	}
	private boolean validateNextSchedule(IScheduleConfigBean  beanObj) throws ParseException
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
	private List<IScheduleConfigBean> getSchedulesList() throws IOException
	{
		String userDir = new String(getDocumentumHome().replace("\\", "/"));
		String fileDir =  new String(userDir + "/DaffIE");
		String fileName = new String(fileDir + "/" + "IScheduleConfigBean" + ".xml");
				
		//System.out.println(fileName);
		logger.info("** fileName ***"+fileName);
		ArrayList<IScheduleConfigBean> ischeduleConfigBeanList = (ArrayList<IScheduleConfigBean>) readConfigFile("ImportSchedule", fileName);
		fileName = new String(fileDir + "/" + "ScheduleImportConfigBean" + ".xml");
		ArrayList<ScheduleImportConfigBean> scheduleDetailList = (ArrayList<ScheduleImportConfigBean>) readAllScheduleDetail("Import", fileName);
		for (int i = 0; i < ischeduleConfigBeanList.size(); i++) {
			 ischeduleConfigBeanList.get(i).setImportConfigBean(scheduleDetailList.get(i));
			 //String nextScheduleDate = ischeduleConfigBeanList.get(i).getNexSchedule();
		}
		return ischeduleConfigBeanList;
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
		} catch (scheduleFileReaderException e) {
			throw new IOException(e.getCause()+" "+e.getMessage());
		}
		return objClass;
	}

public void getUpdatedNextSchedule(IScheduleConfigBean ischeduleConf)
	{
		Date dateObj = Calendar.getInstance().getTime();		
		String hoursStr = ischeduleConf.getHours();
		String minStr   = ischeduleConf.getMinute();	    
		dateObj.setHours(Integer.parseInt(hoursStr));
		dateObj.setMinutes(Integer.parseInt(minStr));
		String pattern = "MM/dd/yyyy HH:mm";
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		ischeduleConf.setNexSchedule(format.format(dateObj));
	}

	 private List<ScheduleImportConfigBean> readAllScheduleDetail (String type, String fileName)
	 {
		 List<ScheduleImportConfigBean> lst=null;
		 try {
			 lst = new DaffIESchedularConfigurator().getAllScheduleDetails(type,fileName);
		} catch (scheduleFileReaderException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		 return lst;
	 }
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
			String fileName = new String(fileDir + "/" + "IScheduleConfigBean" + ".xml");
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
				iScheduleList = getSchedulesList();
				iScheduleList = getSortedSchedules(iScheduleList);
			} catch (IOException e1) {
				System.out.println(e1.getMessage());
				logger.info(e1.getMessage());
				running = false;
			}	
	 }

}
