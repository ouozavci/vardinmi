package com.example.oguz.vardinmi;

import android.app.ProgressDialog;
import android.content.ContentResolver;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthProvider;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
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
        reference = database.getReference("message");


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
            reference.setValue(position + "--" + list_items.get(position).getPhoneNumber() +" "+list_items.get(position).getName());

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
            if (cursor.getCount() > 0) {

                while (cursor.moveToNext()) {

                    String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID)); // id ye göre eşleşme yapılacak
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    //String phone = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
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
                                                            }
                        }
                        person_cursor.close();

                    }
                }
            }
            final List<PersonInfo> providers = new ArrayList<>();
            final List<String> checkAll = new ArrayList<>();
            for (int i = 0; i < contacts.size(); i++) {
                final String phone = contacts.get(i).split("/")[1];
                final String name = contacts.get(i).split("/")[0];
                mAuth.fetchProvidersForEmail(phone+"@vardinmi.com").addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                        checkAll.add("");
                        if(task.isSuccessful()){
                            if(task.getResult().getProviders().size()>0)
                                providers.add(new PersonInfo(name,phone,true));
                            }
                    }
                });

            }

            while (checkAll.size()<contacts.size()){

            }
            list_items=providers;
            return providers;
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
