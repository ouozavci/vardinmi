package com.example.oguz.vardinmi;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FromNotification extends AppCompatActivity {

    TextView tvInfo;
    Button btnAccept;
    Button btnDecline;

    GPSTracker mGPS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_from_notification);

        SharedPreferences preferences = getApplicationContext().getSharedPreferences("userPref", Context.MODE_PRIVATE);
        final String uid = preferences.getString("uid",null);

        if(uid==null){
            finish();
            startActivity(new Intent(this,MainActivity.class));
        }

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference ref = database.getReference(uid);

        btnAccept = (Button) findViewById(R.id.btnAccept);
        btnDecline = (Button) findViewById(R.id.btnDecline);

        final List<String> reqList = new ArrayList<String>();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String reqId = dataSnapshot.child("request").getValue().toString();
                Toast.makeText(getApplicationContext(),reqId,Toast.LENGTH_SHORT).show();
                reqList.add(reqId);
                final DatabaseReference refReq = database.getReference(reqId);

                btnAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mGPS = new GPSTracker(getApplicationContext());

                        double lon = 0;
                        double lat = 0;
                        if(mGPS.canGetLocation){
                            while(lon==0.0||lat==0.0) {
                                mGPS.getLocation();
                                lon = mGPS.getLongtitude();
                                lat = mGPS.getLatitude();
                            }
                        }
                        else {
                            Toast.makeText(getApplicationContext(),"Couldn't get GPS",Toast.LENGTH_LONG).show();
                        }
                        refReq.child(uid).setValue("lat/"+lat+"/lon/"+lon);
                        startActivity(new Intent(FromNotification.this,MainActivity.class));
                    }
                });

                btnDecline.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        refReq.child(uid).setValue("NOOOOOOOO");
                        startActivity(new Intent(FromNotification.this,MainActivity.class));
                    }
                });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });





        ref.child("request").setValue("none");
    }
}
