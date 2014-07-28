package io.relayr.core.error;

public class Relayr_Exception extends Exception  {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public Relayr_Exception (String message) {
		super(message);
	}

	public Relayr_Exception (String message, Throwable throwable) {
		super(message, throwable);
	}
}
