package com.example.jay.syncdemo;

/**
 * Created by Jay on 06-06-2017.
 */

public class DBContact {

    public static final int Sync_Status_OK = 0;
    public static final int Sync_Status_Failed = 1;

    public static final String SERVER_URL = "http://192.168.56.1//SyncData//Sync.php";
    public static final String UI_UPDATE_BROADCAST = "com.example.jay.syncdemo.ui_update_broadcast";

    public static final String DBName = "SyncDB";
    public static final String Table_Name = "Data";
    public static final String Name = "Name";
    public static final String Email = "Email";
    public static final String Sync_Status = "Sync_Status";

}
