package com.daffodil.documentumie.scheduleie.model.apiimpl;

import java.util.Comparator;
import java.util.Date;

import com.daffodil.documentumie.scheduleie.bean.EScheduleConfigBean;
import com.daffodil.documentumie.scheduleie.bean.IScheduleConfigBean;

public class EScheduleSort implements Comparator<EScheduleSort> {

	private int index;
	private EScheduleConfigBean configBean;
	private Date nextScheduleDate;

	public EScheduleSort(int index, EScheduleConfigBean configBean,
			Date nextScheduleDate) {
		super();
		this.index = index;
		this.configBean = configBean;
		this.nextScheduleDate = nextScheduleDate;
	}

	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public EScheduleConfigBean getConfigBean() {
		return configBean;
	}
	public void setConfigBean(EScheduleConfigBean configBean) {
		this.configBean = configBean;
	}
	public Date getNextScheduleDate() {
		return nextScheduleDate;
	}
	public void setNextScheduleDate(Date nextScheduleDate) {
		this.nextScheduleDate = nextScheduleDate;
	}

	@Override
	public int compare(EScheduleSort arg0, EScheduleSort arg1) {
		if(arg0.getNextScheduleDate().getTime() - arg1.getNextScheduleDate().getTime()>=0)
		{
			return 1;
		}
		else{
			return 0;
		}
	}

}
