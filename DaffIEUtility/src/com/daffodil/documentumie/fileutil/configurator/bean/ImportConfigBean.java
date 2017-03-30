package com.daffodil.documentumie.fileutil.configurator.bean;


public class ImportConfigBean extends ConfigBean {

    private String objectType;
	
    private String inputType;
    
    private String updateExisting;
    
    private String liveRun;

    public ImportConfigBean () {
    }

	public String getInputType() {
		return inputType;
	}

	public void setInputType(String inputType) {
		this.inputType = inputType;
	}

	public String getLiveRun() {
		return liveRun;
	}

	public void setLiveRun(String liveRun) {
		this.liveRun = liveRun;
	}

	public String getUpdateExisting() {
		return updateExisting;
	}

	public void setUpdateExisting(String updateExisting) {
		this.updateExisting = updateExisting;
	}

	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}
}

