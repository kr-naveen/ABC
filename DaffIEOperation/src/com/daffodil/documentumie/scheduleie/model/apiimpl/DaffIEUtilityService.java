package com.daffodil.documentumie.scheduleie.model.apiimpl;

import com.daffodil.documentumie.iebusiness.ImportExportMain;

public class DaffIEUtilityService {

	public static void main(String args[])
	{		

		/*DaffIEUtilityScheduler main = DaffIEUtilityScheduler.getInstance();
		main.doStart();
		if(args!=null && args.length>0)
		{
			DaffIEUtilityScheduler main = DaffIEUtilityScheduler.getInstance();
			main.doStart();
		}
		else{*/
		//	new ImportExportMain().showIEUI(1);
		/*}*/
			DaffIEUtilityScheduler main = DaffIEUtilityScheduler.getInstance();
			//main.doStart();
	}	
	
	/*
	 * The below method will be called through the Windows Service
	 */
	public void startService()
	{
		DaffIEUtilityScheduler main = DaffIEUtilityScheduler.getInstance();
		main.doStart();
	}
}
