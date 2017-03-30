package com.daffodil.documentumie.fileutil.metadata.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.daffodil.documentumie.fileutil.metadata.exception.DMetadataWriteException;

public interface MetadataWriter {
	
	public void wirteAttributes(List attributes) throws DMetadataWriteException;
//    public void writeRow (List row) throws DMetadataWriteException;
    public void writeRow(Map metadtaMapForReportFile) throws DMetadataWriteException;

}

