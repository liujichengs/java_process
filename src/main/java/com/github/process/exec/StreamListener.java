package com.github.process.exec;

public interface StreamListener {

	public void onOutputLine(String line);

	public void onClosed();

	public void onError(Throwable t);

}
