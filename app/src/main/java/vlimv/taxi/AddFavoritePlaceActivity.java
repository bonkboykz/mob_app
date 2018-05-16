package vlimv.taxi;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;

public class AddFavoritePlaceActivity extends AppCompatActivity {

    private final double DEF_LAT = 45.017711;
    private final double DEF_LNG = 78.380442;
    String addressText;
    LatLng latLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_favorite_place);

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(2)
                .setCountry("KZ")
                .build();
        autocompleteFragment.setFilter(typeFilter);
        autocompleteFragment.setHint("Введите адрес");

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                addressText = place.getName().toString();
                latLng = place.getLatLng();
                Log.i("TAG", "Place: " + place.getName());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("TAG", "An error occurred: " + status);
            }
        });

        final EditText editText = findViewById(R.id.place_name);

        ImageButton backBtn = findViewById(R.id.back_button);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent resultIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, resultIntent);
                finish();
            }
        });

        Button readyBtn = findViewById(R.id.btn_ready);
        readyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String placeName = editText.getText().toString();
                if(latLng != null && !placeName.equals("") && addressText != null) {
                    Intent resultIntent = new Intent();
                    setResult(Activity.RESULT_OK, resultIntent);
                    Address address = new Address(latLng.latitude, latLng.longitude, placeName, addressText);
                    SharedPref.addFavorite(view.getContext(), address);
                    finish();
                } else {
                    Toast.makeText(view.getContext(), "Заполните пустые поля.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
