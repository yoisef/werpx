package werpx.cashiery;

import android.arch.lifecycle.ViewModelProviders;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;
import com.google.gson.Gson;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;

import retrofit2.Call;
import werpx.cashiery.RoomLivedata.Cashierydatabase;
import werpx.cashiery.RoomLivedata.Cashieryviewmodel;
import werpx.cashiery.productmodels.getallproductsroot;


public class Utils {

    Context con;
    private Call<getallproductsroot> callproducts;
    Gson gson;


    private productdatabase mydatabase;
    DbBitmapUtility dbBitmapUtility;
    Cashierydatabase db;




    public Utils(Context context)
    {
        this.con=context;
        mydatabase = new productdatabase(context);
        dbBitmapUtility=new DbBitmapUtility();
        gson=new Gson();


    }

    public String getstoretype()
    {
        String preferences = con.getSharedPreferences("storedetails", Context.MODE_PRIVATE).getString("categoryid","");
        return preferences;

    }
    public Gson getGson() {
        return gson;
    }


    public String gettoken()
    {
        String token=con.getSharedPreferences("token",Context.MODE_PRIVATE).getString("usertoken","");
        return token;
    }

    public boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) con.getSystemService(Context.CONNECTIVITY_SERVICE);
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

    public int checkapplanguage()
    {
        Configuration configuration;
        int applanguage;
        configuration = con.getResources().getConfiguration();
        applanguage= configuration.getLayoutDirection();
        return applanguage;
    }


    public String convertToArabic (String value){

        String newValue = (((((((((((value + "")
                .replaceAll("1", "١")).replaceAll("2", "٢"))
                .replaceAll("3", "٣")).replaceAll("4", "٤"))
                .replaceAll("5", "٥")).replaceAll("6", "٦"))
                .replaceAll("7", "٧")).replaceAll("8", "٨"))
                .replaceAll("9", "٩")).replaceAll("0", "٠"));
        return newValue;
    }

    public String convertToEnglish (String value){

        String newValue = (((((((((((value + "")
                .replaceAll("١", "1")).replaceAll("٢", "2"))
                .replaceAll("٣", "3")).replaceAll("٤", "4"))
                .replaceAll("٥", "5")).replaceAll("٦", "6"))
                .replaceAll("٧", "7")).replaceAll("٨", "8"))
                .replaceAll("٩", "9")).replaceAll("٠", "0"));
        return newValue;
    }



    public DbBitmapUtility getDbBitmapUtility() {
        return dbBitmapUtility;
    }





    public productdatabase getMydatabase() {
        return mydatabase;
    }



}
