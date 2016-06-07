package com.mz.commons.exec;
 
public interface ProcessListener {

	/**
	 * When the process wrote a line to its standard output stream.
	 * 
	 * @param line
	 */
	public void onOutputLine(String line);

	/**
	 * When the process wrote a line to its error output stream.
	 * 
	 * @param line
	 */
	public void onErrorLine(String line);

	/**
	 * When the output stream is closed.
	 */
	public void onProcessQuit(int returnValue);

	/**
	 * When an unexpected error is thrown while interacting with the process.
	 * 
	 * @param t
	 */
	public void onError(Throwable t);

}
