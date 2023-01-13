package com.systex.sysgateii.util;

import java.util.List;
import org.owasp.esapi.ESAPI;



public class LogUtil {
	/**
	 * Log Forging漏洞校驗
	 * 
	 * @param message`
	 * @return
	 */
	public static String vaildLog(String message) {
		message = message.replace('\n', '_').replace('\r', '_').replace('\t', '_');
		try {
			message = ESAPI.encoder().encodeForHTML(message);
		} catch (Exception e) {
			;//e.printStackTrace();
		}
		return message;
	}
}
