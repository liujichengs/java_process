package com.mz.commons.exec;

public class Main {

    /**
     * 第01次修改
     */
    /**
     * 第02次修改
     */
    /**
     * 第03次修改
     */
	static void synchronously() {
		Spawn.synExec(
				new String[] { "/bin/bash", "-c", "vmstat -s -S M | egrep -ie 'memory|swap' | grep 'free memory'" },
				null);
	}

	static void asynchronously() {
		final String cmd = "ping   www.cnlive.com             -t";
		Spawn.asynExec(cmd.split(" "), new ProcessListener() {
			@Override
			public void onOutputLine(final String line) {
				System.out.println("onOutputLine :" + line);
			}

			@Override
			public void onErrorLine(final String line) {
				System.out.println("onErrorLine:" + line);

			}

			@Override
			public void onError(final Throwable t) {
				throw new RuntimeException(t);
			}

			@Override
			public void onProcessQuit(int returnValue) {
				System.out.println("onProcessQuit");
			}
		});
	}

	public static void main(String[] args) {
		Main.asynchronously();
		System.out.println("ddddddd");
	}
}
