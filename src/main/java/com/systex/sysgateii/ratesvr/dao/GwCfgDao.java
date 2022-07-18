package com.systex.sysgateii.ratesvr.dao;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.configuration2.ConfigurationMap;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.ReloadingFileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.tree.DefaultExpressionEngine;
import org.apache.commons.configuration2.tree.DefaultExpressionEngineSymbols;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.systex.sysgateii.util.LogUtil;

/*
 * GwCfgDao
 * Configuration object process (file)
 *
 * MatsudairaSyuMe
 * Ver 1.0
 *  20190730 
 */

public class GwCfgDao {
	private static Logger log = LoggerFactory.getLogger(GwCfgDao.class);
	public static final String ENCODING = "UTF-8";
	public static final String XMLPATH = "rateservice.xml";
	private String selurl;
	private String seluser;
	private String selpass;
	private String auid;
	private ResultSet selectresult = null;
	private Connection selconn = null;

	public GwCfgDao() throws Throwable {
		log.info(" new GwCfgDao={}", XMLPATH);
		readXml();
		getDB2Connection();
	}

	public void readXml() {
		log.info("xmlpath={}", XMLPATH);
		File xmlfile = new File(XMLPATH);
		SAXReader reader = new SAXReader();
		Document doc;
		try {
			doc = reader.read(xmlfile);
			Element root = doc.getRootElement();
			Element foo;
			for (java.util.Iterator<Element> i = root.elementIterator("Resource"); i.hasNext();) {
				foo = i.next();
//				log.info("Resource ==>{}", foo);
				setSelurl(foo.attribute("url").getStringValue());
				setSelpass(foo.attribute("password").getStringValue());
				setSeluser(foo.attribute("username").getStringValue());
			}
			Parameters params = new Parameters();
			ReloadingFileBasedConfigurationBuilder<XMLConfiguration> builder = new ReloadingFileBasedConfigurationBuilder<XMLConfiguration>(XMLConfiguration.class)
					.configure(params.fileBased().setFile(new File(XMLPATH)));
			DefaultExpressionEngine engine = new DefaultExpressionEngine(DefaultExpressionEngineSymbols.DEFAULT_SYMBOLS);
			builder.getConfiguration().setExpressionEngine(engine);
			Map<Object, Object> cfg = new ConfigurationMap(builder.getConfiguration());
			cfg.entrySet();
			for (@SuppressWarnings("rawtypes")
			Map.Entry entry : cfg.entrySet()) {
				if(entry.getKey().equals("system.auid")) {
					setAuid(entry.getValue().toString());
				}
			}
			if(StringUtils.isBlank(getAuid())) {
				setAuid("2");
			}
		} catch (DocumentException e) {
			log.info(e.getMessage());
		} catch (ConfigurationException e) {
			log.info(e.getMessage());
		}
	}
	public List<Map<String, Object>> findAllMemberNodes(int sysFlag, int isEnable) {
		List<Map<String, Object>> list = null;
		log.info("read AllMemberNodes sysFlag={} isEnable={}", sysFlag, isEnable);
		return list;
	}

	public void getDB2Connection() throws Exception {
		Class.forName("com.ibm.db2.jcc.DB2Driver");
		log.info("Driver Loaded.");
		setSelconn(DriverManager.getConnection(getSelurl(), getSeluser(), getSelpass()));
	}

	public void CloseConnect() throws Exception {
		try {
			if (selconn != null)
				selconn.close();
		} catch (SQLException se) {
			log.error("CloseConnect():{}", se.getMessage());
		} // end finally try
	}

	public List<String> SELECTTB_AUDEVPRM(String DEVTPE, String BRWS) throws Exception {
		List<String> rtnVal = new ArrayList<String>();
		try {
			java.sql.Statement stmt = selconn.createStatement();
			selectresult = ((java.sql.Statement) stmt).executeQuery(
					"SELECT IP FROM TB_AUDEVPRM where DEVTPE = " + DEVTPE + " and BRWS like '" + BRWS + "%'");
			if (selectresult != null) {
				while (selectresult.next()) {
					rtnVal.add(selectresult.getString("IP"));
				}
			}
		} catch (Exception e) {
			log.info("error : {}", e.toString());
		} finally {
			CloseConnect();
		}
		log.info("return TBSDY=[{}]", LogUtil.vaildLog(rtnVal.toString()));
		return rtnVal;
	}

	// 10/14 SUN uodate
	public List<Map<String, String>> SELECTALLTB_AUDEVPRM() throws Exception {
		String selectsql = "SELECT IP,BRWS FROM TB_AUDEVPRM WHERE DEVTPE IN (0,1)";
		List<Map<String, String>> rtnVal = new ArrayList<Map<String, String>>();
		try {
			PreparedStatement selSQL = selconn.prepareStatement(selectsql);
			selectresult = selSQL.executeQuery();
			if (selectresult != null) {
				while (selectresult.next()) {
					Map<String, String> setrtn = new HashMap<String, String>();
					setrtn.put("boards.board.ip", selectresult.getString("IP"));
					setrtn.put("boards.board.brno", selectresult.getString("BRWS"));
					rtnVal.add(setrtn);
				}
			}
		} catch (Exception e) {
			log.info("error : {}", e.toString());
		} finally {
			CloseConnect();
		}
		log.info("return TBSDY=[{}]", LogUtil.vaildLog(rtnVal.toString()));
		return rtnVal;
	}

	// 10/14 SUN uodate
	public void UPDATETB_AUDEVSTS(String IP, String CURSTUS) throws Exception {
		String selectsql = "SELECT * FROM TB_AUDEVSTS WHERE IP = ?";
		String updatesql = "update TB_AUDEVSTS set CURSTUS = ?" + ",VERSION = ?" + ",LASTUPDATE = ?" + ",MODIFIER = ?"  + " where ip = ?"; //20220713 add MODIFIER = SYSTEM
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		String timestamp = "";
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		timestamp = sdf.format(ts);
		try {
			PreparedStatement selSQL = selconn.prepareStatement(selectsql);
			selSQL.setString(1, IP);
			selectresult = selSQL.executeQuery();
			if (selectresult.next()) {
				String verStr = "";
				verStr = String.valueOf((Integer.parseInt(selectresult.getString("VERSION")) + 1) % 10000); //20220718 MatsudairaSyuMe max 0~9999
				selSQL = selconn.prepareStatement(updatesql);
				selSQL.setString(1, CURSTUS);
				selSQL.setString(2, verStr);
				selSQL.setString(3, timestamp);
				selSQL.setString(4, "SYSTEM"); //20220713 add MODIFIER = SYSTEM
				selSQL.setString(5, IP); //20220713 change to 5 for  MODIFIER = SYSTEM
				selSQL.executeUpdate();
			} else {
				INSERTTB_AUDEVSTS(IP, CURSTUS);
			}
			selconn.commit(); //20220706 MatsudairaSyume
		} catch (Exception e) {
			log.info("error : {}", e.toString());
		} finally {
			CloseConnect();
		}
	}

	public void INSERTTB_AUDEVSTS(String IP, String CURSTUS) throws Exception {
		String selectsql = "SELECT * FROM TB_AUDEVPRM where IP = ?";
		String insertsql = "insert into TB_AUDEVSTS (BRWS,IP,PORT,SVRID,SYSIP,SYSPORT,ACTPAS,DEVTPE,CURSTUS,VERSION,CREATOR,CREATETIME,LASTUPDATE,MODIFIER) VALUES "
				+ "(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		String timestamp = "";
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		timestamp = sdf.format(ts);
		try {
			PreparedStatement selSQL = selconn.prepareStatement(selectsql);
			log.info(" IP={}", LogUtil.vaildLog(IP));
			selSQL.setString(1, IP);
			selectresult = selSQL.executeQuery();
			log.info(" selectresult={}", LogUtil.vaildLog(selectresult.toString()));
			if (selectresult.next()) {
				selSQL = selconn.prepareStatement(insertsql);
				log.info(" BRWS={}", LogUtil.vaildLog(selectresult.getString("BRWS")));
				selSQL.setString(1, selectresult.getString("BRWS"));
				selSQL.setString(2, IP);
				log.info(" PORT={}", LogUtil.vaildLog(selectresult.getString("PORT")));
				selSQL.setString(3, selectresult.getString("PORT"));
				log.info(" SVRID={}", LogUtil.vaildLog(selectresult.getString("SVRID")));
				selSQL.setString(4, selectresult.getString("SVRID"));
				selSQL.setString(5, IP);
				selSQL.setString(6, selectresult.getString("PORT"));
				log.info(" ACTPAS={}", LogUtil.vaildLog(selectresult.getString("ACTPAS")));
				selSQL.setString(7, selectresult.getString("ACTPAS"));
				log.info(" DEVTPE={}", LogUtil.vaildLog(selectresult.getString("DEVTPE")));
				selSQL.setString(8, selectresult.getString("DEVTPE"));
				log.info(" CURSTUS={}", LogUtil.vaildLog(CURSTUS));
				selSQL.setString(9, CURSTUS);
				selSQL.setString(10, "1");
				selSQL.setString(11, "SYSTEM"); //20220713 change to SYSTEM
				log.info(" DEVTPE={}", LogUtil.vaildLog(timestamp));
				selSQL.setString(12, timestamp);
				selSQL.setString(13, timestamp);
				selSQL.setString(14, "SYSTEM"); //20220713 change to SYSTEM
				selSQL.executeUpdate();
			}
		} catch (SQLException e) {
			log.info("error : {}", e.toString());
		} finally {
			CloseConnect();
		}
	}

	public String SelectTB_AUFASPRM() throws Exception {
		String rtnVal ="";
		try {
			java.sql.Statement stmt = selconn.createStatement();
			selectresult = ((java.sql.Statement) stmt).executeQuery("SELECT CONNPRM FROM TB_AUFASPRM where AUID = "+getAuid());
			if (selectresult != null) {
				while (selectresult.next()) {
					rtnVal = selectresult.getString("CONNPRM");
				}
			}
		} catch (Exception e) {
			log.info("error : {}", e.toString());
		} finally {
			CloseConnect();
		}
		log.info("return TBSDY=[{}]", LogUtil.vaildLog(rtnVal));
		return rtnVal;
	}

	public String getSelurl() {
		return selurl;
	}

	public void setSelurl(String selurl) {
		this.selurl = selurl;
	}

	public String getSeluser() {
		return seluser;
	}

	public void setSeluser(String seluser) {
		this.seluser = seluser;
	}

	public String getSelpass() {
		return selpass;
	}

	public void setSelpass(String selpass) {
		this.selpass = selpass;
	}

	public Connection getSelconn() {
		return selconn;
	}

	public void setSelconn(Connection selconn) {
		this.selconn = selconn;
	}

	public String getAuid() {
		return auid;
	}

	public void setAuid(String auid) {
		this.auid = auid;
	}
	
}
