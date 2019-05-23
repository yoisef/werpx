package werpx.cashiery.Cashiery;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import werpx.cashiery.R;


public class Choose_Store extends AppCompatActivity {


    TextView addstore,newseller;
   LinearLayout enterstoreid;
   String typestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign__up);

        typestore=getSharedPreferences("storecategory", Context.MODE_PRIVATE).getString("type","");

        newseller=findViewById(R.id.havestore);
        addstore=findViewById(R.id.newstoree);



        if (typestore.trim().equals("market"))
        {
             addstore.setText(getResources().getString(R.string.newmarket));
        }
        else
        {
            addstore.setText(getResources().getString(R.string.newpharn));

        }


        newseller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent myintent=new Intent(Choose_Store.this, registerphone.class);
                if (typestore.equals("market"))
                {
                    myintent.putExtra("value","market");

                }
                else{
                    myintent.putExtra("value","pharmacy");
                }

                startActivity(myintent);

            }
        });

        addstore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent zintent=new Intent(Choose_Store.this, registerphone.class);
                startActivity(zintent);
            }
        });
    }
}
