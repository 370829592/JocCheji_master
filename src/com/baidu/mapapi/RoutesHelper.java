package com.baidu.mapapi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.icalinks.common.MapsHelper;
import com.icalinks.obd.vo.GPSInfo;

import android.graphics.Point;

public class RoutesHelper {

	/**
	 * 从返回数据中，解析Wgs84轨迹坐标
	 * 
	 * @param lstWgs84HashMap
	 * @return
	 */
	public static ArrayList<GeoPoint> getWgs84GeoPoint(
			List<GPSInfo> lstWgs84HashMap) {

		ArrayList<GeoPoint> lstResult = new ArrayList<GeoPoint>();
		try {
			HashMap hashMap = null;
			GeoPoint wgs84GeoPoint = null;
			int size = lstWgs84HashMap.size();
			for (int i = 0; i < size; i++) {
				GPSInfo gPSInfo = lstWgs84HashMap.get(i);
				wgs84GeoPoint = new GeoPoint(
						// (int) (Double.parseDouble("" +
						// hashMap.get("latitude")) * 1E6),
						// (int) (Double.parseDouble("" +
						// hashMap.get("longitude")) * 1E6)
						(int) (Double.parseDouble("" + gPSInfo.getLatitude()) * 1E6),
						(int) (Double.parseDouble("" + gPSInfo.getLongitude()) * 1E6));

				lstResult.add(wgs84GeoPoint);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return lstResult;
	}

	/**
	 * 角度差范围
	 */
	private static double mAngleDiff = Math.PI / 180 / 2;

	/**
	 * 最大距离范围
	 */
	private static double mDistanceDiff = 1000;

	/**
	 * 从Wgs84轨迹坐标中，筛选出特征点数据
	 * 
	 * @param lstWgs84HashMap
	 * @return
	 */
	public static ArrayList<GeoPoint> getBaiduCompGeoPoint(
			ArrayList<GeoPoint> lstWgs84GeoPoint) {
		ArrayList<GeoPoint> lstWgs84CompGeoPoint = new ArrayList<GeoPoint>();

		if (lstWgs84GeoPoint.size() > 2) {
			// 压缩原始轨迹数据
			GeoPoint gp0 = lstWgs84GeoPoint.get(0);
			GeoPoint gp1 = lstWgs84GeoPoint.get(1);
			GeoPoint gp2 = null;

			double angle0 = getAngle(gp0, gp1);
			double angle1 = 0;

			int size = lstWgs84GeoPoint.size();
			lstWgs84CompGeoPoint.add(gp0);

			// double tmpDistance = 0;
			for (int i = 2; i < size; i++) {

				gp2 = lstWgs84GeoPoint.get(i);
				angle1 = getAngle(gp0, gp2);
				// tmpDistance = getDistance(gp0, gp1);
				// System.out.println("Distance:" + tmpDistance);
				if (MapsHelper.getDistance(gp0, gp1) > mDistanceDiff) {
					lstWgs84CompGeoPoint.add(gp1);

					gp0 = gp1;
					gp1 = gp2;

					angle0 = getAngle(gp0, gp1);
				} else if (Math.abs(angle0 - angle1) > mAngleDiff) {
					lstWgs84CompGeoPoint.add(gp1);

					gp0 = gp1;
					gp1 = gp2;

					angle0 = getAngle(gp0, gp1);

				} else {
					gp1 = gp2;
					angle0 = angle1;
				}
			}
			lstWgs84CompGeoPoint.add(lstWgs84GeoPoint.get(size - 1));

		} else {
			lstWgs84CompGeoPoint.add(lstWgs84GeoPoint.get(0));
		}
		// 把压缩后的轨迹数据转换为百度坐标
		// return getBaiduGeoPoint(lstWgs84GeoPoint);
		return getBaiduGeoPoint(lstWgs84CompGeoPoint);
	}

	private static double getAngle(GeoPoint gp0, GeoPoint gp1) {
		return Math.atan2(gp1.getLongitudeE6() - gp0.getLongitudeE6(),
				gp1.getLatitudeE6() - gp0.getLatitudeE6());
	}



	/**
	 * 转换Wgs84坐标到Baidu坐标
	 * 
	 * @param lstWgs84GeoPoint
	 * @return
	 */
	public static ArrayList<GeoPoint> getBaiduGeoPoint(
			ArrayList<GeoPoint> lstWgs84GeoPoint) {
		ArrayList<GeoPoint> lstResult = new ArrayList<GeoPoint>();
		int size = lstWgs84GeoPoint.size();
		for (int i = 0; i < size; i++) {
			lstResult.add(CoordinateConvert.bundleDecode(CoordinateConvert
					.fromWgs84ToBaidu(lstWgs84GeoPoint.get(i))));
		}

		return lstResult;
	}

	/**
	 * 获取屏幕坐标(根据GPS坐标)
	 * 
	 * @return
	 */

	public static ArrayList<Point> getBaiduViewPoint(MapView mapView,
			ArrayList<GeoPoint> lstBaiduGeoPoint) {
		ArrayList<Point> lstResult = new ArrayList<Point>();
		int size = lstBaiduGeoPoint.size();
		for (int i = 0; i < size; i++) {
			lstResult.add(mapView.getProjection().toPixels(
					lstBaiduGeoPoint.get(i), null));
		}
		return lstResult;
	}

	/**
	 * 获取需要显示的屏幕坐标(GGG重点优化点)
	 * 
	 * @return
	 */
	public static ArrayList<Point> getBaiduShowPoint(MapView mapView,
			ArrayList<Point> lstBaiduViewPoint) {

		// // test
		return lstBaiduViewPoint;

		// // 计算那些有效
		// ArrayList<Point> lstPoint = new ArrayList<Point>();
		// ArrayList<Boolean> lstIsValid = new ArrayList<Boolean>();
		// Point point = null;
		// int size = lstBaiduViewPoint.size();
		// for (int i = 0; i < size; i++) {
		// point = lstBaiduViewPoint.get(i);
		// lstPoint.add(point);
		//
		// // 超出范围的不绘（无效）
		// if (point.x < 0 || point.y < 0 || point.x > mapView.getWidth()
		// || point.y > mapView.getHeight()) {
		// lstIsValid.add(false);
		// } else {
		// lstIsValid.add(true);
		// }
		// }
		// // 得到原始数据
		// ArrayList<Point> lstResult = new ArrayList<Point>();
		// for (int i = 0; i < size; i++) {
		// if (lstIsValid.get(i)) {
		// if (i > 0) {
		// // 不在列表中
		// if (!lstResult.contains(lstPoint.get(i - 1))) {
		// // 没有在视图范围内
		// if (!lstIsValid.get(i - 1)) {
		// lstResult.add(lstPoint.get(i - 1));
		// }
		// }
		// }
		// lstResult.add(lstPoint.get(i));
		// } else {
		// if (i > 0) {
		// if (lstIsValid.get(i - 1)) {
		// lstResult.add(lstPoint.get(i));
		// }
		// }
		// }
		// }
		// return lstResult;
	}

	private static int mMapViewZoomLevel;
	private static GeoPoint mMapViewMapCenter;
	private static ArrayList<GeoPoint> mWgs84GeoPointList;
	private static ArrayList<GeoPoint> mBaiduCompGeoPointList;

	public static ArrayList<GeoPoint> getCacheBaiduCompGeoPoint(
			ArrayList<GeoPoint> lstWgs84GeoPoint) {
		synchronized (lstWgs84GeoPoint) {
			if (!lstWgs84GeoPoint.equals(mWgs84GeoPointList)) {
				mWgs84GeoPointList = lstWgs84GeoPoint;
				mBaiduCompGeoPointList = getBaiduCompGeoPoint(lstWgs84GeoPoint);
				// getBaiduGeoPoint(mWgs84GeoPointList);
			}
		}
		return mBaiduCompGeoPointList;
	}

	private synchronized static boolean isBaiduMapShowChanged(MapView mapView,
			ArrayList<GeoPoint> lstWg84GeoPoint) {
		if (mMapViewZoomLevel != mapView.getZoomLevel())
			return true;
		if (!mapView.getMapCenter().equals(mMapViewMapCenter))
			return true;
		if (lstWg84GeoPoint.equals(mWgs84GeoPointList))
			return true;
		// /mapView.getMeasuredHeight()

		return false;
	}

	private static ArrayList<Point> mBaiduCompViewPointList;

	public static ArrayList<Point> getCacheBaiduCompViewPoint(MapView mapView,
			ArrayList<GeoPoint> lstWgs84GeoPoint) {
		if (isBaiduMapShowChanged(mapView, lstWgs84GeoPoint)) {

			mBaiduCompViewPointList = getBaiduViewPoint(mapView,
					getCacheBaiduCompGeoPoint(lstWgs84GeoPoint));
		}
		return mBaiduCompViewPointList;
	}

	private static ArrayList<Point> mBaiduCompShowPointList;

	public static ArrayList<Point> getCacheBaiduShowPoint(MapView mapView,
			ArrayList<GeoPoint> lstWg84GeoPoint) {
		if (isBaiduMapShowChanged(mapView, lstWg84GeoPoint)) {
			mBaiduCompShowPointList = getBaiduShowPoint(mapView,
					getCacheBaiduCompViewPoint(mapView, lstWg84GeoPoint));
		}
		return mBaiduCompShowPointList;
	}
}

// public static ArrayList<RouteItem> getRoute(List<HashMap> lstGpsInfo) {
// ArrayList<RouteItem> lstItem = new ArrayList<RouteItem>();
// HashMap<String, Object> maptmp = null;
// RouteItem steptmp = null;
// int size = lstGpsInfo.size();
// DateTime datetime = DateTime.now();//
// GeoPoint wgs84GeoPoint = null;
// GeoPoint baiduGeoPoint = null;
// for (int i = 0; i < size; i++) {
// maptmp = lstGpsInfo.get(i);
//
// wgs84GeoPoint = new GeoPoint((int) (Double.parseDouble(maptmp.get(
// "latitude").toString()) * 1E6),
// (int) (Double.parseDouble(maptmp.get("longitude")
// .toString()) * 1E6));
//
// baiduGeoPoint = CoordinateConvert.bundleDecode(CoordinateConvert
// .fromWgs84ToBaidu(wgs84GeoPoint));
//
// steptmp = new RouteItem(datetime, baiduGeoPoint, 0);
// lstItem.add(steptmp);
// }
// // // getRoute(lstItem);// ggz:test
// // outputData2SDCardFile(lstItem);
// return lstItem;
// }

// private static void outputData2SDCardFile(ArrayList<RouteItem> lstItem) {
// try {
// StringBuilder sb = new StringBuilder();
// sb.append("latitude,longitude\n");
// int size = lstItem.size();
// RouteItem item = null;
// for (int i = 0; i < size; i++) {
// item = lstItem.get(i);
// sb.append(item.getPosition().getLatitudeE6());
// sb.append(",");
// sb.append(item.getPosition().getLongitudeE6());
// sb.append("\n");
// }
// String data = sb.toString();
// File file = new File("/sdcard/route_"
// + DateTime.now().toString("HHmmss") + ".csv");
//
// FileOutputStream out = null;
// try {
// out = new FileOutputStream(file);
// } catch (FileNotFoundException e) {
// // TODO Auto-generated catch block
// e.printStackTrace();
// }
// OutputStreamWriter osw = new OutputStreamWriter(out);
// osw.write(data);
// osw.flush();
// osw.close();
//
// } catch (IOException e) {
// e.printStackTrace();
// }
// }
// public static ArrayList<RouteItem> getRoute(ArrayList<RouteItem>
// oldRoteList) {
// if (oldRoteList == null || oldRoteList.size() < 2) {
// return null;
// }
//
// ArrayList<RouteItem> newRouteList = new ArrayList<RouteItem>();
// {
// ArrayList<Double> lstSlope = new ArrayList<Double>();
// int size = oldRoteList.size();
// GeoPoint GeoPoint0 = null;
// GeoPoint GeoPoint1 = null;
// double slope = 0;
// double diffX = 0;
// double diffY = 0;
// for (int i = 1; i < size; i++) {
// GeoPoint0 = oldRoteList.get(i - 1).getPosition();
// GeoPoint1 = oldRoteList.get(i).getPosition();
// diffY = (GeoPoint1.getLatitudeE6() - GeoPoint0.getLatitudeE6());
// diffX = (GeoPoint1.getLongitudeE6() - GeoPoint0.getLongitudeE6());
// if (diffX != 0) {
// slope = diffY / diffX;
// lstSlope.add(slope);
// } else {
// // slope = 0;
// lstSlope.add(null);
// }
//
// }
// int slopeSize = lstSlope.size();
// for (int i = 0; i < slopeSize; i++) {
// System.out.println(lstSlope.get(i));
// }
// }
// return newRouteList;
// }

// public static MKRoute getRoutesRote(List<HashMap> lstGpsInfo) {
// MKRoute mkroute = new MKRoute();
// {
// mkroute.b(MKRoute.ROUTE_TYPE_DRIVING);
//
// ArrayList<MKStep> lstMkStep = getStepList(lstGpsInfo);
// mkroute.a(lstMkStep);
//
// mkroute.a(lstMkStep.get(0).getPoint());// 起点
// mkroute.b(lstMkStep.get(lstMkStep.size() - 1).getPoint());// 终点
// }
// return mkroute;
// }

// private static ArrayList<MKStep> getStepList(List<HashMap> lstGpsInfo)//
// = new ArrayList<MKStep>();
// {
// ArrayList<MKStep> lstMkStep = new ArrayList<MKStep>();
//
// HashMap<String, Double> maptmp = null;
// MKStep steptmp = null;
// for (int i = 0; i < lstGpsInfo.size(); i++) {
// maptmp = lstGpsInfo.get(i);
// steptmp = new MKStep();
// {
// steptmp.a(new GeoPoint(maptmp.get("latitude"), maptmp.get("longitude")));
// // steptmp.a(new
// GeoPoint(Double.parseDouble(maptmp.get("latitude").toString()),
// Double.parseDouble(maptmp.get(
// // "longitude").toString())));
// steptmp.a(DateTime.now().toString());
// }
// lstMkStep.add(steptmp);
// }
// return lstMkStep;
// }

// public static ArrayList<ArrayList<RouteItem>> getRouteList(List<HashMap>
// lstGpsInfo) {
// ArrayList<ArrayList<RouteItem>> mRouteList = new
// ArrayList<ArrayList<RouteItem>>();
// {
// ArrayList<RouteItem> lstItem = new ArrayList<RouteItem>();
// HashMap<String, Double> maptmp = null;
// RouteItem steptmp = null;
// for (int i = 0; i < lstGpsInfo.size(); i++) {
// maptmp = lstGpsInfo.get(i);
// DateTime dt = DateTime.now();
// steptmp = new RouteItem(dt.toString(), maptmp.get("latitude"),
// maptmp.get("longitude"), 0);
// lstItem.add(steptmp);
// }
// mRouteList.add(lstItem);
// }
// return mRouteList;
// }
