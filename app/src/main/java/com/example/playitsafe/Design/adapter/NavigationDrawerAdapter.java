package com.example.playitsafe.Design.adapter;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.playitsafe.DBconnect.SessionManager;
import com.example.playitsafe.Design.model.NavDrawerItem;
import com.example.playitsafe.R;
import com.example.playitsafe.SOS.GcmIntentService;

import java.util.Collections;
import java.util.List;
/**
 * Created by Mook on 12/03/2016.
 * As the RecyclerView is customized, we need an adapter class to render the custom xml layout.
 * So under adapter package, create a class named NavigationDrawerAdapter.java and paste the below code.
 * This adapter class inflates nav_drawer_row.xml and renders the RecycleView drawer menu.
 */
public class NavigationDrawerAdapter extends RecyclerView.Adapter<NavigationDrawerAdapter.MyViewHolder> {
    List<NavDrawerItem> data = Collections.emptyList();
    private LayoutInflater inflater;
    private Context context;
    SessionManager session;
    int[] ic;

    public NavigationDrawerAdapter(Context context, List<NavDrawerItem> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;

    }

    public void delete(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.nav_drawer_row, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        ic = new int[]{R.drawable.ic_home, R.drawable.ic_people, R.drawable.ic_msg, R.drawable.ic_guide, R.drawable.ic_logout};
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        NavDrawerItem current = data.get(position);
        holder.title.setText(current.getTitle());
        holder.icon.setImageResource(ic[position]);
        session = new SessionManager(context);
        int msg = session.getMsgID();
        if(position==2 && msg!=0){
            holder.count.setText(msg+"");
            holder.count.setVisibility(View.VISIBLE);
        }else{
            holder.count.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView count;
        ImageView icon;
        public MyViewHolder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.icon);
            title = (TextView) itemView.findViewById(R.id.title);
            count = (TextView) itemView.findViewById(R.id.count);
        }
    }
}
