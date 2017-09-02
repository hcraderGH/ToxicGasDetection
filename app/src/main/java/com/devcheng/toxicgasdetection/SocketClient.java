package com.devcheng.toxicgasdetection;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by DevCheng on 2017/8/30.
 */

public class SocketClient {

	//服务器地址
	private  String SERVER_HOST_IP = "192.168.0.128";
	//服务器端口
	private  int SERVER_HOST_PORT = 8990;

	private Socket mSocket;
	private DataOutputStream out = null;
	private DataInputStream in = null;
	private List<Byte> mResultList =new ArrayList<>();
	private byte[] mResult;
	private Context mContext;

	private int mDataCount;

	private byte[] mConfigCmd;

	private static final String TAG = "测试SocketService";


	public SocketClient(byte[] configCmd,String ip,int port,Context context) {
		this.SERVER_HOST_IP=ip;
		this.SERVER_HOST_PORT=port;
		this.mConfigCmd = configCmd;
		this.mContext=context;
		new Thread(new Runnable() {
			@Override
			public void run() {
				getResultFromServe();
			}
		}).start();
	}

	public SocketClient(String ip,int port){
		this.SERVER_HOST_IP=ip;
		this.SERVER_HOST_PORT=port;
		Log.i(TAG, "SocketClient: ip="+ip+"  port="+port);
	}

	public Boolean isServerClose(){
		try{
			//连接服务器
			mSocket = new Socket();
			mSocket.connect(new InetSocketAddress(SERVER_HOST_IP, SERVER_HOST_PORT), 3000);
			mSocket.sendUrgentData(0xFF);//发送1个字节的紧急数据，默认情况下，服务器端没有开启紧急数据处理，不影响正常通信
			return false;
		}catch(IOException se){
			Log.i(TAG, "isServerClose: 进到到异常中");
			return true;
		}
	}

	/**
	 * 获取接受到的数据Byte数组
	 */
	private void getResultFromServe() {

		try {
			//连接服务器
			if (mSocket==null){
				mSocket = new Socket();
			}
			mSocket.connect(new InetSocketAddress(SERVER_HOST_IP, SERVER_HOST_PORT), 3000);

			out = new DataOutputStream(mSocket.getOutputStream());
			out.write(mConfigCmd);
			out.flush();
			// 一定要加上这句，否则收不到来自服务器端的消息返回
//			mSocket.shutdownOutput();

			//获取服务器端的响应
			while (true) {
				in = new DataInputStream(mSocket.getInputStream());

				if (in.available()>0){
					mDataCount=0;

					while (in.available() > 0) {
						byte b=in.readByte();
						mResultList.add(b);
						Log.e(TAG, "getResultFromServe:" + b);
					}

					if (mResultList.size()==39) {
						mResult=new byte[mResultList.size()];
						for (int i = 0; i < mResultList.size(); i++) {
							mResult[i]=mResultList.get(i);
						}

						byte crc=CheckSumCrc8(mResult,mResult.length-1);
//					Log.i(TAG, "getResultFromServe: size="+mResultList.size());
//					Log.i(TAG, "getResultFromServe: byte[]size="+mResult.length);
//					Log.i(TAG, "getResultFromServe: crc="+crc);
						if (crc!=mResult[mResult.length-1]){
							//TODO CRC误码处理方案
						}
					}else{
						//TODO 数据长度错误处理方法
					}

					//清空List否则下一个数据将会继续添加进去
					mResultList.clear();
					sendResult(mResult);
				}else {
					mDataCount++;
					if (mDataCount==4&&mResult!=null){//当超时则表示断开连接
						mResult=new byte[]{0x00};
						sendResult(mResult);
					}
				}


				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 * 广播传递接受到的数据
	 */
	private void sendResult(byte[] result){
		Intent intent=new Intent();
		intent.putExtra(Const.EXTRA_RESULT,result);
		intent.setAction(Const.BROADCAST_RESULT);
		mContext.sendBroadcast(intent);
	}


	/**
	 * 校验接受到的数据
	 * @param buffer 接受到的整个数据
	 * @param len 接收到的除CRC位的剩余数据长度
	 * @return CRC
	 */
	private byte CheckSumCrc8(byte[] buffer, int len) {
		byte crc = 0;
		byte[] ch = new byte[8];
		byte ch1, i, j, k = 0;
		crc = (byte) 0xff;
		for (i = 0; i < len; i++)
		{
			ch1 = buffer[i];
			for (j = 0; j < 8; j++)
			{
				ch[j] = (byte)(ch1 & 0x01);
				ch1 >>= 1;
			}
			for (k = 0; k < 8; k++)
			{
				ch[7 - k] <<= 7;
				if (((crc ^ ch[7 - k]) & 0x80) == 0x80)
				{
					crc = (byte)((crc << 1) ^ 0x1d);
				}
				else
				{
					crc <<= 1;
				}
			}

		}
		crc ^= 0xff;
		return crc;
	}


	/**
	 * 关闭输入流和输出流和Socket
	 */
	public void disconnect() {
		try {
			if (in != null) {
				in.close();
			}
			if (out != null) {
				out.close();
			}
			if (mSocket != null) {
				mSocket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
