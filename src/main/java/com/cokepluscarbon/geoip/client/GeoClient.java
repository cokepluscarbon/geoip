package com.cokepluscarbon.geoip.client;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class GeoClient {
	/**
	 * URL ConnnectTimeout ReadTimeout
	 * 
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		String host = "localhost";
		int port = 10086;
		int maxThread = 5;

		Map<String, String> params = resolveParams(args);
		if (params.containsKey("host")) {
			host = params.get("host");
		}
		if (params.containsKey("port")) {
			port = Integer.parseInt(params.get("port"));
		}
		if (params.containsKey("maxThread")) {
			maxThread = Integer.parseInt(params.get("maxThread"));
		}

		ArrayBlockingQueue<Object> resultQueue = new ArrayBlockingQueue<Object>(100);
		Socket socket = new Socket(host, port);
		socket.setKeepAlive(true);

		AtomicInteger maxSize = new AtomicInteger(maxThread);
		AtomicInteger curSize = new AtomicInteger(0);

		// writer
		new Thread(new GeoWriter(socket, maxSize, curSize, resultQueue)).start();

		// reader
		new Thread(new GeoReader(socket, maxSize, curSize, resultQueue)).start();

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
}
