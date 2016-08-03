package com.example.playitsafe.Guardian;
/**
 * Created by aquaaquanam on 2/13/2016.
 */

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.playitsafe.Bodyguard.Bodyguard;
import com.example.playitsafe.R;

import java.util.ArrayList;


public class ShowContAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Bodyguard> bList = new ArrayList<>();
    private Uri uri;
    private String type;
    public ShowContAdapter(Context context, ArrayList<Bodyguard> bList) {
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
        Log.i("debug", "TEMP : " + temp);
        if(temp.getPath() != ""){

            imageView.setImageURI(bList.get(position).getbPhoto());

        }
        else{
            Log.i("debug","No : ");
            imageView.setBackgroundResource(R.drawable.bodyguard_user);
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
