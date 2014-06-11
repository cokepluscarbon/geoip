package com.cokepluscarbon.geoip;

public class IpUtils {

	/**
	 * @param IPv4地址
	 * @return IP地址的Long表示形式
	 * @throws Exception
	 */
	public static long getLong(String ip) throws Exception {
		String[] ipblocks = ip.split("\\.");
		if (ipblocks.length == 4) {
			try {
				long rs = (Long.parseLong(ipblocks[0]) << 24)
						+ (Long.parseLong(ipblocks[1]) << 16)
						+ (Long.parseLong(ipblocks[2]) << 8)
						+ Long.parseLong(ipblocks[3]);
				if (rs > (2L * Integer.MAX_VALUE + 1) || rs < 0) {
					throw new NumberFormatException(String.format(
							"IPv4地址[%s]格式不正确！", ip));
				}

				return rs;
			} catch (Exception e) {
				throw new NumberFormatException(String.format(
						"IPv4地址[%s]格式不正确！", ip));
			}
		} else {
			throw new NumberFormatException(String.format("IPv4地址[%s]格式不正确！",
					ip));
		}
	}

	/**
	 * 
	 * @param IPv4地址的整数表示形式
	 * @return IPv4地址
	 */
	public static String getString(long ip) {
		if (ip > (2L * Integer.MAX_VALUE + 1) || ip < 0) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 3; i >= 0; i--) {
			sb.append(((ip >>> (i * 8)) & 0xff) + ".");
		}

		sb.setLength(sb.length() - 1);
		return sb.toString();
	}
}
