package com.kfas.util;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

import com.daffodil.documentumie.filehandler.LocalSystemFileHandler;
import com.daffodil.documentumie.businessoperationprocessorbean.ImportProcessorBean;
import com.daffodil.documentumie.dctm.exception.DDfException;
import com.daffodil.documentumie.fileutil.properties.PropertyFileLoader;
import com.daffodil.documentumie.iebusiness.pimport.model.ImportBusinessOperationProcessor;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.IDfId;


public class KfasImportObjectId implements ImportBusinessOperationProcessor {
	
	public KfasImportObjectId() {
		try{
			Properties prop = PropertyFileLoader.loadUtilityConfigPropertyFile(); 
			System.out.println("Loaded.............KfasImportObjectId");
			}catch(Exception ex){
				System.out.println("Exception occured in fetching property values :"+ex);
			}
	}

	@Override
	public ImportProcessorBean preImportProcess(
			ImportProcessorBean importProcessorBean) {
		System.out.println("KfasImportObjectId.preImportProcess()");
		return importProcessorBean;
	}

	@Override
	public ImportProcessorBean postImportProcess(
			ImportProcessorBean importProcessorBean) throws DDfException {
		
		String objectId = null;
		IDfSysObject documentObj=null;
		IDfSysObject marketObject = null;
		
		objectId = importProcessorBean.getObjectId();
		System.out.println("KfasImportObjectId.java----->>objectId------->> "+objectId);
		
		System.out.println("KfasImportObjectId.postImportProcess()");
		
		IDfSession session = null;	
	
		
		return importProcessorBean;
	}

	@Override
	public void releaseArtifcats() throws Exception {
		// TODO Auto-generated method stub
		
	}
	

}
