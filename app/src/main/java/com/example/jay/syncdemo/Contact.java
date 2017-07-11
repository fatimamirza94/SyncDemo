package com.example.jay.syncdemo;

/**
 * Created by Jay on 06-06-2017.
 */

public class Contact {

    private String Name;
    private String Email;
    private int Sync_Status;

    //  Constucting Constructor and setting values by calling setters

    Contact(String Name,String Email,int Sync_Status){
        this.setEmail(Email);
        this.setName(Name);
        this.setSync_Status(Sync_Status);
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public int getSync_Status() {
        return Sync_Status;
    }

    public void setSync_Status(int sync_Status) {
        Sync_Status = sync_Status;
    }

}
