package com.example.oguz.vardinmi;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class PersonInfo implements Comparable<PersonInfo>{

    private String name;
    private String phoneNumber;
    private boolean isUsing;
    private String uid;

    public String getName(){
        return this.name;
    }

    @Override
    public int compareTo(PersonInfo another) {
        return this.name.compareTo(another.name);
    }
    public boolean isUsing(){
        return this.isUsing;
    }
    public String getPhoneNumber(){
        return this.phoneNumber;
    }
    public String getUid(){
        return this.uid;
    }
    public PersonInfo(String name, String phoneNumber,boolean isUsing,String uid){
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.isUsing = isUsing;
        this.uid = uid;
    }
}