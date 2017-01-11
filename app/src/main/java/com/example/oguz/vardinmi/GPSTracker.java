  package com.example.oguz.vardinmi;
        import android.content.Context;
        import android.location.Location;
        import android.location.LocationListener;
        import android.location.LocationManager;
        import android.media.AudioManager;
        import android.os.Bundle;

        import android.util.Log;
        import android.widget.Toast;

public class GPSTracker implements LocationListener {

    private Context mContext = null;
    public boolean isGPSEnabled = false;
    public boolean isNetworkEnabled = false;
    public boolean canGetLocation = false;

    AudioManager audioManager = null;
    Location location;
    double latitude;
    double longtitude;

    // The minimum distance to change Updates in meters
    public static long MIN_DISTANCE_CHANGE_FOR_UPDATES = 200; // 10 meters

    // The minimum time between updates in milliseconds
    public static long MIN_TIME_BW_UPDATES = 1; // 1 minute

    // Declaring a Location Manager
    protected LocationManager locationManager;

    public GPSTracker(Context context) {
        this.mContext = context;
        getLocation();
    }

    public Location getLocation() throws SecurityException {
        try {
            locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            Log.v("isGPSEnabled", "= " + isGPSEnabled);

            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            Log.v("isNetworkEnabled", "= " + isNetworkEnabled);

            if (!isGPSEnabled && isNetworkEnabled) {
                Toast.makeText(mContext, "No location provider enabled", Toast.LENGTH_SHORT).show();
            } else {
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    location = null;
                    //int permissionCheck = ContextCompat.checkSelfPermission(mContext,)
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES,
                            this);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longtitude = location.getLongitude();
                        }
                    }
                }
                if (isGPSEnabled) {
                    location = null;
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this
                        );
                        Log.d("GPSEnabled", "GPSEnabled");
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longtitude = location.getLongitude();
                            }
                        }

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }

    @Override
    public void onLocationChanged(final Location loc) {
 /*       Log.i("*********************", "Location changed");
        double lat = loc.getLatitude();
        double lon = loc.getLongitude();


        Database db = new Database(mContext);
        ArrayList<LocRecord> locRecords = db.getLocations();

        //if there is no recorded locations return
        if(locRecords.size()==0) return;

        double distance;
        double min = 0;
        for (LocRecord locRecord : locRecords
                ) {
            double recordedLat = locRecord.getLat();
            double recordedLon = locRecord.getLon();

            distance = DistanceCalculator.distance(lat, lon, recordedLat, recordedLon, "K");
            distance = Math.abs(distance);
            if (min < distance) min = distance;
        }
        distance = min;
        audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);

        if (distance < 0.2&&distance> 0) {
            audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
            MIN_TIME_BW_UPDATES = 30*1000;
            Toast.makeText(mContext, "Silent  dist:"+distance, Toast.LENGTH_SHORT).show();
            Toast.makeText(mContext, "Next check is " + MIN_TIME_BW_UPDATES/1000 + " seconds after", Toast.LENGTH_SHORT).show();
        } else {
            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            MIN_TIME_BW_UPDATES = (long) (distance + 0.01) * 1000 * 60 * 10;
            Toast.makeText(mContext, "Normal dist:"+distance, Toast.LENGTH_SHORT).show();
            Toast.makeText(mContext, "Next check is " + MIN_TIME_BW_UPDATES / 1000 + " seconds after", Toast.LENGTH_SHORT).show();
        }
*/
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    public void onProviderDisabled(String provider) {
        Toast.makeText(mContext, "Gps Disabled", Toast.LENGTH_SHORT).show();
    }


    public void onProviderEnabled(String provider) {
        Toast.makeText(mContext, "Gps Enabled", Toast.LENGTH_SHORT).show();
    }

    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }
        return latitude;
    }

    public double getLongtitude() {
        if (location != null) {
            latitude = location.getLongitude();
        }
        return longtitude;
    }

    public boolean canGetLocation() {
        return this.canGetLocation;
    }

}