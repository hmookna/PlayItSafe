package com.example.playitsafe.SafeZone;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Mook on 22/02/2016.
 */
public class SafezoneDBAdapter {
    private static final String DATABASE_NAME ="Safezone.db";
    private static final int DATABASE_VERSION = 1;


    private static final String TABLE_ROUTE = "tbl_route";
    public static final String ROUTE_ID = "route_id";
    public static final String ROUTE_NAME = "route_name";
    public static final String ROUTE_STATUS = "route_status";

    private static final String TABLE_NODE = "tbl_route_node";
    public static final String NODE_ID = "node_id";
    public static final String NODE_LAT = "node_latitude";
    public static final String NODE_LNG = "node_longtitude";
    public static final String NODE_NAME = "node_name";
    public static final String NODE_PREV = "node_prev";
    public static final String NODE_NEXT = "node_next";

    private SQLiteDatabase db;
    private final Context context;
    private SafezoneDBOpenHelper dbHelper;
    public SafezoneDBAdapter(Context context){
        this.context = context;
        dbHelper = new SafezoneDBOpenHelper(this.context , DATABASE_NAME , null , DATABASE_VERSION);
    }
    /***
     * Open connection to the database
     */
    public void open(){
        try{
            db = dbHelper.getWritableDatabase();
            dbHelper.onUpgrade(db, 1, 1);

        }catch (SQLiteException ex)
        {
            ex.printStackTrace();
        }
    }

    /***
     * Close connection to the database
     *
     */
    public void close(){
        db.close();
    }

    public Cursor listAllRoutes(){
        String query =
                " SELECT "+ "*" +
                        " FROM "+ TABLE_ROUTE;
        return db.rawQuery(query, null);
    }

    public String getRouteName(int routeId){
        String query =
                " SELECT "+ ROUTE_NAME +
                        " FROM "+ TABLE_ROUTE +
                        " WHERE "+ ROUTE_ID + " = " + routeId;
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToFirst()){
            return cursor.getString(
                    cursor.getColumnIndex(SafezoneDBAdapter.ROUTE_NAME)
            );
        }
        return "";
    }

    public Cursor getRouteInfoById(int routeId){
        String query =
                " SELECT "+ "*" +
                        " FROM "+ TABLE_NODE +
                        " WHERE "+ ROUTE_ID + " = " + routeId +
                        " ORDER BY " + NODE_ID;
        return db.rawQuery(query, null);
    }

    public RouteNode getStartNode(int routeId){
        String query =
                " SELECT "+ "*" +
                        " FROM "+ TABLE_NODE +
                        " WHERE "+ ROUTE_ID + " = " + routeId +
                        " AND "+ NODE_PREV + " IS NULL";
        return new RouteNode(db.rawQuery(query, null));
    }

    public RouteNode getNodeById(int nodeId){
        String query =
                " SELECT "+ "*" +
                        " FROM "+ TABLE_NODE +
                        " WHERE "+ NODE_ID + " = " + nodeId;
        return new RouteNode(db.rawQuery(query, null));
    }

    private static class SafezoneDBOpenHelper extends SQLiteOpenHelper{
        private static final String DROP_TABLE = "DROP TABLE IF EXISTS "+ SafezoneDBAdapter.TABLE_NODE;
        private static final String DROP_TABLE_ROUTE = "DROP TABLE IF EXISTS 'tbl_route'";

        public SafezoneDBOpenHelper(Context context , String name, SQLiteDatabase.CursorFactory factory, int version)
        {
            super(context,name,factory,version);
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(
                        "CREATE TABLE IF NOT EXISTS `tbl_route` (\n" +
                                "\t`route_id`\tINTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                                "\t`route_name`\tTEXT NOT NULL  UNIQUE,\n" +
                                "\t`route_status`\tTEXT DEFAULT 'INCOMPLETE'\n" +
                                ");\n" );
                db.execSQL(
                        "CREATE TABLE IF NOT EXISTS `tbl_route_node` (\n" +
                                "\t`node_id`\tINTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                                "\t`node_latitude`\tREAL NOT NULL,\n" +
                                "\t`node_longtitude`\tREAL NOT NULL,\n" +
                                "\t`node_prev`\tINTEGER,\n" +
                                "\t`node_next`\tINTEGER,\n" +
                                "\t`route_id`\tINTEGER,\n" +
                                "\t`node_name`\tTEXT NOT NULL UNIQUE,\n" +
                                "\tFOREIGN KEY(`route_id`) REFERENCES tbl_route\n" +
                                ");" );

                // tbl_route_node

                //route1
                db.execSQL(
                        "INSERT INTO `tbl_route_node` VALUES (1,13.7948551,100.3246769,NULL,'2',1,'Faculty of ICT');"
                );
                db.execSQL(
                        "INSERT INTO `tbl_route_node` VALUES (2,13.795339,100.325847,1,'3',1,'Faculty of Engineering');"
                );
                db.execSQL(
                        "INSERT INTO `tbl_route_node` VALUES (3,13.794330,100.325777,2,NULL,1,'Office of President');"
                );

                //route2

                db.execSQL(
                        "INSERT INTO `tbl_route_node` VALUES (4,13.797998,100.327884,NULL,5,2,'7 Place');"
                );
                db.execSQL(
                        "INSERT INTO `tbl_route_node` VALUES (5,13.798287,100.327428,4,6,2,'Seven Eleven');"
                );
                db.execSQL(
                        "INSERT INTO `tbl_route_node` VALUES (6,13.7973952,100.3282025,5,NULL,2,'Sang Chan');"
                );
                // tbl_route

                //route1
                db.execSQL(
                        "INSERT INTO `tbl_route` VALUES (1,'Mahidol','INCOMPLETE');"
                );
                //route2
                db.execSQL(
                        "INSERT INTO `tbl_route` VALUES (2,'Dormistry','INCOMPLETE');"
                );

                //route3

                db.execSQL(
                        "INSERT INTO `tbl_route_node` VALUES (7,13.748325,100.4757066,NULL,NULL,3,'Criminal zone');"
                );
                db.execSQL(
                        "INSERT INTO `tbl_route` VALUES (3,'Dev Team Homes','INCOMPLETE');"
                );

                //route4
                db.execSQL(
                        "INSERT INTO `tbl_route_node` VALUES (8,13.7468351,100.5327397,NULL,9,4,'Siam Paragon');"
                );
                db.execSQL(
                        "INSERT INTO `tbl_route_node` VALUES (9,13.7442856,100.5347454,8,10,4,'Siamkitti');"
                );
                db.execSQL(
                        "INSERT INTO `tbl_route_node` VALUES (10,13.7467925,100.5370031,9,NULL,4,'Central World');"
                );


                db.execSQL(
                        "INSERT INTO `tbl_route` VALUES (4,'Siam Square','INCOMPLETE');"
                );

            } catch (SQLException e) {
                Log.e("DEBUG","Create DB FAIL");
            }

        }

        public void init(){

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TODO auto-generated method stub
            try {
                db.execSQL(DROP_TABLE_ROUTE);
                db.execSQL(DROP_TABLE);
                onCreate(db);
                throw new SQLException("Fail");
            } catch (SQLException e) {
                Log.e("DEBUG","DROP DB FAIL ");
            }

        }
    }

}
