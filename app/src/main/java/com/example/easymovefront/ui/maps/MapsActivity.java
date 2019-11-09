package com.example.easymovefront.ui.maps;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.easymovefront.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.android.PolyUtil;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;

import org.joda.time.DateTime;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    private LocationManager mLocationManager;
    String locationProvider;
    private static final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //getting the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        //setting the title
        toolbar.setTitle("My Toolbar");

        //placing toolbar in place of actionbar
        setSupportActionBar(toolbar);

        initializeLocationManager();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_mapsactivity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        CharSequence text;
        int duration;
        Toast toast;
        switch (item.getItemId()) {
            case R.id.profile:
                text = "PROFILE PLACEHOLDER";
                duration = Toast.LENGTH_LONG;

                toast = Toast.makeText(this, text, duration);
                toast.show();
                return true;
            case R.id.settings:
                text = "SETTINGS PLACEHOLDER";
                duration = Toast.LENGTH_LONG;

                toast = Toast.makeText(this, text, duration);
                toast.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }




    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        // Add a marker in Sydney and move the camera

        String og = String.format(Locale.ENGLISH, "%.8f,%.8f", 41.385063, 2.173404);
        String dest = String.format(Locale.ENGLISH, "%.8f,%.8f", 41.378533, 2.099841);
        CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
                new LatLng(41.385063, 2.173404), 10);
        mMap.animateCamera(location);
        DateTime now = new DateTime();
        DirectionsResult result;
        try {
             result = DirectionsApi.newRequest(getGeoContext())
                    .mode(TravelMode.WALKING).origin(og)
                    .destination(dest).departureTime(now)
                    .await();

            addMarkersToMap(result, mMap);
            addPolyline(result, mMap);
        } catch (ApiException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onLocationChanged(Location location) {

        Log.i("called", "onLocationChanged");


        //when the location changes, update the map by zooming to the location
        CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(),location.getLongitude()));
        mMap.moveCamera(center);

        CameraUpdate zoom=CameraUpdateFactory.zoomTo(15);
        mMap.animateCamera(zoom);
    }

    @Override
    public void onProviderDisabled(String arg0) {

        Log.i("called", "onProviderDisabled");
    }

    @Override
    public void onProviderEnabled(String arg0) {

        Log.i("called", "onProviderEnabled");
    }

    @Override
    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {

        Log.i("called", "onStatusChanged");
    }



    private void initializeLocationManager() {

        //get the location manager
        mLocationManager = (LocationManager)getSystemService(this.LOCATION_SERVICE);


        //define the location manager criteria
        Criteria criteria = new Criteria();

        this.locationProvider = mLocationManager.getBestProvider(criteria, false);

        if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_COARSE_LOCATION  },
                    MY_PERMISSION_ACCESS_COARSE_LOCATION );
        }
        else {
            Location location = mLocationManager.getLastKnownLocation(locationProvider);


            //initialize the location
            if(location != null) {

                onLocationChanged(location);
            }
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            Location location = mLocationManager.getLastKnownLocation(locationProvider);


            //initialize the location
            if(location != null) {

                onLocationChanged(location);
            }
        }
    }



    private GeoApiContext getGeoContext() {
        GeoApiContext geoApiContext = new GeoApiContext();
        return geoApiContext.setQueryRateLimit(3)
                .setApiKey(getString(R.string.google_maps_key))
                .setConnectTimeout(100, TimeUnit.SECONDS)
                .setReadTimeout(100, TimeUnit.SECONDS)
                .setWriteTimeout(100, TimeUnit.SECONDS);
    }

    private void addMarkersToMap(DirectionsResult results, GoogleMap mMap) {
        mMap.addMarker(new MarkerOptions().position(new LatLng(results.routes[0].legs[0].startLocation.lat,results.routes[0].legs[0].startLocation.lng)).title(results.routes[0].legs[0].startAddress));
        mMap.addMarker(new MarkerOptions().position(new LatLng(results.routes[0].legs[0].endLocation.lat,results.routes[0].legs[0].endLocation.lng)).title(results.routes[0].legs[0].startAddress).snippet(getEndLocationTitle(results)));
    }

    private String getEndLocationTitle(DirectionsResult results){
        return  "Time :"+ results.routes[0].legs[0].duration.humanReadable + " Distance :" + results.routes[0].legs[0].distance.humanReadable;
    }

    private void addPolyline(DirectionsResult results, GoogleMap mMap) {
        List<LatLng> decodedPath = PolyUtil.decode(results.routes[0].overviewPolyline.getEncodedPath());
        mMap.addPolyline(new PolylineOptions().addAll(decodedPath));
    }
}
