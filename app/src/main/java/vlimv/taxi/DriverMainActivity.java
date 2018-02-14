package vlimv.taxi;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

public class DriverMainActivity extends AppCompatActivity implements ContainerFragment.TabLayoutSetupCallback,
        PageFragment.OnListItemClickListener, SettingsFragment.OnFragmentInteractionListener,
        View.OnClickListener, CarOptionsFragment.OnFragmentInteractionListener,
        CabinetFragment.OnFragmentInteractionListener, SupportFragment.OnFragmentInteractionListener,
        NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout mDrawerLayout;
    public static TextView free, busy;
    public static TabLayout tabLayout;
    public static Button next_btn;
    public static Toolbar toolbar;
    private NavigationView nvDrawer;
    String status;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");


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
        View navHeader = nvDrawer.getHeaderView(0);
        TextView name = navHeader.findViewById(R.id.name);
        name.setText(loadName());
        next_btn = findViewById(R.id.button);
        next_btn.setVisibility(View.GONE);
        free = findViewById(R.id.free);
        free.setVisibility(View.VISIBLE);
        busy = findViewById(R.id.busy);
        busy.setVisibility(View.VISIBLE);
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

    String loadName() {
        SharedPreferences name = getSharedPreferences("NAME", 0);
        String name_text = name.getString("NAME", "bongo bongo");
        Toast.makeText(getApplicationContext(), name_text, Toast.LENGTH_SHORT).show();
        return name_text;
    }

    void startOrder() {
        Intent intent = new Intent(this, DriverOrderActivity.class);
        startActivity(intent);

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
                    startOrder();
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }
}