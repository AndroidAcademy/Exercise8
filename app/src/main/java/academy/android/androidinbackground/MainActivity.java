package academy.android.androidinbackground;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import java.util.Locale;

import static academy.android.androidinbackground.MainActivity.BackgroundProgressReceiver.PROGRESS_UPDATE_ACTION;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

  private TextView progressValue;
  private BackgroundProgressReceiver backgroundProgressReceiver;
  private boolean isServiceStarted;
  private boolean isIntentServiceStarted;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    progressValue = findViewById(R.id.tv_progress_value);
    findViewById(R.id.btn_start_service).setOnClickListener(this);
    findViewById(R.id.btn_start_intent_service).setOnClickListener(this);
    subscribeForProgressUpdates();
  }

  @Override public void onClick(View v) {
    switch (v.getId()) {
      case R.id.btn_start_service:
        if (isIntentServiceStarted) {
          stopService(new Intent(this, HardJobIntentService.class));
          isIntentServiceStarted = false;
        }
        isServiceStarted = true;
        startService(new Intent(this, HardJobService.class));
        break;
      case R.id.btn_start_intent_service:
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
    IntentFilter progressUpdateActionFilter = new IntentFilter(PROGRESS_UPDATE_ACTION);
    registerReceiver(backgroundProgressReceiver, progressUpdateActionFilter);
  }

  public class BackgroundProgressReceiver extends BroadcastReceiver {
    public static final String PROGRESS_UPDATE_ACTION = "PROGRESS_UPDATE_ACTION";
    public static final String PROGRESS_VALUE_KEY = "PROGRESS_VALUE_KEY";

    @Override public void onReceive(Context context, Intent intent) {
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
