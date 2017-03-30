package com.daffodil.documentumie.scheduleie.model.apiimpl;

import java.util.Comparator;
import java.util.Date;

import com.daffodil.documentumie.scheduleie.bean.IScheduleConfigBean;

public class IScheduleSort implements Comparator<IScheduleSort> {

	private int index;
	private IScheduleConfigBean configBean;
	private Date nextScheduleDate;
	
	public IScheduleSort(int index,IScheduleConfigBean configBean,Date nextScheduleDate)
	{
		this.index=index;
		this.configBean=configBean;
		this.nextScheduleDate=nextScheduleDate;
	}
	
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public IScheduleConfigBean getConfigBean() {
		return configBean;
	}

	public void setConfigBean(IScheduleConfigBean configBean) {
		this.configBean = configBean;
	}

	public Date getNextScheduleDate() {
		return nextScheduleDate;
	}

	public void setNextScheduleDate(Date nextScheduleDate) {
		this.nextScheduleDate = nextScheduleDate;
	}

	@Override
	public int compare(IScheduleSort arg0, IScheduleSort arg1) {
		if(arg0.getNextScheduleDate().getTime() - arg1.getNextScheduleDate().getTime()>=0)
		{
			return 1;
		}
		else{
			return 0;
		}
	}

}
