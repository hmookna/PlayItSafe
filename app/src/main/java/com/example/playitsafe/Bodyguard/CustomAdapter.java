package com.example.playitsafe.Bodyguard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.playitsafe.R;

/**
 * Created by Alice on 2/24/2016.
 */

public class CustomAdapter extends BaseAdapter {
    Context mContext;
    String[] strName;
    String[] email;
    String[] mobile;
    String[] img;
    int[] resId;

    public CustomAdapter(Context context, String[] strName, String[] mobile, String[] email){
        this.mContext= context;
        this.strName = strName;
        this.email = email;
        this.mobile = mobile;

    }
    public String getEmail(int position){
        return email[position];
    }
    public String getName(int position){
        return strName[position];
    }
    public String getMobile(int position){
        return mobile[position];
    }
    public String getImg(int position) { return img[position];}


    public int getCount() {
        return strName.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater mInflater =
                (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(view == null)
            view = mInflater.inflate(R.layout.bodyguard_app_user, parent, false);

        TextView textView = (TextView)view.findViewById(R.id.textView1);
        textView.setText(strName[position]);

        ImageView imageView = (ImageView)view.findViewById(R.id.imageView1);
        imageView.setBackgroundResource(R.drawable.bodyguard_user);

        return view;
    }
}
