package com.FingerPointEngg.Labs.FreeGifts;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddUserDeliveryData extends AppCompatActivity {

    Button submit_button;
    //CheckBox defaultval;
    EditText email,phone,name,door,street,landmark,city,pincode,state,pre_address;
    LocalDb db;
    String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_data);

        db = new LocalDb(getApplicationContext());

        submit_button = findViewById(R.id.submit_reset_device);
        name = findViewById(R.id.r_name);
        door = findViewById(R.id.door);
        street = findViewById(R.id.street);
        landmark = findViewById(R.id.landmark);
        city = findViewById(R.id.city);
        state = findViewById(R.id.state);
        pincode = findViewById(R.id.pincode);
        pre_address = findViewById(R.id.pre_address);

        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);

        Bundle user_data= new Bundle();
        user_data = db.getUserDetails();

        if(!user_data.getString("email").equals("null")) {
            email.setText(user_data.getString("email"));
            phone.setText(user_data.getString("phone"));
            pre_address.setText(user_data.getString("address"));
        }else {
            pre_address.setVisibility(View.INVISIBLE);
        }

        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!email.getText().toString().trim().isEmpty()&&!phone.getText().toString().trim().isEmpty()){
                    address = name.getText().toString().trim()+","+door.getText().toString().trim()+","+street.getText().toString().trim()+","+landmark.getText().toString().trim()+","+city.getText().toString().trim()+","+state.getText().toString().trim()+"-"+pincode.getText().toString().trim();
                    db.setDeliveryDetails(address,email.getText().toString().trim(),phone.getText().toString().trim());
                    Toast.makeText(getApplicationContext(),"Address Updated:"+address,Toast.LENGTH_SHORT).show();
                    finish();
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddUserDeliveryData.this);
                    builder.setCancelable(false);
                    builder.setTitle("Details");
                    builder.setMessage("Please enter complete details");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.setIcon(getResources().getDrawable(R.drawable.ic_add));
                    builder.show();
                }
            }
        });
    }
}
