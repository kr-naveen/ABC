package com.kfas.util;

import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import com.daffodil.documentumie.businessoperationprocessorbean.ImportProcessorBean;
import com.daffodil.documentumie.dctm.exception.DDfException;
import com.daffodil.documentumie.fileutil.properties.PropertyFileLoader;
import com.daffodil.documentumie.iebusiness.pimport.model.ImportBusinessOperationProcessor;
import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.client.IDfTypedObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;

public class KfasApplyAclProcessor implements ImportBusinessOperationProcessor {
	String acl_name = null, cabinte_name = null;

	public String KfasApplyAcl() {
		Properties prop = PropertyFileLoader.loadUtilityConfigPropertyFile();
		// acl_name = prop.getProperty(folder_name);
		// System.out.println("acl_name----->"+acl_name);
		cabinte_name = prop.getProperty("cabinet");
		System.out.println("cabinte_name*****************" + cabinte_name);
		return cabinte_name;
	}

	@Override
	public ImportProcessorBean preImportProcess(ImportProcessorBean importProcessorBean) {
		System.out.println("KfasApplyAcl---------preImportProcess");

		return importProcessorBean;
	}

	@Override
	public ImportProcessorBean postImportProcess(ImportProcessorBean importProcessorBean) throws DDfException {
		System.out.println("KfasApplyAcl---------postImportProcess");
		/*
		 * IDfSession session = null; String objectId = null; IDfSysObject
		 * documentObj=null; try{ session =
		 * importProcessorBean.getCServicesProvider().getSession(); }
		 * catch(Exception
		 * ex){System.out.println("Error in getting document Object :"+ex); }
		 * 
		 * Properties prop = PropertyFileLoader.loadUtilityConfigPropertyFile();
		 * cabinte_name=prop.getProperty("cabinet");
		 * System.out.println("*******cabinte_name*****************"
		 * +cabinte_name); if(cabinte_name!=null){
		 * 
		 * acl_name = prop.getProperty(cabinte_name);
		 * System.out.println("acl_name on cabinet---->"+acl_name); } return
		 * importProcessorBean;
		 */

		System.out.println("Applying ACL through Post Processor");

		IDfSession session = null;
		String objectId = null;
		IDfSysObject documentObj = null;

		HashMap metadataMap = (HashMap) importProcessorBean.getMetadataMap();
		try {
			session = importProcessorBean.getCServicesProvider().getSession();
			objectId = importProcessorBean.getObjectId();

			documentObj = (IDfSysObject) session.getObject(new DfId(objectId));
			System.out.println("Document Object Name :: "+ documentObj.getObjectName());
		} catch (Exception ex) {
			System.out.println("Error in getting session:" + ex);
		}
		KfasApplyACL getObj = new KfasApplyACL();
		String destination_path = (String) metadataMap.get("r_folder_path");
		System.out.println("destination_path-----" + destination_path);

		Properties prop = PropertyFileLoader.loadUtilityConfigPropertyFile();
		String doc_type = prop.getProperty("Document_Type");

		String doc_acl = prop.getProperty("Document_ACL");
		getObj.applyDocumentTypeACLOnFolder(session, doc_acl, destination_path,documentObj);

		/*
		 * if (destination_path.contains(doc_type)) { String doc_acl =
		 * prop.getProperty("Document_ACL");
		 * getObj.applyDocumentTypeACLOnFolder(session, doc_acl,
		 * destination_path, documentObj); }
		 */

		return importProcessorBean;

	}

	@Override
	public void releaseArtifcats() throws Exception {
		// TODO Auto-generated method stub

	}

}
