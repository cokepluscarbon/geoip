package com.cokepluscarbon.geoip;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;

public class MainTest {
	@Test
	public void t1() {
		String[] sources = { "221.4.0.0,221.4.255.255", "222.83.0.0,222.83.127.255", "157.122.0.0,157.122.255.255",
				"125.76.128.0,125.76.255.255", "61.177.0.0,61.177.255.255", "27.192.0.0,27.223.255.255",
				"36.51.0.0,36.51.255.255", "42.51.0.0,42.51.255.255", "58.83.0.0,58.83.255.255",
				"60.166.0.0,60.167.255.255", "61.144.0.0,61.147.255.255", "61.178.0.0,61.178.255.255",
				"110.236.0.0,110.237.255.255" };

		final LimitTimePool limitTimePool = new LimitTimePool(5, 1000);
		ExecutorService fixedThreadPool = Executors.newFixedThreadPool(5);
		for (final String source : sources) {
			fixedThreadPool.execute(new Runnable() {
				@Override
				public void run() {
					try {
						String[] ipRange = source.split(",");
						long from = IpUtils.getLong(ipRange[0]);
						long to = IpUtils.getLong(ipRange[1]);
						LocationResolver resolver = new LocationResolver();
						resolver.setConnectTimeout(10000);
						resolver.setReadTimeout(5000);
						String currCity = "NULL";
						for (long i = from, currBlock = from; i <= to; i += 256) {
							String ip = IpUtils.getString(i);

							limitTimePool.get();
							Location location = resolver.getLocation(ip);
							limitTimePool.release();

							if (location != null) {
								if (!location.getCity().equals(currCity)) {
									if (currCity.equals("NULL")) {
										currCity = location.getCity();
									} else {
										System.out.println(IpUtils.getString(currBlock) + "-"
												+ IpUtils.getString(i - 1) + "-" + currCity);
										currBlock = i;
										currCity = location.getCity();
									}
								}
							}
						}
					} catch (Exception e) {
						System.out.println(e.getMessage());
					}
					System.out.println(String.format("-------------[%s]------------", source));

				}
			});

		}

		while (true) {
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	@Test
	public void t2() throws IOException {
		BufferedReader cityReader = new BufferedReader(new FileReader(new File("c://city.txt")));
		BufferedReader provinceReader = new BufferedReader(new FileReader(new File("c://province.txt")));

		List<Map<String, String>> cityList = new ArrayList<Map<String, String>>();
		Map<String, String> proviceMap = new HashMap<String, String>();

		String line = null;
		while ((line = cityReader.readLine()) != null) {
			String[] tmp = line.split(",");
			Map<String, String> city = new HashMap<String, String>();
			city.put("city_en", tmp[0]);
			city.put("city_zh", tmp[1]);
			city.put("province_en", tmp[2]);
			cityList.add(city);
		}

		while ((line = provinceReader.readLine()) != null) {
			String[] tmp = line.split(",");
			proviceMap.put(tmp[0], tmp[1]);
		}

		for (Map<String, String> city : cityList) {
			String proviceEn = city.get("province_en");
			if (proviceMap.get(proviceEn) != null) {
				city.put("province_zh", proviceMap.get(proviceEn));
			}
		}

		cityReader.close();
		provinceReader.close();

		BufferedWriter writer = new BufferedWriter(new FileWriter(new File("c://city-province.csv")));
		for (Map<String, String> city : cityList) {
			String cityLine = String.format("%s,%s,%s,%s", city.get("city_en"), city.get("city_zh"),
					city.get("province_en"), city.get("province_zh"));
			System.out.println(cityLine);
			writer.append(cityLine + "\n");
		}
		writer.close();
	}
}
