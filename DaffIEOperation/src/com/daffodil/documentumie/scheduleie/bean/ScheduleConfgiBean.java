package com.daffodil.documentumie.scheduleie.bean;

public class ScheduleConfgiBean {

	String startDate = null;
	String hours = null;
	String minute = null;
	String repeat = null;
	String frequency = null;
	String scheduleFor = null;
	String endDate = null;
	String nexSchedule = null;
	ScheduleExportConfigBean  exportConfigBean;
	ScheduleImportConfigBean  importConfigBean;
	
	
	public ScheduleImportConfigBean getImportConfigBean() {
		return importConfigBean;
	}
	public void setImportConfigBean(ScheduleImportConfigBean importConfigBean) {
		this.importConfigBean = importConfigBean;
	}
	public ScheduleExportConfigBean getExportConfigBean() {
		return exportConfigBean;
	}
	public void setExportConfigBean(ScheduleExportConfigBean exportConfigBean) {
		this.exportConfigBean = exportConfigBean;
	}
	public String getNexSchedule() {
		return nexSchedule;
	}
	public void setNexSchedule(String nexSchedule) {
		this.nexSchedule = nexSchedule;
	}
	public String getRepeat() {
		return repeat;
	}
	public void setRepeat(String repeat) {
		this.repeat = repeat;
	}
	public String getFrequency() {
		return frequency;
	}
	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}
	public String getScheduleFor() {
		return scheduleFor;
	}
	public void setScheduleFor(String scheduleFor) {
		this.scheduleFor = scheduleFor;
	}
	public String getHours() {
		return hours;
	}
	public void setHours(String hours) {
		this.hours = hours;
	}
	public String getMinute() {
		return minute;
	}
	public void setMinute(String minute) {
		this.minute = minute;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
}
