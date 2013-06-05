package com.baidu.mapapi;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.NinePatch;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

public class DrawPopupUtil {
    public DrawPopupUtil(Context context) {
        Activity activity = (Activity) context;
        mPaint = new Paint();
        {
            mPaint.setAntiAlias(true);
            mPaint.setTextSize(18);
            mPaint.setColor(Color.BLACK);
        }

        FontMetrics fontMetrics = mPaint.getFontMetrics();// 得到系统默认字体属性
        {
            mFontHeight = (float) (Math.ceil(fontMetrics.descent
                    - fontMetrics.top) + 2);// 获得字体高度
        }
        {
            Bitmap bitmap = new BitmapHelper(activity)
                    .getBitmapFromAssets("zoom_popup_button_pressed.9.png");

            mNinePatch = new NinePatch(bitmap, bitmap.getNinePatchChunk(), null);
        }
        mPoint = new Point();
        mRectF = new RectF();
    }

    private Paint mPaint;

    private NinePatch mNinePatch;

    private float mFontHeight;
    private float[] mTextWidths;

    private int mTextHeight;
    private int mTextWidth;

    private float getTotalTextWidth() {
        if (mTextWidths == null)
            return 0;

        float fResult = 0f;
        for (int i = 0; i < mTextWidths.length; i++) {
            fResult += mTextWidths[i];
        }
        return fResult;
    }

    private float getMaxTextWidth() {
        if (mTextWidths == null)
            return 0;

        float fResult = 0f;
        for (int i = 0; i < mTextWidths.length; i++) {
            if (mTextWidths[i] > fResult) {
                fResult = mTextWidths[i];
            }
        }
        return fResult;
    }

    private boolean mIsShow;
    private String mText;
    private Point mPoint;
    private RectF mRectF;

    public void setText(String text) {
        this.mText = text;

        if (text != null) {
            this.mTextWidths = new float[text.length()];
            this.mPaint.getTextWidths(text, this.mTextWidths);// 获取文字的宽度

            float mTextTotalArea = mFontHeight * getTotalTextWidth();// 字体的总面积

            // 根据屏幕比例，字所占的面积来计算 文本区域的大概宽度
            double refWidth = Math.sqrt(mTextTotalArea * 5 / 3);// mDisplayWidth/
                                                                // mDisplayHeight
            float maxTextWidth = getMaxTextWidth();// 找出最宽的字符
            int colMaxCount = (int) (refWidth / maxTextWidth);// 计算文本宽可容纳最宽字符数量
            this.mTextWidth = Math.round(colMaxCount * maxTextWidth);// 四舍五入文本区域的宽度
            this.mTextHeight = Math.round(getTextRowCount() * mFontHeight);// 四舍五入文本区域的高度

        }
    }

    private int mErrorRange = 10;// 容错范围
    private int mBgPaddingLR = 13;// 背景左右内间距
    // private int mArrowHeight = 7;// 箭头的高度
    private int mBgPaddingTB = mBgPaddingLR + 7;// 背景上下内间距

    protected void draw(Canvas canvas, int x, int y) {
        if (mIsShow && mText != null) {

            // 计算偏移后的坐标
            mPoint.set(x - mTextWidth / 2, y - mTextHeight - 12);

            // 计算矩形的大小
            mRectF.set(mPoint.x - mBgPaddingLR, mPoint.y - mBgPaddingLR,
                    mPoint.x + mBgPaddingLR + mTextWidth, mPoint.y
                            + mBgPaddingTB + mTextHeight);

            // 绘制文本区域背景
            mNinePatch.draw(canvas, mRectF);

            // 绘制文本区域
            drawText(canvas);
        }
    }

    public boolean onTap(Rect rect, Point event) {
        if (event.x >= rect.left - mErrorRange
                && event.x <= rect.right + mErrorRange
                && event.y >= rect.top - mErrorRange
                && event.y <= rect.bottom + mErrorRange) {
            return mIsShow = true;
        } else {
            return mIsShow = false;
        }
    }

    private int getTextRowCount() {
        int iResult = 0;
        int dx = 0;
        for (int i = 0; i < mText.length(); i++) {
            if (i > 0) {
                dx += (int) mTextWidths[i - 1];
            } else {
                iResult = 1;
            }
            if (dx + mTextWidths[i] > mTextWidth) {
                dx = 0;
                iResult++;
            }
        }
        return iResult;
    }

    private void drawText(Canvas canvas) {
        int dx = 0;
        int dy = (int) (mFontHeight * 4 / 7);
        for (int i = 0; i < mText.length(); i++) {
            if (i > 0) {
                dx += (int) mTextWidths[i - 1];
            }
            if (dx + mTextWidths[i] > mTextWidth) {
                dx = 0;
                dy += mFontHeight;
            }
            canvas.drawText("" + mText.charAt(i), mPoint.x + dx, mPoint.y + dy,
                    mPaint);
        }
    }

}
