package com.systex.sysgateii.util;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;



public class LogUtil {
	/**
	 * Log Forging漏洞校驗
	 * 
	 * @param logs
	 * @return
	 */
	public static String vaildLog(String logs) {
		List<String> list = new ArrayList<String>();
		list.add("%0d");
		list.add("%0a");
		list.add("\r");
		list.add("\n");
		if(StringUtils.isNotBlank(logs)) {
			String normalize = Normalizer.normalize(logs, Normalizer.Form.NFKC);
			for (String str : list) {
				normalize = normalize.replace(str, "");
			}
			return normalize;
		}
		return null;
	}
}
