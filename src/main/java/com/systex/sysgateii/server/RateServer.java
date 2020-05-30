package com.systex.sysgateii.server;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.systex.sysgateii.controller.NodeController;
import com.systex.sysgateii.ratesvr.conf.DynamicProps;

import ch.qos.logback.classic.util.ContextInitializer;

/**
 * Gateway server for members.<br/>
 * Priority of Settings:<br/>
 * 
 * <pre>
 *   program command line > JVM command line > default properties
 *   
 *  
 * 以 TCP/IP Socket方式與各分行匯利率看板連接同時與FAS中心連線線，並採長連線方式
 * 每個分行依需求為每片顯示版建立一個連線通道，每個通道以非同步方式獨立收、送處理交易電文
 * 當系統偵測到其中一個通道斷線時，會自動中斷通道，然後重新建立該連線
 * </pre>
 * 
 * @author MathuDaira Syume
 *
 */
public class RateServer {
	private static Logger log = null;
	private static boolean isRunning = true;

	public static void main(String[] args) {
		// must be set before the first call to  LoggerFactory.getLogger()
		// ContextInitializer.CONFIG_FILE_PROPERTY is set to "logback.configurationFile"
		System.setProperty(ContextInitializer.CONFIG_FILE_PROPERTY, "." + File.separator + "logback.xml");
		log = LoggerFactory.getLogger(RateServer.class);
	 
		// TODO Auto-generated method stub
		// 级别為debug的日誌
//		log.debug("Hello! debug!");
		// 级别為info的日誌
//		log.info("Hello! info!");
		// 级别為warn的日誌
//		log.warn("Hello! warn!");
		// 级别為error的日誌
//		log.error("Hello! error!");
		try {
			log.info("RateServer server start...");
			DynamicProps dcf = new DynamicProps("rateservice.xml");

//			ServiceServer.createServer();
//			ServiceServer.startServer();

//			EddaClientServer.createServer();
//			EddaClientServer.startServer();

//			ClientController.createController(dcf.getConHashMap());
//			ClientController.startController();

			NodeController.createController(dcf.getConHashMap());
			NodeController.startController();

//			WebServer.createServer();
//			WebServer.startServer();

			log.info("RateServer server start complete!");

			while (isRunning) {
				sleep(1);
				dcf.ChkCfg();
			}

			log.info("RateServer server stop!");
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}

	}

	public static void addDaemonShutdownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
//          	ServiceServer.stopServer();
//            	EddaClientServer.stopServer();
				NodeController.stopController();
//				ClientController.stopController();
//            	WebServer.stopServer();

				RateServer.sleep(1);
				RateServer.isRunning = false;
			}
		});
	}

	public static void sleep(int t) {
		try {
			Thread.sleep(t * 3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
