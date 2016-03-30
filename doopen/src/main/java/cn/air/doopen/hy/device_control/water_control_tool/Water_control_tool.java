package cn.air.doopen.hy.device_control.water_control_tool;

import java.nio.ByteBuffer;

import android.widget.ToggleButton;
import cn.air.doopen.socket.TcpCommSerivce;
import cn.air.doopen.utli.MyLog;
/**水立方净水器和旭力康净水器的控制代码；/*按照协议*/
public class Water_control_tool {
	public static int index = 0;
	// 发起控制；
	public static void opendevwash(int len,byte ord, ToggleButton water_wash_true_false_togg, int clientID,
			TcpCommSerivce.MyBinder myBinder) {
		MyLog.i("index=="+index);
		int wash = 0;
		if (water_wash_true_false_togg.isChecked() == true) {
			wash = 1;
		} else {
			wash = 0;
		}
		ByteBuffer buf = ByteBuffer.allocate(256);
		if(++index==4){
			buf.put((byte) 1); // 消息长度 -6
			buf.put((byte) 0x03); // 指令
			buf.putInt((int) 0x01); // 发送者n
			index=0;
		}else{
			buf.put((byte) len); // 消息长度 -6
			buf.put((byte) 0x03); // 指令
			buf.putInt((int) 0x01); // 发送者n
		}
		buf.putInt((int) clientID);// 接收者
		buf.putShort((short) 0x00);// 设备返回状态；
		buf.putShort((short) 0x0900);// 长度；
		buf.putShort((short) 0x0400);// 控制字0x0004低位在前；；
		buf.put((byte) ord);// 控制指令
		buf.put((byte) wash);// 控制对象；
		buf.put((byte) 0);// 复位滤芯；
		buf.putInt((int) 0);// 滤芯设定值；
		myBinder.send(buf);
	}
	// 发起控制；
	public static void opendevwash(byte ord, ToggleButton water_wash_true_false_togg, int clientID,
			TcpCommSerivce.MyBinder myBinder) {
		int wash = 0;
		if (water_wash_true_false_togg.isChecked() == true) {
			wash = 1;
		} else {
			wash = 0;
		}
		ByteBuffer buf = ByteBuffer.allocate(256);
		buf.put((byte) 23); // 消息长度 -6
		buf.put((byte) 0x03); // 指令
		buf.putInt((int) 0x01); // 发送者n
		buf.putInt((int) clientID);// 接收者
		buf.putShort((short) 0x00);// 设备返回状态；
		buf.putShort((short) 0x0900);// 长度；
		buf.putShort((short) 0x0400);// 控制字0x0004低位在前；；
		buf.put((byte) ord);// 控制指令
		buf.put((byte) wash);// 控制对象；
		buf.put((byte) 0);// 复位滤芯；
		buf.putInt((int) 0);// 滤芯设定值；
		myBinder.send(buf);
	}
	// 发起控制；
	public static void opendevwash(int ord, boolean water_wash_true_false_togg, int clientID,
			TcpCommSerivce.MyBinder myBinder) {
		int wash = 0;
		if (water_wash_true_false_togg == true) {
			wash = 0;
		} else {
			wash = 1;
		}
		ByteBuffer buf = ByteBuffer.allocate(256);
		buf.put((byte) 23); // 消息长度 -6
		buf.put((byte) 0x03); // 指令
		buf.putInt((int) 0x01); // 发送者n
		buf.putInt((int) clientID);// 接收者
		buf.putShort((short) 0x00);// 设备返回状态；
		buf.putShort((short) 0x0900);// 长度；
		buf.putShort((short) 0x0400);// 控制字0x0004低位在前；；
		buf.put((byte) ord);// 控制指令
		buf.put((byte) wash);// 控制对象；
		buf.put((byte) 0);// 复位滤芯；
		buf.putInt((int) 0);// 滤芯设定值；
		myBinder.send(buf);
	}
	// 复位滤芯
	public static void opendevwashfu(byte zu, int clientID,int sheding,TcpCommSerivce.MyBinder myBinder) {
		ByteBuffer buf = ByteBuffer.allocate(256);
		buf.put((byte) 23); // 消息长度 -6
		buf.put((byte) 0x03); // 指令
		buf.putInt((int) 0x01); // 发送者n
		buf.putInt((int) clientID);// 接收者
		buf.putShort((short) 0x00);// 设备返回状态；
		buf.putShort((short)0x0900);// 长度；
		buf.putShort((short) 0x0400);// 控制字0x0004低位在前；；
		buf.put((byte) 4);// 控制指令
		buf.put((byte) 0);// 控制对象；
		buf.put((byte) zu);// 复位滤芯；
		buf.putInt((int) 0);// 滤芯设定值；
		myBinder.send(buf);
	}

	// 重发；
	public static void get2stats(final int index, final short cmd, final int clientID,
			final TcpCommSerivce.MyBinder myBinder) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				boolean cb = sendstatus((short) index, clientID, myBinder);
				if (cb == true) {
					if (cmd == index) {
						sendstatus((short) (index + 1), clientID, myBinder);
						if (cmd == index + 1) {
							sendstatus((short) (index + 1 + 1), clientID, myBinder);
							if (cmd == index + 1 + 1) {
								return;
							} else {
								try {
									Thread.sleep(3000);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
								sendstatus((short) (index + 1 + 1), clientID, myBinder);
							}
						} else {
							try {
								Thread.sleep(3000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							sendstatus((short) (index + 1), clientID, myBinder);

						}
					} else {
						try {
							Thread.sleep(3000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						boolean db = sendstatus((short) index, clientID, myBinder);
						if (db == true) {
							if (cmd == index) {
								sendstatus((short) (index + 1), clientID, myBinder);
							} else {
							}
						}

					}
				}
			}
		}).start();
	}

	// 返回状态；
	public static void returndata(int clientID, byte acmdL, byte cmdH, TcpCommSerivce.MyBinder myBinder) {
		ByteBuffer buf = ByteBuffer.allocate(256);
		buf.put((byte) 0x10); // 消息长度 -6
		buf.put((byte) 0x03); // 指令
		buf.putInt((int) 0x01); // 发送者n
		buf.putInt((int) clientID);// 接收者
		buf.putShort((short) 0x00);// 返回状态嘛；
		buf.putShort((short) 0x0200);/// 长度；
		buf.putShort((short) ((short) (acmdL << 8) | cmdH));// 指令字；
		myBinder.send(buf);
	}

	// 两个字节转换成int类型；防止溢出；而出现符号-；
	public static int getTransform(byte H, byte L) {
		int value = L & 0x000000ff;
		value = (int) (((H << 8) | value) & 0x0000ffff);// 纯水值；
		return value;
	}

	/// 获取设备状态；
	public static boolean sendstatus(Short stare, int clientID, TcpCommSerivce.MyBinder myBinder) {
		ByteBuffer buf1 = ByteBuffer.allocate(256);
		buf1.put((byte) 17); // 消息长度 -6
		buf1.put((byte) 0x03); // 指令
		buf1.putInt((int) 0x01); // 发送者n
		buf1.putInt((int) clientID);// 接收者
		buf1.putShort((short) 0x00);// 设备返回状态；
		buf1.putShort((short) 0x0300);// 长度；
		buf1.putShort((short) (0x0 + stare + 00 << 8));// 控制字0x0004低位在前；；
		buf1.put((byte) 01);//
		boolean code = myBinder.send(buf1);
		MyLog.i("获取了设备", "获取了设备状态" + stare);
		return code;
	}
}
