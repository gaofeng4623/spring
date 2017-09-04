// Decompiled by Jad v1.5.7g. Copyright 2000 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/SiliconValley/Bridge/8617/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi 
// Source File Name:   GUID.java

package com.spring.mvc.common;

import java.net.InetAddress;

public class GUID {
	private static byte ip[];
	static int counter = 0;
	byte guts[];

	public static synchronized byte[] nextGUID() {
		try {
			byte ip[] = InetAddress.getLocalHost().getAddress();
			counter++;
			byte guid[] = new byte[16];
			for (int i = 0; i < 4; i++)
				guid[i] = ip[i];

			byte timeAry[] = long2bytes(System.currentTimeMillis());
			for (int i = 4; i < 12; i++)
				guid[i] = timeAry[i - 4];

			byte counterAry[] = int2bytes(counter);
			for (int i = 12; i < 16; i++)
				guid[i] = counterAry[i - 12];

			return guid;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public GUID() {
		guts = nextGUID();
	}

	public GUID(byte guts[]) throws IllegalArgumentException {
		if (guts == null || guts.length != 16) {
			throw new IllegalArgumentException("格式错误，必须传入16字节长的树组");
		} else {
			this.guts = guts;
			return;
		}
	}

	public boolean equals(Object obj) {
		if (obj instanceof GUID) {
			if (this != obj) {
				for (int i = 0; i < 16; i++)
					if (((GUID) obj).guts[i] != guts[i])
						return false;

			}
			return true;
		} else {
			return false;
		}
	}

	public String toString() {
		StringBuffer str = new StringBuffer();
		StringBuffer sb = toStringBuffer();
		str.append("{");
		str.append(sb.substring(0, 8));
		str.append("-");
		str.append(sb.substring(8, 12));
		str.append("-");
		str.append(sb.substring(12, 16));
		str.append("-");
		str.append(sb.substring(16, 20));
		str.append("-");
		str.append(sb.substring(20, 32));
		str.append("}");
		return (new String(str)).toUpperCase();
	}

	private StringBuffer toStringBuffer() {
		StringBuffer str = new StringBuffer();
		byte ip[] = new byte[4];
		for (int i = 0; i < 4; i++)
			ip[i] = guts[i];

		String s = Integer.toHexString(bytes2int(ip));
		int ii = 8 - s.length();
		for (int i = 0; i < ii; i++)
			s = "0" + s;

		str.append(s);
		byte time[] = new byte[8];
		for (int i = 4; i < 12; i++)
			time[i - 4] = guts[i];

		s = Long.toHexString(bytes2long(time));
		ii = 16 - s.length();
		for (int i = 0; i < ii; i++)
			s = "0" + s;

		str.append(s);
		byte count[] = new byte[4];
		for (int i = 12; i < 16; i++)
			count[i - 12] = guts[i];

		s = Integer.toHexString(bytes2int(count));
		ii = 8 - s.length();
		for (int i = 0; i < ii; i++)
			s = "0" + s;

		str.append(s);
		return str;
	}

	public byte[] getData() {
		return guts;
	}

	private static synchronized byte[] long2bytes(long lParam) {
		byte byteAry[] = new byte[8];
		for (int i = 0; i < 8; i++)
			byteAry[i] = (byte) (int) (lParam >> (7 - i) * 8);

		return byteAry;
	}

	private static synchronized byte[] int2bytes(int iParam) {
		byte byteAry[] = new byte[4];
		for (int i = 0; i < 4; i++)
			byteAry[i] = (byte) (iParam >> (3 - i) * 8);

		return byteAry;
	}

	private static synchronized long bytes2long(byte byteAry[]) {
		if (byteAry == null || byteAry.length != 8)
			return 0L;
		long l = 0L;
		for (int i = 0; i < byteAry.length; i++)
			l += byteAry[i] << (7 - i) * 8;

		return l;
	}

	private static synchronized int bytes2int(byte byteAry[]) {
		if (byteAry == null || byteAry.length != 4)
			return 0;
		int ii = 0;
		for (int i = 0; i < byteAry.length; i++)
			ii += byteAry[i] << (3 - i) * 8;

		return ii;
	}

	public String getArchiveFormTableName() {
		String tableName = "ARC_FILE_"
				+ (new String(toStringBuffer())).toUpperCase().substring(11);
		return tableName;
	}


	public static byte[] getIp() {
		return ip;
	}

	public static void setIp(byte[] ip) {
		GUID.ip = ip;
	}

}
