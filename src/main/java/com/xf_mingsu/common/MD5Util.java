package com.xf_mingsu.common;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {

	private static final String Md5Key = "MD51357";

	private static String hexDigits[] = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e",
			"f" };

	private MD5Util() {
	}

	/**
	 * byte数组MD5加密
	 * @param b
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static String md5(byte b[]) throws NoSuchAlgorithmException {
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		md5.update(b, 0, b.length);
		return byteArrayToHexString(md5.digest());
	}

	/**
	 * String类型MD5加密
	 * @param data
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 */
	public static String getMD5(String data) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		byte b[] = data.getBytes("UTF-8");
		md5.update(b, 0, b.length);
		return byteArrayToHexString(md5.digest());
	}

	/**
	 * md5两次加密
	 *
	 * @param str
	 * @return
	 */
	public static String getSecondMD5(String str)  {
		str += Md5Key;
		String firstStr = null;
		try {
			firstStr = getMD5(str);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		firstStr += Md5Key;
		String second = null;
		try {
			second = getMD5(firstStr);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return second;
	}

	private static String byteArrayToHexString(byte b[]) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < b.length; i++)
			sb.append(byteToHexString(b[i]));

		return sb.toString();
	}

	private static String byteToHexString(byte b) {
		int n = b;
		if (n < 0)
			n = 256 + n;
		int d1 = n / 16;
		int d2 = n % 16;
		return hexDigits[d1] + hexDigits[d2];
	}

	public static void main(String[] args) {
		try {
			String secondMD5 = getSecondMD5("123456");
			System.out.println("1111111:"+secondMD5);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String getMd5Value(String sSecret) {
		try {
			MessageDigest bmd5 = MessageDigest.getInstance("MD5");
			bmd5.update(sSecret.getBytes());
			int i;
			StringBuffer buf = new StringBuffer();
			byte[] b = bmd5.digest();
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			return buf.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 生成登陆token
	 *
	 * @param accountNumber
	 * @return
	 */
	public static String getToken(String accountNumber) throws Exception {
		return MD5Util.getMD5(accountNumber + "jkln23888000587unma" + System.currentTimeMillis());
	}


}
