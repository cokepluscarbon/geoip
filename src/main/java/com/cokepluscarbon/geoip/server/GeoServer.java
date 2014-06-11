package com.cokepluscarbon.geoip.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class GeoServer {
	private int port = 10086;
	private String from = "c://cn.csv";
	private String to = "c://accomplish.csv";
	private BlockingQueue<String> ipQueue;
	private ServerSocket serverSocket;

	public static void main(String[] args) throws IOException {
		new GeoServer(resolveParams(args)).start();
	}

	public GeoServer(Map<String, String> params) {
		if (params.containsKey("port")) {
			this.port = Integer.parseInt(params.get("port"));
		}
		if (params.containsKey("from")) {
			this.from = params.get("from");
		}
		if (params.containsKey("to")) {
			this.to = params.get("to");
		}
		ipQueue = getIpQueue(this.from, this.to);
	}

	public void start() throws IOException {
		serverSocket = new ServerSocket(this.port);

		System.out.println(String.format("Server[port:%d] start at : %s", this.port, new Date()));

		BufferedWriter accomplishWriter = new BufferedWriter(new FileWriter(new File(this.to), true));
		while (true) {
			Socket clientSocket = serverSocket.accept();
			new Thread(new ServerHandler(ipQueue, clientSocket, accomplishWriter)).start();
		}
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		serverSocket.close();
		System.out.println("Server closed at : " + new Date());
	}

	public static Map<String, String> resolveParams(String[] args) {
		Map<String, String> params = new HashMap<String, String>();
		for (String arg : args) {
			String[] splits = arg.split("=");
			if (splits.length != 2) {
				throw new RuntimeException(String.format("参数对[%s]有误！", arg));
			} else {
				params.put(splits[0], splits[1]);
			}
		}

		return params;
	}

	/**
	 * 
	 * @param blockPath
	 *            IP段地址列表文件
	 * @param accomplishPath
	 *            已经处理的IP段地址列表文件
	 * @return
	 */
	public static BlockingQueue<String> getIpQueue(String blockPath, String accomplishPath) {
		List<String> blockList = loadBlockList(blockPath);
		List<String> accomplishList = loadAccomplishList(accomplishPath);

		BlockingQueue<String> ipQueue = new LinkedBlockingQueue<String>();

		String block = null;
		String[] splits = null;
		long count = 0;
		for (String item : blockList) {
			splits = item.split(",");
			block = splits[0] + "," + splits[1];
			if (!accomplishList.contains(block)) {
				ipQueue.add(item);
				count += Long.parseLong(splits[2]);
			}
		}

		System.out.println(String.format("需要处理的IP段%d项,共有IP%d个.", ipQueue.size(), count));
		

		return ipQueue;
	}

	/**
	 * 加载需要处理的IP段列表
	 * 
	 * @param filePath
	 * @return
	 */
	public static List<String> loadBlockList(String filePath) {
		File file = new File(filePath);
		List<String> accomplish = new ArrayList<String>();

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));

			long count = 0;
			String line = null;
			String[] splits = null;
			while ((line = reader.readLine()) != null) {
				splits = line.split(",");
				if (splits.length >= 3) {
					accomplish.add(line);
					count += Long.parseLong(splits[2]);
				}
			}
			System.out.println(String.format("加载IP段列表文件[%s]完毕,共有IP段%d项,IP地址%d个.", filePath, accomplish.size(), count));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(String.format("IP段地址列表文件[%s]不存在!", filePath));
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return accomplish;
	}

	/**
	 * 加载已经完成的IP段列表
	 * 
	 * @param filePath
	 * @return
	 */
	public static List<String> loadAccomplishList(String filePath) {
		File file = new File(filePath);
		if (!file.exists()) {
			System.out.println(String.format("完成列表文件[%s]不存在,创建文件[%s].", filePath, filePath));
			try {
				if (file.createNewFile()) {
					System.out.println(String.format("创建完成列表文件[%s]成功.", filePath));
				} else {
					throw new RuntimeException(String.format("创建完成列表文件[%s]失败.", filePath));
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		List<String> accomplishList = new ArrayList<String>();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));

			long blockCount = 0;
			long ipCount = 0;
			String line = null;
			String[] splits = null;
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("POST")) {
					splits = line.split(",");
					accomplishList.add(splits[1] + "," + splits[2]);
					blockCount += Long.parseLong(splits[3]);
					ipCount += Long.parseLong(splits[4]);
				}
			}
			System.out.println(String.format("加载完成列表文件[%s]完毕,共有IP段%d项,可以拆分为%d个连续IP段,共计%d个IP地址.", filePath,
					accomplishList.size(), blockCount, ipCount));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(String.format("IP段完成地址列表文件[%s]不存在!", filePath));
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return accomplishList;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

}
