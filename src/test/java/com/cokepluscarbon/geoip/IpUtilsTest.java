package com.cokepluscarbon.geoip;

import junit.framework.Assert;

import org.junit.Test;

import com.cokepluscarbon.geoip.IpUtils;

public class IpUtilsTest {
	@Test
	public void t01_getLong() throws Exception {
		String ip = "0.0.0.0";
		long rs = IpUtils.getLong(ip);

		Assert.assertEquals(0, rs);
	}

	@Test
	public void t02_getLong() throws Exception {
		String ip = "255.255.255.255";
		long rs = IpUtils.getLong(ip);

		Assert.assertEquals(2L * Integer.MAX_VALUE + 1, rs);
	}

	@Test(expected = NumberFormatException.class)
	public void t03_getLong() throws Exception {
		String ip = "255.255.255.256";
		IpUtils.getLong(ip);
	}

	@Test(expected = NumberFormatException.class)
	public void t04_getLong() throws Exception {
		String ip = "255.255.255";
		IpUtils.getLong(ip);
	}

	@Test(expected = NumberFormatException.class)
	public void t05_getLong() throws Exception {
		String ip = "255.255.255.255.255";
		IpUtils.getLong(ip);
	}

	@Test(expected = Exception.class)
	public void t06_getLong() throws Exception {
		String ip = "tiger.coke.255.255";
		IpUtils.getLong(ip);
	}

	@Test
	public void getString_01() {
		long ip = 0;
		System.out.println(IpUtils.getString(ip));
	}

	@Test
	public void getString_02() {
		long ip = 2L * Integer.MAX_VALUE + 1;

		String ipString = IpUtils.getString(ip);
		Assert.assertEquals("255.255.255.255", ipString);
	}

}
