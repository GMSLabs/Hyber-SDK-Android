package com.hyber.example.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.hyber.Hyber;
import com.hyber.example.AndroidUtilities;
import com.hyber.example.R;
import com.hyber.handler.CurrentUserHandler;
import com.hyber.handler.LogoutUserHandler;
import com.hyber.log.HyberLogger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        MessagesFragment.OnMessagesFragmentInteractionListener,
        DevicesFragment.OnDevicesFragmentInteractionListener,
        SettingsFragment.OnSettingsFragmentInteractionListener {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @BindView(R.id.logout)
    Button logout;

    private TextView name;

    private MessagesFragment mMessagesFragment;
    private DevicesFragment mDevicesFragment;
    private SettingsFragment mSettingsFragment;

    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        HyberLogger.i("I'm alive!");

        AndroidUtilities.checkDisplaySize(this, getResources().getConfiguration());

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        onNavigationItemSelected(navigationView.getMenu().getItem(0));

        View header = navigationView.getHeaderView(0);
        name = (TextView) header.findViewById(R.id.name);

        Hyber.getCurrentUser(new CurrentUserHandler() {
            @Override
            public void onCurrentUser(String id, String phone) {
                MainActivity.this.name.setText(phone);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (checkPlayServices()) {
            Hyber.checkFirebaseToken();
        }
    }

    @OnClick(R.id.logout)
    public void onLogOut() {
        logout.setEnabled(false);
        Hyber.logoutCurrentUser(new LogoutUserHandler() {
            @Override
            public void onSuccess() {
                Intent intent = new Intent(MainActivity.this, SplashActivity.class);
                MainActivity.this.startActivity(intent);
            }

            @Override
            public void onFailure() {
                logout.setEnabled(true);
            }
        });
    }

    @Override
    public void onMessageActionInteraction(@NonNull String action) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(action));
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 1000);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_messages:
                if (mMessagesFragment == null)
                    mMessagesFragment = MessagesFragment.newInstance();
                replaceMainFragment(mMessagesFragment);
                break;
            case R.id.nav_devices:
                if (mDevicesFragment == null)
                    mDevicesFragment = DevicesFragment.newInstance();
                replaceMainFragment(mDevicesFragment);
                break;
            case R.id.nav_settings:
                if (mSettingsFragment == null)
                    mSettingsFragment = SettingsFragment.newInstance();
                replaceMainFragment(mSettingsFragment);
                break;
            default:
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void replaceMainFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_main_container, fragment);
        transaction.commit();
    }

    @Override
    public void onDeviceRevokeRequestActionInteraction(@NonNull String deviceId) {
//        Hyber.deviceRevokeRequest(deviceId);
    }

    @Override
    public void onActionInteraction(@NonNull String action) {

    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                HyberLogger.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

}
