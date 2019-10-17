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
import java.util.Map;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.ConcurrentHashMap;

public class DynamicProps {
	private static Logger log = LoggerFactory.getLogger(DynamicProps.class);
	private ReloadingFileBasedConfigurationBuilder<XMLConfiguration> builder = null;
	private final ConcurrentHashMap<String, String> conHashMap = new ConcurrentHashMap<String, String>();

	public DynamicProps(String string) {
		Parameters params = new Parameters();
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

		builder.addEventListener(ConfigurationBuilderEvent.ANY, new EventListener<ConfigurationBuilderEvent>() {
			public void onEvent(ConfigurationBuilderEvent event) {
				log.debug("rateservice.xml been modified Event: {}", event.getEventType().getName());
				if (event.getEventType() == ConfigurationBuilderEvent.RESET) {
					XMLConfiguration config;
					try {
						config = builder.getConfiguration();
						DefaultExpressionEngine engine = new DefaultExpressionEngine(
								DefaultExpressionEngineSymbols.DEFAULT_SYMBOLS);
						// 指定表達示引擎
						config.setExpressionEngine(engine);
						Map<Object, Object> cfg = new ConfigurationMap(config);
						cfg.entrySet();
						for (@SuppressWarnings("rawtypes")
						Map.Entry entry : cfg.entrySet()) {
							log.info("ConfProc info! {}, {}", entry.getKey(), entry.getValue());
							if (entry.getKey().equals("system.ip") || entry.getKey().equals("system.port")
									|| entry.getKey().equals("svrsubport.svrip")
									|| entry.getKey().equals("svrsubport.svrport")
									|| entry.getKey().equals("svrsubport.localip")
									|| entry.getKey().equals("svrsubport.localport")
									|| entry.getKey().equals("svrsubport.verhbrno")
									|| entry.getKey().equals("svrsubport.verhwsno")
									|| entry.getKey().equals("boards.board.brno")
									|| entry.getKey().equals("boards.board.ip")) {
								conHashMap.put(entry.getKey().toString(), entry.getValue().toString());
								log.info("ConfProc put to config map info! {}, {}", entry.getKey(), entry.getValue());
							}
						}
					} catch (ConfigurationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
		});
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

		System.out.println(config.getInt("boards.board.brno"));
		System.out.println(config.getInt("boards.board.id"));
		System.out.println(config.getString("boards.board.ip"));
		System.out.println(config.getString("boards.board.start[@description]"));
	}

	public void ChkCfg(XMLConfiguration config) throws ConfigurationException {

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
					|| entry.getKey().equals("svrsubport.svrip") || entry.getKey().equals("svrsubport.svrport")
					|| entry.getKey().equals("svrsubport.localip") || entry.getKey().equals("svrsubport.localport")
					|| entry.getKey().equals("svrsubport.verhbrno") || entry.getKey().equals("svrsubport.verhwsno")
					|| entry.getKey().equals("boards.board.brno") || entry.getKey().equals("boards.board.ip")) {
				conHashMap.put(entry.getKey().toString(), entry.getValue().toString());
				log.info("ConfProc put to config map info! {}, {}", entry.getKey(), entry.getValue());
			}
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
