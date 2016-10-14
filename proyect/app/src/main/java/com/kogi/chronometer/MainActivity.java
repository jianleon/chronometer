package com.kogi.chronometer;

import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private Handler customHandler = new Handler();
    ArrayList<String> arrayList;
    ArrayAdapter<String> adapter;

    private int STATE;
    private final int STOP = 0;
    private final int PAUSE = 1;
    private final int RUNNING = 2;
    private int lapsCounter;

    Button buttonStartPause;
    TextView textTimer;
    ListView listLaps;

    long startTime = 0L;
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listLaps = (ListView) findViewById(R.id.lstLaps);
        textTimer = (TextView) findViewById(R.id.txtTime);
        buttonStartPause = (Button) findViewById(R.id.btnStartPause);
        textTimer.setTextColor(getResources().getColor(android.R.color.darker_gray));

        STATE = STOP;
        lapsCounter = 0;

        arrayList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, arrayList);
        listLaps.setAdapter(adapter);
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
            textTimer.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            textTimer.clearAnimation();
            buttonStartPause.setText(getString(R.string.label_pause));
        } else {
            STATE = PAUSE;
            timeSwapBuff += timeInMilliseconds;
            customHandler.removeCallbacks(updateTimerThread);
            buttonStartPause.setText(getString(R.string.label_start));
            textTimer.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
            textTimer.setAnimation(AnimationUtils.loadAnimation(this, R.anim.blink));
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
        if (STATE != PAUSE && STATE != STOP) {
            lapsCounter += 1;
            arrayList.add(0, String.format("%s\t\t\t%s", lapsCounter, textTimer.getText()));
            adapter.notifyDataSetChanged();
        }
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
        arrayList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, arrayList);
        listLaps.setAdapter(adapter);

        STATE = STOP;
        lapsCounter = 0;

        startTime = 0L;
        timeInMilliseconds = 0L;
        timeSwapBuff = 0L;
        updatedTime = 0L;
        textTimer.setText(getString(R.string.template_timer));
        buttonStartPause.setText(getString(R.string.label_start));

        textTimer.setTextColor(getResources().getColor(android.R.color.darker_gray));
        textTimer.clearAnimation();
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
