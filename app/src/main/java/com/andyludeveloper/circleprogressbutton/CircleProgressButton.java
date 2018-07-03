package com.andyludeveloper.circleprogressbutton;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;

import java.lang.ref.WeakReference;

public class CircleProgressButton extends AppCompatButton {
    private static final String TAG = CircleProgressButton.class.getSimpleName();
    private Context context;
    private Paint progressPaint;
    private Paint progressingPaint;
    private RectF oval;
    private Paint circlePaint;
    private boolean click = false;
    private long clickTime;
    private ProgressHandler handler;
    private static final int ACTION_UPDATE_PROGRESS = 1;

//    public CircleProgressButton(Context context) {
//        super(context);
//        this.context = context;
//        init(null);
//    }

    public CircleProgressButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(attrs);
    }

    private void init(AttributeSet attrs){
        setProgressPaint(attrs);
        setCirclePaint(attrs);
        setProgressingPaint(attrs);
        setFocusable(true);
        setClickable(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width= canvas.getWidth();
        int height = canvas.getHeight();
        Paint paint = getProgressPaint();
        canvas.drawCircle(width/2, height/2, (width-(getCircleWidthInPX()*2))/2, paint);
        canvas.drawCircle(width/2, height/2, (width-(getCircleWidthInPX()*2)-(getGapWidthInPX()*2))/2, getCirclePaint());
        if(isClick())
            canvas.drawArc(getOval(width, height,(width-(getCircleWidthInPX()*2))/2), 270.0f, getProgressAngle(),
                false, getProgressingPaint());
        drawString(canvas);
    }

    private void drawString(Canvas canvas){
        Paint paint = new Paint();
        paint.setColor(getCurrentTextColor());
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(getTextSize());
        paint.setAntiAlias(true);
        int xPos = (canvas.getWidth() / 2);
        int yPos = (int) ((canvas.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 2)) ;
        canvas.drawText(getText().toString(), xPos, yPos, paint);
    }

    private void setProgressPaint(AttributeSet attrs){
        if(progressPaint == null) {
            progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//            progressPaint.setColor(context.getResources().getColor(android.R.color.holo_red_light));

            if(attrs!=null) {
                TypedArray a = context.getTheme().obtainStyledAttributes(
                        attrs,
                        R.styleable.CircleProgressButton,
                        0, 0);
                try {
                    progressPaint.setColor(a.getColor(R.styleable.CircleProgressButton_normalCircleColor,
                            context.getResources().getColor(android.R.color.holo_red_light)));
                } finally {
                    a.recycle();
                }
            }
            progressPaint.setStrokeWidth(getCircleWidthInPX());
            progressPaint.setStyle(Paint.Style.STROKE);
        }
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

//            progressingPaint.setColor(context.getResources().getColor(android.R.color.holo_red_dark));
            progressingPaint.setStrokeWidth(getCircleWidthInPX());
            progressingPaint.setStyle(Paint.Style.STROKE);
        }
    }

    private Paint getProgressingPaint(){
        return progressingPaint;
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
    private void setCirclePaint(AttributeSet attrs){
        if(circlePaint == null) {
            circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            int color = android.R.color.holo_purple;

            circlePaint.setColor(context.getResources().getColor(color));
            circlePaint.setStyle(Paint.Style.FILL);
        }
    }

    @Override
    public boolean performClick() {
        super.performClick();
        return true;
    }

    public void performClick(long clickTimeMillis){
        setClick(true);
        setClickTime(clickTimeMillis);
        performClick();
        updateProgress();
    }

    public void updateProgress(){
        handler = new ProgressHandler(this);
        handler.sendEmptyMessageDelayed(ACTION_UPDATE_PROGRESS, 100);
        invalidate();
    }

    public void releaseClick(){
        setClick(false);
        clickTime = 0;
        if(handler!=null)
            handler.removeMessages(ACTION_UPDATE_PROGRESS);
    }

    private void setClick(boolean pressed){
        if(pressed != click) {
            click = pressed;
            requestLayout();
        }
    }

    private boolean isClick(){
        return click;
    }

    private void setClickTime(long clickTimeMillis){
        if(clickTime == 0){
            clickTime = clickTimeMillis;
        }
    }

    private float getProgressAngle(){
        long totalMillis = 3*1000; //3secs
        long totalAngle = 360;
        long currentTime = System.currentTimeMillis();
        long diffTime = currentTime - clickTime;
        return ((float)totalAngle/(float)totalMillis)*diffTime>360?360:((float)totalAngle/(float)totalMillis)*diffTime;
    }

    private Paint getProgressPaint(){
        return progressPaint;
    }

    private Paint getCirclePaint(){
        circlePaint.setColor(context.getResources().getColor(isClick()?R.color.colorPrimaryDark:R.color.colorPrimary));
        return circlePaint;
    }

    private float getCircleWidthInPX(){
        return context.getResources().getDimensionPixelSize(R.dimen.circle_progress_width);
    }

    private float getGapWidthInPX(){
        return context.getResources().getDimensionPixelSize(R.dimen.circle_progress_button_gap);
    }

    private static class ProgressHandler extends Handler {

        private WeakReference<CircleProgressButton> circleButtonWeakReference;
        ProgressHandler(CircleProgressButton context){
            circleButtonWeakReference = new WeakReference<>(context);
        }
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == ACTION_UPDATE_PROGRESS){
                circleButtonWeakReference.get().updateProgress();
            }
        }
    }
}
