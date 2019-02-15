package com.hw4.juri.hw4;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "MainActivity";
    private RecyclerView recyclerView;
    private TextView locationText;
    private OfficialAdapter mAdapter;
    private List<Official> officialList = new ArrayList<>();
    private Locator locator;
    private String locText;
    private double LAT;
    private double LONG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locator = new Locator(this);

        locationText = findViewById(R.id.curLocation);
        recyclerView = findViewById(R.id.recycler);
        mAdapter = new OfficialAdapter(officialList, this);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if(doNetCheck()){
            new CivicInfoDownloader(this).execute(locText);
        }else{
            setOfficialList(null);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.help:
                Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
                return true;
            case R.id.location:
                selectLocation();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view) {
        int pos = recyclerView.getChildLayoutPosition(view);
        Official o = officialList.get(pos);
        Intent intent = new Intent(MainActivity.this, OfficialActivity.class);
        intent.putExtra("OFFICIAL",o);
        intent.putExtra("LOCATION",locationText.getText().toString());
        startActivity(intent);
    }

    private boolean doNetCheck() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            Toast.makeText(this, "Cannot access ConnectivityManager", Toast.LENGTH_SHORT).show();
            return false;
        }

        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    public void selectLocation(){
        LayoutInflater inflater = LayoutInflater.from(this);
        final View view = inflater.inflate(R.layout.add_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Enter a City, State or a Zip Code:");
        builder.setView(view);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                EditText inputLocation = view.findViewById(R.id.inputLocation);
                locText = inputLocation.getText().toString();
                if(doNetCheck()){
                    new CivicInfoDownloader(MainActivity.this).execute(locText);
                }else{
                    Toast.makeText(MainActivity.this, "No Network Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCancelable(false);
    }

    public void setData(double lat, double lon) {
        LAT = lat;
        LONG = lon;
        if(doNetCheck()){
            String address = doLocationWork(lat, lon);
            locText = address;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.d(TAG, "onRequestPermissionsResult: CALL: " + permissions.length);
        Log.d(TAG, "onRequestPermissionsResult: PERM RESULT RECEIVED");

        if (requestCode == 5) {
            Log.d(TAG, "onRequestPermissionsResult: permissions.length: " + permissions.length);
            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "onRequestPermissionsResult: HAS PERM");
                        locator.setUpLocationManager();
                        locator.determineLocation();
                    } else {
                        Toast.makeText(this, "Location permission was denied - cannot determine address", Toast.LENGTH_LONG).show();
                        Log.d(TAG, "onRequestPermissionsResult: NO PERM");
                    }
                }
            }
        }
        Log.d(TAG, "onRequestPermissionsResult: Exiting onRequestPermissionsResult");
    }

    private String doLocationWork(double latitude, double longitude) {
        Log.d(TAG, "doAddress: Lat: " + latitude + ", Lon: " + longitude);
        List<Address> addresses = null;
        Geocoder geocoder = new Geocoder(this);
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            String zipCode = addresses.size()>0?addresses.get(0).getCountryCode():null;
            return zipCode;
        } catch (IOException e) {
            Log.d(TAG, "doAddress: " + e.getMessage());
            Toast.makeText(this, "Address cannot be acquired from provided lat and long", Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(this, "GeoCoder service timed out - please try again", Toast.LENGTH_LONG).show();
        return null;
    }

    public void noLocationAvailable() {
        Toast.makeText(this, "No location providers were available", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        locator.shutdown();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        if(doNetCheck()){
            if(locText == null){
                locText = doLocationWork(LAT,LONG);
                new CivicInfoDownloader(this).execute(locText);
            }
        }else{
            setOfficialList(null);
        }
        super.onResume();
    }

    public void setOfficialList(Object[] results){
        if(results != null){
            locationText.setText((String)results[0]);
            officialList.clear();
            officialList.addAll((ArrayList<Official>)results[1]);
        }else {
            locText = null;
            locationText.setText("No Data For Location");
            officialList.clear();
        }
        mAdapter.notifyDataSetChanged();
    }
}
