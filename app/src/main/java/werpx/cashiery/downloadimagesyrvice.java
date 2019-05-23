package werpx.cashiery;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.daimajia.numberprogressbar.NumberProgressBar;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import werpx.cashiery.Cashiery.barcodevalue;
import werpx.cashiery.RoomDatabase.Sqlitetable;

public class downloadimagesyrvice extends Service {



    private productdatabase mydatabase;
    Boolean imgcondition = true;

    historydata mydata;
    int i = 0;
    public NumberProgressBar numberProgressBar;
    public TimerTask doAsynchronousTask;
    public Boolean downloadconditionl = true;
    public Boolean firstrun = true;
    private werpx.cashiery.RoomDatabase.productViewmodel mWordViewModel;



    barcodevalue mvalue;
    SharedPreferences preferences;
   SharedPreferences.Editor editor;
   Context context;



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mvalue=new barcodevalue();


        preferences = getSharedPreferences("progressvalues", Context.MODE_PRIVATE);
        editor = preferences.edit();

        mydata=new historydata( this);

        mydatabase = new werpx.cashiery.productdatabase(this);


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {




        final Handler handler = new Handler(getMainLooper());
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {


                        if (haveNetworkConnection())
                        {

                            if (firstrun)
                            {

                                try {
                                    downloadimagesforproducts();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            else{
                                if (!imgcondition)
                                {
                                    try {
                                        downloadimagesforproducts();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }



                        }
                        else {

                            imgcondition=false;
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 10); //execute in every 1000 ms




        return START_STICKY;
    }

    public byte[] convertbitmaptobyte(Bitmap bitmap) {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        bitmap.recycle();
        return byteArray;
    }

    public void downloadimagesforproducts() throws IOException {

        //   downloadconditiontext.setText(getResources().getString(R.string.downloadimges));
        //  imgdownloadlayout.setVisibility(View.VISIBLE);







                List<Sqlitetable> myproducts = mydatabase.getproductsitems();


                i = getSharedPreferences("progressvalues", Context.MODE_PRIVATE).getInt("current", 0);
                if (myproducts.size() != 0) {

                    firstrun=false;
                   // Toast.makeText(downloadimagesyrvice.this, String.valueOf(i), Toast.LENGTH_SHORT).show();
                    Log.e("downloadornot",String.valueOf(i));


                    if (i<myproducts.size())
                    {
                        String img = myproducts.get(i).getImge();
                        if (img!=null)
                        {


                            String barcode = myproducts.get(i).getBarcode();
                            mvalue.setBarcodeimg(barcode);


                            URL url = new URL(img);
                            Convertimg convertimg = new Convertimg();
                            convertimg.execute(img);
                            imgcondition=true;

                        }
                        else{

                            i++;
                            editor.putInt("current",i);
                            editor.apply();
                        }

                    }
                    else{
                        this.stopSelf();
                    }




                }





    }




    class Convertimg extends AsyncTask<String, Integer, Bitmap> {

        public Asynctaskresultimg delegate = null;

        private Exception exception;
        private Bitmap myBitmap;


        protected Bitmap doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());




                return image;

            } catch (IOException e) {
                // Log exception
                return null;
            }

        }

        protected void onPostExecute(Bitmap feed) {

            if (feed != null) {
                byte[] myimg = convertbitmaptobyte(feed);
                Boolean val = mydatabase.updateimagevalues(myimg, mvalue.getBarcodeimg());
              //  Toast.makeText(downloadimagesyrvice.this, String.valueOf(val), Toast.LENGTH_SHORT).show();
                Log.e("downloadornot",String.valueOf(val));

                imgcondition = false;
                i++;
                editor.putInt("current",i);
                editor.apply();


            } else {
                if (haveNetworkConnection())
                {

                    imgcondition = false;
                    i++;
                    editor.putInt("current",i);
                    editor.apply();
                }
                else{
                    imgcondition = false;
                }


            }


            //   return myBitmap;
            // TODO: check this.exception
            // TODO: do something with the feed
        }


    }
    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
