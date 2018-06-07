package vlimv.taxi;

import android.content.Intent;
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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class DriverOrderActivity extends AppCompatActivity implements
        DriverCityFragment.OnFragmentInteractionListener, SettingsFragment.OnFragmentInteractionListener,
        CarOptionsFragment.OnFragmentInteractionListener, CabinetFragment.OnFragmentInteractionListener,
        SupportFragment.OnFragmentInteractionListener, NavigationView.OnNavigationItemSelectedListener,
        ServerRequest.NextActivity, ServerRequest.UpdateCarInfo {
    private DrawerLayout mDrawerLayout;
    public static Toolbar toolbar;
    static CountDownTimer timer;
    private NavigationView nvDrawer;
    static ActionBarDrawerToggle mActionBarDrawerToggle;

    Fragment fragment;

    private String mTripId, mTripPrice, mTripTo, mTripFrom;

    static String orderState = "new";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_order);
        Intent i = getIntent();
        Bundle extras = i.getExtras();
        mTripId = extras.getString("TRIP_ID");
        mTripPrice = extras.getString("TRIP_PRICE");
        mTripTo = extras.getString("TRIP_TO");
        mTripFrom = extras.getString("TRIP_FROM");
//        if (savedInstanceState != null) mTripId = savedInstanceState.getString("TRIP_ID");
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
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mActionBarDrawerToggle.syncState();
        mActionBarDrawerToggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);
        nvDrawer = findViewById(R.id.nav_view);
        nvDrawer.setNavigationItemSelectedListener(this);

        View navHeader = nvDrawer.getHeaderView(0);
        TextView name = navHeader.findViewById(R.id.name);
        //name.setText(Driver.name + " "  + Driver.surname);

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

    public void lockDrawer() {
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }
    public void unlockDrawer() {
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    private void displaySelectedScreen(int itemId) {
        fragment = null;
        //initializing the fragment object which is selected
        switch (itemId) {
            case R.id.nav_city:
                mActionBarDrawerToggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
                Bundle bundle = new Bundle();
                bundle.putString("TRIP_ID", mTripId);
                bundle.putString("TRIP_PRICE", mTripPrice);
                bundle.putString("TRIP_TO", mTripTo);
                bundle.putString("TRIP_FROM", mTripFrom);
                Log.d("DriverOrderActivity", "tripId: " + mTripId);
                Log.d("DriverOrderActivity", "tripPrice: " + mTripPrice);
                Log.d("DriverOrderActivity", "tripTo: " + mTripTo);

                fragment = new DriverCityFragment();
                fragment.setArguments(bundle);
                break;
            case R.id.nav_cabinet:
                mActionBarDrawerToggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.colorPrimary));
                fragment = new CabinetFragment();
                break;
            case R.id.nav_car_options:
                mActionBarDrawerToggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.colorPrimary));
                fragment = new CarOptionsFragment();
                break;
            case R.id.nav_support:
                mActionBarDrawerToggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.colorPrimary));
                fragment = new SupportFragment();
                break;
            case R.id.nav_settings:
                mActionBarDrawerToggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.colorPrimary));
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
    static void startTimer(long timeInMillis) {
        DriverCityFragment.time.setTextSize(36.0f);
        timer = new CountDownTimer(timeInMillis, 1000) {
            public void onTick(long millisUntilFinished) {
                long timeInSec = millisUntilFinished / 1000;
                String time_text = timeInSec / 60 + " : ";
                if (timeInSec % 60 < 10) {
                    time_text += "0";
                }
                time_text += timeInSec % 60;
                DriverCityFragment.time.setText(time_text);
            }

            public void onFinish() {
                DriverCityFragment.time.setText("done!");
            }
        }.start();
    }

    @Override
    public void updateCarInfo(String name, String model, String type, String gosNumber, int year) {
        CarOptionsFragment fr = (CarOptionsFragment) fragment;
        fr.spinner_car.setText(name);
        fr.spinner_car_model.setText(model);
        fr.spinner_car_type.setText(type);
        fr.numberEditText.setText(gosNumber);
        fr.yearEditText.setText(year + "");
//        fr.spinner_car.setEnabled(true);
//        fr.spinner_car_model.setEnabled(true);
//        fr.spinner_car_type.setEnabled(true);
    }

    @Override
    public void goNext() {
        Toast.makeText(this, "Информация успешно обновлена.", Toast.LENGTH_LONG).show();
        try {
            CarOptionsFragment fr = (CarOptionsFragment) fragment;
            fr.progressBar.hideNow();
            fr.next_btn.setVisibility(View.VISIBLE);
        } catch (ClassCastException e) {
            Log.e("goNext", "Oops");
        }
    }

    @Override
    public void tryAgain() {
        Toast.makeText(this, "Произошла ошибка. Повторите попытку.", Toast.LENGTH_LONG).show();
        try {
            CarOptionsFragment fr = (CarOptionsFragment) fragment;
            fr.progressBar.hideNow();
            fr.next_btn.setVisibility(View.VISIBLE);
        } catch (ClassCastException e) {
            Log.e("goNext", "Oops");
        }
    }
}
