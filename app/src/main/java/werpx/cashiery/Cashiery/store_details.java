package werpx.cashiery.Cashiery;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.hbb20.CountryCodePicker;
import com.mynameismidori.currencypicker.CurrencyPicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
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
import werpx.cashiery.storemodelbyid.Store;
import werpx.cashiery.storemodelbyid.Storeroot;

public class store_details extends AppCompatActivity {


    private TextView Store_address;
    private static final int image = 101;
    private android.app.AlertDialog.Builder builder;
    private android.app.AlertDialog chossewaydialog;
    private final static int PLACE_PICKER_REQUEST = 999;
    private Geocoder geocoder;
    private List<Address> addresses;
    private Boolean imgcond;
    private LinearLayout Map_Open;
    private CountryCodePicker ccp;
    private TextView storename, storeadmin, storeaddress, adminphone, passadmin;
    private ImageView imageview, placepicker;
    private LinearLayout adminlinear, storelinear, phonelinear, addresslinear, passlinear;
    private EditText admin, store, phone, address, pass;
    private Call<ResponseBody> mycall;
    private Button savechange;
    private ImageView imageadmin, imagestore, imagemobile, imageaddress, imagepass;
    private RelativeLayout adminrelative, storerelative, passrelative, addressrelative, phonerelative;
    private werpx.cashiery.productdatabase mydatabase;

    private TextView admindone, admincancel, storedone, storecancel, phonedone, phonecancel, addressdone, addresscancel, codenumber, passdonee, passcancell;
    private SharedPreferences prefs;
    private String usertoken, storeid, lastaddressstore, idfmcg, idpharm, typestore, valuecategory;
    private FrameLayout updateimglayout;
    private Call<Storeroot> storecall;


    private ProgressBar imguploadprogress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_details);

        typestore = getSharedPreferences("storecategory", Context.MODE_PRIVATE).getString("type", "");

        SharedPreferences mprefrence = getSharedPreferences("categories", Context.MODE_PRIVATE);
        idfmcg = mprefrence.getString("fmcgid", "");
        idpharm = mprefrence.getString("pharmid", "");
        if (typestore.trim().equals("market")) {
            valuecategory = idfmcg;
        } else {
            valuecategory = idpharm;

        }


        imgcond = true;
        Store_address = findViewById(R.id.storeaddressup);
        placepicker = findViewById(R.id.openplacepickerup);
        Map_Open = findViewById(R.id.openmapup);
        ccp = findViewById(R.id.pickerdetails);

        String StoreCode = getSharedPreferences("storedetails", Context.MODE_PRIVATE).getString("codestore", "0");

        prefs = getSharedPreferences("token", Context.MODE_PRIVATE);
        usertoken = prefs.getString("usertoken", "def");
        lastaddressstore = getSharedPreferences("storedetails", Context.MODE_PRIVATE).getString("storeaddress", "none");

        geocoder = new Geocoder(this, Locale.getDefault());
        imguploadprogress = findViewById(R.id.imgprog);


        codenumber = findViewById(R.id.storecode);
        codenumber.setText(StoreCode);
        Store_address.setText(lastaddressstore);
        adminrelative = findViewById(R.id.relativeadmin);
        storerelative = findViewById(R.id.relativestore);
        passrelative = findViewById(R.id.relativeass);
        phonerelative = findViewById(R.id.relativemob);
        updateimglayout = findViewById(R.id.updateimgframe);

        imageadmin = findViewById(R.id.imgadmin);
        imagestore = findViewById(R.id.imgstore);

        imagemobile = findViewById(R.id.imgphone);
        imagepass = findViewById(R.id.imgpass);

        admin = findViewById(R.id.edittextadminname);
        store = findViewById(R.id.edittextstornam);
        phone = findViewById(R.id.edittextphon);
        address = findViewById(R.id.edittextstoreaddress);
        savechange = findViewById(R.id.savechangessD);

        admindone = findViewById(R.id.doneadminname);
        admincancel = findViewById(R.id.canceladminname);
        storedone = findViewById(R.id.donestoree);
        storecancel = findViewById(R.id.cancelstoree);
        phonedone = findViewById(R.id.donephone);
        phonecancel = findViewById(R.id.cancelphone);
        addressdone = findViewById(R.id.doneaddress);
        addresscancel = findViewById(R.id.canceladdress);

        adminlinear = findViewById(R.id.adminedit);
        storelinear = findViewById(R.id.storenameedit);
        phonelinear = findViewById(R.id.phoneedit);
        addresslinear = findViewById(R.id.addressedit);

        storename = findViewById(R.id.namesD);
        storeadmin = findViewById(R.id.nameaD);

        imageview = findViewById(R.id.imgD);
        adminphone = findViewById(R.id.phoneD);

        passlinear = findViewById(R.id.passedit);
        pass = findViewById(R.id.edittextretailerpass);
        passdonee = findViewById(R.id.donepass);
        passcancell = findViewById(R.id.cancelapass);
        passadmin = findViewById(R.id.passD);

        SharedPreferences preferences = getSharedPreferences("storedetails", Context.MODE_PRIVATE);
        String namestore = preferences.getString("nam", "");
        String img = preferences.getString("img", "");
        storename.setText(namestore);

        if (!img.equals(""))
        {
            GlideApp.with(this)
                    .load(img)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageview);
        }
        else{
            GlideApp.with(this)
                    .load(getResources().getDrawable(R.drawable.ic_shop))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageview);
        }



        SharedPreferences prefe = getSharedPreferences("retailerdata", Context.MODE_PRIVATE);
        String nam = prefe.getString("nam", "");
        String mob = prefe.getString("mob", "");
        storeid = prefe.getString("idstore", "");


        storeadmin.setText(nam);
        adminphone.setText(mob);


        updateimglayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                chooseimg();


            }
        });


//admin

        Map_Open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openplacepicker();
            }
        });
        imageadmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                adminlinear.setVisibility(View.VISIBLE);
                adminrelative.setVisibility(View.GONE);


            }
        });
        admindone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                storeadmin.setText(admin.getText().toString());
                adminlinear.setVisibility(View.GONE);
                adminrelative.setVisibility(View.VISIBLE);

            }
        });
        admincancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adminlinear.setVisibility(View.GONE);
                adminrelative.setVisibility(View.VISIBLE);
            }
        });

        //store
        imagestore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                storerelative.setVisibility(View.GONE);
                storelinear.setVisibility(View.VISIBLE);
            }
        });

        storedone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storename.setText(store.getText().toString());
                storelinear.setVisibility(View.GONE);
                storerelative.setVisibility(View.VISIBLE);


            }
        });

        storecancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storelinear.setVisibility(View.GONE);
                storerelative.setVisibility(View.VISIBLE);
            }
        });

        //adminphone
        imagemobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phonerelative.setVisibility(View.GONE);
                phonelinear.setVisibility(View.VISIBLE);
            }
        });
        phonedone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                adminphone.setText(phone.getText().toString());
                phonelinear.setVisibility(View.GONE);
                phonerelative.setVisibility(View.VISIBLE);

                String code = ccp.getSelectedCountryCodeWithPlus();


                String retailerphone, retailerpass;
                retailerphone = adminphone.getText().toString().trim();
                StringBuilder phone = new StringBuilder(retailerphone);
                if (phone.toString().equals("")) {

                } else {
                    Character charSequence = phone.charAt(0);
                    if (charSequence == '0') {
                        phone.deleteCharAt(0);
                    }
                }

                String phoneresult = phone.toString();

                String full_number = code + phoneresult;


                adminphone.setText(full_number);
                phonelinear.setVisibility(View.GONE);
                phonerelative.setVisibility(View.VISIBLE);

            }

        });
        phonecancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phonelinear.setVisibility(View.GONE);
                phonerelative.setVisibility(View.VISIBLE);
            }
        });

        //
        imagepass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                passrelative.setVisibility(View.GONE);
                passlinear.setVisibility(View.VISIBLE);

            }
        });

        passdonee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                passadmin.setText(pass.getText().toString());
                passlinear.setVisibility(View.GONE);
                passrelative.setVisibility(View.VISIBLE);
            }
        });
        passcancell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                passlinear.setVisibility(View.GONE);
                passrelative.setVisibility(View.VISIBLE);

            }
        });


        savechange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                synchronized (this) {
                    if (haveNetworkConnection()) {

                        savechange.setClickable(false);
                        savechange.setHintTextColor(Color.GRAY);
                        updateadminname();

                    } else {
                        Toast.makeText(store_details.this, getResources().getString(R.string.connectionfailed), Toast.LENGTH_LONG).show();
                    }

                }

            }
        });


    }

    private void uploadimg(MultipartBody.Part img, RequestBody parmeter) {

        Call<ResponseBody> uploadcall;
        Retrofitclient myretro = Retrofitclient.getInstance();
        Retrofit retrofittok = myretro.getretro();
        final Endpoints myendpoints = retrofittok.create(Endpoints.class);


        uploadcall = myendpoints.uploadimg(img, parmeter);
        uploadcall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.isSuccessful()) {
                    // upload_progress_bar.setVisibility(View.GONE);


                    try {
                        String mresonse = response.body().string();
                        JSONObject uploadcond = new JSONObject(mresonse);
                        String operation = uploadcond.getString("operation");
                        if (operation.trim().equals("success")) {
                            Toast.makeText(store_details.this, "uploaded", Toast.LENGTH_LONG).show();

                        } else {
                            Toast.makeText(store_details.this, "uploaded problem", Toast.LENGTH_LONG).show();

                        }
                        String imagid = uploadcond.getString("id");
                        if (imagid != null) {
                            Log.e("idbimage", imagid);
                            SharedPreferences preferences = getSharedPreferences("imageupdate", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("id", imagid);
                            editor.apply();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                } else {
                    // upload_progress_bar.setVisibility(View.GONE);
                    Toast.makeText(store_details.this, "not uploaded", Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // upload_progress_bar.setVisibility(View.GONE);
                Toast.makeText(store_details.this, t.getMessage(), Toast.LENGTH_LONG).show();


            }
        });

    }

    private File convert_bitmap_to_file(Bitmap mybitmap) {
        File f = new File(this.getCacheDir(), "file");
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

//Convert bitmap to byte array
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        mybitmap.compress(Bitmap.CompressFormat.JPEG, 0 /*ignored for PNG*/, bos);
        byte[] bitmapdata = bos.toByteArray();

//write the bytes in file
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            fos.write(bitmapdata);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return f;

    }

    private void chooseimg() {
        LinearLayout camera, gallery;

        builder = new android.app.AlertDialog.Builder(store_details.this);

        View myview = LayoutInflater.from(store_details.this.getApplicationContext()).inflate(R.layout.chooselayoutfortakepic, null);
        camera = myview.findViewById(R.id.takcam);
        gallery = myview.findViewById(R.id.takgalee);
        builder.setView(myview);
        chossewaydialog = builder.create();
        chossewaydialog.show();


        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 0);

                /*
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , 0);//one can be replaced with any action code
                */
                chossewaydialog.cancel();

            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicture, 1);//zero can be replaced with any action code
                chossewaydialog.cancel();

            }
        });


    }

    private void openplacepicker() {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            // for activty
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
            // for fragment
            //startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0 && resultCode == RESULT_OK && data.getData() != null) {
            imgcond = false;
            imguploadprogress.setVisibility(View.VISIBLE);

            Bitmap selectedImage = null;
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                selectedImage = BitmapFactory.decodeStream(imageStream);
                imageview.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(store_details.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
            File f = convert_bitmap_to_file(selectedImage);
            RequestBody mFile = RequestBody.create(MediaType.parse("image/*"), f);
            MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", f.getName(), mFile);
            RequestBody type = RequestBody.create(MediaType.parse("text/plain"), "3");
            uploadimg(fileToUpload, type);

        }


        if (requestCode == 1 && resultCode == RESULT_OK) {

            imguploadprogress.setVisibility(View.VISIBLE);

            if (data.getExtras() != null) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");


                imageview.setBackground(null);
                imageview.setImageBitmap(photo);

                File f = convert_bitmap_to_file(photo);

                RequestBody mFile = RequestBody.create(MediaType.parse("image/*"), f);
                MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", f.getName(), mFile);
                RequestBody type = RequestBody.create(MediaType.parse("text/plain"), "3");
                uploadimg(fileToUpload, type);

                imgcond = false;


            }
        }


        if (requestCode == PLACE_PICKER_REQUEST && resultCode == RESULT_OK) {
            //Store_Admin.requestFocus();

            SharedPreferences preferences = getSharedPreferences("addressinfo", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();


            Place place = PlacePicker.getPlace(this, data);
            String placeName = String.format("Place: %s", place.getName());
            double latitude = place.getLatLng().latitude;
            double longitude = place.getLatLng().longitude;


            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (addresses != null) {
                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName();


                String latstring = String.valueOf(latitude);
                String lanotstring = String.valueOf(longitude);

                editor.putString("lat", latstring);
                editor.putString("lang", lanotstring);
                editor.putString("city", city);
                editor.putString("address", address);
                editor.apply();


                Store_address.setText(address + "" + city + "" + state + "" + country);


            }


        }

    }


    public void updatestoreinfo() {


        SharedPreferences preferencesaddress = getSharedPreferences("addressinfo", Context.MODE_PRIVATE);
        SharedPreferences preferencesimg = getSharedPreferences("imageupdate", Context.MODE_PRIVATE);

        final String imgid = preferencesimg.getString("id", "");
        String lat = preferencesaddress.getString("lat", "");
        String longt = preferencesaddress.getString("lang", "");
        String city = preferencesaddress.getString("city", "");
        final String address = preferencesaddress.getString("address", "");

        Call<ResponseBody> updatestorecall;
        werpx.cashiery.Retrofitclient myretro = werpx.cashiery.Retrofitclient.getInstance();
        Retrofit retrofittok = myretro.getretro();
        final werpx.cashiery.Endpoints myendpoints = retrofittok.create(werpx.cashiery.Endpoints.class);
        updatestorecall = myendpoints.updatestoreinfo("Bearer " + usertoken, storeid, storeid, lat, longt, address,
                imgid, true, city, "00:00", "00:00", "Egypt Pounds", "2",
                "00:00", "description", "email@email.com", storename.getText().toString(),
                "00:00", "00:00", "0993838", "12345", "werpx",
                valuecategory, "PATCH");

        updatestorecall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                SharedPreferences.Editor editorret;
                SharedPreferences.Editor editorstore;
                SharedPreferences retailer,store;

                if (response.isSuccessful()) {
                    try {
                        String updateresponse = response.body().string();
                        JSONObject addobject = new JSONObject(updateresponse);
                        String opercon = addobject.getString("operation");
                        if (opercon.trim().equals("success")) {
                            String id=getSharedPreferences("retailerdata",Context.MODE_PRIVATE).getString("idstore","");

                            savechange.setClickable(true);
                            savechange.setHintTextColor(Color.BLACK);
                            Toast.makeText(store_details.this, getResources().getString(R.string.updatesuc), Toast.LENGTH_LONG).show();
                            // getstoredetails(id);
                            restart();
                        }
                        else{
                            savechange.setClickable(true);
                            savechange.setHintTextColor(Color.BLACK);
                            Toast.makeText(store_details.this, getResources().getString(R.string.updatefailed), Toast.LENGTH_LONG).show();

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
                savechange.setClickable(true);
                savechange.setHintTextColor(Color.BLACK);

                Toast.makeText(store_details.this, getResources().getString(R.string.connectionfailed), Toast.LENGTH_LONG).show();




            }
        });
    }

    public void updateadminname() {

        String pass = "";
        String check = passadmin.getText().toString();
        if (check.equals(getResources().getString(R.string.newpass))) {
            pass = "";
        } else {
            pass = check;
        }
        SharedPreferences preferences = getSharedPreferences("retailerdata", Context.MODE_PRIVATE);
        String idadmin = preferences.getString("id", "");
        String idstore = preferences.getString("idstore", "");

        werpx.cashiery.Retrofitclient myretro = werpx.cashiery.Retrofitclient.getInstance();
        Retrofit retrofittok = myretro.getretro();
        final werpx.cashiery.Endpoints myendpoints = retrofittok.create(werpx.cashiery.Endpoints.class);
        mycall = myendpoints.updateretailerinfo("Bearer " + usertoken, idadmin, idstore, storeadmin.getText().toString(), adminphone.getText().toString(), pass, adminphone.getText().toString(), "retailerAdmin");
        mycall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.isSuccessful()) {
                    String retailerresponse = null;
                    try {
                        retailerresponse = response.body().string();
                        JSONObject retailerecond = new JSONObject(retailerresponse);
                        String operation = retailerecond.getString("operation");

                        if (operation.trim().equals("success"))
                        {

                        }
                        else{
                            Toast.makeText(store_details.this, getResources().getString(R.string.updatefailed), Toast.LENGTH_LONG).show();

                        }

                        updatestoreinfo();


                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }


            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                Toast.makeText(store_details.this, getResources().getString(R.string.connectionfailed), Toast.LENGTH_LONG).show();


            }
        });
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


    private void getstoredetails(String id)
    {

        werpx.cashiery.Retrofitclient myretro = werpx.cashiery.Retrofitclient.getInstance();
        Retrofit retrofittok = myretro.getretro();
        final werpx.cashiery.Endpoints myendpoints = retrofittok.create(werpx.cashiery.Endpoints.class);
        storecall = myendpoints.getstore("Bearer "+usertoken,id);
        storecall.enqueue(new Callback<Storeroot>() {
            @Override
            public void onResponse(Call<Storeroot> call, Response<Storeroot> response) {
                if (response.isSuccessful())
                {
                    if (response.body().getStores().size()!=0) {
                        Store mstore= response.body().getStores().get(0);
                        String nam = mstore.getName();
                        String img = mstore.getImage().getUrl();
                        String storecode=mstore.getCode();
                        String storeaddress=mstore.getAddress();
                        String storeType=mstore.getCategoryId();



                            if (!img.equals("")) {
                                GlideApp.with(store_details.this)
                                        .load(img)
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .into(imageview);
                            }
                            if (!nam.equals("")) {
                               storename.setText(nam);

                            }
                            if (!storeaddress.equals(""))
                            {
                                Store_address.setText(storeaddress);
                            }



                        SharedPreferences preferences = getSharedPreferences("storedetails", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("nam", nam);
                        editor.putString("img", img);
                        editor.putString("codestore",storecode);
                        editor.putString("storeaddress",storeaddress);
                        editor.putString("storetype",storeType);
                        if (storeType.trim().equals("market"))
                        {
                            editor.putString("categoryid","4BB24B08-6F74-4A30-9806-124293F2D262");
                            editor.putString("producerid","E007875D-6CD1-4D3E-9CD0-0AE6DAF1AED7");
                            editor.putString("unitid","F5A99CB5-924D-4D40-8404-E31C7EF6020F");
                        }
                        else{

                            editor.putString("categoryid","B5172DEC-69BD-4E69-87FD-65B6118775F7");
                            editor.putString("producerid","E062981E-FE8D-41CC-918D-7982F2D13C9F");
                            editor.putString("unitid","917E10F2-D38B-4F21-98C3-895FE4753009");

                        }
                        editor.apply();
                    }
                }
                else{
                    Toast.makeText(store_details.this,response.message(),Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Storeroot> call, Throwable t) {


            }
        });





    }

    public void restart ()
    {
        Intent i = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage( getBaseContext().getPackageName() );
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

}


