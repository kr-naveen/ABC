package com.daffodil.documentumie.fileutil.configurator.bean;

public class ConfigBean {

	private String repoName;

	private String userName;

	private String domain;

	private String loggerFileName;

	private String loggerLevel;

	private String loggerAppender;

	private String logAppend;

	public ConfigBean () {
	}

	public String getDomain () {
		return domain;
	}

	public void setDomain (String val) {
		this.domain = val;
	}

	public String getLoggerAppender () {
		return loggerAppender;
	}

	public void setLoggerAppender (String val) {
		this.loggerAppender = val;
	}

	public String getLoggerFileName () {
		return loggerFileName;
	}

	public void setLoggerFileName (String val) {
		this.loggerFileName = val;
	}

	public String getLoggerLevel () {
		return loggerLevel;
	}

	public void setLoggerLevel (String val) {
		this.loggerLevel = val;
	}

	public String getRepoName () {
		return repoName;
	}

	public void setRepoName (String val) {
		this.repoName = val;
	}

	public String getUserName () {
		return userName;
	}

	public void setUserName (String val) {
		this.userName = val;
	}

	public String getLogAppend() {
		return logAppend;
	}

	public void setLogAppend(String logAppend) {
		this.logAppend = logAppend;
	}

}

