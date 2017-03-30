package com.daffodil.documentumie.fileutil.configurator.bean;


public class ExportConfigBean extends ConfigBean {

	private String objectType;
    
    private String exportType;

//   private String metadataPath;
//
//    private String outputPath;

 //   private String logFilePath;
    
   private String ReportType;
    
    private String allVersion;
    
    private String metadataOnly;
    
    private String inZip;
    
//    private String 

    public ExportConfigBean () {
    }

    public String getExportType () {
        return exportType;
    }

    public void setExportType (String val) {
        this.exportType = val;
    }

	public String getAllVersion() {
		return allVersion;
	}

	public void setAllVersion(String allVersion) {
		this.allVersion = allVersion;
		System.out.println("allVersion                 "+allVersion);
	}

	public String getInZip() {
		return inZip;
	}

	public void setInZip(String inZip) {
		this.inZip = inZip;
	}

	public String getMetadataOnly() {
		return metadataOnly;
	}

	public void setMetadataOnly(String metadataOnly) {
		this.metadataOnly = metadataOnly;
	}

	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	public String getReportType() {
		return ReportType;
	}

	public void setReportType(String reportType) {
		ReportType = reportType;
	}

    

}

