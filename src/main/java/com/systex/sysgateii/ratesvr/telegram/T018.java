package com.systex.sysgateii.ratesvr.telegram;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class T018 {
	private static Logger log = LoggerFactory.getLogger(T018.class);

	private final static int OC = 7;

	private final static int CURCDLEN = 2;
	private final static int FIXRT1LEN = 6;
	private final static int F1LEN = 1;
	private final static int FIXRT2LEN = 6;
	private final static int F2LEN = 1;
	private final static int FIXRT3LEN = 6;
	private final static int F3LEN = 1;
	private final static int FIXRT4LEN = 6;
	private final static int F4LEN = 1;
	private final static int FIXRT5LEN = 6;
	private final static int F5LEN = 1;
	private final static int FIXRT6LEN = 6;
	private final static int F6LEN = 1;
	private final static int FIXRT7LEN = 6;
	private final static int F7LEN = 1;
	private final static int FIXRT8LEN = 6;
	private final static int F8LEN = 1;
	private final static int FIXRT9LEN = 6;
	private final static int F9LEN = 1;

	private final static int CURCDSTARTPOS = 0;
	private final static int FIXRT1STARTPOS = CURCDSTARTPOS + CURCDLEN;
	private final static int F1STARTPOS = FIXRT1STARTPOS + FIXRT1LEN;
	private final static int FIXRT2STARTPOS = F1STARTPOS + F1LEN;
	private final static int F2STARTPOS = FIXRT2STARTPOS + FIXRT2LEN;
	private final static int FIXRT3STARTPOS = F2STARTPOS + F2LEN;
	private final static int F3STARTPOS = FIXRT3STARTPOS + FIXRT3LEN;
	private final static int FIXRT4STARTPOS = F3STARTPOS + F3LEN;
	private final static int F4STARTPOS = FIXRT4STARTPOS + FIXRT4LEN;
	private final static int FIXRT5STARTPOS = F4STARTPOS + F4LEN;
	private final static int F5STARTPOS = FIXRT5STARTPOS + FIXRT5LEN;
	private final static int FIXRT6STARTPOS = F5STARTPOS + F5LEN;
	private final static int F6STARTPOS = FIXRT6STARTPOS + FIXRT6LEN;
	private final static int FIXRT7STARTPOS = F6STARTPOS + F6LEN;
	private final static int F7STARTPOS = FIXRT7STARTPOS + FIXRT7LEN;
	private final static int FIXRT8STARTPOS = F7STARTPOS + F7LEN;
	private final static int F8STARTPOS = FIXRT8STARTPOS + FIXRT8LEN;
	private final static int FIXRT9STARTPOS = F8STARTPOS + F8LEN;
	private final static int F9STARTPOS = FIXRT9STARTPOS + FIXRT9LEN;

	private static final int OCCURSLEN = F9STARTPOS + F9LEN;

	private byte[][] CURCD = new byte[OC][CURCDLEN];
	private byte[][] FIXRT1 = new byte[OC][FIXRT1LEN];
	private byte[][] F1 = new byte[OC][F1LEN];
	private byte[][] FIXRT2 = new byte[OC][FIXRT2LEN];
	private byte[][] F2 = new byte[OC][F2LEN];
	private byte[][] FIXRT3 = new byte[OC][FIXRT3LEN];
	private byte[][] F3 = new byte[OC][F3LEN];
	private byte[][] FIXRT4 = new byte[OC][FIXRT4LEN];
	private byte[][] F4 = new byte[OC][F4LEN];
	private byte[][] FIXRT5 = new byte[OC][FIXRT5LEN];
	private byte[][] F5 = new byte[OC][F5LEN];
	private byte[][] FIXRT6 = new byte[OC][FIXRT6LEN];
	private byte[][] F6 = new byte[OC][F6LEN];
	private byte[][] FIXRT7 = new byte[OC][FIXRT7LEN];
	private byte[][] F7 = new byte[OC][F7LEN];
	private byte[][] FIXRT8 = new byte[OC][FIXRT8LEN];
	private byte[][] F8 = new byte[OC][F8LEN];
	private byte[][] FIXRT9 = new byte[OC][FIXRT9LEN];
	private byte[][] F9 = new byte[OC][F9LEN];
	private CopyOnWriteArrayList<String> RateRecList = new CopyOnWriteArrayList<String>();
//	DecimalFormat df3 = new DecimalFormat("###");
//	DecimalFormat df3_5 = new DecimalFormat("###.#####");
//	DecimalFormat df1_5 = new DecimalFormat("#.#####");

	private String brno = "";
	private String wsno = "";
	private int startiCurcd = -1;

	public byte[] getCURCD(int idx) {
		return CURCD[idx];
	}

	public byte[] getFIXRT1(int idx) {
		return FIXRT1[idx];
	}

	public byte[] getFIXRT2(int idx) {
		return FIXRT2[idx];
	}

	public byte[] getFIXRT3(int idx) {
		return FIXRT3[idx];
	}

	public byte[] getFIXRT4(int idx) {
		return FIXRT4[idx];
	}

	public byte[] getFIXRT5(int idx) {
		return FIXRT5[idx];
	}

	public byte[] getFIXRT6(int idx) {
		return FIXRT6[idx];
	}

	public byte[] getFIXRT7(int idx) {
		return FIXRT7[idx];
	}

	public byte[] getFIXRT8(int idx) {
		return FIXRT8[idx];
	}

	public byte[] getFIXRT9(int idx) {
		return FIXRT9[idx];
	}

	public CopyOnWriteArrayList<String> getRateRecList() {
		return RateRecList;
	}

	public void setRateRecList(CopyOnWriteArrayList<String> rateRecList) {
		RateRecList = rateRecList;
	}

	public String getBrno() {
		return brno;
	}

	public void setBrno(String brno) {
		this.brno = brno;
	}

	public String getWsno() {
		return wsno;
	}

	public void setWsno(String wsno) {
		this.wsno = wsno;
	}

	public int getStartiCurcd() {
		return startiCurcd;
	}

	public void setStartiCurcd(int startiCurcd) {
		this.startiCurcd = startiCurcd;
	}

	public T018(String brno, String wsno) {
		this.brno = brno;
		this.wsno = wsno;
		this.startiCurcd = 1;
		this.RateRecList.clear();
	}

	private String leftzero(byte[] num) {
		String numstr = String.valueOf(Integer.parseInt((new String(num)).trim()));
		if (numstr.length() < 4 && Integer.parseInt(numstr) > 0) {
			return String.format("%04d", Integer.parseInt((new String(num)).trim()));
		} else if(numstr.length() < 4 && Integer.parseInt(numstr) == 0){
			return String.format("%4s", new String());
		} else {
			return numstr;
		}
	}

	private int numOfrate(byte[] rate) {
		if (Integer.parseInt((new String(rate)).trim()) == 0) {
			return 0;
		} else {
			return 2;
		}
	}

	public void setData(byte[] src) {
		byte[] data = null;
		List<Float> cdList = null;
		String sndstr;
		log.debug(new String(src));
		for (int idx = 0; idx < (src.length / T018.OCCURSLEN); idx++) {
			byte[] ocurs = null;
			cdList = new ArrayList<Float>();
			// 取出一整段OCCURS
			data = new byte[T018.OCCURSLEN];
			System.arraycopy(src, (T018.OCCURSLEN * idx), data, 0, T018.OCCURSLEN);

			// 取出幣別
			ocurs = new byte[T018.CURCDLEN];
			System.arraycopy(data, T018.CURCDSTARTPOS, ocurs, 0, T018.CURCDLEN);
			this.CURCD[idx] = ocurs;
			cdList.add(new Float(new String(ocurs)));

			// 取出活期
			ocurs = new byte[T018.FIXRT1LEN];
			System.arraycopy(data, T018.FIXRT1STARTPOS, ocurs, 0, T018.FIXRT1LEN);
			this.FIXRT1[idx] = ocurs;
			cdList.add(new Float(new String(ocurs)));

			// 取出一週
			ocurs = new byte[T018.FIXRT2LEN];
			System.arraycopy(data, T018.FIXRT2STARTPOS, ocurs, 0, T018.FIXRT2LEN);
			this.FIXRT2[idx] = ocurs;
			cdList.add(new Float(new String(ocurs)));

			// 取出二週
			ocurs = new byte[T018.FIXRT3LEN];
			System.arraycopy(data, T018.FIXRT3STARTPOS, ocurs, 0, T018.FIXRT3LEN);
			this.FIXRT3[idx] = ocurs;
			cdList.add(new Float(new String(ocurs)));

			// 取出三週
			ocurs = new byte[T018.FIXRT4LEN];
			System.arraycopy(data, T018.FIXRT4STARTPOS, ocurs, 0, T018.FIXRT4LEN);
			this.FIXRT4[idx] = ocurs;
			cdList.add(new Float(new String(ocurs)));

			// 取出一月
			ocurs = new byte[T018.FIXRT5LEN];
			System.arraycopy(data, T018.FIXRT5STARTPOS, ocurs, 0, T018.FIXRT5LEN);
			this.FIXRT5[idx] = ocurs;
			cdList.add(new Float(new String(ocurs)));

			// 取出三月
			ocurs = new byte[T018.FIXRT6LEN];
			System.arraycopy(data, T018.FIXRT6STARTPOS, ocurs, 0, T018.FIXRT6LEN);
			this.FIXRT6[idx] = ocurs;
			cdList.add(new Float(new String(ocurs)));

			// 取出六月
			ocurs = new byte[T018.FIXRT7LEN];
			System.arraycopy(data, T018.FIXRT7STARTPOS, ocurs, 0, T018.FIXRT7LEN);
			this.FIXRT7[idx] = ocurs;
			cdList.add(new Float(new String(ocurs)));

			// 取出九月
			ocurs = new byte[T018.FIXRT8LEN];
			System.arraycopy(data, T018.FIXRT8STARTPOS, ocurs, 0, T018.FIXRT8LEN);
			this.FIXRT8[idx] = ocurs;
			cdList.add(new Float(new String(ocurs)));

			// 取出一年
			ocurs = new byte[T018.FIXRT9LEN];
			System.arraycopy(data, T018.FIXRT9STARTPOS, ocurs, 0, T018.FIXRT9LEN);
			this.FIXRT9[idx] = ocurs;
			cdList.add(new Float(new String(ocurs)));

			log.debug(
					"data={},CURCD={} \n\r  FIXRT1={},FIXRT2={},FIXRT3={},FIXRT4={},FIXRT5={},FIXRT6={},FIXRT7={},FIXRT8={},FIXRT9={}",
					new String(data), getCURCD(idx), getFIXRT1(idx), getFIXRT2(idx), getFIXRT3(idx), getFIXRT4(idx),
					getFIXRT5(idx), getFIXRT6(idx), getFIXRT7(idx), getFIXRT8(idx), getFIXRT9(idx));
			// XS983960000R0095 AT007500000000906030161527
			if (this.startiCurcd == 1) {
				Date date = new Date();
				SimpleDateFormat sdFormat2 = new SimpleDateFormat("yyyy");
				String year = String.valueOf(Integer.parseInt(sdFormat2.format(date)) - 1911).substring(1);
				sndstr = String.format(
						"XS%s%s00R0095    AT00750000000%s%tm%td0%tl%tM%tS                                                 ",
						brno, wsno, year, date, date, date, date, date);
				this.RateRecList.add(sndstr);
				log.debug("pre len={} sndStr=[{}]", sndstr.length(), sndstr);
			}
			// XS983960000R0095 A000750100012 15002 30002 30002 30002 35002 50002 60002
			// 65002 7500
			sndstr = String.format("XS%s%s00R0095    A00075%2s00%02d%d%6s%d%6s%d%6s%d%6s%d%6s%d%6s%d%6s%d%6s%d%6s",
					brno, wsno, new String(getCURCD(idx)), this.RateRecList.size(), numOfrate(getFIXRT1(idx)),
					leftzero(getFIXRT1(idx)), numOfrate(getFIXRT2(idx)), leftzero(getFIXRT2(idx)),
					numOfrate(getFIXRT3(idx)), leftzero(getFIXRT3(idx)), numOfrate(getFIXRT4(idx)),
					leftzero(getFIXRT4(idx)), numOfrate(getFIXRT5(idx)), leftzero(getFIXRT5(idx)),
					numOfrate(getFIXRT6(idx)), leftzero(getFIXRT6(idx)), numOfrate(getFIXRT7(idx)),
					leftzero(getFIXRT7(idx)), numOfrate(getFIXRT8(idx)), leftzero(getFIXRT8(idx)),
					numOfrate(getFIXRT9(idx)), leftzero(getFIXRT9(idx)));
			this.RateRecList.add(sndstr);
			log.debug("len={} sndStr=[{}]", sndstr.length(), sndstr);
			this.startiCurcd += 1;
		}
	}
}
