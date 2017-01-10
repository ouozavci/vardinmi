package com.example.oguz.vardinmi;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SigninActivity extends AppCompatActivity {

    EditText txtPhone;
    EditText txtPass;
    EditText txtPassConf;
    Button btnSignin;

    SharedPreferences preferences;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        preferences = getApplicationContext().getSharedPreferences("userPref", Context.MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();

        //Bounding views with objects
        txtPhone = (EditText) findViewById(R.id.txtPhoneSignin);
        txtPass = (EditText) findViewById(R.id.txtPassSignin);
        txtPassConf = (EditText) findViewById(R.id.txtPassConfirm);
        btnSignin = (Button) findViewById(R.id.btnSignin2);

        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = txtPhone.getText().toString();
                String pass = txtPass.getText().toString();
                String passConf = txtPassConf.getText().toString();

                //Tüm alanlar dolumu kontrol et
                if(phone.isEmpty() || pass.isEmpty() || passConf.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Tüm bilgileri eksiksiz doldurun!",Toast.LENGTH_SHORT).show();
                }//Şifreler uyuşuyormu kontrol et
                else if(!pass.equals(passConf)){
                    Toast.makeText(getApplicationContext(),"Şifreler birbiri ile uyuşmuyor!",Toast.LENGTH_SHORT).show();
                }//Kayıt et
                else {
                    createUser(phone,pass);
                }

            }
        });

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("uid",user.getUid());
                    editor.putBoolean("isLogged",true);
                    editor.commit();

                    Log.d("Firebase", "onAuthStateChanged:signed_in:" + user.getUid());

                    Intent intentMain = new Intent(SigninActivity.this,MainActivity.class);
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

    private void createUser(String phone,String pass){
        mAuth.createUserWithEmailAndPassword(phone+"@vardinmi.com", pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("Firebase", "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(SigninActivity.this, task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                        else
                        Toast.makeText(SigninActivity.this, "Success",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


}
