package com.devcheng.toxicgasdetection;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.SubscriptSpan;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * Created by DevCheng on 2017/8/30.
 */

public class Test {

	String s="fa fb 01 8c 9d ea 42 2d cd fe 41 01 35 32 34 30 30 01 25 55 01 36 25 01 12 23 01 25 36 25 78 01 12 28 35 30 01 16 a2";

	public static void main(String[] args) throws UnsupportedEncodingException {
//		String s="42013b17";
//		Float value = Float.intBitsToFloat(Integer.valueOf(s.trim(),16));
//		System.out.println(value);
//
//		Float f=32.3077063f;
//		System.out.println(Integer.toHexString(Float.floatToIntBits(f)));
//
//		String s1= Arrays.toString(new byte[]{0x01, (byte) 0xff});
//		System.out.println(s1);
//
//		System.out.println(byte2float(new byte[]{(byte) 0x8c, (byte) 0x9d, (byte) 0xea,0x42},0));
//
//
//		System.out.println(byteArrayToInt(new byte[]{0x01,0x02,0x01,0x01}));
//		System.out.println(getCO2(new byte[]{0x01,0x02,0x01,0x01,0x01}));

		SpannableString span=new SpannableString("CHC");
		span.setSpan(new SubscriptSpan(),2,3, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
		System.out.println(span);

	}

	/**
	 * 字节转换为浮点
	 *
	 * @param b 字节（至少4个字节）
	 * @param index 开始位置
	 * @return
	 */
	public static float byte2float(byte[] b, int index) {
		int l;
		l = b[index + 0];
		l &= 0xff;
		l |= ((long) b[index + 1] << 8);
		l &= 0xffff;
		l |= ((long) b[index + 2] << 16);
		l &= 0xffffff;
		l |= ((long) b[index + 3] << 24);
		return Float.intBitsToFloat(l);
	}

	/**
	 * byte[]转int
	 * @param bytes
	 * @return
	 */
	public static int byteArrayToInt(byte[] bytes) {
		int value= 0;
		//由高位到低位
		for (int i = 0; i < 4; i++) {
			int shift= (4 - 1 - i) * 8;
			value +=(bytes[i] & 0x000000FF) << shift;//往高位游
		}
		return value;
	}


	public static double getCO2(byte[] a){
		return ((a[0]-0x30)*10000+(a[1]-0x30)*1000+(a[2]-0x30)*100+(a[3]-0x30)*10+(a[4]-0x30))*10;

	}

}
