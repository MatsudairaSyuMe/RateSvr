package com.systex.sysgateii.comm;

public class Constants {
	public static String LABEL_KEY_SYNC = "syncCheck";
	public static final String DEF_ENCODING = "UTF-8";

	public static final String CFG_SYS_FILE = "gw.properties";
	public static String CFG_FILE_LOCATION = "/resources/"; // 可以修改
	public static final String CFG_FILE = "memberserver.properties";
	public static final String CFG_MEMBER_ID = "mid";
	public static final String CFG_KEEP_ALIVE = "ts.keep.alive";
	public static final String CFG_IDLE_TIMEOUT = "ts.idle.timeout";
	public static final String CFG_TX_TIMEOUT = "ts.tx.timeout";
	public static final String CFG_CLIENT_ADDRESS = "client.ip.address";
	public static final String CFG_SERVER_ADDRESS = "system.ip";
	public static final String CFG_CHANNEL_NO_MIN = "channel.no.min";
	public static final String CFG_CHANNEL_NO_MAX = "channel.no.max";
	public static final String CFG_CHANNEL_BUFFER_SIZE = "channel.buffer.size";

	public static final int DEF_TIME_TO_LIVE = 30;
	public static final int DEF_CONNECT_TIMEOUT = 10;

	public static final String DEF_MEMBER_ID = "000";
	public static final int DEF_KEEP_ALIVE = 120;
	public static final int DEF_IDLE_TIMEOUT = 600;
	public static final int DEF_TX_TIMEOUT = 40;
//	public static final String DEF_CLIENT_ADDRESS = "192.168";
	public static final String DEF_CLIENT_ADDRESS = "127.0";
	public static final String DEF_SERVER_ADDRESS = "0.0.0.0";
	public static final int DEF_CHANNEL_NO_MIN = 11;
	public static final int DEF_CHANNEL_NO_MAX = 12;
	public static final int DEF_CHANNEL_BUFFER_SIZE = 3072;

	public static String SCRIPT_NAME = "TelecomCfg.java";
	public static String SCRIPT_FUNC = "getConfig()";
	public static String SCRIPT_PATH = System.getProperty("file.separator") + "script"
			+ System.getProperty("file.separator");

	public static String DEF_PCODE_R = "01001300";
	public static String DEF_PCODE_S = "01101300";

	public static final String RET_CD_TIMEOUT = "E1"; // 逾時未回應
	public static final String RET_CD_INVALID = "E2"; // 應用系統未開啟
	public static final String RET_CD_DATE_ERROR = "E3"; // 交易日期時間錯誤

//	public static final int MSG_HDR_SIZE = 102; //20150326 與文件同步扣除TextSize的4個byte
	public static final int MSG_HDR_SIZE = 98;
	public static final int MSG_ID_OFFSET = 4;
	public static final int MSG_ID_SIZE = 39;
}
