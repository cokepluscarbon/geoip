package com.cokepluscarbon.geoip;

import java.io.IOException;

import org.junit.Test;

import com.cokepluscarbon.geoip.Location;
import com.cokepluscarbon.geoip.LocationResolver;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public class LocationResolverTest {
	@Test
	public void getLocation_01() throws JsonSyntaxException, JsonIOException, IOException {
		LocationResolver locationResolver = new LocationResolver();
		locationResolver.setConnectTimeout(2000);
		locationResolver.setReadTimeout(2000);

		System.out.println(locationResolver.getLocation("221.4.213.94"));
		System.out.println(locationResolver.getLocation("221.4.2.94"));
		System.out.println(locationResolver.getLocation("221.4.13.94"));
		System.out.println(locationResolver.getLocation("221.4.3.94"));
		System.out.println(locationResolver.getLocation("22.4.213.94"));
		System.out.println(locationResolver.getLocation("21.4.23.94"));
	}
}
