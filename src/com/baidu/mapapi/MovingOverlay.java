package com.baidu.mapapi;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

public class MovingOverlay extends Overlay implements Runnable {
    private Thread mThread;

    private MapView mMapView;
    private Paint mPaint;
    private Bitmap mCarIcon_P;
    private Bitmap mCarIcon_N;

    public MovingOverlay(Activity activity, MapView mapView) {
        this.mMapView = mapView;
        this.mPaint = new Paint();
        {
            this.mPaint.setAntiAlias(true);
        }

        BitmapHelper bitmapHelper = new BitmapHelper(activity);
        {
            this.mCarIcon_P = bitmapHelper
                    .getBitmapFromAssets("rmct_routes_car_p.png");
            this.mCarIcon_N = bitmapHelper
                    .getBitmapFromAssets("rmct_routes_car_n.png");
        }
    }

    // private ArrayList<GeoPoint> mWgs84GeoPointList;
    private ArrayList<GeoPoint> mBaiduGeoPointList;
    private Point mCurrViewPoint;
    private int mCurrViewIndex;

    public void setData(ArrayList<GeoPoint> lstWgs84GeoPoint) {
        // // 从缓存中得到百度坐标
        mBaiduGeoPointList = RoutesHelper.getBaiduGeoPoint(lstWgs84GeoPoint);

        // 开始移动了
        startMoving();
    }

    /**
     * 绘制移动中的车
     */
    @Override
    public void draw(Canvas canvas, MapView mapView, boolean arg2) {
        if (mBaiduGeoPointList != null) {
            // 计算当前移动位置在屏幕上的坐标
            mCurrViewPoint = mapView.getProjection().toPixels(
                    mBaiduGeoPointList.get(mCurrViewIndex), null);

            // 绘制移动中的车
            if (mIsMoving) {
                canvas.drawBitmap(mCarIcon_P,
                        mCurrViewPoint.x - mCarIcon_P.getWidth() / 2,
                        mCurrViewPoint.y - mCarIcon_P.getHeight(), mPaint);
            } else {
                canvas.drawBitmap(mCarIcon_N,
                        mCurrViewPoint.x - mCarIcon_N.getWidth() / 2,
                        mCurrViewPoint.y - mCarIcon_N.getHeight(), mPaint);
            }

            // 判断地图需不需要移动
            if (mIsMoving) {
                if (mCurrViewPoint.x - mCarIcon_P.getWidth() / 2 < 0 + mErrorRange
                        || mCurrViewPoint.y - mCarIcon_P.getHeight() < 0 + mErrorRange
                        || mCurrViewPoint.x + mCarIcon_P.getWidth() / 2 > mMapView
                                .getWidth() - mErrorRange
                        || mCurrViewPoint.y > mMapView.getHeight()
                                - mErrorRange) {

                    // 地图居中位置移动
                    mMapView.getController().setCenter(
                            mBaiduGeoPointList.get(mCurrViewIndex));
                }
            }
        }
    }

    private void startMoving() {
        // 设置车辆位置坐标索引为最初值
        mCurrViewIndex = -1;
        mIsMoving = true;
        if (mThread != null) {
            if (!mThread.isAlive()) {
                mThread.start();
            }
        } else {
            mThread = new Thread(this);
            mThread.start();
        }
    }

    public void run() {
        while (mBaiduGeoPointList != null) {

            if (mIsMoving) {
                mCurrViewIndex++;
                if (mCurrViewIndex == mBaiduGeoPointList.size()) {
                    mCurrViewIndex = 0;
                    // 终止这个回放的线程
                    mBaiduGeoPointList = null;
                    mMapView.postInvalidate();
                    break;
                }
                mMapView.postInvalidate();
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean mIsMoving;
    private int mErrorRange = 10;

    @Override
    public boolean onTap(GeoPoint arg0, MapView arg1) {
        if (mCurrViewPoint != null) {
            Point eventPoint = mMapView.getProjection().toPixels(arg0, null);

            if (eventPoint.x >= mCurrViewPoint.x - 16 - mErrorRange
                    && eventPoint.x <= mCurrViewPoint.x + 16 + mErrorRange
                    && eventPoint.y >= mCurrViewPoint.y - 51 - mErrorRange
                    && eventPoint.y <= mCurrViewPoint.y + 13 + mErrorRange) {

                mIsMoving = !mIsMoving;

                return true;
            }
        }
        return super.onTap(arg0, arg1);
    }

    /**
     * 恢复绘行车线程
     */
    public void onResume() {
        // mIsMoving = true;
        // synchronized (mThread) {
        // mThread.notify();
        // }
        // mMapView.invalidate();
    }

    /**
     * 挂起绘行车线程
     */
    public void onPause() {
        // mIsMoving = false;
        // try {
        // synchronized (mThread) {
        // mThread.wait();
        // }
        // } catch (InterruptedException e) {
        // e.printStackTrace();
        // }
        // mMapView.invalidate();
    }

    /**
     * 释放当前控件
     */
    public void onDestroy() {
        mBaiduGeoPointList = null;// 结束线程循环
    }
}
