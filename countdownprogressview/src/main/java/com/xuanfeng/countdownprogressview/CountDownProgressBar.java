package com.xuanfeng.countdownprogressview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Created by xuanfengwuxiang on 2018/6/28.
 * 圆圈倒计时
 */

public class CountDownProgressBar extends View {

    private Context mContext;
    //圆环参数
    private int mDefaultRingColor;//圆环颜色
    private float mDefaultRingWidth;//圆环宽度
    //进度条参数
    private int mProgressColor;//进度条颜色
    private float mProgressWidth;//进度条宽度
    private int mSweepAngle;//当前弧的角度
    //文字参数
    private float mTextSize;//文字大小
    private int mTextColor;//文字颜色
    //其他
    private int mCountDownTime;//倒计时时间
    private final String unit = "s";//倒计时单位
    private float mTextRingSpace;//文字与圆圈的留白
    private OnCountDownFinishListener mOnCountDownFinishListener;//计时结束监听器
    //画笔
    private Paint mDefaultRingPaint;
    private Paint mProgressPaint;
    private Paint mTextPaint;


    public CountDownProgressBar(Context context) {
        this(context, null);
    }

    public CountDownProgressBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CountDownProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        getAttributes(attrs);
        init();
    }

    //获取自定义属性
    private void getAttributes(@Nullable AttributeSet attrs) {
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.CountDownProgressBar);
        mDefaultRingColor = typedArray.getColor(R.styleable.CountDownProgressBar_default_ring_color, mContext.getResources().getColor(R.color.default_ring_color));
        mDefaultRingWidth = typedArray.getDimension(R.styleable.CountDownProgressBar_default_ring_width, 2);

        mProgressColor = typedArray.getColor(R.styleable.CountDownProgressBar_progress_color, mContext.getResources().getColor(R.color.progress_color));
        mProgressWidth = typedArray.getDimension(R.styleable.CountDownProgressBar_progress_width, 2);

        mTextSize = typedArray.getDimension(R.styleable.CountDownProgressBar_text_size, sp2px(mContext, 20));
        mTextColor = typedArray.getColor(R.styleable.CountDownProgressBar_text_color, mContext.getResources().getColor(R.color.progress_color));

        mCountDownTime = typedArray.getInteger(R.styleable.CountDownProgressBar_count_down_time, 15);
        mTextRingSpace = typedArray.getDimension(R.styleable.CountDownProgressBar_text_ring_space, 4);

        typedArray.recycle();
    }

    private void init() {
        initPaint();
    }

    //初始化画笔
    private void initPaint() {
        //圆环画笔
        mDefaultRingPaint = new Paint();
        mDefaultRingPaint.setAntiAlias(true);
        mDefaultRingPaint.setDither(true);
        mDefaultRingPaint.setStyle(Paint.Style.STROKE);
        mDefaultRingPaint.setStrokeWidth(mDefaultRingWidth);
        mDefaultRingPaint.setColor(mDefaultRingColor);

        //进度条画笔
        mProgressPaint = new Paint();
        mProgressPaint.setAntiAlias(true);
        mProgressPaint.setDither(true);//防抖动
        mProgressPaint.setStyle(Paint.Style.STROKE);
        mProgressPaint.setStrokeWidth(mProgressWidth);
        mProgressPaint.setColor(mProgressColor);
        mProgressPaint.setStrokeCap(Paint.Cap.ROUND);//线帽

        //文字画笔
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setDither(true);
        mTextPaint.setStyle(Paint.Style.FILL);//填充内部
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setTextAlign(Paint.Align.CENTER);//x坐标中间开始画
    }

    @Override//Tip-------onMeasure只需要处理wrap_content这种情况。match_parent、具体宽高这2种情况交给super处理就好。
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //测量模式
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int strokeWidth = (int) Math.max(mDefaultRingWidth, mProgressWidth);//圆圈的宽度

        int textMaxLength = (int) getTextMaxLength();

        if (widthMode != MeasureSpec.EXACTLY) {
            int widthSize = getPaddingLeft() + getPaddingRight() + textMaxLength + strokeWidth * 2 + (int) (mTextRingSpace * 2);//整个View的宽度
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY);
        }
        if (heightMode != MeasureSpec.EXACTLY) {
            int heightSize = getPaddingTop() + getPaddingBottom() + textMaxLength + strokeWidth * 2 + (int) (mTextRingSpace * 2);//整个View的高度
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();//canvas初始状态保存
        canvas.translate(getPaddingLeft(), getPaddingTop());

        //画默认圆
        float centerX = getTextMaxLength() / 2 + mDefaultRingWidth + mTextRingSpace;
        float centerY = getTextMaxLength() / 2 + mDefaultRingWidth + mTextRingSpace;
        float defaultRingRadius = getTextMaxLength() / 2 + mDefaultRingWidth / 2 + mTextRingSpace;
        canvas.drawCircle(centerX, centerY, defaultRingRadius, mDefaultRingPaint);

        //画文字
        String text = mCountDownTime - (int) (mSweepAngle / 360f * mCountDownTime) + unit;
        float baseX = centerX;
        float baseLine = centerY + (mTextPaint.descent() - mTextPaint.ascent()) / 2 - mTextPaint.descent();
        canvas.drawText(text, baseX, baseLine, mTextPaint);

        //画进度圆弧
        RectF rectF = new RectF(mProgressWidth / 2, mProgressWidth / 2, centerX * 2 - mProgressWidth / 2, centerY * 2 - mProgressWidth / 2);
        canvas.drawArc(rectF, -90, mSweepAngle, false, mProgressPaint);

        canvas.restore();//canvas初始状态恢复
    }

    //获取文字宽高的最大值
    private float getTextMaxLength() {
        float textWidth = mTextPaint.measureText(mCountDownTime + unit);//文字宽
        float textHeight = mTextPaint.descent() - mTextPaint.ascent();//文字高
        return Math.max(textWidth, textHeight);
    }

    public void startCountDown() {
        ValueAnimator valueAnimator = getValueAnimator(mCountDownTime * 1000);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                mSweepAngle = (int) (360 * (value / 100f));
                invalidate();
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (mOnCountDownFinishListener != null) {
                    mOnCountDownFinishListener.countDownFinished();
                }
            }

        });
        valueAnimator.start();
    }

    private ValueAnimator getValueAnimator(long countdownTime) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 100);
        valueAnimator.setDuration(countdownTime);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setRepeatCount(0);
        return valueAnimator;
    }

    public void setOnCountDownFinishListener(OnCountDownFinishListener onCountDownFinishListener) {
        mOnCountDownFinishListener = onCountDownFinishListener;
    }

    public void setDefaultRingColor(int defaultRingColor) {
        mDefaultRingColor = defaultRingColor;
    }

    public void setDefaultRingWidth(float defaultRingWidth) {
        mDefaultRingWidth = defaultRingWidth;
    }

    public void setProgressColor(int progressColor) {
        mProgressColor = progressColor;
    }

    public void setProgressWidth(float progressWidth) {
        mProgressWidth = progressWidth;
    }

    public void setTextSize(float textSize) {
        mTextSize = textSize;
    }

    public void setTextColor(int textColor) {
        mTextColor = textColor;
    }

    public void setCountDownTime(int countDownTime) {
        mCountDownTime = countDownTime;
    }

    public int sp2px(Context context, float spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spVal, context.getResources().getDisplayMetrics());
    }

    public interface OnCountDownFinishListener {
        void countDownFinished();
    }

}
