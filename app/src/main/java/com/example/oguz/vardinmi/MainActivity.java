package com.example.oguz.vardinmi;

import android.app.ActivityManager;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    SharedPreferences userPref;
    String uid;

    Button btnLogout;
    Button btnContacts;

    private static final int READ_CONTACTS_PERMISSIONS_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLogout = (Button) findViewById(R.id.btnLogout);
        btnContacts = (Button) findViewById(R.id.btnContacts);

        userPref = getApplicationContext().getSharedPreferences("userPref", Context.MODE_PRIVATE);

        Boolean isLogged = userPref.getBoolean("isLogged", false);

        if (!isLogged) {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            finish();
            startActivity(loginIntent);
        }

        uid = userPref.getString("uid", "ERROR!");

        if(!isMyServiceRunning(NotificationListener.class)){
            startService(new Intent(MainActivity.this, NotificationListener.class));
        }

        btnContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED)
                        try {
                            ContactsFragment fr = new ContactsFragment();
                            MainActivity.this.getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.fragment_container, fr).addToBackStack("fragment").commit();
                        } catch (Exception e) {
                            Snackbar.make(v, "Error!", Snackbar.LENGTH_SHORT).show();
                        }
                    else {
                        getPermissionToReadUserContacts();
                    }
                }
                else{
                        try {
                            ContactsFragment fr = new ContactsFragment();
                            MainActivity.this.getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.fragment_container, fr).addToBackStack("fragment").commit();
                        } catch (Exception e) {
                            Snackbar.make(v, "Error!", Snackbar.LENGTH_SHORT).show();
                        }
                }
            }
        });
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor edit = userPref.edit();
                edit.putString("uid", null);
                edit.putBoolean("isLogged", false);
                edit.commit();
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(getIntent());
            }
        });

        Button btnWait = (Button) findViewById(R.id.btnWait);
        btnWait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WaitingFragment fr = new WaitingFragment();
                MainActivity.this.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container,fr).addToBackStack("fragment").commit();
            }
        });

    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void getPermissionToReadUserContacts() {

        if (Build.VERSION.SDK_INT >= 23) {
            // 1) Use the support library version ContextCompat.checkSelfPermission(...) to avoid
            // checking the build version since Context.checkSelfPermission(...) is only available
            // in Marshmallow
            // 2) Always check for permission (even if permission has already been granted)
            // since the user can revoke permissions at any time through Settings
            if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED) {

                // The permission is NOT already granted.
                // Check if the user has been asked about this permission already and denied
                // it. If so, we want to give more explanation about why the permission is needed.
                if (shouldShowRequestPermissionRationale(
                        android.Manifest.permission.READ_CONTACTS)) {
                    // Show our own UI to explain to the user why we need to read the contacts
                    // before actually requesting the permission and showing the default UI
                }

                // Fire off an async request to actually get the permission
                // This will show the standard permission request dialog UI
                requestPermissions(new String[]{android.Manifest.permission.READ_CONTACTS},
                        READ_CONTACTS_PERMISSIONS_REQUEST);
            }
        }
    }
}
