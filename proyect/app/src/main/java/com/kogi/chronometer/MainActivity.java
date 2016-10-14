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
    private final int STOP = 0;
    private final int PAUSE = 1;
    private final int RUNNING = 2;
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

    /**
     * Funcionalidad del botón Start/Pause
     *
     * Dependiendo del estado actual del timer, 'Pausa' o 'Inicia' el cronómetro
     *
     * @param view Referencia a la vista actual
     */
    public void buttonStartPause(View view) {
        if (STATE == PAUSE || STATE == STOP) {
            STATE = RUNNING;
            startTime = SystemClock.uptimeMillis();
            customHandler.postDelayed(updateTimerThread, 0);
            buttonStartPause.setText(getResources().getString(R.string.label_pause));
        } else {
            STATE = PAUSE;
            timeSwapBuff += timeInMilliseconds;
            customHandler.removeCallbacks(updateTimerThread);
            buttonStartPause.setText(getResources().getString(R.string.label_start));
        }
    }

    /**
     * Funcionalidad del botón Record
     *
     * Se encarga de agregar el tiempo actual del cronómetro en una lista de 'vueltas'
     *
     * @param view Referencia a la vista actual
     */
    public void buttonRecordTime(View view) {

    }

    /**
     * Funcionalidad del botón Stop
     *
     * Se encarga de parar el cronómetro e inicializar los componentes del cronómetro
     *
     * @param view Referencia a la vista actual
     */
    public void buttonStop(View view) {
        customHandler.removeCallbacks(updateTimerThread);
        STATE = STOP;
        startTime = 0L;
        timeInMilliseconds = 0L;
        timeSwapBuff = 0L;
        updatedTime = 0L;
        textTimer.setText(getResources().getString(R.string.template_timer));
    }

    /**
     * Método asíncrono encargado de actualizar el timer del UI
     */
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
