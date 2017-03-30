package com.daffodil.documentumie.fileutil.metadata.api;

import java.util.HashMap;
import java.util.List;

import com.daffodil.documentumie.fileutil.metadata.exception.DMetadataReadException;


public interface MetadataReader {

    public HashMap getAttributes () throws DMetadataReadException;

    public List getRows (List attrName, String whereClause) throws DMetadataReadException;

    public int getRowsCount () throws DMetadataReadException;

}

