package com.github.process.exec;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

public class Engine {
 
    /**
     * master 第一次修改
     * 
     * @param command
     * @param listener
     * @param folder
     * @return
     */
	 
	public static XProcess exec(final String[] command, final ProcessListener listener, final File folder) {
		final ProcessBuilder pb;
		final Process process;
		//branch 第二次修改
		try {
			pb = new ProcessBuilder(command);
			if (folder != null) {
				pb.directory(folder);
			}
			// pb.redirectOutput(Redirect.PIPE);
			// pb.redirectInput(Redirect.PIPE);
			process = pb.start();
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}

        final AtomicLong lastOutput = new AtomicLong();
        lastOutput.set(0);
        final OutputStream outputStream = process.getOutputStream();
        final InputStream inputStream = process.getInputStream();
        final InputStream errorStream = process.getErrorStream();
        // BufferedReader reader = new BufferedReader(new
        // InputStreamReader(inputStream));

        // String line;
        // try {
        // line = reader.readLine();
        // while (line != null && !line.trim().equals("--EOF--")) {
        // listener.onOutputLine(line);
        // line = reader.readLine();
        // }
        // } catch (IOException e1) {
        // // TODO Auto-generated catch block
        // e1.printStackTrace();
        // }

        final StreamReader streamReader = new StreamReader(inputStream, new StreamListener() {
            @Override
            public void onOutputLine(final String line) {
                // System.out.println("Receiving line");
                lastOutput.set(new Date().getTime());
                listener.onOutputLine(line);
            }

            @Override
            public void onError(final Throwable t) {
                listener.onError(new Exception("Error while reading standard output", t));
            }

            @Override
            public void onClosed() {

            }
        });

        final StreamReader errorStreamReader = new StreamReader(errorStream, new StreamListener() {
            @Override
            public void onOutputLine(final String line) {
                lastOutput.set(new Date().getTime());
                listener.onErrorLine(line);
            }

            @Override
            public void onError(final Throwable t) {
                listener.onError(new Exception("Error while reading error output", t));
            }

            @Override
            public void onClosed() {

            }
        });

        new Thread() {
            @Override
            public void run() {
                errorStreamReader.read();
            }
        }.start();

        new Thread() {
            @Override
            public void run() {
                try {
                    streamReader.read();
                    final int returnValue = process.waitFor();
                    // while (lastOutput.get() == 0 && new Date().getTime() -
                    // startTime < 500) {
                    // Thread.sleep(10);
                    // }

                    // while ( new Date().getTime() - lastOutput.get() < 1000) {
                    // System.out.println("DELAY ... "+(new Date().getTime() -
                    // lastOutput.get()));
                    // Thread.sleep(500);
                    // }
                    // System.out.println("processes finished with "+returnValue);

                    listener.onProcessQuit(returnValue);
                } catch (final InterruptedException e) {
                    listener.onError(e);
                    return;
                }
            }
        }.start();
        return new XProcess() {
            @Override
            public synchronized void sendLine(final String line) {
                final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
                try {
                    writer.append(line);
                } catch (final IOException e) {
                    listener.onError(e);
                }
            }

            @Override
            public void destory() {
                try {
                    errorStreamReader.stop();
                    outputStream.close();
                    inputStream.close();
                    errorStream.close();
                } catch (final IOException e) {
                    listener.onError(e);
                }
                process.destroy();
                try {
                    process.waitFor();
                } catch (final InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    // ###############

    public static XProcess exec(final String command, final ProcessListener listener) {
        final Process process;
        try {
            process = Runtime.getRuntime().exec(command);
            System.out.println(process.getInputStream());
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("ddddd");
        final AtomicLong lastOutput = new AtomicLong();
        lastOutput.set(0);
        final OutputStream outputStream = process.getOutputStream();
        final InputStream inputStream = process.getInputStream();
        final InputStream errorStream = process.getErrorStream();
        final StreamReader streamReader = new StreamReader(inputStream, new StreamListener() {
            @Override
            public void onOutputLine(final String line) {
                lastOutput.set(new Date().getTime());
                listener.onOutputLine(line);
            }

            @Override
            public void onError(final Throwable t) {
                listener.onError(new Exception("Error while reading standard output", t));
            }

            @Override
            public void onClosed() {

            }
        });

        final StreamReader errorStreamReader = new StreamReader(errorStream, new StreamListener() {
            @Override
            public void onOutputLine(final String line) {
                lastOutput.set(new Date().getTime());
                listener.onErrorLine(line);
            }

            @Override
            public void onError(final Throwable t) {
                listener.onError(new Exception("Error while reading error output", t));
            }

            @Override
            public void onClosed() {

            }
        });

        new Thread() {
            @Override
            public void run() {
                errorStreamReader.read();
            }
        }.start();

        new Thread() {
            @Override
            public void run() {
                try {
                    streamReader.read();
                    final int returnValue = process.waitFor();
                    listener.onProcessQuit(returnValue);
                } catch (final InterruptedException e) {
                    listener.onError(e);
                    return;
                }
            }
        }.start();
        return new XProcess() {
            @Override
            public synchronized void sendLine(final String line) {
                final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
                try {
                    writer.append(line);
                } catch (final IOException e) {
                    listener.onError(e);
                }
            }

            @Override
            public void destory() {
                try {
                    errorStreamReader.stop();
                    outputStream.close();
                    inputStream.close();
                    errorStream.close();
                } catch (final IOException e) {
                    listener.onError(e);
                }
                process.destroy();
                try {
                    process.waitFor();
                } catch (final InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }
}
