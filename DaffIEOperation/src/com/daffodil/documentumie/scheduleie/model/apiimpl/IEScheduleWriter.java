package com.daffodil.documentumie.scheduleie.model.apiimpl;

import com.daffodil.documentumie.scheduleie.bean.ScheduleConfgiBean;
import com.daffodil.documentumie.scheduleie.configurator.DaffIESchedularConfigurator;
import com.daffodil.documentumie.scheduleie.model.Exception.scheduleFileWriterException;

public class IEScheduleWriter {
public static void main(String[] args) {
	new IEScheduleWriter().writeScheduleXMLFile(null);
}
	public void writeScheduleXMLFile(Object iConfigBean){
		ScheduleConfgiBean scheduleConfgiBean = new ScheduleConfgiBean();
		writeScheduleBean(scheduleConfgiBean);
		try {
//			DaffIESchedularConfigurator.createXMLFile(scheduleConfgiBean);
			new DaffIESchedularConfigurator().saveAndUpdateSchedule(iConfigBean);
		} catch (scheduleFileWriterException e) {
			e.printStackTrace();
		}
		
	}

	private void writeScheduleBean(ScheduleConfgiBean scheduleConfgiBean) {
//		scheduleConfgiBean.setDate("07/08/2009");
//		scheduleConfgiBean.setHours(2);
//		scheduleConfgiBean.setMinute(30);
		scheduleConfgiBean.setFrequency("1");
		scheduleConfgiBean.setRepeat("hour");
		scheduleConfgiBean.setScheduleFor("Import");
	}
}
