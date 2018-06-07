package com.github.process.exec;

 
public interface XProcess {

	/**
	 * Call to push the specified {@link String} into the started processes
	 * input.
	 * 
	 * @param line
	 */
	public void sendLine(String line);

	/**
	 * Try to destroy the process
	 */
	public void destory();

}
