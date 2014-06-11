package com.cokepluscarbon.geoip.x;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.junit.Assert;
import org.junit.Test;

public class IpHelperTest {
	@Test
	public void loadDB_t1() throws Exception {
		IpHelper helper = new IpHelper();
		helper.loadDB("c://sort.txt");
	}

	@Test
	public void loadDB_t2() throws IOException {
		ZipFile zipFile = new ZipFile(new File("c://sort.zip"));
		ZipInputStream zis = new ZipInputStream(new FileInputStream(new File("c://sort.zip")));

		ZipEntry entry = zis.getNextEntry();

		BufferedReader br = new BufferedReader(new InputStreamReader(zipFile.getInputStream(entry)));

		String line = null;
		while ((line = br.readLine()) != null) {
			System.out.println(line);
		}
		zis.close();
		zipFile.close();
	}

	@Test
	public void find_t1() throws Exception {
		IpHelper helper = new IpHelper();
		helper.loadDB("c://sort.txt");

		System.out.println(helper.find("221.4.213.94"));
		System.out.println(helper.find("221.1.64.11"));
		System.out.println(helper.find("121.14.212.26"));
		System.out.println(helper.find("219.132.16.158"));
		System.out.println(helper.find("255.255.255.255"));
	}

	@Test
	public void find_t2() throws Exception {
		IpHelper helper = new IpHelper();
		helper.loadDB("c://sort.txt");

		Assert.assertEquals(null, helper.find("0.0.0.0"));
	}

	@Test
	public void find_t3() throws Exception {
		IpHelper helper = new IpHelper();
		helper.loadDB("c://sort.txt");

		Assert.assertEquals(null, helper.find("255.255.255.255"));
	}

	@Test
	public void find_t4() throws Exception {
		IpHelper helper = new IpHelper();
		helper.loadDB("c://sort.txt");

		String city = helper.find("221.4.213.96").split(",")[2].trim();
		Assert.assertEquals("珠海市", city);

		System.out.println(helper.find("221.4.213.96"));
	}

	@Test
	public void find_t5() throws Exception {
		IpHelper helper = new IpHelper();
		helper.loadDB("c://sort.txt");
	}

}
