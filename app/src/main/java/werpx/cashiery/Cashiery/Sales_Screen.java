package werpx.cashiery.Cashiery;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import werpx.cashiery.DbBitmapUtility;
import werpx.cashiery.Downloadimageservice;
import werpx.cashiery.Endpoints;
import werpx.cashiery.R;
import werpx.cashiery.Retrofitclient;
import werpx.cashiery.RoomDatabase.Sqlitetable;
import werpx.cashiery.RoomLivedata.Cashieryviewmodel;
import werpx.cashiery.RoomLivedata.producttable;
import werpx.cashiery.Utils;

import werpx.cashiery.modelmarketcategories.Catgwithsubroot;
import werpx.cashiery.modelmarketcategories.Subcategory;
import werpx.cashiery.productdatabase;
import werpx.cashiery.productmodels.Product;
import werpx.cashiery.productmodels.getallproductsroot;
import werpx.cashiery.spinneradapter;
import werpx.cashiery.subcategorymodel.Subcatroot;

public class Sales_Screen extends AppCompatActivity {

    public RecyclerView show_product_recycle, addedproducts;
    private showproduct_adapter myadapter;
    public Recycleadapter adapteradded;
    private werpx.cashiery.RoomDatabase.productViewmodel mWordViewModel;
    private Spinner categoryspinner;
    private TextView totalprice, paytext;
    private List<werpx.cashiery.RoomDatabase.mytable> myproducts;
    private Call<werpx.cashiery.salemodel.Saleroot> salecall;
    private EditText typebarcode;
    private Call<werpx.cashiery.productmodels.Rootproductdetail> mcall;
    private SharedPreferences prefs;
    private String usertoken;
    private productdatabase mydatabase;
    private android.app.AlertDialog.Builder builder, builder1;
    private android.app.AlertDialog alertDialog;
    private String retailerid;
    private Call<ResponseBody> callsell;
    private List<producttable> products;
    private Call<werpx.cashiery.productmodels.getallproductsroot> callproducts;
    private List<Sqlitetable> myprodscreen;
    private String LocalNameValue, Categoryid, producerid, unitid, productname, barcodefinal;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Timer timer = new Timer();
    private final long DELAY = 2000; //
    private Configuration configuration;
    private int applanguage;
    private Call<getallproductsroot> callproductsearch;
    private DbBitmapUtility dbBitmapUtility;

    public Boolean imgcondition = true;
    private LinearLayout imgdownloadlayout;
    private TextView downloadconditiontext;
    int i = 0;
    public NumberProgressBar numberProgressBar;
    public TimerTask doAsynchronousTask;
    SharedPreferences preferencescurrent;
    SharedPreferences.Editor editorcurrent;
    private Toolbar mytoolbar;
    ProgressBar payprogressbar;
    barcodevalue mvalue;
    SharedPreferences sharedPreferencespoints;
     List<String> categoriesname,categoriesids;
     Gson gson;
     Utils utils;
Cashieryviewmodel cashieryviewmodel;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales__screen);
        gson=new Gson();
        utils=new Utils(this);

        cashieryviewmodel= ViewModelProviders.of(this).get(Cashieryviewmodel.class);
        categoriesname=new ArrayList<>();
        categoriesids=new ArrayList<>();

        mytoolbar = findViewById(R.id.toolbarsalesscreen);
        setSupportActionBar(mytoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        mvalue = new barcodevalue();

        preferencescurrent = getSharedPreferences("progressvalues", Context.MODE_PRIVATE);
        editorcurrent = preferencescurrent.edit();
        dbBitmapUtility = new DbBitmapUtility();
        downloadconditiontext = findViewById(R.id.downloadcondition);
        imgdownloadlayout = findViewById(R.id.downloadlayout);
        configuration = getResources().getConfiguration();
        applanguage = configuration.getLayoutDirection();
        prefs = getSharedPreferences("token", Context.MODE_PRIVATE);
        sharedPreferencespoints=getSharedPreferences("pointsync",Context.MODE_PRIVATE);

        usertoken = prefs.getString("usertoken", "def");
        myadapter = new showproduct_adapter(this);
        mydatabase = new productdatabase(this);
        adapteradded = new werpx.cashiery.Cashiery.Recycleadapter(this);
        show_product_recycle = findViewById(R.id.prodcts_show_recycle);
        payprogressbar=findViewById(R.id.payprogress1);
        addedproducts = findViewById(R.id.addedproductsrecycle);
        numberProgressBar = findViewById(R.id.numberprogres);
        typebarcode = findViewById(R.id.barcodetype);
        totalprice = findViewById(R.id.totalpricec1);
        paytext = findViewById(R.id.paylayout1);
        String type = getSharedPreferences("storedetails", Context.MODE_PRIVATE).getString("storetype", "");
        Categoryid = getSharedPreferences("storedetails", Context.MODE_PRIVATE).getString("categoryid", "");
        producerid = getSharedPreferences("storedetails", Context.MODE_PRIVATE).getString("producerid", "");
        unitid = getSharedPreferences("storedetails", Context.MODE_PRIVATE).getString("unitid", "");
        configuration = getResources().getConfiguration();
        applanguage = configuration.getLayoutDirection();


        productname = "Unregistered Product";
        LocalNameValue = "منتج غير مسجل";



        products = new ArrayList<>();
        myprodscreen = new ArrayList<>();
        mSwipeRefreshLayout = findViewById(R.id.swipe_container);

        if (haveNetworkConnection())
        {
            if (mydatabase.getproductsitems().size() == 0) {
                mSwipeRefreshLayout.setRefreshing(true);

            }
        }
        else {
            mSwipeRefreshLayout.setRefreshing(false);
        }


        //


        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (haveNetworkConnection()) {
                   // getallproducts();
                    setmarketcategoriesrefresh();
                    editorcurrent.putInt("current", 0);
                    editorcurrent.apply();
                } else {
                    mSwipeRefreshLayout.setRefreshing(false);
                }


            }
        });

        retailerid = getSharedPreferences("retailerdata", Context.MODE_PRIVATE).getString("id", "n");


        mWordViewModel = ViewModelProviders.of(this).get(werpx.cashiery.RoomDatabase.productViewmodel.class);
        mWordViewModel.getAllWords().observe(this, new Observer<List<werpx.cashiery.RoomDatabase.mytable>>() {
            @Override
            public void onChanged(@Nullable final List<werpx.cashiery.RoomDatabase.mytable> words) {

                adapteradded.setWords(words);
                myproducts = words;
                Double allcoast = 0.0;

                for (int i = 0; i < words.size(); i++) {
                    Double unitprice = Double.parseDouble(words.get(i).getPprice());
                    int quantity = words.get(i).getPitemn();
                    Double productcoast = unitprice * quantity;
                    allcoast = allcoast + productcoast;
                }

                if (utils.checkapplanguage()==0)
                {
                    totalprice.setText(new DecimalFormat("##.##").format(allcoast));

                }
                else{
                    Typeface typeface = ResourcesCompat.getFont(Sales_Screen.this, R.font.arabicnum);
                    totalprice.setTypeface(typeface);

                    totalprice.setText(new DecimalFormat("##.##").format(allcoast));


                }


            }


        });


        show_product_recycle.setHasFixedSize(true);
        show_product_recycle.setItemAnimator(new DefaultItemAnimator());
        cashieryviewmodel.getproducts().observe(this, new Observer<List<producttable>>() {
            @Override
            public void onChanged(@Nullable List<producttable> producttables) {
                products=producttables;
                myadapter.setproducts(producttables);
                show_product_recycle.setAdapter(myadapter);
            }
        });


        setUpRecyclerView();


        categoryspinner = findViewById(R.id.categoryspin);
        //getcategoriesstore();
       // List<CharSequence> list = new ArrayList<>();
      //  list.addAll(Arrays.asList(getResources().getStringArray(R.array.categries)));

       // spinneradapter madapter = new spinneradapter(this, android.R.layout.simple_spinner_item, list);
       // madapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


       // categoryspinner.setAdapter(madapter);

/*
        categoryspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                List<Sqlitetable> products=new ArrayList<>();
                if(position==0)
                {
                    products=mydatabase.getproductsitems();
                }
                else{
                    products=mydatabase.selectcategoryproduct(position-1);
                }

               myadapter.setproducts(products);
               myadapter.notifyDataSetChanged();
               show_product_recycle.setAdapter(myadapter);


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
*/
        paytext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (myproducts.size()==0)
                {
                    Toast.makeText(Sales_Screen.this,getResources().getString(R.string.listempty),Toast.LENGTH_LONG).show();
                }
                else{
                   payprogressbar.setVisibility(View.VISIBLE);
                   paytext.setText("");

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
                        retailerids.add(retailerid);
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

                        String currentpoint=sharedPreferencespoints.getString("point","0");

                        if (currentpoint.equals("0"))
                        {
                            SharedPreferences.Editor editor=sharedPreferencespoints.edit();
                            editor.putString("point",String.valueOf(totalordercoast));
                            editor.apply();
                        }
                        else{


                            Double currentpointconvert=totalordercoast;
                            Double lastpoint=Double.parseDouble(currentpoint);
                            Double sum=lastpoint+currentpointconvert;

                            SharedPreferences.Editor editor=sharedPreferencespoints.edit();
                            editor.putString("point",String.valueOf(sum));
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
                        payprogressbar.setVisibility(View.GONE);
                        paytext.setText("Pay");
                        Boolean check= insertofflinesales("1",formattedDate,String.valueOf(totalordercoast),String.valueOf(totalorderitems),quantities,barcodes,retailerids,productsids,lives);
                        if (check)
                        {
                            Toast.makeText(Sales_Screen.this,getResources().getString(R.string.insertofflinesales),Toast.LENGTH_LONG).show();
                            mWordViewModel.deleteallproduct();

                        }

                    }

                }

            }


        });

        typebarcode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before,
                                      int count) {
                if (timer != null)
                    timer.cancel();
            }

            @Override
            public void afterTextChanged(final Editable s) {
                //avoid triggering event when text is too short
                if (s.length() == 0) {
                    myadapter.setproducts(products);
                }
                else if (s.toString().matches("\\d+(?:\\.\\d+)?"))
                {
                   // barcodefinal = convertToEnglish(s.toString());
                    //checkifproductexsistsearchbar(barcodefinal);
                }
                else {
                   // searchforproductbyNameoffline(s.toString());
                }
            }
        });


        typebarcode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                                                  @Override
                                                  public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                                                      if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                                                              actionId == EditorInfo.IME_ACTION_DONE ||
                                                              event != null &&
                                                                      event.getAction() == KeyEvent.ACTION_DOWN &&
                                                                      event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                                                          if (event == null || !event.isShiftPressed()) {
                                                              // the user is done typing.
                                                              String barcode = typebarcode.getText().toString().trim();
                                                              if (!barcode.trim().equalsIgnoreCase("")) {
                                                                  if (barcode.matches("\\d+(?:\\.\\d+)?")) {
                                                                      barcodefinal = utils.convertToEnglish(barcode);
                                                                      checkifproductexsist(barcodefinal);
                                                                  } else {

                                                                      //searchforproductbyNameoffline(barcode);
                                                                         // searchforproductbyName(barcode);


                                                                      }



                                                                  InputMethodManager inputManager = (InputMethodManager)
                                                                          getSystemService(Context.INPUT_METHOD_SERVICE);
                                                                  inputManager.toggleSoftInput(0, 0);
                                                                  return true;
                                                              }


                                                          }
                                                      }
                                                      return false; // pass on to other listeners.
                                                  }
                                              }
        );
        setmarketcategories();

    }

    @Override
    protected void onResume() {
        super.onResume();



    }


    public void setmarketcategories() {

        List<Sqlitetable> products=utils.getMydatabase().getproductsitems();
        if (products.size()==0)
        {
            //refrshcategories.setRefreshing(true);

            Call<Catgwithsubroot> callcategories;
            Retrofitclient myretro = Retrofitclient.getInstance();
            Retrofit retrofittok = myretro.getretro();
            final Endpoints myendpoints = retrofittok.create(Endpoints.class);
            callcategories = myendpoints.getmarketcategories("Bearer " + usertoken, utils.getstoretype());
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

                    Toast.makeText(Sales_Screen.this,getResources().getString(R.string.connectionfailed),Toast.LENGTH_SHORT).show();

                    mSwipeRefreshLayout.setRefreshing(false);
                    setcategorieswithproductdownloaded();

                }
            });
        }
        else {

            setcategorieswithproductdownloaded();


        }

    }


    public void setcategorieswithproductdownloaded() {
        String jsonsub = getSharedPreferences("marketinfo", Context.MODE_PRIVATE).getString("subcat", "");
        if (!jsonsub.equals("")) {

            Type type = new TypeToken<List<Subcategory>>() {
            }.getType();
            List<Subcategory> subcategories = utils.getGson().fromJson(jsonsub, type);
            List<String> subnames = new ArrayList<>();
            subnames.add(getResources().getString(R.string.allcategories));
            for (int i = 0; i < subcategories.size(); i++) {
                if (utils.checkapplanguage() == 1) {
                    String namesub = subcategories.get(i).getLocalname();
                    subnames.add(namesub);
                } else {
                    String namesub = subcategories.get(i).getName();
                    subnames.add(namesub);
                }

            }
            spinneradapter madapter = new spinneradapter(Sales_Screen.this, android.R.layout.simple_spinner_item, subnames);
            madapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            categoryspinner.setAdapter(madapter);

          cashieryviewmodel.getproducts().observe(this, new Observer<List<producttable>>() {
                @Override
                public void onChanged(@Nullable List<producttable> producttables) {
                    myadapter.setproducts( producttables);
                    show_product_recycle.setAdapter(myadapter);
                }
            });


        }
    }

    public void setmarketcategoriesrefresh()
    {
        //refrshcategories.setRefreshing(true);

        Call<Catgwithsubroot> callcategories;
        Retrofitclient myretro = Retrofitclient.getInstance();
        Retrofit retrofittok = myretro.getretro();
        final Endpoints myendpoints = retrofittok.create(Endpoints.class);
        callcategories = myendpoints.getmarketcategories("Bearer " + usertoken, utils.getstoretype());
        callcategories.enqueue(new Callback<Catgwithsubroot>() {
            @Override
            public void onResponse(Call<Catgwithsubroot> call, Response<Catgwithsubroot> response) {

                if (response.isSuccessful()) {

                    List<Subcategory> subcategories = response.body().getCategory().getSubcategories();
                    if (subcategories.size()!=0)
                    {
                        String json = utils.getGson().toJson(subcategories);
                        getSharedPreferences("marketinfo", Context.MODE_PRIVATE).edit().putString("subcat",json).apply();
                    }

                  //  refrshcategories.setRefreshing(true);
                    new insertproductswithcat().execute(subcategories);


                }
            }

            @Override
            public void onFailure(Call<Catgwithsubroot> call, Throwable t) {


                Toast.makeText(Sales_Screen.this,getResources().getString(R.string.connectionfailed),Toast.LENGTH_SHORT).show();

                mSwipeRefreshLayout.setRefreshing(false);

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
        mWordViewModel.inserthis(myhis);
    }

    private void saleoperation(final Double totalordercoast, final int totalitems, ArrayList<String> barcode, ArrayList<String> quantity, ArrayList<String> retailerid, ArrayList<String> productid, ArrayList<Boolean> live)
    {
        werpx.cashiery.Retrofitclient myretro= werpx.cashiery.Retrofitclient.getInstance();
        Retrofit retrofit=  myretro.getretro();

        final werpx.cashiery.Endpoints myendpoints = retrofit.create(werpx.cashiery.Endpoints.class);
        callsell= myendpoints.salearray(barcode,quantity,retailerid,productid,live);
        callsell.enqueue(new Callback<ResponseBody>() {
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


                            Toast.makeText(Sales_Screen.this, getResources().getString(R.string.succefulpayment), Toast.LENGTH_LONG).show();
                            payprogressbar.setVisibility(View.GONE);
                            paytext.setText("Pay");
                        } else {
                            Toast.makeText(Sales_Screen.this, getResources().getString(R.string.failedpayment), Toast.LENGTH_LONG).show();
                           payprogressbar.setVisibility(View.GONE);
                            paytext.setText("Pay");
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
//
                }else{
                    Toast.makeText( Sales_Screen.this,getResources().getString(R.string.failedpayment),Toast.LENGTH_LONG).show();
                    payprogressbar.setVisibility(View.GONE);
                    paytext.setText("Pay");
                }


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                payprogressbar.setVisibility(View.GONE);
                paytext.setText("Pay");
                Toast.makeText( Sales_Screen.this,getResources().getString(R.string.connectionfailed),Toast.LENGTH_LONG).show();


            }
        });
    }

    private Boolean insertofflinesales(String ornum, String ordata, String oramount, String units, ArrayList<String> quantites, ArrayList<String> orbarcodes,
                                       ArrayList<String> orretailerids, ArrayList<String> orproductids, ArrayList<Boolean> orlives) {
        Gson gson = new Gson();
        String barcodes = gson.toJson(orbarcodes);
        String retailerids = gson.toJson(orretailerids);
        String produvtids = gson.toJson(orproductids);
        String lives = gson.toJson(orlives);
        String quant = gson.toJson(quantites);
        return mydatabase.insertofflinesales(ornum, ordata, oramount, units, barcodes, retailerids, produvtids, lives, quant);
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

    private void checkifproductexsist(String barcod) {
        boolean mycondition = true;

        // Update the cached copy of the words in the adapter.
        int i;
        if (myproducts.size() != 0) {


            for (i = 0; i < myproducts.size(); i++) {
                if (barcod.trim().equals(myproducts.get(i).getPbar().trim())) {

                    werpx.cashiery.RoomDatabase.mytable current = myproducts.get(i);
                    // int totalitems = current.getPitemn() + Integer.parseInt(numitems);
                    mWordViewModel.updateproduct(current.getPitemn() + 1, Long.parseLong(barcod));
                    mycondition = false;

                    scrolltoposition(i);


                    Handler myhandler = new Handler();
                    myhandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            typebarcode.setText("");

                            typebarcode.requestFocus();
                            InputMethodManager inputManager = (InputMethodManager)
                                    getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputManager.toggleSoftInput(0, 0);
                        }
                    }, 1000);

                }

            }
            if (mycondition) {
                getrpoductbybarcodeoffline(barcod);
               // loginwithenternumber(barcod);

            }

        } else {
            getrpoductbybarcodeoffline(barcod);
            //loginwithenternumber(barcod);
        }

    }

    private void checkifproductexsistsearchbar(String barcod) {
        boolean mycondition = true;

        // Update the cached copy of the words in the adapter.
        int i;
        if (myproducts.size() != 0) {


            for (i = 0; i < myproducts.size(); i++) {
                if (barcod.trim().equals(myproducts.get(i).getPbar().trim())) {

                    werpx.cashiery.RoomDatabase.mytable current = myproducts.get(i);
                    // int totalitems = current.getPitemn() + Integer.parseInt(numitems);
                    mWordViewModel.updateproduct(current.getPitemn() + 1, Long.parseLong(barcod));
                    mycondition = false;

                    scrolltoposition(i);


                    Handler myhandler = new Handler();
                    myhandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            typebarcode.setText("");

                            typebarcode.requestFocus();
                            InputMethodManager inputManager = (InputMethodManager)
                                    getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputManager.toggleSoftInput(0, 0);
                        }
                    }, 1000);

                }

            }
            if (mycondition) {
                getrpoductbybarcodeofflinesearchbar(barcod);
                // loginwithenternumber(barcod);

            }

        } else {
            getrpoductbybarcodeofflinesearchbar(barcod);
            //loginwithenternumber(barcod);
        }

    }

    public void getrpoductbybarcodeofflinesearchbar(String barcode)
    {
        werpx.cashiery.RoomDatabase.Sqlitetable mytable = mydatabase.getdataforrowinproduct(barcode);
        werpx.cashiery.RoomDatabase.Sqlitetable mytablee = mydatabase.getdataforrowinproductoffline(barcode);
        werpx.cashiery.RoomDatabase.Sqlitetable mytableee = mydatabase.getdataforrowinfinalproductoffline(barcode);


        if (mytable != null) {
            mWordViewModel.insert(new werpx.cashiery.RoomDatabase.mytable(mytable.getPid(), mytable.getName(), mytable.getBarcode(), Integer.parseInt("1"), mytable.getImge(), mytable.getDescription(), mytable.getPrice(), null, mytable.getLocalnam(), mytable.getImgblob()));

            runOnUiThread(new Runnable() {

                @Override
                public void run() {


                    typebarcode.setText("");

                    typebarcode.requestFocus();
                    InputMethodManager inputManager = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.toggleSoftInput(0, 0);

                }
            });

        } else if (mytablee != null) {
            mWordViewModel.insert(new werpx.cashiery.RoomDatabase.mytable(mytablee.getPid(), mytablee.getName(), mytablee.getBarcode(), Integer.parseInt("1"), mytablee.getImge(), mytablee.getDescription(), mytablee.getPrice(), null, mytablee.getLocalnam(), mytablee.getImgblob()));

            runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    typebarcode.setText("");
                    typebarcode.requestFocus();
                    InputMethodManager inputManager = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.toggleSoftInput(0, 0);

                }
            });
        } else if (mytableee != null) {
            mWordViewModel.insert(new werpx.cashiery.RoomDatabase.mytable(mytableee.getPid(), mytableee.getName(), mytableee.getBarcode(), Integer.parseInt("1"), mytableee.getImge(), mytableee.getDescription(), mytableee.getPrice(), null, mytableee.getLocalnam(), mytableee.getImgblob()));

            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    typebarcode.setText("");
                    typebarcode.requestFocus();
                    InputMethodManager inputManager = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.toggleSoftInput(0, 0);

                }
            });
        } else {

        }


    }





    private void loginwithenternumber(final String barcode) {


        werpx.cashiery.Retrofitclient myretro = werpx.cashiery.Retrofitclient.getInstance();
        Retrofit retrofittok = myretro.getretro();
        final werpx.cashiery.Endpoints myendpoints = retrofittok.create(werpx.cashiery.Endpoints.class);

        mcall = myendpoints.getdetails("Bearer " + usertoken, barcode);
        mcall.enqueue(new Callback<werpx.cashiery.productmodels.Rootproductdetail>() {
            @Override
            public void onResponse(Call<werpx.cashiery.productmodels.Rootproductdetail> call, Response<werpx.cashiery.productmodels.Rootproductdetail> response) {

                if (response.isSuccessful()) {
                    String pid, pronam, prodbar, prodimg, broddetail,
                            brodprice, prodcat, prname, prlocalnam;


                    if (response.body().getProduct() != null) {

                        if (response.body().getProduct().getImage() == null) {
                            pid = response.body().getProduct().getId();

                            prodbar = response.body().getProduct().getBarcode();
                            broddetail = response.body().getProduct().getDescription();
                            brodprice = response.body().getProduct().getPrice();

                            prname = response.body().getProduct().getName();
                            prlocalnam = response.body().getProduct().getLocalname();

                            prodcat = response.body().getProduct().getCategory().getName();
                            werpx.cashiery.RoomDatabase.mytable word = new werpx.cashiery.RoomDatabase.mytable(pid, prname, prodbar, Integer.parseInt("1"), null, broddetail, brodprice, prodcat, prlocalnam, null);
                            mWordViewModel.insert(word);
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {

                                    typebarcode.setText("");
                                    typebarcode.requestFocus();
                                    InputMethodManager inputManager = (InputMethodManager)
                                            getSystemService(Context.INPUT_METHOD_SERVICE);
                                    inputManager.toggleSoftInput(0, 0);


                                }
                            });

                        } else {
                            pid = response.body().getProduct().getId();
                            prname = response.body().getProduct().getName();
                            prlocalnam = response.body().getProduct().getLocalname();
                            prodbar = response.body().getProduct().getBarcode();
                            prodimg = response.body().getProduct().getImage().getFilename();
                            String imgurl = getResources().getString(R.string.baseurlimgproduct) + prodimg;

                            broddetail = response.body().getProduct().getDescription();
                            brodprice = response.body().getProduct().getPrice();

                            prodcat = response.body().getProduct().getCategory().getName();
                            werpx.cashiery.RoomDatabase.mytable word = new werpx.cashiery.RoomDatabase.mytable(pid, prname, prodbar, Integer.parseInt("1"), imgurl, broddetail, brodprice, prodcat, prlocalnam, null);
                            mWordViewModel.insert(word);
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {


                                    typebarcode.setText("");

                                    typebarcode.requestFocus();
                                    InputMethodManager inputManager = (InputMethodManager)
                                            getSystemService(Context.INPUT_METHOD_SERVICE);
                                    inputManager.toggleSoftInput(0, 0);

                                }
                            });


                        }

                    } else {
                        addnewproduct();
                    }

                } else {

                    addnewproduct();


                }


            }

            @Override
            public void onFailure(Call<werpx.cashiery.productmodels.Rootproductdetail> call, Throwable t) {


                getrpoductbybarcodeoffline(barcode);

    }


        });

    }

    public void addnewproduct() {
        final EditText addnam, addpric;
        TextView add, cancel;
        ImageView incre, decre;
        builder = new android.app.AlertDialog.Builder(Sales_Screen.this);

        View myview = LayoutInflater.from(Sales_Screen.this.getApplicationContext()).inflate(R.layout.addnewproduct, null);

        addpric = myview.findViewById(R.id.addprice);
        add = myview.findViewById(R.id.addproductt);
        cancel = myview.findViewById(R.id.cancelproduct);

        builder.setView(myview);
        alertDialog = builder.create();
        alertDialog.show();


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {


                if (addpric.getText().toString().trim().equalsIgnoreCase("")) {
                    Toast.makeText(Sales_Screen.this, getResources().getString(R.string.addprice), Toast.LENGTH_LONG).show();
                } else {

                    String token = getSharedPreferences("token", Context.MODE_PRIVATE).getString("usertoken", "");

                    Call<ResponseBody> calladd;
                    werpx.cashiery.Retrofitclient myretro = werpx.cashiery.Retrofitclient.getInstance();
                    Retrofit retrofittok = myretro.getretro();
                    final werpx.cashiery.Endpoints myendpoints = retrofittok.create(werpx.cashiery.Endpoints.class);
                    calladd = myendpoints.addproduct("Bearer " + token, barcodefinal,Categoryid,"description",productname,addpric.getText().toString(),producerid,"distributor",unitid,LocalNameValue,
                            "19AE5F5B-7207-48B8-BB4D-14344321AC00",1,false);

                    calladd.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                            if (response.isSuccessful()) {
                                loginwithenternumber(typebarcode.getText().toString());
                               // getallproducts();
                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {

                                        typebarcode.setText("");
                                        typebarcode.requestFocus();
                                      //  myadapter.setproducts(products);

                                        InputMethodManager inputManager = (InputMethodManager)
                                                getSystemService(Context.INPUT_METHOD_SERVICE);
                                        inputManager.toggleSoftInput(0, 0);

                                    }
                                });

                            } else {

                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {

                            werpx.cashiery.RoomDatabase.mytable word = new werpx.cashiery.RoomDatabase.mytable(null, productname, barcodefinal, Integer.parseInt("1"), null, null, addpric.getText().toString(), null, productname, null);
                            Boolean val = addofflinesproducts(productname, typebarcode.getText().toString(), addpric.getText().toString(), null, "detail", LocalNameValue);
                            Toast.makeText(Sales_Screen.this, String.valueOf(val), Toast.LENGTH_LONG).show();
                            mWordViewModel.insert(word);
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {

                                    typebarcode.setText("");

                                    typebarcode.requestFocus();

                                    List<Sqlitetable> mtable = mydatabase.getofflineproducts();

                                 //   mtable.addAll(products);
                                   // myadapter.setproducts(mtable);

                                    InputMethodManager inputManager = (InputMethodManager)
                                            getSystemService(Context.INPUT_METHOD_SERVICE);
                                    inputManager.toggleSoftInput(0, 0);

                                }
                            });
                            alertDialog.cancel();

                        }
                    });


                    alertDialog.cancel();


                }


            }

        });


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        typebarcode.setText("");

                        typebarcode.requestFocus();
                        InputMethodManager inputManager = (InputMethodManager)
                                getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputManager.toggleSoftInput(0, 0);

                    }
                });


            }
        });

    }

    public void getrpoductbybarcodeoffline(String barcode)
    {
        werpx.cashiery.RoomDatabase.Sqlitetable mytable = mydatabase.getdataforrowinproduct(barcode);
        werpx.cashiery.RoomDatabase.Sqlitetable mytablee = mydatabase.getdataforrowinproductoffline(barcode);
        werpx.cashiery.RoomDatabase.Sqlitetable mytableee = mydatabase.getdataforrowinfinalproductoffline(barcode);


        if (mytable != null) {
            mWordViewModel.insert(new werpx.cashiery.RoomDatabase.mytable(mytable.getPid(), mytable.getName(), mytable.getBarcode(), Integer.parseInt("1"), mytable.getImge(), mytable.getDescription(), mytable.getPrice(), null, mytable.getLocalnam(), mytable.getImgblob()));

            runOnUiThread(new Runnable() {

                @Override
                public void run() {


                    typebarcode.setText("");

                    typebarcode.requestFocus();
                    InputMethodManager inputManager = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.toggleSoftInput(0, 0);

                }
            });

        } else if (mytablee != null) {
            mWordViewModel.insert(new werpx.cashiery.RoomDatabase.mytable(mytablee.getPid(), mytablee.getName(), mytablee.getBarcode(), Integer.parseInt("1"), mytablee.getImge(), mytablee.getDescription(), mytablee.getPrice(), null, mytablee.getLocalnam(), mytablee.getImgblob()));

            runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    typebarcode.setText("");
                    typebarcode.requestFocus();
                    InputMethodManager inputManager = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.toggleSoftInput(0, 0);

                }
            });
        } else if (mytableee != null) {
            mWordViewModel.insert(new werpx.cashiery.RoomDatabase.mytable(mytableee.getPid(), mytableee.getName(), mytableee.getBarcode(), Integer.parseInt("1"), mytableee.getImge(), mytableee.getDescription(), mytableee.getPrice(), null, mytableee.getLocalnam(), mytableee.getImgblob()));

            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    typebarcode.setText("");
                    typebarcode.requestFocus();
                    InputMethodManager inputManager = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.toggleSoftInput(0, 0);

                }
            });
        } else {
            loginwithenternumber(barcode);
          //  addnewproduct();
        }


    }

    public Boolean addofflinesproducts(String name, String bar, String price, String img, String detail, String localnam) {
        return mydatabase.insertofflieproducts(name, bar, price, img, detail, localnam);
    }




        @Override
        public boolean onSupportNavigateUp () {
            onBackPressed();
            return true;
        }


        public void setUpRecyclerView () {
            addedproducts.setAdapter(adapteradded);
            addedproducts.setLayoutManager(new LinearLayoutManager(this));

            ItemTouchHelper itemTouchHelper = new
                    ItemTouchHelper(new SwipeToDeleteCallback(adapteradded, this));
            itemTouchHelper.attachToRecyclerView(addedproducts);
        }

        public void scrolltoposition ( final int pos){


            addedproducts.post(new Runnable() {
                @Override
                public void run() {
                    if (adapteradded.getItemCount() == 0) {

                    } else {
                        addedproducts.smoothScrollToPosition(pos);
                    }

                }
            });

        }

        public void scrolltodown () {

            addedproducts.post(new Runnable() {
                @Override
                public void run() {
                    if (adapteradded.getItemCount() == 0) {

                    } else {
                        addedproducts.smoothScrollToPosition(adapteradded.getItemCount() + 1);
                    }

                }
            });

        }
/*
        private void searchforproductbyNameoffline (String searchname){
            List<Sqlitetable> allproducts = mydatabase.getproductsitems();
            List<Sqlitetable> searchproducts = new ArrayList<>();
            searchproducts.clear();

            if (allproducts.size() != 0) {
                for (int t = 0; t < allproducts.size(); t++) {

                    if (allproducts.get(t).getName().trim().toLowerCase().contains(searchname.toLowerCase())) {
                        searchproducts.add(allproducts.get(t));

                    } else if (allproducts.get(t).getLocalnam().trim().toLowerCase().contains(searchname.toLowerCase())) {
                        searchproducts.add(allproducts.get(t));

                    }


                }
                myadapter.setproducts(searchproducts);
            }

        }


*/
        @Override
        protected void onDestroy () {
            super.onDestroy();


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
                        cashieryviewmodel.insertproductitem(new producttable(name,localnam,productid,0,price,bar,"details",null,i));

                    } else {
                        String imgfull = getResources().getString(R.string.baseurlimgproduct) + products.get(j).getImage().getFilename();

                      //  utils.getMydatabase().insertdatalistproducts(name, bar, price, imgfull, "details", productid, localnam, null,i);
                        cashieryviewmodel.insertproductitem(new producttable(name,localnam,productid,0,price,bar,"details",imgfull,i));

                    }
                    if (products.size() - 1 == j) {

                        editor.putInt(String.valueOf(i), utils.getMydatabase().getproductsCount());
                        editor.apply();
                    }


                }
            }
            return subcategories;
        }

        @Override
        protected void onPostExecute(List<Subcategory> aVoid) {
            super.onPostExecute(aVoid);

            mSwipeRefreshLayout.setRefreshing(false);
            setcategorieswithproductdownloaded();



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


    private static class MyTaskParams {
        List<werpx.cashiery.subcategorymodel.Product> products;
        String idcategory;


        MyTaskParams(List<werpx.cashiery.subcategorymodel.Product> products, String idcategory ) {
            this.products = products;
            this.idcategory=idcategory;
        }
    }
}
