package com.cokepluscarbon.geoip.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import com.cokepluscarbon.geoip.IpUtils;
import com.cokepluscarbon.geoip.LimitTimePool;
import com.cokepluscarbon.geoip.Location;
import com.cokepluscarbon.geoip.LocationResolver;

public class GeoReader implements Runnable {
	private Socket socket;
	private BufferedReader reader;
	private ArrayBlockingQueue<Object> resultQueue;
	private AtomicInteger maxSize;
	private AtomicInteger curSize;

	public GeoReader(Socket socket, AtomicInteger maxSize, AtomicInteger curSize, ArrayBlockingQueue<Object> resultQueue) {
		this.socket = socket;
		this.maxSize = maxSize;
		this.curSize = curSize;
		this.resultQueue = resultQueue;
		try {
			this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		/**
		 * 非常混乱
		 */
		final LimitTimePool limitTimePool = new LimitTimePool(maxSize.get(), 1000);
		ExecutorService fixedThreadPool = Executors.newFixedThreadPool(maxSize.get());

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				if (!line.startsWith("LIST")) {
					final String[] ipRange = line.split(",");
					fixedThreadPool.execute(new Runnable() {
						@Override
						public void run() {
							curSize.addAndGet(1);

							try {
								long from = IpUtils.getLong(ipRange[0]);
								long to = IpUtils.getLong(ipRange[1]);

								System.out.println(String.format("[%s]分配并处理IP段 : [%s-%s]", Thread.currentThread()
										.getName(), ipRange[0], ipRange[1]));

								LocationResolver resolver = new LocationResolver();
								resolver.setConnectTimeout(2000);
								resolver.setReadTimeout(2000);
								Location currLocation = null;

								Location location = null;
								List<String> blockList = new ArrayList<String>();
								for (long i = from, currBlock = from; i <= to; i += 256) {
									String ip = IpUtils.getString(i);

									try {
										while (!limitTimePool.get()) {
											Thread.sleep(100);
										}
										location = resolver.getLocation(ip);
									} catch (Exception e) {
										System.out.println(e.getMessage());
									} finally {
										limitTimePool.release();
									}

									if (location != null) {
										System.out.println(location);
										if (currLocation == null) { // 第一个
											currLocation = location;
										} else {
											// 判断城市和isp是否相同，如果有其中一项不同，则入列
											if (!currLocation.getCity().equals(location.getCity())
													|| !currLocation.getIsp().equals(location.getIsp())) {

												String cityBlock = IpUtils.getString(currBlock) + ","
														+ IpUtils.getString(i - 1) + "," + currLocation.getCity() + ","
														+ currLocation.getRegion() + "," + currLocation.getArea() + ","
														+ currLocation.getIsp();
												currBlock = i;
												currLocation = location;

												blockList.add(cityBlock);
											}
										}

										/**
										 * TODO FIXME 补全最后的IP段,
										 * 如果IP的最后一段不是以0开始或和255结束则会产生问题,
										 * 例如：0.0.0.1-0.0.1.245,
										 * 是否可以使用to-i小于256代替?
										 * 
										 */
										if (i == (to - 255)) {
											String cityBlock = IpUtils.getString(currBlock) + ","
													+ IpUtils.getString(to) + "," + currLocation.getCity() + ","
													+ currLocation.getRegion() + "," + currLocation.getArea() + ","
													+ currLocation.getIsp();

											blockList.add(cityBlock);
										}
									}
								}

								System.out.println(String.format("[%s]处理IP段 [%s-%s]完毕,执行结果拆分为%d个连续的IP段.", Thread
										.currentThread().getName(), ipRange[0], ipRange[1], blockList.size()));

								Map<String, Object> result = new HashMap<String, Object>();
								result.put("block_name", ipRange[0] + "," + ipRange[1]);
								result.put("block_list", blockList);
								resultQueue.add(result);
							} catch (Exception e) {
								System.out.println(e.getMessage());
							} finally {
								curSize.decrementAndGet();
							}
						}
					});
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		this.reader.close();
		this.socket.close();
	}

}
