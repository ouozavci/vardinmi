package com.example.oguz.vardinmi;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.example.oguz.vardinmi.jsonlib.Constants;
import com.example.oguz.vardinmi.jsonlib.JSONParser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthProvider;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by oguz on 10.01.2017.
 */

public class ContactsFragment extends Fragment implements View.OnClickListener{

    ListView listview_contacts;
    public List<PersonInfo> list_items = new ArrayList<PersonInfo>();
    private ListViewAdapter listviewAdapter;
    private ProgressDialog progressDialog;

    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference reference;
    DatabaseReference referenceMine;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts,container,false);

        mAuth = FirebaseAuth.getInstance();

        listview_contacts = (ListView) view.findViewById(R.id.listView_contacts);
        if (list_items.size() == 0) {   // liste dolu ise tekrardan Async Task çağırma
            new FetchAsyncTask().execute();
        } else {
        }

        database = FirebaseDatabase.getInstance();

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {  // geri tuşuna bastığımızda listview in aynen kalması için gerekli
        super.onActivityCreated(savedInstanceState);
        this.listview_contacts = ((ListView) getActivity().findViewById(R.id.listView_contacts));
        this.listview_contacts.setAdapter(this.listviewAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        int position = (Integer) v.getTag(R.id.key_position);

        if (v.getId() == R.id.btnSendNotification){
            SharedPreferences pref = getActivity().getSharedPreferences("userPref", Context.MODE_PRIVATE);
            String myUid = pref.getString("uid",null);
            String reqUid = list_items.get(position).getUid();

            reference = database.getReference(reqUid).child("request");


            if(myUid == null) return;
            reference.setValue(myUid);
            referenceMine = database.getReference(myUid).child(reqUid);
            referenceMine.setValue("wait");
            Toast.makeText(getActivity().getApplication().getApplicationContext(),list_items.get(position).getUid(),Toast.LENGTH_LONG).show();
        }

    }


    public class FetchAsyncTask extends AsyncTask<Void, Void, List<PersonInfo>> {


        @Override
        protected void onPreExecute() {

            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Yükleniyor...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected List<PersonInfo> doInBackground(Void... params) {

            ContentResolver contentResolver = getActivity().getContentResolver();
            Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null,
                    null, null, null);
            List<String> contacts = new ArrayList<>();
            List<String> numbers = new ArrayList<>();
            String checkPhoneQuery = "select * from vardinmi where phone='qwerty'";
            if (cursor.getCount() > 0) {

                while (cursor.moveToNext()) {

                    String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID)); // id ye göre eşleşme yapılacak
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)); // telefonda kayıtlı olan ismi
                    if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                        // telefon numarasına sahip ise if içine gir.
                        Cursor person_cursor = contentResolver.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                new String[]{id}, null);

                        while (person_cursor.moveToNext()) {
                            String person_phoneNumber = person_cursor.getString(person_cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replaceAll(" ", "");
                            if (person_phoneNumber.startsWith("+9"))
                                person_phoneNumber = person_phoneNumber.substring(2);
                            if (!contacts.contains(name + "/" + person_phoneNumber)) {
                                contacts.add(name + "/" + person_phoneNumber);
                                numbers.add(person_phoneNumber);
                                checkPhoneQuery += " OR phone='" + person_phoneNumber + "'";
                            }
                        }

                    /*    while (person_cursor.moveToNext()) {
                            String person_phoneNumber = person_cursor.getString(person_cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replaceAll(" ", "");
                            if (!contacts.contains(name + "/" + person_phoneNumber)) {
                                list_items.add(new PersonInfo(name, person_phoneNumber, false)); // ismini ve telefon numarasını list içine at
                                contacts.add(name + "/" + person_phoneNumber);
                                numbers.add(person_phoneNumber);
                                checkPhoneQuery += " OR phoneNumber='" + person_phoneNumber + "'";
                            }
                        }*/
                        person_cursor.close();
                    }

                }
            }

            Log.i("phoneQuery", checkPhoneQuery);
            List<NameValuePair> args = new ArrayList<>();
            args.add(new BasicNameValuePair("query", checkPhoneQuery));
            ArrayList<String> usingNumbers = new ArrayList<>();
            JSONObject obj = null;
            try {
                JSONParser jsonParser = new JSONParser();
                obj = jsonParser.makeHttpRequest("http://pinti.16mb.com/vardinmi/checkPhones.php",
                        "POST",
                        args);
                JSONArray jsnNumbers = obj.getJSONArray("phones");

                for (int i = 0; i < jsnNumbers.length(); i++) {
                    usingNumbers.add(jsnNumbers.getString(i));
                }

                if (obj != null) Log.i("phoneResult", obj.toString());
                else Log.i("phoneResult", "null");
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
            }
            SharedPreferences pref = getActivity().getSharedPreferences("userPref",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            for (int i = 0; i < contacts.size(); i++) {
                if(usingNumbers.contains(contacts.get(i).split("/")[1]))
                    try {
                        editor.putString(obj.getString(contacts.get(i).split("/")[1]),contacts.get(i).split("/")[0]);
                        list_items.add(new PersonInfo(contacts.get(i).split("/")[0], contacts.get(i).split("/")[1], true,obj.getString(contacts.get(i).split("/")[1])));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
            }
            editor.commit();

            Collections.sort(list_items);
            return list_items;
        }

        @Override
        protected void onPostExecute(List contactList) {

            listviewAdapter = new ListViewAdapter(getActivity().getApplicationContext(), contactList, ContactsFragment.this);
            listview_contacts.setAdapter(listviewAdapter);

            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

        }
    }
}
