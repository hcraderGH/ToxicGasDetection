package com.devcheng.toxicgasdetection;

/**
 * Created by DevCheng on 2017/8/30.
 */

public class CommonUtils {

	public static String subZeroAndDot(double data){
		String s=String.valueOf(data);
		if(String.valueOf(s).indexOf(".") > 0){
			s = s.replaceAll("0+?$", "");//去掉多余的0
			s = s.replaceAll("[.]$", "");//如最后一位是.则去掉
		}
		return s;
	}

	/**
	 * 获取经度
	 * @param result
	 * @return
	 */
	public static double getLongitude(byte[] result){
		return ConvertUtil.setDecimalPointCount2(ConvertUtil.getFloat(new byte[]{result[3],result[4],result[5],result[6]}),6);
	}


	/**
	 * 获取纬度
	 * @param result
	 * @return
	 */
	public static double getLatitude(byte[] result){
		return ConvertUtil.setDecimalPointCount2(ConvertUtil.getFloat(new byte[]{result[7],result[8],result[9],result[10]}),6);
	}


	/**
	 * 获取二氧化碳
	 * @param result
	 * @return
	 */
	public static double getCarbonDioxide(byte[] result){
		return (result[12]-48)*10000+(result[13]-48)*1000+(result[14]-48)*100
				+(result[15]-48)*10+(result[16]-48)*10;
	}


	/**
	 * 获取氧气
	 * @param result
	 * @return
	 */
	public static double getOxygen(byte[] result){
		return (result[18]*256+result[19])/10.0;
	}

	/**
	 * 获取一氧化碳
	 * @param result
	 * @return
	 */
	public static double getCarbonMonoxide(byte[] result){
		return ConvertUtil.setDecimalPointCount2((result[21]*256+result[22])*0.1,1);
	}

	/**
	 * 获取二氧化硫
	 * @param result
	 * @return
	 */
	public static double getSulfurDioxide(byte[] result){
		return result[24]*256+result[25];
	}

	/**
	 * 获取硫化氢
	 * @param result
	 * @return
	 */
	public static double getHydrothion(byte[] result){
		return result[27]*256+result[28];
	}

	/**
	 * 获取甲烷
	 * @param result
	 * @return
	 */
	public static double getMethane(byte[] result){
		return result[29]*256+result[30];
	}

	/**
	 * 获取空气质量
	 * @param result
	 * @return
	 */
	public static double getAirQuality(byte[] result){
		return (ConvertUtil.byte2unsignedInt(result[32])+ ConvertUtil.byte2unsignedInt(result[33])/100)/1000.0;
	}

	/**
	 * 获取温度
	 * @param result
	 * @return
	 */
	public static double getTemperature(byte[] result){
		return ConvertUtil.byte2unsignedInt(result[34])+ ConvertUtil.byte2unsignedInt(result[35])/100.0;
	}

	/**
	 * 获取湿度
	 * @param result
	 * @return
	 */
	public static double getHumidity(byte[] result){
		return ConvertUtil.byte2unsignedInt(result[37]);
	}

}
