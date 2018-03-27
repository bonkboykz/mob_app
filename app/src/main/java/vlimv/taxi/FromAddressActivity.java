package vlimv.taxi;

import android.app.Activity;
import android.content.Intent;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

public class FromAddressActivity extends AppCompatActivity implements View.OnClickListener {
    Place placeFrom;
    TextView showOnMap, favorites;
    String addressText;

    final double DEF_LAT = 43.143121;
    final double DEF_LNG = 76.889709;
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
        final Activity activity = this;
        final Geocoder geocoder = new Geocoder(getBaseContext());
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(2)
                .setCountry("KZ")
                .build();
        autocompleteFragment.setFilter(typeFilter);
        autocompleteFragment.setHint("Где вы находитесь?");
        latLng = new LatLng(DEF_LAT, DEF_LNG);
        //здесь я ставлю ограничения по координатам
        //первый латлнг юго-западный угол
        //вторйо северо-восточный
        autocompleteFragment.setBoundsBias(new LatLngBounds(
                new LatLng(43.143121, 76.691608),
                new LatLng(43.396356, 77.134495)));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                addressText = place.getName().toString();
                placeFrom = place;
                latLng = placeFrom.getLatLng();
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
                    PassengerMapsFragment.pointA.setText(addressText);
                if(PassengerMapsFragment.markerFrom != null) {
                    PassengerMapsFragment.markerFrom.setPosition(latLng);
                } else {
                    PassengerMapsFragment.markerFrom = PassengerMapsFragment.map.
                            addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .title("Начальный адрес")
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_blue_marker)));
                }
                PassengerMapsFragment.map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        latLng, 17));
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
    public void onPause() {
        super.onPause();
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {return;}
        if (resultCode == Activity.RESULT_OK && requestCode == 1) {
            double lat = data.getDoubleExtra("LAT", DEF_LAT);
            double lng = data.getDoubleExtra("LNG", DEF_LAT);
            latLng  = new LatLng(lat, lng);
            addressText = data.getStringExtra("ADDRESS");
            PassengerMapsFragment.pointA.setText(addressText);
            if (PassengerMapsFragment.markerFrom != null) {
                PassengerMapsFragment.markerFrom.setPosition(latLng);
            } else {
                PassengerMapsFragment.markerFrom = PassengerMapsFragment.map.
                        addMarker(new MarkerOptions()
                                .position(latLng)
                                .title("Начальный адрес")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_blue_marker)));
            }
            PassengerMapsFragment.map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    latLng, 17));
            finish();
        } else if (resultCode == Activity.RESULT_CANCELED) {
            // some stuff that will happen if there's no result
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }
}
