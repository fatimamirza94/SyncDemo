package com.example.jay.syncdemo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Jay on 06-06-2017.
 */

public class DBHelper extends SQLiteOpenHelper{

    private static DBHelper dbHelper = null;
    private Context context;
    private static final int DATABASE_VERSION = 1;

    private static final String  CREATE_TABLE = "create table "+DBContact.Table_Name+
            "(id integer primary key autoincrement,"+DBContact.Name+" text,"+DBContact.Email+" text,"
            +DBContact.Sync_Status+" integer);";

    private static final String  DROP_TABLE = "drop table if exists "+DBContact.Table_Name;

    private DBHelper(Context context)
    {
        super(context,DBContact.DBName,null,DATABASE_VERSION);
        this.context = context;
    }

    public static DBHelper getInstance(Context context) {
        /**
         * use the application context as suggested by CommonsWare.
         * this will ensure that you dont accidentally leak an Activitys
         * context (see this article for more information:
         * http://developer.android.com/resources/articles/avoiding-memory-leaks.html)
         */
        if (dbHelper == null) {
            dbHelper = new DBHelper(context);
        }
        return dbHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE);
        onCreate(db);
    }

    //  Save info for local Database(SQLite Database)
    public void SaveToLocalDatabase(String Name,String Email,int Sync_Status,SQLiteDatabase database){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBContact.Name,Name);
        contentValues.put(DBContact.Email,Email);
        contentValues.put(DBContact.Sync_Status,Sync_Status);
        //  Insert Data into SQlite Database
        database.insert(DBContact.Table_Name,null,contentValues);
    }

    // Read Info from SQLiteDatabse(SQLite Database)
    public Cursor ReadFromLocalDatabase(SQLiteDatabase database){

        //  Coloumn Names for reading data from
        String[] Projection = {DBContact.Name,DBContact.Email,DBContact.Sync_Status};
        return (database.query(DBContact.Table_Name,Projection,null,null,null,null,null));
    }

    //  Update SQLite Database(SQLite Database)
    public void UpdataLocalDatabase(String Name,String Email,int Sync_Status,SQLiteDatabase database){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBContact.Sync_Status, Sync_Status);
		//	Update SyncStatus Based on Name and Email
		String Selection = DBContact.Name + " = ? AND " + DBContact.Email + " = ?";
        String[] selection_args = {Name, Email};
        database.update(DBContact.Table_Name, contentValues, Selection, selection_args);
    }
}