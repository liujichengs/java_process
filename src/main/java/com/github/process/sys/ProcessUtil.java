package com.github.process.sys;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Scanner;

import org.apache.commons.lang.StringUtils;

public class ProcessUtil {

	/**
	 * 以多线程的形式启动第三方软件进程
	 * 
	 * @param cmd
	 * 主干第一次修改
	 */
	/**
	 * 分支第一次修改
	 * @param cmd
	 */
	public static void startProcessWidthThread(String cmd) {
		Process process = null;
		try {
			process = Runtime.getRuntime().exec(cmd);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (process != null) {
			EncodingStream os = new EncodingStream(process.getInputStream(), process.getOutputStream());
			os.start();
			EncodingStream es = new EncodingStream(process.getErrorStream(), process.getOutputStream());
			es.start();
		}
	}

	/**
	 * 在当前线程中调用第三方软件
	 * 
	 * @param cmd
	 * @return
	 */
	public static boolean startProcess(String cmd) {
		boolean success = false;
		Process process = null;
		InputStream error_stream = null;
		OutputStream output_stream = null;
		BufferedReader bre = null;
		try {
			System.out.println(cmd);
			process = Runtime.getRuntime().exec(cmd);
			if (process == null) {
				success = false;
			} else {
				final InputStream input_stream = process.getInputStream();
				new Thread(new Runnable() {
					public void run() {
						BufferedReader bri = new BufferedReader(new InputStreamReader(input_stream));
						try {
							String line = null;
							while ((line = bri.readLine()) != null) {
								System.out.println(line);
							}
						} catch (IOException e) {
							e.printStackTrace();
						} finally {
							try {
								if (input_stream != null) {
									input_stream.close();
								}
								if (bri != null) {
									bri.close();
								}
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}).start();
				error_stream = process.getErrorStream();
				bre = new BufferedReader(new InputStreamReader(error_stream));
				String line = null;
				output_stream = process.getOutputStream();
				while ((line = bre.readLine()) != null) {
					// buf.append(line);
				}
				success = true;
			}
		} catch (IOException e) {
			success = false;
			e.printStackTrace();
		} finally {
			try {
				if (output_stream != null) {
					output_stream.close();
				}
				if (error_stream != null) {
					error_stream.close();
				}
				if (bre != null) {
					bre.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return success;
	}

	public static boolean isWindows() {
		String serverName = System.getProperty("os.name");
		if (serverName.contains("Windows")) {
			return true;
		} else {
			return false;
		}
	}

	public static int getProcessCount(String processName) {
		if (isWindows()) {
			return getProcessCountWin(processName);
		} else {
			return getProcessCountLinux(processName);
		}
	}

	public static void killProcess(String processName, String sessionId) {
		if (isWindows()) {
			int pid = findProcessWin(processName);
			killProcessWin(pid);
		} else {
			killProcessLinux(processName, sessionId);
		}
	}

	/**
	 * 获取某个服务进程数-linux
	 * 
	 * @param processName
	 * @return
	 */
	private static int getProcessCountLinux(String processName) {
		String cmds[] = new String[] { "/bin/sh", "-c", "ps -A | grep " + processName + "" };
		int i = 0;
		try {
			Process process = Runtime.getRuntime().exec(cmds);
			BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = " ";
			while ((line = input.readLine()) != null) {
				if (line.contains(processName)) {
					i++;
				}
			}
			input.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return i;
	}

	/**
	 * 获取某个服务进程数 -win
	 * 
	 * @param processName
	 * @return
	 */
	private static int getProcessCountWin(String processName) {
		int count = 0;
		try {
			Process process = Runtime.getRuntime().exec("tasklist");
			Scanner in = new Scanner(process.getInputStream());
			while (in.hasNextLine()) {
				String p = in.nextLine();
				if (p.contains(processName)) {
					count++;
				}
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return count;
	}

	/**
	 * 关闭ffmpeg 进程,此处要注意一定要安全关闭，以免造成文件损坏
	 * 
	 * @param player_session_id
	 */
	private static void killProcessLinux(String processName, String sessionId) {
		String cmd = "ps -ef|grep " + processName + " | grep " + sessionId + " | cut -c 9-15 |xargs kill  -9";
		startProcessWidthThread(cmd);
	}

	/**
	 * 根据进程名寻找进程ID
	 * 
	 * @param proName
	 * @return 不存在，返回-1
	 */
	public static int findProcessWin(String processName) {
		try {
			Process process = Runtime.getRuntime().exec("tasklist");
			Scanner in = new Scanner(process.getInputStream());
			while (in.hasNextLine()) {
				String p = in.nextLine();
				if (p.contains(processName)) {
					p = p.replaceAll("\\s+", ",");
					String[] arr = p.split(",");
					if (StringUtils.isEmpty(arr[1])) {
						return -1;
					} else {
						return Integer.parseInt(arr[1]);
					}
				}
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * 关闭某进程(根据PID)
	 * 
	 * @param pid
	 * @return
	 */
	private static boolean killProcessWin(int pid) {
		try {
			Runtime.getRuntime().exec("cmd.exe /c taskkill /f /pid " + pid);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}