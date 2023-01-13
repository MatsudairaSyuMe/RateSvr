package com.systex.sysgateii.ratesvr.conf;

/*
 * DynamicProps
 * reading configuration files
 *    Auto detect configuration changed
 * MatsudairaSyuMe
 * Ver 1.0
 *  20190727 
 */

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

//import com.systex.sysgateii.entities.TB_AUDEVPRM;
//import com.systex.sysgateii.repositories.TB_AUDEVPRMRepository;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.apache.commons.configuration2.ConfigurationMap;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.ConfigurationBuilderEvent;
import org.apache.commons.configuration2.builder.ReloadingFileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.event.EventListener;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.reloading.PeriodicReloadingTrigger;
import org.apache.commons.configuration2.tree.DefaultExpressionEngine;
import org.apache.commons.configuration2.tree.DefaultExpressionEngineSymbols;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.systex.sysgateii.ratesvr.dao.GwCfgDao;
import com.systex.sysgateii.util.LogUtil;

public class DynamicProps {
	private static Logger log = LoggerFactory.getLogger(DynamicProps.class);
	private ReloadingFileBasedConfigurationBuilder<XMLConfiguration> builder = null;
	private final ConcurrentHashMap<String, String> conHashMap = new ConcurrentHashMap<String, String>();

	@SuppressWarnings("resource")
	public DynamicProps(String string) {
		Parameters params = new Parameters();
		List<String> abd = null;
		builder = new ReloadingFileBasedConfigurationBuilder<XMLConfiguration>(XMLConfiguration.class)
				.configure(params.fileBased().setFile(new File(string)));
		try {
			ChkCfg(builder.getConfiguration());
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PeriodicReloadingTrigger trigger = new PeriodicReloadingTrigger(builder.getReloadingController(), null, 1,
				TimeUnit.SECONDS);
		trigger.start();
		// 10/14 SUN uodate
//		builder.addEventListener(ConfigurationBuilderEvent.ANY, new EventListener<ConfigurationBuilderEvent>() {
//			public void onEvent(ConfigurationBuilderEvent event) {
//				log.debug("rateservice.xml been modified Event: {}", event.getEventType().getName());
//				if (event.getEventType() == ConfigurationBuilderEvent.RESET) {
//					XMLConfiguration config;
//					try {
//						config = builder.getConfiguration();
//						DefaultExpressionEngine engine = new DefaultExpressionEngine(
//								DefaultExpressionEngineSymbols.DEFAULT_SYMBOLS);
//						// 指定表達示引擎
//						config.setExpressionEngine(engine);
//						Map<Object, Object> cfg = new ConfigurationMap(config);
//						cfg.entrySet();
//						for (@SuppressWarnings("rawtypes")
//						Map.Entry entry : cfg.entrySet()) {
//							log.info("ConfProc info! {}, {}", entry.getKey(), entry.getValue());
//							if (entry.getKey().equals("system.ip") || entry.getKey().equals("system.port")
//									|| entry.getKey().equals("svrsubport.svrip")
//									|| entry.getKey().equals("svrsubport.svrport")
//									|| entry.getKey().equals("svrsubport.localip")
//									|| entry.getKey().equals("svrsubport.localport")
//									|| entry.getKey().equals("svrsubport.verhbrno")
//									|| entry.getKey().equals("svrsubport.verhwsno")
//									|| entry.getKey().equals("boards.board.brno")
//									|| entry.getKey().equals("boards.board.ip")) {
//								conHashMap.put(entry.getKey().toString(), entry.getValue().toString());
//								log.info("ConfProc put to config map info! {}, {}", entry.getKey(), entry.getValue());
//							}
//						}
//					} catch (ConfigurationException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//
//				}
//			}
//		});
	}

	public void Chat() throws InterruptedException, ConfigurationException {
		checkresult(this.builder);
	}

	private static void checkresult(ReloadingFileBasedConfigurationBuilder<XMLConfiguration> builder)
			throws InterruptedException, ConfigurationException {
		while (true) {
			Thread.sleep(1000);
			getcfg(builder.getConfiguration());
		}
	}

	public static void getcfg(XMLConfiguration config) {

		DefaultExpressionEngine engine = new DefaultExpressionEngine(DefaultExpressionEngineSymbols.DEFAULT_SYMBOLS);
		// 指定表達示引擎
		config.setExpressionEngine(engine);

		log.info("{}", config.getInt("boards.board.brno"));
		log.info("{}", config.getInt("boards.board.id"));
		log.info("{}", config.getString("boards.board.ip"));
		log.info("{}", config.getString("boards.board.start[@description]"));
	}

	public void ChkCfg(XMLConfiguration config) throws ConfigurationException {
		// 10/14 SUN uodate
		DefaultExpressionEngine engine = new DefaultExpressionEngine(DefaultExpressionEngineSymbols.DEFAULT_SYMBOLS);
		// 指定表達示引擎
		config.setExpressionEngine(engine);
		Map<Object, Object> cfg = new ConfigurationMap(config);
		cfg.entrySet();
		for (@SuppressWarnings("rawtypes")
		Map.Entry entry : cfg.entrySet()) {
//			System.out.println(entry.getKey() + ", " + entry.getValue());
			log.info("ConfProc info! {}, {}", entry.getKey(), entry.getValue());
			if (entry.getKey().equals("system.ip") || entry.getKey().equals("system.port")
//					|| entry.getKey().equals("svrsubport.svrip") || entry.getKey().equals("svrsubport.svrport")
//					|| entry.getKey().equals("svrsubport.localip") || entry.getKey().equals("svrsubport.localport")
//					|| entry.getKey().equals("svrsubport.verhbrno") || entry.getKey().equals("svrsubport.verhwsno")
//					|| entry.getKey().equals("boards.board.brno") || entry.getKey().equals("boards.board.ip")
					) {
				conHashMap.put(entry.getKey().toString(), entry.getValue().toString());
				log.info("ConfProc put to config map info! {}, {}", entry.getKey(), entry.getValue());
			}
			// SUN update
			else if(entry.getKey().toString().contains("Resource")) {
				log.info("ConfProc put to config map info! {}, {}", entry.getKey(), entry.getValue());
				conHashMap.put(entry.getKey().toString(), entry.getValue().toString());
			}
		}
		String ipPort ="";
		List<Map<String,String>> rtnVal = null;
		try {
			log.info("READ DB DATA!");
			GwCfgDao GwCfgDao = new GwCfgDao();
			ipPort = GwCfgDao.SelectTB_AUFASPRM();
		}catch(Throwable e) {
			// TODO Auto-generated catch block
			log.error("READ IPDB ERROR={}",e.getMessage());
		}
		if(StringUtils.isNotBlank(ipPort)) {
			String surip ="";
			String svrport ="";
			String localip ="";
			String localport ="";
			//192.168.14.91:15000=192.168.14.129:15331
			surip = ipPort.split("=")[0].split(":")[0];
			svrport = ipPort.split("=")[0].split(":")[1];
			localip = ipPort.split("=")[1].split(":")[0];
			localport = ipPort.split("=")[1].split(":")[1];
			log.info("DB DATA svrsubport.svrip={}",LogUtil.vaildLog(surip)); //20230113 chsange to use LogUtil.vaildLog
			log.info("DB DATA svrsubport.svrport={}",LogUtil.vaildLog(svrport));
			log.info("DB DATA svrsubport.localip={}",LogUtil.vaildLog(localip));
			log.info("DB DATA svrsubport.localport={}",LogUtil.vaildLog(localport));
			conHashMap.put("svrsubport.svrip",LogUtil.vaildLog(surip));
			conHashMap.put("svrsubport.svrport",LogUtil.vaildLog(svrport));
			conHashMap.put("svrsubport.localip",LogUtil.vaildLog(localip));
			conHashMap.put("svrsubport.localport",LogUtil.vaildLog(localport));
		}
		try {
			log.info("READ DB DATA!");
			GwCfgDao GwCfgDao = new GwCfgDao();
			log.info("GETCONNECT FINISH!");
			rtnVal = GwCfgDao.SELECTALLTB_AUDEVPRM();
			log.info("READ BOARDDB DATA FINISHED!");
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			log.error("READ DB ERROR={}",e.getMessage());
		}
		if(rtnVal != null) {
			log.info("DB DATA RTNVAL.SIZE={}",rtnVal.size());
			String[] iplist = new String[rtnVal.size()];
			String[] brnolist = new String[rtnVal.size()];
			int i = 0;
			for(Map<String,String> alboard:rtnVal) {
				iplist[i] = alboard.get("boards.board.ip");
				brnolist[i] = alboard.get("boards.board.brno");
				log.info("DB DATA IP={}",LogUtil.vaildLog(iplist[i]));
				log.info("DB DATA BRNO={}",LogUtil.vaildLog(brnolist[i]));
				i++;
			}
			log.info("DB DATA boards.board.IP={}",LogUtil.vaildLog(Arrays.asList(iplist).toString()));
			log.info("DB DATA boards.board.BRNO={}",LogUtil.vaildLog(Arrays.asList(brnolist).toString()));
			conHashMap.put("boards.board.ip",Arrays.asList(iplist).toString());
			conHashMap.put("boards.board.brno",Arrays.asList(brnolist).toString());
		}
		/**/
	}

	public void ChkCfg() throws ConfigurationException {
	}

	public static void main(String[] args) throws Exception {
		DynamicProps dcf = new DynamicProps("rateservice.xml");
		dcf.Chat();
	}

	public ConcurrentHashMap<String, String> getConHashMap() {
		return conHashMap;
	}
}
