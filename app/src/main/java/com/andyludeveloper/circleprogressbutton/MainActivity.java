package com.andyludeveloper.circleprogressbutton;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    CircleProgressButton cpb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initCircleProgressButton();
    }

    private void initCircleProgressButton(){
        cpb = findViewById(R.id.circle_progress);
        cpb.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                cpb.click();
            }
        });

        cpb.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        if(pressedButtonBeginTime==0) {
                            setPressedButtonBeginTime();
                            cpb.click();
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        cpb.releaseClick();
                        long currentTime = System.currentTimeMillis();
                        if(currentTime - pressedButtonBeginTime > 3000){
                            //DO something
                            Toast.makeText(getBaseContext(), "3secs", Toast.LENGTH_SHORT).show();
                        }
                        resetPressedButtonBeginTime();
                        break;
                }
                view.performClick();
                return false;
            }
        });
    }

    private long pressedButtonBeginTime;
    private void setPressedButtonBeginTime(){
        if(pressedButtonBeginTime == 0) {
            pressedButtonBeginTime = System.currentTimeMillis();

        }
    }

    private void resetPressedButtonBeginTime(){
        pressedButtonBeginTime = 0;
    }
}
