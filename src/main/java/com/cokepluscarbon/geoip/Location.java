package com.cokepluscarbon.geoip;

import com.google.gson.annotations.SerializedName;

public class Location {
	@SerializedName("country")
	private String country;
	@SerializedName("country_id")
	private String countryId;
	@SerializedName("city")
	private String city;
	@SerializedName("city_id")
	private String cityId;
	@SerializedName("area")
	private String area;
	@SerializedName("area_id")
	private String areaId;
	@SerializedName("region")
	private String region;
	@SerializedName("region_id")
	private String regionId;
	@SerializedName("isp")
	private String isp;
	@SerializedName("isp_id")
	private String ispId;
	@SerializedName("ip")
	private String ip;

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCountryId() {
		return countryId;
	}

	public void setCountryId(String countryId) {
		this.countryId = countryId;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCityId() {
		return cityId;
	}

	public void setCityId(String cityId) {
		this.cityId = cityId;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getAreaId() {
		return areaId;
	}

	public void setAreaId(String areaId) {
		this.areaId = areaId;
	}

	public String getIsp() {
		return isp;
	}

	public void setIsp(String isp) {
		this.isp = isp;
	}

	public String getIspId() {
		return ispId;
	}

	public void setIspId(String ispId) {
		this.ispId = ispId;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getRegionId() {
		return regionId;
	}

	public void setRegionId(String regionId) {
		this.regionId = regionId;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	@Override
	public String toString() {
		return "Location [country=" + country + ", countryId=" + countryId + ", city=" + city + ", cityId=" + cityId
				+ ", area=" + area + ", areaId=" + areaId + ", region=" + region + ", regionId=" + regionId + ", isp="
				+ isp + ", ispId=" + ispId + ", ip=" + ip + "]";
	}

}
