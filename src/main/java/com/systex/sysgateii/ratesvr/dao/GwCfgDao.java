package com.systex.sysgateii.ratesvr.dao;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	public static final String ENCODING="UTF-8";

	public GwCfgDao() {
		log.debug("load config from file");
	}
	public List<Map<String, Object>> findAllMemberNodes(int sysFlag, int isEnable) {
		List<Map<String, Object>> list = null;
		log.debug("read AllMemberNodes sysFlag={} isEnable={}", sysFlag, isEnable);
		return list;
	}
}
