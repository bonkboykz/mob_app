package vlimv.taxi;

import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Response;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class FromAddressActivity extends AppCompatActivity implements View.OnClickListener {
    EditText address;
    ImageButton search_btn;
    TextView locationTV, showOnMap, favorites;
    String addressText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_from_address);
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        //address = findViewById(R.id.address);
        search_btn = findViewById(R.id.search);
        locationTV = findViewById(R.id.location);
        showOnMap = findViewById(R.id.showOnMap);
        showOnMap.setOnClickListener(this);
        favorites = findViewById(R.id.favorites);
        favorites.setOnClickListener(this);
        Button btn_next = findViewById(R.id.btn_next);
        final Activity activity = this;
        final Geocoder geocoder = new Geocoder(getBaseContext());
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setHint("Где вы находитесь?");
        autocompleteFragment.setBoundsBias(new LatLngBounds(
                new LatLng(43.143121, 76.691608),
                new LatLng(43.396356, 77.134495)));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
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
                String g = address.getText().toString();
                getLocationInfo(g);
                List<Address> addresses = null;
                String errorMessage = "";
                String address_text = "";
//                try {
//                    addresses = geocoder.getFromLocationName(g, 5, 43.163659, 76.748600, 43.370656, 77.075443);
//                } catch (IOException ioException) {
//                    // Catch network or other I/O problems.
//                    errorMessage = ("Service not available");
//                    Log.e("Location ERROR", errorMessage, ioException);
//                } catch (IllegalArgumentException illegalArgumentException) {
//                    // Catch invalid latitude or longitude values.
//                    errorMessage = "Invalid address values";
//                    Log.e("Location ERROR", "invalid address: " + g);
//                }
//
//                // Handle case where no address was found.
//                if (addresses == null || addresses.size() == 0) {
//                    if (errorMessage.isEmpty()) {
//                        errorMessage = "No address found";
//                        Log.e("Location ERROR", errorMessage);
//                        Toast.makeText(getBaseContext(), errorMessage, Toast.LENGTH_SHORT);
//                    }
//                    //deliverResultToReceiver(SyncStateContract.Constants.FAILURE_RESULT, errorMessage);
//                } else {
//                    if (addresses.size() > 0) {
//                        DialogChoose d = new DialogChoose(activity, addresses);
//                        d.showDialog(activity);
//                    } else {
//                        Address address = addresses.get(0);
//                        address_text = addresses.get(0).getAddressLine(0);
//                        ArrayList<String> addressFragments = new ArrayList<String>();
//                        for(int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
//                            addressFragments.add(address.getAddressLine(i));
//                        }
//                    }

//                    deliverResultToReceiver(Constants.SUCCESS_RESULT,
//                            TextUtils.join(System.getProperty("line.separator"),
//                                    addressFragments));
                }

 //               Toast.makeText(getApplicationContext(), address_text, Toast.LENGTH_SHORT).show();
//                Geocoder geocoder = new Geocoder(getBaseContext());
//                List<Address> addresses = null;
//                try {
//                    addresses = geocoder.getFromLocationName(g, 3);
//                    if (addresses != null && !addresses.equals(""))
//                        search(addresses);
//                } catch (Exception e) {
//
//                }
                //Toast.makeText(getBaseContext(), addressText, Toast.LENGTH_SHORT).show();
            //}
        });

    }
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.showOnMap:
                Intent intentMap = new Intent(getApplicationContext(), AddressFromMapActivity.class);
                startActivity(intentMap);
                break;
            case R.id.favorites:
                Intent intentFav = new Intent (getApplicationContext(), FavoritesFragment.class);
                startActivity(intentFav);
                break;
            default:
                break;
        }
    }
    public void getLocationInfo(String address) {
        //Toast.makeText(getBaseContext(), "location info", Toast.LENGTH_SHORT).show();
        String url = "https://maps.googleapis.com/maps/api/geocode/json?address="
                + Uri.encode(address) + "&sensor=true&key=AIzaSyByyy9enHivlSwNtdH1OOG_yodGoK7W6Mc";
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest stateReq = new JsonObjectRequest(Request.Method.GET, url,
                null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONObject location;
                try {
                    location = response.getJSONArray("results").getJSONObject(0).
                            getJSONObject("geometry").getJSONObject("location");
                    if (location.getDouble("lat") != 0 && location.getDouble("lng") != 0) {
                        LatLng latLng = new LatLng(location.getDouble("lat"), location.getDouble("lng"));
                        String address = location.getString("formatted_address");

                        Toast.makeText(getApplicationContext(), address, Toast.LENGTH_SHORT).show();

                        //Do what you want
                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();

                }
            }

        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error.Response", error.toString());
            }
        });
        // add it to the queue
        queue.add(stateReq);
    }
    protected void search(List<Address> addresses) {

        Address address = addresses.get(0);
        double home_long = address.getLongitude();
        double home_lat = address.getLatitude();
        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

        addressText = String.format(
                "%s, %s",
                address.getMaxAddressLineIndex() > 0 ? address
                        .getAddressLine(0) : "", address.getCountryName());

//        MarkerOptions markerOptions = new MarkerOptions();
//
//        markerOptions.position(latLng);
//        markerOptions.title(addressText);
//
//        PassengerMapsFragment.map.clear();
//        PassengerMapsFragment.map.addMarker(markerOptions);
//        PassengerMapsFragment.map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//        PassengerMapsFragment.map.animateCamera(CameraUpdateFactory.zoomTo(15));
        locationTV.setText("Latitude:" + address.getLatitude() + ", Longitude:"
                + address.getLongitude());


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
    public class DialogChoose extends Dialog {
        List<Address> addresses;
        List<String> addresses_text = new ArrayList<>();
        public DialogChoose(Activity a, List<Address> addresses) {
            super(a);
            this.addresses = addresses;
        }

        public void showDialog(Activity activity) {
            final DialogChoose dialog = new DialogChoose(activity, this.addresses);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.dialog_choose);
//            for (int i = 0; i < addresses.size(); i++) {
//                Address address = addresses.get(i);
//                addresses_text.add(address.getAddressLine(0));
//            }
            LinearLayout linearLayout = findViewById(R.id.layout);
            TextView textView = new TextView(getApplicationContext());
            textView.setText("simple text");
            linearLayout.addView(textView);
//            for(int i = 0; i < addresses_text.size(); i++ )
//            {
//                TextView textView = new TextView(getContext());
//                textView.setText(addresses_text.get(i));
//                linearLayout.addView(textView);
//            }
            dialog.show();
        }
    }
}
