package vlimv.taxi;

import android.app.Activity;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
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
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

public class DriverMainActivity extends AppCompatActivity implements ContainerFragment.TabLayoutSetupCallback,
        PageFragment.OnListItemClickListener, View.OnClickListener, CarOptionsFragment.OnFragmentInteractionListener,
        NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout mDrawerLayout;
    private TextView free, busy;
    public static TabLayout tabLayout;
    private NavigationView nvDrawer;
    String status;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        if (savedInstanceState == null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.flContent, new ContainerFragment());
            transaction.commit();
        }
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mActionBarDrawerToggle.syncState();
        mActionBarDrawerToggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.colorPrimary));
        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);
        nvDrawer = findViewById(R.id.nav_view);
        nvDrawer.setNavigationItemSelectedListener(this);

        free = findViewById(R.id.free);
        busy = findViewById(R.id.busy);
        free.setOnClickListener(this);
        busy.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.free:
                status = "free";
                free.setBackground(getResources().getDrawable(R.drawable.ripple_effect_square));
                free.setTextColor(Color.parseColor("#ffffff"));
                free.setElevation(5.0f);
                busy.setBackground(getResources().getDrawable(R.drawable.ripple_effect_square_white));
                busy.setTextColor(Color.parseColor("#000000"));
                busy.setElevation(0.0f);
                Toast.makeText(
                        getApplicationContext(), status, Toast.LENGTH_LONG
                );
                break;
            case R.id.busy:
                status = "busy";
                free.setBackground(getResources().getDrawable(R.drawable.ripple_effect_square_white));
                free.setTextColor(Color.parseColor("#000000"));
                free.setElevation(0.0f);
                busy.setBackground(getResources().getDrawable(R.drawable.ripple_effect_square));
                busy.setTextColor(Color.parseColor("#ffffff"));
                busy.setElevation(5.0f);
                Toast.makeText(
                        getApplicationContext(), status, Toast.LENGTH_LONG
                );
                break;
        }
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
    public void setupTabLayout(ViewPager viewPager) {
        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setVisibility(View.VISIBLE);
        tabLayout.setupWithViewPager(viewPager);
    }
    @Override
    public void onListItemClick(String title) {
        Toast.makeText(this, title, Toast.LENGTH_SHORT).show();
        Dialog_details dialog = new Dialog_details(this);
        dialog.showDialog(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        Toast.makeText(getApplicationContext(), "listeneer", Toast.LENGTH_SHORT).show();
        Fragment fragment = null;
        Class fragmentClass = null;
        switch(item.getItemId()) {
            case R.id.nav_city:
                //fragmentClass = FragmentNavCity.class;
                break;
            case R.id.nav_cabinet:
                //fragmentClass = SecondFragment.class;
                break;
            case R.id.nav_car_options:
                Toast.makeText(getApplicationContext(), "CAR OPTIONS", Toast.LENGTH_SHORT).show();
                fragmentClass = CarOptionsFragment.class;
                break;
            case R.id.nav_support:
                //fragmentClass = ThirdFragment.class;
                break;
            case R.id.nav_settings:
                //fragmentClass = ThirdFragment.class;
                break;
            case R.id.nav_client_mode:
                //switch mode
                break;
            default:
                //fragmentClass = FirstFragment.class;
        }
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri){
        //you can leave it empty
    }
    public class Dialog_details extends android.app.Dialog {
        public Dialog_details(Activity a) {
            super(a);
        }

        public void showDialog(Activity activity){
            final Dialog_details dialog = new Dialog_details(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.dialog_details);

            TextView text_cancel = dialog.findViewById(R.id.text_cancel);
            TextView text_accept = dialog.findViewById(R.id.text_accept);
            text_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), "Заказ не беру.", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });
            text_accept.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), "Заказ принят.", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }
}