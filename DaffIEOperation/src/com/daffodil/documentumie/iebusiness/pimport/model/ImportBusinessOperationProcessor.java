package com.daffodil.documentumie.iebusiness.pimport.model;

import com.daffodil.documentumie.businessoperationprocessorbean.ImportProcessorBean;
import com.daffodil.documentumie.dctm.exception.DDfException;

public interface ImportBusinessOperationProcessor {

	public ImportProcessorBean preImportProcess(ImportProcessorBean importProcessorBean);

	public ImportProcessorBean postImportProcess(ImportProcessorBean importProcessorBean) throws DDfException;
	
	public void releaseArtifcats() throws Exception;
	
}
