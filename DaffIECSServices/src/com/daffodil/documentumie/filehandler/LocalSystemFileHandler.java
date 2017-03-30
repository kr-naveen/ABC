package com.daffodil.documentumie.filehandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.daffodil.documentumie.dctm.exception.DDfException;

public class LocalSystemFileHandler implements ContentFileHandler {

	@Override
	public String getFile(String filePath) {
		File f = new File(filePath);
		if(f!=null && f.exists())
		{
			return f.getAbsolutePath();
		}
		return null;
	}

	@Override
	public boolean saveFile(String filePath,String destFilePath) throws DDfException {
		boolean status = false;
		FileInputStream fis = null;
		FileOutputStream fos =null;
		String fileName = filePath.substring(filePath.lastIndexOf("\\")+1);
		try {
            File inputFile = new File(filePath);
            File tempDestPath = new File(destFilePath);
            if(!tempDestPath.exists())
            {           	
            	tempDestPath.mkdirs();
            }
            File outputFile = new File(tempDestPath+"\\"+fileName);

            fis = new FileInputStream(inputFile);
            fos = new FileOutputStream(outputFile);
            int c=0;
            byte[] bytesIn = new byte[1024];
            while ((c = fis.read(bytesIn)) != -1) {
               fos.write(bytesIn);
            }          
            status = true;
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
                if(fos !=null){
                	fos.flush();
                	fos.close();
                }               	
            } catch (IOException e) {
            	throw new DDfException(e.getMessage());
            }
		}
		return status;
	}

	@Override
	public boolean deleteFile(String filePath) {
		boolean status=false;
		if(filePath!=null)
		{
		 File fileObj = new File(filePath);
		 if(fileObj!=null && fileObj.exists())
		 {
			 status=fileObj.delete();
		 }
		}
		return status;
	}
	public void relocateFilesToProcessdOrException(String srcFilePath, String destFilePath)throws DDfException
	{
		saveFile(srcFilePath, destFilePath);// Moving Local file to Processed or Exception Folder
		deleteFile(srcFilePath);
	}
	public void relocateAllFilesToAnotherFTP(FTPFileHandler ffh,String srcFilePath, String destFilePath)throws DDfException
	{
		if(srcFilePath!=null)
		{
			File dirSource = new File(srcFilePath);
			System.out.println(dirSource.getAbsolutePath());
			if(dirSource.isDirectory())
			{
				File[] fileNames = dirSource.listFiles();
				System.out.println(fileNames.length);
				if(fileNames!=null && fileNames.length>0)
				{
					for (int i = 0; i < fileNames.length; i++) {
						File file = fileNames[i];
						if(file.isFile())
						{
						srcFilePath = file.getAbsolutePath();						
						ffh.saveFile(srcFilePath, destFilePath);
						}
					}
				}
			}
		}
	}
	public void moveDocumentFromExceptionToRoot(String srcFilePath, String destFilePath)throws DDfException
	{
		if(srcFilePath!=null)
		{
			File dirSource = new File(srcFilePath);
			if(dirSource.isDirectory())
			{
				File[] fileNames = dirSource.listFiles();
				if(fileNames!=null && fileNames.length>0)
				{
					for (int i = 0; i < fileNames.length; i++) {
						File file = fileNames[i];
						boolean saved = saveFile(file.getAbsolutePath(), destFilePath);
						if(saved)
						{
							file.delete();
						}
					}
				}
				dirSource.delete();// Deleting the locally created Exception Folder.
			}
		}
	}

}
