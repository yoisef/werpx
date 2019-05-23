package werpx.cashiery.Cashiery;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hbb20.CountryCodePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import werpx.cashiery.Endpoints;

import werpx.cashiery.GlideApp;
import werpx.cashiery.R;
import werpx.cashiery.Retrofitclient;
import werpx.cashiery.modelsauth.Roottoken;
import werpx.cashiery.storemodel.Storecode;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private TextView renam,remobile,storetxtvalidation;
    private EditText retemail,retapass,retailer_retype_pass;
    private Button register;
    private EditText Search_Store;
    private Call<Storecode> callstore;
    private Call<ResponseBody> registerretailer;
    private Call<Roottoken> mcall;
    private ImageView storeimage;
    private TextView storenamee,storeaddresss;
    private CountryCodePicker ccp;
    String storeid,valuecategory,idfmcg,idpharm,typestore;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);



        typestore=getSharedPreferences("storecategory",Context.MODE_PRIVATE).getString("type","");
        SharedPreferences preferences=getSharedPreferences("categories",Context.MODE_PRIVATE);
        idfmcg= preferences.getString("fmcgid","");
        idpharm= preferences.getString("pharmid","");
        storenamee=findViewById(R.id.namstoreS);
        storeaddresss=findViewById(R.id.addressstoreS);
        storeimage=findViewById(R.id.storeimg);
        renam=findViewById(R.id.Name_retailer);
        remobile=findViewById(R.id.Phone_retailer);
        register=findViewById(R.id.registerr);
        retapass=findViewById(R.id.Pass_retailer);
        retailer_retype_pass=findViewById(R.id.repass_retailer);
        ccp=findViewById(R.id.ccpseller);

        Search_Store=findViewById(R.id.searchstore);

        Search_Store.requestFocus();


        if (typestore.trim().equals("market"))
        {
            Search_Store.setHint(getResources().getString(R.string.storeidV));

            valuecategory=idfmcg;

        }
        else {

            storenamee.setHint(R.string.pharmname);
            Search_Store.setHint(getResources().getString(R.string.codepharm));

            valuecategory=idpharm;
        }







        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validteretailer();

            }
        });
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera

        Search_Store.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {



            }

            @Override
            public void afterTextChanged(Editable editable) {

                searchstorebycode(editable.toString());


            }
        });
    }


    private void validteretailer()
    {

        register.setClickable(false);
        register.setTextColor(Color.GRAY);
        final String storid,nam,mob,pas1,pas2;


        storid=Search_Store.getText().toString();
        nam=renam.getText().toString().trim();
        pas1=retapass.getText().toString().trim();
        pas2=retailer_retype_pass.getText().toString().trim();
        mob=remobile.getText().toString().trim();


        if (storid.isEmpty())
        {
            storetxtvalidation.setError(getResources().getString(R.string.storeidV));
            storetxtvalidation.requestFocus();
            register.setClickable(true);
            register.setTextColor(Color.BLACK);
            return;
        }
        if (nam.isEmpty())
        {
            renam.setError(getResources().getString(R.string.namV));
            renam.requestFocus();
            register.setClickable(true);
            register.setTextColor(Color.BLACK);
            return;
        }
        if (mob.isEmpty())
        {
            remobile.setError(getResources().getString(R.string.mobileV));
            remobile.requestFocus();
            register.setClickable(true);
            register.setTextColor(Color.BLACK);
            return;
        }


        if (pas1.isEmpty())
        {
            retapass.setError(getResources().getString(R.string.passV));
            retapass.requestFocus();
            register.setClickable(true);
            register.setTextColor(Color.BLACK);

            return;
        }
        if (pas1.length()<6)
        {
            retapass.setError(getResources().getString(R.string.minumV));
            retapass.requestFocus();
            register.setClickable(true);
            register.setTextColor(Color.BLACK);
            return;
        }
        if (pas2.isEmpty())
        {
            retailer_retype_pass.setError(getResources().getString(R.string.passRV));
            retailer_retype_pass.requestFocus();
            register.setClickable(true);
            register.setTextColor(Color.BLACK);

            return;
        }
        if (!pas2.equals(pas1))
        {
            retailer_retype_pass.setError(getResources().getString(R.string.passRV));
            retailer_retype_pass.requestFocus();
            register.setClickable(true);
            register.setTextColor(Color.BLACK);
            return;
        }

        String code=ccp.getSelectedCountryCodeWithPlus();
        StringBuilder phone=new StringBuilder(mob);
        Character charSequence=phone.charAt(0);
        if(charSequence=='0')
        {
            phone.deleteCharAt(0);
        }
        String phoneresult=phone.toString();
        final String full_number=code+phoneresult;

        Retrofitclient myretro=Retrofitclient.getInstance();
        Retrofit retrofittok=  myretro.getretro();
        final Endpoints myendpoints = retrofittok.create(Endpoints.class);

        registerretailer=myendpoints.registerretailer(storeid,nam,full_number,pas1,full_number,"retailerAdmin",valuecategory);
        registerretailer.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

               // registerpro.setVisibility(View.GONE);
                if (response.isSuccessful())
                {
                    try {
                        String retailerresponse=response.body().string();
                        JSONObject retailerecond=new JSONObject(retailerresponse);
                        String operation=retailerecond.getString("operation");
                        if (operation.trim().equals("success"))
                        {

                            register.setClickable(true);
                           register.setTextColor(Color.BLACK);

                            Toast.makeText(MapsActivity.this,"Successful Register",Toast.LENGTH_LONG).show();

                            signin(full_number,pas1);



                        }
                        else{
                            register.setClickable(true);
                            register.setTextColor(Color.BLACK);
                            String reason=retailerecond.getString("reason");
                            Toast.makeText(MapsActivity.this,getResources().getString(R.string.dublicatenum),Toast.LENGTH_LONG).show();

                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                Toast.makeText(MapsActivity.this,getResources().getString(R.string.connectionfailed),Toast.LENGTH_LONG).show();
                register.setClickable(true);
                register.setTextColor(Color.BLACK);

            }
        });


        //







    }

    private void searchstorebycode(String code)
    {
        werpx.cashiery.Retrofitclient myretro= werpx.cashiery.Retrofitclient.getInstance();
        Retrofit retrofit=  myretro.getretro();

        final werpx.cashiery.Endpoints myendpoints = retrofit.create(werpx.cashiery.Endpoints.class);

        callstore = myendpoints.getstorebycode(code);
        callstore.enqueue(new Callback<Storecode>() {
            @Override
            public void onResponse(Call<Storecode> call, Response<Storecode> response) {

                if (response.isSuccessful())
                {
                    if (response.body().getStore()!=null)
                    {
                        String img= response.body().getStore().getImage().getUrl();
                        String storenam=response.body().getStore().getName();
                        String storeaddress=response.body().getStore().getAddress();
                        Double lati=response.body().getStore().getLatitude();
                        Double longtitude=response.body().getStore().getLongitude();
                       storeid=response.body().getStore().getId();

                        GlideApp.with(MapsActivity.this)
                                .load(img)
                                .into(storeimage);
                        LatLng Location = new LatLng(lati, longtitude);

                        mMap.addMarker(new MarkerOptions().position(Location).title("Location"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(Location));
                        mMap.animateCamera(CameraUpdateFactory.zoomBy(8));
                        storenamee.setText(storenam);
                        storeaddresss.setText(storeaddress);




                    }
                }


            }

            @Override
            public void onFailure(Call<Storecode> call, Throwable t) {

                Toast.makeText(MapsActivity.this,getResources().getString(R.string.connectionfailed),Toast.LENGTH_LONG).show();

            }
        });
    }

    public void signin(String email ,String password)
    {


        Retrofitclient myretro=Retrofitclient.getInstance();
        Retrofit retrofittok=  myretro.getretro();
        final Endpoints myendpoints = retrofittok.create(Endpoints.class);

        mcall = myendpoints.signuser("application/x-www-form-urlencoded",email,password);
        mcall.enqueue(new Callback<Roottoken>() {
            @Override
            public void onResponse(Call<Roottoken> call, Response<Roottoken> response) {
                if (response.isSuccessful())
                {

                    String thetoken= response.body().getData().getToken();
                    SharedPreferences prefs = getSharedPreferences("token", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor=prefs.edit();
                    editor.putString("usertoken",thetoken);
                    editor.apply();
                    Toast.makeText(MapsActivity.this,"Successful login",Toast.LENGTH_LONG).show();
                    startActivity(new Intent(MapsActivity.this, MainActivity.class));
                    finishAffinity();


                }
                else
                {
                    Toast.makeText(MapsActivity.this,"Wrong email or password",Toast.LENGTH_LONG).show();



                }
            }

            @Override
            public void onFailure(Call<Roottoken> call, Throwable t) {

                Toast.makeText(MapsActivity.this,"Connection Failed",Toast.LENGTH_LONG).show();


            }
        });


    }


}
