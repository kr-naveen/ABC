package com.daffodil.documentumie.iebusiness.export.processor;

import java.util.Map;

import com.daffodil.documentumie.businessoperationprocessorbean.ExportProcessorBean;
import com.daffodil.documentumie.dctm.exception.DDfException;

public interface ExportBusinessOperationProcessor {
	
	public void preExportProcess(ExportProcessorBean exportProcessorBean) throws DDfException;

	public void postExportProcess(ExportProcessorBean exportProcessorBean);

	public void releaseArtifcats() throws Exception;

}
