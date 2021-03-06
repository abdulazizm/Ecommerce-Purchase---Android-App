package com.FingerPointEngg.Labs.FreeGifts;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class ProductList extends AppCompatActivity {

    String category;
    private GridView gridView;
    static public ProductListAdapter gridAdapter;
    static ArrayList imageItems;
    ProgressDialog progressDialog;

    ArrayList pid = new ArrayList();
    ArrayList title = new ArrayList();
    ArrayList price = new ArrayList();
    ArrayList description = new ArrayList();
    ArrayList imageurl = new ArrayList();

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);
        Bundle bu = getIntent().getExtras();
        category = bu.getString("category");

        progressDialog = ProgressDialog.show(ProductList.this, "Listing Gifts", null, true, true);
        new ProductsData().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        gridView = findViewById(R.id.product_grid);
        gridAdapter = new ProductListAdapter(getApplicationContext(), R.layout.grid_product_item, getData());
        gridView.setAdapter(gridAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                //Toast.makeText(getApplicationContext(),"Product. ID: "+pid.get((int)id),Toast.LENGTH_SHORT).show();
                Bundle data = new Bundle();

                data.putString("description", "" + description.get((int) id));
                data.putString("pid", "" + pid.get((int) id));
                data.putString("title", "" + title.get((int) id));
                data.putString("price", "" + price.get((int) id));
                data.putString("imageurl", "" + imageurl.get((int) id));

                Intent product = new Intent(getApplicationContext(), ProductPage.class);
                product.putExtras(data);
                startActivity(product);

            }
        });

    }

    /**
     * Prepare some dummy data for gridview
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ArrayList<ProductListItem> getData() {

        imageItems = new ArrayList<>();
        for (int i = 0; i < pid.size(); i++) {

            imageItems.add(new ProductListItem(imageurl.get(i).toString(), title.get(i).toString(), Integer.parseInt(pid.get(i).toString()),price.get(i).toString()));

        }

        return imageItems;
    }

    class ProductsData extends AsyncTask<String, String, String> {
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        protected void onPostExecute(String jsonRead) {

            super.onPostExecute(jsonRead);

            int products;
            String status = null;
            try {
                JSONObject json = new JSONObject(jsonRead);
                products = Integer.parseInt(json.getString("products"));
                status = json.getString("status");

                for (int i = 0; i < products; i++) {
                    pid.add(i, json.getString("pid" + i));
                    title.add(i, json.getString("title" + i));
                    price.add(i, json.getString("price" + i));
                    description.add(i, json.getString("description" + i));
                    imageurl.add(i, json.getString("imageurl" + i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (status != null && status.equals("success")) {

                ProductListAdapter gridViewAdapter = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    gridViewAdapter = new ProductListAdapter(ProductList.this, R.layout.grid_product_item, getData());
                }
                gridView.setAdapter(gridViewAdapter);

            } else {
                // Toast.makeText(RegisterActivity.this,"try again later", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(ProductList.this);
                builder.setTitle("Listing failed");
                builder.setMessage("Network Connectivity Error. Please try again later");
                builder.setCancelable(false);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.setIcon(getResources().getDrawable(R.drawable.ic_lightbulb_off));
                builder.show();
            }

            progressDialog.dismiss();
        }

        @Override
        protected String doInBackground(String... strings) {

            try {
                String data = URLEncoder.encode("category", "UTF-8")
                        + "=" + URLEncoder.encode(category, "UTF-8");

                URL url = new URL("https://www.fpelabs.com/Android_App/FreeGifts/product_list.php");
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
                //e.println(e.getMessage());
            }
            return "{\"status\":\"failed\"}";
        }
    }
}
