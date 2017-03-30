package com.daffodil.documentumie.scheduleie.model.apiimpl;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.daffodil.documentumie.iebusiness.EvaluateLicensePeriod;
import com.daffodil.documentumie.scheduleie.bean.EScheduleConfigBean;
import com.daffodil.documentumie.scheduleie.bean.IScheduleConfigBean;
import com.daffodil.documentumie.scheduleie.bean.ScheduleImportConfigBean;
import com.daffodil.documentumie.scheduleie.bean.ScheduleConfgiBean;
import com.daffodil.documentumie.scheduleie.bean.ScheduleExportConfigBean;
import com.daffodil.documentumie.scheduleie.configurator.DaffIESchedularConfigurator;
import com.daffodil.documentumie.scheduleie.controller.ScheduleExportController;
import com.daffodil.documentumie.scheduleie.controller.ScheduleImportController;
import com.daffodil.documentumie.scheduleie.model.Exception.scheduleFileReaderException;
import com.daffodil.documentumie.fileutil.logger.IELogger;

public class CheckSchedule {
	static boolean running = true;
	static Logger logger = Logger.getLogger(IELogger.class);
	

	public static void main(String[] args) {
		logger.info("checkSchedule statrted");
		//if (new EvaluateLicensePeriod().readFile()==true)  // shubhra: true added  
		{
			final CheckSchedule checkSchedule = new CheckSchedule();
			Runnable importRun = null;
			Runnable exportRun = null;
			
			importRun = new Runnable() {
				public void run() {
					boolean importRunning = true;
					Date nextScheduleTime = null;
					Map nextImportScheduleMap = null;
					while (running) {
						importRunning = false;
						try {
							System.out.println("..... before import process");
							logger.info("..... before import process");
							nextImportScheduleMap = checkSchedule.checkImportSchedule();
							if (nextImportScheduleMap != null) {
								importRunning = Boolean.getBoolean(nextImportScheduleMap.get("run").toString());
								System.out.println("import process is run -- "+ importRunning);
								System.out.println("next schedule...in import..  "+ nextImportScheduleMap.get("nextScheduleTime"));
								logger.info("next schedule...in import..  "+ nextImportScheduleMap.get("nextScheduleTime"));
								nextScheduleTime = new Date(String.valueOf(nextImportScheduleMap.get("nextScheduleTime")));
								logger.info("nextScheduleTime value :::"+nextScheduleTime);
								GregorianCalendar cg = (GregorianCalendar) Calendar.getInstance();
								cg.setTime(nextScheduleTime);
								long nextSchDate = cg.getTimeInMillis();
								long currentDate = new GregorianCalendar().getTimeInMillis();
								logger.info("***   nextSchDate  ***"+nextSchDate);
								logger.info("*****   currentDate  ********* ::"+currentDate);
								
								long diff = nextSchDate - currentDate;
								System.out.println("import schedule diff......... "+ diff);
								logger.info("import schedule diff......... "+ diff);
								if (diff >= 0) {
									if (diff < 3600000) {
										Thread.sleep(diff);
									} else {
										long rem = diff % 3600000;
										if (rem != 0) {
											Thread.sleep(rem);
										} else {
											Thread.sleep(3600000);
										}
									}
									importRunning = true;
								} else {
									Thread.sleep(3600000);
									importRunning = false;
								}
							} else {
								Thread.sleep(3600000);
								importRunning = false;
							}
						} catch (IOException e) {
							e.printStackTrace();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			};
			exportRun = new Runnable() {
				public void run() {
					System.out.println("Export runnning....................................");
					logger.info("Export runnning....................................");
					boolean exportRunning = true;
					Date nextScheduleTime = null;
					Map nextExportScheduleMap = null;
					while (running) {
						exportRunning = false;
						try {
							System.out.println("before export process..........");
							nextExportScheduleMap = checkSchedule.checkExportSchedule();
							if (nextExportScheduleMap != null) {
								exportRunning = Boolean.getBoolean(nextExportScheduleMap.get("run").toString());

								nextScheduleTime = new Date(nextExportScheduleMap.get("nextScheduleTime").toString());

								System.out.println("sleep time in export.. "+ nextScheduleTime.getTime());
								GregorianCalendar cg = (GregorianCalendar) Calendar.getInstance();
								cg.setTime(nextScheduleTime);
								long nextSchDate = cg.getTimeInMillis();
								long currentDate = new GregorianCalendar().getTimeInMillis();
								long diff = nextSchDate - currentDate;
								System.out.println("export schedule diff......... " + diff);
								if (diff >= 0) {
									if (diff < 3600000) {
										Thread.sleep(diff);
									} else {
										long rem = diff % 3600000;
										if (rem != 0) {
											Thread.sleep(rem);
										} else {
											Thread.sleep(3600000);
										}
									}
									exportRunning = true;
								} else {
									Thread.sleep(3600000);
									exportRunning = false;
								}
							} else {
								Thread.sleep(3600000);
								exportRunning = true;
							}
						} catch (IOException e) {
							try {
								Thread.sleep(3600000);
							} catch (InterruptedException e1) {
								e1.printStackTrace();
							}
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			};
			Thread importThread = new Thread(importRun);
			Thread exportThread = new Thread(exportRun);

			importThread.start();
			//exportThread.start();

			System.out.println("Main Thread going to finish.");
			logger.info("Main Thread going to finish.");
		}
	}

	private Map checkExportSchedule() throws IOException {
		boolean isSchedule = false;
		String processType = "Export";
		String userDir = null;
		String fileDir = null;
		String fileName = null;
		Map nextRunMap = new HashMap();
		String nextScheduleTime = null;

		userDir = new String(getDocumentumHome().replace("\\", "/"));
		fileDir = new String(userDir + "/DaffIE");
		fileName = new String(fileDir + "/" + "EScheduleConfigBean" + ".xml");
		System.out.println("--------------" + fileName);
		EScheduleConfigBean scheduleConfgiBean = (EScheduleConfigBean) readConfigFile(
				"ExportSchedule", fileName);
		boolean isDateValid = isEndDateValid(scheduleConfgiBean.getEndDate());
		System.out.println("end date com .......... " + isDateValid);
		if (isDateValid) {
			System.out.println("date is not expired......");
			isSchedule = isAnySchedule(scheduleConfgiBean);
			if (isSchedule) {
				nextScheduleTime = scheduleConfgiBean.getNexSchedule();
				fileName = new String(fileDir + "/"
						+ "ScheduleExportConfigBean" + ".xml");
				System.out.println(fileName);
				ScheduleExportConfigBean scheduleExportConfigBean = (ScheduleExportConfigBean) readConfigFile(
						processType, fileName);
				ScheduleExportController scheduleExportController = new ScheduleExportController(
						scheduleExportConfigBean, scheduleConfgiBean);
				System.out.println("aftre processing........ "
						+ scheduleConfgiBean.getNexSchedule());
				nextRunMap.put("run", true);
				nextRunMap.put("nextScheduleTime", nextScheduleTime);
			} else {
				System.out.println("no any schedule for exporrt");
				nextScheduleTime = scheduleConfgiBean.getNexSchedule();
				nextRunMap.put("run", false);
				nextRunMap.put("nextScheduleTime", nextScheduleTime);
			}
		} else {
			System.out.println("end date is less .......");
			return null;
		}
		return nextRunMap;
	}

	private boolean isEndDateValid(String enDate) {
		Date endDate = new Date(enDate);
		Date cuurDate = new Date();
		int yr = cuurDate.getYear();
		int mnth = cuurDate.getMonth();
		int date = cuurDate.getDate();
		int i = endDate.compareTo(new Date(yr, mnth, date));
		if (i >= 0) {
			return true;
		} else {
			return false;
		}
	}

	private Map checkImportSchedule() throws IOException {
		System.err.println("this is shubhra");
		System.out.println("**********************");
		logger.info("******XML file going to Create***** ");
		
		boolean isSchedule = false;
		String processType = "Import";
		String userDir = null;
		String fileDir = null;
		String fileName = null;
		Map nextRunMap = new HashMap();
		String nextScheduleTime = null;
		userDir = new String(getDocumentumHome().replace("\\", "/"));
		fileDir = new String(userDir + "/DaffIE");
		fileName = new String(fileDir + "/" + "IScheduleConfigBean" + ".xml");
		System.out.println(fileName);
		logger.info("** fileName ***"+fileName);
		IScheduleConfigBean scheduleConfgiBean = (IScheduleConfigBean) readConfigFile("ImportSchedule", fileName);
		System.out.println("before checking..............");
		logger.info("****After reading Config File ****** ");
		isSchedule = isAnySchedule(scheduleConfgiBean);
		Date endDate = new Date(scheduleConfgiBean.getEndDate());
		boolean isDateValid = isEndDateValid(scheduleConfgiBean.getEndDate());
		System.out.println("end date com .......... " + isDateValid);
		logger.info("****End Date Come ****** "+isDateValid);
		if (isDateValid) {
			if (isSchedule) {

				fileName = new String(fileDir + "/"+ "ScheduleImportConfigBean" + ".xml");

				ScheduleImportConfigBean schedulImportConfigBean = (ScheduleImportConfigBean) readConfigFile(processType, fileName);
				logger.info("**** After Calling readConfigFile if Date is Valid & isSchedule is true ****");
				ScheduleImportController scheduleImportController = new ScheduleImportController(schedulImportConfigBean, scheduleConfgiBean);
				logger.info("****After Calling  ScheduleImportController Method****** ");
				nextScheduleTime = scheduleConfgiBean.getNexSchedule();
				nextRunMap.put("run", true);
				nextRunMap.put("nextScheduleTime", nextScheduleTime);
				logger.info("*****  nextScheduleTime while isSchedule is true***** "+nextScheduleTime);
			} else {
				logger.info("****If isSchedule is false****** ");
				nextScheduleTime = scheduleConfgiBean.getNexSchedule();
				nextRunMap.put("run", false);
				nextRunMap.put("nextScheduleTime", nextScheduleTime);
				logger.info("*****  nextScheduleTime while isSchedule is false***** "+nextScheduleTime);
			}
		} else {
			System.out.println("end date is less............");
			logger.info("*****  end date is less............*****");
			return null;
		}
		return nextRunMap;
	}

	private String getDocumentumHome() 
	{
		Properties properties = System.getProperties();
		String SceduleFileRootPath = properties.getProperty("user.home");
		return SceduleFileRootPath;
	}

	private boolean isAnySchedule(ScheduleConfgiBean scheduleConfgiBean) {
		String date = null;
		date = scheduleConfgiBean.getNexSchedule();
		System.out.println("date .. " + date);
		logger.info("*****  Date is ....*****"+date);
		boolean isValid = checkDate(date);
		System.out.println("isvalid......... " + isValid);
		logger.info("isvalid......... " + isValid);
		if (isValid) {
			return true;
		} else {
			return false;
		}
	}

	private boolean checkDate(String date) {
		try {
			GregorianCalendar calendar = (GregorianCalendar) Calendar.getInstance();
			System.out.println("date... " + date);
			logger.info("*****  Date coming while calling method checkDate ....*****"+date);
			Date d = new Date(date);
			calendar.set(d.getYear() + 1900, d.getMonth(), d.getDate(), d.getHours(), d.getMinutes());
			System.out.println("new GregorianCalendar()..........  "+ new GregorianCalendar().getTime());
			logger.info("new GregorianCalendar()..........  "+ new GregorianCalendar().getTime());
			System.out.println("------ " + calendar.getTime());
			logger.info("Calender.getTime ------ " + calendar.getTime());
			Date currentTime = new Date();
			int i = calendar.compareTo(new GregorianCalendar());
			// new Date(currentTime.getYear(), currentTime.getMonth(),
			// currentTime.getDate())
			System.out.println("i = " + i);
			logger.info("i value while calling method checkDate  = " + i);
			if (i == 0) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage() + "  " + e.getCause());
			logger.info(e.getMessage() + "  " + e.getCause());
		}
		return false;
	}

	private Object readConfigFile(String type, String fileName)
			throws IOException {
		Object objClass = null;
		try {
			logger.info("********* Inside readConfigFile *****");
			objClass = new DaffIESchedularConfigurator().getSchedules(type, fileName);
			// Initially the method readFile() was called. Changed by Harsh for the 
			// support of Multiple scheduling.
		} catch (scheduleFileReaderException e) {
			e.printStackTrace();
		}
		return objClass;
	}

	public static void stop() {
		running = false;
	}
}
