package com.systex.sysgateii.util;

import javax.xml.bind.DatatypeConverter;

public class DataConvert {
	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

	public static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			// unsigned right shift operator ">>>"
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

	public static String toHexString(byte[] array) {
		return DatatypeConverter.printHexBinary(array);
	}

	public static String toHexStringWithSpace(byte[] array) {
		String _rtnStr = "";
		byte[] _a = new byte[1];
		for (int i = 0; i < array.length; i++) {
			_a[0] = array[i];
			_rtnStr += (DatatypeConverter.printHexBinary(_a) + (i == (array.length - 1) ? "" : " "));
		}
		_a = null;
		return _rtnStr;
	}

	public static byte[] toByteArray(String s) {
		return DatatypeConverter.parseHexBinary(s);
	}

	/**
	 * 將 int 數值轉成 byte array
	 * 
	 * @param value int 數直
	 * @return byte array
	 */
	// byte[] bytes = ByteBuffer.allocate(4).putInt(1695609641).array();
	// final will prevent the method from being hidden by subclasses
	public static final byte[] intToByteArray(int value) {
		return new byte[] { (byte) (value >>> 24), (byte) (value >>> 16), (byte) (value >>> 8), (byte) value };
	}

	public static int byteArrayToInt(byte[] buf, boolean isBigEndian) {
		if (isBigEndian) {
			return java.nio.ByteBuffer.wrap(buf).getInt();
		} else {
			return java.nio.ByteBuffer.wrap(buf).order(java.nio.ByteOrder.LITTLE_ENDIAN).getInt();
		}
	}

	/**
	 * 
	 * @param b   要設定的 byte
	 * @param pos 要設定的位元
	 * @param op  0=清除, 1=設定, 2=反向
	 * @return
	 */
	public static byte setBit(byte b, int pos, int op) {
		if (op == 0) {
			return (byte) (b & ~(1 << pos));
		} else if (op == 1) {
			return (byte) (b | (1 << pos));
		} else {
			return (byte) (b ^ (1 << pos));
		}
	}

	public static void main(String[] args) {
		toByteArray("0102030405060708090A0B0C0D0F0102030405060708090A0B0C0D0F01020304");

		byte[] b = intToByteArray(1);
		System.out.println(b.length);
		System.out.println(byteArrayToInt(intToByteArray(1), true));
	}
}
