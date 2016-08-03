package com.example.playitsafe.SafeZone;

import android.support.v4.app.NavUtils;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.playitsafe.Design.FragmentDrawer;
import com.example.playitsafe.R;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Mook on 29/01/2016.
 */
public class SafeZoneActivity extends AppCompatActivity {

    final static String ROUTE_NODE_INTENT = "route_node_intent";
    final static String ROUTE_ID_INTENT = "route_id_intent";
    final static String NODE_ID_INTENT = "node_id_intent";
    final static String NODE_NUMBER_INTENT = "node_no_intent";
    private Toolbar toolbar;
    private FragmentDrawer drawerFragment;
    ListView routeListView;
    ArrayList<RouteItem> routeItems;
    RouteListAdapter adapter;
    Cursor cursor;
    private SafezoneDBAdapter dbAdapter = new SafezoneDBAdapter(this);
    AlertDialog.Builder proceedDialog;
    int lastRouteClicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safezone);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        // enabling toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


//        this.deleteDatabase("MUVisitor.db");

        //Init DB
        dbAdapter.open();

        routeListView = (ListView) findViewById(R.id.routeListView);

        routeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)  {
                int routeId = adapter.routeItemList.get(position).getId();
                toMapActivity(routeId);
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        adapter = new RouteListAdapter(this, R.layout.route_item_layout, dbAdapter);
        routeListView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the BodyguardSplash/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    //intent to MapActivity
    private void toMapActivity(int routeId){
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra(ROUTE_ID_INTENT, Integer.toString(routeId));
        startActivity(intent);
    }
}

