package com.kyletung.dotsprogressbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

/**
 * All rights reserved by Author<br>
 * Author: Dong YuHui<br>
 * Email: <a href="mailto:dyh920827@gmail.com">dyh920827@gmail.com</a><br>
 * Blog: <a href="http://www.kyletung.com">www.kyletung.com</a><br>
 * Create Time: 2016/03/28 at 21:15<br>
 * 用于分段显示进度的进度条
 */
public class DotsProgressBar extends View {

    /**
     * 进度条的点数
     */
    private int mDotsCount;

    /**
     * 进度条圆点的半径
     */
    private int mDotsRadius;

    /**
     * 进度条的宽度
     */
    private int mDotsProgressWidth;

    /**
     * 进度条宽度的一半
     */
    private int mDotsProgressWidthHalf;

    /**
     * 进度条的背景色
     */
    private int mDotsBackColor;

    /**
     * 进度条的前景色
     */
    private int mDotsFrontColor;

    /**
     * 进度条前进的速度
     */
    private int mSpeed;

    /**
     * 通过速度算出的时间最小单位
     */
    private int mSpeedTime;

    /**
     * 画笔
     */
    private Paint mPaint;

    /**
     * 原先的进度在某个点
     */
    private int mOldPosition = 1;

    /**
     * 新的进度在某个点
     */
    private int mNewPosition = 1;

    /**
     * 控件宽度
     */
    private int mWidth;

    /**
     * 控件高度
     */
    private int mHeight;

    /**
     * 每段矩形的长度
     */
    private int mPartWidth;

    /**
     * 表示每段动画已经进行的时间
     */
    private int mPartTime;

    public DotsProgressBar(Context context) {
        this(context, null);
    }

    public DotsProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DotsProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // 获取自定义属性
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DotsProgressBar);
        mDotsCount = typedArray.getInt(R.styleable.DotsProgressBar_barDotsCount, 2);
        mDotsRadius = typedArray.getDimensionPixelSize(R.styleable.DotsProgressBar_barDotsRadius, dp2px(8));
        mDotsProgressWidth = typedArray.getDimensionPixelSize(R.styleable.DotsProgressBar_barProgressWidth, dp2px(8));
        if ((2 * mDotsRadius) < mDotsProgressWidth)
            mDotsProgressWidth = mDotsRadius * 2; // 如果用户设置进度条的宽度大于点的直径，则设置为半径大小
        mDotsProgressWidthHalf = mDotsProgressWidth / 2;
        mSpeed = typedArray.getInt(R.styleable.DotsProgressBar_barSpeed, 100);
        mSpeedTime = 10 * 10 / mSpeed;
        mDotsBackColor = typedArray.getColor(R.styleable.DotsProgressBar_barBackColor, ContextCompat.getColor(context, android.R.color.darker_gray));
        mDotsFrontColor = typedArray.getColor(R.styleable.DotsProgressBar_barFrontColor, ContextCompat.getColor(context, android.R.color.holo_blue_light));
        typedArray.recycle();
        // 初始化画笔
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(width, mDotsRadius * 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        mPartWidth = (mWidth - mDotsRadius * 2) / (mDotsCount - 1);
        // 画背景
        mPaint.setColor(mDotsBackColor);
        canvas.drawRect(mDotsRadius, mHeight / 2 - mDotsProgressWidthHalf, mWidth - mDotsRadius, mHeight / 2 + mDotsProgressWidthHalf, mPaint);
        for (int i = 0; i < mDotsCount; i++) {
            canvas.drawCircle(mDotsRadius + mPartWidth * i, mDotsRadius, mDotsRadius, mPaint);
        }
        // 调整画笔为前景色
        mPaint.setColor(mDotsFrontColor);
        // 画切换过程中不变的部分
        drawNotChange(canvas, mPaint, Math.min(mOldPosition, mNewPosition));
        // 画变化的部分
        int start = (mOldPosition - 1) * mPartWidth + mDotsRadius;
        if (mNewPosition > mOldPosition) {
            // 进度条向前
            mPartTime = mPartTime + mSpeedTime;
            drawForward(canvas, mPaint, start, mPartTime);
            if (mPartTime < (mSpeedTime * 100)) {
                postInvalidateDelayed(mSpeedTime);
            } else {
                mOldPosition = mNewPosition;
            }
        }
    }

    /**
     * 绘制在变化过程中仍然不变的前景部分
     *
     * @param canvas   画布
     * @param paint    画笔
     * @param position 不变的位置
     */
    private void drawNotChange(Canvas canvas, Paint paint, int position) {
        if (position != 0) {
            // 画矩形
            if (position - 1 != 0) {
                canvas.drawRect(mDotsRadius, mHeight / 2 - mDotsProgressWidthHalf, mDotsRadius + mPartWidth * (position - 1), mHeight / 2 + mDotsProgressWidthHalf, mPaint);
            }
            // 画圆
            for (int i = 0; i < position; i++) {
                canvas.drawCircle(mDotsRadius + mPartWidth * i, mDotsRadius, mDotsRadius, paint);
            }
        }
    }

    private void drawForward(Canvas canvas, Paint paint, int start, int time) {
        if (time < (mSpeedTime * 90)) {
            // 画矩形
            int rectWidth = time * mPartWidth / (mSpeedTime * 90);
            canvas.drawRect(start, mHeight / 2 - mDotsProgressWidthHalf, start + rectWidth, mHeight / 2 + mDotsProgressWidthHalf, paint);
        } else {
            // 画矩形和圆
            int radius = ((time - mSpeedTime * 90) / (mSpeedTime * 10)) * mDotsRadius;
            canvas.drawRect(start, mHeight / 2 - mDotsProgressWidthHalf, start + mPartWidth, mHeight / 2 + mDotsProgressWidthHalf, paint);
            canvas.drawCircle(start + mPartWidth, mHeight / 2, radius, paint);
        }
    }

    /**
     * 进度条向前进一
     */
    public void startForward() {
        if (mOldPosition < mDotsCount) {
            mNewPosition = mOldPosition + 1;
            mPartTime = 0;
            invalidate();
        }
    }

    /**
     * 通过该方法返回需要绘制的进度条的长度及圆点的半径
     * @return 返回一个整型数组，数组第一个表示进度条的长度，数组第二个表示圆点的半径
     */
    private int[] getParams() {
        final int[] params = new int[2];
        return params;
    }

    /**
     * dp 转 px
     *
     * @param dpValue dp 值
     * @return 返回 px 值
     */
    private int dp2px(int dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}