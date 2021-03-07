package com.FingerPointEngg.Labs.FreeGifts;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SettingsFragment extends Fragment  {


    CardView card_profile,card_device,card_logout,card_control,card_help;
    TextView user_name,user_devices;
    LocalDb db;
    Bundle user_data;

    @SuppressLint({"InflateParams", "SetTextI18n"})
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);
        card_profile = v.findViewById(R.id.card_profile);
        card_device = v.findViewById(R.id.card_device);
        card_logout = v.findViewById(R.id.card_logout);
        card_control = v.findViewById(R.id.card_control);
        card_help = v.findViewById(R.id.card_help);
        user_name = v.findViewById(R.id.user_name);
        user_devices = v.findViewById(R.id.user_devices);

        db = new LocalDb(getContext());
        user_data = new Bundle();
        user_data = db.getUserDetails();
        user_name.setText(user_data.getString("username"));
        if(!user_data.getString("phone").equals("null"))
            user_devices.setText(user_data.getString("phone"));
        else
            user_devices.setText("Click here !!");
        card_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent logout = new Intent(getContext(), AddUserDeliveryData.class);
                startActivity(logout);
            }
        });

        card_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent logout = new Intent(getContext(), AddUserDeliveryData.class);
                startActivity(logout);
            }
        });

        card_control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cs = new Intent(getContext(), ComingSoon.class);
                startActivity(cs);
            }
        });

        card_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cs = new Intent(getContext(), ComingSoon.class);
                startActivity(cs);
            }
        });


        card_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                //builder.setCancelable(true);
                builder.setTitle("Logout");
                builder.setMessage("You can't purchase gifts until you login again. Are you sure to logout and purchase later? ");
                builder.setNegativeButton("I will purchase later", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.logout();
                        dialog.cancel();
                        Intent logout = new Intent(getContext(), SplashScreen.class);
                        startActivity(logout);
                    }
                });
                builder.setPositiveButton("Keep purchasing",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.setIcon(getResources().getDrawable(R.drawable.ic_card_giftcard_black_24dp ));
                builder.show();

            }
        });

        return v;
    }

}
