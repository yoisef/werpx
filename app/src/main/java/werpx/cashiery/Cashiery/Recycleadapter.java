package werpx.cashiery.Cashiery;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.daimajia.swipe.SwipeLayout;

import java.text.DecimalFormat;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import werpx.cashiery.Endpoints;

import werpx.cashiery.GlideApp;
import werpx.cashiery.R;
import werpx.cashiery.Retrofitclient;
import werpx.cashiery.RoomDatabase.mytable;
import werpx.cashiery.RoomDatabase.productViewmodel;
import werpx.cashiery.Utils;


public class Recycleadapter extends RecyclerView.Adapter<Recycleadapter.viewholder> {

    private   Context con;
    private productViewmodel mWordViewModel;

    private Thread mythread ;
    private Configuration configuration;
    private int applanguage;
    Utils utils;


    private final LayoutInflater mInflater;
    private List<mytable> mWords; // Cached copy of words
    private  int position;
    private werpx.cashiery.productdatabase mydatabase;
    String usertoken;

    Recycleadapter(Context context) {
        this.con=context;
        utils=new Utils(context);
        mInflater = LayoutInflater.from(context);
        mythread=new Thread();
        mydatabase = new werpx.cashiery.productdatabase(context);

        configuration = con.getResources().getConfiguration();
        applanguage= configuration.getLayoutDirection();
        usertoken = context.getSharedPreferences("token", Context.MODE_PRIVATE).getString("usertoken","");


        mWordViewModel = ViewModelProviders.of((FragmentActivity) context).get(productViewmodel.class);

    }

    @Override
    public viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.rowcashier, parent, false);
        return new viewholder(itemView);
    }



    @Override
    public void onBindViewHolder(final viewholder holder, final int position) {


        if (mWords != null) {

            holder.updatepricebut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    updateproductprice(mWords.get(position).getPid(),holder.editprice.getText().toString(),holder.unitprice,holder.updatepricebut,holder.barcodeee.getText().toString());



                }
            });


            final mytable current = mWords.get(position);
            if (applanguage==0)
            {
                holder.namee.setText(current.getPname());
                holder.unitprice.setText(current.getPprice());
                holder.barcodeee.setText(current.getPbar());
                holder.showitems_number.setText(String.valueOf(current.getPitemn()));

                Double coast=current.getPitemn()*Double.parseDouble(current.getPprice());
                // String convercoast= convertToEnglish(String.valueOf(coast));

                holder.total_itemscoast.setText(new DecimalFormat("##.##").format(coast));

            }
            else{

                Typeface typeface = ResourcesCompat.getFont(con, R.font.arabicnum);
                holder.barcodeee.setTypeface(typeface);
                holder.unitprice.setTypeface(typeface);
                holder.showitems_number.setTypeface(typeface);
                holder.total_itemscoast.setTypeface(typeface);
                holder.namee.setText(current.getPlocalname());

                Double coast=current.getPitemn()*Double.parseDouble(current.getPprice());
                holder.total_itemscoast.setText(new DecimalFormat("##.##").format(coast));

                holder.showitems_number.setText(String.valueOf(current.getPitemn()));
                holder.unitprice.setText(current.getPprice());
                holder.barcodeee.setText(current.getPbar());

            }




                if (current.getPimg()!=null)
                {
                    GlideApp
                            .with(con)
                            .load(current.getPimg())
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(holder.productimage);
                }
                else{
                    GlideApp
                            .with(con)
                            .load(con.getResources().getDrawable(R.drawable.default_product))
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(holder.productimage);
                }





           holder.showitems_number.setText(String.valueOf(current.getPitemn()));
            holder.showitems_number.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                    if (actionId==EditorInfo.IME_ACTION_DONE)
                    {
                        if (!holder.showitems_number.getText().toString().equals(""))
                        {
                            mWordViewModel.updateproduct(Long.parseLong(holder.showitems_number.getText().toString()),Long.parseLong(current.getPbar()));
                            InputMethodManager inputManager = (InputMethodManager)
                                    con.getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputManager.toggleSoftInput(0, 0);

                            return true;
                        }

                    }
                    return false;
                }
            });




            holder.add_items.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int num=Integer.parseInt(holder.showitems_number.getText().toString());
                    int currentnum=num+1;
                    holder.showitems_number.setText(String.valueOf(currentnum));
                    Double Uprice=Double.parseDouble(current.getPprice());
                    Double totalpriceP=currentnum*Uprice;
                    if (utils.checkapplanguage()==0)
                    {
                        holder.total_itemscoast.setText(String.valueOf(totalpriceP));
                        mWordViewModel.updateproduct(Long.parseLong(holder.showitems_number.getText().toString()),Long.parseLong(current.getPbar()));

                    }
                    else{
                        Typeface typeface = ResourcesCompat.getFont(con, R.font.arabicnum);
                        holder.total_itemscoast.setTypeface(typeface);

                        holder.total_itemscoast.setText(String.valueOf(totalpriceP));
                        mWordViewModel.updateproduct(Long.parseLong(holder.showitems_number.getText().toString()),Long.parseLong(current.getPbar()));

                    }


                }
            });
            holder.remove_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int num=Integer.parseInt(holder.showitems_number.getText().toString());
                    if (num <= 1) {
                        holder.showitems_number.setText(String.valueOf(1));

                    } else {
                        int cunum=num-1;
                        holder.showitems_number.setText(String.valueOf(cunum));
                        Double Uprice=Double.parseDouble(current.getPprice());
                        Double totalp=Uprice*cunum;
                        holder.total_itemscoast.setText(String.valueOf(totalp));
                        mWordViewModel.updateproduct(Long.parseLong(holder.showitems_number.getText().toString()),Long.parseLong(current.getPbar()));

                    }
                }
            });



        } else {
            // Covers the case of data not being ready yet.
            holder.namee.setText("No Product");
        }





    }


    void setWords(List<mytable> words) {
        mWords = words;
        notifyDataSetChanged();
    }
    public void updateproductprice(final String id, final String price , final TextView unitprice, final TextView updatebut, final String barcodeed)
    {
        final String barcode=con.getSharedPreferences("productinfo",Context.MODE_PRIVATE).getString("bar","");
        Call<ResponseBody> mycall;
        Retrofitclient myretro = Retrofitclient.getInstance();
        Retrofit retrofitt = myretro.getretro();
        final Endpoints myendpoints = retrofitt.create(Endpoints.class);

        mycall=myendpoints.updateproduct("Bearer "+usertoken,id,id,price,"PATCH");
        mycall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                Toast.makeText(con,"updated",Toast.LENGTH_SHORT).show();
                unitprice.setText(price);
                updatebut.setTextColor(Color.BLACK);
               Boolean val= mydatabase.updateproductprice(price,id);
               Toast.makeText(con,con.getResources().getString(R.string.update),Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                unitprice.setText(price);
                updatebut.setTextColor(Color.BLACK);
                Boolean val=mydatabase.insertproductadjustprices(id,price);
                if (val)
                {
                    Toast.makeText(con,con.getResources().getString(R.string.update),Toast.LENGTH_SHORT).show();
                }



            }
        });
    }


    void notifycange()
    {
        notifyDataSetChanged();
    }
    public void deleteItem(int position) {

        mydatabase.deleterowofofflineproducts(mWords.get(position).getPbar());
        mWordViewModel.delterow(mWords.get(position));
        mWords.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    public Context getChontext()
    {
        return con;
    }

    void deleterow(int i)
    {
        if (mWords.size()!=0){
            mWordViewModel.delterow(mWords.get(i));
            mWords.remove(i);
            notifyItemRemoved(i);

        }

    }

    int getadapterposition()
    {
        int i = position;
        return i;

    }

    // getItemCount() is called many times, and when it is first called,
    // mWords has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (mWords != null)
            return mWords.size();
        else return 0;
    }




    class viewholder extends RecyclerView.ViewHolder {

        ImageView productimage,add_items,remove_item,xremove;
        SwipeLayout rowrecycle;
        TextView namee,unitprice,barcodeee,total_itemscoast,delete_product,updatepricebut;
        EditText showitems_number,editprice;

        public viewholder(View itemView) {
            super(itemView);


            namee = itemView.findViewById(R.id.nameproduct);
            barcodeee=itemView.findViewById(R.id.numberproduct);
            total_itemscoast=itemView.findViewById(R.id.totalitemsc);
            productimage=itemView.findViewById(R.id.productimg);
            add_items=itemView.findViewById(R.id.additemsc);
            remove_item=itemView.findViewById(R.id.removeitemsc);
            showitems_number=itemView.findViewById(R.id.itemsnumberc);
            editprice=itemView.findViewById(R.id.editpricesales);
            updatepricebut=itemView.findViewById(R.id.updatepricesales);

            unitprice=itemView.findViewById(R.id.unitpricec);


           int pos= getadapterposition();
       mytable table=  mWords.get(pos);
          //  showitems_number.setText(String.valueOf(table.getPitemn()));







        }


    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager)con.getSystemService(Context.CONNECTIVITY_SERVICE);
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