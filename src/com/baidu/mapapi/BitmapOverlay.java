package com.baidu.mapapi;

import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.Overlay;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class BitmapOverlay extends Overlay {

	private Paint mPaint = new Paint();
	private GeoPoint mBaiduGeoPoint;

	private Activity mActivity;
	private MapView mMapView;
	private DrawPopupUtil mDrawPopupUtil;

	public BitmapOverlay(Activity activity, MapView mapView) {
		this.mActivity = activity;
		this.mMapView = mapView;
		this.mDrawPopupUtil = new DrawPopupUtil(activity);
	}

	private Bitmap mBitmap;
	private int mWidth;
	private int mHeight;

	public void setBitmap(int resId) {
		mBitmap = ((BitmapDrawable) (mActivity.getResources()
				.getDrawable(resId))).getBitmap();

		mWidth = mBitmap.getWidth();
		mHeight = mBitmap.getHeight();
	}

	public void setBitmap(String assetName) {
		mBitmap = new BitmapHelper(mActivity).getBitmapFromAssets(assetName);

		mWidth = mBitmap.getWidth();
		mHeight = mBitmap.getHeight();
	}

	public void setData(MapPointInfo info) {
		if (info != null && info.getGps() != null) {

			// 转换成百度坐标
			mBaiduGeoPoint = CoordinateConvert.bundleDecode(CoordinateConvert
					.fromWgs84ToBaidu(info.getGps()));
			// 设置 提示文本
			mDrawPopupUtil.setText(info.getStr());
		} else {
			mBaiduGeoPoint = null;
		}

		mMapView.invalidate();
	}

	private Point mCurrPoint;

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean arg2) {
		if (mBaiduGeoPoint != null) {

			// 获得屏幕上的坐标
			mCurrPoint = mapView.getProjection().toPixels(mBaiduGeoPoint, null);

			// 绘制图标
			canvas.drawBitmap(mBitmap, mCurrPoint.x - mWidth / 2, mCurrPoint.y
					- mHeight, mPaint);

			// 绘制提示文本
			mDrawPopupUtil.draw(canvas, mCurrPoint.x, mCurrPoint.y - mHeight
					+ 4);
		}
	}

	private Rect mRect = new Rect();

	@Override
	public boolean onTap(GeoPoint eventGeoPoint, MapView mapView) {
		if (mCurrPoint != null) {
			Point event = mMapView.getProjection()
					.toPixels(eventGeoPoint, null);

			mRect.set(mCurrPoint.x - mWidth / 2, mCurrPoint.y - mHeight,
					mCurrPoint.x + mWidth, mCurrPoint.y + 0);

			if (mDrawPopupUtil.onTap(mRect, event)) {
				// 刷新地图
				mMapView.invalidate();
				return true;
			}
		}
		return super.onTap(eventGeoPoint, mapView);
	}
}
