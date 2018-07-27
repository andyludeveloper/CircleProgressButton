package com.andyludeveloper.circleprogressbutton;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.util.Log;

public class CircleProgressButton extends AppCompatButton {
    private static final String TAG = CircleProgressButton.class.getSimpleName();
    private Context context;
    private Paint circlePaint;
    private boolean click = false;
    private CircleProgressDrawable circleProgressDrawable;
    public CircleProgressButton(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.context = context;
        init();
    }

    private void init(){
        setCirclePaint();
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
            circleProgressDrawable = new CircleProgressDrawable(context);
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

    private void setCirclePaint(){
        if(circlePaint == null) {
            circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            circlePaint.setStyle(Paint.Style.FILL);
        }
    }

    private Paint getCirclePaint(){
        circlePaint.setColor(context.getResources().getColor(isClick()?R.color.colorPrimaryDark:R.color.colorPrimary));
        return circlePaint;
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



    private float getCircleWidthInPX(){
        return context.getResources().getDimensionPixelSize(R.dimen.circle_progress_width);
    }

    private float getGapWidthInPX(){
        return context.getResources().getDimensionPixelSize(R.dimen.circle_progress_button_gap);
    }

    @Override
    protected boolean verifyDrawable(@NonNull Drawable who) {
        return who == circleProgressDrawable || super.verifyDrawable(who);
    }

    @Override
     public void invalidateDrawable(@NonNull Drawable who) {
        if (verifyDrawable(who)) {
            invalidate();
        }
     }
}
