package cn.air.doopen.socket.uploadingimg;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import cn.air.doopen.utli.MyLog;

/**
 * 上传文件到服务器类
 * 
 * @author tom
 */

public class UploadUtil {
	private static final String TAG = "uploadFile";
	private static final int TIME_OUT = 100* 1000; // 超时时间
	private static final String CHARSET = "utf-8"; // 设置编码

	/**
	 * android上传文件到服务器
	 * @param file
	 *            需要上传的文件
	 * @param RequestURL
	 *            请求的rul
	 * @return 返回响应的内容
	 */
	public static String uploadFile(String filepath, String filename, String RequestURL) {
		String result = null;
		String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成
		String PREFIX = "--", LINE_END = "\r\n";
		String CONTENT_TYPE = "multipart/form-data"; // 内容类型
		try {
			URL url = new URL(RequestURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(TIME_OUT);
			conn.setConnectTimeout(TIME_OUT);
			conn.setDoInput(true); // 允许输入流
			conn.setDoOutput(true); // 允许输出流
			conn.setUseCaches(false); // 不允许使用缓存
			conn.setRequestMethod("POST"); // 请求方式
			conn.setRequestProperty("Charset", CHARSET); // 设置编码
			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary="
					+ BOUNDARY);
			if (filepath != null) {
				/**
				 * 当文件不为空，把文件包装并且上传
				 */
				DataOutputStream dos = new DataOutputStream(
						conn.getOutputStream());
				// ///////////////////
				dos.writeBytes(PREFIX + BOUNDARY + LINE_END);
				dos.writeBytes("Content-Disposition: form-data; "
						+ "name=\"file\"; filename=\"" + filename + "\"" + LINE_END);
				dos.writeBytes(LINE_END);
				// ///////////////////
				/* 取得文件的FileInputStream */
				InputStream fStream = new PictureUtil().bitmapToString(filepath);
				/* 设置每次写入1024bytes */
				int bufferSize = 1024;
				byte[] buffer = new byte[bufferSize];
				int length = -1;
				/* 从文件读取数据至缓冲区 */
				while ((length = fStream.read(buffer)) != -1) {
					/* 将资料写入DataOutputStream中 */
					dos.write(buffer, 0, length);
				}
				dos.writeBytes(LINE_END);

				dos.writeBytes(PREFIX + BOUNDARY + PREFIX + LINE_END);
				/* close streams */
				fStream.close();
				dos.flush();
				/**
				 * 获取响应码 200=成功 当响应成功，获取响应的流
				 */
				int res = conn.getResponseCode();
				MyLog.e(TAG, "response code:" + res);
				if (res == 200) {
					MyLog.i(TAG, "request success");
					InputStream input = conn.getInputStream();
					StringBuffer sb1 = new StringBuffer();
					int ss;
					while ((ss = input.read()) != -1) {
						sb1.append((char) ss);
					}
					result = sb1.toString();
					MyLog.e(TAG, "result : " + result);
				} else {
					MyLog.e(TAG, "request error");
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static InputStream compressImage(String file) {
		Bitmap bitmap = BitmapFactory.decodeFile(file);
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds设为true
		newOpts.inJustDecodeBounds = true;
		bitmap = BitmapFactory.decodeFile(file, newOpts);
		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		// 设置分辨率
		float hh = 800f;
		float ww = 400f;
		// 缩放比。由于是固定的比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;
		if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0) {
			be = 1;
		}
		newOpts.inSampleSize = be;// 设置缩放比例
		// 重新读入图片，注意此时已经把newOpts.inJustDecodeBounds = false
		bitmap = BitmapFactory.decodeFile(file, newOpts);
		try {
			//			Bitmap bitmapyas=compressImage1(bitmap);
			return compressImage2(bitmap);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public static InputStream compressImage2(Bitmap bitmap) throws Exception {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
			int options = 100;
			int size = baos.toByteArray().length / 1024;
			while (size > 40 && options > 0) {
				baos.reset();// 重置baos即清空baos
				options -=10;// 每次都减少10
				// 这里压缩options%，把压缩后的数据存放到baos中
				bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
				size = baos.toByteArray().length / 1024;
			}
			// 把压缩后的数据baos存放到ByteArrayInputStream中
			ByteArrayInputStream isBm = new ByteArrayInputStream(
					baos.toByteArray());
			return isBm;

		} catch (Exception e) {
			throw e;
		}
	}
}