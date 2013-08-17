package cz.fit.lentaruand.data;


public enum IntentContent {
	ACTION_EXECUTE_COMMAND(".ACTION_EXECUTE_COMMAND"), 
	ACTION_EXECUTE_DOWNLOAD_BRIEF(".ACTION_EXECUTE_DOWNLOAD_BRIEF"), 
	ACTION_EXECUTE_DOWNLOAD_FULL(".ACTION_EXECUTE_DOWNLOAD_FULL"), 
	ACTION_CANCEL_COMMAND(".ACTION_CANCEL_COMMAND"), 
	EXTRA_REQUEST_ID(".EXTRA_REQUEST_ID"), 
	EXTRA_STATUS_RECEIVER(".EXTRA_STATUS_RECEIVER"), 
	EXTRA_COMMAND(".EXTRA_COMMAND"), 
	EXTRA_RUBRIC(".EXTRA_RUBRIC"), 
	EXTRA_LIST(".EXTRA_LIST");
	 
	private String intentContentName;
	private String packageName = "cz.fit.lentaruand.service"; 
		
	private IntentContent(String name) {
		this.intentContentName = packageName.concat(name); 
	}

	public String getIntentContent() {
		return intentContentName;
	}


	public String getPackageName() {
		return packageName;
	}

}
