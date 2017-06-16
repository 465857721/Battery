package com.king.battery.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore.Images.Media;
import android.util.DisplayMetrics;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

public class Util {
	public static String getDurationTime(int m) {
		SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");// 初始化Formatter的转换格式。

		return formatter.format(m);

	}

	public static String getUUID() {
		String uuid = UUID.randomUUID().toString().trim().replaceAll("-", "");
		return uuid;
	}

	public static int getWindowWidth(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay()
				.getMetrics(dm);
		return dm.widthPixels;
	}

	public static int getWindowHeight(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay()
				.getMetrics(dm);
		return dm.heightPixels;
	}

	// 判断sd卡是否可用
	public static boolean getSDCardStatus() {
		String status = Environment.getExternalStorageState();
		boolean result = false;
		if (Environment.MEDIA_MOUNTED.equals(status)) {
			result = true;
		}
		return result;
	}

	public static String getDate(int day) {// 得到日期 参数 前后几天 正数 为 后 负数 为前几天
		Calendar calendar = Calendar.getInstance();// 此时打印它获取的是系统当前时间
		calendar.add(Calendar.DATE, day); // 得到前day天

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");// 设置日期格式
		return df.format(calendar.getTime());
	}

	/**
	 * 向SharedPreferences中存入String
	 * 
	 * */
	public static void saveString(Context context, String columnName,
			String value) {
		try {
			Editor passfileEditor = context.getSharedPreferences("kvoa", 0)
					.edit();
			passfileEditor.putString(columnName, value);
			passfileEditor.commit(); // 提交数据
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 向SharedPreferences中存入String
	 * 
	 * */
	public static void removeString(Context context, String columnName) {
		try {
			Editor passfileEditor = context.getSharedPreferences("kvoa", 0)
					.edit();
			passfileEditor.remove(columnName);
			passfileEditor.commit(); // 提交数据
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 从SharedPreferences中读取String
	 * 
	 * */
	public static String getString(Context context, String columnName,
			String defValue) {
		try {
			SharedPreferences sharedPreferences = context.getSharedPreferences(
					"kvoa", 0);
			String show = sharedPreferences.getString(columnName, defValue);
			return show;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * @author ZhouKang
	 * @param url
	 *            网络地址 如http://iciba.com/aa.jpg
	 * @param picPath
	 *            SD卡路径 如 /sdcadr/kvoa/
	 * @return isok 是否成功
	 */
	public static boolean saveNetFile2SDCard(String url, String picPath) {
		File destDir = new File(picPath);
		if (!destDir.exists()) {
			destDir.mkdirs();
		}
		String temp[] = url.split("/");
		String picName = temp[temp.length - 1];
		return saveNetFile2SDCard(url, picPath, picName);
	}

	/**
	 * @author ZhouKang
	 * @param url
	 *            网络地址 如http://iciba.com/aa.jpg
	 * @param picPath
	 *            SD卡路径 如 /sdcadr/kvoa/
	 * @param rename
	 *            新文件的名字 如 xx.xx
	 * @return isok 是否成功
	 */
	public static boolean saveNetFile2SDCard(String url, String picPath,
			String rename) {
		boolean ok = false;
		InputStream is;
		try {
			String cnname = rename;
			// url = url.replaceAll(cnname, "");
			is = getUrlInputStream(url);
			saveStreamToFile(is, picPath + rename);
			ok = true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ok;
	}

	// public static boolean saveNetFile2SDCard(String url, String
	// picPath,String rename){
	// boolean ok = false;
	// InputStream is;
	// try {
	// String cnname = Util.getNetFileName(url);
	// url = url.replaceAll(cnname, "");
	// is = getUrlInputStream(url+URLEncoder.encode(cnname));
	// saveStreamToFile(is, picPath + rename);
	// ok = true;
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// return ok;
	// }
	/**
	 * @author ZhouKang
	 * @param url
	 *            网络地址 如http://iciba.com/aa.jpg
	 * @return Bitmap 对象
	 */

	// public static Bitmap getUrlimg(String url){
	// Bitmap bit = null;
	// String name = getNetFileName(url);
	// File destDir = new File(Const.picPath);
	// if (!destDir.exists()) {
	// destDir.mkdirs();
	// }
	// bit = Util.getUrlimg(url, Const.picPath);
	// return bit;
	// }
	/**
	 * @author ZhouKang
	 * @param url
	 *            网络地址 如http://iciba.com/aa.jpg
	 * @param picPath
	 *            SD卡路径 如 /sdcadr/kvoa/
	 * @return Bitmap 对象
	 */

	public static Bitmap getUrlimg(String url, String picPath) {
		String picName = getNetFileName(url);
		if (!saveNetFile2SDCard(url, picPath))
			return null;
		return BitmapFactory.decodeFile(picPath + picName);
	}

	public static Bitmap getUrlimg(String url, String filename, String picPath) {
		String picName = filename;
		if (!saveNetFile2SDCard(url, picPath, filename))
			return null;
		return BitmapFactory.decodeFile(picPath + picName);
	}

	/**
	 * 
	 * @param url
	 *            地址 如 http://iciba.com/aa.xx
	 * @return aa.xx
	 */
	public static String getNetFileName(String url) {
		String temp[] = url.split("/");
		String picName = temp[temp.length - 1];
		return picName;
	}

	public static InputStream getUrlInputStream(String url) throws IOException {
		URL imageUrl = null;
		imageUrl = new URL(url);

		HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
		conn.connect();
		InputStream is = conn.getInputStream();
		return is;
	}

	public static boolean saveStreamToFile(InputStream in, String fileNamePath)
			throws IOException {
		boolean result = true;

		File f = null;
		try {
			f = new File(fileNamePath);
			if (f.exists()) {
				f.delete();
			} else {
				f.createNewFile();
			}

			FileOutputStream fos = new FileOutputStream(f);
			copyStream(in, fos);
			fos.close();
		} catch (Exception e) {
			if (f != null && f.exists()) {
				f.delete();
			}
			e.printStackTrace();
			result = false;
		}
		return result;
	}

	public static void copyStream(InputStream in, OutputStream out)
			throws IOException {
		BufferedInputStream bin = new BufferedInputStream(in);
		BufferedOutputStream bout = new BufferedOutputStream(out);

		byte[] buffer = new byte[4096];

		while (true) {
			int doneLength = bin.read(buffer);
			if (doneLength == -1)
				break;
			bout.write(buffer, 0, doneLength);
		}
		bin.close();
		bout.flush();
		bout.close();
	}

	/**
	 * 获得 bitmap的缩略图
	 * 
	 * @param times
	 *            缩小倍数 1/tiames
	 * @return bitmap
	 * @author ZhouKang
	 * 
	 */
	// public static Bitmap getSmaleBitmap(Bitmap bit, int times) {
	// if (bit != null) {
	// bit = ThumbnailUtils.extractThumbnail(bit, bit.getWidth() / times,
	// bit.getHeight() / times);
	// }
	// return bit;
	// }
	/**
	 * 转换图片成圆形
	 * 
	 * @param bitmap
	 *            传入Bitmap对象
	 * @return
	 */
	public static Bitmap toRoundBitmap(Bitmap bitmap) {
		if (bitmap == null)
			return null;
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float roundPx;
		float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
		if (width <= height) {
			roundPx = width / 2;
			top = 0;
			bottom = width;
			left = 0;
			right = width;
			height = width;
			dst_left = 0;
			dst_top = 0;
			dst_right = width;
			dst_bottom = width;
		} else {
			roundPx = height / 2;
			float clip = (width - height) / 2;
			left = clip;
			right = width - clip;
			top = 0;
			bottom = height;
			width = height;
			dst_left = 0;
			dst_top = 0;
			dst_right = height;
			dst_bottom = height;
		}

		Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect src = new Rect((int) left, (int) top, (int) right,
				(int) bottom);
		final Rect dst = new Rect((int) dst_left, (int) dst_top,
				(int) dst_right, (int) dst_bottom);
		final RectF rectF = new RectF(dst);

		paint.setAntiAlias(true);

		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bitmap, src, dst, paint);
		return output;
	}

	// 得到距离 几天
	public static String getTextFormRefresh(Context context, String name) {
		// "dsretime"
		long time = Long.valueOf(Util.getString(context, name, "0"));
		long now = System.currentTimeMillis();
		int sec = (int) ((now - time) / 1000);
		if (sec < 60) {
			return sec + "秒前更新";
		} else if (sec < 60 * 60) {
			int min = sec / 60;
			return min + "分钟前更新";
		} else if (sec < 60 * 60 * 60) {
			int hou = sec / 60 / 60;
			return hou + "小时前更新";
		} else if (sec < 60 * 60 * 60 * 24) {
			int day = sec / 60 / 60 / 24;
			if (day > 99) {
				return "尼玛N天前跟新";
			} else {
				return day + "天前跟新";
			}
		}

		return "";
	}

	public static void SendShare(Context context, String Title, String Body,
			String imagePath1) {
		Intent it = new Intent(Intent.ACTION_SEND);
		String[] tos = { "" };
		if (imagePath1 != null) {
			String url;
			try {
				File imageFile = new File(imagePath1);
				if (imageFile.exists() && imageFile.length() > 0) {
					url = Media.insertImage(context.getContentResolver(),
							imageFile.getAbsolutePath(), imageFile.getName(),
							imageFile.getName());
					Uri uri = Uri.parse(url);// + imagePath1);
					it.putExtra(Intent.EXTRA_STREAM, uri);
				} else {
					imagePath1 = null;
				}
			} catch (Exception e) {
				e.printStackTrace();
				imagePath1 = null;
			}
		}
		it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		it.putExtra(Intent.EXTRA_EMAIL, tos);
		// it.putExtra(Intent.EXTRA_SUBJECT, "");
		it.putExtra(Intent.EXTRA_TITLE, "" + Title);
		it.putExtra(Intent.EXTRA_TEXT, "" + Body);
		if (imagePath1 == null) {
			it.setType("text/plain");
		} else {
			it.setType("image/*");
		}
		it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(it);
		return;
	}


	// 判断是否是wifi联接
	public static boolean isNetWifi(Context context) {
		try {
			ConnectivityManager connManager = (ConnectivityManager) (context)
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = connManager.getActiveNetworkInfo();
			/******* EMULATOR HACK - false condition needs to be removed *****/
			if ((netInfo == null || netInfo.isConnected() == false)) {
				// SendMessage((context), "No Internet Connection");
			} else if (netInfo.getTypeName().equals("WIFI")) {
				return true;
			} else {

				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static void showToast(String text, Context context) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static int sp2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (dipValue * scale + 0.5f);
	}

	public static int px2sp(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (pxValue / scale + 0.5f);
	}

	public static boolean isNetworkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}
	public static void showCommentDialog(final Context context){

		int times =Integer.valueOf(getString(context, "times", "0")) ;
		if(times++>6){
			new AlertDialog.Builder(context)   
			.setTitle("电量显示")  
			.setMessage("您觉得好用或者有建议给个评价把~")  
			.setPositiveButton("施舍一下", new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					Uri uri = Uri.parse("market://details?id=" +  context.getPackageName());
					Intent intent = new Intent(Intent.ACTION_VIEW, uri);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(intent);
				}
				
			})  
			.setNegativeButton("残忍拒绝", null)  
			.show();
			times=0;
		}
		else{
			
		}
		saveString(context, "times", ""+times);
		  
	}
}