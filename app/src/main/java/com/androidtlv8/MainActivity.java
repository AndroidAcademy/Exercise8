package com.androidtlv8;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView progressValue;
    private BackgroundProgressReceiver backgroundProgressReceiver;
    private boolean isServiceStarted;
    private boolean isIntentServiceStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //VTODO find reference to progress TextView
        progressValue = findViewById(R.id.percents);

        //VTODO Add listeners for two buttons
        Button btnIntentService = findViewById(R.id.btn_intent_service);
        btnIntentService.setOnClickListener(this);

        Button btnService = findViewById(R.id.btn_service);
        btnService.setOnClickListener(this);

        subscribeForProgressUpdates();
    }

    @Override
    public void onClick(View v) {
        //VTODO implement clicks on two buttons
        switch (v.getId()) {
            case R.id.btn_service:
                if (isIntentServiceStarted) {
                    stopService(new Intent(this, HardJobIntentService.class));
                    isIntentServiceStarted = false;
                }
                isServiceStarted = true;
                startService(new Intent(this, HardJobService.class));
                break;
            case R.id.btn_intent_service:
                if (isServiceStarted) {
                    stopService(new Intent(this, HardJobService.class));
                    isServiceStarted = false;
                }

                isIntentServiceStarted = true;
                startService(new Intent(this, HardJobIntentService.class));
                break;
        }
    }

    private void subscribeForProgressUpdates() {
        if (backgroundProgressReceiver == null) {
            backgroundProgressReceiver = new BackgroundProgressReceiver();
        }

        IntentFilter progressUpdateActionFilter = new IntentFilter(BackgroundProgressReceiver.PROGRESS_UPDATE_ACTION);
        registerReceiver(backgroundProgressReceiver, progressUpdateActionFilter);
    }

    public class BackgroundProgressReceiver extends BroadcastReceiver {
        public static final String PROGRESS_UPDATE_ACTION = "PROGRESS_UPDATE_ACTION";
        public static final String PROGRESS_VALUE_KEY = "PROGRESS_VALUE_KEY";

        @Override
        public void onReceive(Context context, Intent intent) {
            int progress = intent.getIntExtra(PROGRESS_VALUE_KEY, 0);
            String text;
            if (progress == 100) {
                text = "Done!";
                isIntentServiceStarted = false;
                isServiceStarted = false;
            } else {
                text = String.format(Locale.getDefault(), "%d%%", progress);
            }
            progressValue.setText(text);
        }
    }
}