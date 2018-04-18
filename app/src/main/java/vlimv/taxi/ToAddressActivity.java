package vlimv.taxi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

public class ToAddressActivity extends AppCompatActivity implements View.OnClickListener {
    TextView showOnMap, favorites;
    String addressText;
    Place placeTo;
    PlaceAutocompleteFragment autocompleteFragment;

    final double DEF_LAT = 45.017711;
    final double DEF_LNG = 78.380442;
    LatLng latLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_from_address);

        showOnMap = findViewById(R.id.showOnMap);
        showOnMap.setOnClickListener(this);
        favorites = findViewById(R.id.favorites);
        favorites.setOnClickListener(this);
        Button btn_next = findViewById(R.id.btn_next);
        autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(2)
                .setCountry("KZ") //поставила фильтр на страну
                .build();
        autocompleteFragment.setFilter(typeFilter);
        autocompleteFragment.setHint("Куда вы едете?");
        latLng = new LatLng(DEF_LAT, DEF_LNG);
//        autocompleteFragment.setBoundsBias(new LatLngBounds(
//                new LatLng(43.143121, 76.691608),
//                new LatLng(43.396356, 77.134495)));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                addressText = place.getName().toString();
                placeTo = place;
                Log.i("TAG", "Place: " + place.getName());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("TAG", "An error occurred: " + status);
            }
        });
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (addressText != null)
                    PassengerCityFragment.pointB.setText(addressText);
                if (PassengerCityFragment.markerTo != null) {
                    PassengerCityFragment.markerTo.setPosition(latLng);
                } else {
                    PassengerCityFragment.markerTo = PassengerCityFragment.map.
                            addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .title("Начальный адрес")
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_green_marker)));
                }
                PassengerCityFragment.map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        latLng, 17));

//                PassengerCityFragment.pointB.setText(addressText);
//                PassengerCityFragment.to = placeTo;
//                if(PassengerCityFragment.markerTo != null) {
//                    PassengerCityFragment.markerTo.setPosition(placeTo.getLatLng());
//                } else {
//                    PassengerCityFragment.markerTo = PassengerCityFragment.map.
//                            addMarker(new MarkerOptions()
//\
//                            .position(placeTo.getLatLng())
//                                    .title("Начальный адрес")
//                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_green_marker)));
//                }
//                PassengerCityFragment.map.moveCamera(CameraUpdateFactory.newLatLngZoom(
//                        placeTo.getLatLng(), 17));
                finish();
            }
        });

    }
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.showOnMap:
                Intent intentMap = new Intent(getApplicationContext(), AddressFromMapActivity.class);
                startActivityForResult(intentMap, 1);
                break;
            case R.id.favorites:
                Intent intentFav = new Intent (getApplicationContext(), AddressFromFavoritesActivity.class);
                startActivity(intentFav);
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {return;}
        if (resultCode == Activity.RESULT_OK && requestCode == 1) {
            double lat = data.getDoubleExtra("LAT", DEF_LAT);
            double lng = data.getDoubleExtra("LNG", DEF_LAT);
            latLng  = new LatLng(lat, lng);
            addressText = data.getStringExtra("ADDRESS");
            PassengerCityFragment.pointB.setText(addressText);
            if (PassengerCityFragment.markerTo != null) {
                PassengerCityFragment.markerTo.setPosition(latLng);
            } else {
                PassengerCityFragment.markerTo = PassengerCityFragment.map.
                        addMarker(new MarkerOptions()
                                .position(latLng)
                                .title("Конечный адрес")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_green_marker)));
            }
            PassengerCityFragment.map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    latLng, 17));
            finish();
        } else if (resultCode == Activity.RESULT_CANCELED) {
            // some stuff that will happen if there's no result
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }
}
