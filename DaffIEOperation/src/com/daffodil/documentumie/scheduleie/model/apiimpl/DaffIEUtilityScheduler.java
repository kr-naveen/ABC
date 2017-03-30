package com.daffodil.documentumie.scheduleie.model.apiimpl;
public class DaffIEUtilityScheduler {
	private static DaffIEUtilityScheduler schedulerInstance;
	
	private ImportSchedular iSchedule = null;
	private ExportSchedular eSchedule = null;
	
	private DaffIEUtilityScheduler(){
		initialize();
	}
	
	public static DaffIEUtilityScheduler getInstance(){
		if (schedulerInstance == null){
			schedulerInstance = new DaffIEUtilityScheduler();
		}
		return schedulerInstance;
	}

	public void initialize()
	{
		iSchedule = new ImportSchedular(); 
		eSchedule = new ExportSchedular();
		doStart();
	}
	public void doStart()
	{
		if(iSchedule!=null){
			iSchedule.setRunning(true);
			Thread importThread = new Thread(iSchedule,"Import Thread");
			//importThread.setDaemon(true);
			//Runtime.getRuntime().addShutdownHook(importThread);
			importThread.start();
		}
		if(eSchedule!=null){		
			eSchedule.setRunningExport(true);
			Thread exportThread = new Thread(eSchedule,"Export Thread");	
			//exportThread.setDaemon(true);
			//Runtime.getRuntime().addShutdownHook(exportThread);
			exportThread.start();
		}
	}	
	public void doStop()
	{
		if(iSchedule!=null)
		{
			iSchedule.setRunning(false);
		}
		if(eSchedule!=null)
		{
			eSchedule.setRunningExport(false);
		}
	}
}
