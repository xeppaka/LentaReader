package com.xeppaka.lentareader.parser.exceptions;

public class ParseException extends Exception {
	private static final long serialVersionUID = 2502997894047026316L;

	public ParseException() {
		this("Error occured during parse operation.");
	}
	
	public ParseException(String message) {
		super(message);
	}
}
