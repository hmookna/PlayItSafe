package com.example.playitsafe.Bodyguard;

/**
 * Created by aquaaquanam on 2/13/2016.
 */

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.playitsafe.R;

import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Bodyguard> bList = new ArrayList<>();
    private Uri uri;
    public ImageAdapter(Context context, ArrayList<Bodyguard> bList) {
        this.context = context;
        this.bList = bList;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {

        ImageView imageView;
        TextView textView;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView  = inflater.inflate(R.layout.bodyguard_grid, parent, false);
        }
        imageView = (ImageView) convertView.findViewById(R.id.imageView);

        // set image based on selected text
        textView = (TextView) convertView.findViewById(R.id.textView);

        textView.setText(bList.get(position).getbName());

        Uri temp = bList.get(position).getbPhoto();
        //Log.i("debug", "TEMP : " + temp);

        if(temp.getPath() != ""){
            Log.d("debug",""+ temp.getPath() );
            imageView.setImageURI(temp);


        }
        else{
            Log.i("debug", "No : ");
            imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.bodyguard_user));

        }

        notifyDataSetChanged();


        return convertView;
    }

    @Override
    public int getCount() {
        return bList.size();
    }

    @Override
    public Bodyguard getItem(int i) {
        return bList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public Bodyguard getBodyguard(int position){
        return bList.get(position);
    }

}

