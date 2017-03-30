package com.kfas.util;

import java.util.Map;

import com.daffodil.documentumie.dctm.exception.DDfException;
import com.daffodil.documentumie.iebusiness.pimport.model.BatchProcessor;

public class KfasBatchProcessor implements BatchProcessor  {

	@Override
	public void postBatchCompletion(Map map) throws DDfException {
		// TODO Auto-generated method stub
		System.out.println("**postBatchCompletion**");
		
	}

	@Override
	public void preBatchCompletion(Map map) throws DDfException {
		// TODO Auto-generated method stub
		System.out.println("**preBatchCompletion**");
		
	}

}
