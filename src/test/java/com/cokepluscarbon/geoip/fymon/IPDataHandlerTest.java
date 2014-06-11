package com.cokepluscarbon.geoip.fymon;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.google.gson.Gson;

public class IPDataHandlerTest {
	@Test
	public void resolveIPList_01() {
		String rs = IPDataHandler.findGeography("221.4.213.94");
		System.out.println(rs);
	}

	@Test
	public void resolveLocation_01() throws IOException {
		List<String> ipList = getIpList();

		File file = new File("C://20140528ChinaLocation.txt");
		if (!file.exists()) {
			file.createNewFile();
		}
		FileWriter writer = new FileWriter(file);
		for (String ip : ipList) {
			writer.append(IPDataHandler.findGeography(ip) + "\n");
			writer.flush();
		}
		writer.close();
	}

	public static List<String> getIpList() throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(new File("C://20140528China.txt")));

		List<String> ipList = new ArrayList<String>();

		String line = null;
		while ((line = reader.readLine()) != null) {
			ipList.add(line.substring(1, line.length() - 1));
		}
		reader.close();

		return ipList;
	}

	@Test
	public void citytoJson() throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(new File("C://1.txt")));

		List<Object[]> cityList = new ArrayList<Object[]>();
		String line = null;
		String tmp[] = null;
		Object[] city = null;
		while ((line = reader.readLine()) != null) {
			tmp = line.split("\\b( )\\b");
			if (tmp.length == 2) {
				city = new Object[2];
				city[0] = tmp[1];
				city[1] = Long.parseLong(tmp[0].trim());
				cityList.add(city);
			}
		}

		System.out.println(cityList.size());
		reader.close();

		String json = new Gson().toJson(cityList);
		System.out.println(json);

	}

	public void cityDiff() throws FileNotFoundException {
		BufferedReader reader = new BufferedReader(new FileReader(new File("C://1.txt")));

	}

	private String[] LEVEL_1_CITY = { "北京", "上海", "广州", "深圳", "天津" };
	private String[] LEVEL_2_CITY = { "杭州", "" };
	private String[] LEVEL_3_CITY = {};
}
