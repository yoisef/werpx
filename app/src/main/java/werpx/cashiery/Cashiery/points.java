package werpx.cashiery.Cashiery;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.LayoutDirection;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


import werpx.cashiery.GlideApp;
import werpx.cashiery.R;
import werpx.cashiery.Utils;
import werpx.cashiery.pointsmodel.Pointroot;

public class points extends AppCompatActivity {

    private ImageView imgpoints;
    private TextView namestore,creditnumber,exchangebut;
    private Call<Pointroot> callpoints;
    private SharedPreferences prefs;
    private String usertoken,retailerid;
    Toolbar mytoolbar;
    Utils utils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_points);
        utils=new Utils(this);

        mytoolbar=findViewById(R.id.toolbarpoint);
        setSupportActionBar(mytoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        imgpoints=findViewById(R.id.pointimg);
        namestore=findViewById(R.id.pointstorename);
        creditnumber=findViewById(R.id.pointcreditnum);
        exchangebut=findViewById(R.id.pointexchange);
        retailerid= getSharedPreferences("retailerdata",Context.MODE_PRIVATE).getString("id","n");
        prefs = getSharedPreferences("token", Context.MODE_PRIVATE);
        usertoken = prefs.getString("usertoken", "def");

        SharedPreferences preferences = getSharedPreferences("storedetails", Context.MODE_PRIVATE);
        String name_store=preferences.getString("nam","");
        String img=preferences.getString("img","");

        if (!img.equals(""))
        {
            GlideApp.with(this)
                    .load(img)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imgpoints);
        }
        else{
            GlideApp.with(this)
                    .load(getResources().getDrawable(R.drawable.ic_shop))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imgpoints);
        }


        namestore.setText(name_store);


            getretailerpoints();


            /*
            Toast.makeText(points.this,"else",Toast.LENGTH_SHORT).show();

            String point=getSharedPreferences("pointsync",Context.MODE_PRIVATE).getString("point","0");
            if (!point.equals("0"))
            {
                int applanguage = checkapplanguage();
                if (applanguage == LayoutDirection.RTL) {

                    String arabicpoints = convertToArabic(point);
                    Double pointdouble=Double.parseDouble(arabicpoints);
                    creditnumber.setText(new DecimalFormat("##.##").format(pointdouble));
                } else {
                    Double pointdouble=Double.parseDouble(point);
                    creditnumber.setText(new DecimalFormat("##.##").format(pointdouble));
                }
            }

        */

    }

    private void getretailerpoints()
    {

        werpx.cashiery.Retrofitclient myretro= werpx.cashiery.Retrofitclient.getInstance();
        Retrofit retrofit=  myretro.getretro();

        final werpx.cashiery.Endpoints myendpoints = retrofit.create(werpx.cashiery.Endpoints.class);

        callpoints=myendpoints.getretailerpoints("Bearer "+usertoken,retailerid);
        callpoints.enqueue(new Callback<Pointroot>() {
            @Override
            public void onResponse(Call<Pointroot> call, Response<Pointroot> response) {
                int fullpoints=0;

            if (response.isSuccessful())
            {
              if ( response.body().getData().getPoints()!=null)
              {
                String pointscurrent=  response.body().getData().getPoints().getCurrentBalance().toString();
                  int applanguage = checkapplanguage();
                  if (applanguage == LayoutDirection.RTL) {
                      String arabicpoints = convertToArabic(pointscurrent);
                      creditnumber.setText(arabicpoints);
                  } else {
                      creditnumber.setText(pointscurrent);
                  }
              }
              else{
                  Toast.makeText(points.this,"No Sales Yet",Toast.LENGTH_LONG).show();

              }
            }


                }


            @Override
            public void onFailure(Call<Pointroot> call, Throwable t) {

                if (t instanceof IOException) {
                    Toast.makeText(points.this,getResources().getString(R.string.connectionfailed),Toast.LENGTH_LONG).show();
                    // logging probably not necessary
                }
                else {
                    Toast.makeText(points.this,"Failed to get Data",Toast.LENGTH_LONG).show();
                    // todo log to some central bug tracking service
                }

            }
        });
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

    public int checkapplanguage()
    {
        Configuration configuration;
        int applanguage;
        configuration =getResources().getConfiguration();
        applanguage= configuration.getLayoutDirection();
        return applanguage;
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
