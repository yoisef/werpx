package werpx.cashiery.Cashiery;

import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.SSLContext;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import werpx.cashiery.DbBitmapUtility;
import werpx.cashiery.Downloadimageservice;
import werpx.cashiery.Endpoints;

import werpx.cashiery.GlideApp;
import werpx.cashiery.NetworkBroadcastreciever;
import werpx.cashiery.R;
import werpx.cashiery.Retrofitclient;
import werpx.cashiery.RoomDatabase.Sqlitetable;
import werpx.cashiery.RoomDatabase.historytable;
import werpx.cashiery.RoomLivedata.Cashieryviewmodel;
import werpx.cashiery.RoomLivedata.producttable;
import werpx.cashiery.Utils;

import werpx.cashiery.historydata;

import werpx.cashiery.modelmarketcategories.Catgwithsubroot;
import werpx.cashiery.modelmarketcategories.Subcategory;
import werpx.cashiery.productmodels.Product;
import werpx.cashiery.storemodelbyid.Store;
import werpx.cashiery.storemodelbyid.Storeroot;

public class MainActivity extends AppCompatActivity {


    private RecyclerView myrecycle;
    private ImageView scan, barcodimg;
    private Toolbar mytoolbar;
    private Button enterbarcode;
    private android.app.AlertDialog.Builder builder, builder1;
    private android.app.AlertDialog alertDialog, alertDialog1;
    private Call<werpx.cashiery.productmodels.Rootproductdetail> mcall;
    private Call<werpx.cashiery.salemodel.Saleroot> salecall;
    private Call<werpx.cashiery.usermodels.Userroot> usercall;
    private Call<Storeroot> storecall;
    private Call<werpx.cashiery.productmodels.getallproductsroot> callproducts;
    private werpx.cashiery.Cashiery.Recycleadapter mAdapter;
    private TextView pricetotal;
    private TextView paylinear;
    private werpx.cashiery.RoomDatabase.productViewmodel mWordViewModel;
    private SharedPreferences prefs,tablepref,storepref;
    private SharedPreferences.Editor myeditor,tableeditor;
    private String usertoken;
    private ProgressBar payprpgressbar;
    private List<werpx.cashiery.RoomDatabase.mytable> myproducts;
    private werpx.cashiery.productdatabase mydatabase;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private TextView headerstorenam,headerretailernam ,headeraccountdata;
    private ImageView headerstoreimg;
    private NavigationView mynavigation;
    private Boolean checkactiviy;
    private Call<ResponseBody> callsall;
    private String retailerid;
    private Boolean addornot;
    private Boolean addproducttablefirsttime;
    private Configuration configuration;
    private int applanguage;
    private DbBitmapUtility dbBitmapUtility;
    private historydata mydata;

    private BroadcastReceiver receiver;
    private IntentFilter filter;
    private ProgressDialog downloadproductprogress;
    String img="";
    String imgurl="" ;
    Utils utils;
    Cashieryviewmodel cashieryviewmodel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseApp.initializeApp(this);

        FirebaseMessaging.getInstance().subscribeToTopic("cashieryglobal");

        cashieryviewmodel= ViewModelProviders.of(this).get(Cashieryviewmodel.class);



        utils=new Utils(this);

        receiver=new NetworkBroadcastreciever();
        filter=new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);

        registerNetworkBroadcastForNougat();

      //  startService(new Intent(MainActivity.this, Downloadimageservice.class));
        dbBitmapUtility=new DbBitmapUtility();
         configuration = getResources().getConfiguration();
        applanguage= configuration.getLayoutDirection();
        sslconnection();
        addproducttablefirsttime = true;
        retailerid = getSharedPreferences("retailerdata", Context.MODE_PRIVATE).getString("id", "n");
        addornot = true;

        setContentView(R.layout.activity_main);
    // this.startService(new Intent(MainActivity.this, SyncService.class));
    //  this.startService(new Intent(MainActivity.this, downloadimagesyrvice.class));

        checkactiviy = false;
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mytoolbar = findViewById(R.id.toolbar);
        mynavigation = findViewById(R.id.nav_view);
        payprpgressbar=findViewById(R.id.payprogress);
        View header = mynavigation.getHeaderView(0);
        headerstorenam = header.findViewById(R.id.storenamnav);
        headerretailernam = header.findViewById(R.id.retailernamnav);
        headeraccountdata = header.findViewById(R.id.accountdatanav);
        headerstoreimg = header.findViewById(R.id.storeimgnav);

        headeraccountdata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, store_details.class));
            }
        });

        setSupportActionBar(mytoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menumain);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        
        mydatabase = new werpx.cashiery.productdatabase(this);
        //  mydatabase.getallproducts(this);

        tablepref = getSharedPreferences("tablep", Context.MODE_PRIVATE);
        tableeditor = tablepref.edit();
        myproducts = new ArrayList<>();
        prefs = getSharedPreferences("token", Context.MODE_PRIVATE);
        usertoken = prefs.getString("usertoken", "def");



        if (haveNetworkConnection()) {
         //   getallproducts();
            List<Sqlitetable> mylist=mydatabase.getproductsitems();
            if (mylist.size()!=0)
            {
                for (int h=0;h<mylist.size();h++)
                {
                    String imgurl=mylist.get(h).getImge();

                }

            }
            getretailerid();

        }
        else{

            String nameadmin=getSharedPreferences("retailerdata",Context.MODE_PRIVATE).getString("nam","");
            String nametore = getSharedPreferences("storedetails", Context.MODE_PRIVATE).getString("nam","");
            String img = getSharedPreferences("storedetails", Context.MODE_PRIVATE).getString("img","");


            if (!img.equals(""))
            {
                GlideApp.with(MainActivity.this)
                        .load(img)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(headerstoreimg);

                headerstorenam.setText(nametore);


                headerretailernam.setText(nameadmin);
            }
            else{
                GlideApp.with(MainActivity.this)
                        .load(getResources().getDrawable(R.drawable.ic_shop))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(headerstoreimg);

                headerstorenam.setText(nametore);


                headerretailernam.setText(nameadmin);
            }



        }


        pricetotal = findViewById(R.id.totalpricec);
        barcodimg = findViewById(R.id.aboutus);
        paylinear = findViewById(R.id.paylayout);
        myrecycle = findViewById(R.id.productrecycle);
        mAdapter = new werpx.cashiery.Cashiery.Recycleadapter(this);

        setUpRecyclerView();
        mynavigation.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight

                        switch (menuItem.getItemId()) {
                            case R.id.signout: {

                                removesales_signout();

                                break;
                            }

                            case R.id.sales: {
                                mDrawerLayout.closeDrawers();

                                startActivity(new Intent(MainActivity.this, werpx.cashiery.Cashiery.sales_history.class));

                                break;
                            }

                            case R.id.readbar: {
                                mDrawerLayout.closeDrawers();

                                startActivity(new Intent(MainActivity.this, werpx.cashiery.Cashiery.Camera_activity.class));

                                break;
                            }


                            case R.id.about: {

                                startActivity(new Intent(MainActivity.this, Aboutus.class));

                                break;
                            }

                            case R.id.Salesscree: {
                                mDrawerLayout.closeDrawers();

                                startActivity(new Intent(MainActivity.this, Sales_Screen.class));
                                break;

                            }
                            case R.id.setting: {

                                mDrawerLayout.closeDrawers();
                                startActivity(new Intent(MainActivity.this, Setting.class));
                                break;

                            }
                            case R.id.pointscredit: {
                                mDrawerLayout.closeDrawers();
                                startActivity(new Intent(MainActivity.this, points.class));
                                break;
                            }


                            default:
                                menuItem.setChecked(true);
                                // close drawer when item is tapped
                                mDrawerLayout.closeDrawers();
                                break;
                        }
                        return true;


                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here


                    }
                });


        mWordViewModel = ViewModelProviders.of(this).get(werpx.cashiery.RoomDatabase.productViewmodel.class);
        mWordViewModel.getAllWords().observe(this, new Observer<List<werpx.cashiery.RoomDatabase.mytable>>() {
            @Override
            public void onChanged(@Nullable final List<werpx.cashiery.RoomDatabase.mytable> words) {

                mAdapter.setWords(words);
                myproducts = words;
                Double allcoast = 0.0;

                // Update the cached copy of the words in the adapter.

                for (int i = 0; i < words.size(); i++) {

                    Double unitprice = Double.parseDouble(words.get(i).getPprice());
                    int quantity = words.get(i).getPitemn();
                    Double productcoast = unitprice * quantity;
                    allcoast = allcoast + productcoast;

                }
                if (utils.checkapplanguage()==0)
                {
                    pricetotal.setText(new DecimalFormat("##.##").format(allcoast));

                }
                else{
                    Typeface typeface = ResourcesCompat.getFont(MainActivity.this, R.font.arabicnum);
                    pricetotal.setTypeface(typeface);

                    pricetotal.setText(new DecimalFormat("##.##").format(allcoast));


                }



            }


        });


        mWordViewModel.getAllhistory().observe(this, new Observer<List<historytable>>() {
            @Override
            public void onChanged(@Nullable List<historytable> historytables) {

                Gson gson = new Gson();
                SharedPreferences preferences = getSharedPreferences("history", Context.MODE_PRIVATE);

                SharedPreferences.Editor editor = preferences.edit();
                String json = gson.toJson(historytables);
                editor.putString("sales", json);
                editor.apply();

            }

        });


        //  getSupportActionBar().setCustomView(R.layout.cutom_action_bar);


        getallproducts();

        barcodimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this, werpx.cashiery.Cashiery.Camera_activity.class));
            }
        });


        paylinear.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (myproducts.size()==0)
                {
                    Toast.makeText(MainActivity.this,getResources().getString(R.string.listempty),Toast.LENGTH_LONG).show();
                }
                else{
                    payprpgressbar.setVisibility(View.VISIBLE);
                    paylinear.setText("");

                    ArrayList<String> barcodes,quantities,retailerids,productsids;
                    ArrayList<Boolean> lives=new ArrayList<>();
                    barcodes=new ArrayList<>();
                    quantities=new ArrayList<>();
                    retailerids=new ArrayList<>();
                    productsids=new ArrayList<>();
                    lives=new ArrayList<>();
                    int totalorderitems = 0;
                    Double totalordercoast= 0.0;
                    int i;
                    for (i = 0; i < myproducts.size(); i++) {

                        String currentproduct = myproducts.get(i).getPbar();
                        String name=myproducts.get(i).getPname();
                        String productid=myproducts.get(i).getPid();
                        String localnam=myproducts.get(i).getPlocalname();
                        Double currentcoast = Double.parseDouble(myproducts.get(i).getPprice());
                        int items = myproducts.get(i).getPitemn();
                        totalorderitems = totalorderitems + items;
                        Double totalproduct=currentcoast*items;
                        totalordercoast = totalordercoast + totalproduct;
                        barcodes.add(currentproduct);
                        quantities.add(String.valueOf(items));
                        lives.add(true);
                        String id=getSharedPreferences("retailerdata",Context.MODE_PRIVATE).getString("id","");
                        retailerids.add(id);
                        if (productid==null)
                        {
                            productsids.add(null);
                            mydatabase.insertofflinefinaleproducts(name,currentproduct,String.valueOf(currentcoast),null,"detail",localnam);


                        }
                        else{
                            productsids.add(productid);
                        }

                    }
                    if (myproducts.size() == 0) {

                    } else {
                        SharedPreferences sharedPreferences=getSharedPreferences("pointsync",Context.MODE_PRIVATE);
                        String currentpoint=sharedPreferences.getString("point","0");

                        if (currentpoint.equals("0"))
                        {
                            SharedPreferences.Editor editor=sharedPreferences.edit();
                            editor.putString("point",String.valueOf(totalordercoast));
                            editor.apply();
                        }
                        else{
                            Double totalpoint=0.0;
                            Double currentpointconvert=Double.parseDouble(currentpoint);
                            totalpoint=totalpoint+currentpointconvert;
                            SharedPreferences.Editor editor=sharedPreferences.edit();
                            editor.putString("point",String.valueOf(totalpoint));
                            editor.apply();
                        }

                        saleoperation(totalordercoast,totalorderitems,barcodes,quantities,retailerids,productsids,lives);

                    }

                    Locale locale = new Locale("ar", "KW");
                    SimpleDateFormat sdf = new SimpleDateFormat("E, dd-MMMM-yy");
                    Date currDate = new Date();
                    String formattedDate = sdf.format(currDate);
                   werpx.cashiery.RoomDatabase.historytable myhis = new werpx.cashiery.RoomDatabase.historytable(1,formattedDate,myproducts, String.valueOf(totalordercoast), String.valueOf(totalorderitems));
                    mWordViewModel.inserthis(myhis);

                    if (!haveNetworkConnection())
                    {
                        payprpgressbar.setVisibility(View.GONE);
                        paylinear.setText("Pay");
                        Boolean check= insertofflinesales("1",formattedDate,String.valueOf(totalordercoast),String.valueOf(totalorderitems),quantities,barcodes,retailerids,productsids,lives);
                        if (check)
                        {
                            Toast.makeText(MainActivity.this,getResources().getString(R.string.insertofflinesales),Toast.LENGTH_LONG).show();
                            mWordViewModel.deleteallproduct();

                        }

                    }

                }

            }


        });


    }



    private void registerNetworkBroadcastForNougat() {
        receiver=new NetworkBroadcastreciever();
        filter=new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        this.registerReceiver(receiver,filter);

    }

    @Override
    public void onBackPressed() {
        if (this.mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

       unregisterNetworkChanges();

        checkactiviy=true;


    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);

    }

    private Boolean insertofflinesales(String ornum, String ordata , String oramount,String units, ArrayList<String> quantites, ArrayList<String> orbarcodes,
                                       ArrayList<String> orretailerids, ArrayList<String> orproductids,  ArrayList<Boolean> orlives  )
    {
        Gson gson=new Gson();
        String barcodes= gson.toJson(orbarcodes);
        String retailerids=gson.toJson(orretailerids);
        String produvtids=gson.toJson(orproductids);
        String lives=gson.toJson(orlives);
        String quant=gson.toJson(quantites);
        return mydatabase.insertofflinesales(ornum,ordata,oramount,units,barcodes,retailerids,produvtids,lives,quant);
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


    private void getretailerid()
    {
        werpx.cashiery.Retrofitclient myretro= werpx.cashiery.Retrofitclient.getInstance();
        Retrofit retrofittok=  myretro.getretro();
        final werpx.cashiery.Endpoints myendpoints = retrofittok.create(werpx.cashiery.Endpoints.class);
        usercall=myendpoints.getuserdata("Bearer "+usertoken);
        usercall.enqueue(new Callback<werpx.cashiery.usermodels.Userroot>() {
            @Override
            public void onResponse(Call<werpx.cashiery.usermodels.Userroot> call, Response<werpx.cashiery.usermodels.Userroot> response) {

                if (response.isSuccessful())
                {
                    if (response.body()!=null)
                    {
                        headerretailernam.setText(response.body().getAuth().getName());
                        String retailerid=response.body().getAuth().getId();
                        String storeid=response.body().getAuth().getStoreId();
                        String namretailer=response.body().getAuth().getName();
                        String mobile=response.body().getAuth().getMobile();
                        SharedPreferences preferences=getSharedPreferences("retailerdata",Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor=preferences.edit();
                        editor.putString("id",retailerid);
                        editor.putString("idstore",storeid);
                        editor.putString("nam",namretailer);
                        editor.putString("mob",mobile);
                        editor.apply();
                        getstoredetails(storeid);

                    }

                }
                else
                {
                    Toast.makeText(MainActivity.this,"problem with authentication",Toast.LENGTH_LONG).show();
                }


            }

            @Override
            public void onFailure(Call<werpx.cashiery.usermodels.Userroot> call, Throwable t) {

                String nameadmin=getSharedPreferences("retailerdata",Context.MODE_PRIVATE).getString("nam","");


            }
        });
    }
    private void inserttohis(Double coast,int items)
    {
        Locale locale = new Locale("ar", "KW");
        SimpleDateFormat sdf = new SimpleDateFormat("E, dd-MMMM-yy");
        Date currDate = new Date();
        String formattedDate = sdf.format(currDate);
        werpx.cashiery.RoomDatabase.historytable myhis = new werpx.cashiery.RoomDatabase.historytable(1,formattedDate,myproducts, String.valueOf(coast), String.valueOf(items));
//        mWordViewModel.inserthis(myhis);
    }

    public void getallproducts() {

        if (cashieryviewmodel.getproducts().getValue()!=null) {


            downloadproductprogress=new ProgressDialog(this);
            downloadproductprogress.setMessage(getResources().getString(R.string.downloadproduct));
            downloadproductprogress.show();
            String usertoken = getSharedPreferences("token", Context.MODE_PRIVATE).getString("usertoken", "def");
            Call<Catgwithsubroot> callcategories;
            Retrofitclient myretro = Retrofitclient.getInstance();
            Retrofit retrofittok = myretro.getretro();
            final Endpoints myendpoints = retrofittok.create(Endpoints.class);
            callcategories = myendpoints.getmarketcategories("Bearer " + usertoken, "4BB24B08-6F74-4A30-9806-124293F2D262");
            callcategories.enqueue(new Callback<Catgwithsubroot>() {
                @Override
                public void onResponse(Call<Catgwithsubroot> call, Response<Catgwithsubroot> response) {

                    if (response.isSuccessful()) {

                        List<Subcategory> subcategories = response.body().getCategory().getSubcategories();
                        String json = utils.getGson().toJson(subcategories);
                        getSharedPreferences("marketinfo", Context.MODE_PRIVATE).edit().putString("subcat",json).apply();
                        // refrshcategories.setRefreshing(true);
                        new insertproductswithcat().execute(subcategories);


                    }
                }

                @Override
                public void onFailure(Call<Catgwithsubroot> call, Throwable t) {

                    Toast.makeText(MainActivity.this,getResources().getString(R.string.connectionfailed),Toast.LENGTH_SHORT).show();

                    downloadproductprogress.dismiss();


                }
            });
        }
        else {




        }





    }

    private void saleoperation(final Double totalordercoast, final int totalitems, ArrayList<String> barcode, ArrayList<String> quantity, ArrayList<String> retailerid, ArrayList<String> productid, ArrayList<Boolean> live)
    {
        werpx.cashiery.Retrofitclient myretro= werpx.cashiery.Retrofitclient.getInstance();
        Retrofit retrofit=  myretro.getretro();

        final werpx.cashiery.Endpoints myendpoints = retrofit.create(werpx.cashiery.Endpoints.class);
        callsall= myendpoints.salearray(barcode,quantity,retailerid,productid,live);
        callsall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if(response.isSuccessful())
                {



                    try {
                        String saleresponse = response.body().string();
                        JSONObject saleobject = new JSONObject(saleresponse);
                        String opercon = saleobject.getString("operation");
                        if (opercon.trim().equals("success")) {
                            mWordViewModel.deleteallproduct();


                            Toast.makeText(MainActivity.this, getResources().getString(R.string.succefulpayment), Toast.LENGTH_LONG).show();
                            payprpgressbar.setVisibility(View.GONE);
                            paylinear.setText("Pay");
                        } else {
                            Toast.makeText(MainActivity.this, getResources().getString(R.string.failedpayment), Toast.LENGTH_LONG).show();
                            payprpgressbar.setVisibility(View.GONE);
                            paylinear.setText("Pay");
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
//
                }else{
                    Toast.makeText( MainActivity.this,getResources().getString(R.string.failedpayment),Toast.LENGTH_LONG).show();
                    payprpgressbar.setVisibility(View.GONE);
                    paylinear.setText("Pay");
                }


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                payprpgressbar.setVisibility(View.GONE);
                paylinear.setText("Pay");
                Toast.makeText( MainActivity.this,getResources().getString(R.string.connectionfailed),Toast.LENGTH_LONG).show();


            }
        });
    }

    class insertproductswithcat extends AsyncTask<List<Subcategory>, Void, List<Subcategory>> {

        @Override
        protected List<Subcategory> doInBackground(List<Subcategory>... lists) {

            utils.getMydatabase().deleteproductitems();

            SharedPreferences sharedPreferences = getSharedPreferences("catprod", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            List<Subcategory> subcategories = lists[0];
            for (int i = 0; i < subcategories.size(); i++) {
                List<werpx.cashiery.modelmarketcategories.Product> products = subcategories.get(i).getProducts();
                for (int j = 0; j < products.size(); j++) {

                    String bar = products.get(j).getBarcode();
                    String localnam = products.get(j).getLocalname();
                    String name = products.get(j).getName();
                    String price = products.get(j).getPrice();
                    String productid = products.get(j).getId();
                    if (products.get(j).getImage() == null) {
                      // utils.getMydatabase().insertdatalistproducts(name, bar, price, null, "details", productid, localnam, null,i);
                      //  utils.getlivedatabase().userDao().insertProductforlist(new producttable(name,localnam,price,productid));
                       cashieryviewmodel.insertproductitem(new producttable(name,localnam,productid,0,price,bar,"details",null,i));
                    } else {
                        String imgfull = getResources().getString(R.string.baseurlimgproduct) + products.get(j).getImage().getFilename();

                      //  utils.getMydatabase().insertdatalistproducts(name, bar, price, imgfull, "details", productid, localnam, null,i);
                        cashieryviewmodel.insertproductitem(new producttable(name,localnam,productid,0,price,bar,"details",imgfull,i));

                    }



                }
            }
            return subcategories;
        }

        @Override
        protected void onPostExecute(List<Subcategory> aVoid) {
            super.onPostExecute(aVoid);

            downloadproductprogress.dismiss();




/*
            adaptersubcat.setproducts(aVoid);
            allcategoriesrecyclee.setAdapter(adaptersubcat);
            if (utils.isTablet(MainActivity.this))
            {
                List<Sqlitetable> allproducts=utils.getMydatabase().getproductsitems();
                categorymaintablet.setproducts(allproducts);
                allproductrecycle.setAdapter(categorymaintablet);

            }
            else {
                List<Sqlitetable> allproducts=utils.getMydatabase().getproductsitems();
                adaptercate.setproducts(allproducts);
                allproductrecycle.setAdapter(adaptercate);

            }
            refrshcategories.setRefreshing(false);


        */
        }
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

                        if (mstore.getImage()!=null)
                        {
                            img = mstore.getImage().getFilename();

                             imgurl =getResources().getString(R.string.baseurlimgstore)+ img;
                            if (!checkactiviy)
                            {
                                if (!img.equals("")) {
                                    GlideApp.with(MainActivity.this)
                                            .load(imgurl)
                                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                                            .into(headerstoreimg);
                                }
                                if (!nam.equals("")) {
                                    headerstorenam.setText(nam);

                                }
                            }

                        }
                        else{
                            if (!checkactiviy)
                            {
                                if (!img.equals("")) {
                                    GlideApp.with(MainActivity.this)
                                            .load(getResources().getDrawable(R.drawable.ic_shop))
                                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                                            .into(headerstoreimg);
                                }
                                if (!nam.equals("")) {
                                    headerstorenam.setText(nam);

                                }
                            }
                        }

                        String storecode=mstore.getCode();
                        String storeaddress=mstore.getAddress();
                        String storeType=mstore.getCategoryId();




                        SharedPreferences preferences = getSharedPreferences("storedetails", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("nam", nam);
                        editor.putString("img", imgurl);
                        editor.putString("codestore",storecode);
                        editor.putString("storeaddress",storeaddress);
                        editor.putString("storetype",storeType);

                        //fe 7aga 8lt hna


                        if (storeType.trim().equals("4BB24B08-6F74-4A30-9806-124293F2D262"))
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
                    Toast.makeText(MainActivity.this,response.message(),Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Storeroot> call, Throwable t) {


            }
        });





    }


    class insertproducts extends AsyncTask<List<Product>,Void,List<Sqlitetable> > {



        @Override
        protected List<Sqlitetable> doInBackground(List<Product>... lists) {

            List<Product> myproducts=lists[0];
            for (int i = 0; i < myproducts.size(); i++) {
                if (myproducts.get(i).getImage() == null) {
                    String barcode = myproducts.get(i).getBarcode();
                    String  price = myproducts.get(i).getPrice();
                    String namepr = myproducts.get(i).getName();
                    String localnam = myproducts.get(i).getLocalname();
                    String desc = myproducts.get(i).getDescription();
                    String id = myproducts.get(i).getId();
                    mydatabase.insertdatalistproducts(namepr, barcode, price, null, desc, id, localnam, null,0);
                } else {
                    String barcode = myproducts.get(i).getBarcode();
                    String price = myproducts.get(i).getPrice();
                    String imgfilename = myproducts.get(i).getImage().getFilename();
                    String imgurl = getResources().getString(R.string.baseurlimgproduct) + imgfilename;


                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    String name = myproducts.get(i).getName();
                    String namepr = myproducts.get(i).getName();
                    String localnam = myproducts.get(i).getLocalname();

                    String desc = myproducts.get(i).getDescription();
                    String id = myproducts.get(i).getId();
                    mydatabase.insertdatalistproducts(namepr, barcode, price, imgurl, desc, id, localnam, null,0);

                }

            }

            return mydatabase.getproductsitems();
        }

        @Override
        protected void onPostExecute(List<Sqlitetable> aVoid) {


            startService(new Intent(MainActivity.this, Downloadimageservice.class));
            downloadproductprogress.dismiss();

            super.onPostExecute(aVoid);
        }
    }




    private void setUpRecyclerView() {
        myrecycle.setAdapter(mAdapter);
        myrecycle.setLayoutManager(new LinearLayoutManager(this));
        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new SwipeToDeleteCallback(mAdapter,this));
        itemTouchHelper.attachToRecyclerView(myrecycle);
    }
    protected void unregisterNetworkChanges() {
        try {
            unregisterReceiver(receiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public void addnewproduct()
    {
        final EditText addnam,addpric;
        TextView add, cancel;
        ImageView incre,decre;


        builder = new android.app.AlertDialog.Builder(MainActivity.this);

        View myview = LayoutInflater.from(MainActivity.this.getApplicationContext()).inflate(R.layout.addnewproduct, null);
        addpric=myview.findViewById(R.id.addprice);
        add=myview.findViewById(R.id.addproductt);
        cancel=myview.findViewById(R.id.cancelproduct);

        builder.setView(myview);
        alertDialog = builder.create();
        alertDialog.show();



        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.cancel();
            }

        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();

            }
        });

    }

    public void removesales_signout()
    {

        TextView confirmremove,cancelremove;
        builder = new android.app.AlertDialog.Builder(MainActivity.this);

        View myview = LayoutInflater.from(MainActivity.this.getApplicationContext()).inflate(R.layout.signoutdialog, null);
        confirmremove=myview.findViewById(R.id.confirmre);
        cancelremove=myview.findViewById(R.id.cancelre);
        builder.setView(myview);
        alertDialog = builder.create();
        alertDialog.show();

        confirmremove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDrawerLayout.closeDrawers();
                SharedPreferences prefs = getSharedPreferences("token", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.clear().apply();
                SharedPreferences preferences = getSharedPreferences("retailerdata", Context.MODE_PRIVATE);
                SharedPreferences.Editor editorr = preferences.edit();
                editorr.clear().apply();

                mWordViewModel.deleteallhist();


                if (preferences.getString("idstore", "def").equals("def")) {
                    startActivity(new Intent(MainActivity.this, werpx.cashiery.Cashiery.loginactivity.class));
                    finish();
                }
                alertDialog.cancel();

            }
        });

        cancelremove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();

            }
        });

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



    public void setmarketcategoriesrefresh()
    {
        // refrshcategories.setRefreshing(true);

        Call<Catgwithsubroot> callcategories;
        Retrofitclient myretro = Retrofitclient.getInstance();
        Retrofit retrofittok = myretro.getretro();
        final Endpoints myendpoints = retrofittok.create(Endpoints.class);
        callcategories = myendpoints.getmarketcategories("Bearer " + usertoken, "4BB24B08-6F74-4A30-9806-124293F2D262");
        callcategories.enqueue(new Callback<Catgwithsubroot>() {
            @Override
            public void onResponse(Call<Catgwithsubroot> call, Response<Catgwithsubroot> response) {

                if (response.isSuccessful()) {

                    List<werpx.cashiery.modelmarketcategories.Subcategory> subcategories = response.body().getCategory().getSubcategories();
                     String json = utils.getGson().toJson(subcategories);
                    getSharedPreferences("marketinfo", Context.MODE_PRIVATE).edit().putString("subcat",json).apply();
                    //  refrshcategories.setRefreshing(true);
                    new insertproductswithcat().execute(subcategories);


                }
            }

            @Override
            public void onFailure(Call<Catgwithsubroot> call, Throwable t) {

                String jsonsub = getSharedPreferences("marketinfo", Context.MODE_PRIVATE).getString("subcat", "");
                if (!jsonsub.equals("")) {

                    Type type = new TypeToken<List<Subcategory>>() {
                    }.getType();
                    List<Subcategory> subcategories = utils.getGson().fromJson(jsonsub, type);

                  //  adaptersubcat.setproducts(subcategories);
                  //  allcategoriesrecyclee.setAdapter(adaptersubcat);
                  //  refrshcategories.setRefreshing(false);


                } else {

                 //   refrshcategories.setRefreshing(false);
                  //  progresscategories.setVisibility(View.GONE);
                }

            }
        });
    }






}





























