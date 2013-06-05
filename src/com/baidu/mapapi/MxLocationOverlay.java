package com.baidu.mapapi;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.location.Location;

public class MxLocationOverlay extends MyLocationOverlay {
    public static final int REVERSE_GEOCODE = 0x0022;

    private DrawPopupUtil mDrawPopupUtil;
    private MapView mMapView;

    public MxLocationOverlay(Context arg0, MapView arg1) {
        super(arg0, arg1);
        this.mMapView = arg1;
        mDrawPopupUtil = new DrawPopupUtil(arg0);
        mRect = new Rect();
    }

    private OnOverlayEventListener mOnOverlayEventListener;

    public void setOnOverlayEventListener(
            OnOverlayEventListener onOverlayEventListener) {
        this.mOnOverlayEventListener = onOverlayEventListener;
    }

    private Rect mRect;

    // previous

    private GeoPoint mBaiduGeoPoint;
    private Point mCurrPoint;
    private int mRadius = 20;

    @Override
    protected void drawMyLocation(Canvas arg0, MapView arg1, Location arg2,
            GeoPoint arg3, long arg4) {
        try {
            mCurrPoint = arg1.getProjection().toPixels(mBaiduGeoPoint, null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        // arg0.drawCircle(mCurrPoint.x, mCurrPoint.y, mRadius, new Paint());
        super.drawMyLocation(arg0, arg1, arg2, arg3, arg4);

        if (mBaiduGeoPoint == null) {// /第一次，现请求

            mBaiduGeoPoint = new GeoPoint(arg3.getLatitudeE6(),
                    arg3.getLongitudeE6());
            reqAddrCode(mBaiduGeoPoint);
        } else if ((mBaiduGeoPoint.getLatitudeE6() - arg3.getLatitudeE6() ^ 2
                + (mBaiduGeoPoint.getLongitudeE6() - arg3.getLongitudeE6()) ^ 2) > 1 / 1e6) {

            mBaiduGeoPoint.setLatitudeE6(arg3.getLatitudeE6());
            mBaiduGeoPoint.setLongitudeE6(arg3.getLongitudeE6());
            reqAddrCode(mBaiduGeoPoint);
        }

        if (mCurrPoint != null) {
            // 绘制提示文本
            mDrawPopupUtil.draw(arg0, mCurrPoint.x, mCurrPoint.y - 12 + 1);
        }

    }

    private void reqAddrCode(GeoPoint geoPoint) {
        if (mOnOverlayEventListener != null)
            mOnOverlayEventListener.reverseGeocode(geoPoint, REVERSE_GEOCODE);
    }

    @Override
    public boolean onTap(GeoPoint eventGeoPoint, MapView mapView) {
        if (mCurrPoint != null) {
            Point event = mapView.getProjection().toPixels(eventGeoPoint, null);
           
            mRect.set(mCurrPoint.x - mRadius, mCurrPoint.y - mRadius,
                    mCurrPoint.x + mRadius, mCurrPoint.y + mRadius);

            if (mDrawPopupUtil.onTap(mRect, event)) {
                mapView.invalidate();
                return true;
            }
        }
        return super.onTap(eventGeoPoint, mapView);
    }

    public void onGetAddrResult(MKAddrInfo arg0) {
        System.out.println("Recv:" + arg0.strAddr);
        mDrawPopupUtil.setText(arg0.strAddr);
        mMapView.postInvalidate();
    }
}
