package vlimv.taxi;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class PassengerMainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener, FavoritesFragment.OnFragmentInteractionListener,
        PassengerCityFragment.OnFragmentInteractionListener, SettingsFragment.OnFragmentInteractionListener,
        SupportFragment.OnFragmentInteractionListener, ServerRequest.NextActivity {

    //drawer variables
    private static Activity mActivity;
    private DrawerLayout mDrawerLayout;
    public static Toolbar toolbar;
    private static NavigationView nvDrawer;
    static ActionBarDrawerToggle mActionBarDrawerToggle;
    Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        if (savedInstanceState != null) {
            //Restore the fragment's instance
            Log.d("Activity load fragment", "");
            fragment = getSupportFragmentManager().getFragment(savedInstanceState, "PassengerCityFragment");
        }
        setContentView(R.layout.activity_passenger_maps);
        //toolbar and nav drawer
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ServerRequest.getInstance(this).getUser(SharedPref.loadToken(this), this, 0);
        getSupportActionBar().setTitle("");
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
        String nameText = SharedPref.loadUserName(this) + " " + SharedPref.loadUserSurname(this);
        name.setText(nameText);

        displaySelectedScreen(R.id.nav_city);

    }

    public static void setName(final String name, final String lname) {
        if (mActivity != null) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    View navHeader = nvDrawer.getHeaderView(0);
                    TextView fullName = navHeader.findViewById(R.id.name);
                    fullName.setText(name + " " + lname);
                }
            });
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        Bundle outState = new Bundle();
        getSupportFragmentManager().putFragment(outState, "PassengerCityFragment", fragment);

    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        Log.d("Activity onSaveInstance", outState.toString());
        getSupportFragmentManager().putFragment(outState, "PassengerCityFragment", fragment);
    }
    @Override
    public void tryAgain() {
        Toast.makeText(this, "Произошла ошибка. Попробуйте еще раз.", Toast.LENGTH_LONG).show();
        PassengerCityFragment fr = (PassengerCityFragment) fragment;
        fr.progressBar.hideNow();
        fr.order_btn.setClickable(true);
        fr.order_btn.setVisibility(View.VISIBLE);
    }
    @Override
    public void goNext() {
        PassengerCityFragment fr = (PassengerCityFragment) fragment;
        fr.progressBar.hideNow();
        fr.searchDriver();
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

    public void lockDrawer() {
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }
    public void unlockDrawer() {
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        displaySelectedScreen(item.getItemId());
        return true;
    }

    private void displaySelectedScreen(int itemId) {
        //creating fragment object
        fragment = null;
        //initializing the fragment object which is selected
        switch (itemId) {
            case R.id.nav_city:
                fragment = new PassengerCityFragment();
                break;
            case R.id.nav_favorites:
                fragment = new FavoritesFragment();
                break;
            case R.id.nav_trips:
                fragment = new CompletedOrdersFragment();
                break;
            case R.id.nav_support:
                fragment = new SupportFragment();
                break;
            case R.id.nav_settings:
                fragment = new SettingsFragment();
                break;

            default:
                break;
            //fragmentClass = FirstFragment.class;
        }

        //replacing the fragment
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flContent, fragment);
            ft.commit();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    @Override
    public void onFragmentInteraction(Uri uri){
        //you can leave it empty
    }
}
