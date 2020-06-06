package com.systex.sysgateii.ratesvr.telegram;

//import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
//import java.util.Arrays;
//import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class S004 {

	private static Logger log = LoggerFactory.getLogger(S004.class);
	private final static int MRKDTLEN = 8;
	private final static int SYSDTLEN = 8;
	private final static int MRKTTMLEN = 4;
	private final static int FILLER2LEN = 2;
	private final static int TYPELEN = 1;

	private final static int MRKDTSTARTPOS = 0;
	private final static int SYSDTSTARTPOS = MRKDTSTARTPOS + MRKDTLEN;
	private final static int MRKTTMSTARTPOS = SYSDTSTARTPOS + SYSDTLEN;
	private final static int FILLER2STARTPOS = MRKTTMSTARTPOS + MRKTTMLEN;
	private final static int TYPESTARTPOS = FILLER2STARTPOS + FILLER2LEN;
	private final static int OCSTARTPOS = TYPESTARTPOS + TYPELEN;

	private final static int OC = 2;
	private final static int CURCDLEN = 2;
	private final static int MRKTCDLEN = 1;
	private final static int BRATSLEN = 8;
	private final static int SRATSLEN = 8;
	private final static int USCRATLEN = 8;
	private final static int RCNTLEN = 6;
	private final static int BRAT1LEN = 8;
	private final static int BRAT3LEN = 8;
	private final static int BRAT6LEN = 8;
	private final static int BRAT9LEN = 8;
	private final static int BRAT12LEN = 8;
	private final static int BRAT15LEN = 8;
	private final static int BRAT18LEN = 8;
	private final static int BRAT1YLEN = 8;
	private final static int BRAT2YLEN = 8;
	private final static int BRAT3YLEN = 8;
	private final static int SRAT1LEN = 8;
	private final static int SRAT3LEN = 8;
	private final static int SRAT6LEN = 8;
	private final static int SRAT9LEN = 8;
	private final static int SRAT12LEN = 8;
	private final static int SRAT15LEN = 8;
	private final static int SRAT18LEN = 8;
	private final static int SRAT1YLEN = 8;
	private final static int SRAT2YLEN = 8;
	private final static int SRAT3YLEN = 8;
	private final static int BCASHLEN = 8;
	private final static int SCASHLEN = 8;
	private final static int FXRATLEN = 8;
	private final static int ACRATLEN = 8;
	private final static int BLEN = 1;
	private final static int LMTBLEN = 6;
	private final static int SLEN = 1;
	private final static int LMTSLEN = 6;
	private final static int RLRATLEN = 8;
	private final static int CODELEN = 1;

	private final static int CURCDSTARTPOS = 0;
	private final static int MRKTCDSTARTPOS = CURCDSTARTPOS + CURCDLEN;
	private final static int BRATSSTARTPOS = MRKTCDSTARTPOS + MRKTCDLEN;
	private final static int SRATSSTARTPOS = BRATSSTARTPOS + BRATSLEN;
	private final static int USCRATSTARTPOS = SRATSSTARTPOS + SRATSLEN;
	private final static int RCNTSTARTPOS = USCRATSTARTPOS + USCRATLEN;
	private final static int BRAT1STARTPOS = RCNTSTARTPOS + RCNTLEN;
	private final static int BRAT3STARTPOS = BRAT1STARTPOS + BRAT1LEN;
	private final static int BRAT6STARTPOS = BRAT3STARTPOS + BRAT3LEN;
	private final static int BRAT9STARTPOS = BRAT6STARTPOS + BRAT6LEN;
	private final static int BRAT12STARTPOS = BRAT9STARTPOS + BRAT9LEN;
	private final static int BRAT15STARTPOS = BRAT12STARTPOS + BRAT12LEN;
	private final static int BRAT18STARTPOS = BRAT15STARTPOS + BRAT15LEN;
	private final static int BRAT1YSTARTPOS = BRAT18STARTPOS + BRAT18LEN;
	private final static int BRAT2YSTARTPOS = BRAT1YSTARTPOS + BRAT1YLEN;
	private final static int BRAT3YSTARTPOS = BRAT2YSTARTPOS + BRAT2YLEN;
	private final static int SRAT1STARTPOS = BRAT3YSTARTPOS + BRAT3YLEN;
	private final static int SRAT3STARTPOS = SRAT1STARTPOS + SRAT1LEN;
	private final static int SRAT6STARTPOS = SRAT3STARTPOS + SRAT3LEN;
	private final static int SRAT9STARTPOS = SRAT6STARTPOS + SRAT6LEN;
	private final static int SRAT12STARTPOS = SRAT9STARTPOS + SRAT9LEN;
	private final static int SRAT15STARTPOS = SRAT12STARTPOS + SRAT12LEN;
	private final static int SRAT18STARTPOS = SRAT15STARTPOS + SRAT15LEN;
	private final static int SRAT1YSTARTPOS = SRAT18STARTPOS + SRAT18LEN;
	private final static int SRAT2YSTARTPOS = SRAT1YSTARTPOS + SRAT1YLEN;
	private final static int SRAT3YSTARTPOS = SRAT2YSTARTPOS + SRAT2YLEN;
	private final static int BCASHSTARTPOS = SRAT3YSTARTPOS + SRAT3YLEN;
	private final static int SCASHSTARTPOS = BCASHSTARTPOS + BCASHLEN;
	private final static int FXRATSTARTPOS = SCASHSTARTPOS + SCASHLEN;
	private final static int ACRATSTARTPOS = FXRATSTARTPOS + FXRATLEN;
	private final static int BSTARTPOS = ACRATSTARTPOS + ACRATLEN;
	private final static int LMTBSTARTPOS = BSTARTPOS + BLEN;
	private final static int SSTARTPOS = LMTBSTARTPOS + LMTBLEN;
	private final static int LMTSSTARTPOS = SSTARTPOS + SLEN;
	private final static int RLRATSTARTPOS = LMTSSTARTPOS + LMTSLEN;
	private final static int CODESTARTPOS = RLRATSTARTPOS + RLRATLEN;

	private static final int RARYLEN = (CURCDLEN + MRKTCDLEN + BRATSLEN + SRATSLEN + USCRATLEN + RCNTLEN + BRAT1LEN
			+ BRAT3LEN + BRAT6LEN + BRAT9LEN + BRAT12LEN + BRAT15LEN + BRAT18LEN + BRAT1YLEN + BRAT2YLEN + BRAT3YLEN
			+ SRAT1LEN + SRAT3LEN + SRAT6LEN + SRAT9LEN + SRAT12LEN + SRAT15LEN + SRAT18LEN + SRAT1YLEN + SRAT2YLEN
			+ SRAT3YLEN + BCASHLEN + SCASHLEN + FXRATLEN + ACRATLEN + BLEN + LMTBLEN + SLEN + LMTSLEN + RLRATLEN
			+ CODELEN);

	private final static int PEN_XCERT1LEN = 6;
	private byte[] mrktdt = new byte[MRKDTLEN];
	private byte[] sysdt = new byte[SYSDTLEN];
	private byte[] mrkttm = new byte[MRKTTMLEN];
	private byte[] type = new byte[TYPELEN];
	private byte[][] oc_bratedata = new byte[OC][RARYLEN];

	private byte[][] curcd = new byte[OC][CURCDLEN];
	private byte[][] mrktcd = new byte[OC][MRKTCDLEN];
	private byte[][] brats = new byte[OC][BRATSLEN];
	private byte[][] srats = new byte[OC][SRATSLEN];
	private byte[][] uscrat = new byte[OC][USCRATLEN];
	private byte[][] rcnt = new byte[OC][RCNTLEN];
	private byte[][] brat1 = new byte[OC][BRAT1LEN];
	private byte[][] brat3 = new byte[OC][BRAT3LEN];
	private byte[][] brat6 = new byte[OC][BRAT6LEN];
	private byte[][] brat9 = new byte[OC][BRAT9LEN];
	private byte[][] brat12 = new byte[OC][BRAT12LEN];
	private byte[][] brat15 = new byte[OC][BRAT15LEN];
	private byte[][] brat18 = new byte[OC][BRAT18LEN];
	private byte[][] brat1y = new byte[OC][BRAT1YLEN];
	private byte[][] brat2y = new byte[OC][BRAT2YLEN];
	private byte[][] brat3y = new byte[OC][BRAT3YLEN];
	private byte[][] srat1 = new byte[OC][SRAT1LEN];
	private byte[][] srat3 = new byte[OC][SRAT3LEN];
	private byte[][] srat6 = new byte[OC][SRAT6LEN];
	private byte[][] srat9 = new byte[OC][SRAT9LEN];
	private byte[][] srat12 = new byte[OC][SRAT12LEN];
	private byte[][] srat15 = new byte[OC][SRAT15LEN];
	private byte[][] srat18 = new byte[OC][SRAT18LEN];
	private byte[][] srat1y = new byte[OC][SRAT1YLEN];
	private byte[][] srat2y = new byte[OC][SRAT2YLEN];
	private byte[][] srat3y = new byte[OC][SRAT3YLEN];
	private byte[][] bcash = new byte[OC][BCASHLEN];
	private byte[][] scash = new byte[OC][SCASHLEN];
	private byte[][] fxrat = new byte[OC][FXRATLEN];
	private byte[][] acrat = new byte[OC][ACRATLEN];
	private byte[][] b = new byte[OC][BLEN];
	private byte[][] lmtb = new byte[OC][LMTBLEN];
	private byte[][] s = new byte[OC][SLEN];
	private byte[][] lmts = new byte[OC][LMTSLEN];
	private byte[][] rlrat = new byte[OC][RLRATLEN];
	private byte[][] code = new byte[OC][CODELEN];
	private CopyOnWriteArrayList<List<Float>> S004RecMap = new CopyOnWriteArrayList<List<Float>>();
	private CopyOnWriteArrayList<String> RateRecList = new CopyOnWriteArrayList<String>();
//	DecimalFormat df3 = new DecimalFormat("###");
//	DecimalFormat df3_5 = new DecimalFormat("###.#####");
//	DecimalFormat df1_5 = new DecimalFormat("#.#####");

	private String brno = "";
	private String wsno = "";
	private int startiCurcd = -1;

	public S004(String brno, String wsno) {
		this.brno = brno;
		this.wsno = wsno;
		this.startiCurcd = 1;
		this.RateRecList.clear();
	}

	private String getStrColno(int iCurcd) {
		int iInt = iCurcd;
		switch (iCurcd) {
		case 4:
			iInt = 3;
			break;
		case 5:
			iInt = 4;
			break;
		case 6:
			iInt = 5;
			break;
		case 7:
			iInt = 25;
			break;
		case 8:
			iInt = 25;
			break;
		case 9:
			iInt = 25;
			break;
		case 10:
			iInt = 6;
			break;
		case 11:
			iInt = 7;
			break;
		case 12:
			iInt = 25;
			break;
		case 13:
			iInt = 8;
			break;
		case 14:
			iInt = 25;
			break;
		case 15:
			iInt = 25;
			break;
		case 16:
			iInt = 9;
			break;
		case 17:
			iInt = 10;
			break;
		case 18:
			iInt = 11;
			break;
		case 19:
			iInt = 12;
			break;
		case 20:
			iInt = 13;
			break;
		case 21:
			iInt = 14;
			break;
		case 22:
			iInt = 15;
			break;
		case 23:
			iInt = 25;
			break;
		case 24:
			iInt = 16;
			break;
		case 25:
			iInt = 19;
			break;
		case 26:
			iInt = 17;
			break;
		case 3:
			iInt = 18;
			break;

		default:
			iInt = iCurcd;
			break;
		}
		return String.format("%02d", iInt);
	}

	private int getDecimal(byte[] src) {
		int decimalno1 = 0;
		for (int index = 7; index >= 3; index--)
			if (src[index] != (byte) '0') {
				decimalno1 = 8 - index;
				break;
			}
		if (decimalno1 == 0) {
			if (Integer.valueOf(new String(src)) != 0)
				decimalno1 = 6;
		}
		return decimalno1;
	}
	//20200606
	private byte[] adjustMantissa(byte[] src) {
		// adjust for 3.5 float value digit put into 6 digit display box
		int intBits = Integer.parseInt(new String(src, 0, 3));
		if (9 < intBits && intBits < 100) {
			if (src[7] != (byte) '0')
				src[7] = (byte) '0';
		} else if (99 < intBits) {
			if (src[7] != (byte) '0')
				src[7] = (byte) '0';
			if (src[6] != (byte) '0')
				src[6] = (byte) '0';
		}
		return src;
	}
	// ----

	private String parFieldValue(byte[] b) {
		String msg = new String(b);
//		System.out.println(msg);
		int decimalno1 = 0;
		for (int index = 7; index >= 3; index--)
			if (b[index] != (byte) '0') {
				decimalno1 = 8 - index;
				msg = new String(b, 0, index + 1).trim();
				if (b[0] == (byte) '0')
					msg = new String(b, 1, index).trim();
				byte[] b2 = msg.getBytes();
				String hdr = new String(b2, 0, 2);
				switch (decimalno1) {
				case 1:
					hdr = String.format("%1d", Integer.valueOf(hdr));
					break;
				case 2:
					hdr = String.format("%2d", Integer.valueOf(hdr));
					break;
				case 3:
					hdr = String.format("%3d", Integer.valueOf(hdr));
					break;
				case 4:
					hdr = String.format("%4d", Integer.valueOf(hdr));
					break;
				case 5:
					hdr = String.format("%5d", Integer.valueOf(hdr));
					break;
				default:
					break;
				}
				String bdy = new String(b2, 2, msg.length() - 2);
				msg = hdr + bdy;
				break;
			}
		if (decimalno1 == 0) {
			if (Integer.valueOf(msg) == 0)
				msg = "      ";
			else {
				int s = -1, e = -1, l = 0;
				for (int i = 0; i < b.length; i++)
					if (b[i] != (byte) '0') {
						s = i;
						break;
					}
				if ((s + 1) < b.length) {
					for (int i = b.length - 1; i >= s + 1; i--) {
						if (b[i] != (byte) '0') {
							e = i;
							l = e - s + 1;
							break;
						}
					}
					if (s >= 0 && e == -1)
						l = 1;
				} else {
					if (s >= 0)
						l = 1;
				}
				msg = String.format("%6d", Integer.valueOf(new String(b, s, l)));
			}
		}
		return msg;
	}
	
	//20100108 adjust decimal and show message
	private int adjustField3_4(String src, int decimal) {
		int leftfirstnotspace = -1;
		int adjust = 0;
		if (decimal < 3)
			return adjust;
		byte[] b = src.getBytes();
		for (int i = 0;i < b.length;i++)
			if (b[i] != (byte)' ') {
				leftfirstnotspace = i;
				break;
			}
		if (decimal == 6) {
			if (leftfirstnotspace < 2)
				adjust = 1;
			else
				adjust = 2;
		} else if (b[b.length - 1] != (byte)'0' && leftfirstnotspace > 0)
			adjust = 1;
//      System.out.println("decimal point djust " + adjust);
		return adjust;
	}

	private int chkadjust(int mindecimal, int decimal) {
		int adjust = 0;
		if (decimal == 0 || mindecimal == 0)
			return adjust;
		return (decimal - mindecimal);
	}

	private String adjustField(String src, int adjust) {
		byte[] b = src.getBytes();
		if (adjust > 0) {
			src = new String(b, adjust, b.length - adjust);
			for (int i = 0; i < adjust; i++)
				src = src + "0";
//          System.out.println(" [" + src + "] <==new");
			}
		return src;
	}
	//----
	public void setData(byte[] src) {
		byte[] b = null;
		byte[] tmpb = new byte[6];
		String sndStr = "";

		b = new byte[S004.MRKDTLEN];
		System.arraycopy(src, S004.MRKDTSTARTPOS, b, 0, S004.MRKDTLEN);
		this.mrktdt = b;
		b = new byte[S004.SYSDTLEN];
		System.arraycopy(src, S004.SYSDTSTARTPOS, b, 0, S004.SYSDTLEN);
		this.sysdt = b;
		b = new byte[S004.MRKTTMLEN];
		System.arraycopy(src, S004.MRKTTMSTARTPOS, b, 0, S004.MRKTTMLEN);
		this.mrkttm = b;
		b = new byte[S004.TYPELEN];
		System.arraycopy(src, S004.TYPESTARTPOS, b, 0, S004.TYPELEN);
		this.type = b;
		List<Float> cdList = null;
		for (int idx = 0; idx < ((src.length - (S004.OCSTARTPOS)) / S004.RARYLEN); idx++) {
			b = new byte[S004.RARYLEN];
			System.arraycopy(src, (S004.RARYLEN * idx) + S004.OCSTARTPOS, b, 0, S004.RARYLEN);
			byte[] fld = null;
//			System.out.println("idx=" + idx + " ocr[" + new String(b) + "]");
			fld = new byte[S004.CURCDLEN];
			System.arraycopy(b, S004.CURCDSTARTPOS, fld, 0, S004.CURCDLEN);
			this.curcd[idx] = fld;
			log.debug("idx=" + idx + " getCurcd=[" + new String(getCurcd(idx)) + "]");

			cdList = new ArrayList<Float>();
			cdList.add(new Float(new String(fld)));
			fld = new byte[S004.MRKTCDLEN];
			System.arraycopy(b, S004.MRKTCDSTARTPOS, fld, 0, S004.MRKTCDLEN);
			this.mrktcd[idx] = fld;
			cdList.add(new Float(new String(fld)));

			fld = new byte[S004.BRATSLEN];
			System.arraycopy(b, S004.BRATSSTARTPOS, fld, 0, S004.BRATSLEN);
			this.brats[idx] = fld;
			cdList.add(new Float(new String(fld)) / 100000.0f);

			fld = new byte[S004.SRATSLEN];
			System.arraycopy(b, S004.SRATSSTARTPOS, fld, 0, S004.SRATSLEN);
			this.srats[idx] = fld;
			cdList.add(new Float(new String(fld)) / 100000.0f);

			fld = new byte[S004.USCRATLEN];
			System.arraycopy(b, S004.USCRATSTARTPOS, fld, 0, S004.USCRATLEN);
			this.uscrat[idx] = fld;
			cdList.add(new Float(new String(fld)) / 100000.0f);

			fld = new byte[S004.RCNTLEN];
			System.arraycopy(b, S004.RCNTSTARTPOS, fld, 0, S004.RCNTLEN);
			this.rcnt[idx] = fld;
			cdList.add(new Float(new String(fld)) / 100000.0f);

			fld = new byte[S004.BRAT1LEN];
			System.arraycopy(b, S004.BRAT1STARTPOS, fld, 0, S004.BRAT1LEN);
			this.brat1[idx] = fld;
			cdList.add(new Float(new String(fld)) / 100000.0f);

			fld = new byte[S004.BRAT3LEN];
			System.arraycopy(b, S004.BRAT3STARTPOS, fld, 0, S004.BRAT3LEN);
			this.brat3[idx] = fld;
			cdList.add(new Float(new String(fld)) / 100000.0f);

			fld = new byte[S004.BRAT6LEN];
			System.arraycopy(b, S004.BRAT6STARTPOS, fld, 0, S004.BRAT6LEN);
			this.brat6[idx] = fld;
			cdList.add(new Float(new String(fld)) / 100000.0f);

			fld = new byte[S004.BRAT9LEN];
			System.arraycopy(b, S004.BRAT9STARTPOS, fld, 0, S004.BRAT9LEN);
			this.brat9[idx] = fld;
			cdList.add(new Float(new String(fld)) / 100000.0f);

			fld = new byte[S004.BRAT12LEN];
			System.arraycopy(b, S004.BRAT12STARTPOS, fld, 0, S004.BRAT12LEN);
			this.brat12[idx] = fld;
			cdList.add(new Float(new String(fld)) / 100000.0f);

			fld = new byte[S004.BRAT15LEN];
			System.arraycopy(b, S004.BRAT15STARTPOS, fld, 0, S004.BRAT15LEN);
			this.brat15[idx] = fld;
			cdList.add(new Float(new String(fld)) / 100000.0f);

			fld = new byte[S004.BRAT18LEN];
			System.arraycopy(b, S004.BRAT18STARTPOS, fld, 0, S004.BRAT18LEN);
			this.brat18[idx] = fld;
			cdList.add(new Float(new String(fld)) / 100000.0f);

			fld = new byte[S004.BRAT1YLEN];
			System.arraycopy(b, S004.BRAT1YSTARTPOS, fld, 0, S004.BRAT1YLEN);
			this.brat1y[idx] = fld;
			cdList.add(new Float(new String(fld)) / 100000.0f);

			fld = new byte[S004.BRAT2YLEN];
			System.arraycopy(b, S004.BRAT2YSTARTPOS, fld, 0, S004.BRAT2YLEN);
			this.brat2y[idx] = fld;
			cdList.add(new Float(new String(fld)) / 100000.0f);

			fld = new byte[S004.BRAT3YLEN];
			System.arraycopy(b, S004.BRAT3YSTARTPOS, fld, 0, S004.BRAT3YLEN);
			this.brat3y[idx] = fld;
			cdList.add(new Float(new String(fld)) / 100000.0f);

			fld = new byte[S004.SRAT1LEN];
			System.arraycopy(b, S004.SRAT1STARTPOS, fld, 0, S004.SRAT1LEN);
			this.srat1[idx] = fld;
			cdList.add(new Float(new String(fld)) / 100000.0f);

			fld = new byte[S004.SRAT3LEN];
			System.arraycopy(b, S004.SRAT3STARTPOS, fld, 0, S004.SRAT3LEN);
			this.srat3[idx] = fld;
			cdList.add(new Float(new String(fld)) / 100000.0f);

			fld = new byte[S004.SRAT6LEN];
			System.arraycopy(b, S004.SRAT6STARTPOS, fld, 0, S004.SRAT6LEN);
			this.srat6[idx] = fld;
			cdList.add(new Float(new String(fld)) / 100000.0f);

			fld = new byte[S004.SRAT9LEN];
			System.arraycopy(b, S004.SRAT9STARTPOS, fld, 0, S004.SRAT9LEN);
			this.srat9[idx] = fld;
			cdList.add(new Float(new String(fld)) / 100000.0f);

			fld = new byte[S004.SRAT12LEN];
			System.arraycopy(b, S004.SRAT12STARTPOS, fld, 0, S004.SRAT12LEN);
			this.srat12[idx] = fld;
			cdList.add(new Float(new String(fld)) / 100000.0f);

			fld = new byte[S004.SRAT15LEN];
			System.arraycopy(b, S004.SRAT15STARTPOS, fld, 0, S004.SRAT15LEN);
			this.srat15[idx] = fld;
			cdList.add(new Float(new String(fld)) / 100000.0f);

			fld = new byte[S004.SRAT18LEN];
			System.arraycopy(b, S004.SRAT18STARTPOS, fld, 0, S004.SRAT18LEN);
			this.srat18[idx] = fld;
			cdList.add(new Float(new String(fld)) / 100000.0f);

			fld = new byte[S004.SRAT1YLEN];
			System.arraycopy(b, S004.SRAT1YSTARTPOS, fld, 0, S004.SRAT1YLEN);
			this.srat1y[idx] = fld;
			cdList.add(new Float(new String(fld)) / 100000.0f);

			fld = new byte[S004.SRAT2YLEN];
			System.arraycopy(b, S004.SRAT2YSTARTPOS, fld, 0, S004.SRAT2YLEN);
			this.srat2y[idx] = fld;
			cdList.add(new Float(new String(fld)) / 100000.0f);

			fld = new byte[S004.SRAT3YLEN];
			System.arraycopy(b, S004.SRAT3YSTARTPOS, fld, 0, S004.SRAT3YLEN);
			this.srat3y[idx] = fld;
			cdList.add(new Float(new String(fld)) / 100000.0f);

			fld = new byte[S004.BCASHLEN];
			System.arraycopy(b, S004.BCASHSTARTPOS, fld, 0, S004.BCASHLEN);
			this.bcash[idx] = fld;
			cdList.add(new Float(new String(fld)) / 100000.0f);

			fld = new byte[S004.SCASHLEN];
			System.arraycopy(b, S004.SCASHSTARTPOS, fld, 0, S004.SCASHLEN);
			this.scash[idx] = fld;
			cdList.add(new Float(new String(fld)) / 100000.0f);

			fld = new byte[S004.FXRATLEN];
			System.arraycopy(b, S004.FXRATSTARTPOS, fld, 0, S004.FXRATLEN);
			this.fxrat[idx] = fld;
			cdList.add(new Float(new String(fld)) / 100000.0f);

			fld = new byte[S004.ACRATLEN];
			System.arraycopy(b, S004.ACRATSTARTPOS, fld, 0, S004.ACRATLEN);
			this.acrat[idx] = fld;
			cdList.add(new Float(new String(fld)) / 100000.0f);

			fld = new byte[S004.BLEN];
			System.arraycopy(b, S004.BSTARTPOS, fld, 0, S004.BLEN);
			this.b[idx] = fld;

			fld = new byte[S004.LMTBLEN];
			System.arraycopy(b, S004.LMTBSTARTPOS, fld, 0, S004.LMTBLEN);
			this.lmtb[idx] = fld;
			float tmpf = new Float(new String(fld)) / 100000.0f;
			if (new String(this.b[idx]).equalsIgnoreCase("-"))
				tmpf *= -1.0;
			cdList.add(tmpf);

			fld = new byte[S004.SLEN];
			System.arraycopy(b, S004.SSTARTPOS, fld, 0, S004.SLEN);
			this.s[idx] = fld;

			fld = new byte[S004.LMTSLEN];
			System.arraycopy(b, S004.LMTSSTARTPOS, fld, 0, S004.LMTSLEN);
			this.lmts[idx] = fld;
			tmpf = new Float(new String(fld)) / 100000.0f;
			if (new String(this.s[idx]).equalsIgnoreCase("-"))
				tmpf *= -1.0;
			cdList.add(tmpf);

			fld = new byte[S004.RLRATLEN];
			System.arraycopy(b, S004.RLRATSTARTPOS, fld, 0, S004.RLRATLEN);
			this.rlrat[idx] = fld;
			cdList.add(new Float(new String(fld)) / 100000.0f);

			fld = new byte[S004.CODELEN];
			System.arraycopy(b, S004.CODESTARTPOS, fld, 0, S004.CODELEN);
			this.code[idx] = fld;
			S004RecMap.add(cdList);
			/**/
			  log.debug(" getMrktcd=[" + new String(getMrktcd(idx)) + "] getBrats=[" + new
			  String(getBrats(idx)) + "] getSrats=[" + new String(getSrats(idx)) +
			  "] getUscrat=[" + new String(getUscrat(idx)) + "] getRcnt=[" + new
			  String(getRcnt(idx)) + "] getBrat1=[" + new String(getBrat1(idx)) +
			  "] getBrat3=[" + new String(getBrat3(idx)) + "] getBrat6=[" + new
			  String(getBrat6(idx)) + "] getBrat9=[" + new String(getBrat9(idx)) +
			  "] getBrat12=[" + new String(getBrat12(idx)) + "] getBrat15=[" + new
			  String(getBrat15(idx)) + "] getBrat18=[" + new String(getBrat18(idx)) +
			  "] getBrat1y=[" + new String(getBrat1y(idx)) + "] getBrat2y=[" + new
			  String(getBrat2y(idx)) + "] getBrat3y=[" + new String(getBrat3y(idx)) +
			  "] getSrat1=[" + new String(getSrat1(idx)) + "] getSrat3=[" + new
			  String(getSrat3(idx)) + "] getSrat6=[" + new String(getSrat6(idx)) +
			  "] getSrat9=[" + new String(getSrat9(idx)) + "] getSrat12=[" + new
			  String(getSrat12(idx)) + "] getSrat15=[" + new String(getSrat15(idx)) +
			  "] getSrat18=[" + new String(getSrat18(idx)) + "] getSrat1y=[" + new
			  String(getSrat1y(idx)) + "] getSrat2y=[" + new String(getSrat2y(idx)) +
			  "] getSrat3y=[" + new String(getSrat3y(idx)) + "] getBcash=[" + new
			  String(getBcash(idx)) + "] getScash=[" + new String(getScash(idx)) +
			  "] getFxrat=[" + new String(getFxrat(idx)) + "] getAcrat=[" + new
			  String(getAcrat(idx)) + "] getB=[" + new String(getB(idx)) + "] getLmtb=[" +
			  new String(getLmtb(idx)) + "] getS=[" + new String(getS(idx)) + "] getLmts=["
			  + new String(getLmts(idx)) + "] getRlrat=[" + new String(getRlrat(idx)) +
			  "] getCode=[" + new String(getCode(idx)) + "]");
			 /**/
			if (this.startiCurcd == 1) {
				System.arraycopy(mrktdt, 2, tmpb, 0, S004.PEN_XCERT1LEN);
//				sndStr = String.format("XS%s%s00R00530   AT00210000000%s0%-6s4   615", brno, wsno, new String(tmpb),
				sndStr = String.format("XS%s%s00R00530   AT00210000000%s0%-6s       ", brno, wsno, new String(tmpb),
						new String(mrkttm));
				this.RateRecList.add(sndStr);
				log.debug("pre len={} sndStr=[{}]", sndStr.length(), sndStr);
			}
			//20200606
			byte[] brats = adjustMantissa(getBrats(idx));
			byte[] bcash = adjustMantissa(getBcash(idx));
			byte[] srats = adjustMantissa(getSrats(idx));
			byte[] scash = adjustMantissa(getScash(idx));
//			int decimalno1 = getDecimal(getBrats(idx));
			int decimalno1 = getDecimal(brats);
			//----
			int mindecimalno = decimalno1;
			//20200606
//			int decimalno2 = getDecimal(getBcash(idx));
			int decimalno2 = getDecimal(bcash);
			//----
			if (mindecimalno == 0)
				mindecimalno = decimalno2;
			else if (mindecimalno > decimalno2)
				mindecimalno = decimalno2;
			//20200606
//			int decimalno3 = getDecimal(getSrats(idx));
			int decimalno3 = getDecimal(srats);
			//----
			if (mindecimalno == 0)
				mindecimalno = decimalno3;
			else if (mindecimalno > decimalno3)
				mindecimalno = decimalno3;
			//20200606
//			int decimalno4 = getDecimal(getScash(idx));
			int decimalno4 = getDecimal(scash);
			//----
			if (mindecimalno == 0)
				mindecimalno = decimalno4;
			else if (mindecimalno > decimalno4)
				mindecimalno = decimalno4;
		  log.debug(" mindecimalno=[{}] {} {} {} {}",mindecimalno, decimalno1,decimalno2, decimalno3, decimalno4);
				
			String strCurcd = new String(getCurcd(idx));
			int iCurcd = Integer.parseInt(strCurcd);
			String strColno = getStrColno(iCurcd);
			String strRowno = String.format("%02d", iCurcd - 1);

			//20100108 modify by MatsudairaSyume
			// for adjust display message decimal data
			//
			//20200606		
//			String parstr1 = parFieldValue(getBrats(idx));
//			String parstr2 = parFieldValue(getBcash(idx));
//			String parstr3 = parFieldValue(getSrats(idx));
//			String parstr4 = parFieldValue(getScash(idx));
			String parstr1 = parFieldValue(brats);
			String parstr2 = parFieldValue(bcash);
			String parstr3 = parFieldValue(srats);
			String parstr4 = parFieldValue(scash);
			//----
			int adjno1 = chkadjust(mindecimalno, decimalno1);
			parstr1 = adjustField(parstr1, adjno1);
			decimalno1 = decimalno1 - adjno1;
			int adjno2 = chkadjust(mindecimalno, decimalno2);
			parstr2 = adjustField(parstr2, adjno2);
			decimalno2 = decimalno2 - adjno2;
			int adjno3 = chkadjust(mindecimalno, decimalno3);
			parstr3 = adjustField(parstr3, adjno3);
			decimalno3 = decimalno3 - adjno3;
			int adjno4 = chkadjust(mindecimalno, decimalno4);
			parstr4 = adjustField(parstr4, adjno4);
			decimalno4 = decimalno4 - adjno4;
			sndStr = String.format("XS%s%s00R00610   A00028%s%s%s%s%s%s%s%s%s%s%s ", brno, wsno, strColno, strCurcd
			, strRowno, decimalno1, parstr1, decimalno2, parstr2,
			decimalno3, parstr3, decimalno4, parstr4);
			//----
			this.RateRecList.add(sndStr);
			log.debug("len={} sndStr=[{}]", sndStr.length(), sndStr);
			this.startiCurcd += 1;
		}
	}

	public List<String> getRateRecList() {
		return this.RateRecList;
	}

/*	public int getMapSize() { 
		int idx = 0;
		byte[] bary = null;
		byte[] tmpb = new byte[6];
		String sndStr = "";
		Iterator<List<Float>> itr = S004RecMap.iterator();
		int iCurcd = 1;
		while (itr.hasNext()) {
			idx = 0;
			List<Float> s = (List<Float>) itr.next();
			for (float ss : s) {
				if (idx == 0) {
					System.out.print(idx + " key= " + df3.format(ss) + " ");
				else if (idx == 30 || idx == 31)
					System.out.print(idx + " val= " + df1_5.format(ss) +	 " ");
				} else
					System.out.print(idx + " val= " + df3_5.format(ss) + " ");
				idx += 1;
			}
			System.out.println(""); b = null; iCurcd += 1;
		}
		return S004RecMap.size();
	 }*/

	public byte[] getMrktdt() {
		return mrktdt;
	}

	public byte[] getMrkttm() {
		return mrkttm;
	}

	public byte[] getSysdt() {
		return sysdt;
	}

	public byte[] getType() {
		return type;
	}

	public byte[] getCurcd(int idx) {
		return curcd[idx];
	}

	public byte[] getMrktcd(int idx) {
		return mrktcd[idx];
	}

	public byte[] getBrats(int idx) {
		return brats[idx];
	}

	public byte[] getSrats(int idx) {
		return srats[idx];
	}

	public byte[] getUscrat(int idx) {
		return uscrat[idx];
	}

	public byte[] getRcnt(int idx) {
		return rcnt[idx];
	}

	public byte[] getBrat1(int idx) {
		return brat1[idx];
	}

	public byte[] getBrat3(int idx) {
		return brat3[idx];
	}

	public byte[] getBrat6(int idx) {
		return brat6[idx];
	}

	public byte[] getBrat9(int idx) {
		return brat9[idx];
	}

	public byte[] getBrat12(int idx) {
		return brat12[idx];
	}

	public byte[] getBrat15(int idx) {
		return brat15[idx];
	}

	public byte[] getBrat18(int idx) {
		return brat18[idx];
	}

	public byte[] getBrat1y(int idx) {
		return brat1y[idx];
	}

	public byte[] getBrat2y(int idx) {
		return brat2y[idx];
	}

	public byte[] getSrat1(int idx) {
		return srat1[idx];
	}

	public byte[] getBrat3y(int idx) {
		return brat3y[idx];
	}

	public byte[] getSrat3(int idx) {
		return srat3[idx];
	}

	public byte[] getSrat6(int idx) {
		return srat6[idx];
	}

	public byte[] getSrat9(int idx) {
		return srat9[idx];
	}

	public byte[] getSrat12(int idx) {
		return srat12[idx];
	}

	public byte[] getSrat15(int idx) {
		return srat15[idx];
	}

	public byte[] getSrat18(int idx) {
		return srat18[idx];
	}

	public byte[] getSrat2y(int idx) {
		return srat2y[idx];
	}

	public byte[] getSrat1y(int idx) {
		return srat1y[idx];
	}

	public byte[] getSrat3y(int idx) {
		return srat3y[idx];
	}

	public byte[] getBcash(int idx) {
		return bcash[idx];
	}

	public byte[] getScash(int idx) {
		return scash[idx];
	}

	public byte[] getFxrat(int idx) {
		return fxrat[idx];
	}

	public byte[] getAcrat(int idx) {
		return acrat[idx];
	}

	public byte[] getB(int idx) {
		return b[idx];
	}

	public byte[] getLmtb(int idx) {
		return lmtb[idx];
	}

	public byte[] getS(int idx) {
		return s[idx];
	}

	public byte[] getLmts(int idx) {
		return lmts[idx];
	}

	public byte[] getRlrat(int idx) {
		return rlrat[idx];
	}

	public byte[] getCode(int idx) {
		return code[idx];
	}

	public byte[] getOc_bratedata(int idx) {
		return oc_bratedata[idx];
	}

}
