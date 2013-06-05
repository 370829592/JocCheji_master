package com.baidu.mapapi;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;

public class RoutesOverlay extends Overlay {

	public static final int REVERSE_GEOCODE = 0x0011;

	private DrawPopupUtil mDrawPopupUtilBeg;
	private DrawPopupUtil mDrawPopupUtilEnd;
	private MapView mMapView;
	private Paint mPaintL;
	private Paint mPaintM;
	private Paint mPaintT = new Paint();
	private Path mPath;
	private Bitmap mBitmaps[] = new Bitmap[2];
	private Bitmap mDirectionIcon;
	private int mDirectionWhith;
	private int mDirectionHeight;
	private Matrix mDirectionMatrix = new Matrix();

	public RoutesOverlay(Activity activity, MapView mapView) {
		this.mMapView = mapView;
		this.mDrawPopupUtilBeg = new DrawPopupUtil(activity);
		this.mDrawPopupUtilEnd = new DrawPopupUtil(activity);

		BitmapHelper bitmapHelper = new BitmapHelper(activity);
		{
			mBitmaps[0] = bitmapHelper
					.getDpiBitmapFromAssets("icon_nav_start_");
			mBitmaps[1] = bitmapHelper.getDpiBitmapFromAssets("icon_nav_end_");

			mDirectionIcon = bitmapHelper
					.getDpiBitmapFromAssets("icon_direction_");

			mDirectionWhith = mDirectionIcon.getWidth();
			mDirectionHeight = mDirectionIcon.getHeight();

			mDirectionMatrix.setScale(1.0f, 1.0f);
		}

		mPaintL = new Paint();
		{
			mPaintL.setAntiAlias(true);
			mPaintL.setDither(true);
			mPaintL.setColor(0xFFFF0000);
			mPaintL.setStyle(Paint.Style.STROKE);
			mPaintL.setStrokeJoin(Paint.Join.ROUND);
			mPaintL.setStrokeCap(Paint.Cap.ROUND);
			mPaintL.setStrokeWidth(6.0F);
			mPaintL.setColor(Color.argb(192, 65, 110, 213));
		}
		mPath = new Path();
		mPaintM = new Paint();
		{
			mPaintM.setAntiAlias(true);
			mPaintM.setDither(true);
			mPaintM.setColor(0xFFFF0000);
			mPaintM.setStyle(Paint.Style.STROKE);
			mPaintM.setStrokeJoin(Paint.Join.ROUND);
			mPaintM.setStrokeCap(Paint.Cap.ROUND);
			// mPaintM.setStrokeWidth(6.0F);
			mPaintM.setStrokeWidth(4.0F);
			mPaintM.setColor(Color.argb(192, 117, 148, 225));
		}

		{
			mPaintT.setAntiAlias(true);
			mPaintT.setStrokeWidth(1);
			mPaintT.setColor(Color.RED);
		}
	}

	public GeoPoint getBeg() {
		if (mBaiduCompGeoPointList != null && mBaiduCompGeoPointList.size() > 0) {
			return mBaiduCompGeoPointList.get(0);
		}
		return null;
	}

	public GeoPoint getEnd() {
		if (mBaiduCompGeoPointList != null && mBaiduCompGeoPointList.size() > 0) {
			return mBaiduCompGeoPointList
					.get(mBaiduCompGeoPointList.size() - 1);
		}
		return null;
	}

	private ArrayList<GeoPoint> mWgs84GeoPointList;
	private ArrayList<GeoPoint> mBaiduCompGeoPointList;

	public void setData(ArrayList<GeoPoint> lstWgs84GeoPoint) {
		mWgs84GeoPointList = lstWgs84GeoPoint;
		mBaiduCompGeoPointList = RoutesHelper
				.getBaiduCompGeoPoint(lstWgs84GeoPoint);

		reqAddrCode(getBeg());// 请求地址名称
		mMapView.invalidate();
	}

	private ArrayList<Point> mShowPointList;
	private ArrayList<Point> mViewPointList;

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean arg2) {
		// 判断点数(小于两个点也没必要画了)
		if (mBaiduCompGeoPointList == null || mBaiduCompGeoPointList.size() < 2)
			return;

		long begShowPointList = System.currentTimeMillis();

		// 从缓存中获取需要显示的屏幕坐标
		mShowPointList = RoutesHelper.getCacheBaiduShowPoint(mapView,
				mWgs84GeoPointList);

		long ct = System.currentTimeMillis() - begShowPointList;

		long startdrawLine = System.currentTimeMillis();
		// int count = 0;
		int showSize = mShowPointList.size();
		if (showSize < 2) {
			return;
		}

		int mapZoomLevel = mapView.getZoomLevel();
		double deviationRange = (Math.pow(2, mapZoomLevel - 9) + mapZoomLevel - 9) * 9;

		int distance = 0;

		// 绘制路线（判断压缩只把把需要特征的点）
		ArrayList<Point> lstDrawPoist = new ArrayList<Point>();
		Point p1 = mShowPointList.get(0);
		String k1 = getKey(p1);
		mPath.reset();
		mPath.moveTo(p1.x, p1.y);
		lstDrawPoist.add(p1);

		Point p2 = null;
		String k2 = null;
		Point p0 = p1;
		for (int i = 1; i < showSize; i++) {
			p2 = mShowPointList.get(i);
			k2 = getKey(p2);

			// canvas.drawCircle(p2.x, p2.y, 3, mPaintM);// test
			if (k1.equals(k2)) {
				p0 = p2;
				continue;
			}
			k1 = k2;

			// 判断是否是断开的连线
			// 判断是否00坐标// 去掉00坐标
			distance = (p2.y - p0.y) * (p2.y - p0.y) + (p2.x - p0.x)
					* (p2.x - p0.x);
			if (distance > 0 && distance < deviationRange * deviationRange) {
				mPath.quadTo(p1.x, p1.y, p2.x, p2.y);
				lstDrawPoist.add(p2);
				// count++;
			}

			p0 = p1 = p2;
		}
		mPath.lineTo(p2.x, p2.y);

		// 绘制行车轨迹
		canvas.drawPath(mPath, mPaintL);
		canvas.drawPath(mPath, mPaintM);
		// mPath.reset();

		// 绘制转角图标
		int drawSize = lstDrawPoist.size();
		if (drawSize > 3) {
			Point point0 = lstDrawPoist.get(0);
			Point point1 = lstDrawPoist.get(1);
			Point point2;
			Point pprov = point0;
			int alt = mMapView.getWidth() / 8;
			for (int i = 3; i < drawSize; i++) {
				point2 = lstDrawPoist.get(i);

				// 距离必须大于一定范围
				if (getDistance(pprov, point1) > alt) {
					drawDirectionIcon(canvas, pprov = point1,
							getRotateDirection(point1, point2));
				}

				point0 = point1;
				point1 = point2;

				mDirectionTemp = null;
			}
		}

		// 绘制 起点、终点
		mViewPointList = RoutesHelper.getCacheBaiduCompViewPoint(mapView,
				mWgs84GeoPointList);
		drawBegPoint(canvas, mViewPointList.get(0));
		drawEndPoint(canvas, mViewPointList.get(mViewPointList.size() - 1));
		// ~

		// long dt = System.currentTimeMillis() - startdrawLine;
		//
		// String outlog = "算/绘:" + ct + "/" + dt + ",总/压/屏/终:"
		// + mWgs84GeoPointList.size() + "/"
		// + mBaiduCompGeoPointList.size() + "/" + mShowPointList.size()
		// + "/" + lstDrawPoist.size() + ",缩:" + mapView.getZoomLevel();// +
		// // "/"
		// // DR/+
		// // deviationRange
		// System.out.println(outlog);
	}

	private Bitmap mDirectionTemp;

	private double getDistance(Point p0, Point p1) {

		return Math.sqrt(Math.pow((p1.x - p0.x), 2)
				+ Math.pow((p1.y - p0.y), 2));
	}

	private double getRotateDirection(Point p0, Point p1) {
		double x = p1.x - p0.x;
		double y = p1.y - p0.y;

		double jiaodu = Math.atan(y / x);
		if (x >= 0) {
			if (y >= 0) {
				return jiaodu;
			} else {
				return jiaodu;// -jiaodu?
			}
		} else {
			if (y >= 0) {
				return Math.PI + jiaodu;
			} else {
				return Math.PI + jiaodu;
			}
		}
	}

	private void drawDirectionIcon(Canvas canvas, Point point, double direction) {
		mDirectionMatrix.reset();
		/* 设置旋转 */
		float jiaodu = 90 + (float) (direction / Math.PI * 180);
		mDirectionMatrix.setRotate(jiaodu);
		// Log.e("direction/jiaodu", "direction:" + direction / Math.PI
		// + "*PI,jiaodu:" + jiaodu);

		try {
			/* 按mMatrix得旋转构建新的Bitmap */
			mDirectionTemp = Bitmap.createBitmap(mDirectionIcon, 0, 0,
					mDirectionWhith, mDirectionHeight, mDirectionMatrix, true);

			/* 绘制旋转之后的图片 */
			canvas.drawBitmap(mDirectionTemp,
					(float) (point.x - mDirectionTemp.getWidth() / 2.0),
					(float) (point.y - mDirectionTemp.getHeight() / 2.0), null);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		// canvas.drawText("" + jiaodu, point.x, point.y, mPaintT);
	}

	private String getKey(Point point) {
		return point.x / 4 + "," + point.y / 4;
	}

	// /**
	// * 画行车路线
	// *
	// * @param canvas
	// * @param p1
	// * @param p2
	// */
	// private void drawRoute(Canvas canvas, Point p1, Point p2) {
	// // canvas.drawLine(p1.x, p1.y, p2.x, p2.y, mPaintL);
	// canvas.drawLine(p1.x, p1.y, p2.x, p2.y, mPaintM);
	//
	// // // 拐点圆弧
	// // canvas.drawCircle(p2.x, p2.y, 3, mPaintL);
	// }

	/**
	 * 画点（起点、终点）
	 * 
	 * @param canvas
	 * @param mapView
	 * @param route
	 * @param text
	 */
	private void drawBegPoint(Canvas canvas, Point point) {

		// // 绘制圆圈
		// // canvas.drawCircle(point.x, point.y, 1, mPaintL);
		// canvas.drawCircle(point.x, point.y, 1, mPaintM);

		// 绘制图标
		canvas.drawBitmap(mBitmaps[0], point.x - mBitmaps[0].getWidth() / 2,
				point.y - mBitmaps[0].getHeight(), mPaintM);

		// 绘制文本提示
		mDrawPopupUtilBeg.draw(canvas, point.x,
				point.y - mBitmaps[0].getHeight() + 4);
	}

	private void drawEndPoint(Canvas canvas, Point point) {

		// // 绘制圆圈
		// // canvas.drawCircle(point.x, point.y, 1, mPaintL);
		// canvas.drawCircle(point.x, point.y, 1, mPaintM);

		// 绘制图标
		canvas.drawBitmap(mBitmaps[1], point.x - mBitmaps[1].getWidth() / 2,
				point.y - mBitmaps[1].getHeight(), mPaintM);

		// 绘制文本提示
		mDrawPopupUtilEnd.draw(canvas, point.x,
				point.y - mBitmaps[1].getHeight() + 3);
	}

	private Rect mRectBeg = new Rect();
	private Rect mRectEnd = new Rect();

	@Override
	public boolean onTap(GeoPoint eventGeoPoint, MapView mapView) {
		if (mViewPointList != null && mViewPointList.size() > 1) {

			Point point = null;
			Point event = mMapView.getProjection()
					.toPixels(eventGeoPoint, null);

			point = mViewPointList.get(0);
			mRectBeg.set(point.x - mBitmaps[0].getWidth() / 2, point.y
					- mBitmaps[0].getHeight(),
					point.x + mBitmaps[0].getWidth(), point.y + 0);
			if (mDrawPopupUtilBeg.onTap(mRectBeg, event)) {
				// 刷新地图
				mMapView.invalidate();
				return true;
			}

			point = mViewPointList.get(mViewPointList.size() - 1);
			mRectEnd.set(point.x - mBitmaps[1].getWidth() / 2, point.y
					- mBitmaps[1].getHeight(),
					point.x + mBitmaps[1].getWidth(), point.y + 0);

			if (mDrawPopupUtilEnd.onTap(mRectEnd, event)) {
				// 刷新地图
				mMapView.invalidate();
				return true;
			}
		}
		return super.onTap(eventGeoPoint, mapView);

	}

	private OnOverlayEventListener mOnOverlayEventListener;

	public void setOnOverlayEventListener(
			OnOverlayEventListener onOverlayEventListener) {
		this.mOnOverlayEventListener = onOverlayEventListener;
	}

	private void reqAddrCode(GeoPoint geoPoint) {
		if (mOnOverlayEventListener != null)
			mOnOverlayEventListener.reverseGeocode(geoPoint, REVERSE_GEOCODE);
	}

	private static final int REQ_CODE_BEG = 0;
	private static final int REQ_CODE_END = 1;
	private int mReqCode;

	public void onGetAddrResult(MKAddrInfo addrInfo) {

		switch (mReqCode) {
		case REQ_CODE_BEG:
			mDrawPopupUtilBeg.setText(addrInfo.strAddr);
			mReqCode = REQ_CODE_END;
			reqAddrCode(getEnd());
			break;
		case REQ_CODE_END:
			mDrawPopupUtilEnd.setText(addrInfo.strAddr);
			mReqCode = REQ_CODE_BEG;
			break;
		}

		mMapView.postInvalidate();
	}
}
