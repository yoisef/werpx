package werpx.cashiery.Cashiery;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import werpx.cashiery.R;

public class pharmormarket extends AppCompatActivity {


    TextView pharmstore,marketstore;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pharmormarket);

        preferences=getSharedPreferences("storecategory", Context.MODE_PRIVATE);
        editor=preferences.edit();


        pharmstore=findViewById(R.id.pharmchoose);
        marketstore=findViewById(R.id.marketchoose);


        pharmstore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                editor.putString("type","pharmacy");
                editor.apply();

                Intent myintent=new Intent(pharmormarket.this, Choose_Store.class);
                startActivity(myintent);

            }
        });

        marketstore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editor.putString("type","market");
                editor.apply();

                Intent intentm=new Intent(pharmormarket.this,Choose_Store.class);
                startActivity(intentm);
            }
        });
    }
    }

