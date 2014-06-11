package com.cokepluscarbon.geoip.x;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.cokepluscarbon.geoip.IpUtils;

public class IpHelper {
	private List<String[]> records = new ArrayList<String[]>();

	public void loadDB(String filepath) throws Exception {
		File file = new File(filepath);
		BufferedReader reader = new BufferedReader(new FileReader(file));

		String line = null;
		long count = 0;
		String[] arr = null;
		while ((line = reader.readLine()) != null) {
			arr = line.split(",");
			records.add(arr);
			count += IpUtils.getLong(arr[1]) - IpUtils.getLong(arr[0]);
		}
		reader.close();

		System.out.println(String.format("加载IP数据完毕,共加载%d条记录,共%d个IP.", records.size(), count));
	}

	/**
	 * 暂时采用二分查找
	 * 
	 * @param ip
	 * @return 地理位置信息
	 * @throws Exception
	 */
	public String find(String ip) throws Exception {
		long num = IpUtils.getLong(ip);

		if (num >= IpUtils.getLong("225.255.255.255") || num <= 0) {
			return null;
		}

		String[] target = null;

		int left = 0;
		int right = records.size() - 1;
		long start = System.currentTimeMillis();
		while (left < right) {
			int middle = (left + right) / 2;
			long misp = IpUtils.getLong(records.get(middle)[0]);
			// System.out.println(records.get(middle)[0] + "->" +
			// records.get(middle)[1]);
			if (misp > num) {
				right = middle;
			} else if (misp < num) {
				if (IpUtils.getLong(records.get(middle)[1]) < num) {
					left = middle;
				} else {
					target = records.get(middle);
					break;
				}
			} else if (misp == num) {
				target = records.get(middle);
				break;
			}
		}
		long end = System.currentTimeMillis();
		System.out.println("查询时间s: " + start + "ms");
		System.out.println("查询时间e: " + end + "ms");

		return Arrays.toString(target);
	}
}
