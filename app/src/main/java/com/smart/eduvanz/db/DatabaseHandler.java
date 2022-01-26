package com.smart.eduvanz.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "edManager";
    private static final String TABLE_CONTACTS = "eduser";
    private static final String KEY_ID = "id";
    private static final String KEY_ACCID = "accId";
    private static final String KEY_MOBILE = "mobile";
    private static final String KEY_ISFACEENABLED = "isFaceEnabled";
    private static final String KEY_ISFINGERENABLED = "isFingerEnabled";
    private static final String KEY_ISMPINENABLED = "isMPinEnabled";
    private static final String KEY_LOGINDATE = "loginDate";
    private static final String KEY_LOGINSTATUS = "loginStatus";
    private static final String KEY_LOGOUTDATE = "logoutDate";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //3rd argument to be passed is CursorFactory instance
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_ACCID + " TEXT,"
                + KEY_MOBILE + " TEXT,"
                + KEY_ISFACEENABLED + " INTEGER,"
                + KEY_ISFINGERENABLED + " INTEGER,"
                + KEY_ISMPINENABLED + " INTEGER,"
                + KEY_LOGINDATE + " TEXT,"
                + KEY_LOGOUTDATE + " TEXT,"
                + KEY_LOGINSTATUS + " INTEGER" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);

        // Create tables again
        onCreate(db);
    }

    // code to add the new contact
    public  void addEdUser(Customer contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ACCID, contact.getAccId()); // Contact Name
        values.put(KEY_MOBILE, contact.getMobile()); // Contact Name
       // values.put(KEY_PH_NO, contact.getPhoneNumber()); // Contact Phone

        // Inserting Row
        db.insert(TABLE_CONTACTS, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    // code to get the single contact
    public Customer getEdUser() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CONTACTS, new String[] { KEY_ID,
                KEY_ACCID,KEY_MOBILE,KEY_ISFACEENABLED,KEY_ISFINGERENABLED,KEY_ISMPINENABLED,KEY_LOGINDATE,KEY_LOGOUTDATE,KEY_LOGINSTATUS }, null,null , null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Customer objCust = new Customer();
        objCust.setAccId(cursor.getString(1));
        objCust.setMobile(cursor.getString(2));
        objCust.setIsFaceEnabled(cursor.getInt(3));
        objCust.setIsMPinEnabled(cursor.getInt(5));
        //Customer contact = new Customer(Integer.parseInt(cursor.getString(0)),cursor.getString(1),
        //        Integer.parseInt(cursor.getString(2)), Integer.parseInt(cursor.getString(3)),Integer.parseInt(cursor.getString(4)),cursor.getString(5),cursor.getString(6),Integer.parseInt(cursor.getString(7)));
        // return contact
        return objCust;
    }
    // code to get the single contact
    public int getEdUserId() {
        SQLiteDatabase db = this.getReadableDatabase();
        int id = 0;
        Cursor cursor = db.query(TABLE_CONTACTS, new String[] { KEY_ID,
                KEY_ACCID,KEY_ISFACEENABLED,KEY_ISFINGERENABLED,KEY_ISMPINENABLED,KEY_LOGINDATE,KEY_LOGOUTDATE,KEY_LOGINSTATUS }, null,null , null, null, null, null);
        if (cursor != null){
            cursor.moveToFirst();
            id = cursor.getInt(0);
            cursor.close();
        }


        //Customer contact = new Customer(Integer.parseInt(cursor.getString(0)),cursor.getString(1),
        //        Integer.parseInt(cursor.getString(2)), Integer.parseInt(cursor.getString(3)),Integer.parseInt(cursor.getString(4)),cursor.getString(5),cursor.getString(6),Integer.parseInt(cursor.getString(7)));
        // return contact
        return id;
    }
    // code to update the single contact
    public int updateContact(Customer cus) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ISFACEENABLED, cus.getAccId());
       // values.put(KEY_PH_NO, contact.getPhoneNumber());

        // updating row
        return db.update(TABLE_CONTACTS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(cus.getAccId()) });
    }
    public int updateBiometric(Customer cus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ISFACEENABLED, cus.getIsFaceEnabled());
        values.put(KEY_ACCID,cus.getAccId());
        // values.put(KEY_PH_NO, contact.getPhoneNumber());
        int id = this.getEdUserId();
        // updating row
        return db.update(TABLE_CONTACTS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(id) });
    }
    public int updateMobile(Customer cus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_MOBILE, cus.getMobile());
        values.put(KEY_ACCID,cus.getAccId());
        // values.put(KEY_PH_NO, contact.getPhoneNumber());
        int id = this.getEdUserId();
        // updating row
        return db.update(TABLE_CONTACTS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(id) });
    }
    public int updateMpin(Customer cus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ISMPINENABLED, cus.getIsMPinEnabled());
        values.put(KEY_ACCID,cus.getAccId());
        // values.put(KEY_PH_NO, contact.getPhoneNumber());
        int id = this.getEdUserId();
        // updating row
        return db.update(TABLE_CONTACTS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(id) });
    }
    // Deleting single contact
    public void deleteContact(Customer cus) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACTS, KEY_ID + " = ?",
                new String[] { String.valueOf(cus.getAccId()) });
        db.close();
    }

    // Getting contacts Count
    public int getEduserCount() {
        String countQuery = "SELECT  * FROM " + TABLE_CONTACTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int row = cursor.getCount();
        cursor.close();

        // return count
        return row;
    }

}
