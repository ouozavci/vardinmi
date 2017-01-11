package com.example.oguz.vardinmi;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by oguz on 11.01.2017.
 */

public class WaitingFragment extends Fragment implements View.OnClickListener {

    FirebaseDatabase database;
    DatabaseReference ref;

    @Override
    public void onClick(View v) {
        int position = (Integer) v.getTag(R.id.key_position);
        if (v.getId() == R.id.btnSendNotification){
            String data = list_items.get(position).getPhoneNumber();
            double lat = Double.parseDouble(data.split("/")[1]);
            double lon = Double.parseDouble(data.split("/")[3]);
            Intent intentMap = new Intent(getActivity(),MapsActivity.class);
            intentMap.putExtra("lon",lon);
            intentMap.putExtra("lat",lat);
            startActivity(intentMap);
            Toast.makeText(getActivity().getApplicationContext(),"lon: "+lon+" lat:"+lat,Toast.LENGTH_LONG).show();
        }
    }
    ListView listview_contacts;
    public List<PersonInfo> list_items = new ArrayList<PersonInfo>();
    private ListViewAdapter listviewAdapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_contacts,container,false);

        final SharedPreferences preferences = getActivity().getSharedPreferences("userPref", Context.MODE_PRIVATE);
        String uid = preferences.getString("uid",null);

        if(uid==null) return null;


        database = FirebaseDatabase.getInstance();
        ref = database.getReference(uid);

        final Context c = getActivity().getApplicationContext();
        /*    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Map<String,String> td = (HashMap<String,String>) dataSnapshot.getValue();
                    td.remove("request");

                  for(Map.Entry<String,String> entry:td.entrySet()){
                      String name = preferences.getString(entry.getKey(),"Bilinmeyen");
                      list_items.add(new PersonInfo(name,entry.getValue(),false,entry.getKey()));
                  }
                    listviewAdapter = new ListViewAdapter(c,list_items,WaitingFragment.this);
                    listview_contacts = (ListView) view.findViewById(R.id.listView_contacts);
                    listview_contacts.setAdapter(listviewAdapter);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });*/

            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    list_items=new ArrayList<PersonInfo>();

                    Map<String,String> td = (HashMap<String,String>) dataSnapshot.getValue();
                    td.remove("request");

                    for(Map.Entry<String,String> entry:td.entrySet()){
                        String name = preferences.getString(entry.getKey(),"Bilinmeyen");
                        String value = entry.getValue();
                        if(value.equals("wait"))
                        list_items.add(new PersonInfo(name,entry.getValue(),false,entry.getKey()));
                        else if(value.startsWith("lat/")){
                            list_items.add(new PersonInfo(name, entry.getValue(), true, entry.getKey()));
                        }
                    }
                    listviewAdapter = new ListViewAdapter(c,list_items,WaitingFragment.this,"Haritada Görüntüle");
                    listview_contacts = (ListView) view.findViewById(R.id.listView_contacts);
                    listview_contacts.setAdapter(listviewAdapter);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        return view;
    }

}
