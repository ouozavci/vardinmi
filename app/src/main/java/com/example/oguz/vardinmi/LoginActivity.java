package com.example.oguz.vardinmi;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.oguz.vardinmi.jsonlib.Constants;
import com.example.oguz.vardinmi.jsonlib.JSONParser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    Button btnSignin;

    EditText txtPhone;
    EditText txtPass;
    Button btnLogin;

    SharedPreferences userPref;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userPref = getApplicationContext().getSharedPreferences("userPref", Context.MODE_PRIVATE);
        boolean isLogged = userPref.getBoolean("isLogged", false);
        if (isLogged) {
            Intent intentMain = new Intent(this, MainActivity.class);
            finish();
            startActivity(intentMain);
        } else {
            mAuth = FirebaseAuth.getInstance();

            txtPass = (EditText) findViewById(R.id.txtPass);
            txtPhone = (EditText) findViewById(R.id.txtPhone);

            btnLogin = (Button) findViewById(R.id.btnLogin);
            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String phone = txtPhone.getText().toString();
                    String pass = txtPass.getText().toString();

                    login(phone,pass);

                }
            });

            btnSignin = (Button) findViewById(R.id.btnSignin);
            btnSignin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intentSignin = new Intent(LoginActivity.this, SigninActivity.class);
                    startActivity(intentSignin);
                }
            });
        }

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    SharedPreferences.Editor editor = userPref.edit();
                    editor.putString("uid",user.getUid());
                    editor.putBoolean("isLogged",true);
                    editor.commit();

                    Log.d("Firebase", "onAuthStateChanged:signed_in:" + user.getUid());
                    startService(new Intent(LoginActivity.this, NotificationListener.class));
                    Intent intentMain = new Intent(LoginActivity.this,MainActivity.class);
                    finish();
                    startActivity(intentMain);
                } else {
                    // User is signed out
                    Log.d("Firebase", "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
    }

    public void login(String phone,String pass){
        mAuth.signInWithEmailAndPassword(phone+"@vardinmi.com", pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("Firebase", "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w("Firebase", "signInWithEmail:failed", task.getException());
                            Toast.makeText(LoginActivity.this,  task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

}
