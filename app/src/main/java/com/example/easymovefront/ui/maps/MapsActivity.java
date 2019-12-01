package com.example.easymovefront.ui.maps;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.easymovefront.R;
import com.example.easymovefront.data.model.CurrentBitmap;
import com.example.easymovefront.data.model.LoggedUser;
import com.example.easymovefront.data.model.ObstacleMap;
import com.example.easymovefront.network.CreateMarkerTask;
import com.example.easymovefront.network.GetMarkerTask;
import com.example.easymovefront.ui.profile.ProfileActivity;
import com.example.easymovefront.ui.settings.SettingsActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.android.PolyUtil;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener, RouteDialogFragment.OnFragmentInteractionListener, ObstacleDialogFragment.OnFragmentInteractionListener {

    private GoogleMap mMap;
    private LocationManager mLocationManager;
    String locationProvider;
    private static final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 11;
    private LatLng mUserLocation;
    private FusedLocationProviderClient fusedLocationClient;
    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar mToolbar;
    private ProgressBar mloadingBar;
    private ProgressBar loadingDrawer;
    private ImageView drawerHeader;
    DrawerLayout dLayout;

    List<Polyline> polylines = new ArrayList<Polyline>();
    List<Marker> markers = new ArrayList<Marker>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        //getting the toolbar

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mloadingBar = findViewById(R.id.loadingMaps);

        //setting the title
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setNavigationDrawer();


        initializeLocationManager();
    }



    private void setNavigationDrawer() {
        dLayout = (DrawerLayout) findViewById(R.id.drawer_layout); // initiate a DrawerLayout
        mDrawerToggle = new ActionBarDrawerToggle(this, dLayout, mToolbar,R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

                invalidateOptionsMenu();
            }
        };
        dLayout.addDrawerListener(mDrawerToggle);
        NavigationView navView = (NavigationView) findViewById(R.id.navigation); // initiate a Navigation View
        // implement setNavigationItemSelectedListener event on NavigationView
        navView.getMenu().getItem(0).setChecked(true);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                loadingDrawer = findViewById(R.id.loadingDrawer);
                drawerHeader = findViewById(R.id.imageHeader);
                CharSequence text;
                int duration;
                Toast toast;
                // check selected menu item's id and replace a Fragment Accordingly
                switch (menuItem.getItemId()) {
                    case R.id.profile:
                        drawerHeader.setVisibility(View.GONE);
                        loadingDrawer.setVisibility(View.VISIBLE);
                        Intent profileIntent = new Intent(getApplicationContext(), ProfileActivity.class);
                        startActivity(profileIntent);
                        finish();
                        return true;
                    case R.id.settings:
                        text = "SETTINGS PLACEHOLDER";
                        duration = Toast.LENGTH_LONG;
                        Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                        startActivity(intent);

                        toast = Toast.makeText(getApplicationContext(), text, duration);
                        toast.show();
                        return true;
                    default:
                        return false;
                }
            }
        });
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
            case R.id.route:
                DialogFragment newFragment = new RouteDialogFragment(this);
                newFragment.show(getSupportFragmentManager(), "kek");
                return true;
            case R.id.obstacle:
                DialogFragment newFragment2 = new ObstacleDialogFragment(this);
                newFragment2.show(getSupportFragmentManager(), "kok");
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
        if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) == PackageManager.PERMISSION_GRANTED ) {
            mMap.setMyLocationEnabled(true);
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                mUserLocation = new LatLng(location.getLatitude(), location.getLongitude());
                                CameraUpdate locationUser = CameraUpdateFactory.newLatLngZoom(mUserLocation, 10);
                                mMap.animateCamera(locationUser);
                            }
                        }
                    });
        }
        populateMap();
        CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
                new LatLng(41.385063, 2.173404), 10);
        mMap.animateCamera(location);
        mMap.setOnMarkerClickListener(Marker -> {
            mloadingBar.setVisibility(View.VISIBLE);
            DialogFragment newFragment = new DisplayObstacleFragment(this, Marker, mloadingBar);
            newFragment.show(getSupportFragmentManager(), "kek");
            return true;
        });
    }

    private void populateMap() {
        GetMarkerTask myTask = new GetMarkerTask(getApplicationContext());
        myTask.execute();
        String result = "";
        try {
              result = myTask.get();
            JSONArray Jarray = new JSONArray(result);
            for(int i=0; i<Jarray.length(); i++) {
                JSONObject dataObj = Jarray.getJSONObject(i);
                Integer id = dataObj.getInt("id");
                Double lat = dataObj.getDouble("latitud");
                Double lon = dataObj.getDouble("longitud");
                String title = dataObj.getString("nom");
                String desc = dataObj.getString("descripcio");
                //Similarly you can extract for other fields.
                LatLng loc = new LatLng(lat, lon);


                Marker mark = mMap.addMarker(new MarkerOptions()
                        .position(loc)
                        .title(title)
                        .snippet(desc)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                ObstacleMap.getInstance().addMarker(mark, dataObj);
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
                e.printStackTrace();
            }
    }

    @Override
    public void onLocationChanged(Location location) {

        Log.i("called", "onLocationChanged");
        mUserLocation = new LatLng(location.getLatitude(),location.getLongitude());


        //when the location changes, update the map by zooming to the location
        CameraUpdate center = CameraUpdateFactory.newLatLng(mUserLocation);
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

        }
        if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) == PackageManager.PERMISSION_GRANTED ) {
            mMap.setMyLocationEnabled(true);
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                mUserLocation = new LatLng(location.getLatitude(), location.getLongitude());
                                CameraUpdate locationUser = CameraUpdateFactory.newLatLngZoom(mUserLocation, 10);
                                mMap.animateCamera(locationUser);
                            }
                        }
                    });
        }
    }

    private void createObstacle(String pos, String desc, Bitmap foto, String title) {
        Address posicio = null;
        CurrentBitmap.getInstance().setBitMap(foto);
        try {
            Geocoder geo = new Geocoder(this);

            try {
                if (!pos.isEmpty()) {
                    List<Address> addresses = geo.getFromLocationName(pos, 1);
                    posicio = addresses.get(0);
                }
            } catch (IOException e) {
                //e.printStackTrace();
                CharSequence text;
                int duration;
                Toast toast;
                text = getString(R.string.address_source_notfound);
                duration = Toast.LENGTH_LONG;

                toast = Toast.makeText(this, text, duration);
                toast.show();
            }
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                LatLng loc = null;
                if (pos.isEmpty())
                    loc = new LatLng(mUserLocation.latitude, mUserLocation.longitude);
                else {
                    //IMPLEMENTAR AMB DIRECCIO
                }
                Marker mark = mMap.addMarker(new MarkerOptions()
                        .position(loc)
                        .title(title)
                        .snippet(desc)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                addMarkerToBack(desc, loc.latitude, loc.longitude, title, mark);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addMarkerToBack(String desc, double latitude, double longitude, String title, Marker mark){
        CreateMarkerTask myTask = new CreateMarkerTask(getApplicationContext());
        myTask.execute(desc, LoggedUser.getInstance().getId(), String.valueOf(latitude), String.valueOf(longitude), title);
        try {
            JSONObject obj = myTask.get();
            ObstacleMap.getInstance().addMarker(mark, obj);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void clearMap() {
        for(Polyline line : polylines)
        {
            line.remove();
        }

        polylines.clear();

        for(Marker mark : markers)
        {
            mark.remove();
        }

        markers.clear();
    }

    private void createRoute(String inputSource, String inputDest) {
        Address addOg = null;
        Address addDest = null;
        try {
            Geocoder geo = new Geocoder(this);
            List<Address> addressList = new LinkedList<>();
            try {
                if (!inputSource.isEmpty()) {
                    addressList = geo.getFromLocationName(inputSource, 1);
                    addOg = addressList.get(0);
                }
            }
            catch (IndexOutOfBoundsException e) {
                CharSequence text;
                int duration;
                Toast toast;
                text = getString(R.string.address_source_notfound);
                duration = Toast.LENGTH_LONG;

                toast = Toast.makeText(this, text, duration);
                toast.show();
            }
            List<Address> adressList2 = new LinkedList<>();
            try {
                adressList2 = geo.getFromLocationName(inputDest, 1);
                addDest = adressList2.get(0);
            }
            catch (IndexOutOfBoundsException e) {
                CharSequence text;
                int duration;
                Toast toast;
                text = getString(R.string.address_destination_notfound);
                duration = Toast.LENGTH_LONG;

                toast = Toast.makeText(this, text, duration);
                toast.show();
            }
            if ((addOg != null && addDest != null) || (inputSource.isEmpty() && addDest != null &&
                    ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) == PackageManager.PERMISSION_GRANTED)) {
                String og;
                if (!inputSource.isEmpty())
                og = String.format(Locale.ENGLISH, "%.8f,%.8f", addOg.getLatitude(), addOg.getLongitude());
                else og = String.format(Locale.ENGLISH, "%.8f,%.8f", mUserLocation.latitude, mUserLocation.longitude);
                String dest = String.format(Locale.ENGLISH, "%.8f,%.8f", addDest.getLatitude(), addDest.getLongitude());

                DateTime now = new DateTime();
                DirectionsResult result;
                result = DirectionsApi.newRequest(getGeoContext())
                        .mode(TravelMode.WALKING).origin(og)
                        .destination(dest).departureTime(now)
                        .await();

                addMarkersToMap(result, mMap);
                addPolyline(result, mMap);
            }
        } catch (ApiException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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
        markers.add(mMap.addMarker(new MarkerOptions().position(new LatLng(results.routes[0].legs[0].startLocation.lat,results.routes[0].legs[0].startLocation.lng)).title(results.routes[0].legs[0].startAddress)));
        markers.add(mMap.addMarker(new MarkerOptions().position(new LatLng(results.routes[0].legs[0].endLocation.lat,results.routes[0].legs[0].endLocation.lng)).title(results.routes[0].legs[0].endAddress).snippet(getEndLocationTitle(results))));
    }

    private String getEndLocationTitle(DirectionsResult results){
        return  "Time :"+ results.routes[0].legs[0].duration.humanReadable + " Distance :" + results.routes[0].legs[0].distance.humanReadable;
    }

    private void addPolyline(DirectionsResult results, GoogleMap mMap) {
        List<LatLng> decodedPath = PolyUtil.decode(results.routes[0].overviewPolyline.getEncodedPath());
        polylines.add(mMap.addPolyline(new PolylineOptions().addAll(decodedPath)));
    }

    public static Bitmap StringToBitMap(String image){
        try{
            byte [] encodeByte=Base64.decode(image, Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        }catch(Exception e){
            e.getMessage();
            return null;
        }
    }

    public static String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] arr = baos.toByteArray();
        String result = Base64.encodeToString(arr, Base64.DEFAULT);
        return result;
    }


    @Override
    public void onOkPressed(String src, String dest) {
        clearMap();
        createRoute(src, dest);
    }

    @Override
    public void onOkPressedObstacle(String pos, String desc, Bitmap foto, String title) {
        createObstacle(pos, desc, foto, title);
    }
}
