package com.hangyuan.colorstatus;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

/**
 * chensi
 */
public class ColorStatusView extends View {

    /**
     * 画笔
     */
    private Paint mPaint;

    /**
     * 上下文
     */
    private Context mContext;

    private int mWidth;

    private int mHeight;

    /**
     * 圆角边框的圆角值
     */
    private float radius;

    /**
     * 圆点的半径
     */
    private float circle_radius;

    /**
     * 提示文字
     */
    private String text_notice = "";

    /**
     * 字体大小
     */
    private float text_size;

    /**
     * 字体大小
     */
    private float stoke_size;


    /**
     * 渐变开始的颜色
     */
    private int startColor;

    /**
     * 渐变结束的颜色
     */
    private int endColor;

    /**
     * 颜色
     */
    private int noticeColor;

    /**
     * 用来获得渐变色中间某一点的颜色
     */
    private int change_color = 0;
    private ValueAnimator anim;

    public ColorStatusView(Context context) {
        this(context, null);
    }

    public ColorStatusView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorStatusView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;

        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.ColorStatusView);
        radius = typedArray.getDimension(R.styleable.ColorStatusView_radius_size, 9);
        circle_radius = typedArray.getDimension(R.styleable.ColorStatusView_circle_radius, 6);
        text_size = typedArray.getDimension(R.styleable.ColorStatusView_text_size, 16);
        stoke_size = typedArray.getDimension(R.styleable.ColorStatusView_stoke_width, 3);
        text_notice = typedArray.getString(R.styleable.ColorStatusView_text_notice);
        startColor = typedArray.getColor(R.styleable.ColorStatusView_start_color, ContextCompat.getColor(mContext,R.color.status_start_color));
        endColor = typedArray.getColor(R.styleable.ColorStatusView_end_color, ContextCompat.getColor(mContext,R.color.status_end_color));
        noticeColor = typedArray.getColor(R.styleable.ColorStatusView_circle_color, ContextCompat.getColor(mContext,R.color.status_notice_color));

        typedArray.recycle();
        initView();
        initAnim();
    }

    private void initAnim() {
        anim = ValueAnimator.ofFloat(1.0f, 0.9f);
        anim.setRepeatCount(ValueAnimator.INFINITE);//设置无限重复
        anim.setRepeatMode(ValueAnimator.REVERSE);//设置重复模式
        anim.setDuration(1200);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedValue = (float) animation.getAnimatedValue();
                change_color = getColorFrom(startColor, endColor, animation.getAnimatedFraction());
                ColorStatusView.this.setScaleX(animatedValue);
                ColorStatusView.this.setScaleY(animatedValue);
                postInvalidate();
            }
        });
    }

    /**
     * 初始化操作
     */
    private void initView() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(stoke_size);

    }

    public void startAnim() {
        if (anim == null) {
            throw new RuntimeException("Animation not initialized!");
        }
        anim.start();
    }

    public void stopAnim () {
        if (anim != null) {
            anim.cancel();
            anim = null;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mWidth == 0) {
            mWidth = getWidth();
        }
        if (mHeight == 0) {
            mHeight = getHeight();
        }

        // 画出外圆角边框
        mPaint.setColor(noticeColor);
        mPaint.setStyle(Paint.Style.STROKE);
        RectF targetRect = new RectF(3, 3, mWidth - 3, mHeight - 3);
        canvas.drawRoundRect(targetRect, radius, radius, mPaint);

        // 画出左方的圆点
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(change_color);
        canvas.drawCircle(30, (mHeight) / 2, circle_radius, mPaint);

        // 画出 中间的提示文字
        mPaint.setColor(noticeColor);
        mPaint.setTextSize(text_size);
        Rect rect = new Rect();
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.getTextBounds(text_notice, 0, text_notice.length(), rect);

        Paint.FontMetricsInt fontMetrics = mPaint.getFontMetricsInt();
        int baseline = (int) ((targetRect.bottom + targetRect.top - fontMetrics.bottom - fontMetrics.top) / 2);
        canvas.drawText(text_notice, targetRect.centerX() + circle_radius * 2, baseline, mPaint);

    }

    /**
     * 取两个颜色间的渐变区间 中的某一点的颜色
     *
     * @param startColor
     * @param endColor
     * @param radio
     * @return
     */
    private int getColorFrom(int startColor, int endColor, float radio) {
        int redStart = Color.red(startColor);
        int blueStart = Color.blue(startColor);
        int greenStart = Color.green(startColor);
        int redEnd = Color.red(endColor);
        int blueEnd = Color.blue(endColor);
        int greenEnd = Color.green(endColor);

        int red = (int) (redStart + ((redEnd - redStart) * radio + 0.5));
        int greed = (int) (greenStart + ((greenEnd - greenStart) * radio + 0.5));
        int blue = (int) (blueStart + ((blueEnd - blueStart) * radio + 0.5));
        return Color.argb(255, red, greed, blue);
    }
}
