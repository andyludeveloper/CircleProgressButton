package com.andyludeveloper.circleprogressbutton;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.util.Log;

public class CircleProgressButton extends AppCompatButton {
    private static final String TAG = CircleProgressButton.class.getSimpleName();
    private Context context;
    private Paint circlePaint;
    private boolean click = false;
    private CircleProgressDrawable circleProgressDrawable;
    private AttributeSet attributeSet;

    public CircleProgressButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.attributeSet = attrs;

        init(attrs);
    }

    private void init(AttributeSet attrs){
        setCirclePaint(attrs);
        setFocusable(true);
        setClickable(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width= canvas.getWidth();
        int height = canvas.getHeight();
        drawCircleProgress(canvas,isClick());
        canvas.drawCircle(width/2, height/2,
                (width-(getCircleWidthInPX()*2)-(getGapWidthInPX()*2))/2, getCirclePaint());
        drawString(canvas);
    }



    private void drawCircleProgress(Canvas canvas, boolean progressing){
        Log.d(TAG, "drawCircleProgress");
        if(circleProgressDrawable == null){
            circleProgressDrawable = new CircleProgressDrawable(context, attributeSet);
        }
        if(progressing){
            circleProgressDrawable.setCallback(this);
            circleProgressDrawable.start();
        }
        circleProgressDrawable.draw(canvas);
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

    public void click(){
        setClick(true);
        performClick();
    }

    public void releaseClick(){
        circleProgressDrawable.stop();
        setClick(false);
//        clickTime = 0;
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

    @Override
    protected boolean verifyDrawable(Drawable who) {
        return who == circleProgressDrawable || super.verifyDrawable(who);
    }

    @Override
     public void invalidateDrawable(Drawable who) {
        if (verifyDrawable(who)) {
            invalidate();
        }
     }
}
