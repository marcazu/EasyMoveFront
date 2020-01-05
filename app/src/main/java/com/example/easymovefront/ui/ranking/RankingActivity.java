package com.example.easymovefront.ui.ranking;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easymovefront.R;
import com.example.easymovefront.data.model.RankingUser;
import com.example.easymovefront.network.GetMarkerTask;
import com.example.easymovefront.network.GetRankingTask;
import com.example.easymovefront.ui.maps.MapsActivity;
import com.example.easymovefront.ui.profile.ProfileActivity;
import com.example.easymovefront.ui.settings.SettingsActivity;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * It is the Ranking container.
 */
public class RankingActivity extends AppCompatActivity  {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar mToolbar;
    private ProgressBar mloadingBar;
    DrawerLayout dLayout;
    private ProgressBar loadingDrawer;
    private ImageView drawerHeader;

    private ArrayList<RankingUser> mUserList;

    /**
     * Is executed when the ranking screen is opened.
     * It initializes the view and load the xml
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);
        mToolbar = (Toolbar) findViewById(R.id.toolbarRanking);

        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setNavigationDrawer();
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        getRankingList();
        mAdapter = new RecyclerViewAdapter(this, mUserList);
        recyclerView.setAdapter(mAdapter);

        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL));
    }

    /**
     * Gets the ranking list from the database.
     * This list contain the tenth best users.
     */
    private void getRankingList() {
        GetRankingTask myTask = new GetRankingTask(getApplicationContext());
        myTask.execute();
        String result = "";
        try {
            result = myTask.get();
            JSONArray Jarray = new JSONArray(result);
            mUserList = new ArrayList<>();
            for(int i=0; i<Jarray.length(); i++) {
                JSONObject dataObj = Jarray.getJSONObject(i);
                String nom = dataObj.getString("nom");
                String puntuacio = dataObj.getString("puntuacio");
                String id = dataObj.getString("id");
                RankingUser u = new RankingUser(nom, Integer.valueOf(puntuacio), Integer.valueOf(id));
                mUserList.add(u);
            }

        } catch ( ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Defines the {@link DrawerLayout} for the current screen.
     * It highlights the current menu and links the others to the respective screens
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
        NavigationView navView = (NavigationView) findViewById(R.id.navigationProfile); // initiate a Navigation View
        // implement setNavigationItemSelectedListener event on NavigationView
        navView.getMenu().getItem(2).setChecked(true);
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
                    case R.id.mapActivity:
                        drawerHeader.setVisibility(View.GONE);
                        loadingDrawer.setVisibility(View.VISIBLE);
                        Intent mapIntent = new Intent(getApplicationContext(), MapsActivity.class);
                        startActivity(mapIntent);
                        finish();
                        return true;
                    case R.id.profile:
                        drawerHeader.setVisibility(View.GONE);
                        loadingDrawer.setVisibility(View.VISIBLE);
                        Intent profileIntent = new Intent(getApplicationContext(), ProfileActivity.class);
                        startActivity(profileIntent);
                        finish();
                        return true;
                    case R.id.ranking:
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
     * Defines the functionality when the back button is pressed.
     * It opens the {@link DrawerLayout}
     */
    @Override
    public void onBackPressed() {
        if (!dLayout.isDrawerOpen(GravityCompat.START)) dLayout.openDrawer(GravityCompat.START);
    }
}
