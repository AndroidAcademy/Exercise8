package com.androidtlv8;

import com.androidtlv8.MainActivity.BackgroundProgressReceiver;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.widget.Toast;

public class HardJobService extends Service {
    private ServiceHandler mServiceHandler;
    private Looper mServiceLooper;
    private boolean isDestroyed=false;

    public HardJobService() {

    }

    @Override
    public void onCreate() {

      // To avoid cpu-blocking, we create a background handler to run our service
      //VTODO Create HandlerThread
        HandlerThread handlerThread = new HandlerThread("HardJobService", Process.THREAD_PRIORITY_BACKGROUND);

      // start the new handler thread
      //VTODO Start a created HandlerThread
        handlerThread.start();

      //VTODO Get looper out of thread
        mServiceLooper = handlerThread.getLooper();

      // start the service using the background handler
      //VTODO Create instance of ServiceHandler class that receives instance of ServiceLooper
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isDestroyed = false;
        Toast.makeText(this, "onStartCommand", Toast.LENGTH_SHORT).show();
        // call a new service handler. The service ID can be used to identify the service
        Message message = mServiceHandler.obtainMessage();
        message.arg1 = startId;

        //VTODO Send message to ServiceHandler
        mServiceHandler.sendMessage(message);

        //VTODO Return START_STICKY
        return START_STICKY;
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

    @Override
    public IBinder onBind(Intent intent) {
      return null;
    }

    // Object responsible for
    private final class ServiceHandler extends Handler {

        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            // Well calling mServiceHandler.sendMessage(message);
            // from onStartCommand this method will be called.

            // Add your cpu-blocking activity here
            try {
                for (int i = 0; i <= 100 && !isDestroyed; i++) {
                    //DOING Very hard job
                    Thread.sleep(100);
                    notifyU(i);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            showToast("Finishing HardJobService, id: " + msg.arg1);
            // the msg.arg1 is the startId used in the onStartCommand,
            // so we can track the running sevice here.
            stopSelf(msg.arg1);
        }
    }

    private void notifyU(int progress) {
        Intent intent = new Intent(BackgroundProgressReceiver.PROGRESS_UPDATE_ACTION);
        intent.putExtra(BackgroundProgressReceiver.PROGRESS_VALUE_KEY, progress);
        sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        isDestroyed = true;
        super.onDestroy();
    }
}


