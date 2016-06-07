 

package com.mz.commons.sys;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

/**
 * TODO: 调用系统scp命令执行系统操作 <br/>
 * Date: 2015年9月7日 上午11:15:14 <br/>
 * 
 * @author liujicheng
 * @version V1.0
 * @since JDK 1.6
 */
public class Scp {
	/**
	 * 从远程服务器拷贝文件到本地
	 * 
	 * @param ip
	 * @param username
	 * @param password
	 * @param localPath
	 * @param remoteFile
	 * @return
	 */
	public static boolean get(String ip, String username, String password, String localPath, String remoteFile) {
		boolean code = true;
		try {
			Connection conn = new Connection(ip);
			conn.connect();
			boolean isAuthenticated = conn.authenticateWithPassword(username, password);
			if (isAuthenticated == false) {
				code = false;
			}
			SCPClient client = new SCPClient(conn);
			client.get(remoteFile, localPath);
			conn.close();
		} catch (Exception e) {
			code = false;
			e.printStackTrace();
		}
		return code;
	}

	public static boolean exec(String ip, String username, String password, String command) {
		boolean code = true;
		Session ssh = null;
		Connection conn = null;
		try {
			conn = new Connection(ip);
			conn.connect();
			boolean isAuthenticated = conn.authenticateWithPassword(username, password);
			if (isAuthenticated == false) {
				code = false;
			}
			ssh = conn.openSession();
			ssh.execCommand(command);
			InputStream is = new StreamGobbler(ssh.getStdout());
			BufferedReader brs = new BufferedReader(new InputStreamReader(is));
			while (true) {
				String line = brs.readLine();
				if (line == null) {
					break;
				}
				System.out.println(line);
			}
		} catch (Exception e) {
			code = false;
			e.printStackTrace();
		} finally {
			// 连接的Session和Connection对象都需要关闭
			if (ssh != null) {
				ssh.close();
			}
			if (conn != null) {
				conn.close();
			}
		}

		return code;
	}

	/**
	 * 把本地文件拷贝到远程服务器
	 * 
	 * @param ip
	 * @param username
	 * @param password
	 * @param localFile
	 * @param remotePath
	 * @return
	 */
	public static boolean put(String ip, String username, String password, String localFile, String remotePath) {
		boolean code = true;
		try {
			Connection conn = new Connection(ip);
			conn.connect();
			boolean isAuthenticated = conn.authenticateWithPassword(username, password);
			if (isAuthenticated == false) {
				code = false;
				throw new IOException("登录失败");
			}
			SCPClient client = new SCPClient(conn);
			client.put(localFile, remotePath);
			conn.close();
		} catch (Exception e) {
			code = false;
			e.printStackTrace();
		}
		return code;
	}
}
