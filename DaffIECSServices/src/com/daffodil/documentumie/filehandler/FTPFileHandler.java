package com.daffodil.documentumie.filehandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import com.daffodil.documentumie.dctm.exception.DDfException;

public class FTPFileHandler implements ContentFileHandler {

	private String ftpUrl;
	private String userName;
	private String password;
	private  FTPClient ftp;
	
	private static int counter=1;
	
	public FTPFileHandler(String ftpUrl,String userName,String password)
	{
		this.ftpUrl = ftpUrl;
		this.userName = userName;
		this.password = password;
	}
	public boolean connect() throws DDfException
	{
		boolean status=false;
		ftp = new FTPClient();
		/*ftpUrl = "210.7.76.130";
		userName="rajkumar";
		password ="rajkumar";*/
		try {
			System.out.println("==========================FTP Connect() Called=============");
			ftp.enterLocalPassiveMode();
			ftp.connect(ftpUrl);
			if (!ftp.login(userName, password)) {
				ftp.logout();	
			}
			else{
				status=true;				
			}			
		}
		catch (SocketException e) {
			throw new DDfException(e.getMessage());
		}
		catch (IOException e) {
			throw new DDfException(e.getMessage());
		}
		return status;
	}
	
	@Override
	public String getFile(String metadataPath) throws DDfException {
		String path=null;
		FileOutputStream fos = null;
	
		try {
			 if(metadataPath!=null && metadataPath.length()>0)
			{
				File dir = new File("temps");
				if (!dir.exists()) {
					dir.mkdirs();
				}
				if(metadataPath.contains("\\"))
				{
				metadataPath = metadataPath.replaceAll("\\", "/");
				}
				System.out.println("Metadat FTP path is"+metadataPath);
				String fileName = metadataPath.substring(metadataPath.lastIndexOf("/")+1,metadataPath.length());
				fos = new FileOutputStream("temps/"+fileName);
				System.out.println("Complete Path="+"/" + fileName);
				ftp.setFileType(FTP.BINARY_FILE_TYPE);
				if(System.getProperty("os.name").contains("Window")){
				ftp.enterLocalPassiveMode();
				}
				boolean hasDownloaded = ftp.retrieveFile(metadataPath, fos);//Retrieves a named file(filename) from the FTP and writes it to the given OutputStream.
				System.out.println(hasDownloaded);
				if(hasDownloaded)
				{
					path = new File ("").getAbsolutePath()+"/temps/" + fileName;
					System.out.println("File Path in downloadMetadataFile() is"+path);
					//ftp.disconnect();
					fos.flush();
					fos.close();
				}
				else{
					try{
						fos.flush();
						fos.close();
					}catch(Exception e)
					{
						System.out.println(e.getMessage());
					}
				}
			}
		} catch (FileNotFoundException e) {			
			throw new DDfException(e.getMessage());
		} catch (IOException e) {
			throw new DDfException(e.getMessage());
		}
		finally{
			try{
				fos.flush();
				fos.close();
			}catch(Exception e)
			{
				System.out.println(e);
			}
		}
		counter++;
		return path;
	}

	@Override
	public boolean saveFile(String srcFilePath,String destFilePath) throws DDfException {
		boolean status=false;
		FileInputStream fis = null;
		try {
			
			System.out.println("Source File Path FTP is:"+srcFilePath);
			System.out.println("Destibnation path FTP is:"+destFilePath);
			String fileName = srcFilePath.substring(srcFilePath.lastIndexOf("\\")+1);
			System.out.println("Local file Path:"+srcFilePath);
			System.out.println("File Name is:"+fileName);
			//String destinationPath = "/testUtility/Harsh/test";
			if(!destFilePath.startsWith("/"))
			 {
				 destFilePath = "/"+destFilePath;
			 }
			String[] dirsList = destFilePath.split("/");
			System.out.println("Length is"+dirsList.length);
			String rooTPath="/";
			if(dirsList!=null && dirsList.length>0)
			{
				for(int i=1;i<dirsList.length;i++)
				{
					String temp = dirsList[i];
					
					rooTPath = rooTPath+temp;
					System.out.println(rooTPath);
					System.out.println("FTP is"+ftp);
					ftp.makeDirectory(rooTPath) ;
					rooTPath = rooTPath+"/";
				}
			}
			ftp.setFileType(FTP.BINARY_FILE_TYPE);
			 fis = new FileInputStream(srcFilePath);
			 if(!destFilePath.endsWith("/"))
			 {
				 destFilePath = destFilePath+"/";
			 }
			 
			 status = ftp.storeFile(destFilePath+""+fileName, fis);
			 System.out.println("Status of storing file on FTP is"+status);
			 System.out.println("Path at FTP is"+destFilePath+""+fileName);
		} catch (FileNotFoundException e) {
			throw new DDfException(e.getMessage());
		} catch (IOException e) {
			throw new DDfException(e.getMessage());
		}
		finally {
            try {
                if (fis != null) {
                    fis.close();
                }            
            } catch (IOException e) {
            	throw new DDfException(e.getMessage());
            }
		}
		return status;
	}
	public void relocateAllFiles(String srcFilePath, String destFilePath)throws DDfException
	{
		//ftp.ren
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
		try {
			ftp.logout();
			ftp.disconnect();
		} catch (IOException e) {
			throw new DDfException(e.getMessage());
		}
	}

}
