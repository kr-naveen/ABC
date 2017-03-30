package com.daffodil.documentumie.filehandler;


import java.io.File;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemOptions;
import org.apache.commons.vfs.Selectors;
import org.apache.commons.vfs.UserAuthenticator;
import org.apache.commons.vfs.VFS;
import org.apache.commons.vfs.auth.StaticUserAuthenticator;
import org.apache.commons.vfs.impl.DefaultFileSystemConfigBuilder;
import org.apache.commons.vfs.impl.DefaultFileSystemManager;
import org.apache.commons.vfs.impl.StandardFileSystemManager;
import org.apache.commons.vfs.provider.sftp.SftpFileSystemConfigBuilder;

import com.daffodil.documentumie.dctm.exception.DDfException;

public class SFTPFileHandler implements ContentFileHandler {
	
	private String ftpUrl;
	private String userName;
	private String password;
    private DefaultFileSystemManager fsManager = null;
    private FileSystemOptions opts = null;
	
    public String getFTPUrl()
    {
    	return ftpUrl;
    }

	public SFTPFileHandler(String ftpUrl, String userName, String password) {
		this.ftpUrl = ftpUrl;
		this.userName = userName;
		this.password = password;
	}
	
	public boolean connect() throws DDfException
	{
		boolean status=false;
		/*ftpUrl = "124.153.112.4";
		userName="baroda";
		password ="BArodA5NokiA";*/
		try {
            this.fsManager = (DefaultFileSystemManager)VFS.getManager();	        
	        UserAuthenticator auth = new StaticUserAuthenticator(null, this.userName,this.password);	                
	        this.opts = new FileSystemOptions();	       
            DefaultFileSystemConfigBuilder.getInstance().setUserAuthenticator(opts,  auth);	                  
            SftpFileSystemConfigBuilder.getInstance().setStrictHostKeyChecking(opts, "no");
            this.fsManager.resolveFile("sftp://" + this.ftpUrl , opts);
            System.out.println("SFTP connection successfully established to " + "sftp://" + this.ftpUrl);
            status=true;    
            /*fsManager.freeUnusedResources();
            fsManager.close();*/
        } catch (FileSystemException ex) {
        	status=false;
            throw new DDfException("Wrong User Name or Password", ex);
        }
		return status;
	}

	@Override
	public String getFile(String metadataPath) throws DDfException {
		String fileName=null;
		String tempDir = new File("").getAbsolutePath();
		StandardFileSystemManager manager=null;
		tempDir = tempDir+"/temps";
		try {
			if(metadataPath!=null && metadataPath.length()>0)
			{
				if(metadataPath.contains("\\"))
				{
				metadataPath = metadataPath.replaceAll("\\", "/");
				}
				System.out.println("Metadata FTP path is"+metadataPath);
				fileName = metadataPath.substring(metadataPath.lastIndexOf("/")+1,metadataPath.length());
				manager = new StandardFileSystemManager();
			    
				String sftpUri = "sftp://"+this.userName+":"+this.password+"@"+this.ftpUrl;    
				System.out.println("sftpUri"+sftpUri);
				FileSystemOptions opts = new FileSystemOptions();
				SftpFileSystemConfigBuilder.getInstance().setStrictHostKeyChecking(opts, "no");

				manager.init();
				FileObject fileObject = manager.resolveFile(sftpUri+metadataPath, opts);
				System.out.println("File Object==>"+fileObject);
				FileObject localFileObject = manager.resolveFile(tempDir+"/"+fileName);

				localFileObject.copyFrom(fileObject, Selectors.SELECT_SELF);			    
			   
			}
		} catch(FileSystemException ex)
		{
			System.out.println(ex.getMessage()+" cause:-"+ex.getCause());
			throw new DDfException(ex.getMessage()+""+ex.getCause());
		}
		 finally {
		      manager.close();
		    }
		return tempDir+"/"+fileName;
	}

	@Override
	public boolean saveFile(String srcFilePath, String destFilePath)throws DDfException {			
		boolean status=false;		
		String fileName = srcFilePath.substring(srcFilePath.lastIndexOf("\\")+1);
		System.out.println("Local file Path:"+srcFilePath);
		System.out.println("File Name is:"+fileName);
		System.out.println("Destination path is:"+destFilePath);
		if(!destFilePath.endsWith("/"))
		 {
			 destFilePath = destFilePath+"/";
		 }
	    StandardFileSystemManager manager = new StandardFileSystemManager();
	    try {
	      String sftpUri = "sftp://"+userName+":"+password+"@"+ftpUrl;         
	      FileSystemOptions opts = new FileSystemOptions();
	      SftpFileSystemConfigBuilder.getInstance().setStrictHostKeyChecking(opts, "no");	      
	     // manager.init();
	      FileObject fileObject = manager.resolveFile(sftpUri+destFilePath+fileName, opts);
	      System.out.println("File Object==>"+fileObject);
	      FileObject localFileObject = manager.resolveFile(srcFilePath);	      	      
	      fileObject.copyFrom(localFileObject, Selectors.SELECT_SELF);
	      status=true;
	    } catch (Exception ex) {
	    	throw new DDfException(ex.getMessage()+""+ex.getCause());
	    }finally {
	      manager.close();
	    }
		return status;
	}
	
	public void relocateFiles(String srcFilePath, String destFilePath)throws DDfException
	{
		String localFilePath = getFile(srcFilePath);
		System.out.println("Locally Downloaded Path is"+localFilePath);
		String fileName = srcFilePath.substring(srcFilePath.lastIndexOf("/")+1);
		System.out.println("SFTP source file Path:"+srcFilePath);
		System.out.println("SFTP File Name is:"+fileName);
		System.out.println("SFTP Destination path is:"+destFilePath);
		
	    StandardFileSystemManager manager = new StandardFileSystemManager();
	     
	    try {
	    	manager.init();
	      String sftpUri = "sftp://"+userName+":"+password+"@"+ftpUrl;         
	      FileSystemOptions opts = new FileSystemOptions();
	      SftpFileSystemConfigBuilder.getInstance().setStrictHostKeyChecking(opts, "no");	      
	      FileObject ftpSrcFileObject = manager.resolveFile(sftpUri+srcFilePath, opts);
	      FileObject localSrcFileObject = manager.resolveFile(localFilePath);
	      FileObject fileObjectFTPDest = manager.resolveFile(sftpUri+destFilePath, opts);
	      fileObjectFTPDest.copyFrom(localSrcFileObject, Selectors.SELECT_SELF);
	      localSrcFileObject.delete();// Deleting Locally downloaded file
	      ftpSrcFileObject.delete();// Deleting FTP file
	      
	    } catch (Exception ex) {
	    	System.out.println(ex);
	    	throw new DDfException(ex.getMessage()+""+ex.getCause());
	    }finally {
	      manager.close();
	    }
	}
	public void relocateFilesToProcessdOrException(String srcFilePath, String destFilePath,String localPath)throws DDfException
	{
		String localFilePath = localPath;
		System.out.println("Locally Downloaded Path is"+localFilePath);
		String fileName = srcFilePath.substring(srcFilePath.lastIndexOf("/")+1);
		System.out.println("SFTP source file Path:"+srcFilePath);
		System.out.println("SFTP File Name is:"+fileName);
		System.out.println("SFTP Destination path is:"+destFilePath);
		
	    StandardFileSystemManager manager = new StandardFileSystemManager();
	     
	    try {
	    	manager.init();
	      String sftpUri = "sftp://"+userName+":"+password+"@"+ftpUrl;         
	      FileSystemOptions opts = new FileSystemOptions();
	      SftpFileSystemConfigBuilder.getInstance().setStrictHostKeyChecking(opts, "no");	      
	      FileObject ftpSrcFileObject = manager.resolveFile(sftpUri+srcFilePath, opts);
	      FileObject localSrcFileObject = manager.resolveFile(localFilePath);
	      FileObject fileObjectFTPDest = manager.resolveFile(sftpUri+destFilePath, opts);
	      fileObjectFTPDest.copyFrom(localSrcFileObject, Selectors.SELECT_SELF);
	      localSrcFileObject.delete();// Deleting Locally downloaded file
	      ftpSrcFileObject.delete();// Deleting FTP file
	      
	    } catch (Exception ex) {
	    	System.out.println(ex);
	    	throw new DDfException(ex.getMessage()+""+ex.getCause());
	    }finally {
	      manager.close();
	    }
	}
	public void relocateAllFiles(String srcFilePath, String destFilePath)throws DDfException
	{
		System.out.println("srcFilePath"+srcFilePath);
		System.out.println("destFilePath"+destFilePath);
		 StandardFileSystemManager manager = new StandardFileSystemManager();
		    try {
		    	manager.init();
		      String sftpUri = "sftp://"+userName+":"+password+"@"+ftpUrl;         
		      FileSystemOptions opts = new FileSystemOptions();
		      SftpFileSystemConfigBuilder.getInstance().setStrictHostKeyChecking(opts, "no");	      
		      FileObject ftpSrcDirObject = manager.resolveFile(sftpUri+srcFilePath, opts);// Locating the Root directory
		      if(ftpSrcDirObject!=null)
		      {
		    	  FileObject[] children = ftpSrcDirObject.getChildren();
		    	  if(children!=null&& children.length>0)
		    	  {
		    		  for (int i = 0; i < children.length; i++) 
		    		  {
		    			  FileObject child = children[i];
		    			  if(child.getType().toString().equalsIgnoreCase("file"))
		    			  {
		    				 String childName = child.getName().toString();
		    				 String tempChildName= childName.toLowerCase();
		    				 if(tempChildName.contains(".pdf"))
		    				 {
		    					 childName = childName.substring(childName.lastIndexOf("/")+1);// Extracting the File name with extension only
		    					 String fName = childName;
		    					 childName = srcFilePath+childName;// Complete path of the File on the SFTP
		    					 System.out.println("childName"+childName);
		    					 String localFilePath = getFile(childName);
		    					 System.out.println("localFilePath"+localFilePath);
		    					 FileObject ftpSrcFileObject = manager.resolveFile(sftpUri+childName, opts);
		    					 FileObject localSrcFileObject = manager.resolveFile(localFilePath);
		    					 FileObject fileObjectFTPDest = manager.resolveFile(sftpUri+destFilePath+fName, opts);
		    					 fileObjectFTPDest.copyFrom(localSrcFileObject, Selectors.SELECT_SELF);
		    					 localSrcFileObject.delete();// Deleting Locally downloaded file
		    					 ftpSrcFileObject.delete();// Deleting FTP file
		    				 }
		    				 
		    			  }
					  } 
		    	  }
		      }
		      
		    } catch (Exception ex) {
		    	throw new DDfException(ex.getMessage()+""+ex.getCause());
		    }finally {
		      manager.close();
		    }
	}
	public void moveDocumentFromExceptionToRoot(String srcFilePath, String destFilePath)throws DDfException
	{
		System.out.println("srcFilePath in Movement from Exception"+srcFilePath);
		System.out.println("destFilePath in Movement from Exception"+destFilePath);
		StandardFileSystemManager manager = new StandardFileSystemManager();
	    try {
	    	manager.init();
	      String sftpUri = "sftp://"+userName+":"+password+"@"+ftpUrl;         
	      FileSystemOptions opts = new FileSystemOptions();
	      SftpFileSystemConfigBuilder.getInstance().setStrictHostKeyChecking(opts, "no");	      
	      FileObject ftpSrcDirObject = manager.resolveFile(sftpUri+srcFilePath, opts);// Locating the Root directory
	      if(ftpSrcDirObject!=null)
	      {
	    	  FileObject[] children = ftpSrcDirObject.getChildren();
	    	  if(children!=null&& children.length>0)
	    	  {
	    		  for (int i = 0; i < children.length; i++) 
	    		  {
	    			  FileObject child = children[i];
	    			  if(child.getType().toString().equalsIgnoreCase("file"))
	    			  {
	    				 String childName = child.getName().toString();
	    				 String tempChildName= childName.toLowerCase();
	    				 if(tempChildName.contains(".pdf"))
	    				 {
	    					 childName = childName.substring(childName.lastIndexOf("/")+1);// Extracting the File name with extension only
	    					 String fName = childName;
	    					 System.out.println("==CHILD Name is"+childName);
		    				 childName = srcFilePath+childName;// Complete path of the File on the SFTP
		    				 System.out.println("Child Name in Exception move is"+childName);
		    				 FileObject ftpSrcFileObject = manager.resolveFile(sftpUri+childName, opts);
		    				 String localFilePath = getFile(childName);
		    				 System.out.println("Local Path is"+localFilePath);
		    				 System.out.println("sftpUri+destFilePath"+sftpUri+destFilePath);
		    				 FileObject ftpDestFileObject = manager.resolveFile(sftpUri+destFilePath+"/"+fName, opts);
		    				 FileObject localSrcFileObject = manager.resolveFile(localFilePath);
		    				 System.out.println("Dest Object"+ftpDestFileObject);
		    				 System.out.println("localSrcFileObject"+localSrcFileObject);
		    				 if(ftpSrcFileObject!=null && ftpDestFileObject!=null)
		    				 {
		    					 ftpDestFileObject.copyFrom(localSrcFileObject, Selectors.SELECT_SELF);
		    				 }
		    				 ftpSrcFileObject.delete();
		    				 localSrcFileObject.delete();
	    				 }
	    			  }
	    		  }
	    	  }
	      }
	      ftpSrcDirObject.delete();// Deleting the Exception Folder
	    } catch (Exception ex) {
	    	throw new DDfException(ex.getMessage()+""+ex.getCause());
	    }finally {
	    	if(manager!=null){
	      manager.close();
	    	}	
	    	
	    	}
	}
	public void relocateAllFilesToAnotherFTP(FTPFileHandler ffh,String srcFilePath, String destFilePath)throws DDfException
	{
		System.out.println("srcFilePath"+srcFilePath);
		System.out.println("destFilePath"+destFilePath);
		 StandardFileSystemManager manager = new StandardFileSystemManager();
		    try {
		    	manager.init();
		      String sftpUri = "sftp://"+userName+":"+password+"@"+ftpUrl;         
		      FileSystemOptions opts = new FileSystemOptions();
		      SftpFileSystemConfigBuilder.getInstance().setStrictHostKeyChecking(opts, "no");	      
		      FileObject ftpSrcDirObject = manager.resolveFile(sftpUri+srcFilePath, opts);// Locating the Root directory
		      if(ftpSrcDirObject!=null)
		      {
		    	  FileObject[] children = ftpSrcDirObject.getChildren();
		    	  if(children!=null&& children.length>0)
		    	  {
		    		  for (int i = 0; i < children.length; i++) 
		    		  {
		    			  FileObject child = children[i];
		    			  if(child.getType().toString().equalsIgnoreCase("file"))
		    			  {
		    				 String childName = child.getName().toString();
		    				 String tempChildName= childName.toLowerCase();
		    				 if(tempChildName.contains(".pdf"))
		    				 {
		    					 childName = childName.substring(childName.lastIndexOf("/")+1);// Extracting the File name with extension only
		    					// String fName = childName;
		    					 childName = srcFilePath+childName;// Complete path of the File on the SFTP
		    					 System.out.println("childName"+childName);
		    					 String localFilePath = getFile(childName);
		    					 System.out.println("localFilePath"+localFilePath);
		    					// FileObject ftpSrcFileObject = manager.resolveFile(sftpUri+childName, opts);
		    					 FileObject localSrcFileObject = manager.resolveFile(localFilePath);
		    					 
		    					 localFilePath = new File(localFilePath).getAbsolutePath();
		    					 ffh.saveFile(localFilePath, destFilePath);// This line is moving the local content to the FTP location
		    					 
		    					 
		    					 localSrcFileObject.delete();// Deleting Locally downloaded file
		    					 //ftpSrcFileObject.delete();// Deleting FTP file This is blocked on 1/29/2011 Harsh
		    				 }
		    				 
		    			  }
					  } 
		    	  }
		      }
		      
		    } catch (Exception ex) {
		    	throw new DDfException(ex.getMessage()+""+ex.getCause());
		    }finally {
		    	if(manager!=null){
		      manager.close();
		    	}
		    	if(ffh!=null)
		    	{
		    		//ffh.disconnect();
		    	}
		    }
	}
	@Override
	public boolean deleteFile(String filePath) {
		boolean status=false;
		 File fileObj = new File(filePath);
		 if(fileObj!=null && fileObj.exists())
		 {
			 System.out.println("In side FTP delete");
			 status=fileObj.delete();
		 }
		return status;
	}
	
	public void disconnect() throws DDfException
	{
		if(fsManager != null) {
	           try{
	        	   fsManager.freeUnusedResources();
	               fsManager.close();
	           }catch(Exception e){
	        	   throw new DDfException(e.getMessage());
	           }
	        }
	        fsManager = null;
	}
	

}
