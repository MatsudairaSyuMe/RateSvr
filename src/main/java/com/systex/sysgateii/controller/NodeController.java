package com.systex.sysgateii.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.systex.sysgateii.util.DataConvert;
import com.systex.sysgateii.util.StrUtil;
import com.systex.sysgateii.controller.NodeController;
import com.systex.sysgateii.client.ClientConnection;
//import com.fstop.gateway.server.ServerConsumer;
//import com.fstop.gateway.server.ServerProducer;
import com.systex.sysgateii.comm.Constants;
import com.systex.sysgateii.listener.ActorStatusListener;
import com.systex.sysgateii.listener.MessageListener;
import com.systex.sysgateii.server.ServerProducer;

public class NodeController implements MessageListener<byte[]>, Runnable {
	private static Logger log = LoggerFactory.getLogger(NodeController.class);
	public final static String KEY_NAME = "NDC";
	private Properties prop;
	String nodeId;
	int bufferSize;
	int tsKeepAlive;
	int tsIdleTimeout;
	int tsTxTimeout;
	int channelNoMin;
	int channelNoMax;
	String clientIpRange;
	String bindAddr;
	int groupCount;
	static NodeController controller;
	ConcurrentHashMap<String, String> map;
	String id;

	public NodeController(String id, ConcurrentHashMap<String, String> map) {
		nodeId = Constants.DEF_MEMBER_ID;
		channelNoMin = Constants.DEF_CHANNEL_NO_MIN;
		channelNoMax = Constants.DEF_CHANNEL_NO_MAX;
		clientIpRange = Constants.DEF_CLIENT_ADDRESS;

		bufferSize = Constants.DEF_CHANNEL_BUFFER_SIZE;
		tsKeepAlive = Constants.DEF_KEEP_ALIVE;
		tsIdleTimeout = Constants.DEF_IDLE_TIMEOUT;
		tsTxTimeout = Constants.DEF_TX_TIMEOUT;
		bindAddr = Constants.DEF_SERVER_ADDRESS;
		this.id = id;
		this.map = map;
	}

	public NodeController(String nodeId, int channelNoMin, int channelNoMax, String clientIpRange) {
		this.nodeId = nodeId;
		this.channelNoMin = channelNoMin;
		this.channelNoMax = channelNoMax;
		this.clientIpRange = clientIpRange;

		bufferSize = Constants.DEF_CHANNEL_BUFFER_SIZE;
		tsKeepAlive = Constants.DEF_KEEP_ALIVE;
		tsIdleTimeout = Constants.DEF_IDLE_TIMEOUT;
		tsTxTimeout = Constants.DEF_TX_TIMEOUT;
		bindAddr = Constants.DEF_SERVER_ADDRESS;

	}

	public NodeController(Properties prop) {
		this.prop = prop;

		// check property and overwrite
		nodeId = getCfgStr(Constants.CFG_MEMBER_ID, Constants.DEF_MEMBER_ID);

		bufferSize = getCfgInt(Constants.CFG_CHANNEL_BUFFER_SIZE, Constants.DEF_CHANNEL_BUFFER_SIZE);
		tsKeepAlive = getCfgInt(Constants.CFG_KEEP_ALIVE, Constants.DEF_KEEP_ALIVE);
		tsIdleTimeout = getCfgInt(Constants.CFG_IDLE_TIMEOUT, Constants.DEF_IDLE_TIMEOUT);
		tsTxTimeout = getCfgInt(Constants.CFG_TX_TIMEOUT, Constants.DEF_TX_TIMEOUT);
		channelNoMin = getCfgInt(Constants.CFG_CHANNEL_NO_MIN, Constants.DEF_CHANNEL_NO_MIN);
		channelNoMax = getCfgInt(Constants.CFG_CHANNEL_NO_MAX, Constants.DEF_CHANNEL_NO_MAX);

		clientIpRange = getCfgStr(Constants.CFG_CLIENT_ADDRESS, Constants.DEF_CLIENT_ADDRESS);
		bindAddr = getCfgStr(Constants.CFG_SERVER_ADDRESS, Constants.DEF_SERVER_ADDRESS);

	}

	int getCfgInt(String key, int def) {
		int value = def;
		String var = prop.getProperty(key);
		if (StrUtil.isNotEmpty(var) && StrUtil.isNumeric(var)) {
			value = Integer.valueOf(var);
		}
		return value;
	}

	String getCfgStr(String key, String def) {
		String value = def;
		String var = prop.getProperty(key);
		log.info("key={} var={} def={}", key, var, def);
		if (StrUtil.isNotEmpty(var)) {
			value = var;
		}
		return value;
	}

	private String addLeftZeroForNum(int num, int strLength) {
		return addLeftZeroForNum(Integer.toString(num), strLength);
	}

	private String addLeftZeroForNum(String str, int strLength) {
		int strLen = str.length();
		if (strLen < strLength) {
			while (strLen < strLength) {
				StringBuffer sb = new StringBuffer();
				// sb.append(appn).append("0");// 左補0
				sb.append(str).append("0");// 右補0
				str = sb.toString();
				strLen = str.length();
			}
		}

		return str;
	}

	@Override
	public void messageReceived(String serverId, byte[] msg) {
		// TODO Auto-generated method stub
		log.debug(serverId + " msg=" + DataConvert.bytesToHex(msg));
	}

	public void run() {
		log.info(id + " run");
		try {
			// channel no 必需要成對設定
			if (channelNoMin >= channelNoMax || (channelNoMax - channelNoMin + 1) % 2 > 0) {
				log.error("Invalid channel number:" + channelNoMin + " " + channelNoMax);
				return;
			}
			groupCount = (channelNoMax - channelNoMin + 1) / 2;
			log.debug("groupCount=" + groupCount);

			// 成對啟動
			// 11 即為單位送出訊息的管道(server收)；另12即為收sysgateii訊息之管道(server送)
			String serverId;
			int port;
			Thread thread;
			for (int i = channelNoMin; i <= channelNoMax; i = i + 2) {
				log.info("start channel " + i + " " + (i + 1));
//				serverId = String.format("%02d%s", i, nodeId);
//				port = Integer.parseInt(serverId);
//				log.debug("serverId=" + serverId);
//				ServerConsumer consumer = new ServerConsumer(serverId, bindAddr, port, bufferSize, tsKeepAlive,
//						tsIdleTimeout);

				serverId = String.format("%02d%s", i + 1, nodeId);
//				port = Integer.parseInt(serverId);
				log.info("default serverId=" + serverId);
				bindAddr = map.get("system.ip");
				port = Integer.parseInt(map.get("system.port"));
				log.info("port=" + port);
				String tmps = map.get("boards.board.ip");
				if (tmps.startsWith("["))
					tmps = tmps.substring(1);
				if (tmps.endsWith("]"))
					tmps = tmps.substring(0, tmps.length() - 2);
				String[] localary = tmps.split(",");
				ServerProducer producer = new ServerProducer(map.get("system.port"), bindAddr, port, bufferSize,
						tsKeepAlive, tsIdleTimeout);

				List<String> wsnos = new ArrayList<String>();
				wsnos.add(addLeftZeroForNum(98, 4));

				for (String cs : localary) {
					producer.getIpList().add(cs.trim());
					String[] p = cs.trim().split("\\.");
					wsnos.add(addLeftZeroForNum(Integer.valueOf(p[3]), 4));
				}
				List<String> brnos = new ArrayList<String>();
				tmps = map.get("boards.board.brno");
				if (tmps.startsWith("["))
					tmps = tmps.substring(1);
				if (tmps.endsWith("]"))
					tmps = tmps.substring(0, tmps.length() - 2);
				localary = tmps.split(",");
				for (String cs : localary)
					brnos.add(cs.trim());

				ClientConnection conn = new ClientConnection(map, brnos, wsnos, new Timer());

				conn.addActorStatusListener(producer);

				thread = new Thread(producer);
				thread.start();
				thread = new Thread(conn);
				thread.start();
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
	}

	public void stop(int waitTime) {
		log.info(id + " stop");
		try {
//			thread.start();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}// stop

	public static void sleep(int t) {
		try {
			Thread.sleep(t * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void createController(ConcurrentHashMap<String, String> map) {
		log.info("Enter createController");
//		NodeController.map = map;
		if (map != null) {
			if (controller == null) {
				controller = new NodeController(KEY_NAME, map);
			}
		}
	}

	public static void startController() {
		log.info("Enter startController");
		if (controller != null) {
			controller.run();
		}
	}

	public static void stopController() {
		log.info("Enter stopController");
		if (controller != null) {
			controller.stop(0);
		}
	}

	public static void main(String[] args) {
//		NodeController c = new NodeController();
//		c.run();
	}

}
