package com.daffodil.documentumie.scheduleie.model.Exception;

public class scheduleFileWriterException extends Exception{

    public scheduleFileWriterException () {
    	super();
    }

	public scheduleFileWriterException(String arg0) {
		super(arg0);
	}
    
	public scheduleFileWriterException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public scheduleFileWriterException(Throwable arg1) {
		super(arg1);
	}
}
