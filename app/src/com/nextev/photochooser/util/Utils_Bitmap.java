package com.nextev.photochooser.util;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.media.ExifInterface;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class Utils_Bitmap {

	/**
	 * ??Image????
	 */
	private final static String	PHOTO_FOLDER_NAME	= "wenyao";

	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			// Calculate ratios of height and width to requested height and
			// width
			final int heightRatio = Math.round((float) height / (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			// Choose the smallest ratio as inSampleSize value, this will
			// guarantee
			// a final image with both dimensions larger than or equal to the
			// requested height and width.
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}

		return inSampleSize;
	}

	public static Bitmap decodeSampledBitmapFromByte(byte[] resource, int resId, int reqWidth, int reqHeight) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(resource, 0, resource.length, options);

		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeByteArray(resource, 0, resource.length, options);
	}

	public static Bitmap decodeSampledBitmapFromResource(Context context, int resId, int reqWidth, int reqHeight) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(context.getResources(), resId, options);
		if (reqWidth != 0 && reqHeight != 0) {
			options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
		}
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(context.getResources(), resId, options);
	}

	public static Bitmap decodeSampledBitmapFromFile(Context context, String path, int reqWidth, int reqHeight) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);
		if (reqWidth != 0 && reqHeight != 0) {
			options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
		}
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(path, options);
	}

	public static String saveImage(Bitmap photo, String fileName) {
		File root = new File(Environment.getExternalStorageDirectory() + File.separator + "qzapp/images/");
		if (!root.exists()) {
			root.mkdirs();
		}
		File out = new File(root, fileName);

		String absolutePath = out.getAbsolutePath();
		System.out.println("absolutePath is " + absolutePath);
		try {
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(absolutePath, false));

			photo.compress(Bitmap.CompressFormat.PNG, 100, bos);//????
			bos.flush();
			System.out.println("size is " + out.length());
			bos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return absolutePath;
	}

	public static String saveImageToPhoto(Bitmap photo, String fileName) {
		File root = new File(Environment.getExternalStorageDirectory() + File.separator + PHOTO_FOLDER_NAME);
		if (!root.exists()) {
			root.mkdirs();
		}
		File out = new File(root, fileName);

		String absolutePath = out.getAbsolutePath();
		System.out.println("absolutePath is " + absolutePath);
		try {
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(absolutePath, false));

			photo.compress(Bitmap.CompressFormat.PNG, 100, bos);//????
			bos.flush();
			System.out.println("size is " + out.length());
			bos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return absolutePath;
	}

	public static Bitmap getBitmapByFileName(String fileName, int reqWidth, int reqHeight) {
		File root = new File(Environment.getExternalStorageDirectory() + File.separator + "qzapp/images/");
		if (!root.exists()) {
			root.mkdirs();
		}
		File out = new File(root, fileName);

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(out.getAbsolutePath(), options);

		if (reqWidth != 0) {
			options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
		}
		options.inSampleSize = 1;
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(out.getAbsolutePath(), options);
	}

	public static synchronized String queryImageContentFromSdCard(String fileName) {
		File root = new File(Environment.getExternalStorageDirectory() + File.separator + "qzapp/images/");
		if (!root.exists()) {
			root.mkdirs();
		}
		File out = new File(root, fileName);
		try {
			FileInputStream inputStream = new FileInputStream(out);
			byte[] buffer = new byte[(int) out.length()];
			inputStream.read(buffer);
			inputStream.close();
			return Base64.encodeToString(buffer, Base64.DEFAULT);
			// return new String(buffer);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Bitmap zoomBitmap(Bitmap bitmap, int width) {
		if (null != bitmap){
			float scale = (float) width / bitmap.getWidth();
			Matrix matrix = new Matrix();
			matrix.reset();
			matrix.postScale(scale, scale);
			Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
			// bitmap.recycle();
			System.gc();
			return resizeBmp;
		}
		//????
		return bitmap;
	}

	public static Bitmap zoomBitmapByHeight(Bitmap bitmap, int height) {
		float scale = (float) height / bitmap.getHeight();
		Matrix matrix = new Matrix();
		matrix.reset();
		matrix.postScale(scale, scale);
		Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		// bitmap.recycle();
		System.gc();
		return resizeBmp;
	}

	public static Bitmap PicZoom(Bitmap bmp, int width, int height) {
		int bmpWidth = bmp.getWidth();
		int bmpHeght = bmp.getHeight();
		Matrix matrix = new Matrix();
		matrix.postScale((float) width / bmpWidth, (float) height / bmpHeght);
		return Bitmap.createBitmap(bmp, 0, 0, bmpWidth, bmpHeght, matrix, true);
	}

	/**
	 * ???
	 *
	 * @param bitmap
	 * @param px
	 * @return
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int px) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = px;
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}

	/**
	 * ???
	 *
	 * @param bitmap
	 * @return
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float pxPrecent) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = bitmap.getWidth() * pxPrecent;
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}

	public static Bitmap createBitmap(String path, int w, int h) {
		try {
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = false;//inJustDecodeBounds??true???????????
			BitmapFactory.decodeFile(path, opts);
			int srcWidth = opts.outWidth;// ?????????
			int srcHeight = opts.outHeight;// ????????
			int destWidth = 0;
			int destHeight = 0;
			// ?????
			double ratio = 0.0;
			if (srcWidth < w || srcHeight < h) {
				ratio = 0.0;
				destWidth = srcWidth;
				destHeight = srcHeight;
			}
			else if (srcWidth > srcHeight) {// ??????????????maxLength???????????
				ratio = (double) srcWidth / w;
				destWidth = w;
				destHeight = (int) (srcHeight / ratio);
			}
			else {
				ratio = (double) srcHeight / h;
				destHeight = h;
				destWidth = (int) (srcWidth / ratio);
			}
			BitmapFactory.Options newOpts = new BitmapFactory.Options();
			// ?????????????????????????????????inSampleSize????????????????SDK??????2????
			newOpts.inSampleSize = (int) ratio + 1;
			// inJustDecodeBounds??false??????????
			newOpts.inJustDecodeBounds = false;
			// ?????????????????inSampleSize????????????????
			newOpts.outHeight = destHeight;
			newOpts.outWidth = destWidth;
			// ???????
			return BitmapFactory.decodeFile(path, newOpts);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * ???????
	 *
	 * @param path
	 * @return
	 */
	public static int getPictureDegree(String path) {

		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;

			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;

			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;

			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return degree;
	}

	/**
	 * ????????
	 *
	 * @param bitmap
	 * @param path
	 * @return
	 */
	public static Bitmap rotateBitmap(Bitmap bitmap, String path) {

		if (bitmap != null) {
			Matrix m = new Matrix();
			int degress = getPictureDegree(path);
			m.postRotate(degress);
			Bitmap rotateBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
			return rotateBitmap;
		}
		return null;
	}


	/**
	 * ???????
	 * @param path
	 * --????
	 * @param degree
	 * --??????
	 */
	public static void rotateImage(String path, int degree) {
		if (!TextUtils.isEmpty(path) && new File(path).exists()) {
			BitmapFactory.Options newOpts = new BitmapFactory.Options();
			Bitmap bitmap = BitmapFactory.decodeFile(path, newOpts);

			Matrix m = new Matrix();
			m.postRotate(degree);
			Bitmap rotateBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);

			try {
				FileOutputStream out = new FileOutputStream(path);
				rotateBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
				out.flush();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (bitmap != null && !bitmap.isRecycled()) {
				bitmap.recycle();
				System.gc();
				bitmap = null;
			}

			if (rotateBitmap != null && !rotateBitmap.isRecycled()) {
				rotateBitmap.recycle();
				System.gc();
				rotateBitmap = null;
			}
		}
	}

	public static String getPathCut(String fileName) {
		File root = new File(Environment.getExternalStorageDirectory() + File.separator + "qzapp/images/");
		if (!root.exists()) {
			root.mkdirs();
		}
		File out = new File(root, fileName);
		String absolutePath = out.getAbsolutePath();

		return absolutePath;
	}


	public static int getViewMaxWidth(Activity activity) {
		DisplayMetrics metric = new DisplayMetrics();
		if (activity != null) {
			activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
			return (int) (metric.widthPixels - 10 * 2);
		}
		else {
			return metric.widthPixels;
		}

	}

	public static void setScale(Bitmap bitmap, View view, int maxWidth) {
		setScale(bitmap, 100, view, maxWidth);
	}

	/**
	 *
	 * ????????View???
	 *
	 * @param bitmap
	 *            -- ??ImageLoadingListener??bitmap??
	 * @param percent
	 *            -- ??????
	 * @param view
	 *            -- ?????View
	 * @param maxWidth
	 *            -- ???????
	 */
	public static void setScale(Bitmap bitmap, int percent, View view, int maxWidth) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, percent, baos);
		ByteArrayInputStream inputStream = new ByteArrayInputStream(baos.toByteArray());
		try {

			BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
			bitmapOptions.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(inputStream, null, bitmapOptions);
			bitmapOptions.inJustDecodeBounds = false;
			int bitmapWidth = bitmapOptions.outWidth;
			int bitmapHeight = bitmapOptions.outHeight;
			float scale = bitmapHeight * 1.0f / bitmapWidth;
			int imageWidth = maxWidth;
			int imageHeight = (int) (maxWidth * scale);

			DebugLog.i("imageWidth =?" + imageWidth + "; imageHeight =:" + imageHeight);
			ViewGroup.LayoutParams layoutParams = null;
			if (view.getParent() instanceof FrameLayout) {
				layoutParams = new FrameLayout.LayoutParams(imageWidth, imageHeight);
			}
			else if (view.getParent() instanceof RelativeLayout) {
				layoutParams = new RelativeLayout.LayoutParams(imageWidth, imageHeight);
			}
			else if (view.getParent() instanceof LinearLayout) {
				layoutParams = new LinearLayout.LayoutParams(imageWidth, imageHeight);
			}

			if (layoutParams != null)
				view.setLayoutParams(layoutParams);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (inputStream != null)
					inputStream.close();
				if (baos != null)
					baos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 *
	 * ????????View???
	 *
	 * @param drawable
	 *            -- ?????Drawable
	 * @param view
	 *            -- ?????View
	 * @param maxWidth
	 *            -- ???????
	 */
	public static void setScale(Drawable drawable, View view, int maxWidth) {
		setScale(drawable,view,maxWidth,true);
	}

	public static void setScale(Drawable drawable, View view, int maxWidth,boolean relativeCenter) {
		try {
			int bitmapWidth = drawable.getBounds().width();
			int bitmapHeight = drawable.getBounds().height();
			float scale = bitmapHeight * 1.0f / bitmapWidth;
			int imageWidth = maxWidth;
			int imageHeight = (int) (maxWidth * scale);

			DebugLog.i("imageWidth =?" + imageWidth + "; imageHeight =:" + imageHeight);
			ViewGroup.LayoutParams layoutParams = null;
			if (view.getParent() instanceof FrameLayout) {
				layoutParams = new FrameLayout.LayoutParams(imageWidth, imageHeight);
			}
			else if (view.getParent() instanceof RelativeLayout) {
				if (relativeCenter){
					layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
					((RelativeLayout.LayoutParams)layoutParams).addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
				}else{
					layoutParams = new RelativeLayout.LayoutParams(imageWidth, imageHeight);
				}
			}
			else if (view.getParent() instanceof LinearLayout) {
				layoutParams = new LinearLayout.LayoutParams(imageWidth, imageHeight);
			}

			if (layoutParams != null)
				view.setLayoutParams(layoutParams);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Bitmap drawable2Bitmap(Drawable drawable) {
		if (drawable instanceof BitmapDrawable) {
			return ((BitmapDrawable) drawable).getBitmap();
			} else if (drawable instanceof NinePatchDrawable) {
			Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight(),
					drawable.getOpacity() != PixelFormat.OPAQUE ? Config.ARGB_8888: Config.RGB_565);
			Canvas canvas = new Canvas(bitmap);
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
					drawable.getIntrinsicHeight());
			drawable.draw(canvas);
			return bitmap;
			} else {
			return null;
			}
	}


}
