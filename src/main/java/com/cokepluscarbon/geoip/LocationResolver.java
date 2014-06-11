package com.cokepluscarbon.geoip;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public class LocationResolver {
	public static String requestUrl = "http://ip.taobao.com/service/getIpInfo.php?ip=%s";
	private int connectTimeout = 1000;
	private int readTimeout = 1000;

	public Location getLocation(String ip) {
		try {
			return tryLocation(ip, 1);
		} catch (JsonSyntaxException | JsonIOException e) {
			// 不处理
		} catch (IOException e) {
			try {
				return tryLocation(ip, 2);
			} catch (JsonSyntaxException | JsonIOException | IOException e1) {
				System.out.println(e1.getMessage());
				// 不处理
			}
		}

		return null;
	}

	private Location tryLocation(String ip, int multi) throws JsonSyntaxException, JsonIOException, IOException {
		Location location = null;
		URL url = new URL(String.format(requestUrl, ip));
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setReadTimeout(this.getReadTimeout() * multi);
		conn.setConnectTimeout(this.getConnectTimeout() * multi);

		if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
			Response response = new Gson().fromJson(new InputStreamReader(conn.getInputStream()), Response.class);
			if (response.getCode() == 0) {
				location = response.getLocation();
			}
		} else {
			System.out.println(conn.getResponseMessage());
		}

		return location;
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	/**
	 * @param connectTimeout
	 *            default is 1000
	 */
	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public int getReadTimeout() {
		return readTimeout;
	}

	/**
	 * 
	 * @param readTimeout
	 *            default is 1000
	 */
	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}

}
