package com.daffodil.documentumie.fileutil.metadata.exception;


public class DMetadataReadException extends Exception {

    public DMetadataReadException () {
    	super();
    }

	public DMetadataReadException(String arg0) {
		super(arg0);
	}
    
	public DMetadataReadException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public DMetadataReadException(Throwable arg1) {
		super(arg1);
	}
}

