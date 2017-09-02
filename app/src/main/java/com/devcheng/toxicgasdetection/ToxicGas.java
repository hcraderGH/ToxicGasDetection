package com.devcheng.toxicgasdetection;

/**
 * Created by DevCheng on 2017/8/29.
 */

public class ToxicGas {

	private double Latitude;//纬度
	private double longitude;//经度
	private String address;//详细位置
	private double carbonMonoxide;//一氧化碳
	private double carbonDioxide;//二氧化碳
	private double oxygen;//氧气
	private double hydrothion;//硫化氢
	private double methane;//甲烷
	private double sulfurDioxide;//二氧化硫
	private double AirQuality;//空气质量
	private double temperature;//温度
	private double humidity;//湿度


	public ToxicGas(double latitude, double longitude, double carbonMonoxide,
	                double carbonDioxide, double oxygen,
	                double hydrothion, double methane, double sulfurDioxide,
	                double airQuality, double temperature, double humidity) {
		this.Latitude = latitude;
		this.longitude = longitude;
		this.carbonMonoxide = carbonMonoxide;
		this.carbonDioxide = carbonDioxide;
		this.oxygen = oxygen;
		this.hydrothion = hydrothion;
		this.methane = methane;
		this.sulfurDioxide = sulfurDioxide;
		AirQuality = airQuality;
		this.temperature = temperature;
		this.humidity = humidity;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public double getLatitude() {
		return Latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public double getCarbonMonoxide() {
		return carbonMonoxide;
	}

	public double getCarbonDioxide() {
		return carbonDioxide;
	}

	public double getOxygen() {
		return oxygen;
	}

	public double getHydrothion() {
		return hydrothion;
	}

	public double getMethane() {
		return methane;
	}

	public double getSulfurDioxide() {
		return sulfurDioxide;
	}

	public double getAirQuality() {
		return AirQuality;
	}

	public double getTemperature() {
		return temperature;
	}

	public double getHumidity() {
		return humidity;
	}

	public void setLatitude(double latitude) {
		this.Latitude = latitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public void setCarbonMonoxide(double carbonMonoxide) {
		this.carbonMonoxide = carbonMonoxide;
	}

	public void setCarbonDioxide(double carbonDioxide) {
		this.carbonDioxide = carbonDioxide;
	}

	public void setOxygen(double oxygen) {
		this.oxygen = oxygen;
	}

	public void setHydrothion(double hydrothion) {
		this.hydrothion = hydrothion;
	}

	public void setMethane(double methane) {
		this.methane = methane;
	}

	public void setSulfurDioxide(double sulfurDioxide) {
		this.sulfurDioxide = sulfurDioxide;
	}

	public void setAirQuality(double airQuality) {
		AirQuality = airQuality;
	}

	public void setTemperature(double temperature) {
		this.temperature = temperature;
	}

	public void setHumidity(double humidity) {
		this.humidity = humidity;
	}

	@Override
	public String toString() {
		return "ToxicGas{" +
				"Latitude=" + Latitude +
				", longitude=" + longitude +
				", address='" + address + '\'' +
				", carbonMonoxide=" + carbonMonoxide +
				", carbonDioxide=" + carbonDioxide +
				", oxygen=" + oxygen +
				", hydrothion=" + hydrothion +
				", methane=" + methane +
				", sulfurDioxide=" + sulfurDioxide +
				", AirQuality=" + AirQuality +
				", temperature=" + temperature +
				", humidity=" + humidity +
				'}';
	}
}
