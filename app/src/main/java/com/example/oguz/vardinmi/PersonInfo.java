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

    /**
     * Created by oguz on 11.01.2017.
     */

    public static class WaitingFragment extends Fragment implements View.OnClickListener {
        @Override
        public void onClick(View v) {

        }

        ListView listview_contacts;
        public List<PersonInfo> list_items = new ArrayList<PersonInfo>();
        private ListViewAdapter listviewAdapter;
        private ProgressDialog progressDialog;

        FirebaseDatabase database;
        DatabaseReference reference;
        DatabaseReference referenceMine;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_contacts,container,false);

            list_items.add(new PersonInfo("oguz baba","0543787874",true,"asdasdasdas"));
            listviewAdapter = new ListViewAdapter(getActivity().getApplicationContext(),list_items,this);


            return view;
        }
    }
}