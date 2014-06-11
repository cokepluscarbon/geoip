package com.cokepluscarbon.geoip.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.Date;
import java.util.concurrent.BlockingQueue;

public class ServerHandler implements Runnable {
	private BlockingQueue<String> ipQueue;
	private Socket socket;
	private BufferedWriter accomplishWriter;

	public ServerHandler(BlockingQueue<String> ipQueue, Socket socket, BufferedWriter accomplishWriter) {
		this.ipQueue = ipQueue;
		this.socket = socket;
		this.accomplishWriter = accomplishWriter;
	}

	@Override
	public void run() {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
			String line = reader.readLine();

			/**
			 * 判断是否是真实客户端,如果不是则立即返回 TODO 是否需要互相验证？
			 */
			if (line.equals("GeoClient")) {
				System.out.println(String.format("A geo client were accepted at : %s and ip its %s", new Date(),
						socket.getRemoteSocketAddress()));
			} else {
				socket.close();
				return;
			}
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("GET")) {
					handleGet(line, writer);
				} else if (line.startsWith("POST")) {
					handlePost(line, reader);
				}
			}
		} catch (SocketException e) {
			System.out.println("A client were closed at : " + new Date());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	private void handleGet(String line, BufferedWriter writer) {
		try {
			int getSize = Integer.parseInt(line.split("#")[1]);
			writer.append("LIST#" + getSize + "\n");
			for (int i = 0; i < getSize; i++) {
				if (ipQueue.size() > 0) {
					writer.append(ipQueue.poll() + "\n");
				}
			}
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void handlePost(String line, BufferedReader reader) {
		System.out.println(line);
		String[] header = line.split(",");
		// String range = header[1];

		int size = Integer.parseInt(header[3]);

		// 写到文件rs.csv 和 accept.csv
		recordIntoFile(line, reader, size, accomplishWriter);
	}

	/**
	 * TODO 有安全隐患，写的非常混乱
	 * 
	 * @param line
	 * @param reader
	 * @param size
	 */
	public static void recordIntoFile(String line, BufferedReader reader, int size, BufferedWriter accomplishWriter) {
		try {
			accomplishWriter.write(line + "\n");
			System.out.println(line);
		} catch (IOException e) {
			e.printStackTrace();
		}

		String tmpLine = null;
		for (int i = 0; i < size; i++) {
			try {
				tmpLine = reader.readLine();
				System.out.println(tmpLine);
				accomplishWriter.write(tmpLine + "\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		try {
			accomplishWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
