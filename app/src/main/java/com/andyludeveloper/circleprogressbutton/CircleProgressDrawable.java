package com.andyludeveloper.circleprogressbutton;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Property;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

public class CircleProgressDrawable extends Drawable implements Animatable{
    private static final Interpolator SWEEP_INTERPOLATOR = new DecelerateInterpolator();
    private static final int SWEEP_ANIMATOR_DURATION = 1000*3;
    private static final int MIN_SWEEP_ANGLE = 30;
    private Paint progressPaint;
    private Paint progressingPaint;
    private Context context;
    private RectF oval;
    private boolean isRunning = false;
    private ObjectAnimator mObjectAnimatorSweep;

    private static final String TAG = CircleProgressDrawable.class.getSimpleName();

    public CircleProgressDrawable(Context context, AttributeSet attr){
        this.context = context;
        setProgressPaint(attr);
        setProgressingPaint(attr);
        setupAnimations();
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        Log.d(TAG, "draw");
        int width= canvas.getWidth();
        int height = canvas.getHeight();
        canvas.drawCircle(width/2, height/2, (width-(getCircleWidthInPX()*2))/2, progressPaint);
        if(isRunning()) {
            canvas.drawArc(getOval(width, height, (width - (getCircleWidthInPX() * 2)) / 2), 270.0f, getCurrentSweepAngle(),
                    false, getProgressingPaint());
        }
    }
    private void setProgressPaint(AttributeSet attrs){
        if(progressPaint == null) {
            progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            if(attrs!=null) {
                TypedArray a = context.getTheme().obtainStyledAttributes(
                        attrs,
                        R.styleable.CircleProgressButton,
                        0, 0);
                try {
                    progressPaint.setColor(a.getColor(R.styleable.CircleProgressButton_normalCircleColor,
                            context.getResources().getColor(R.color.grey)));
                } finally {
                    a.recycle();
                }
            }
            progressPaint.setStrokeWidth(getCircleWidthInPX());
            progressPaint.setStyle(Paint.Style.STROKE);
        }
    }

    private float getCircleWidthInPX(){
        return context.getResources().getDimensionPixelSize(R.dimen.circle_progress_width);
    }

    private RectF getOval(float width, float height, float radius){
        if(oval == null) {
            oval = new RectF();

            float center_x = width / 2;
            float center_y = height / 2;

            oval.set(center_x - radius,
                    center_y - radius,
                    center_x + radius,
                    center_y + radius);
        }
        return oval;
    }

    @Override
    public void start() {
        Log.d(TAG, "start");
        if (!isRunning()) {
            setRunning(true);
            mObjectAnimatorSweep.start();
            invalidateSelf();
        }
    }

    @Override
    public void stop() {
        Log.d(TAG, "stop");
        setRunning(false);
    }
    private boolean mModeAppearing;
    private float mCurrentGlobalAngleOffset;
    private void toggleAppearingMode() {
        mModeAppearing = !mModeAppearing;
        if (mModeAppearing) {
            mCurrentGlobalAngleOffset = (mCurrentGlobalAngleOffset + MIN_SWEEP_ANGLE * 2) % 360;
        }
    }

    private void setupAnimations() {
        mObjectAnimatorSweep = ObjectAnimator.ofFloat(this, mSweepProperty, 360f);
        mObjectAnimatorSweep.setInterpolator(SWEEP_INTERPOLATOR);
        mObjectAnimatorSweep.setDuration(SWEEP_ANIMATOR_DURATION);
        mObjectAnimatorSweep.setRepeatMode(ValueAnimator.RESTART);
        mObjectAnimatorSweep.setRepeatCount(0);
        mObjectAnimatorSweep.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                Log.d(TAG,"onAnimationStart");
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Log.d(TAG,"onAnimationEnd");
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                Log.d(TAG,"onAnimationCancel");
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                toggleAppearingMode();
                Log.d(TAG,"onAnimationRepeat");
            }
        });
    }

    private Property<CircleProgressDrawable, Float> mSweepProperty
            = new Property<CircleProgressDrawable, Float>(Float.class, "arc") {
        @Override
        public Float get(CircleProgressDrawable object) {
            return object.getCurrentSweepAngle();
        }

        @Override
        public void set(CircleProgressDrawable object, Float value) {
            object.setCurrentSweepAngle(value);
        }
    };
    @Override
    public boolean isRunning() {
        return isRunning;
    }

    private void setRunning(boolean running){
        isRunning = running;
    }


    private void setProgressingPaint(AttributeSet attrs){
        if(progressingPaint == null){
            progressingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

            if(attrs!=null) {
                TypedArray a = context.getTheme().obtainStyledAttributes(
                        attrs,
                        R.styleable.CircleProgressButton,
                        0, 0);
                try {
                    progressingPaint.setColor(a.getColor(R.styleable.CircleProgressButton_progressCircleColor,
                            context.getResources().getColor(android.R.color.holo_red_dark)));
                } finally {
                    a.recycle();
                }
            }

            progressingPaint.setStrokeWidth(getCircleWidthInPX());
            progressingPaint.setStyle(Paint.Style.STROKE);
        }
    }

    private Paint getProgressingPaint(){
        return progressingPaint;
    }

    @Override
    public void setAlpha(int i) {

    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }

    private float mCurrentSweepAngle;
    private void setCurrentSweepAngle(float currentSweepAngle) {
        Log.d(TAG, "setCurrentSweepAngle, angle= "+currentSweepAngle);
        mCurrentSweepAngle = currentSweepAngle;
        invalidateSelf();
    }

    private float getCurrentSweepAngle() {
        return mCurrentSweepAngle;
    }
}
