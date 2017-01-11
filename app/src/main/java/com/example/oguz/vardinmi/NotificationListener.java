package com.example.oguz.vardinmi;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NotificationListener extends Service {
    SharedPreferences sharedPreferences;

    FirebaseDatabase database;
    DatabaseReference reference;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //When the service is started
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Opening sharedpreferences
        sharedPreferences = getSharedPreferences("userPref", MODE_PRIVATE);



        String id = sharedPreferences.getString("uid", null);


        if(id==null) this.stopSelf();
        else {
            database = FirebaseDatabase.getInstance();
            reference = database.getReference(id);

            Toast.makeText(getApplicationContext(), "Service started", Toast.LENGTH_SHORT).show();
            //Adding a valueevent listener to firebase
            //this will help us to  track the value changes on firebase
            reference.addValueEventListener(new ValueEventListener() {

                //This method is called whenever we change the value in firebase
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    //Getting the value from firebase
                    //We stored none as a initial value
                    String msg = "";
                    try {
                        msg = snapshot.child("request").getValue().toString();
                        //So if the value is none we will not create any notification
                        if (msg.equals("none"))
                            return;
                        //If the value is anything other than none that means a notification has arrived
                        //calling the method to show notification
                        //String msg is containing the msg that has to be shown with the notification
                        showNotification(msg);
                    } catch (Exception e) {
                        Log.i("Notification", "There is no notification.");
                    }
                }

                @Override
                public void onCancelled(DatabaseError firebaseError) {
                    Log.e("The read failed: ", firebaseError.getMessage());
                }
            });
        }
            return START_STICKY;

    }


    private void showNotification(String msg){
        //Creating a notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        Intent intent = new Intent(getApplicationContext(),FromNotification.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        builder.setContentIntent(pendingIntent);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));

        if(sharedPreferences.contains(msg)){
        builder.setContentText(sharedPreferences.getString(msg,"")+" senin konum bilgine ulaşmak istiyor.");
            builder.setContentTitle(sharedPreferences.getString(msg,"")+" seni merak ediyor :)");
        }
        else{
        builder.setContentText("Birileri senin konum bilgine ulaşmak istiyor.");
            builder.setContentTitle("Birileri seni merak ediyor :)");}
        builder.setAutoCancel(true);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(alarmSound);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }
}