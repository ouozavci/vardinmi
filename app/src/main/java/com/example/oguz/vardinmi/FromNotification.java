package com.example.oguz.vardinmi;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FromNotification extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_from_notification);

        SharedPreferences preferences = getApplicationContext().getSharedPreferences("userPref", Context.MODE_PRIVATE);
        String uid = preferences.getString("uid",null);

        if(uid==null){
            finish();
            startActivity(new Intent(this,MainActivity.class));
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(uid);



        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String reqId = dataSnapshot.child("request").getValue().toString();
                Toast.makeText(getApplicationContext(),reqId,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        ref.child("request").setValue("none");

        startActivity(new Intent(this,MainActivity.class));

    }
}
