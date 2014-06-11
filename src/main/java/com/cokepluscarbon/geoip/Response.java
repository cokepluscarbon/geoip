package com.cokepluscarbon.geoip;

import com.google.gson.annotations.SerializedName;

public class Response {
	@SerializedName("code")
	private int code;
	@SerializedName("data")
	private Location location;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	@Override
	public String toString() {
		return "Response [code=" + code + ", location=" + location + "]";
	}

}
