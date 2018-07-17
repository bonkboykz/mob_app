package vlimv.taxi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AddressFromMapActivity extends AppCompatActivity implements OnMapReadyCallback{
    private GoogleMap map;
    Marker marker;
    Button btn_ready;
    SupportMapFragment mapFragment;
    GeoDataClient mGeoDataClient;
    Geocoder geocoder;
    PlaceDetectionClient mPlaceDetectionClient;
    FusedLocationProviderClient mFusedLocationProviderClient;
    private final double DEF_LAT = 45.017711;
    private final double DEF_LNG = 78.380442;
    private final LatLng mDefaultLocation = new LatLng(DEF_LAT, DEF_LNG);
    private static final int DEFAULT_ZOOM = 17;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;
    private Location mLastKnownLocation;
    private static boolean mLocationPermissionResolved = true;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    String origin = "FROM";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_from_map);

//        Intent intent = getIntent();
//        Bundle bundle = intent.getExtras();
//        if (bundle != null){
//            origin = bundle.getString("KEY");
//        }

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment == null) {
            Log.d("MAPFRAFMENT", "null");
        }
        geocoder = new Geocoder(this, Locale.getDefault());
        mapFragment.getMapAsync(this);
        mGeoDataClient = Places.getGeoDataClient(this, null);

        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        btn_ready = findViewById(R.id.btn_ready);
        btn_ready.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Address> addresses = null;
                LatLng latLng = marker.getPosition();
                String errorMessage = "";
                String address_text = "";
                try {
                    addresses = geocoder.getFromLocation(latLng.latitude,
                            latLng.longitude,1);
                } catch (IOException ioException) {
                    // Catch network or other I/O problems.
                    errorMessage = ("Service not available");
                    Log.e("Location ERROR", errorMessage, ioException);
                } catch (IllegalArgumentException illegalArgumentException) {
                    // Catch invalid latitude or longitude values.
                    errorMessage = "Invalid latlong values";
                    Toast.makeText(view.getContext(), "Неправильный адрес. Попробуйте еще раз", Toast.LENGTH_LONG).show();
                    Log.e("Location ERROR", errorMessage + ". " +
                            "Latitude = " + latLng.latitude +
                            ", Longitude = " +
                            latLng.longitude, illegalArgumentException);
                }

                // Handle case where no address was found.
                if (addresses == null || addresses.size()  == 0) {
                    if (errorMessage.isEmpty()) {
                        Toast.makeText(view.getContext(), "Неправильный адрес. Попробуйте еще раз", Toast.LENGTH_LONG).show();
                        errorMessage = "No address found";
                        Log.e("Location ERROR", errorMessage);
                    }
                    //deliverResultToReceiver(SyncStateContract.Constants.FAILURE_RESULT, errorMessage);
                } else {
                    Address address = addresses.get(0);
                    address_text = addresses.get(0).getAddressLine(0);
                    ArrayList<String> addressFragments = new ArrayList<String>();
                    for(int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                        addressFragments.add(address.getAddressLine(i));
                    }
//                    deliverResultToReceiver(Constants.SUCCESS_RESULT,
//                            TextUtils.join(System.getProperty("line.separator"),
//                                    addressFragments));
                }
                Intent intent = new Intent();
                intent.putExtra("LAT", latLng.latitude);
                intent.putExtra("LNG", latLng.longitude);
                intent.putExtra("ADDRESS", address_text);
                setResult(Activity.RESULT_OK, intent);
                finish();

                //Toast.makeText(getApplicationContext(), address_text, Toast.LENGTH_SHORT).show();
            }
        });

    }
    public static boolean isLocationEnabled(Context context) {
        String locationProviders;
        locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        return !TextUtils.isEmpty(locationProviders);
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (map != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, map.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }
    @Override
    public void onMapReady(GoogleMap mMap) {
        map = mMap;
        // Use a custom info window adapter to handle multiple lines of text in the
        // info window contents.
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
//        map.setLatLngBoundsForCameraTarget(new LatLngBounds(
//                new LatLng(43.143121, 76.691608),
//                new LatLng(43.396356, 77.134495)));
        map.setMinZoomPreference(12.0f);

        // Prompt the user for permission.
        getLocationPermission();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();

        //Let user choose place
        choosePlace();
    }
    private void choosePlace() {
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                if(marker != null) {
                    marker.setPosition(point);
                } else {
                    marker = map.addMarker(new MarkerOptions()
                            .position(point)
                            .title("Начальный адрес"));
                }
                String address = "lat: " + point.latitude + ", long: " + point.longitude;

                btn_ready.setBackground(getDrawable(R.drawable.ripple_effect_square));
                btn_ready.setEnabled(true);
                //Toast.makeText(getApplicationContext(), address, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        if(isLocationEnabled(this)) {
            try {
                if (mLocationPermissionGranted) {
                    Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                    locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if (task.isSuccessful()) {
                                // Set the map's camera position to the current location of the device.
                                mLastKnownLocation = task.getResult();
                                if (mLastKnownLocation != null) {
                                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                            new LatLng(mLastKnownLocation.getLatitude(),
                                                    mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                                }
                            } else {
                                Log.d("TAG", "Current location is null. Using defaults.");
                                Log.e("TAG", "Exception: %s", task.getException());
                                map.moveCamera(CameraUpdateFactory
                                        .newLatLngZoom(new LatLng(DEF_LAT, DEF_LNG), DEFAULT_ZOOM));
                                map.getUiSettings().setMyLocationButtonEnabled(false);
                            }
                        }
                    });
                }
            } catch (SecurityException e)  {
                Log.e("Exception: %s", e.getMessage());
            }
        }
        else {
            DialogLocation d = new DialogLocation(this);
            d.showDialog(this);
            Toast.makeText(this, "Услуги геолокации отключены.", Toast.LENGTH_SHORT).show();
        }


    }
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            if (!mLocationPermissionResolved) {
                mLocationPermissionResolved = false;
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        mLocationPermissionResolved = true;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }
    private void updateLocationUI() {
        if (map == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                map.setMyLocationEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }
    public class DialogLocation extends android.app.Dialog {
        public DialogLocation(Activity a) {
            super(a);
        }

        public void showDialog(Activity activity) {
            final DialogLocation dialog = new DialogLocation(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.dialog);

            TextView text_cancel = dialog.findViewById(R.id.text_cancel);
            TextView text_turn_on = dialog.findViewById(R.id.text_turn_on);
            text_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    map.moveCamera(CameraUpdateFactory
                            .newLatLngZoom(new LatLng(DEF_LAT, DEF_LNG), DEFAULT_ZOOM));
                }
            });
            text_turn_on.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }
}
