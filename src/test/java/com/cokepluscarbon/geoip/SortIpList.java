package com.cokepluscarbon.geoip;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.RuntimeErrorException;

import org.junit.Test;

public class SortIpList {
	@Test
	public void t() throws IOException {
		File file = new File("c://ip.txt");
		BufferedReader reader = new BufferedReader(new FileReader(file));

		List<String> result = new ArrayList<String>();
		String line = null;
		while ((line = reader.readLine()) != null) {
			if (!line.startsWith("POST")) {
				result.add(line);
			}
		}

		result.sort(new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				try {
					long l1 = IpUtils.getLong(o1.split(",")[0]);
					long l2 = IpUtils.getLong(o2.split(",")[0]);
					if (l1 > l2) {
						return 1;
					}
					if (l1 < l2) {
						return -1;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return 0;
			}
		});

		BufferedWriter writer = new BufferedWriter(new FileWriter(new File("c://sort.txt")));

		for (String s : result) {
			writer.write(s + "\n");
		}
		writer.flush();
		writer.close();
	}

	@Test
	public void t2() throws IOException {
		Map<String, String> countryMap = getCountryMap();

		File target = new File("c://all.csv");
		target.createNewFile();
		BufferedWriter writer = new BufferedWriter(new FileWriter(target));

		List<File> files = getFiles();
		for (File file : files) {
			String code = file.getName().split("\\.")[0];

			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = null;
			if (countryMap.containsKey(code)) {
				while ((line = br.readLine()) != null) {
					String[] arr = line.split(",");
					if (arr.length == 5) {
						writer.write(arr[0] + "," + arr[1] + "," + arr[2] + ",,,,," + countryMap.get(code) + ","
								+ arr[4] + "\n");
					} else if (arr.length == 4) {
						writer.write(arr[0] + "," + arr[1] + "," + arr[2] + ",,,,," + countryMap.get(code) + ",,"
								+ "\n");
					}
				}
			} else {
				while ((line = br.readLine()) != null) {
					String[] arr = line.split(",");
					writer.write(arr[0] + "," + arr[1] + "," + arr[2] + ",,,,,,,," + "," + arr[4] + "\n");
				}
			}

			br.close();
		}

		writer.close();
	}

	public static Map<String, String> getCountryMap() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("c://countries.txt"));

		String line = null;
		Map<String, String> countryMap = new HashMap<String, String>();
		while ((line = br.readLine()) != null) {
			String[] arr = line.split("\t");
			countryMap.put(arr[2], arr[1] + "," + arr[0] + "," + arr[2]);
		}
		br.close();

		return countryMap;
	}

	public static List<File> getFiles() {
		File file = new File("c://ctlist");
		if (file.isDirectory()) {
			return Arrays.asList(file.listFiles());
		}
		return null;
	}

	@Test
	public void t3() throws Exception {
		BufferedReader br = new BufferedReader(new FileReader("c://sort.txt"));
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File("c://sort.tr.txt")));

		Map<String, String> cityMap = getCityMap();
		for (String c : cityMap.values()) {
			System.out.println(c);
		}

		String line = null;
		while ((line = br.readLine()) != null) {
			String[] arr = line.split(",");
			if (arr.length < 4) {
				continue;
			}
			if (cityMap.containsKey(arr[2])) {
				if (arr.length == 4) {

				} else if (arr.length == 6) {
					String[] city = cityMap.get(arr[2]).split(",");
					String format = String.format("%s,%s,%d,%s,%s,%s,%s,%s,%s,%s,%s\n", new Object[] { arr[0], arr[1],
							IpUtils.getLong(arr[1]) - IpUtils.getLong(arr[0]), arr[2], city[0], city[1], city[2], "中国",
							"China", "CN", city[3], arr[5] });
					bw.append(format);
				}
			}
		}

		br.close();
		bw.close();
	}

	public static Map<String, String> getCityMap() throws IOException {
		Map<String, String> cityMap = new HashMap<String, String>();

		BufferedReader br = new BufferedReader(new FileReader("c://city-province.csv"));
		String line = null;
		while ((line = br.readLine()) != null) {
			String[] arr = line.split(",");
			if (arr.length == 4) {
				cityMap.put(arr[1], arr[1] + "," + arr[0] + "," + arr[3] + "," + arr[2]);
			}
		}

		br.close();

		return cityMap;
	}
}
