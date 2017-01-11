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
import java.util.List;

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

                        String[] params = {user.getUid().toString() , user.getEmail().toString().split("@")[0]};
                        String result = "fail";
                    try {
                        result = new addUser().execute(params).get();
                    }
                    catch (Exception e){
                        Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                    }

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

    private void createUser(final String phone, String pass){
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


    ProgressDialog pDialog;
    class addUser extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SigninActivity.this);
            pDialog.setMessage("Connecting...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... args) {

            String uid = args[0];
            String phone = args[1];

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("uid", uid));
            params.add(new BasicNameValuePair("phone", phone));


            JSONParser jsonParser = new JSONParser();
            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest("http://pinti.16mb.com/vardinmi/addUser.php",
                    "POST", params);

            // check log cat fro response
            try {
                Log.d("Create Response", json.toString());
            } catch (NullPointerException e) {
                Log.e("JSon Null", "Json returned null from POST");
            }
            // check for success tag
            try {
                int success = json.getInt("success");

                if (success == 1) {
                    // successfully created product
                } else {
                    // failed to create product
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }


        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();
        }

    }

}
