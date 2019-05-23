package werpx.cashiery;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.daimajia.numberprogressbar.NumberProgressBar;

import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import werpx.cashiery.Cashiery.barcodevalue;
import werpx.cashiery.RoomDatabase.productViewmodel;

public class SyncService extends Service {

    private productViewmodel mWordViewModel;
    private Context context;
    private Call<werpx.cashiery.salemodel.Saleroot> salecall;
    private productdatabase mydatabase;
    Boolean imgcondition=true;
    int i=0;
    public NumberProgressBar numberProgressBar;

    barcodevalue mvalue;





    historydata mydata;


    @Override
    public void onCreate() {
        super.onCreate();
        mydata=new historydata(this);

        mydatabase = new werpx.cashiery.productdatabase(this);





    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            mydata.syncedata();


                        } catch (Exception e) {

                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 17000); //execute in every 1000 ms






        return START_STICKY;
    }






    @Override
    public void onDestroy() {
        super.onDestroy();
    }



}
