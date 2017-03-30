package com.daffodil.documentumie.filehandler;

import com.daffodil.documentumie.dctm.exception.DDfException;

public interface ContentFileHandler {
	
	public String getFile(String filePath) throws DDfException;
	boolean saveFile(String srcfilePath,String destFilePath) throws DDfException;
	boolean deleteFile(String filePath);

}
