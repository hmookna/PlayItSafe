package com.example.playitsafe.Bodyguard;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class DatabaseManager {
    private static final String DATABASE_NAME = "contactsBodyguard";
    private static final String TABLE_CONTACTS = "Bodyguard";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_PHONE = "phone_number";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PHOTO = "photo";
    private static final String KEY_ADD = "addFrom";
    DatabaseHandler databaseHandler;
    Context context;
    SQLiteDatabase db;

    public DatabaseManager(Context context) {
        this.context = context;
        databaseHandler = new DatabaseHandler(context);
        db = databaseHandler.getWritableDatabase();
        Cursor cursor = getAllData();  //cursor hold all your data
    }

    public void open(){
        try{
            db = databaseHandler.getWritableDatabase();
          //  databaseHandler.onUpgrade(db, 1, 1);
        }catch (SQLiteException ex){
            ex.printStackTrace();
        }
    }

    public void deleteDB(){
        context.deleteDatabase(DATABASE_NAME);
        Log.i("debug", "Deleted");
    }

    public void close(){
        db.close();
    }

    public long insertBodyguard(int id, String name, String phone,String email,String photo,String addFrom){

        ContentValues newTaskValues = new ContentValues();
        newTaskValues.put(KEY_ID, id);
        newTaskValues.put(KEY_NAME, name);
        newTaskValues.put(KEY_PHONE, phone);
        newTaskValues.put(KEY_EMAIL, email);
        newTaskValues.put(KEY_PHOTO, photo);
        newTaskValues.put(KEY_ADD, addFrom);
        Log.i("debug", "DatabaseAdapter: inserted into User xx");
        return db.insert("Bodyguard", null, newTaskValues);

    }
    public int getLastKId(){
        String query = "SELECT id from Bodyguard order by id DESC limit 1";
        open();
        Cursor c = db.rawQuery(query, null);
        int lastId = 0;
        if (c != null && c.moveToFirst()) {
            lastId = c.getInt(0); //The 0 is the column index, we only have 1 column, so the index is 0
        }
        return lastId;
    }

    public int getLastId() {
        Cursor c = db.rawQuery("SELECT max(id) FROM " + TABLE_CONTACTS, null);
        return c.getInt(c.getColumnIndex(KEY_ID));
    }

    public int checkNo(String no){
        open();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_CONTACTS + " WHERE phone_number = '" + no + "'", null);
        return c.getCount();
    }
    public int getRowNo(){
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_CONTACTS, null);

        return c.getCount();
    }


    public void showColumns(){
        Cursor dbCursor = db.query(TABLE_CONTACTS, null, null, null, null, null, null);
        String[] columnNames = dbCursor.getColumnNames();
        for(int i=0; i<columnNames.length; i++){
            Log.i("debug", "Column : " + columnNames[i]);
        }
    }

    public Cursor selectAllBodyguard(){
        open();
        return db.rawQuery("SELECT * From Bodyguard", null);
    }

    public Cursor selectAppBodyguard(){
        open();
        return db.rawQuery("SELECT * From Bodyguard WHERE addFrom = 'App'", null);
    }

    public void deleteBodyguard(String id){
        String deleteBodyguard = "DELETE FROM Reference WHERE PID = " + id;
        db.execSQL(deleteBodyguard);
    }

    public void excSQL(int SQL){
        db.delete("Bodyguard", "id = " + SQL, null);
//        db.rawQuery(SQL, null);
    }
    public Cursor getAllData() {
        String selectQuery = "Select * from "+TABLE_CONTACTS;
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor;
    }
    public JSONArray getResults()
    {

        String myTable = TABLE_CONTACTS;//Set name of your table
        String searchQuery = "SELECT  * FROM " + myTable;
        Cursor cursor = db.rawQuery(searchQuery, null );

        JSONArray resultSet     = new JSONArray();

        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {

            int totalColumn = cursor.getColumnCount();
            JSONObject rowObject = new JSONObject();

            for( int i=0 ;  i< totalColumn ; i++ )
            {
                if( cursor.getColumnName(i) != null )
                {
                    try
                    {
                        if( cursor.getString(i) != null )
                        {
                            Log.d("TAG_NAME", cursor.getString(i) );
                            rowObject.put(cursor.getColumnName(i) ,  cursor.getString(i) );
                        }
                        else
                        {
                            rowObject.put( cursor.getColumnName(i) ,  "" );
                        }
                    }
                    catch( Exception e )
                    {
                        Log.d("TAG_NAME", e.getMessage()  );
                    }
                }
            }
            resultSet.put(rowObject);
            cursor.moveToNext();
        }
        cursor.close();
        Log.d("TAG_NAME", resultSet.toString() );
        return resultSet;
    }
    public String getLastRecord(){
        String myTable = TABLE_CONTACTS;//Set name of your table
        String searchQuery = "SELECT  * FROM " + myTable;
        Cursor cursor = db.rawQuery(searchQuery, null );
        cursor.moveToLast();
        String result = "";
        int totalColumn = cursor.getColumnCount();

        for( int i=0 ;  i< totalColumn ; i++ ) {
            if (cursor.getColumnName(i) != null) {
                try {
                    if (cursor.getString(i) != null) {
                        Log.d("TAG_NAME", cursor.getString(i));
                        result = result + "##" + cursor.getString(i);
                    }
                    else{
                        result = result + "# #";
                    }
                } catch (Exception e) {
                    Log.d("TAG_NAME", e.getMessage());
                }
            }
        }
        cursor.close();
        Log.d("TAG_NAME", result );
        return result;
    }
    public void deleteAll()
    {
        open();
        db.execSQL("delete from " + TABLE_CONTACTS);
        db.close();
    }

    public ArrayList<String> getPhoneNumberBodyguards(){
        ArrayList<String> phoneList = new ArrayList<String>();
        String query = "SELECT " + KEY_PHONE+ " FROM " + TABLE_CONTACTS + " WHERE " + KEY_ADD+" = 'Phone book'";
        Cursor cursor = db.rawQuery(query, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                phoneList.add(cursor.getString(cursor.getColumnIndex(KEY_PHONE)));

            } while (cursor.moveToNext());
        }
        return phoneList;
    }
}
