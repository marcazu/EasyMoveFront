package com.example.easymovefront.ui.maps;

import android.content.Intent;
import android.content.SharedPreferences;
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
import android.preference.PreferenceManager;
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
import com.example.easymovefront.ui.ranking.RankingActivity;
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
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.TravelMode;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

/**
 * This activity displays a Google Maps fragment along with a list of obstacles on top of it. Each
 * one can be clicked to bring up a window displaying info. This activity also displays a toolbar
 * on top of the screen which has buttons that allow the user to add an obstacle to the map as well as
 * generate a new route or see the steps of said route.
 */
public class MapsActivity extends AppCompatActivity implements AsyncResponse, OnMapReadyCallback, LocationListener, RouteDialogFragment.OnFragmentInteractionListener, ObstacleDialogFragment.OnFragmentInteractionListener, StepDialogFragment.OnListFragmentInteractionListener {

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
    private SharedPreferences mSharedPreference;
    private boolean mGeneratedRoute;
    private MenuItem nextStep;
    DrawerLayout dLayout;
    private Marker currentMarker;

    List<Polyline> polylines = new ArrayList<Polyline>();
    ArrayList <Marker> markers = new ArrayList<Marker>();
    ArrayList<String> steps;
    Set <Marker> obstacles;
    private Fragment newFragment3;
    private boolean inStepDialogFragment;
    private ArrayList<Integer> obstructedRoutes;

    /**
     * Initializes the map instance, retrieves all markers from backend and initializes the drawer
     * and tool bar instances
     * @param savedInstanceState of the activity created
     */
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
        mGeneratedRoute = false;
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mloadingBar = findViewById(R.id.loadingMaps);
        //setting the title
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        setNavigationDrawer();


        initializeLocationManager();

        mSharedPreference = PreferenceManager
                        .getDefaultSharedPreferences(this);

    }


    /**
     * Creates the lateral menu (drawer) and sets its listener for each possible clicked item
     */
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
                    case R.id.ranking:
                        drawerHeader.setVisibility(View.GONE);
                        loadingDrawer.setVisibility(View.VISIBLE);
                        Intent rankingIntent = new Intent(getApplicationContext(), RankingActivity.class);
                        startActivity(rankingIntent);
                        finish();
                        return true;
                    case R.id.settings:
                        drawerHeader.setVisibility(View.GONE);
                        loadingDrawer.setVisibility(View.VISIBLE);
                        Intent settingsIntent = new Intent(getApplicationContext(), SettingsActivity.class);
                        startActivity(settingsIntent);
                        finish();
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    /**
     * This displays the list of options in the menu
     * @param menu to be inflated with options
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_mapsactivity, menu);
        nextStep = menu.getItem(2);
        nextStep.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * This acts as a handler for when an item of the menu provided is clicked
     * @param item MenuItem
     * @return true always
     */
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
            case R.id.nextStep:
                if (mGeneratedRoute) {
                    inStepDialogFragment = true;
                    newFragment3 = new StepDialogFragment(this, steps);
                    FragmentManager manager = getSupportFragmentManager();
                    FragmentTransaction ft = manager.beginTransaction();
                    ft.replace(R.id.drawer_layout, newFragment3);
                    //ft.add(newFragment3, null);
                    ft.addToBackStack(null).commit();
                }
                else  {
                    toast = Toast.makeText(this, "Please generate a route" , Toast.LENGTH_LONG);
                    toast.show();
                }
                return true;
                //newFragment3.get
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
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
            if (ObstacleMap.getInstance().getMap().containsKey(Marker)) {
                DialogFragment newFragment = new DisplayObstacleFragment(this, Marker, mloadingBar);
                newFragment.show(getSupportFragmentManager(), "kek");
            }
            else {
                Marker.showInfoWindow();
                mloadingBar.setVisibility(View.GONE);
            }
            return true;
        });
    }

    /**
     * This method retrieves all the markers available from backend and proceeds to instantiate each
     * one on top of the map fragment
     */
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

                Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(),getResources().getIdentifier("pin_obstacle", "drawable", getPackageName()));
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, 140, 140, false);

                Marker mark = mMap.addMarker(new MarkerOptions()
                        .position(loc)
                        .title(title)
                        .snippet(desc)
                        .icon(BitmapDescriptorFactory.fromBitmap(resizedBitmap)));

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

    /**
     * This method is called when location changes, it updated the local variable for currentUser
     * location
     * @param location new location
     */
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


    /**
     * Initializes the location manager which will keep track of the currentUser's location
     * It will prompt the user to accept permissions if it hasn't been done yet
     */
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

    /**
     * This handles the permissions callback result
     * @param requestCode
     * @param permissions the list of permissions requiered
     * @param grantResults
     */
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

    /**
     * This method creates an obstacle and adds it to the map
     * @param pos position of the currentUser
     * @param desc description of the obstacle
     * @param foto picture attached to the obstacle
     * @param title title of the obstacle
     */
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

                Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(),getResources().getIdentifier("pin_obstacle", "drawable", getPackageName()));
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, 140, 140, false);

                Marker mark = mMap.addMarker(new MarkerOptions()
                        .position(loc)
                        .title(title)
                        .snippet(desc)
                        .icon(BitmapDescriptorFactory.fromBitmap(resizedBitmap)));

                addMarkerToBack(desc, loc.latitude, loc.longitude, title, mark);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method created a network asyncTask to update the backend with the new obstacle
     * @param desc description of the obstacle
     * @param latitude in º of the obstacle
     * @param longitude in º of the obstacle
     * @param title of the obstacle
     * @param mark instance of Marker corresponding to the obstacle
     */
    private void addMarkerToBack(String desc, double latitude, double longitude, String title, Marker mark){
        CreateMarkerTask myTask = new CreateMarkerTask(getApplicationContext());
        currentMarker = mark;
        mloadingBar.setVisibility(View.VISIBLE);
        myTask.asyncResponse = this;
        myTask.execute(desc, LoggedUser.getInstance().getId(), String.valueOf(latitude), String.valueOf(longitude), title);
    }

    /**
     * Handles the loading bar status and adds the marker to the map
     * @param output Json containing the obstacle info
     */
    @Override
    public void processFinish(JSONObject output) {
        ObstacleMap.getInstance().addMarker(currentMarker, output);
        mloadingBar.setVisibility(View.GONE);
    }

    /**
     * Handles the loading bar status and adds the marker to the map
     * @param output Json containing the obstacle info
     */
    @Override
    public void processFinish(String output) {
    }

    /**
     * Clears the currently drawn route
     */
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

    /**
     * Creates the route and draws the polylines on the map
     * @param inputSource the address of the starting position
     * @param inputDest the address of the ending position
     */
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
                        .alternatives(true)
                        .language("ca")
                        .destination(dest).departureTime(now)
                        .await();
                mGeneratedRoute = true;
                selectRoutesWithoutObstacles(result);
                formStepByStepRoute(result);
                addMarkersToMap(result, mMap);
                addPolyline(result, mMap);
                nextStep.setVisible(true);
            }
        } catch (ApiException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void selectRoutesWithoutObstacles(DirectionsResult result) {
        obstacles =  ObstacleMap.getInstance().getMap().keySet();
        obstructedRoutes = new ArrayList<>();
        for (int i = 0; i < result.routes.length; ++i) {
            if (obstacleInRange(result.routes[i]))
                obstructedRoutes.add(i);
            }

    }

    private boolean obstacleInRange(DirectionsRoute route) { //mirar si la ruta té un obstacle en un rang proper en qualsevol dels seus punts
        List<LatLng> decodedPath = PolyUtil.decode(route.overviewPolyline.getEncodedPath());
        //FIB 41.389482, 2.113387
        //obstacle FIB 41.389190, 2.113584
        //consell est 41.388625, 2.112816
        final double RANG_CONSTANT_LAT =  0.0002; //0.000345;
        final double RANG_CONSTANT_LONG = 0.0002; //0.000345;
        Iterator<Marker> iterator = obstacles.iterator();
        while (iterator.hasNext()) {
            Marker marker = iterator.next();
            double latObstacle = marker.getPosition().latitude;
            double longObstacle = marker.getPosition().longitude;
            //System.out.println("Latitud obstacle "+ latObstacle);
            for (int j = 0; j < decodedPath.size(); ++j) {
                double latPuntRuta = decodedPath.get(j).latitude;
                double longPuntRuta = decodedPath.get(j).longitude;
                //System.out.println("Latitud punt ruta "+ latPuntRuta);
                System.out.println("latObstacle: " + latObstacle + "  longObstacle: " + longObstacle);
                System.out.println("latPuntRuta: " + latPuntRuta + "  longPuntRuta: " + longPuntRuta);
                if ((latObstacle <= latPuntRuta + RANG_CONSTANT_LAT  && latObstacle >= latPuntRuta - RANG_CONSTANT_LAT) &&
                        (longObstacle <= longPuntRuta + RANG_CONSTANT_LONG  && longObstacle >= longPuntRuta - RANG_CONSTANT_LONG)
                    ) {
                    System.out.println("DINS EL IF!!!");
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Obtains an instance of the geocoder used to translate LatLongs into actual physical addresses
     * @return the geocoder context
     */
    private GeoApiContext getGeoContext() {
        GeoApiContext geoApiContext = new GeoApiContext();
        return geoApiContext.setQueryRateLimit(3)
                .setApiKey(getString(R.string.google_maps_key))
                .setConnectTimeout(100, TimeUnit.SECONDS)
                .setReadTimeout(100, TimeUnit.SECONDS)
                .setWriteTimeout(100, TimeUnit.SECONDS);
    }


    /**
     * Draws the starting and ending point markers for the route to the map
     * @param results the array containing the starting and ending point
     * @param mMap the GoogleMap map instance
     */
    private void addMarkersToMap(DirectionsResult results, GoogleMap mMap) {

        String orgColor = mSharedPreference.getString("origin_color","Red");
        String dstColor = mSharedPreference.getString("destination_color","Green");

        MarkerOptions origin = new MarkerOptions();
        MarkerOptions destination = new MarkerOptions();

        switch (orgColor) {
            case "Azure":
                origin = new MarkerOptions()
                        .position(new LatLng(results.routes[0].legs[0].startLocation.lat,
                                results.routes[0].legs[0].startLocation.lng))
                        .title(results.routes[0].legs[0].startAddress)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                break;
            case "Cyan":
                origin = new MarkerOptions()
                        .position(new LatLng(results.routes[0].legs[0].startLocation.lat,results.routes[0].legs[0].startLocation.lng))
                        .title(results.routes[0].legs[0].startAddress)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
                break;
            case "Blue":
                origin = new MarkerOptions()
                        .position(new LatLng(results.routes[0].legs[0].startLocation.lat,results.routes[0].legs[0].startLocation.lng))
                        .title(results.routes[0].legs[0].startAddress)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                break;
            case "Green":
                origin = new MarkerOptions()
                        .position(new LatLng(results.routes[0].legs[0].startLocation.lat,results.routes[0].legs[0].startLocation.lng))
                        .title(results.routes[0].legs[0].startAddress)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                break;
            case "Magenta":
                origin = new MarkerOptions()
                        .position(new LatLng(results.routes[0].legs[0].startLocation.lat,results.routes[0].legs[0].startLocation.lng))
                        .title(results.routes[0].legs[0].startAddress)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                break;
            case "Orange":
                origin = new MarkerOptions()
                        .position(new LatLng(results.routes[0].legs[0].startLocation.lat,results.routes[0].legs[0].startLocation.lng))
                        .title(results.routes[0].legs[0].startAddress)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                break;
            case "Red":
                origin = new MarkerOptions()
                        .position(new LatLng(results.routes[0].legs[0].startLocation.lat,results.routes[0].legs[0].startLocation.lng))
                        .title(results.routes[0].legs[0].startAddress)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                break;
            case "Rose":
                origin = new MarkerOptions()
                        .position(new LatLng(results.routes[0].legs[0].startLocation.lat,results.routes[0].legs[0].startLocation.lng))
                        .title(results.routes[0].legs[0].startAddress)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
                break;
            case "Violet":
                origin = new MarkerOptions()
                        .position(new LatLng(results.routes[0].legs[0].startLocation.lat,results.routes[0].legs[0].startLocation.lng))
                        .title(results.routes[0].legs[0].startAddress)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
                break;
            case "Yellow":
                origin = new MarkerOptions()
                        .position(new LatLng(results.routes[0].legs[0].startLocation.lat,results.routes[0].legs[0].startLocation.lng))
                        .title(results.routes[0].legs[0].startAddress)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                break;
        }

        switch (dstColor) {
            case "Azure":
                destination = new MarkerOptions()
                        .position(new LatLng(results.routes[0].legs[0].endLocation.lat,results.routes[0].legs[0].endLocation.lng))
                        .title(results.routes[0].legs[0].endAddress)
                        .snippet(getEndLocationTitle(results))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                break;
            case "Cyan":
                destination = new MarkerOptions()
                        .position(new LatLng(results.routes[0].legs[0].endLocation.lat,results.routes[0].legs[0].endLocation.lng))
                        .title(results.routes[0].legs[0].endAddress)
                        .snippet(getEndLocationTitle(results))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
                break;
            case "Blue":
                destination = new MarkerOptions()
                        .position(new LatLng(results.routes[0].legs[0].endLocation.lat,results.routes[0].legs[0].endLocation.lng))
                        .title(results.routes[0].legs[0].endAddress)
                        .snippet(getEndLocationTitle(results))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                break;
            case "Green":
                destination = new MarkerOptions()
                        .position(new LatLng(results.routes[0].legs[0].endLocation.lat,results.routes[0].legs[0].endLocation.lng))
                        .title(results.routes[0].legs[0].endAddress)
                        .snippet(getEndLocationTitle(results))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                break;
            case "Magenta":
                destination = new MarkerOptions()
                        .position(new LatLng(results.routes[0].legs[0].endLocation.lat,results.routes[0].legs[0].endLocation.lng))
                        .title(results.routes[0].legs[0].endAddress)
                        .snippet(getEndLocationTitle(results))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                break;
            case "Orange":
                destination = new MarkerOptions()
                        .position(new LatLng(results.routes[0].legs[0].endLocation.lat,results.routes[0].legs[0].endLocation.lng))
                        .title(results.routes[0].legs[0].endAddress)
                        .snippet(getEndLocationTitle(results))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                break;
            case "Red":
                destination = new MarkerOptions()
                        .position(new LatLng(results.routes[0].legs[0].endLocation.lat,results.routes[0].legs[0].endLocation.lng))
                        .title(results.routes[0].legs[0].endAddress)
                        .snippet(getEndLocationTitle(results))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                break;
            case "Rose":
                destination = new MarkerOptions()
                        .position(new LatLng(results.routes[0].legs[0].endLocation.lat,results.routes[0].legs[0].endLocation.lng))
                        .title(results.routes[0].legs[0].endAddress)
                        .snippet(getEndLocationTitle(results))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
                break;
            case "Violet":
                destination = new MarkerOptions()
                        .position(new LatLng(results.routes[0].legs[0].endLocation.lat,results.routes[0].legs[0].endLocation.lng))
                        .title(results.routes[0].legs[0].endAddress)
                        .snippet(getEndLocationTitle(results))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
                break;
            case "Yellow":
                destination = new MarkerOptions()
                        .position(new LatLng(results.routes[0].legs[0].endLocation.lat,results.routes[0].legs[0].endLocation.lng))
                        .title(results.routes[0].legs[0].endAddress)
                        .snippet(getEndLocationTitle(results))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                break;
        }

        markers.add(mMap.addMarker(origin));
        markers.add(mMap.addMarker(destination));
    }

    /**
     * Gets the route estimated time
     * @param results the array containing the list of steps
     * @return the estimated time in humanReadable form
     */
    private String getEndLocationTitle(DirectionsResult results){
        return  "Time :"+ results.routes[0].legs[0].duration.humanReadable + " Distance :" + results.routes[0].legs[0].distance.humanReadable;
    }

    /**
     * Draws polylines
     * @param results the array containing the points that need to be drawn
     * @param mMap the Google Maps instance
     */
    private void addPolyline(DirectionsResult results, GoogleMap mMap) {
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(0xff000000); //black
        colors.add(0xff0000ff); //blue
        colors.add(0xffff0000); //red
        colors.add(0xff888888); //gray
        colors.add(0xff00ff00); //green
        colors.add(0xffff00ff); //magenta
        colors.add(0xffffff00); //yellow
        if (results.routes.length == 0) {
            Toast toast = Toast.makeText(this, "NO HI HA RUTES" , Toast.LENGTH_LONG);
            toast.show();
        }
        else if (obstructedRoutes.size() == results.routes.length) {
            Toast toast = Toast.makeText(this, "NO HI HA RUTES NO OBSTACULITZADES" , Toast.LENGTH_LONG);
            toast.show();
        }
        else {
            for (int i = 0; i < results.routes.length; ++i) {
                if (!obstructedRoutes.contains(i)) {
                    List<LatLng> decodedPath = PolyUtil.decode(results.routes[i].overviewPolyline.getEncodedPath());
                    polylines.add(mMap.addPolyline(new PolylineOptions().color(colors.get(i)).addAll(decodedPath)));
                }
            }
        }
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

    /**
     * Defines the functionality when the back button is pressed.
     * It opens the {@link DrawerLayout}
     */
    @Override
    public void onBackPressed() {
        if (inStepDialogFragment) {
            android.app.FragmentManager fm = getFragmentManager();
            if (fm.getBackStackEntryCount() > 0) {
                fm.popBackStack();
            } else {
                super.onBackPressed();
            }
            inStepDialogFragment = false;
        }
        else if (!dLayout.isDrawerOpen(GravityCompat.START)) dLayout.openDrawer(GravityCompat.START);
    }

    private void formStepByStepRoute(DirectionsResult result) {
        steps = new ArrayList<>();
        for (int i = 0; i < result.routes.length; ++i) {
            if (!obstructedRoutes.contains(i)) {
                steps.add("RUTA " + (i + 1));
                for (int j = 0; j < result.routes[i].legs.length; ++j) {
                    for (int k = 0; k < result.routes[i].legs[j].steps.length; ++k) {
                        String html_steps = result.routes[i].legs[j].steps[k].htmlInstructions;
                        String plain_text_steps = html_steps.replaceAll("(?s)<[^>]*>(\\s*<[^>]*>)*", " ");
                        steps.add(plain_text_steps);
                    }
                }
            }
        }
    }

    @Override
    public void onListFragmentInteraction(String mItem) {

    }
    
}
