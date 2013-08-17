package cz.fit.lentaruand.service;

public enum Progress {
	RESPONSE_SUCCESS(0), 
	RESPONSE_FAILURE(-1), 
	RESPONSE_PROGRESS(1);
	
    private final int value;

    private Progress(final int newValue) {
	            value = newValue;
    }

	        
    public int getValue() { return value; }
	
}
