package com.nextev.photochooser.util;

import com.nextev.photochooser.PhotoApp;

import java.util.Locale;

import me.xiaopan.sketch.CancelCause;
import me.xiaopan.sketch.DisplayListener;
import me.xiaopan.sketch.DisplayOptions;
import me.xiaopan.sketch.FailCause;
import me.xiaopan.sketch.ImageFrom;
import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.SketchImageView;

public class ImageLoader {

	private static ImageLoader	instance;

	private ImageLoader() {}

	public static ImageLoader getInstance() {
		if (instance == null) {
			synchronized (ImageLoader.class) {
				if (instance == null) {
					instance = new ImageLoader();
				}
			}
		}
		return instance;
	}

	/**
	 * ??????TAG?????Adapter???????
	 *
	 * @param view
	 *            ??View
	 * @param url
	 *            ??????
	 * @param optionsName
	 *            UIL??Option
	 */
	public void displayImage(String url, SketchImageView view, Enum<?> optionsName, SketchImageView.ImageShape imageShape) {

		if (url == null) {
			url = "";
		}

		if (view.getTag() == null || !view.getTag().equals(url + view.getId())) {
			view.setTag(url + view.getId());

			view.setDisplayOptions(optionsName);
			view.setImageShape(imageShape);
			view.displayImage(url);

		}
	}

	public void displayImage(String url, SketchImageView view, DisplayOptions displayOptions, SketchImageView.ImageShape imageShape) {

		if (url == null) {
			url = "";
		}

		if (view.getTag() == null || !view.getTag().equals(url + view.getId())) {
			view.setTag(url + view.getId());

			view.setDisplayOptions(displayOptions);
			view.setImageShape(imageShape);
			view.displayImage(url);
		}
	}

	public void displayImage(String url, final SketchImageView view, Enum<?> optionsName, SketchImageView.ImageShape imageShape, final int width) {
		displayImage(url, view, optionsName, imageShape, width, false);
	}

	public void displayImage(String url, final SketchImageView view, Enum<?> optionsName, SketchImageView.ImageShape imageShape, final int width, final boolean relativeCenter) {

		if (url == null) {
			url = "";
		}

		if (view.getTag() == null || !view.getTag().equals(url + view.getId())) {
			view.setTag(url + view.getId());

			if (width > 0) {
				view.setDisplayListener(new DisplayListener() {
					@Override
					public void onStarted() {

					}

					@Override
					public void onCompleted(ImageFrom imageFrom, String mimeType) {
						Utils_Bitmap.setScale(view.getDrawable(), view, width, false);
					}

					@Override
					public void onFailed(FailCause failCause) {

					}

					@Override
					public void onCanceled(CancelCause cancelCause) {

					}
				});
			}

			view.setDisplayOptions(optionsName);
			view.setImageShape(imageShape);
			view.displayImage(url);

		}
	}

	public void displayImage(String url, final SketchImageView view, DisplayOptions displayOptions, SketchImageView.ImageShape imageShape, final int width) {
		displayImage(url, view, displayOptions, imageShape, width, false);
	}
	
	public void displayImage(String url, final SketchImageView view, DisplayOptions displayOptions, SketchImageView.ImageShape imageShape, final int width, final boolean relativeCenter) {

		if (url == null) {
			url = "";
		}

		if (view.getTag() == null || !view.getTag().equals(url + view.getId())) {
			view.setTag(url + view.getId());

			if (width > 0) {
				view.setDisplayListener(new DisplayListener() {
					@Override
					public void onStarted() {

					}

					@Override
					public void onCompleted(ImageFrom imageFrom, String mimeType) {
						Utils_Bitmap.setScale(view.getDrawable(), view, width, relativeCenter);
					}

					@Override
					public void onFailed(FailCause failCause) {

					}

					@Override
					public void onCanceled(CancelCause cancelCause) {

					}
				});
			}

			view.setDisplayOptions(displayOptions);
			view.setImageShape(imageShape);
			view.displayImage(url);
		}
	}

	public void clearMemoryCache() {
		Sketch.with(PhotoApp.getContext()).getConfiguration().getMemoryCache().clear();
	}

	/**
	 * Represents supported schemes(protocols) of URI. Provides convenient
	 * methods for work with schemes and URIs.
	 */
	public enum Scheme {
	HTTP("http"), HTTPS("https"), FILE("file"), CONTENT("content"), ASSETS("assets"), DRAWABLE("drawable"), UNKNOWN("");

	private String	scheme;
	private String	uriPrefix;

	Scheme(String scheme) {
		this.scheme = scheme;
		uriPrefix = scheme + "://";
	}

	/**
	 * Defines scheme of incoming URI
	 *
	 * @param uri
	 *            URI for scheme detection
	 * @return Scheme of incoming URI
	 */
	public static Scheme ofUri(String uri) {
		if (uri != null) {
			for (Scheme s : values()) {
				if (s.belongsTo(uri)) {
					return s;
				}
			}
		}
		return UNKNOWN;
	}

	private boolean belongsTo(String uri) {
		return uri.toLowerCase(Locale.US).startsWith(uriPrefix);
	}

	/** Appends scheme to incoming path */
	public String wrap(String path) {
		return uriPrefix + path;
	}

	/** Removed scheme part ("scheme://") from incoming URI */
	public String crop(String uri) {
		if (!belongsTo(uri)) {
			throw new IllegalArgumentException(String.format("URI [%1$s] doesn't have expected scheme [%2$s]", uri, scheme));
		}
		return uri.substring(uriPrefix.length());
	}
	}
}
