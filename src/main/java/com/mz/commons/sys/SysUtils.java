 

package com.mz.commons.sys;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * TODO:  这里用一句话描述当前类的作用
 * Date:     2016年3月15日 下午4:30:05 <br/>
 * @author   liujicheng
 * @version  V1.0
 * @since    JDK 1.6 
 */
public class SysUtils {
	public static String getLocalIp() {
		String localip = null;// 本地IP，如果没有配置外网IP则返回它
		String netip = null;// 外网IP
		try {
			Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
			InetAddress ip = null;
			boolean finded = false;// 是否找到外网IP
			while (netInterfaces.hasMoreElements() && !finded) {
				NetworkInterface ni = netInterfaces.nextElement();
				Enumeration<InetAddress> address = ni.getInetAddresses();
				while (address.hasMoreElements()) {
					ip = address.nextElement();
					// System.out.println(ni.getName() + ";" +
					// ip.getHostAddress()
					// + ";ip.isSiteLocalAddress()="
					// + ip.isSiteLocalAddress()
					// + ";ip.isLoopbackAddress()="
					// + ip.isLoopbackAddress());
					if (!ip.isSiteLocalAddress() && !ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1) {// 外网IP
						netip = ip.getHostAddress();
						finded = true;
						break;
					} else if (ip.isSiteLocalAddress() && !ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1) {// 内网IP
						localip = ip.getHostAddress();
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return localip;
	}
}
