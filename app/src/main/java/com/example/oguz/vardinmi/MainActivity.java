package com.example.oguz.vardinmi;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    SharedPreferences userPref;
    String uid;

    TextView txtUid;
    Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtUid = (TextView) findViewById(R.id.txtUid);
        btnLogout = (Button) findViewById(R.id.btnLogout);

        userPref = getApplicationContext().getSharedPreferences("userPref", Context.MODE_PRIVATE);

        Boolean isLogged = userPref.getBoolean("isLogged",false);

        if(!isLogged) {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            finish();
            startActivity(loginIntent);
        }
        else{
            uid = userPref.getString("uid","ERROR!");
            txtUid.setText(uid);
        }

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor edit = userPref.edit();
                edit.putString("uid",null);
                edit.putBoolean("isLogged",false);
                edit.commit();
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(getIntent());
            }
        });
    }
}
