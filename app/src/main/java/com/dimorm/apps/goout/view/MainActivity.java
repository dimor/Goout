package com.dimorm.apps.goout.view;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.dimorm.apps.goout.R;
import com.dimorm.apps.goout.controller.ChangeFragment;
import com.dimorm.apps.goout.controller.InternetConnection;
import com.dimorm.apps.goout.model.DatabaseSQL;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class MainActivity extends AppCompatActivity implements LocationListener, ChangeFragment {
    Bundle args;
    double lat,lng;
    LocationManager locationManager;
    boolean currentLocation;
    LatLng latLngMyLocation;
    TextView LoadingGpsTV;
    ProgressBar LoadingGpsProgress;
    boolean autoSearch;
    public static boolean DISTANCE_IN_KM;
    SharedPreferences preferences;
    DatabaseSQL databaseSQL;
    SearchView searchView = null;
    MenuItem menuItem;
    MenuItem itemToggle;
    ToggleButton toggleButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ////////////////////////////////preference menu check////////////////////////////////
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        DISTANCE_IN_KM = preferences.getBoolean("switch_dis",true);

        /////////////////////////////////////VARIABLES//////////////////////////////////////////
        autoSearch = true;
        databaseSQL = new DatabaseSQL(this);
        args = new Bundle();
        LoadingGpsTV = (TextView) findViewById(R.id.LoadingGps);
        LoadingGpsProgress = (ProgressBar) findViewById(R.id.progressBarGPS);
        locationManager = (LocationManager) this.getSystemService(Service.LOCATION_SERVICE);
        ////////////////////////////////////////////////////////////////////////////////////////////

        //////////////////////////////////////Progress bar //////////////////////////
        LoadingGpsTV.setVisibility(View.VISIBLE);
        LoadingGpsProgress.setVisibility(View.VISIBLE);

        /////////////////////////////////////////CHECK FOR GPS///////////////////////////////////
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }

        //////////////////////////////////////////CHECK FOR INTERNET///////////////////////////////////////
        if (!InternetConnection.checkConnection(this)) {
            HistoryFragment historyFragment = new HistoryFragment();
            Bundle data = new Bundle();
            data.putDouble("lat",lat);
            data.getDouble("lng",lng);
            historyFragment.setArguments(data);
            getFragmentManager().beginTransaction().addToBackStack("history").replace(R.id.mainContainer,historyFragment).commit();
            Toast.makeText(this, "No Internet Connection ", Toast.LENGTH_SHORT).show();
        }


        /////////////////////////////////////////////////GPS PERMISSIONS -CHECK////////////////////////////////////////////////////////////
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            //requestLocationUpdates(the provider used to get location - gps/network , refresh time milliseconds ,minimum refresh distance,
            //location listener)
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 1, this);

        } else {
            //request permission 12 is the request number
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 12);
        }

        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    }


    /////////////////////////////////////////////GPS PERMISSIONS -NO PERMISSION///////////////////////////////////////////////////
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 12) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                }
                locationManager = (LocationManager) getSystemService(Service.LOCATION_SERVICE);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 1, MainActivity.this);
            } else {
                Toast.makeText(MainActivity.this, "you must open the gps permission!", Toast.LENGTH_SHORT).show();
            }
        }
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////





    /////////////////////////////////////////////////////////////MENU APP BAR///////////////////////////////////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        ////////////////////////Toggle//////////////////////////
         itemToggle = menu.findItem(R.id.currentLocationSwitch);
        itemToggle.setActionView(R.layout.use_toggle);
        toggleButton = (ToggleButton) menu.findItem(R.id.currentLocationSwitch).getActionView().findViewById(R.id.action_toggle);
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                currentLocation = isChecked;
                if (currentLocation){
                        searchView.setQueryHint("e.g Pizza,Supermarket,Bank");
                    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        buildAlertMessageNoGps();
                        buttonView.setChecked(false);
                    }
                    else
                        Toast.makeText(MainActivity.this, "Search via Current location Enabled ", Toast.LENGTH_SHORT).show();
                }else{
                    searchView.setQueryHint("e.g  Pizza in Berlin");
                    Toast.makeText(MainActivity.this, "Current Location Disabled", Toast.LENGTH_SHORT).show();
                }

            }
        });




        /////////////SEARCH ACTION BAR//////////////////////////
         menuItem = menu.findItem(R.id.search_bar);
        searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {

                searchView.onActionViewCollapsed();
                searchView.setQueryHint("e.g  Pizza in Berlin");
                if (!InternetConnection.checkConnection(MainActivity.this)) {
                    Toast.makeText(MainActivity.this, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show();
                } else {

                    if (currentLocation & lat == 0 & lng == 0) {
                        Toast.makeText(MainActivity.this, "No GPS Connection", Toast.LENGTH_SHORT).show();
                    } else {
                        if (query.length() > 0) {
                            autoSearch = false;
                            Request_Result_Fragmnet listFragment = new Request_Result_Fragmnet();

                         Intent intent = new Intent(MainActivity.this,MyIntentService.class);
                            intent.putExtra("query_string", query);
                            intent.putExtra("lat", lat);
                            intent.putExtra("lng", lng);
                            intent.putExtra("currentLocation", currentLocation);
                            intent.putExtra("auto", autoSearch);
                            startService(intent);
                            LoadingGpsProgress.setVisibility(View.GONE);
                            LoadingGpsTV.setVisibility(View.GONE);
                            getFragmentManager().beginTransaction().addToBackStack("list").replace(R.id.LinearContainer, listFragment).commit();

                        }
                    }
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {


                return false;
            }
        });


        ////////SEARCH HINT/////////////////

        return true;
    }


    ///////////////////////////////////MENU CLICK HANDLE ///////////////////////////
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.fav:
                FavFragment favFragment = new FavFragment();
                Bundle data = new Bundle();
                data.putDouble("lat", lat);
                data.putDouble("lng", lng);
                favFragment.setArguments(data);
                getFragmentManager().beginTransaction().addToBackStack("fav").replace(R.id.LinearContainer, favFragment).commit();
                return true;

            case R.id.settings_preference:
                Intent intent = new Intent(this, MySettings.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //////////////////////////////////////ALERT DIALOG NO GPS///////////////////////////////////////////////////////////
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.dismiss();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }


    /////////////////////////////////////////////////LOCATION LISTENER /////////////////////////////////////////////////
    @Override
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        lng = location.getLongitude();
        if (lat > 0 & lng > 0 & InternetConnection.checkConnection(this)) {
            openAutoSearchFragment();
            LoadingGpsTV.setVisibility(View.GONE);
            LoadingGpsProgress.setVisibility(View.GONE);
            LoadingGpsTV.setVisibility(View.GONE);
            latLngMyLocation = new LatLng(lat, lng);
            Log.d("Location Detected: ", location.getLatitude() + "\n " + location.getLongitude());
        }
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////AUTO SEARCH ON START////////////////////////////////
    private void openAutoSearchFragment() {

        Request_Result_Fragmnet listFragment = (Request_Result_Fragmnet) getFragmentManager().findFragmentByTag("autoRun");

        if (listFragment == null & autoSearch) {
            listFragment = new Request_Result_Fragmnet();
            Intent intent = new Intent(MainActivity.this,MyIntentService.class);
            intent.putExtra("lat", lat);
            intent.putExtra("lng", lng);
            intent.putExtra("auto", autoSearch);
            startService(intent);
            getFragmentManager().beginTransaction().addToBackStack("list").replace(R.id.LinearContainer, listFragment, "autoRun").commit();
        }

    }
    //////////////////////////////////////////////////////CHANGE TO MAP FRAGMENT INTERFACE///////////////////////
    @Override
    public void ChangeFragment(final LatLng latLng) {
        final LatLng disLocation = latLng;
        MapFragment mapFragment = new MapFragment();
        getFragmentManager().beginTransaction().addToBackStack("map Frag").replace(R.id.mainContainer, mapFragment).commit();
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {

                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(disLocation, 14);

                googleMap.addMarker(new MarkerOptions()
                        .position(disLocation)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

                if (latLngMyLocation != null)
                    googleMap.addMarker(new MarkerOptions()
                            .position(latLngMyLocation)
                            .title("My Location")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

                googleMap.moveCamera(update);
            }
        });
    }


    @Override
    protected void onPause() {
        locationManager.removeUpdates(this);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        locationManager.removeUpdates(this);
        super.onDestroy();
    }


    @Override
    protected void onResume() {

        ////////////////////////////////////ON RESUME ASK FOR GPS CONNECTION////////////////////////////////////
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 1, this);
        //////////////////////////////////////LOAD PREFERENCE /////////////////////////////
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        DISTANCE_IN_KM = preferences.getBoolean("switch_dis",true);

        Toast.makeText(this, ""+DISTANCE_IN_KM, Toast.LENGTH_SHORT).show();

        super.onResume();
    }

            ////////////////////BACK PRESSED//////////////////////
    @Override //TODO // FIXME: 5/1/2017
    public void onBackPressed() {

        int count = getFragmentManager().getBackStackEntryCount();
        if (count == 0)
            super.onBackPressed();
            //additional code
        else {
            getFragmentManager().popBackStack();
        }

    }


    //////////////////////////////RECEIVER FOR BATTERY CONNECTION//////////////////////////
     public static class myPlugInReceiver extends BroadcastReceiver
    {
        public myPlugInReceiver(){
        }
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "Device Power Connected", Toast.LENGTH_SHORT).show();
        }
    }


}


