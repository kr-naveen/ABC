package com.daffodil.documentumie.fileutil.properties;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.ResourceBundle;

public class PropertyFileLoader {
private static Properties utility_config_properties;

public static Properties loadUtilityConfigPropertyFile(){
	
	try {
		if(utility_config_properties!=null){
			return utility_config_properties;
		}
		utility_config_properties = new Properties();
		String currentdir = System.getProperty("user.dir");
		//ResourceBundle myResources =ResourceBundle.getBundle("UtilityConfiguration.properties");
		        
		InputStream in = PropertyFileLoader.class.getResourceAsStream("/UtilityConfiguration.properties");
		// The below commented code is the Working code.
		String propertyFile = currentdir.substring(0,currentdir.lastIndexOf("\\"))+"/DaffIEUtility/UtilityConfiguration.properties";
		//String propertyFile = currentdir.substring(0,currentdir.lastIndexOf("\\"))+"/nlsbundle/UtilityConfiguration.properties";
		System.out.println(currentdir+" --dir-- "+propertyFile);
//		String propertyFile = "D:\\DATA\\jyoti\\SSSL Utility Workspace\\DaffIEUtility\\UtilityConfiguration.properties";
		
		//System.out.println(propertyFile);
		utility_config_properties.load(in);
		System.out.println("after load properties file ");
	} catch (IOException io) {
		io.printStackTrace();
	} catch (Exception ex) {
		ex.printStackTrace();
	}
	return utility_config_properties;
	
}

public static void main(String[] args) {
	System.out.println(System.getProperty("user.dir"));
	 
	loadUtilityConfigPropertyFile();
	//new PropertyFileLoader().testMeth();
}
public   void testMeth()
{
	InputStream in = this.getClass().getResourceAsStream("/UtilityConfiguration.properties");
    try {
    	utility_config_properties.load(in);
    } catch (IOException e) {
        e.printStackTrace();
    }
}
}
