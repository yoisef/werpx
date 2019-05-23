package werpx.cashiery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import werpx.cashiery.Cashiery.Sales_Screen;
import werpx.cashiery.Cashiery.barcodevalue;

public class NetworkBroadcastreciever extends BroadcastReceiver {

    Context context;
    Sales_Screen salesScreen;
    private werpx.cashiery.productdatabase mydatabase;
    private Boolean downloadcondition,firsttime;
    barcodevalue mvalue;
    int i=0;


    @Override
    public void onReceive(Context context, Intent intent) {

        mydatabase = new werpx.cashiery.productdatabase(context);
        downloadcondition=true;
        firsttime=true;
        mvalue=new barcodevalue();


        this.context=context;
        salesScreen=new Sales_Screen();


        if (haveNetworkConnection())
        {

            context.startService(new Intent(context,Downloadimageservice.class));


        }
        else{
         context.stopService(new Intent(context,Downloadimageservice.class));

        }

    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
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














}
