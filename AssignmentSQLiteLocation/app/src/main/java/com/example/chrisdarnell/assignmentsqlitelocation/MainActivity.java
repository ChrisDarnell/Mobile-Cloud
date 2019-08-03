package com.example.chrisdarnell.assignmentsqlitelocation;

import android.app.Activity;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.List;

import static com.example.chrisdarnell.assignmentsqlitelocation.R.id.sql_list_view;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    SQLiteDatabase db;
    TextView tv;


    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    public TextView mLatText;
    public TextView mLonText;
    private Location mLastLocation;
    private com.google.android.gms.location.LocationListener mLocationListener;
    private static final int LOCATION_PERMISSON_RESULT = 17;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppDatabase database = AppDatabase.getDatabase(getApplicationContext());
        for (int i = 0; i <10; i++) {
            Task build = Task.builder().setId(i).setSummary("Testing " + i).setDescription("More ..." + i).build();
            database.taskModel().addTask(build);
        }
        List<Task> allTasks = database.taskModel().getAllTasks();
//        TextView textView = findViewById(R.id.result);
//        textView.setText(allTasks.toString());
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }


//        mLatText = (TextView) findViewById(lat_output);
//        mLonText = (TextView) findViewById(lon_output);
        tv = (TextView) findViewById(sql_list_view);
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(5000);
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (location != null) {
                    mLonText.setText(String.valueOf(location.getLongitude()));
                    mLatText.setText(String.valueOf(location.getLatitude()));
                } else {
                    mLonText.setText(R.string.defaultlon);
                    mLatText.setText(R.string.defaultlat);
                }
            }


        };

    }


    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLatText.setText(R.string.oncon);
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSON_RESULT);
            mLonText.setText(R.string.noper);
            return;
        }
        updateLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Dialog errDialog = GoogleApiAvailability.getInstance().getErrorDialog(this, connectionResult.getErrorCode(), 0);
        errDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == LOCATION_PERMISSON_RESULT) {
            if (grantResults.length > 0) {
                updateLocation();
            }
        }
    }

    private void updateLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            mLonText.setText(String.valueOf(mLastLocation.getLongitude()));
            mLatText.setText(String.valueOf(mLastLocation.getLatitude()));
        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, mLocationListener);
        }
    }

    //    // Display all
    public void display(View v) {
        //use cursor to keep all data
        //cursor can keep data of any data type
        Cursor c = db.rawQuery("select * from mytable1", null);
        tv.setText("");


        if(c!=null && c.getCount() > 0)
        {
            if (c.moveToFirst())
            {
                do {
                    String userText = c.getString(c.getColumnIndex("userText"));
                    String latText=c.getString(c.getColumnIndex("latText"));
                    String lonText=c.getString(c.getColumnIndex("lonText"));
//                    String latText=((EditText) findViewById(R.id.lat_output)).getText().toString();
//                    String lonText=((EditText) findViewById(R.id.lon_output)).getText().toString();

                    tv.append("Text:"+userText+" Latitude: "+latText+" Longitude: "+lonText+"\n");
                } while (c.moveToNext());
            }
        }

    }


}





