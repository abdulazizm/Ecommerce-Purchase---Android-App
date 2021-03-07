package com.FingerPointEngg.Labs.FreeGifts;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

public class ProductPage extends AppCompatActivity {

    int prod_id;
    String price,title,imageurl,quantity,l_descrip;
    Bundle data;
    String[] items = new String[]{"1", "2", "3"};
    TextView p_title,p_price, delivery, description;
    ImageView image;
    Button buy_now,add_to_cart;
    ProgressDialog progressDialog;
    Spinner dropdown;
    LocalDb db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_page);
        db=new LocalDb(getApplicationContext());

        progressDialog = ProgressDialog.show(ProductPage.this, "Loading Product Contents", null, true, true);

        final Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                progressDialog.dismiss();
            }
        }, 1000);

        image = (ImageView) findViewById(R.id.p_image);

        dropdown = findViewById(R.id.quan);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        data = getIntent().getExtras();
        prod_id = Integer.parseInt(data.getString("pid"));
        price = data.getString("price");
        title = data.getString("title");
        imageurl = data.getString("imageurl");
        l_descrip = data.getString("description");

        buy_now = findViewById(R.id.buynow);
        add_to_cart = findViewById(R.id.add_to_cart);
        p_title = findViewById(R.id.title);
        p_price = findViewById(R.id.price);
        delivery = findViewById(R.id.delivery);
        description = findViewById(R.id.description);

        p_title.setText(title);
        if(!price.equals("FREE"))
            p_price.setText("₹"+price);
        else
            p_price.setText(price);
        if(!db.checkNewUser()) {
            delivery.setText("₹50");
        }else{
            delivery.setText("FREE");
        }
        description.setText(l_descrip);

        new LoadImage(image).execute(imageurl);

        buy_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent product = new Intent(getApplicationContext(), BuyNow.class);
                quantity = dropdown.getSelectedItem().toString();
                data.putString("quantity",quantity);
                product.putExtras(data);
                startActivity(product);
            }
        });

        add_to_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent product = new Intent(getApplicationContext(), BuyNow.class);
                quantity = dropdown.getSelectedItem().toString();
                data.putString("quantity",quantity);
                product.putExtras(data);
                startActivity(product);
            }
        });
    }
}
