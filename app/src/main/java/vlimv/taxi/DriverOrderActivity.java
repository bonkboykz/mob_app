package vlimv.taxi;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class DriverOrderActivity extends AppCompatActivity implements SettingsFragment.OnFragmentInteractionListener,
        CarOptionsFragment.OnFragmentInteractionListener,
        CabinetFragment.OnFragmentInteractionListener, SupportFragment.OnFragmentInteractionListener,
        NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout mDrawerLayout;
    public static Button next_btn;
    public static Toolbar toolbar;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_order);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");


//        if (savedInstanceState == null) {
//            FragmentManager fragmentManager = getSupportFragmentManager();
//            fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//            FragmentTransaction transaction = fragmentManager.beginTransaction();
//            transaction.replace(R.id.flContent, new ContainerFragment());
//            transaction.commit();
//        }
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mActionBarDrawerToggle.syncState();
        mActionBarDrawerToggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.colorPrimary));
        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);
        nvDrawer = findViewById(R.id.nav_view);
        nvDrawer.setNavigationItemSelectedListener(this);
        View navHeader = nvDrawer.getHeaderView(0);
//        TextView name = navHeader.findViewById(R.id.name);
//        name.setText(loadName());
        next_btn = findViewById(R.id.button);
        next_btn.setVisibility(View.GONE);
    }
    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        Class fragmentClass = null;
        switch(item.getItemId()) {
            case R.id.nav_city:
                //fragmentClass = FragmentNavCity.class;
                break;
            case R.id.nav_cabinet:
                fragmentClass = CabinetFragment.class;
                switchFragment(fragmentClass, fragment);
                break;
            case R.id.nav_car_options:
                fragmentClass = CarOptionsFragment.class;
                switchFragment(fragmentClass, fragment);
                break;
            case R.id.nav_support:
                fragmentClass = SupportFragment.class;
                switchFragment(fragmentClass, fragment);
                break;
            case R.id.nav_settings:
                fragment = new SettingsFragment();
                switchFragment(fragmentClass, fragment);
                break;
            case R.id.nav_client_mode:
                //switch mode
                break;
            default:
                break;
        }

        return true;
    }
    @Override
    public void onFragmentInteraction(Uri uri){
        //you can leave it empty
    }

    void switchFragment(Class fragmentClass, Fragment fragment) {
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

//    String loadName() {
//        SharedPreferences name = getSharedPreferences("NAME", 0);
//        String name_text = name.getString("NAME", "bongo bongo");
//        Toast.makeText(getApplicationContext(), name_text, Toast.LENGTH_SHORT).show();
//        return name_text;
//    }
}
