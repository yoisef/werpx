package werpx.cashiery.Cashiery;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
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
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import werpx.cashiery.DbBitmapUtility;
import werpx.cashiery.Endpoints;
import werpx.cashiery.R;
import werpx.cashiery.Retrofitclient;
import werpx.cashiery.modelsauth.Roottoken;

public class Storeinfo extends AppCompatActivity {


    private TextView imgadd;

    private static final int image = 101;
    private android.app.AlertDialog.Builder builder;
    private android.app.AlertDialog chossewaydialog;
    private Button register_store;
    CurrencyPicker picker;
    private Spinner distruborspinnerr,  subdistributer;
    private RelativeLayout addimg;

    private ImageView storeimage,placepicker;
    private Uri uriprofileimage;
    private Boolean imgcond;
    private final static int PLACE_PICKER_REQUEST = 999;
    private Geocoder geocoder;
    private List<Address> addresses;
    private EditText Store_Name,Store_Admin,Store_pass,Store_passconfirm,Store_phone,Store_desc,Store_deletecharge;
    private Call<ResponseBody> uploadcall,registerstore,registerretailer;
    private ProgressBar upload_progress_bar,registerpro;
    private LinearLayout Map_Open;
    private Call<Roottoken> mcall;
    private CountryCodePicker ccp;
    private TextView Store_address;
    private werpx.cashiery.productdatabase mydatabase;
    String idfmcg,idpharm,valuecategory,typestore;
    private final long DELAY = 1000; //
    private TextWatcher watcher;
    private Timer timer = new Timer();
    private DbBitmapUtility converter;
    private Bitmap selectedImage;
    private ProgressDialog progressDialog;
    Boolean registerstorecon=false;
    Boolean registerretailercon=false;
    Boolean uploadimgcon=false;
    Button confirmbut;
    String mVerificationId;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    FirebaseAuth mAuth;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storeinfo);

        converter=new DbBitmapUtility();
        mAuth= FirebaseAuth.getInstance();
        mydatabase = new werpx.cashiery.productdatabase(this);
        typestore=getSharedPreferences("storecategory",Context.MODE_PRIVATE).getString("type","");
        placepicker=findViewById(R.id.openplacepicker);
        Store_address=findViewById(R.id.storeaddress);

        imgcond=true;
        geocoder = new Geocoder(this, Locale.getDefault());
        Store_Name=findViewById(R.id.storernam);
        Store_phone=findViewById(R.id.storephone);
        Store_pass=findViewById(R.id.storepass);
        Store_passconfirm=findViewById(R.id.storpassconfirm);
        register_store=findViewById(R.id.storesubsc);
        Store_Admin=findViewById(R.id.storeadmin);
        storeimage=findViewById(R.id.storeimg);
        imgadd=findViewById(R.id.addphototxt);
        upload_progress_bar=findViewById(R.id.proimage);
        Map_Open=findViewById(R.id.openmap);
        registerpro=findViewById(R.id.prosub);
        ccp=findViewById(R.id.storecodepicker);
        addimg = findViewById(R.id.relativeLayout);










        SharedPreferences preferences=getSharedPreferences("categories",Context.MODE_PRIVATE);
        idfmcg= preferences.getString("fmcgid","");
        idpharm= preferences.getString("pharmid","");
        if (typestore.trim().equals("market"))
        {

            valuecategory=idfmcg;
        }
        else{
            Store_Name.setHint(getResources().getString(R.string.pharmname));

            valuecategory=idpharm;

        }









        register_store.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                register_store.setClickable(false);
                register_store.setTextColor(Color.GRAY);
               // intilazeviewswithvaildateandregister();
                retreivebitmapstore();

            }
        });





        Map_Open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                Store_address.setError(null);

                vaildstore();

            }
        });







        //   distruborspinnerr=findViewById(R.id.storedistrbter);
        //    subdistributer=findViewById(R.id.storesubdis);


        addimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                imgadd.setError(null);
               opencamera();

            }
        });


    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK && data.getData() != null) {
            imgcond=false;



            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                selectedImage = BitmapFactory.decodeStream(imageStream);
                storeimage.setImageBitmap(selectedImage);

                storeimgbitmaptoshared(selectedImage);



            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(Storeinfo.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
            /*
            File f=convert_bitmap_to_file(selectedImage);
            RequestBody mFile = RequestBody.create(MediaType.parse("image/*"), f);
            MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", f.getName(), mFile);
            RequestBody type = RequestBody.create(MediaType.parse("text/plain"), "3");
            uploadimg(fileToUpload,type);
*/
        }


        if (requestCode == 1 && resultCode == RESULT_OK)
        {



            if (data.getExtras()!=null)
            {
               selectedImage = (Bitmap) data.getExtras().get("data");


                storeimage.setBackground(null);

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                selectedImage.compress(Bitmap.CompressFormat.JPEG, 100 /*ignored for PNG*/, bos);
                byte[] bitmapdata = bos.toByteArray();
                Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);


                storeimage.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 150, 150, false));

              //  storeimage.setImageBitmap(bitmap);

               // storeimgbitmaptoshared(selectedImage);
/*
                File f=convert_bitmap_to_file(photo);

                RequestBody mFile = RequestBody.create(MediaType.parse("image/*"), f);
                MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", f.getName(), mFile);
                RequestBody type = RequestBody.create(MediaType.parse("text/plain"), "3");
                uploadimg(fileToUpload,type);
                */

                imgcond=false;



            }

        }

        if (requestCode==PLACE_PICKER_REQUEST&&resultCode==RESULT_OK)
        {


            Place place = PlacePicker.getPlace(this, data);
            String placeName = String.format("Place: %s", place.getName());
            double latitude = place.getLatLng().latitude;
            double longitude = place.getLatLng().longitude;
            SharedPreferences preferences=getSharedPreferences("image",Context.MODE_PRIVATE);


            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (addresses!=null)
            {
                if (addresses.size()!=0)
                {
                    String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    String city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
                    String country = addresses.get(0).getCountryName();
                    String postalCode = addresses.get(0).getPostalCode();
                    String knownName = addresses.get(0).getFeatureName();

                    Store_address.setText(address+""+city+""+state+""+country);
                    SharedPreferences sharedPreferences=getSharedPreferences("storeinfor",Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putString("lag",String.valueOf(longitude));
                    editor.putString("lat",String.valueOf(latitude));
                    editor.putString("city",city);
                    editor.apply();


                    String idimg= preferences.getString("id",null);
                    if (idimg!=null)
                    {
                        //  registerstore(idimg,Store_address.getText().toString(),Store_Name.getText().toString(),String.valueOf(longitude),String.valueOf(latitude),"1",city,"description",valuecategory);

                    }
                }

            }



        }
    }





    private void openplacepicker()
    {
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

    private void storeimgbitmaptoshared(Bitmap realImage)
    {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        realImage.compress(Bitmap.CompressFormat.JPEG, 0, baos);
        byte[] b = baos.toByteArray();

        String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);


        SharedPreferences shre = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit=shre.edit();
        edit.putString("image_data",encodedImage);
        edit.apply();
    }

    private void retreivebitmapstore()
    {

        if (selectedImage!=null)
        {
            File f=convert_bitmap_to_file(selectedImage);
            RequestBody mFile = RequestBody.create(MediaType.parse("image/*"), f);
            MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", f.getName(), mFile);
            RequestBody type = RequestBody.create(MediaType.parse("text/plain"), "3");
            uploadimg(fileToUpload,type);
        }
        else{
            intilazeviewswithvaildateandregister();
        }




    }

    private void registerstore(String imgid, String address, String storenam, String lon, String lat, String deletecharge, String cityy, String description, String storecategory, final String fulnum)
    {


        if (!registerstorecon)
        {


            Retrofitclient myretro=Retrofitclient.getInstance();
            Retrofit retrofittok=  myretro.getretro();
            final Endpoints myendpoints = retrofittok.create(Endpoints.class);

            registerstore=myendpoints.registerstore(lat,lon,address,imgid,true,cityy,"00:00 AM","00:00 AM"
                    ,"Egypt Pounds",deletecharge,"5",description,null,storenam,"00:00 AM","00:00 AM"
                    ,"123456","123456","web",valuecategory);

            registerstore.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful())
                    {
                        try {
                            String storeresponse= response.body().string();
                            JSONObject storecond=new JSONObject(storeresponse);
                            String operstore=storecond.getString("operation");
                            if (operstore.trim().equals("success"))
                            {
                                registerstorecon=true;
                                String storeid=storecond.getString("store_id");
                                String storecode=storecond.getString("code");

                                if (storeid!=null)
                                {

                                    //  Toast.makeText(Storeinfo.this,storeid,Toast.LENGTH_LONG).show();
                                    SharedPreferences preferences=getSharedPreferences("store", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor=preferences.edit();
                                    editor.putString("id",storeid);
                                    editor.putString("code",storecode);
                                    editor.apply();
                                    registerretailer(fulnum);
                                }
                            }
                            else{
                                register_store.setClickable(true);
                                register_store.setTextColor(Color.BLACK);

                                Toast.makeText(Storeinfo.this,getResources().getString(R.string.storenameusedbefore),Toast.LENGTH_SHORT).show();
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        register_store.setClickable(true);
                        register_store.setTextColor(Color.BLACK);

                    }


                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                    Toast.makeText(Storeinfo.this,getResources().getString(R.string.connectionfailed),Toast.LENGTH_SHORT).show();

                    register_store.setClickable(true);
                    register_store.setTextColor(Color.BLACK);

                }
            });
        }
        else{
            registerretailer(fulnum);
        }

    }
    private void uploadimg(MultipartBody.Part img ,RequestBody parmeter)
    {

        if (!uploadimgcon)
        {
            Retrofitclient myretro=Retrofitclient.getInstance();
            Retrofit retrofittok=  myretro.getretro();
            final Endpoints myendpoints = retrofittok.create(Endpoints.class);


            uploadcall=myendpoints.uploadimg(img,parmeter);
            uploadcall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    if (response.isSuccessful())
                    {
                        upload_progress_bar.setVisibility(View.GONE);

                        //  Toast.makeText(Storeinfo.this,mresonse,Toast.LENGTH_LONG).show();

                        try {
                            String mresonse=response.body().string();
                            JSONObject uploadcond=new JSONObject(mresonse);
                            String operation=uploadcond.getString("operation");
                            if (operation.trim().equals("success"))
                            {
                                //  Toast.makeText(Storeinfo.this,"uploaded",Toast.LENGTH_LONG).show();

                                uploadimgcon=true;

                                String imagid=uploadcond.getString("id");
                                if (imagid!=null)
                                {
                                    Log.e("idbimage",imagid);
                                    SharedPreferences preferences=getSharedPreferences("image", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor=preferences.edit();
                                    editor.putString("id",imagid);
                                    editor.apply();

                                    intilazeviewswithvaildateandregister();
                                }


                            }
                            else{
                                Toast.makeText(Storeinfo.this,"uploaded problem",Toast.LENGTH_LONG).show();
                                intilazeviewswithvaildateandregister();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    }
                    else
                    {
                        upload_progress_bar.setVisibility(View.GONE);
                        Toast.makeText(Storeinfo.this,"not uploaded",Toast.LENGTH_LONG).show();

                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    upload_progress_bar.setVisibility(View.GONE);
                    Toast.makeText(Storeinfo.this,t.getMessage(),Toast.LENGTH_LONG).show();


                }
            });

        }
        else{
            intilazeviewswithvaildateandregister();
        }

    }


    private File convert_bitmap_to_file(Bitmap mybitmap)
    {
        File f = new File(this.getCacheDir(), "file");
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

//Convert bitmap to byte array
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        mybitmap.compress(Bitmap.CompressFormat.JPEG, 20 /*ignored for PNG*/, bos);
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


    private void chooseimg()
    {
        LinearLayout camera,gallery;

        builder = new android.app.AlertDialog.Builder(Storeinfo.this);

        View myview = LayoutInflater.from(Storeinfo.this.getApplicationContext()).inflate(R.layout.chooselayoutfortakepic, null);
        camera=myview.findViewById(R.id.takcam);
        gallery=myview.findViewById(R.id.takgalee);
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

    private void opencamera()
    {
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePicture, 1);//zero can be replaced with any action code

    }




    private void vaildstore()
    {
        String namestore =Store_Name.getText().toString().trim();
        /*
        if (imgcond)
        {
            imgadd.setError(getResources().getString(R.string.imgV));
            imgadd.requestFocus();
            return;
        }
        */

        openplacepicker();
    }
    private void intilazeviewswithvaildateandregister()
    {


        String namestore,addressstore,adminstore,phonestore,passstore,confipassstore,descstore,deletstore;



        namestore =Store_Name.getText().toString().trim();
        addressstore=Store_address.getText().toString().trim();
        adminstore=Store_Admin.getText().toString().trim();
        phonestore=Store_phone.getText().toString().trim();
        passstore=Store_pass.getText().toString().trim();
        confipassstore=Store_passconfirm.getText().toString().trim();


/*
        if (imgcond)
        {
            imgadd.setError(getResources().getString(R.string.imgV));
            imgadd.requestFocus();

            register_store.setClickable(true);
            register_store.setTextColor(Color.BLACK);
            return;
        }
        */
        if (addressstore.isEmpty())
        {
            Store_address.setError(getResources().getString(R.string.emailV));
            Store_address.requestFocus();
            register_store.setClickable(true);
            register_store.setTextColor(Color.BLACK);
            return ;
        }
        if (phonestore.isEmpty()||phonestore.length() < 10)
        {
            Store_phone.setError(getResources().getString(R.string.phoneV));
            Store_phone.requestFocus();
            register_store.setClickable(true);
            register_store.setTextColor(Color.BLACK);
            return;
        }



        if (namestore.isEmpty())
        {
            Store_Name.setError(getResources().getString(R.string.enterstorenum));
            Store_Name.requestFocus();
            register_store.setClickable(true);
            register_store.setTextColor(Color.BLACK);
            return;
        }




        if (adminstore.isEmpty())
        {
            Store_Admin.setError(getResources().getString(R.string.passV));
            Store_Admin.requestFocus();
            register_store.setClickable(true);
            register_store.setTextColor(Color.BLACK);
            return;
        }


        if (passstore.length()<6)
        {
            Store_pass.setError(getResources().getString(R.string.minumV));
            Store_pass.requestFocus();
            register_store.setClickable(true);
            register_store.setTextColor(Color.BLACK);
            return;
        }
        if (confipassstore.isEmpty())
        {
            Store_passconfirm.setError(getResources().getString(R.string.passRV));
            Store_passconfirm.requestFocus();
            register_store.setClickable(true);
            register_store.setTextColor(Color.BLACK);
            return;
        }


        if (!confipassstore.equals(passstore))
        {
            Store_passconfirm.setError(getResources().getString(R.string.passRV));
            Store_passconfirm.requestFocus();
            register_store.setClickable(true);
            register_store.setTextColor(Color.BLACK);
            return;
        }
        String code=ccp.getSelectedCountryCodeWithPlus();
        StringBuilder phone=new StringBuilder(Store_phone.getText().toString());
        Character charSequence=phone.charAt(0);
        if(charSequence=='0')
        {
            phone.deleteCharAt(0);
        }
        String phoneresult=phone.toString();
        final String full_number=code+phoneresult;
        SharedPreferences sharedPreferencess=getSharedPreferences("retailerphone",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferencess.edit();
        editor.putString("number",full_number);
        editor.apply();


        SharedPreferences preferences=getSharedPreferences("image",Context.MODE_PRIVATE);
        SharedPreferences sharedPreferences=getSharedPreferences("storeinfor",Context.MODE_PRIVATE);

        String idimg= preferences.getString("id","");
        String lang=sharedPreferences.getString("lag","");
        String lati=sharedPreferences.getString("lat","");
        String city=sharedPreferences.getString("lat","");

        if (idimg.trim().equals(""))
        {
            registerstore("",Store_address.getText().toString(),Store_Name.getText().toString(),String.valueOf(lang),String.valueOf(lati),"1",city,"description",valuecategory, full_number);

        }
        else {
            registerstore(idimg,Store_address.getText().toString(),Store_Name.getText().toString(),String.valueOf(lang),String.valueOf(lati),"1",city,"description",valuecategory, full_number);

        }








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
                    SharedPreferences preferences=getSharedPreferences("image", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor1=preferences.edit();
                    editor1.clear();
                    editor1.apply();


                    String thetoken= response.body().getData().getToken();
                    SharedPreferences prefs = getSharedPreferences("token", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor=prefs.edit();
                    editor.putString("usertoken",thetoken);
                    editor.apply();
                    Toast.makeText(Storeinfo.this,getResources().getString(R.string.succesfullogin),Toast.LENGTH_LONG).show();
                    startActivity(new Intent(Storeinfo.this, MainActivity.class));
                    finishAffinity();



                }
                else
                {
                    Toast.makeText(Storeinfo.this,"Wrong email or password",Toast.LENGTH_LONG).show();



                }
            }

            @Override
            public void onFailure(Call<Roottoken> call, Throwable t) {

                Toast.makeText(Storeinfo.this,"Connection Failed",Toast.LENGTH_LONG).show();


            }
        });


    }


    public void registerretailer(final String fullnumber)
    {

        Retrofitclient myretro=Retrofitclient.getInstance();
        Retrofit retrofittok=  myretro.getretro();
        final Endpoints myendpoints = retrofittok.create(Endpoints.class);
        SharedPreferences preferences=getSharedPreferences("store", Context.MODE_PRIVATE);
        String storeid=preferences.getString("id",null);
        registerretailer=myendpoints.registerretailer(storeid,Store_Admin.getText().toString(),fullnumber,Store_pass.getText().toString(),fullnumber,"retailerAdmin",valuecategory);
        registerretailer.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                registerpro.setVisibility(View.GONE);
                if (response.isSuccessful())
                {
                    try {
                        String retailerresponse=response.body().string();
                        JSONObject retailerecond=new JSONObject(retailerresponse);
                        String operation=retailerecond.getString("operation");
                        if (operation.trim().equals("success"))
                        {

                        //    Toast.makeText(Storeinfo.this,"Successful Register",Toast.LENGTH_LONG).show();

                            registerretailercon=true;
                            signin(fullnumber,Store_pass.getText().toString());



                        }
                        else{
                            String reason=retailerecond.getString("reason");
                            register_store.setClickable(true);
                            register_store.setTextColor(Color.BLACK);
                            Toast.makeText(Storeinfo.this,getResources().getString(R.string.dublicatenum),Toast.LENGTH_LONG).show();

                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    register_store.setClickable(true);
                    register_store.setTextColor(Color.BLACK);
                    Toast.makeText(Storeinfo.this,response.errorBody().toString(),Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                register_store.setClickable(true);
                register_store.setTextColor(Color.BLACK);
                Toast.makeText(Storeinfo.this,getResources().getString(R.string.connectionfailed),Toast.LENGTH_SHORT).show();


            }
        });




    }











}
