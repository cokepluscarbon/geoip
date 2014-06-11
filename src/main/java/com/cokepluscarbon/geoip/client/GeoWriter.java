package com.cokepluscarbon.geoip.client;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.cokepluscarbon.geoip.IpUtils;

public class GeoWriter implements Runnable {
	private Socket socket;
	private BufferedWriter writer;
	private AtomicInteger maxSize;
	private AtomicInteger curSize;
	private ArrayBlockingQueue<Object> resultQueue;

	public GeoWriter(Socket socket, AtomicInteger maxSize, AtomicInteger curSize, ArrayBlockingQueue<Object> resultQueue) {
		this.socket = socket;
		this.maxSize = maxSize;
		this.curSize = curSize;
		this.resultQueue = resultQueue;
		try {
			this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
			this.writer.write("GeoClient\n");
			this.writer.flush();
		} catch (IOException e) {
			throw new RuntimeException();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		while (true) {

			/**
			 * 判断是否需要向服务器获取IP段
			 */
			synchronized (curSize) {
				if (curSize.get() < maxSize.get()) {
					try {
						writer.write("GET#" + (maxSize.get() - curSize.get()) + "\n");
						writer.flush();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

			/**
			 * 发送统计结果
			 */
			while (resultQueue.size() != 0) {
				Map<String, Object> blockResult = (Map<String, Object>) resultQueue.poll();
				if (blockResult != null) {
					String blockName = (String) blockResult.get("block_name");
					List<String> blockList = (List<String>) blockResult.get("block_list");

					try {
						String[] block = blockName.split(",");
						String header = String.format("POST,%s,%s,%d,%d\n", block[0], block[1], blockList.size(),
								IpUtils.getLong(block[1]) - IpUtils.getLong(block[0]));

						System.out.print(header);
						writer.write(header);
						for (String tmp : blockList) {
							System.out.println(tmp);
							writer.write(tmp + "\n");
						}
						writer.flush();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

			/**
			 * 休眠1s
			 */
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		writer.close();
		socket.close();
	}

}
