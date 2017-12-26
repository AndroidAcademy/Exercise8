package academy.android.androidinbackground;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.widget.Toast;

public class HardJobIntentService extends IntentService {

  private boolean isDestroyed;

  public HardJobIntentService() {
    super("HardJobIntentService");
  }

  public HardJobIntentService(String name) {
    super(name);
  }

  @Override protected void onHandleIntent(@Nullable Intent intent) {
    isDestroyed = false;
    showToast("Starting IntentService");
    try {
      for (int i = 0; i <= 100 && !isDestroyed; i++) {
        Thread.sleep(100);
        Intent broadcastIntent =
            new Intent(MainActivity.BackgroundProgressReceiver.PROGRESS_UPDATE_ACTION);
        broadcastIntent.putExtra(MainActivity.BackgroundProgressReceiver.PROGRESS_VALUE_KEY, i);
        sendBroadcast(broadcastIntent);
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
    showToast("Finishing IntentService");
  }

  protected void showToast(final String msg) {
    //gets the main thread
    Handler handler = new Handler(Looper.getMainLooper());
    handler.post(new Runnable() {
      @Override public void run() {
        // run this code in the main thread
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
      }
    });
  }

  @Override public void onDestroy() {
    isDestroyed = true;
    super.onDestroy();
  }
}
