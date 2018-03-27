package vlimv.taxi;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import static vlimv.taxi.DriverActivity.driver;

public class DriverMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        SettingsFragment.OnFragmentInteractionListener, CarOptionsFragment.OnFragmentInteractionListener,
        CabinetFragment.OnFragmentInteractionListener, SupportFragment.OnFragmentInteractionListener,
        DriverTakeOrderFragment.OnFragmentInteractionListener, View.OnClickListener {
    private DrawerLayout mDrawerLayout;
    public static Toolbar toolbar;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle mActionBarDrawerToggle;

    public static Button next_btn;
    public static TextView free, busy;
    String status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mActionBarDrawerToggle.syncState();
        mActionBarDrawerToggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.colorPrimary));
        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);
        nvDrawer = findViewById(R.id.nav_view);
        nvDrawer.setNavigationItemSelectedListener(this);
        View navHeader = nvDrawer.getHeaderView(0);

        TextView name = navHeader.findViewById(R.id.name);
        name.setText(Driver.name + " " + Driver.surname);

        next_btn = findViewById(R.id.button);
        next_btn.setVisibility(View.GONE);
        free = findViewById(R.id.free);
        free.setVisibility(View.VISIBLE);
        busy = findViewById(R.id.busy);
        busy.setVisibility(View.VISIBLE);
        free.setOnClickListener(this);
        busy.setOnClickListener(this);
        displaySelectedScreen(R.id.nav_city);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            DialogQuitApp d = new DialogQuitApp(this);
            d.showDialog(this);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        displaySelectedScreen(item.getItemId());
        return true;
    }
    @Override
    public void onFragmentInteraction(Uri uri){
        //you can leave it empty
    }

    private void displaySelectedScreen(int itemId) {

        //creating fragment object
        Fragment fragment = null;
        //initializing the fragment object which is selected
        switch (itemId) {
            case R.id.nav_city:
                fragment = new DriverTakeOrderFragment();
                break;
            case R.id.nav_cabinet:
                fragment = new CabinetFragment();
                break;
            case R.id.nav_car_options:
                fragment = new CarOptionsFragment();
                break;
            case R.id.nav_support:
                fragment = new SupportFragment();
                break;
            case R.id.nav_settings:
                fragment = new SettingsFragment();
                break;
            default:
                break;
        }

        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flContent, fragment);
            ft.commit();
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.free:
                status = "free";
                free.setBackground(getResources().getDrawable(R.drawable.ripple_effect_square));
                free.setTextColor(Color.parseColor("#ffffff"));
                free.setElevation(5.0f);
                busy.setBackground(getResources().getDrawable(R.drawable.ripple_effect_square_white_stroke));
                busy.setTextColor(Color.parseColor("#000000"));
                busy.setElevation(0.0f);
                Toast.makeText(this, status, Toast.LENGTH_LONG).show();
                break;
            case R.id.busy:
                status = "busy";
                free.setBackground(getResources().getDrawable(R.drawable.ripple_effect_square_white_stroke));
                free.setTextColor(Color.parseColor("#000000"));
                free.setElevation(0.0f);
                busy.setBackground(getResources().getDrawable(R.drawable.ripple_effect_square));
                busy.setTextColor(Color.parseColor("#ffffff"));
                busy.setElevation(5.0f);
                Toast.makeText(this, status, Toast.LENGTH_LONG).show();
                break;
        }
    }

}