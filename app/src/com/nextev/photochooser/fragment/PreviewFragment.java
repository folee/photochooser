package com.nextev.photochooser.fragment;

import java.util.ArrayList;
import java.util.List;

import com.nextev.photochooser.R;
import com.nextev.photochooser.PhotoChooseActivity;
import com.nextev.photochooser.PhotoChooseMgr;
import com.nextev.photochooser.adapter.PreviewAdapter;
import com.nextev.photochooser.adapter.vo.ImageItem;
import com.nextev.photochooser.util.DebugLog;
import com.nextev.photochooser.util.ParallaxPageTransformer;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class PreviewFragment extends Fragment implements ViewPager.OnPageChangeListener {

    private ViewPager vp_preview;
    private CheckBox preview_image_cb;
    private TextView header_right_button;
    private TextView header_back;
    private RelativeLayout preview_image_header;
    private RelativeLayout preview_image_bottom;
    private List<ImageItem> data = new ArrayList<ImageItem>();

    public PreviewFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_preview, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();
        int offset = args == null ? 0 : args.getInt("offset", 0);
        boolean showAll = args == null ? false : args.getBoolean("show_all", false);
        //boolean isChangeTitleColor = args == null ? false : args.getBoolean("change_title_color", false);

        vp_preview = (ViewPager) getView().findViewById(R.id.vp_preview);
        preview_image_cb = (CheckBox) getView().findViewById(R.id.preview_image_cb);
        header_back = (TextView) getView().findViewById(R.id.header_back);
        header_right_button = (TextView) getView().findViewById(R.id.header_right_button);
        preview_image_bottom = (RelativeLayout) getView().findViewById(R.id.preview_image_bottom);
        preview_image_header = (RelativeLayout) getView().findViewById(R.id.preview_image_header);

        getView().findViewById(R.id.preview_image_header).setBackgroundResource(R.color.color_04);
        header_back.setTextColor(getResources().getColor(R.color.color_01));
        header_right_button.setTextColor(getResources().getColor(R.color.color_01));


        preview_image_cb.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                ImageItem item = (ImageItem) v.getTag();
                changeCheckStatus(item);
            }
        });
        header_back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                ((PhotoChooseActivity) getActivity()).onBackPressed();
            }
        });
        header_right_button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //当最多只可选一张，且还未选择时，设置当前Image为选中状态
                //217需求修改为，一张都未选择时，点击完成，发送当前预览的照片
                if (/*PhotoChooseMgr.getInstance(getActivity()).getMaxSelectSize() == 1 &&*/
                        PhotoChooseMgr.getInstance(getActivity()).getSeletectList().size() == 0 //
                        ) {
                    ImageItem item = (ImageItem) v.getTag();
                    PhotoChooseMgr.getInstance(getActivity()).addSelect(item);
                    //changeCheckStatus(item);
                }

                ((PhotoChooseActivity) getActivity()).finish();
            }
        });


        if (showAll) {
            data = PhotoChooseMgr.getInstance(getActivity()).getAllImageList();
        } else {
            data.addAll(PhotoChooseMgr.getInstance(getActivity()).getSeletectList());
        }
        vp_preview.setAdapter(new PreviewAdapter(this, data, 0));
        vp_preview.setOnPageChangeListener(this);
        vp_preview.setCurrentItem(offset);
        initVPAnim();
        changeSelectedCount();
        setCheckStatus(offset);
        //delayedHide(3000);
    }

    int mBottomHeight;
    int mHeaderHeight;
    int mShortAnimTime;
    boolean visible = true;

    public void handleHearderOrBottom() {
        DebugLog.d("visible = " + visible);
        if (mBottomHeight == 0) {
            mBottomHeight = preview_image_bottom.getHeight();
        }
        if (mHeaderHeight == 0) {
            mHeaderHeight = preview_image_header.getHeight();
        }
        if (mShortAnimTime == 0) {
            mShortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
        }
        preview_image_bottom.animate().translationY(visible ? mBottomHeight : 0).setDuration(mShortAnimTime);
        preview_image_header.animate().translationY(visible ? -mHeaderHeight : 0).setDuration(mShortAnimTime);

        visible = !visible;
        //		if (visible) {
        //			delayedHide(3000);
        //		}
    }

    Handler mHideHandler = new Handler();
    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            if (visible)
                handleHearderOrBottom();
        }
    };

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    public void changeSelectedCount() {

        PhotoChooseMgr imageLoaderMgr = PhotoChooseMgr.getInstance(getActivity());
        int selectedCount = imageLoaderMgr.getSelectCount();
        if (selectedCount > 0)
            header_right_button.setText(getString(R.string.select_done, selectedCount, imageLoaderMgr.getMaxSelectSize()));
        else
            header_right_button.setText(getString(R.string.complete));
    }

    public void setCheckStatus(int position) {
        if (data != null && data.size() > 0) {
            ImageItem item = data.get(position);
            PhotoChooseMgr imageLoaderMgr = PhotoChooseMgr.getInstance(getActivity());
            preview_image_cb.setTag(item);
            header_right_button.setTag(item);
            preview_image_cb.setChecked(imageLoaderMgr.getImageItem(item.id) != null && imageLoaderMgr.getSelectCount() <= imageLoaderMgr.getMaxSelectSize());
        }
    }

    public void changeCheckStatus(ImageItem item) {
        PhotoChooseMgr imageLoaderMgr = PhotoChooseMgr.getInstance(getActivity());
        if (!preview_image_cb.isChecked()) {
            preview_image_cb.setChecked(false);
            imageLoaderMgr.removeSelect(item);
        } else {
            if (!imageLoaderMgr.addSelect(item)) {
                preview_image_cb.setChecked(false);
                Toast.makeText(getActivity(), getString(R.string.max_pic, imageLoaderMgr.getMaxSelectSize()), Toast.LENGTH_LONG).show();
            } else {
                preview_image_cb.setChecked(true);
            }
        }
        changeSelectedCount();
    }

    private void initVPAnim() {
        /*try {
            Field mScroller;
            mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            Interpolator sInterpolator = new AccelerateDecelerateInterpolator();
            FixedScroller scroller = new FixedScroller(vp_preview.getContext(), sInterpolator);
            mScroller.set(vp_preview, scroller);
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        //vp_preview.setPageTransformer(true, new ZoomOutPageTransformer());
        //vp_preview.setPageTransformer(true, new DepthPageTransformer());
        vp_preview.setPageTransformer(true, new ParallaxPageTransformer());

    }

//	@Override
//	public void onClick(View v) {
//		switch (v.getId()) {
//		case R.id.header_back:
//			((PhotoChooseActivity) getActivity()).onBackPressed();
//			break;
//		case R.id.preview_image_cb:
//			ImageItem item = (ImageItem) v.getTag();
//			changeCheckStatus(item);
//			break;
//		case R.id.header_right_button:
//			((PhotoChooseActivity) getActivity()).finish();
//			break;
//		}
//	}

    @Override
    public void onPageScrollStateChanged(int pState) {
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int pPosition) {
        //DebugLog.d("Position = " + pPosition);
        setCheckStatus(pPosition);
    }
}
