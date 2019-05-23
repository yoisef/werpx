package werpx.cashiery.Cashiery;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;
import com.hbb20.CountryCodePicker;


import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.SSLContext;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import werpx.cashiery.Endpoints;
import werpx.cashiery.R;
import werpx.cashiery.categoriesmodel.Category;
import werpx.cashiery.categoriesmodel.categoryroot;
import werpx.cashiery.modelsauth.Roottoken;
import werpx.cashiery.storemodelbyid.Storeroot;

public class loginactivity extends AppCompatActivity {

    private TextView sign_up,arabiclan,englishlan;
    private Call<Roottoken> mcall;
    private EditText log_phone,log_pass;

    private CountryCodePicker ccp;
    private Button signin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginactivity);

        sslconnection();
        requestPermission();
        getcategories();
        ccp = findViewById(R.id.logcodepicker);
       log_phone=findViewById(R.id.logphone);
        log_pass=findViewById(R.id.logpass);
        arabiclan=findViewById(R.id.arabiclanguage);
        englishlan=findViewById(R.id.englishlanguage);

        signin=findViewById(R.id.signinbutton);

        sign_up=findViewById(R.id.retailer_new);

        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(loginactivity.this,pharmormarket.class));
            }
        });



        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               boolean result= validesignin();
               if (result)
               {
                  signin.setClickable(false);
                  signin.setTextColor(Color.GRAY);

               }



            }
        });

        arabiclan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                changelanguage("ar");
                restart();


            }
        });

        englishlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                changelanguage("en");
                restart();

            }
        });
    }
    public void changelanguage(String localcode)
    {



        Resources res=getResources();
        DisplayMetrics dm=res.getDisplayMetrics();
        Configuration conf=res.getConfiguration();
        if (Build.VERSION.SDK_INT >= 17)
        {
            conf.setLocale(new Locale(localcode.toLowerCase()));
        }
        else{
            conf.locale=new Locale(localcode.toLowerCase());
        }
        res.updateConfiguration(conf,dm);
    }
    public void restart ()
    {
        Intent i = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage( getBaseContext().getPackageName() );
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }



    private boolean validesignin()
  {

      String code=ccp.getSelectedCountryCodeWithPlus();


      String retailerphone,retailerpass;
      retailerphone=log_phone.getText().toString().trim();
      StringBuilder phone=new StringBuilder(retailerphone);
      if (phone.toString().equals("")){

      }
      else{
          Character charSequence=phone.charAt(0);
          if(charSequence=='0')
          {
              phone.deleteCharAt(0);
          }
      }

      String phoneresult=phone.toString();

      String full_number=code+phoneresult;
      retailerpass=log_pass.getText().toString().trim();

      if (retailerphone.isEmpty())
      {
          log_phone.setError(getResources().getString(R.string.emailV));
          log_phone.requestFocus();
          return false;
      }


     if(retailerpass.isEmpty())
      {
          log_pass.setError(getResources().getString(R.string.passV));
          log_pass.requestFocus();
          return false;
      }




      signin(full_number,retailerpass);

      return true;
  }

    public void signin(String email ,String password)
    {

        werpx.cashiery.Retrofitclient myretro = werpx.cashiery.Retrofitclient.getInstance();
        Retrofit retrofittok = myretro.getretro();
        final werpx.cashiery.Endpoints myendpoints = retrofittok.create(werpx.cashiery.Endpoints.class);


        mcall = myendpoints.signuser("application/x-www-form-urlencoded",email,password);
        mcall.enqueue(new Callback<Roottoken>() {
            @Override
            public void onResponse(Call<Roottoken> call, Response<Roottoken> response) {
                if (response.isSuccessful())
                {
                    signin.setClickable(true);
                    signin.setTextColor(Color.BLACK);
                   String thetoken= response.body().getData().getToken();
                    SharedPreferences prefs = getSharedPreferences("token", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor=prefs.edit();
                    editor.putString("usertoken",thetoken);
                    editor.apply();
                    Toast.makeText(loginactivity.this,"Successful login",Toast.LENGTH_LONG).show();
                    startActivity(new Intent(loginactivity.this,MainActivity.class));
                    finish();
                }
                else
                {
                    signin.setClickable(true);
                    signin.setTextColor(Color.BLACK);
                    Toast.makeText(loginactivity.this,"Wrong email or password",Toast.LENGTH_LONG).show();



                }
            }

            @Override
            public void onFailure(Call<Roottoken> call, Throwable t) {
                signin.setClickable(true);
                signin.setTextColor(Color.BLACK);
                Toast.makeText(loginactivity.this,"Connection Failed",Toast.LENGTH_LONG).show();


            }
        });


    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_NETWORK_STATE,Manifest.permission.ACCESS_WIFI_STATE},
                1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                   // Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();

                    // main logic
                } else {
                  //  Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {
                            showMessageOKCancel("You need to allow access permissions",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermission();
                                            }
                                        }
                                    });
                        }
                    }
                }
                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(loginactivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
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
    public void getcategories()
    {
        Call<categoryroot> mcall;
        werpx.cashiery.Retrofitclient myretro= werpx.cashiery.Retrofitclient.getInstance();
        Retrofit retrofittok=  myretro.getretro();
        final werpx.cashiery.Endpoints myendpoints = retrofittok.create(werpx.cashiery.Endpoints.class);
        mcall=myendpoints.getcategories();
        mcall.enqueue(new Callback<categoryroot>() {
            @Override
            public void onResponse(Call<categoryroot> call, Response<categoryroot> response) {

                if (response.isSuccessful())
                {
                    List<Category> categoriess=response.body().getCategories();
                    if (categoriess.size()!=0)
                    {

                        String fmcgidcategory=categoriess.get(0).getId();
                        String pharmaidcategory=categoriess.get(1).getId();
                        SharedPreferences preferences=getSharedPreferences("categories",Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor=preferences.edit();
                        editor.putString("fmcgid",fmcgidcategory);
                        editor.putString("pharmid",pharmaidcategory);
                        editor.putString(fmcgidcategory,"market");
                        editor.putString(pharmaidcategory,"pharmacy");
                        editor.apply();


                    }
                }

            }

            @Override
            public void onFailure(Call<categoryroot> call, Throwable t) {

            }
        });

    }

}
