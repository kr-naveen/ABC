package com.daffodil.documentumie.fileutil.logger;

import java.io.IOException;
import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.SimpleLayout;

import com.daffodil.documentumie.fileutil.configurator.bean.ConfigBean;

public class IELogger {
	public static final int DEBUG = 1;
	public static final int INFO = 2;
	public static final int WARN = 3;
	public static final int ERROR = 4;
	public static final int FATAL = 5;
	private static final int ALL = 6;
	private static final int OFF = 7;
	public String level;
	public String loggerFileName = null;;
	public String loggerAppender = null;;
	private boolean logAppend = false;
	
	
	public IELogger() {
		//System.out.println("inside IELogger default constructor.");
		
		initlize();
	}

	public IELogger(ConfigBean bean) {
		init(bean);
		initlize();
	}

	private void init(ConfigBean bean) {
		System.out.println("bean.getLoggerFileName() : "+bean.getLoggerFileName());
		this.level = (bean.getLoggerLevel() == null ? "ALL" :bean.getLoggerLevel());
		this.loggerFileName = bean.getLoggerFileName()== null ? "":bean.getLoggerFileName();
		this.loggerAppender = bean.getLoggerAppender()== null ? "FileAppender":bean.getLoggerAppender();
		this.logAppend = ("True".equalsIgnoreCase(bean.getLogAppend())?true:false);
	}

	Logger logger = Logger.getLogger(IELogger.class);

	private void initlize() {
		Appender appender = null;
		Layout layout = null;

		if ("DEBUG".equalsIgnoreCase(level)) {
			String pattern = "Log: %d [%t] %-5p %c - %m %n";
			layout = new PatternLayout(pattern);
		} else {
			layout = new SimpleLayout();
		}
		if (loggerAppender.equals("ConsoleAppender")) {
			appender = new ConsoleAppender(layout);
		} else if (loggerAppender.equals("FileAppender")) {
			try {
				appender = new FileAppender(layout, loggerFileName, logAppend);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.err
						.println("Problem while creating file appender. So going to add console appender");
				e.printStackTrace();
				appender = new ConsoleAppender(layout);
			}
		}
		logger.addAppender(appender);
		switch (getLevel(level)) {
		case DEBUG:
			logger.setLevel(Level.DEBUG);
			break;
		case INFO:
			logger.setLevel(Level.INFO);
			break;
		case WARN:
			logger.setLevel(Level.WARN);
			break;
		case ERROR:
			logger.setLevel(Level.ERROR);
			break;
		case FATAL:
			logger.setLevel(Level.FATAL);
			break;
		case OFF:
			logger.setLevel(Level.OFF);
			break;
		case ALL:
			logger.setLevel(Level.ALL);
			break;
		default:
			break;
		}
	}

	private int getLevel(String lvl) {
		if (lvl.equals("DEBUG"))
			return 1;
		else if (lvl.equals("INFO"))
			return 2;
		else if (lvl.equals("WARN"))
			return 3;
		else if (lvl.equals("ERROR"))
			return 4;
		else if (lvl.equals("FATAL"))
			return 5;
		else if (lvl.equals("ALL"))
			return 6;
		else
			return 7;
	}

	public void writeLog(String msg, int level) {
		System.out.println("inside write log");
		switch (level) {
		case DEBUG:
			logger.debug(msg);
			break;
		case INFO:
			logger.info(msg);
			break;
		case WARN:
			logger.warn(msg);
			break;
		case ERROR:
			logger.error(msg);
			break;
		case FATAL:
			logger.fatal(msg);
			break;
		case ALL:
			logger.fatal(msg);
			break;
		default:
			break;
		}
	}
}
