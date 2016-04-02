package com.kyletung.dotsprogressbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

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
     * 目前已经进行的时间
     */
    private int mPartTime;

    /**
     * 画笔
     */
    private Paint mPaint;

    /**
     * 原先的进度在某个点
     */
    private int mOldPosition = 0;

    /**
     * 新的进度在某个点
     */
    private int mNewPosition = 0;

    /**
     * 每段矩形的长度
     */
    private int mPartWidth;

    /**
     * 使用插值器来获得进度值，这样不仅简化了获取进度值的过程，还可以向外提供可定制性
     */
    private Interpolator mInterpolator;

    /**
     * 可以设置一个 ViewPager 与进度条同步
     */
    private ViewPager mViewPager;

    /**
     * 用于存放需要绘制的最新坐标
     */
    private int[] mParams;

    /**
     * 标记进度条是否在动画中
     */
    private boolean mIsRunning = false;

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
        // 获取用户定义的时间，并转化成时间间隔
        mSpeed = typedArray.getInt(R.styleable.DotsProgressBar_barSpeed, 40);
        // 获取用户定义的进度条背景色与前景色
        mDotsBackColor = typedArray.getColor(R.styleable.DotsProgressBar_barBackColor, ContextCompat.getColor(context, android.R.color.darker_gray));
        mDotsFrontColor = typedArray.getColor(R.styleable.DotsProgressBar_barFrontColor, ContextCompat.getColor(context, android.R.color.holo_blue_light));
        typedArray.recycle();
        // 初始化画笔
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        // 初始化插值器
        mInterpolator = new LinearInterpolator();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(width, mDotsRadius * 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        mPartWidth = (width - mDotsRadius * 2) / (mDotsCount - 1);
        // 画背景
        mPaint.setColor(mDotsBackColor);
        canvas.drawRect(mDotsRadius, height / 2 - mDotsProgressWidthHalf, width - mDotsRadius, height / 2 + mDotsProgressWidthHalf, mPaint);
        for (int i = 0; i < mDotsCount; i++) {
            canvas.drawCircle(mDotsRadius + mPartWidth * i, mDotsRadius, mDotsRadius, mPaint);
        }
        // 调整画笔为前景色
        mPaint.setColor(mDotsFrontColor);
        // 绘制前景进度条
        if (mViewPager == null) {
            // 说明没有设置过 ViewPager，进度条由按钮或者代码控制
            if (mOldPosition < mNewPosition) {
                // 说明是进度条向前进一段
                float percent = mInterpolator.getInterpolation(((float) mPartTime) / mSpeed);
                int[] params = getParams(percent);
                int start = mDotsRadius + mOldPosition * mPartWidth;
                // 绘制不变的部分
                canvas.drawRect(mDotsRadius, mDotsRadius - mDotsProgressWidthHalf, start, mDotsRadius + mDotsProgressWidthHalf, mPaint);
                for (int i = 0; i < (mOldPosition + 1); i++) {
                    canvas.drawCircle(mDotsRadius + i * mPartWidth, mDotsRadius, mDotsRadius, mPaint);
                }
                // 绘制变化的部分
                canvas.drawRect(start, mDotsRadius - mDotsProgressWidthHalf, start + params[0], mDotsRadius + mDotsProgressWidthHalf, mPaint);
                canvas.drawCircle(start + params[0], mDotsRadius, params[1], mPaint);
                if (mPartTime < mSpeed) {
                    mPartTime++;
                } else {
                    mOldPosition = mNewPosition;
                    mIsRunning = false;
                }
                postInvalidate();
            } else if (mOldPosition > mNewPosition) {
                // 说明是进度条向后退一段
                float percent = 1 - mInterpolator.getInterpolation(((float) mPartTime) / mSpeed);
                int[] params = getParams(percent);
                int start = mDotsRadius + mNewPosition * mPartWidth;
                // 绘制不变的部分
                canvas.drawRect(mDotsRadius, mDotsRadius - mDotsProgressWidthHalf, start, mDotsRadius + mDotsProgressWidthHalf, mPaint);
                for (int i = 0; i < (mNewPosition + 1); i++) {
                    canvas.drawCircle(mDotsRadius + i * mPartWidth, mDotsRadius, mDotsRadius, mPaint);
                }
                // 绘制变化的部分
                canvas.drawRect(start, mDotsRadius - mDotsProgressWidthHalf, start + params[0], mDotsRadius + mDotsProgressWidthHalf, mPaint);
                canvas.drawCircle(start + params[0], mDotsRadius, params[1], mPaint);
                if (mPartTime < mSpeed) {
                    mPartTime++;
                } else {
                    mOldPosition = mNewPosition;
                    mIsRunning = false;
                }
                postInvalidate();
            } else {
                // 说明动画已经结束，我们只需要绘制正确的前景进度
                int start = mDotsRadius + mOldPosition * mPartWidth;
                canvas.drawRect(mDotsRadius, mDotsRadius - mDotsProgressWidthHalf, mDotsRadius + start, mDotsRadius + mDotsProgressWidthHalf, mPaint);
                for (int i = 0; i < (mOldPosition + 1); i++) {
                    canvas.drawCircle(mDotsRadius + i * mPartWidth, mDotsRadius, mDotsRadius, mPaint);
                }
            }
        } else {
            // 说明已经设置过 ViewPager，代码控制不起作用，而由 ViewPager 的滑动控制
            int start = mDotsRadius + mOldPosition * mPartWidth;
            // 绘制不变的部分
            canvas.drawRect(mDotsRadius, mDotsRadius - mDotsProgressWidthHalf, start, mDotsRadius + mDotsProgressWidthHalf, mPaint);
            for (int i = 0; i < (mOldPosition + 1); i++) {
                canvas.drawCircle(mDotsRadius + i * mPartWidth, mDotsRadius, mDotsRadius, mPaint);
            }
            // 绘制变化的部分
            canvas.drawRect(start, mDotsRadius - mDotsProgressWidthHalf, start + mParams[0], mDotsRadius + mDotsProgressWidthHalf, mPaint);
            canvas.drawCircle(start + mParams[0], mDotsRadius, mParams[1], mPaint);
        }
    }

    /**
     * 进度条向前进一
     */
    public void startForward() {
        if (mOldPosition < (mDotsCount - 1) && !mIsRunning) {
            mNewPosition = mOldPosition + 1;
            mPartTime = 0;
            mIsRunning = true;
            postInvalidate();
        }
    }

    /**
     * 进度条向后退一
     */
    public void startBack() {
        if (mOldPosition > 0 && !mIsRunning) {
            mNewPosition = mOldPosition - 1;
            mPartTime = 0;
            mIsRunning = true;
            postInvalidate();
        }
    }

    /**
     * 设置插值器
     *
     * @param interpolator 插值器
     */
    public void setInterpolator(Interpolator interpolator) {
        mInterpolator = interpolator;
    }

    /**
     * 设置 ViewPager
     *
     * @param viewPager ViewPager
     */
    public void setViewPager(ViewPager viewPager) {
        this.mViewPager = viewPager;
        mDotsCount = mViewPager.getAdapter().getCount();
        mOldPosition = mNewPosition = mViewPager.getCurrentItem();
        this.mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mOldPosition = position;
                mParams = getParams(positionOffset);
                postInvalidate();
            }
        });
        invalidate();
    }

    /**
     * 提供一个取消 ViewPager 关联的方法
     */
    public void removeViewPager() {
        mViewPager = null;
    }

    /**
     * 获得当前对应进度所需要绘制的尺寸，整型数组第一个代表进度条的长度，第二个代表圆点的半径
     *
     * @param percent 当前一段变化中的比例
     * @return 返回整型数组
     */
    private int[] getParams(float percent) {
        int[] params = new int[2];
        if (mOldPosition >= 0) {
            if (percent > 0.9) {
                params[0] = mPartWidth;
                params[1] = (int) (((percent - 0.9) / 0.1) * (mDotsRadius - mDotsProgressWidthHalf) + mDotsProgressWidthHalf);
            } else {
                params[0] = (int) ((percent / 0.9) * mPartWidth);
                params[1] = mDotsProgressWidthHalf;
            }
        } else {
            params[0] = 0;
            params[1] = (int) (percent * mDotsRadius);
        }
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
