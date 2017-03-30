package com.kfas.util;
import com.documentum.com.DfClientX;
import com.documentum.com.IDfClientX;
import com.documentum.fc.client.IDfACL;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfFolder;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;

public class KfasApplyACL {
	public void applyDocumentTypeACLOnFolder(IDfSession session, String doc_acl, String destination_path, IDfSysObject documentObj)
	{
		IDfCollection queryColl = null;
		IDfCollection queryCollForId = null;
		IDfCollection queryCollForIdOnDocument = null;
		try{
			IDfClientX clientx = new DfClientX();
			IDfQuery q = clientx.getQuery();				
			String aclQuery, IdQuery= null;
									 
			// Check Existence of Document Type ACL 
			aclQuery = "select count(object_name) from dm_acl where object_name='"+doc_acl+"'";			
			q.setDQL(aclQuery);
			queryColl = q.execute(session,IDfQuery.DF_READ_QUERY);
			
			IDfACL docACL =null;	
																 
			if(queryColl.next()){					
				System.out.println("***********Document ACL querycoll is not null");
				int count=Integer.parseInt(queryColl.getValueAt(0).toString());
				
				if(count == 0){
				System.out.println("Document ACL doesn't Exist in Repository ");
				}
				else{
					// ACL Exists so Check whether its same as Applied on document or not
					// If not then apply it on doc
					 System.out.println("ACL Exist");
					 
					IDfFolder folderVar = session.getFolderByPath(destination_path);
					System.out.println("Folder variable of destination path :: "+folderVar);
					String appliedACL = folderVar.getACL().toString();
					System.out.println("Applied ACL on Folder :: "+appliedACL);
					System.out.println("Applied ACL on Document Type Folder is :"+appliedACL+"But Document ACL should be :"+doc_acl);
											
					if(!appliedACL.equals(doc_acl))
					{	
						System.out.println("Applied acl is different from Document ACL, So going to apply Document ACL");
						IdQuery = "select r_object_id from dm_acl where object_name='"+doc_acl+"'";										 
						//System.out.println("Query for Object Id of already created ACL "+doc_acl +"in Documentum is :"+ IdQuery);
						q.setDQL(IdQuery);
						queryCollForId = q.execute(session,IDfQuery.DF_READ_QUERY);
						System.out.println("To fetch Object Id, call Execute method.........");
						if(queryCollForId.next()){
							String Id = queryCollForId.getValueAt(0).toString();
							// logger.info("Id of ACL is :"+Id);
							if(Id!=null && Id!=" "){
								docACL =(IDfACL)session.getObject(new DfId(Id));
								System.out.println("Going to Apply Document ACL on folder :");				
								//applyACLOnFolder(session,docACL,destination_path);
							}
						}										
				    }
					
					if(documentObj!=null){
						System.out.println("Uploaded Document Object is not Null");
						String uploadedDocACL = documentObj.getACLName().toString();
						System.out.println("Applied ACL on Uploaded Document is :"+uploadedDocACL+"But Uploaded Document ACL should be :"+doc_acl);
						if(!uploadedDocACL.equals(doc_acl))
						{
							IdQuery = "select r_object_id from dm_acl where object_name='"+doc_acl+"'";										 
						System.out.println("Query for Object Id of already created ACL "+doc_acl +"in Documentum is :"+ IdQuery);
							q.setDQL(IdQuery);
							queryCollForIdOnDocument = q.execute(session,IDfQuery.DF_READ_QUERY);
							System.out.println("To fetch Object Id, call Execute method.........");
							if(queryCollForIdOnDocument.next()){
								String Id = queryCollForIdOnDocument.getValueAt(0).toString();
								// logger.info("Id of ACL is :"+Id);
								if(Id!=null && Id!=" "){
									docACL =(IDfACL)session.getObject(new DfId(Id));
									System.out.println("Going to apply Document ACL on Uploaded Document :");				
									applyDocumentTypeACLonUplodedDocument(session,docACL,documentObj);
								}
							}
						} 
					}
					
				}
			
		    }else{
				System.out.println(" ACL doesn't exist in database");
			}
		}catch (Exception ex) {
			System.out.println("Exception Occured in Execute method "+ex);
		}finally {
			if (queryColl != null) {
				try {
					queryColl.close();
				}catch (DfException e) {e.printStackTrace();}
			}
			if(queryCollForId != null) {
			  try {
				  queryCollForId.close();
			  }catch (DfException e) {e.printStackTrace();}
		  }	
			if(queryCollForIdOnDocument != null) {
				  try {
					  queryCollForIdOnDocument.close();
				  }catch (DfException e) {e.printStackTrace();}
			  }	
		}		
	}	
	
	
	public void applyACLOnFolder(IDfSession session, IDfACL docACL, String destination_path) {
	System.out.println("---------- Going to Apply Document ACL on Folder------");
		
		try{
			System.out.println("Document Folder Path :"+destination_path);
			// Created Folder Object & apply ACL on that
			IDfFolder folderVar = session.getFolderByPath(destination_path);
			folderVar.setACL(docACL);
			System.out.println("Document ACL is applied on Folder");
			folderVar.save();			
		}catch(Exception ex){
			System.out.println("Exception occured in Applying ACL on Folder");
			ex.printStackTrace();
		}
			
	}
	
	
	public void applyDocumentTypeACLonUplodedDocument(IDfSession session, IDfACL docACL,IDfSysObject documentObj) {
		System.out.println("---------- Going to Apply Document ACL On Uploaded Document------");
		
		try{
			System.out.println("Uploaded Document Name :"+documentObj.getObjectName());
			// Apply ACL on Uploaded Document
			documentObj.setACL(docACL);			
			System.out.println("ACL is applied on Uploaded Document");
			documentObj.save();			
		}catch(Exception ex){
			System.out.println("Exception occured in Applying ACL Uploaded Document");
			ex.printStackTrace();
		}
			
	}
}
