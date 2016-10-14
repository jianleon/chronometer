package com.kogi.chronometer;

import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    private Handler customHandler = new Handler();
    private int STOP = 0;
    private int PAUSE = 1;
    private int RUNNING = 2;
    private int STATE;

    Button buttonStartPause;
    TextView textTimer;

    long startTime = 0L;
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        STATE = STOP;
        textTimer = (TextView) findViewById(R.id.txtTime);
        buttonStartPause = (Button) findViewById(R.id.btnStartPause);
    }

    public void buttonStartPause(View view) {
        if (STATE == STOP) {
            STATE = RUNNING;
            startTime = SystemClock.uptimeMillis();
            customHandler.postDelayed(updateTimerThread, 0);
            buttonStartPause.setText("PAUSE");
        } else {
            STATE = STOP;
            timeSwapBuff += timeInMilliseconds;
            customHandler.removeCallbacks(updateTimerThread);
            buttonStartPause.setText("START");
        }
    }

    private Runnable updateTimerThread = new Runnable() {

        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updatedTime = timeSwapBuff + timeInMilliseconds;

            int milliseconds = (int) (updatedTime % 1000);
            int seconds = (int) (updatedTime / 1000);
            int minutes = seconds / 60;
            seconds %= 60;
            textTimer.setText(String.format(
                    "%s:%s:%s",
                    String.format("%02d", minutes),
                    String.format("%02d", seconds),
                    String.format("%03d", milliseconds)));
            customHandler.postDelayed(this, 0);
        }
    };
}
