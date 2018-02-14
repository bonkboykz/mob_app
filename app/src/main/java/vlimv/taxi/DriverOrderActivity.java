package vlimv.taxi;

import android.graphics.Color;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DriverOrderActivity extends AppCompatActivity implements View.OnClickListener,
        SettingsFragment.OnFragmentInteractionListener,
        CarOptionsFragment.OnFragmentInteractionListener,
        CabinetFragment.OnFragmentInteractionListener, SupportFragment.OnFragmentInteractionListener,
        NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout mDrawerLayout;
    public static Button next_btn;
    public static Toolbar toolbar;

    long timeInMillis;
    View chooseTime, showAddress;
    LinearLayout textLayout;
    FloatingActionButton fab;
    TextView min_5, min_10, min_15, min_20, cancel, time;

    private NavigationView nvDrawer;
    static ActionBarDrawerToggle mActionBarDrawerToggle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_order);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Spannable text = new SpannableString(toolbar.getTitle());
        text.setSpan(new ForegroundColorSpan(Color.WHITE), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        toolbar.setTitle(text);

//        if (savedInstanceState == null) {
//            FragmentManager fragmentManager = getSupportFragmentManager();
//            fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//            FragmentTransaction transaction = fragmentManager.beginTransaction();
//            transaction.replace(R.id.flContent, new ContainerFragment());
//            transaction.commit();
//        }
        chooseTime = findViewById(R.id.choose_time);
        fab = findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
        chooseTime.setVisibility(View.VISIBLE);
        textLayout = findViewById(R.id.text_layout);
        textLayout.setVisibility(View.GONE);

        showAddress = findViewById(R.id.show_address);
        showAddress.setVisibility(View.GONE);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mActionBarDrawerToggle.syncState();
        mActionBarDrawerToggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);
        nvDrawer = findViewById(R.id.nav_view);
        nvDrawer.setNavigationItemSelectedListener(this);
        View navHeader = nvDrawer.getHeaderView(0);
//        TextView name = navHeader.findViewById(R.id.name);
//        name.setText(loadName());
        min_5 = findViewById(R.id.min_5);
        min_10 = findViewById(R.id.min_10);
        min_15 = findViewById(R.id.min_15);
        min_20 = findViewById(R.id.min_20);
        min_5.setOnClickListener(this);
        min_10.setOnClickListener(this);
        min_15.setOnClickListener(this);
        min_20.setOnClickListener(this);

        time = findViewById(R.id.time);

        next_btn = findViewById(R.id.button);
        next_btn.setVisibility(View.GONE);
    }

    @Override
    public void onClick (View v) {
        switch (v.getId()) {
            case R.id.min_5:
                timeInMillis = 5 * 60 * 1000;
                break;
            case R.id.min_10:
                timeInMillis = 10 * 60 * 1000;
                break;
            case R.id.min_15:
                timeInMillis = 15 * 60 * 1000;
                break;
            case R.id.min_20:
                timeInMillis = 20 * 60 * 1000;
                break;
        }
        new CountDownTimer(timeInMillis, 1000) {
            public void onTick(long millisUntilFinished) {
                long timeInSec = millisUntilFinished / 1000;
                String time_text = timeInSec / 60 + " : " + timeInSec % 60;
                time.setText(time_text);
                time.setTextSize(36.0f);
            }

            public void onFinish() {
                time.setText("done!");
            }
        }.start();

        textLayout.setVisibility(View.VISIBLE);
        showAddress.setVisibility(View.VISIBLE);
        chooseTime.setVisibility(View.GONE);
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
                fragment = new CabinetFragment();
                fragmentClass = CabinetFragment.class;
                switchFragment(fragmentClass, fragment);
                break;
            case R.id.nav_car_options:
                fragment = new CarOptionsFragment();
                fragmentClass = CarOptionsFragment.class;
                switchFragment(fragmentClass, fragment);
                break;
            case R.id.nav_support:
                fragment = new SupportFragment();
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
//        try {
//            fragment = (Fragment) fragment.newInstance();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.flContent, fragment).commit();
        chooseTime.setVisibility(View.GONE);
        fab.setVisibility(View.GONE);
        showAddress.setVisibility(View.GONE);

        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

//    String loadName() {
//        SharedPreferences name = getSharedPreferences("NAME", 0);
//        String name_text = name.getString("NAME", "bongo bongo");
//        Toast.makeText(getApplicationContext(), name_text, Toast.LENGTH_SHORT).show();
//        return name_text;
//    }
}
