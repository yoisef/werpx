package werpx.cashiery.Cashiery;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

import javax.net.ssl.SSLContext;

import werpx.cashiery.LocaleHelper;
import werpx.cashiery.R;
import werpx.cashiery.TypefaceUtil;


public class Launcher_activity extends AppCompatActivity {


    private werpx.cashiery.RoomDatabase.productViewmodel mWordViewModel;
    private Configuration configuration;
    private int applanguage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher_activity);

        FirebaseApp.initializeApp(this);

        FirebaseMessaging.getInstance().subscribeToTopic("cashieryglobal");


        configuration = getResources().getConfiguration();
        applanguage= configuration.getLayoutDirection();


        if (applanguage==0)
        {
            TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/proxma.ttf");

        }
        else{

            TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/arabic.ttf");
        }

        sslconnection();
        mWordViewModel = ViewModelProviders.of(this).get(werpx.cashiery.RoomDatabase.productViewmodel.class);



        Handler myhandler=new Handler();
        myhandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences prefs = getSharedPreferences("token", Context.MODE_PRIVATE);
                String condition= prefs.getString("usertoken","def");
                choosebeginingactivity(condition);
                finishAffinity();

            }
        },3000);
    }




    public void choosebeginingactivity(String condition)
    {



       if (condition.equals("def"))
       {
           startActivity(new Intent(Launcher_activity.this, werpx.cashiery.Cashiery.loginactivity.class));
       }
       else
       {
           startActivity(new Intent(Launcher_activity.this, werpx.cashiery.Cashiery.MainActivity.class));
       }

    }

    public void sslconnection()
    {
        try {
            ProviderInstaller.installIfNeeded(getApplicationContext());
            SSLContext sslContext;
            sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, null, null);
            sslContext.createSSLEngine();
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException
                | NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }
    }
}
