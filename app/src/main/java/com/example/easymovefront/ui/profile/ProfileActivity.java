package com.example.easymovefront.ui.profile;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.easymovefront.R;
import com.example.easymovefront.data.model.LoggedUser;
import com.example.easymovefront.network.CreateImageFromUrlTask;
import com.example.easymovefront.network.GetSingleUserTask;
import com.example.easymovefront.ui.login.LoginActivity;
import com.example.easymovefront.ui.maps.MapsActivity;
import com.example.easymovefront.ui.ranking.RankingActivity;
import com.example.easymovefront.ui.settings.SettingsActivity;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

/**
 * It contains all of Profile screen.
 * It is a user-friendly interface. Displays the google user profile pic, its name,
 * its points and its number of obstacles created.
 * It also contains the sign out button.
 */
public class ProfileActivity extends AppCompatActivity {
    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar mToolbar;
    private ProgressBar mloadingBar;
    DrawerLayout dLayout;
    private ProgressBar loadingDrawer;
    private ImageView drawerHeader;

    /**
     * Is executed when the settings menu is first opened.
     * It initializes the view and load the xml
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mToolbar = (Toolbar) findViewById(R.id.toolbarProfile);

        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setNavigationDrawer();
        initializeProfileUser();
        final Button SignOut = findViewById(R.id.signOut_button);
        findViewById(R.id.signOut_button).setOnClickListener(new View.OnClickListener() {
            /**
             * If is clicked the sign out button it executes signOut
             * @param v View which is clicked
             * @see ProfileActivity#signOut(GoogleSignInClient)
             */
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.signOut_button:
                        signOut(LoggedUser.getInstance().getmGoogleSignInClient());
                        break;
                }
            }
        });
    }

    /**
     * Logs out the user.
     * It redirects to the login screen.
     * @param mGoogleSignInClient client from which it closes the session
     * @see LoginActivity
     */
    private void signOut(GoogleSignInClient mGoogleSignInClient) {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, task -> {});
        revokeAccess(mGoogleSignInClient);
        Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(loginIntent);
    }

    /**
     * It revokes the access of the google account from the app.
     * @param mGoogleSignInClient client from which it revokes the access
     */
    private void revokeAccess(GoogleSignInClient mGoogleSignInClient) {
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(this, task -> {});
    }


    /**
     * It initializes all elements of the profile screen.
     * It loads the profile picture, the name and email of the google user. And also it loads
     * user's score and number of obstacles created.
     */
    private void initializeProfileUser() {
        ImageView pic = findViewById(R.id.pictureProfile);
        TextView name = findViewById(R.id.nameProfile);
        TextView punts = findViewById(R.id.pointsUser);
        TextView obs = findViewById(R.id.obstaclesUser);
        TextView mail = findViewById(R.id.userMail);

        name.setText(LoggedUser.getInstance().getmUserAccount().getDisplayName());
        mail.setText(LoggedUser.getInstance().getmUserAccount().getEmail());
        Drawable d;
        CreateImageFromUrlTask pictask = new CreateImageFromUrlTask(getApplicationContext());
        pictask.execute(LoggedUser.getInstance().getmUserAccount().getPhotoUrl().toString());
        try {
            d = pictask.get();
            pic.setImageDrawable(d);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        GetSingleUserTask puntstask = new GetSingleUserTask((getApplicationContext()));
        puntstask.execute(LoggedUser.getInstance().getId());
        try {
            String s = puntstask.get();
            JSONObject json = new JSONObject(s);
            punts.setText(json.getString("puntuacio"));
            Object n = json.get("obstaclesIds");
            String obstacle = n.toString();
            char[] obstacles = obstacle.toCharArray();
            int count = 1;
            for (char o : obstacles) {
                if (o == ',') count++;
            }
            obs.setText(String.valueOf(count));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * Defines the functionality when the back button is pressed.
     * It opens the {@link DrawerLayout}
     */
    @Override
    public void onBackPressed() {
        if (!dLayout.isDrawerOpen(GravityCompat.START)) dLayout.openDrawer(GravityCompat.START);
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
        navView.getMenu().getItem(1).setChecked(true);
        navView.setNavigationItemSelectedListener(menuItem -> {
            loadingDrawer = findViewById(R.id.loadingDrawer);
            drawerHeader = findViewById(R.id.imageHeader);
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
                    Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                default:
                    return false;
            }
        });
    }
}
