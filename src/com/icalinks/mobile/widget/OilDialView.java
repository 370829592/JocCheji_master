//package com.icalinks.mobile.widget;
//
//import android.content.Context;
//import android.content.res.Configuration;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Canvas;
//import android.graphics.Matrix;
//import android.graphics.Paint;
//import android.graphics.PaintFlagsDrawFilter;
//import android.graphics.Rect;
//import android.util.AttributeSet;
//import android.view.MotionEvent;
//import android.view.View;
//
//import com.icalinks.jocyjt.R;
//import com.icalinks.mobile.GlobalApplication;
//
///**
// * 瞬时油耗
// * 
// * @author zg_hu@icalinks.com.cn
// * 
// */
//
//public class OilDialView extends View {
//	public static final String TAG = OilDialView.class.getSimpleName();
//	private PaintFlagsDrawFilter pfd;
//
//	private Bitmap dialBitmap;
//	private Bitmap needleBootBitmap;
//	private Bitmap needleBitmap;
//
//	private int resDial;
//	private int resNeedle;
//	private int resNeedleBoot;
//
//	private Matrix needleMatrix;
//	private float needleRotation;
//	// ���滻������
//	private float minRotation;
//	private float maxRotation;
//
//	private float rateRotation;
//
//	private float min;
//	private float max;
//
//	private int windowWidth;
//
//	/**
//	 * ����ȡ���
//	 */
//	private int halfWidth;
//
//	/**
//	 * Բ�ĵ�ֱ��
//	 */
//	private int centerWidth;
//	/**
//	 * ָ��Ŀ��
//	 */
//	private int needleWidth;
//	/**
//	 * ���̵�ֱ��
//	 */
//	private int dialDiameter;
//
//	double excursionX = 0.0;
//	double excursionY = 0.0;
//
//	private int MARGIN = 0;// 20
//
//	public OilDialView(Context context, AttributeSet attrs) {
//		super(context, attrs);
//		// TODO Auto-generated constructor stub
//		pfd = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG
//				| Paint.FILTER_BITMAP_FLAG);
//
//		needleRotation = attrs
//				.getAttributeFloatValue(null, "defRotation", 0.0f);
//
//		resDial = attrs.getAttributeResourceValue(null, "srcDial",
//				R.drawable.info_rt_rt);
//		resNeedle = attrs.getAttributeResourceValue(null, "srcNeedle",
//				R.drawable.info_rt_rt_needle);
//
//		resNeedleBoot = attrs.getAttributeResourceValue(null, "srcNeedleBoot",
//				R.drawable.info_rt_rt_needle_boot);
//
//		minRotation = attrs.getAttributeFloatValue(null, "minRotation", 0.0f);
//		maxRotation = attrs.getAttributeFloatValue(null, "maxRotation", 0.0f);
//		min = attrs.getAttributeFloatValue(null, "min", 0.0f);
//		max = attrs.getAttributeFloatValue(null, "max", 0.0f);
//
//		init(context);
//
//	}
//
//	private int mXwidth;
//	private int mXheight;
//
//	private void init(Context context) {
//		windowWidth = GlobalApplication.getApplication().getWinWidth();
//
//		needleBootBitmap = BitmapFactory.decodeResource(context.getResources(),
//				resNeedleBoot);
//		needleBitmap = BitmapFactory.decodeResource(context.getResources(),
//				resNeedle);
//
//		dialBitmap = BitmapFactory.decodeResource(context.getResources(),
//				resDial);
//
//		mXwidth = dialBitmap.getWidth() ;//* 2 / 3;
//		mXheight = dialBitmap.getHeight() ;//* 2 / 3;
//		// halfWidth = windowWidth / 2;
//		halfWidth = mXwidth;
//
//		dialDiameter = mXwidth;
//		centerWidth = needleBootBitmap.getWidth();
//		needleWidth = needleBitmap.getWidth();
//
//		needleMatrix = new Matrix();
//
//		initRotation();
//
//		initRate();
//	}
//
//	/**
//	 * ����ڴ�ֱ���µĽǶ����
//	 */
//	private void initRotation() {
//
//	}
//
//	private void initRate() {
//		rateRotation = (maxRotation - minRotation) / (max - min);
//	}
//
//	@Override
//	protected void onDraw(Canvas canvas) {
//		// TODO Auto-generated method stub
//
//		super.onDraw(canvas);
//		int mCurrentOrientation = getResources().getConfiguration().orientation;
//		if (mCurrentOrientation == Configuration.ORIENTATION_PORTRAIT) {
//			drawPictures(canvas);
//		} else if (mCurrentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
//			drawPictures(canvas);
//		}
//
//	}
//
//	private void drawPictures(Canvas canvas) {
//		Paint mPaint = new Paint();
//		mPaint.setAntiAlias(true);
//
//		Rect src = new Rect();
//		src.left = 0;
//		src.top = 0;
//		src.right = mXwidth;
//		src.bottom = mXheight;
//
//		Rect dst = new Rect();
//		dst.left = halfWidth - mXwidth;
//		dst.top = halfWidth - mXheight - MARGIN;
//		dst.right = halfWidth;
//		dst.bottom = halfWidth - MARGIN;
//
//		canvas.drawBitmap(dialBitmap, src, dst, mPaint);
//
//		if (needleRotation == 0) {
//			excursionX = needleWidth / 2;
//			excursionY = 0.0f;
//		} else if (needleRotation == 180) {
//			excursionX = -(needleWidth / 2);
//			excursionY = 0.0f;
//		} else if (needleRotation == 90) {
//			excursionX = 0.0f;
//			excursionY = needleWidth / 2;
//		} else if (needleRotation == 270) {
//			excursionX = 0.0f;
//			excursionY = -1 * (needleWidth / 2);
//		} else {
//			// 0-90, 90-180, 180-270, 270-360
//
//			excursionX = (float) ((needleWidth / 2) * Math.cos(needleRotation
//					* Math.PI / 180));
//			excursionY = (float) ((needleWidth / 2) * Math.sin(needleRotation
//					* Math.PI / 180));
//		}
//
//		needleMatrix.postRotate(needleRotation);
//		needleMatrix.postTranslate((float) (halfWidth - mXwidth + dialDiameter
//				/ 2 - excursionX), (float) (halfWidth - mXheight - MARGIN
//				+ dialDiameter / 2 - excursionY));
//
//		canvas.setDrawFilter(pfd);
//		canvas.drawBitmap(needleBitmap, needleMatrix, mPaint);
//		canvas.setDrawFilter(pfd);
//
//		needleMatrix
//				.postTranslate(
//						-1
//								* (float) (halfWidth - mXwidth + dialDiameter
//										/ 2 - excursionX), -1
//								* (float) (halfWidth - mXheight - MARGIN
//										+ dialDiameter / 2 - excursionY));
//
//		needleMatrix.postRotate(-1.0f * needleRotation);
//
//		// 圆心
//		canvas.drawBitmap(needleBootBitmap, (halfWidth - dialDiameter)
//				+ dialDiameter / 2 - centerWidth / 2, halfWidth - mXheight
//				+ dialDiameter / 2 - centerWidth / 2 - MARGIN, mPaint);
//
//		// needleRotation++;
//	}
//
//	@Override
//	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		// TODO Auto-generated method stub
//		setMeasuredDimension(halfWidth, halfWidth - MARGIN);
//	}
//
//	/**
//	 * ���ýǶ�
//	 * 
//	 * @param value
//	 *            ֵ
//	 * @param flag
//	 *            ���
//	 */
//	public void setRotation(float value, int flag) {
//		// Log.e(TAG, "flag : " + flag + ", value = " + value);
//
//		if (value >= min && value <= max) {
//			needleRotation = getRotation(value, flag);
//			// Log.e(TAG, "needleRotation = " + needleRotation);
//			invalidate();
//		} else {
//			return;
//		}
//
//	}
//
//	private float getRotation(float value, int flag) {
//		needleRotation = minRotation + rateRotation * value;
//
//		return needleRotation;
//	}
//
//	public void setRotation(float needleRotation) {
//		this.needleRotation = needleRotation;
//		invalidate();
//	}
//
//	public float getRotation() {
//		return needleRotation;
//	}
//
//	@Override
//	public boolean onTouchEvent(MotionEvent event) {
//		// TODO Auto-generated method stub
//		return true;
//	}
//
//	/**
//	 * 
//	 * @param context
//	 * @param resOil
//	 */
//	public void setDailImage(Context context, int resOil[]) {
//		this.resDial = resOil[0];
//		this.resNeedle = resOil[1];
//		this.resNeedleBoot = resOil[2];
//
//		dialBitmap = BitmapFactory.decodeResource(context.getResources(),
//				resDial);
//
//		needleBitmap = BitmapFactory.decodeResource(context.getResources(),
//				resNeedle);
//
//		needleBootBitmap = BitmapFactory.decodeResource(context.getResources(),
//				resNeedleBoot);
//
//		invalidate();
//
//	}
//
//}