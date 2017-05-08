package com.cinlan.xview.utils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;

import com.cinlan.jni.ImRequest;
import com.cinlankeji.khb.iphone.R;


/**
 * ͨ�õķ���
 */
public class Utils {

	public static String getAppRootFile() {
		String EXTERNAL_DIR = null;
		if (Utils.isExternalStorageValid()) {
			EXTERNAL_DIR = Environment.getExternalStorageDirectory()
					.getAbsolutePath() + File.separator;
		} else {
			EXTERNAL_DIR = Environment.getDataDirectory() + File.separator;
		}

		return EXTERNAL_DIR;
	}

	public static void showQuitDialog(String content, final Context context) {
		final com.cinlan.xview.widget.AlertDialog d = new com.cinlan.xview.widget.AlertDialog(
				context)
				.builder()
				.setTitle("")
				.setMsg(context.getResources().getString(
						R.string.this_operation_will_exit_applcation_xviewsdk))
				.setPositiveButton(
						context.getResources().getString(R.string.sure_xviewsdk),
						new OnClickListener() {
							@Override
							public void onClick(View v) {
								ProgressDialog proDialog = android.app.ProgressDialog.show(
										context,
										"",
										context.getResources().getString(
												R.string.logingout_xviewsdk));
								proDialog.show();
								ImRequest.getInstance().logout();
							}
						})
				.setNegativeButton(
						context.getResources().getString(R.string.cancel_xviewsdk),
						new OnClickListener() {
							@Override
							public void onClick(View v) {

							}
						});
		d.show();
	}

	public static int getRandomID() {
		Random mrandom = new Random();
		int nextInt = mrandom.nextInt(65535);
		return nextInt;
	}

	public static void SetPlayoutSpeaker(boolean loudspeakerOn, Context context) {
		// create audio manager if needed
		AudioManager audioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		if (loudspeakerOn) {
			audioManager.setSpeakerphoneOn(true);
			audioManager.setMode(AudioManager.MODE_IN_CALL);
		} else {
			audioManager.setSpeakerphoneOn(false);// �ر�������
			audioManager.setRouting(AudioManager.MODE_NORMAL,
					AudioManager.ROUTE_EARPIECE, AudioManager.ROUTE_ALL);
			// �������趨��Earpiece����Ͳ���������趨Ϊ����ͨ����
			audioManager.setMode(AudioManager.MODE_IN_CALL);
		}

	}

	public static boolean isMobileNO(String mobiles) {
		Pattern p = Pattern
				.compile("^((13[0-9])|(15[^4,\\D])|(18[0,1,5-9]))\\d{8}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}

	public static boolean CheckTelephone(Context context, String value) {
		if (value != null) {
			if (value.matches("\\d{4}-\\d{7,8}|\\d{3}-\\d{7,8}|\\d{0,20}")) {
				return true;
			} else {
				return false;
			}

		} else {
			return false;
		}
	}

	public static String getSimNumber(Context context) {
		// �����绰����
		// TelephonyManager tm =
		// (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		// return "15303030303";

		SharedPreferences preferences = context.getSharedPreferences("config",
				Context.MODE_PRIVATE);
		String string = preferences.getString("gpssim", "110");

		return string;
	}

	//
	static public int[] decodeYUV420SP(byte[] yuv420sp, int width, int height) {
		int[] rgb = new int[width * height];
		final int frameSize = width * height;
		for (int j = 0, yp = 0; j < height; j++) {
			int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
			for (int i = 0; i < width; i++, yp++) {
				int y = (0xff & ((int) yuv420sp[yp])) - 16;
				if (y < 0)
					y = 0;
				if ((i & 1) == 0) {
					v = (0xff & yuv420sp[uvp++]) - 127;
					u = (0xff & yuv420sp[uvp++]) - 127;
				}

				int y1192 = 1192 * y;
				int r = (y1192 + 1634 * v);
				int g = (y1192 - 733 * v - 400 * u);
				int b = (y1192 + 2066 * u);

				if (r < 0)
					r = 0;
				else if (r > 262143)
					r = 262143;
				if (g < 0)
					g = 0;
				else if (g > 262143)
					g = 262143;
				if (b < 0)
					b = 0;
				else if (b > 262143)
					b = 262143;

				rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000)
						| ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
			}
		}
		return rgb;
	}

	public static void fileScan(String fName, Context context) {
		Uri data = Uri.parse("file:///" + fName);
		context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
				data));
	}

	public void sdScan(Context context) {
		context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri
				.parse("file://" + Environment.getExternalStorageDirectory())));
	}

	public static Dialog createDialog(Context context, int iconId,
			String titleId, String messageId,
			DialogInterface.OnClickListener noListener) {
		Dialog dialog = null;
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setIcon(iconId);
		builder.setTitle(titleId);
		builder.setMessage(messageId);
		builder.setNegativeButton(context.getString(R.string.exit_xviewsdk), noListener);
		dialog = builder.create();
		return dialog;
	}

	public static String getNameFromFilename(String filename) {
		int dotPosition = filename.lastIndexOf('.');
		if (dotPosition != -1) {
			return filename.substring(0, dotPosition);
		}
		return "";
	}

	public static String formatDateString(Context context, long time) {
		Date date = new Date(time);
		SimpleDateFormat dateformat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String a1 = dateformat1.format(date);
		return a1;
	}

	public static String getExtFromFilename(String filename) {
		int dotPosition = filename.lastIndexOf('.');
		if (dotPosition != -1) {
			return filename.substring(dotPosition + 1, filename.length());
		}
		return "";
	}

	// �������صİٷֱ�
	public static String getPercent(long x, long total) {
		String result = "";// ���ܰٷֱȵ�ֵ
		double tempresult = x * 1.0 / total;
		DecimalFormat df1 = new DecimalFormat("0.00%"); // ##.00%
														// �ٷֱȸ�ʽ�����治��2λ����0����
		result = df1.format(tempresult);
		return result;
	}

	// �õ�SOS��������Ϣ �ֻ�IMEI��-ִ����Ա���-sos��Ϣ-gps gps('����-γ��')
	public static String getSOSMsg(Context context, String userid, String sos,
			String gps) {
		StringBuilder sb = new StringBuilder();
		sb.append(getIMEI(context)).append("-").append(userid).append("-")
				.append(sos).append("-").append(gps);
		return sb.toString();
	}

	// �õ��ֻ��IMEI��
	public static String getIMEI(Context context) {
		String imei = "-1";
		try {
			TelephonyManager telMgr = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			imei = telMgr.getDeviceId();
			if (imei == null) {
				imei = "-1";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return imei;
	}

	/**
	 * The number of bytes in a kilobyte.
	 */
	public static final long ONE_KB = 1024;

	/**
	 * The number of bytes in a megabyte.
	 */
	public static final long ONE_MB = ONE_KB * ONE_KB;

	/**
	 * The file copy buffer size (30 MB)
	 */
	private static final long FILE_COPY_BUFFER_SIZE = ONE_MB * 30;

	protected static SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(
			"MM/dd HH:mm");
	protected static SimpleDateFormat mSimpleDateFormat2 = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm");
	public static final int MEDIA_TYPE_VIDEO_THUMBNAIL = 1; // ��Ƶ����ͼ
	public static final int MEDIA_TYPE_VIDEO = 2; // ��Ƶ
	public static final int MEDIA_TYPE_ZIP = 3; // ��Ƶ

	/**
	 * ��ȡ��������
	 * 
	 * @return 0�������� 1��wifi 2:2G���� 3��3G����
	 */
	public static String getNetType(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

		if (networkInfo != null && networkInfo.isAvailable()) {
			if (ConnectivityManager.TYPE_WIFI == networkInfo.getType()) {
				return "wifi";
			} else {
				TelephonyManager telephonyManager = (TelephonyManager) context
						.getSystemService(Context.TELEPHONY_SERVICE);

				switch (telephonyManager.getNetworkType()) {
				case TelephonyManager.NETWORK_TYPE_GPRS:
				case TelephonyManager.NETWORK_TYPE_CDMA:
				case TelephonyManager.NETWORK_TYPE_EDGE:
					return "2G����";
				default:
					return "2G����";
				}
			}
		} else {
			return "������";
		}
	}

	/**
	 * ����Ƿ�Ϊwifi
	 * 
	 * @return true:wifi���� false:��������wifi����
	 */
	public static boolean isWifi(Context context) {
		NetworkInfo networkInfo = getAvailableNetWorkInfo(context);
		if (networkInfo != null) {
			if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
				return true;
			}
		}
		return false;
	}

	/**
	 * ��ȡ���õ�����
	 * 
	 * @return ����������粻����ʱ������null
	 */
	public static NetworkInfo getAvailableNetWorkInfo(Context context) {
		NetworkInfo activeNetInfo = null;
		try {
			ConnectivityManager connectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			activeNetInfo = connectivityManager.getActiveNetworkInfo();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		if (activeNetInfo != null && activeNetInfo.isAvailable()) {
			return activeNetInfo;
		} else {
			return null;
		}
	}

	public static String getSDcard(Context context) {
		String sdcard = null;
		final String status = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(status)) {
			sdcard = Environment.getExternalStorageDirectory().getPath();

			return sdcard;
		}
		return "/data/data/" + context.getPackageName() + "/";
	}

	/**
	 * �õ�sd���Ĵ�С ��M��
	 * 
	 * @return
	 */
	public static long readSDCard() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			File sdcardDir = Environment.getExternalStorageDirectory();
			StatFs sf = new StatFs(sdcardDir.getPath());
			long blockSize = sf.getBlockSize();
			long availCount = sf.getAvailableBlocks();
			return availCount * blockSize / 1024;
		} else {
			return 0;
		}
	}

	// �ı�һ���ļ���Ȩ��
	public static void chmod(String permission, String path) {
		try {
			String command = "chmod " + permission + " " + path;
			Runtime runtime = Runtime.getRuntime();
			runtime.exec(command);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * �ַ���ת��Ϊ�ַ�
	 * 
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public static String streamToString(InputStream is) {
		return streamToString(is, "UTF-7");
	}

	public static String streamToString(InputStream is, String enc) {
		StringBuilder buffer = new StringBuilder();
		String line = null;
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, enc), 7192);
			while (null != (line = reader.readLine())) {
				buffer.append(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return buffer.toString();
	}

	// ��ת��Ϊ�ֽ�
	public static byte[] streamToBytes(InputStream in) {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		try {
			byte[] temp = new byte[1024];
			int len = -1;
			while ((len = in.read(temp)) != -1) {
				buffer.write(temp, 0, len);
			}

			return buffer.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (buffer != null) {
				try {
					buffer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * @param is
	 * @param os
	 */
	public static void copyStream(InputStream is, OutputStream os) {
		final int buffer_size = 1024;
		try {
			byte[] bytes = new byte[buffer_size];
			int count = is.read(bytes, 0, buffer_size);
			while (-1 != count) {
				os.write(bytes, 0, count);
				count = is.read(bytes, 0, buffer_size);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static String bundle2String(Bundle bundle)
			throws UnsupportedEncodingException {
		if (null != bundle) {
			Set<String> keys = bundle.keySet();
			if (null != keys && !keys.isEmpty()) {
				StringBuilder buffer = new StringBuilder();
				for (String key : keys) {
					String value = bundle.getString(key);
					buffer.append("&").append(key).append("=")
							.append(URLEncoder.encode(value, "UTF-7"));
				}
				return buffer.toString();
			}
		}

		return "";
	}

	/**
	 * dip(value)=(int) (px(value) / density + 0.5)
	 * 
	 * @param context
	 * @param pxValue
	 * @return
	 */
	public static int px2dip(Context context, float pxValue) {
		final float density = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / density + 0.5f);
	}

	public static int dip2DeviceWidthPx(Context context, float dipValue) {
		final float density = context.getResources().getDisplayMetrics().density;
		float widthPixels = context.getResources().getDisplayMetrics().widthPixels;
		return (int) (widthPixels - density * 320 + density * dipValue);
	}

	/**
	 * �ַ��ʽ��
	 * 
	 * @param date
	 * @return
	 */
	public static String getDisplayTime(Long date) {
		return new SimpleDateFormat("MM/dd HH:mm").format(date);
	}

	/**
	 * @param date
	 * @return
	 */
	public static String getDisplayTime2(Long date) {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date);
	}

	/**
	 * ת��UTCʱ��
	 * 
	 * @param utcTime
	 * @return
	 */
	public static Date getDateByUTC(String utcTime) {
		Date date;
		SimpleDateFormat formatter = new SimpleDateFormat(
				"E MMM dd HH:mm:ss ZZZZ yyyy", Locale.ENGLISH);

		try {
			date = formatter.parse(utcTime);
		} catch (ParseException e) {
			e.printStackTrace();
			date = new Date();
		}

		return date;
	}

	public static String getDateStringByUTC(String utcTime) {
		if (TextUtils.isEmpty(utcTime))
			return "";
		return mSimpleDateFormat.format(new Date(utcTime));
	}

	public static String getDateStringByUTC2(String utcTime) {
		String timeStr = null;
		if (utcTime != null) {
			timeStr = mSimpleDateFormat2.format(new Date(utcTime));
		}
		return timeStr;
	}

	public static String getVideoTimeFormatString(int currentPosition,
			int duration) {
		SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");
		StringBuffer resultStr = new StringBuffer();
		resultStr.append(formatter.format(currentPosition));
		resultStr.append("/");
		resultStr.append(formatter.format(duration));
		return resultStr.toString();
	}

	public static String getVideoTimeFormatString(int time) {
		SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");
		StringBuffer resultStr = new StringBuffer();
		resultStr.append(formatter.format(time));
		return resultStr.toString();
	}

	/**
	 * ��ʽ��ʱ��
	 * 
	 * @param time
	 *            ��λΪ���뼶
	 * @return
	 */
	public static String formatToDoveBoxDate(Context context, String time) {if (context == null)
		return "";
	final Date currentData = new Date();
	final Date targetData = new Date(time);
	int intervalSeconds = (int) ((currentData.getTime() - targetData
			.getTime()) / 1000);
	int intervalMin = intervalSeconds / 60;
	if (intervalSeconds < 0 || intervalMin == 0) {
		return "�ոպ�";
	}
	StringBuilder sb = new StringBuilder();
	if (intervalMin < 60 && intervalMin > 0) {
		sb.append(intervalMin).append("R.string.minute_before");
		return sb.toString();
	}
	int intervalHour = intervalMin / 60;
	if (intervalHour < 24) {
		sb.append(intervalHour).append("R.string.hour_before");
		return sb.toString();
	}
	int currentPassMin = currentData.getHours() * 60
			+ currentData.getMinutes();
	int targetPassMin = targetData.getHours() * 60
			+ targetData.getMinutes();
	int intervalDay = (intervalMin - (targetPassMin - currentPassMin))
			/ (24 * 60);
	if (intervalDay < 3) {
		sb.append(intervalDay).append("R.string.day_before");
		return sb.toString();
	}
	return mSimpleDateFormat2.format(targetData);}

	/**
	 * ��ȡ��Ļ�߶ȵķ���
	 * 
	 * @param context
	 * @return ������Ļ���
	 */
	public static int[] getScreenHeightAndWidth(Context context) {
		int[] returnIntArray = new int[2];
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		returnIntArray[0] = dm.widthPixels;
		returnIntArray[1] = dm.heightPixels;
		return returnIntArray;
	}

	/**
	 * ����ͼƬ��Ҫ����1M�ڴ�
	 * 
	 * @param path
	 * @return ����С��1M��bitmap
	 */
	public static Bitmap create1MBitmap(String path) {
		Options opts = new Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, opts);
		float m = 1024 * 1024;
		opts.inSampleSize = (int) Math.ceil(Math.sqrt(opts.outWidth
				* opts.outHeight * 4 / m));
		opts.inJustDecodeBounds = false;
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeFile(path, opts);
		} catch (OutOfMemoryError er) {
			bitmap = null;
			er.printStackTrace();
		} catch (Exception e) {
			bitmap = null;
			e.printStackTrace();
		}
		return bitmap;

	}

	/**
	 * ����ͼƬ��Ҫ����1M�ڴ�
	 * 
	 * @param path
	 * @return ����С��1M��bitmap
	 */
	public static Bitmap create1MBitmap(Bitmap bitmap) {
		float m = 1024 * 1024;
		int time = (int) Math.ceil(Math.sqrt(bitmap.getWidth()
				* bitmap.getHeight() * 4 / m));
		Bitmap bitMap = null;
		try {
			bitMap = Bitmap.createBitmap(bitmap.getWidth() / time,
					bitmap.getHeight() / time, Config.RGB_565);
		} catch (OutOfMemoryError er) {
			bitmap = null;
			er.printStackTrace();
		} catch (Exception e) {
			bitmap = null;
			e.printStackTrace();
		}
		return bitMap;
	}

	/**
	 * ȡСͼƬ
	 * 
	 * @param path
	 * @return
	 */
	public static Bitmap createSmallBitmap(String path) {
		Options opts = new Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, opts);
		float m = 100 * 100;
		opts.inSampleSize = (int) Math.ceil(Math.sqrt(opts.outWidth
				* opts.outHeight * 4 / m));
		opts.inJustDecodeBounds = false;
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeFile(path, opts);
		} catch (OutOfMemoryError er) {
			bitmap = null;
			er.printStackTrace();
		} catch (Exception e) {
			bitmap = null;
			e.printStackTrace();
		}
		return bitmap;
	}

	/**
	 * 格式化时间（输出类似于 刚刚, 4分钟前, 一小时前, 昨天这样的时间）
	 * 
	 * @param time
	 *            需要格式化的时间 如"2014-07-14 19:01:45"
	 * @param pattern
	 *            输入参数time的时间格式 如:"yyyy-MM-dd HH:mm:ss"
	 *            <p/>
	 *            如果为空则默认使用"yyyy-MM-dd HH:mm:ss"格式
	 * @return time为null，或者时间格式不匹配，输出空字符""
	 */
	public static String formatDisplayTime(Context context, String time,
			String pattern) {	String display = "";
			int tMin = 60 * 1000;
			int tHour = 60 * tMin;
			int tDay = 24 * tHour;

			if (time != null) {
				try {
					Date tDate = new SimpleDateFormat(pattern).parse(time);
					Date today = new Date();
					SimpleDateFormat thisYearDf = new SimpleDateFormat("yyyy");
					SimpleDateFormat todayDf = new SimpleDateFormat("yyyy-MM-dd");
					Date thisYear = new Date(thisYearDf.parse(
							thisYearDf.format(today)).getTime());
					Date yesterday = new Date(todayDf.parse(todayDf.format(today))
							.getTime());
					Date beforeYes = new Date(yesterday.getTime() - tDay);
					if (tDate != null) {
						SimpleDateFormat halfDf = new SimpleDateFormat("MM"
								+ context.getString(R.string.month_xviewsdk) + "dd"
								+ context.getString(R.string.day_xviewsdk));
						long dTime = today.getTime() - tDate.getTime();
						if (tDate.before(thisYear)) {
							display = new SimpleDateFormat("yyyy"
									+ context.getString(R.string.year_xviewsdk) + "MM"
									+ context.getString(R.string.month_xviewsdk) + "dd"
									+ context.getString(R.string.day_xviewsdk))
									.format(tDate);
						} else {

							if (dTime < tMin) {
								display = context.getString(R.string.a_moment_ago_xviewsdk);
							} else if (dTime < tHour) {
								display = (int) Math.ceil(dTime / tMin)
										+ context.getString(R.string.minute_ago_xviewsdk);
							} else if (dTime < tDay && tDate.after(yesterday)) {
								display = (int) Math.ceil(dTime / tHour)
										+ context.getString(R.string.hour_ago_xviewsdk);
							} else if (tDate.after(beforeYes)
									&& tDate.before(yesterday)) {
								display = context.getString(R.string.yesterday_xviewsdk)
										+ new SimpleDateFormat("HH:mm")
												.format(tDate);
							} else {
								display = halfDf.format(tDate);
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			return display;}

	/**
	 * ����Сͼ
	 * 
	 * @param path
	 * @return
	 */
	public static String createSmallImage(String path) {
		Options opts = new Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, opts);
		float m = 100 * 100;
		opts.inSampleSize = (int) Math.ceil(Math.sqrt(opts.outWidth
				* opts.outHeight * 4 / m));
		opts.inJustDecodeBounds = false;
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeFile(path, opts);
		} catch (OutOfMemoryError er) {
			bitmap = null;
			er.printStackTrace();
		} catch (Exception e) {
			bitmap = null;
			e.printStackTrace();
		}

		StringBuilder sb = new StringBuilder();
		sb.append(path);
		sb.append(System.currentTimeMillis());
		sb.append("temp.jpg");
		File file = new File(sb.toString());
		if (file.exists()) {
			file.delete();
		}

		BufferedOutputStream bos = null;
		try {
			bos = new BufferedOutputStream(new FileOutputStream(file));
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
		} catch (OutOfMemoryError e) {
		} catch (Exception e) {
			return "";
		} finally {
			if (bos != null) {
				try {
					bos.flush();
					bos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
					return "";
				}
			}
		}
		return sb.toString();
	}

	/**
	 * ����һ���µ��ļ�(���ʹ���50Kb��ͼƬ������)�������紫��
	 * 
	 * @param savePath
	 * @param bitmap
	 * @return �����µ�ͼƬ����Ϊ�ա�Ϊ��ʱ��ʾû�������ͼƬ
	 */
	public static String create50KBFile(String savePath, Bitmap bitmap,
			boolean isMake) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		// if (!isMake && width * height < 1024 * 50) return "";

		StringBuilder sb = new StringBuilder();
		sb.append(savePath + "/");
		sb.append(System.currentTimeMillis());
		sb.append("temp.jpg");
		File file = new File(sb.toString());
		if (file.exists()) {
			file.delete();
		}

		BufferedOutputStream bos = null;
		try {
			bos = new BufferedOutputStream(new FileOutputStream(file));
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		} finally {
			if (bos != null) {
				try {
					bos.flush();
					bos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
					return "";
				}
			}
		}
		return sb.toString();
	}

	/**
	 * ��ȡ�Զ���������Ƶ�洢·��������ͼ���������
	 * 
	 * @throws IOException
	 */
	public static File getOutputMediaFile(Activity context, int type)
			throws IOException {
		// String state = Environment.getExternalStorageState();
		// if (!state.equals(Environment.MEDIA_MOUNTED)) {
		// throw new IOException("�ռ䲻��");
		// } else {
		// if (readSDCard() < 3096) {
		// throw new IOException("�ռ䲻��");
		// }
		// ;
		// }
		// XiuLiuApplication app=(XiuLiuApplication)context.getApplication();
		// String
		// path=Constant.getInstance(context).getFilesDir(app.getLocalUser().getId());
		// File mediaStorageDir = new File(path);
		// if (!mediaStorageDir.exists()) {
		// if (!mediaStorageDir.mkdirs()) {
		// return null;
		// }
		// }
		// // ����ý���ļ���
		// String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new
		// Date());
		// File mediaFile;
		// if (type == MEDIA_TYPE_VIDEO_THUMBNAIL) {
		// mediaFile = new File(mediaStorageDir.getPath() + File.separator +
		// "IMG_" + timeStamp
		// + ".jpg");
		// } else if (type == MEDIA_TYPE_VIDEO) {
		// mediaFile = new File(mediaStorageDir.getPath() + File.separator +
		// "VID_" + timeStamp
		// + ".mp4");
		// } else if (type == MEDIA_TYPE_ZIP)
		// {
		// mediaFile = new File(mediaStorageDir.getPath() + File.separator +
		// "VID_" + timeStamp
		// + ".zip");
		// }else {
		//
		// return null;
		// }
		// return mediaFile;

		return null;
	}

	/**
	 * ����ͼƬ�ļ�
	 * 
	 * @param oldPath
	 * @param newPath
	 * @throws IOException
	 */
	public static void onCopyPic(String oldPath, String newPath)
			throws IOException {
		File from = new File(oldPath);
		if (!from.exists() || !from.isFile() || !from.canRead()) {
			return;
		}
		File to = new File(newPath);
		if (!to.getParentFile().exists()) {
			to.getParentFile().mkdirs();
		}
		if (to.exists()) {
			to.delete();
		}

		FileInputStream fis = null;
		FileOutputStream fos = null;
		FileChannel input = null;
		FileChannel output = null;
		try {
			fis = new FileInputStream(from);
			fos = new FileOutputStream(to);
			input = fis.getChannel();
			output = fos.getChannel();
			long size = input.size();
			long pos = 0;
			long count = 0;
			while (pos < size) {
				count = size - pos > FILE_COPY_BUFFER_SIZE ? FILE_COPY_BUFFER_SIZE
						: size - pos;
				pos += output.transferFrom(input, pos, count);
			}
		} finally {
			closeQuietly(output);
			closeQuietly(fos);
			closeQuietly(input);
			closeQuietly(fis);
		}

		if (from.length() != to.length()) {
			throw new IOException("Failed to copy full contents from '" + from
					+ "' to '" + to + "'");
		}
		to.setLastModified(from.lastModified());
	}

	public static String getCameraPath() {
		String fileDir;
		if (new File("sdcard/Camera").exists()) {
			fileDir = "sdcard/Camera/";
		} else if (new File("sdcard/DCIM/Camera").exists()) {
			fileDir = "sdcard/DCIM/Camera/";
		} else {
			fileDir = "sdcard/DCIM/";
		}
		return fileDir;
	}

	private static void closeQuietly(OutputStream output) {
		closeQuietly((Closeable) output);
	}

	private static void closeQuietly(Closeable closeable) {
		try {
			if (closeable != null) {
				closeable.close();
			}
		} catch (IOException ioe) {
			// ignore
		}
	}

	/**
	 * get the external storage file
	 * 
	 * @return the file
	 */
	public static File getExternalStorageDir() {
		return Environment.getExternalStorageDirectory();
	}

	/**
	 * get the external storage file path
	 * 
	 * @return the file path
	 */
	public static String getExternalStoragePath() {
		return getExternalStorageDir().getAbsolutePath();
	}

	/**
	 * get the external storage state
	 * 
	 * @return
	 */
	public static String getExternalStorageState() {
		return Environment.getExternalStorageState();
	}

	/**
	 * check the usability of the external storage.
	 * 
	 * @return enable -> true, disable->false
	 */
	public static boolean isExternalStorageEnable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;

	}

	// public static final void hideSoftInputFromWindow(EditText editText,
	// Context context){
	// InputMethodManager imm = (InputMethodManager)
	// context.getSystemService(Context.INPUT_METHOD_SERVICE);
	// imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
	// }

	/**
	 * judge the list is null or isempty
	 * 
	 * @param list
	 * @return
	 */
	public static boolean isEmpty(final List<? extends Object> list) {
		if (list == null || list.isEmpty()) {
			return true;
		}
		return false;
	}

	public static boolean isEmpty(final Set<? extends Object> sets) {
		if (sets == null || sets.isEmpty()) {
			return true;
		}
		return false;
	}

	public static boolean isEmpty(
			final Map<? extends Object, ? extends Object> map) {
		if (map == null || map.isEmpty()) {
			return true;
		}
		return false;
	}

	/**
	 * Returns true if the string is null or 0-length.
	 * 
	 * @param text
	 * @return
	 */
	public static boolean isEmpty(final String text) {
		return TextUtils.isEmpty(text);
	}

	/**
	 * return true ,if the string is numeric
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(final String str) {
		if (isEmpty(str)) {
			return false;
		}
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches()) {
			return false;
		}
		return true;
	}

	/**
	 * get the width of the device screen
	 * 
	 * @param context
	 * @return
	 */
	public static int getSceenWidth(Context context) {
		return context.getResources().getDisplayMetrics().widthPixels;
	}

	/**
	 * get the height of the device screen
	 * 
	 * @param context
	 * @return
	 */
	public static int getSceenHeight(Context context) {
		return context.getResources().getDisplayMetrics().heightPixels;
	}

	/**
	 * 
	 * @param context
	 * @return
	 */
	public static float getSceenDensity(Context context) {
		return context.getResources().getDisplayMetrics().density;
	}

	/**
	 * convert the dip value to px value
	 * 
	 * @param context
	 * @param dipValue
	 * @return
	 */
	public static int dip2px(Context context, float dipValue) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	/**
	 * convert the string to another string which is build by the char value
	 * 
	 * @param str
	 * @return
	 */
	public static String toCharString(String str) {
		if (TextUtils.isEmpty(str)) {
			str = "null";
		}
		String strBuf = "";
		for (int i = 0; i < str.length(); i++) {
			int a = str.charAt(i);
			strBuf += Integer.toHexString(a).toUpperCase();
		}
		// strBuf=String.valueOf(str.hashCode());
		return strBuf;
	}

	/**
	 * hide the input method
	 * 
	 * @param view
	 *            ���������
	 */
	public static void hideSoftInput(View view) {
		if (view == null)
			return;
		InputMethodManager imm = (InputMethodManager) view.getContext()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm.isActive()) {
			imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
		}
	}

	/**
	 * show the input method
	 * 
	 * @param view
	 *            ��ʾ�����
	 */
	public static void showSoftInput(View view) {
		if (view == null)
			return;
		InputMethodManager imm = (InputMethodManager) view.getContext()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(view, 0);
	}

	public static int cleanFile(File dir, long maxInterval) {
		File[] files = dir.listFiles();
		if (files == null)
			return 0;
		int beforeNum = 0;
		long current = System.currentTimeMillis();
		for (File file : files) {
			long lastModifiedTime = file.lastModified(); // ��������
			if ((current - lastModifiedTime) > maxInterval) {
				// if the file is exist more than a week , so need to delete.
				file.delete();
				beforeNum++;
			}
		}
		return beforeNum;
	}

	public static boolean checkNetworkState(final Context context,
			final boolean finishParam) {
		boolean flag = false;
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (manager.getActiveNetworkInfo() != null) {
			flag = manager.getActiveNetworkInfo().isAvailable();
		}
		return flag;
	}

	public static boolean isExternalStorageValid() {
		return Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState());
	}

	public static File getTempSavePath(Context context) {
		if (isExternalStorageValid()) {
			return Environment.getExternalStorageDirectory();
		} else {
			return context.getFilesDir(); // data/data/paoming/files
		}
	}

	public static boolean checkNetworkState(final Context context) {
		boolean flag = false;
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (manager.getActiveNetworkInfo() != null) {
			flag = manager.getActiveNetworkInfo().isAvailable();
		}
		return flag;
	}

	public static String getTakephotoFileName() {
		String randomUUID = UUID.randomUUID().toString();
		return randomUUID + ".jpg";
	}

	/**
	 * @param save_path
	 * @param file_name
	 */
	public static Intent getStartCameraIntent(File save_file) {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(save_file));
		return intent;
	}

	/**
	 * @param context
	 * @param req_code
	 */
	public static Intent getStartGalleryIntent() {
		Intent intent = new Intent(Intent.ACTION_PICK, null);
		intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				"image/*");
		// Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
		// intent.setType("image/*");
		return intent;
	}

	/**
	 * ��������ת��Ϊʱ��
	 */
	public static String getFormatDate(String str) {
		if (str == null || "".equals(str.trim()))
			return "00:00:00";
		long ms = Long.parseLong(str) / 1000;
		long d = 0;
		d = ms / (3600 * 24);

		long h = 0;
		h = ((ms % (3600 * 24)) / 3600);

		long m = 0;
		m = (ms % 3600) / 60;

		long s = 0;
		s = ms % 60;

		return d == 0 ? String.format("%02d:%02d:%02d", h, m, s) : String
				.format("%d day(s) %02d:%02d:%02d", d, h, m, s);
	}

	// ��Сbת��Ϊ����ĵ�λ
	public static String convertStorage(long size) {
		long kb = 1024;
		long mb = kb * 1024;
		long gb = mb * 1024;

		if (size >= gb) {
			return String.format("%.1f GB", (float) size / gb);
		} else if (size >= mb) {
			float f = (float) size / mb;
			return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
		} else if (size >= kb) {
			float f = (float) size / kb;
			return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
		} else
			return String.format("%d B", size);
	}

	public static String getUUID() {
		return "{" + UUID.randomUUID().toString() + "}";
	}

	public static byte[] getMsgHead(byte[] msgid, byte[] mobile, int len,
			int count) {
		byte[] head = new byte[] { 12 };
		head[0] = 0x7e;
		head[1] = msgid[0];
		head[2] = msgid[1];
		head[3] = 0x00;
		head[4] = Byte.parseByte(Integer.toHexString(len));
		head[5] = mobile[0];
		head[6] = mobile[1];
		head[7] = mobile[2];
		head[7] = mobile[3];
		head[9] = mobile[4];
		head[10] = mobile[5];
		head[11] = Byte.parseByte(Integer.toHexString(count));
		;
		return head;
	}

	private static byte count = 0;

	public static byte[] getRegisterBytes() {

		byte[] register = new byte[] { 0x00, 0xb, 0x02, 0x61, 0x11, 0x11, 0x11,
				0x11, 0x11, 0x77, 0x77, 0x77, 0x77, 0x77, 0x77, 0x77, 0x77,
				0x77, 0x77, 0x77, 0x77, 0x77, 0x77, 0x77, 0x77, 0x77, 0x77,
				0x77, 0x77, 0x63, 0x28, 0x00, 0x21, 0x11, 0x16, 0x11, 0x0,
				0x49, 0x49 };

		byte[] msgHead = getMsgHead(new byte[] { 0x01, 0x00 }, new byte[] {
				0x01, 0x51, 0x40, 0x01, 0x46, 0x06 }, register.length, count++);
		byte[] newsend = new byte[register.length + msgHead.length];
		System.arraycopy(msgHead, 0, newsend, 0, msgHead.length);
		System.arraycopy(register, 0, newsend, msgHead.length, register.length);
		return newsend;
	}

	public static int getDuration(Context context, String urllocal) {
		Cursor cursor = context.getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
				MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
		// ����ý����ݿ�
		while (cursor.moveToNext()) {
			String url = cursor.getString(cursor
					.getColumnIndex(MediaStore.Audio.Media.DATA));
			if (urllocal.equals(url)) {
				int duration = cursor.getInt(cursor
						.getColumnIndex(MediaStore.Audio.Media.DURATION));
				return duration / 1000;
			}
		}
		return 0;
	}

	public static int getVideoDuration(Context context, String fileUrl) {
		Cursor cursor = context.getContentResolver().query(
				MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null, null,
				MediaStore.Video.Media.DEFAULT_SORT_ORDER);
		// ����ý����ݿ�
		while (cursor.moveToNext()) {
			String url = cursor.getString(cursor
					.getColumnIndex(MediaStore.Audio.Media.DATA));
			if (fileUrl.equals(url)) {
				int duration = cursor.getInt(cursor
						.getColumnIndex(MediaStore.Audio.Media.DURATION));
				return duration / 1000;
			}
		}
		return 0;
	}

}
