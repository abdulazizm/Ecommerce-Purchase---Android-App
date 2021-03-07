package com.FingerPointEngg.Labs.FreeGifts;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;

import instamojo.library.InstamojoPay;
import instamojo.library.InstapayListener;

import static java.lang.System.out;

public class BuyNow extends AppCompatActivity {

    int prod_id;
    String price,title,imageurl, delivery_f,date,estdate,orderid,giftwrap,quantity;
    String ad,username,phone,email;
    TextView address,item_fee, delivery, total_price, p_title,p_price,estimate_delivery;
    ImageView prod_image;
    LocalDb db;
    Bundle data, user;
    CheckBox gift;

    String[] dateSplit;
    //Needed data to construct this page:
    //p_title,p_price

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_now);
        db = new LocalDb(this);

        data = getIntent().getExtras();
        prod_id = Integer.parseInt(data.getString("pid"));
        quantity = data.getString("quantity");
        price = data.getString("price");
        if(!price.equals("FREE"))
        price = String.valueOf(Integer.parseInt(price)*Integer.parseInt(data.getString("quantity")));
        //Toast.makeText(getApplicationContext(), quantity, Toast.LENGTH_SHORT).show();
        title = data.getString("title");
        imageurl = data.getString("imageurl");

        user = db.getUserDetails();

        ad = user.getString("address");
        username = user.getString("username");
        phone = user.getString("phone");
        email = user.getString("email");

        delivery_f="50";
        item_fee = findViewById(R.id.price);
        address = findViewById(R.id.address);
        delivery = findViewById(R.id.delivery_fee);
        p_title = findViewById(R.id.product_title);
        p_price = findViewById(R.id.product_price);
        total_price = findViewById(R.id.price_total);
        estimate_delivery = findViewById(R.id.est_delivery);
        prod_image = findViewById(R.id.p_image);
        gift = findViewById(R.id.gift);

        Random r = new Random();
        orderid = "FPE3D"+r.nextInt(1000)+6230589+r.nextInt(999)+30;

        date = new SimpleDateFormat("EEE, MMM d", Locale.getDefault()).format(new Date());
        dateSplit = date.split(" ");
        //Toast.makeText(getApplicationContext(), dateSplit[1], Toast.LENGTH_SHORT).show();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.add(Calendar.DATE, 7);
        sdf = new SimpleDateFormat("EEE, MMM d");
        Date resultdate = new Date(c.getTimeInMillis());
        estdate = sdf.format(resultdate);

        if(imageurl!=null)
        new LoadImage(prod_image).execute(imageurl);

        if(!(ad.equals("null"))){
            address.setText(ad);
        } else {
            address.setText("Add Delivery Details");
        }

        p_title.setText(title);
        if(price.equals("FREE")) {
            p_price.setText(price);
            if(db.checkNewUser()) {
                delivery.setText("FREE");
                delivery_f="FREE";
                total_price.setText("FREE");
            } else {
                total_price.setText("₹"+ delivery_f);
                delivery.setText("₹" + delivery_f);
            }
            item_fee.setText("FREE");
        } else {
            p_price.setText("₹" + price);
            delivery.setText("₹" + delivery_f);
            total_price.setText("₹" + String.valueOf(Integer.parseInt(price) + Integer.parseInt(delivery_f)));
            item_fee.setText("₹" + price);
        }

        estimate_delivery.setText(estdate);

        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent address = new Intent(getApplicationContext(),AddUserDeliveryData.class);
                address.putExtras(data);
                startActivity(address);
                finish();
            }
        });
        Button place_order = findViewById(R.id.place_order1);
        place_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    proceedToPayment();
            }
        });

        Button place_order2 = findViewById(R.id.place_order2);
        place_order2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                proceedToPayment();
            }
            });
    }

    private void callInstamojoPay(String email, String phone, String amount, String purpose, String buyername) {
        final Activity activity = this;
        InstamojoPay instamojoPay = new InstamojoPay();
        IntentFilter filter = new IntentFilter("ai.devsupport.instamojo");
        registerReceiver(instamojoPay, filter);
        JSONObject pay = new JSONObject();
        try {
            pay.put("email", email);
            pay.put("phone", phone);
            pay.put("purpose", purpose);
            pay.put("amount", amount);
            pay.put("name", buyername);
            pay.put("send_sms", true);
            pay.put("send_email", true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        initListener();
        instamojoPay.start(activity, pay, listener);
    }

    InstapayListener listener;


    private void initListener() {
        listener = new InstapayListener() {
            @Override
            public void onSuccess(String response) {
                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                new OrderUpload().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                db.addOrderDetails(username,orderid,""+prod_id,date,ad,"success",price,giftwrap,phone,"Order Placed",estdate,quantity,delivery_f,imageurl,title,dateSplit[1]);
                Intent home = new Intent(getApplicationContext(),BottomNav.class);
                startActivity(home);
                finish();
            }

            @Override
            public void onFailure(int code, String reason) {
                Toast.makeText(getApplicationContext(), "Failed: " + reason, Toast.LENGTH_SHORT).show();
            }
        };
    }

    private void proceedToPayment(){
        if(gift.isChecked()) giftwrap="yes"; else giftwrap = "no";

        if((phone.equals("null")||ad.equals("null")||ad.length()<15||phone.length()<10)){

            Intent address = new Intent(getApplicationContext(),AddUserDeliveryData.class);
            //address.putExtras(data);
            startActivity(address);
            finish();

        } else {

            if (price.equals("FREE") && db.checkNewUser()) {
                //Toast.makeText(getApplicationContext(), "Order Placed!!", Toast.LENGTH_SHORT).show();
                new OrderUpload().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                db.addOrderDetails(username, orderid, "" + prod_id, date, ad, "success", price, giftwrap, phone, "Order Placed", estdate, quantity, delivery_f, imageurl, title, dateSplit[1]);
                AlertDialog.Builder builder = new AlertDialog.Builder(BuyNow.this);
                builder.setTitle("Order Placed!!");
                builder.setMessage("Congratulations. You have successfully placed your first FREEGIFTS order!!");
                builder.setCancelable(false);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        Intent home = new Intent(getApplicationContext(), BottomNav.class);
                        startActivity(home);
                        finish();
                    }
                });
                builder.setIcon(getResources().getDrawable(R.drawable.ic_lightbulb_off));
                builder.show();

            } else if (price.equals("FREE") && !db.freeOfferAvailable(dateSplit[1])) {

                AlertDialog.Builder builder = new AlertDialog.Builder(BuyNow.this);
                builder.setTitle("Purchase failed");
                builder.setMessage("You have reached your maximum FREEGIFTS limit for this month");
                builder.setCancelable(false);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        finish();
                    }
                });
                builder.setIcon(getResources().getDrawable(R.drawable.ic_lightbulb_off));
                builder.show();

            } else if (price.equals("FREE") && db.freeOfferAvailable(dateSplit[1])) {

                callInstamojoPay(email, phone, "50", title + "-" + orderid, username);
            } else {

                callInstamojoPay(email, phone, price, title + "-" + orderid, username);
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    class OrderUpload extends AsyncTask<String, String, String> {
        @Override
        protected void onPostExecute(String jsonRead) { super.onPostExecute(jsonRead); }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {

            try {

                String data = URLEncoder.encode("username", "UTF-8")
                        + "=" + URLEncoder.encode(username, "UTF-8");
                data += "&" + URLEncoder.encode("orderid", "UTF-8")
                        + "=" + URLEncoder.encode(orderid, "UTF-8");
                data += "&" + URLEncoder.encode("pid", "UTF-8")
                        + "=" + URLEncoder.encode(""+prod_id, "UTF-8");
                data += "&" + URLEncoder.encode("ad", "UTF-8")
                        + "=" + URLEncoder.encode(ad, "UTF-8");
                data += "&" + URLEncoder.encode("price", "UTF-8")
                        + "=" + URLEncoder.encode(price, "UTF-8");
                data += "&" + URLEncoder.encode("giftwrap", "UTF-8")
                        + "=" + URLEncoder.encode(giftwrap, "UTF-8");
                data += "&" + URLEncoder.encode("phone", "UTF-8")
                        + "=" + URLEncoder.encode(phone, "UTF-8");
                data += "&" + URLEncoder.encode("quantity", "UTF-8")
                        + "=" + URLEncoder.encode(quantity, "UTF-8");
                data += "&" + URLEncoder.encode("delivery", "UTF-8")
                        + "=" + URLEncoder.encode(delivery_f, "UTF-8");

                URL url = new URL("https://www.fpelabs.com/Android_App/FreeGifts/new_order.php");
                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Accept-Encoding", "identity");
                urlConnection.setDoOutput(true);

                OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
                writer.write(data);
                writer.flush();
                writer.close();

                // Get the server response
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                StringBuilder sb = new StringBuilder();
                String line;
                // Read Server Response
                while ((line = reader.readLine()) != null) {
                    // Append server response in string
                    sb.append(line).append("\n");
                }
                reader.close();
                return sb.toString();

            } catch (Exception e) {
                out.println(e.getMessage());
            }
            return "{status:failed}";
        }
    }
}
