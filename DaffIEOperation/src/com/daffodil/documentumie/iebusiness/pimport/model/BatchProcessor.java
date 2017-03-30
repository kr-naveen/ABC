package com.daffodil.documentumie.iebusiness.pimport.model;

import java.util.ArrayList;
import java.util.Map;

import com.daffodil.documentumie.dctm.exception.DDfException;

public interface BatchProcessor {

	public void postBatchCompletion(Map map) throws DDfException;
	public void preBatchCompletion(Map map) throws DDfException;
}
