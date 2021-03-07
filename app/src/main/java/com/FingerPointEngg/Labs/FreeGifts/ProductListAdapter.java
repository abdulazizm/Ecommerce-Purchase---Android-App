package com.FingerPointEngg.Labs.FreeGifts;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProductListAdapter extends ArrayAdapter<ProductListItem> {

    private Context context;
    private int layoutResourceId;
    private ArrayList<ProductListItem> data = new ArrayList<ProductListItem>();

    public ProductListAdapter(Context context, int layoutResourceId, ArrayList<ProductListItem> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.imageTitle = (TextView) row.findViewById(R.id.text);
            holder.imagePrice = (TextView) row.findViewById(R.id.price);
            holder.image = (ImageView) row.findViewById(R.id.image);
            holder.gift = (ImageView) row.findViewById(R.id.gift);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        ProductListItem item = data.get(position);
        holder.imageTitle.setText(item.getTitle());
        //Toast.makeText(context,item.getPrice(),Toast.LENGTH_SHORT).show();

        if(item.getPrice().equals("FREE")){
            holder.imagePrice.setText(item.getPrice());
        } else {
            holder.imagePrice.setText("₹" + item.getPrice());
            holder.gift.setVisibility(View.INVISIBLE);
        }
        Picasso.with(context).load(item.getImageUrl()).resize(300,300).into(holder.image);
        return row;
    }

    static class ViewHolder {
        TextView imageTitle,imagePrice;
        ImageView image,gift;
    }
}