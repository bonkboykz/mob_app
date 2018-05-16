package vlimv.taxi;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.zl.reik.dilatingdotsprogressbar.DilatingDotsProgressBar;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

//основной фрагмент пассажира откуда где происходит создание заказа и поездка

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PassengerCityFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PassengerCityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PassengerCityFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public static GoogleMap map;
    SupportMapFragment mapFragment;
    GeoDataClient mGeoDataClient;
    PlaceDetectionClient mPlaceDetectionClient;
    FusedLocationProviderClient mFusedLocationProviderClient;

    private final double DEF_LAT = 45.017711;
    private final double DEF_LNG = 78.380442;
    private final LatLng mDefaultLocation = new LatLng(DEF_LAT, DEF_LNG);
    private static final int DEFAULT_ZOOM = 17;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    View view;
    private OnFragmentInteractionListener mListener;

    //Layouts
    RelativeLayout layout_from, layout_to, layout_price;
    //TextViews
    static TextView price, pointA, pointB;
    //Places
    static Place from, to;
    //Markers
    static Marker markerFrom, markerTo;
    DilatingDotsProgressBar progressBar;
    TextView finalPrice, leftBtn, rightBtn;

    Button order_btn;
    View horView, linLayout, topLayout, addressLayout;

    FloatingActionButton fabCall;

    String addressFrom, addressTo, comment;

    static boolean isInvalid;

//    private Socket mSocket;

    RelativeLayout mainLayout;
    private DialogPrice d;

    public static final int REQUEST_CODE_FROM = 1;
    public static final int REQUEST_CODE_TO = 2;
    public static final int REQUEST_CODE_COMMENT = 3;

    private static PassengerCityFragment mFragment;

    private static String mTripId;

    public PassengerCityFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PassengerMapsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PassengerCityFragment newInstance(String param1, String param2) {
        PassengerCityFragment fragment = new PassengerCityFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            //Restore the fragment's state here
            Log.d("saved state", savedInstanceState.toString());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        isInvalid = SharedPref.loadUserType(getContext()).equals("invalid");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        TaxiApplication app = (TaxiApplication) getActivity().getApplication();
//        mSocket = app.getSocket();
//        mSocket.on("driver_came", onDriverCame);
//        mSocket.on("trip_accepted", onTripAccepted);
//        mSocket.connect();
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_passenger_maps, container, false);
        mainLayout = view.findViewById(R.id.parent_layout);
        linLayout = view.findViewById(R.id.linear_layout);

        mFragment = this;

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment == null) {
            Log.d("MAPGRAFMENT", "null");
        }
        mapFragment.getMapAsync(this);
        mGeoDataClient = Places.getGeoDataClient(getActivity(), null);

        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(getActivity(), null);

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        layout_to = view.findViewById(R.id.layout_to);
        layout_from = view.findViewById(R.id.layout_from);
        layout_price = view.findViewById(R.id.layout_price);
        price = view.findViewById(R.id.price);

        order_btn = view.findViewById(R.id.button);

        fabCall = view.findViewById(R.id.fab);
        fabCall.setVisibility(View.GONE);

        if (isInvalid) {
            price.setText(getString(R.string.voluntary));
            price.setTextColor(getResources().getColor(R.color.colorPrimary));
            order_btn.setText(getString(R.string.next));
        } else {
            layout_price.setOnClickListener(this);
        }
        layout_to.setOnClickListener(this);
        layout_from.setOnClickListener(this);

        pointA = view.findViewById(R.id.pointA);
        pointB = view.findViewById(R.id.pointB);

        horView = view.findViewById(R.id.horizontal_view);
        progressBar = view.findViewById(R.id.progress);

        order_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = SharedPref.loadUserId(view.getContext());
                String a = pointA.getText().toString();
                String b = pointB.getText().toString();
                String p = price.getText().toString();

                progressBar.showNow();
                order_btn.setVisibility(View.INVISIBLE);
                order_btn.setClickable(false);
//                createNewTrip(a, b, p, "no", id, "any",
//                        false, false);
//                ServerRequest.getInstance(view.getContext()).createTrip(id, a, b,
//                        p, "Нету", false, false, view.getContext());
                createNewTrip(a, b, p, "Нет", id, "", false, false);
//                if (isInvalid) {
//                    Intent intentInvalid = new Intent(getActivity(), OrderTaxiActivity.class);
//                    String address = pointA.getText().toString() + " – " + pointB.getText().toString();
//                    Toast.makeText(getContext(), address, Toast.LENGTH_SHORT).show();
//                    intentInvalid.putExtra("ADDRESS", address);
//                    //startActivity(intentInvalid);
//                    startActivityForResult(intentInvalid, REQUEST_CODE_COMMENT);
//                } else {
//                    searchDriver();
//                }

            }
        });

        return view;
    }
    public void createNewTrip(final String from, final String to, final String cost,
                              final String addInfo, final String passengerId, final String carType,
                              final boolean escort, final boolean wheelchair) {
//        Log.d("createNewTrip", "Emitting new_trip");
//        JSONObject newTripObj = new JSONObject();
//        try {
//            newTripObj.put("from", from);
//            newTripObj.put("to", to);
//            newTripObj.put("cost", cost);
//            newTripObj.put("addInfo", (addInfo != null) ? addInfo : "");
//            newTripObj.put("passengerId", passengerId);
//            newTripObj.put("carType", (carType != null) ? carType : "");
//            newTripObj.put("escort", escort);
//            newTripObj.put("wheelchair", wheelchair);
//            //searchDriver();
//        } catch (JSONException e) {
//            Log.e("createNewTrip", e.getMessage());
//        }
//        mSocket.emit("new_trip", newTripObj);
        ServerSocket.getInstance(getActivity().getApplicationContext()).createNewTrip(from, to, cost, addInfo, passengerId, carType, escort, wheelchair);
//        progressBar.hideNow();
//        searchDriver();
    }
    public static void tripCreatedPassenger(final String tripId) {
        mFragment.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTripId = tripId;
                mFragment.searchDriver();
            }
        });
    }

    //    private Emitter.Listener onTripAccepted = new Emitter.Listener() {
//        @Override
//        public void call(final Object... args) {
//            for (Object arg: args) {
//                Log.d("onTripAccepted", arg.toString());
//            }
//            waitDriver();
//        }
//    };
    // TODO on listen
    public static void tripAcceptedPassenger(final String from, final String to, final String dName, final String dPhone, final String vName, final String vModel, final String vNumber, final int expTime, final String dRating) {
        if (mFragment != null) {
            mFragment.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mFragment.waitDriver(from, to, dName, dPhone, vName, vModel, vNumber, expTime, dRating);
                }
            });
        }
    }
//    private Emitter.Listener onDriverCame = new Emitter.Listener() {
//        @Override
//        public void call(final Object... args) {
//            for (Object arg: args) {
//                Log.d("onDriverCame", arg.toString());
//            }
//            arrivedDriver();
//        }
//    };
    public static void driverCamePassenger(final String tripId) {
        mFragment.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mFragment.arrivedDriver(tripId);
            }
        });
    }
    public static void tripStartedPassenger() {
        if (mFragment != null) {
            mFragment.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mFragment.duringDrive();
                }
            });
        }
    }
    public static void tripEndedPassenger(final String tripId) {
        // if current user is a passenger
        if (mFragment != null) {
            mFragment.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Intent rateIntent = new Intent(mFragment.getContext(), RateActivity.class);
                    rateIntent.putExtra("TRIP_ID", tripId);
                    mFragment.getActivity().startActivity(rateIntent);
                }
            });
        }
    }
    public static void tripCanceledPassenger() {
        if (mFragment != null) {
            mFragment.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mFragment.tripCanceled();
                }
            });
        }
    }
    public void tripCanceled() {
        Toast.makeText(view.getContext(), "Заказ был отменен", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(view.getContext(), PassengerMainActivity.class);
        startActivity(intent);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
//        mSocket.off("driver_came", onDriverCame);
//        mSocket.off("trip_accepted", onTripAccepted);
//        mSocket.disconnect();
    }

    //called after user orders taxi
    void searchDriver() {
        progressBar.hideNow();
        LayoutInflater inflater = getLayoutInflater();
        topLayout = inflater.inflate(R.layout.top_layout, mainLayout, false);
        topLayout.setBackground(getResources().getDrawable(R.drawable.gradient_vertical));
        addressLayout = inflater.inflate(R.layout.address_layout, mainLayout, false);

        finalPrice = topLayout.findViewById(R.id.final_price);
        finalPrice.setText(PassengerCityFragment.price.getText());
        finalPrice.setTextSize(36.0f);

        TextView pointA = addressLayout.findViewById(R.id.pointA);
        TextView pointB = addressLayout.findViewById(R.id.pointB);
        pointA.setText(addressFrom);
        pointB.setText(addressTo);

        DilatingDotsProgressBar progressBar = topLayout.findViewById(R.id.progress);
        progressBar.showNow();

        leftBtn = topLayout.findViewById(R.id.left_btn);
        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (leftBtn.getText().equals("Отмена")) {
                    ServerSocket.getInstance(view.getContext()).sendCancelTrip(mTripId);
                    Toast.makeText(view.getContext(), "Заказ был отменен", Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(view.getContext(), PassengerMainActivity.class);
//                    startActivity(intent);
                }
            }
        });
        rightBtn = topLayout.findViewById(R.id.right_btn);
        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                d = new DialogPrice(getActivity());
                d.showDialog(getActivity());
            }
        });

//        leftBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                waitDriver(0);
//            }
//        });

        mainLayout.removeView(linLayout);
        mainLayout.addView(topLayout);
        mainLayout.addView(addressLayout);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setBackgroundDrawable(getResources()
                .getDrawable(R.drawable.gradient_vertical));
        Spannable text = new SpannableString("Ищем водителя");
        text.setSpan(new ForegroundColorSpan(Color.WHITE), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(text);

        PassengerMainActivity.mActionBarDrawerToggle.getDrawerArrowDrawable()
                .setColor(getResources().getColor(R.color.white));
        layout_price.setVisibility(View.GONE);
        horView.setVisibility(View.GONE);


        order_btn.setVisibility(View.GONE);
        //map.moveCamera(CameraUpdateFactory.newLatLngZoom(to.getLatLng(), DEFAULT_ZOOM));
    }

    //called when user waits for driver
    void waitDriver(final String from, final String to, final String dName, final String dPhone, final String vName, final String vModel, final String vNumber, final int expTime, final String dRating) {
        LayoutInflater inflater = getLayoutInflater();
        final View detailsLayout = inflater.inflate(R.layout.driver_details_layout, mainLayout, false);
        final View expandedDetailsLayout = inflater.inflate(R.layout.driver_details_expanded,
                mainLayout, false);

        topLayout.setBackgroundColor(getResources().getColor(R.color.colorAccent));

        DilatingDotsProgressBar progressBar = topLayout.findViewById(R.id.progress);
        progressBar.hideNow();
        ImageButton btn_more = detailsLayout.findViewById(R.id.btn_more);
        ImageButton btn_less = expandedDetailsLayout.findViewById(R.id.btn_less);

        TextView vehicleInfo = detailsLayout.findViewById(R.id.vehicleInfo);
        vehicleInfo.setText(vName + " " + vModel);
        TextView vehicleInfoExpanded = expandedDetailsLayout.findViewById(R.id.vehicleInfoExpanded);
        vehicleInfoExpanded.setText(vName + " " + vModel);
        TextView vehicleNumber = detailsLayout.findViewById(R.id.vehicleNumber);
        vehicleNumber.setText(vNumber);
        TextView vehicleNumberExpanded = expandedDetailsLayout.findViewById(R.id.vehicleNumberExpanded);
        vehicleNumberExpanded.setText(vNumber);
        TextView driverNameExpanded = expandedDetailsLayout.findViewById(R.id.driverNameExpanded);
        driverNameExpanded.setText(dName);
        TextView fromExpanded = expandedDetailsLayout.findViewById(R.id.fromExpanded);
        fromExpanded.setText(from);
        TextView toExpanded = expandedDetailsLayout.findViewById(R.id.toExpanded);
        toExpanded.setText(to);
        TextView ratingExpanded = expandedDetailsLayout.findViewById(R.id.ratingExpanded);
        ratingExpanded.setText(dRating);

        btn_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainLayout.removeView(detailsLayout);
                mainLayout.addView(expandedDetailsLayout);
            }
        });

        btn_less.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainLayout.removeView(expandedDetailsLayout);
                mainLayout.addView(detailsLayout);
            }
        });

        mainLayout.removeView(addressLayout);
        mainLayout.addView(detailsLayout);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(
                Color.parseColor("#08aeea")));
        Spannable text = new SpannableString("Водитель в пути");
        text.setSpan(new ForegroundColorSpan(Color.WHITE), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(text);
        // finalPrice.setText("Приедет через" + "\n8 минут");
        finalPrice.setText("Приедет через" + "\n" + (expTime / 60 / 1000) +  " минут");
        finalPrice.setTextSize(18.0f);
        rightBtn.setVisibility(View.GONE);
        leftBtn.setGravity(Gravity.CENTER);

        fabCall.setVisibility(View.VISIBLE);
        fabCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                String telNumber = "tel:+77078199809";
                String telNumber = "tel:" + dPhone;
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(telNumber));
                startActivity(intent);
            }
        });
        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (leftBtn.getText().equals("Отмена")) {
                    ServerSocket.getInstance(view.getContext()).sendCancelTrip(mTripId);
//                    Toast.makeText(view.getContext(), "Заказ был отменен", Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(view.getContext(), PassengerMainActivity.class);
//                    startActivity(intent);
                }
            }
        });

        //map.moveCamera(CameraUpdateFactory.newLatLngZoom(from.getLatLng(), DEFAULT_ZOOM));
    }

    //called when driver arrived
    void arrivedDriver(final String tripId) {
        topLayout.setBackgroundColor(getResources().getColor(R.color.yellow));
        ((AppCompatActivity)getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(
                Color.parseColor("#f7ce68")));
        Spannable text = new SpannableString("Водитель приехал");
        text.setSpan(new ForegroundColorSpan(Color.WHITE), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(text);
        finalPrice.setText("Водитель ожидает вас");
        finalPrice.setTextSize(18.0f);
        rightBtn.setVisibility(View.VISIBLE);
        rightBtn.setText("Выхожу");
        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ServerSocket.getInstance(getActivity().getApplicationContext()).sendClientReady(tripId);
            }
        });
        leftBtn.setGravity(Gravity.START);
        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                duringDrive();
            }
        });
    }

    //called when the drive starts
    void duringDrive() {
        topLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        ((AppCompatActivity)getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(
                Color.parseColor("#25d485")));
        Spannable text = new SpannableString("Вы в пути");
        text.setSpan(new ForegroundColorSpan(Color.WHITE), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(text);
        finalPrice.setText(price.getText() + "\nдо проспекта Достык, 188");
        finalPrice.setTextSize(18.0f);
        //rightBtn.setVisibility(View.GONE);
        leftBtn.setVisibility(View.GONE);
        rightBtn.setVisibility(View.GONE);
//        rightBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent rateIntent = new Intent(getContext(), RateActivity.class);
//                startActivity(rateIntent);
//            }
//        });
    }


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_from:
                Intent intentFrom = new Intent(getActivity(), FromAddressActivity.class);
                startActivity(intentFrom);
                break;
            case R.id.layout_to:
                Intent intentTo = new Intent(getActivity(), ToAddressActivity.class);
                startActivity(intentTo);
                break;
            case R.id.layout_price:
                DialogPrice d = new DialogPrice(getActivity());
                d.showDialog(getActivity());
                break;
            default:
                break;
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {return;}

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_COMMENT:
                    comment = data.getStringExtra("COMMENT");
                    break;
                case REQUEST_CODE_FROM:
//                    double lat = data.getDoubleExtra("LAT", DEF_LAT);
//                    double lng = data.getDoubleExtra("LNG", DEF_LAT);
                    addressFrom = data.getStringExtra("ADDRESS");
                    pointA.setText(addressFrom);
                    break;
                case REQUEST_CODE_TO:
//                    double lat = data.getDoubleExtra("LAT", DEF_LAT);
//                    double lng = data.getDoubleExtra("LNG", DEF_LAT);
                    addressTo = data.getStringExtra("ADDRESS");
                    pointA.setText(addressTo);
                    break;
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
        // some stuff that will happen if there's no result
        }
    }

    public static boolean isLocationEnabled(Context context) {
        String locationProviders;
        locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        return !TextUtils.isEmpty(locationProviders);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        if (d != null) d.dismiss();
        super.onDetach();
        mListener = null;
        Log.d("PassengerCityFragment", "detach");
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (outState != null) {
//            outState.putString("mTripId", mTripId);
            Log.d("Fragment save instance", outState.toString());
        }
        if (map != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, map.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    /**
     * Sets up the options menu.
     * @param menu The options menu.
     * @return Boolean.
     */
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getActivity().getMenuInflater().inflate(R.menu.current_place_menu, menu);
//        return true;
//    }

    /**
     * Handles a click on the menu option to get a place.
     * @param item The menu item to handle.
     * @return Boolean.
     */
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == R.id.option_get_place) {
//            showCurrentPlace();
//        }
//        return true;
//    }

    /**
     * Manipulates the map when it's available.
     * This callback is triggered when the map is ready to be used.
     */
    @Override
    public void onMapReady(GoogleMap mMap) {
        map = mMap;
        if (map == null) {
            Log.d("MAP", "null in onmapreayd");
        }
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
//        map.setLatLngBoundsForCameraTarget(new LatLngBounds(new LatLng(43.143121, 76.691608),
//                new LatLng(43.396356, 77.134495)));
        map.setMinZoomPreference(12.0f);
        // Prompt the user for permission.
        getLocationPermission();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();
    }

    @Override
    public void onResume() {
        super.onResume();
//        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Ищем водителя");
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        if(isLocationEnabled(getContext())) {
            try {
                if (mLocationPermissionGranted) {
                    Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                    locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if (task.isSuccessful()) {
                                // Set the map's camera position to the current location of the device.
                                mLastKnownLocation = task.getResult();
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(mLastKnownLocation.getLatitude(),
                                                mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
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
            DialogLocation d = new DialogLocation(getActivity());
            d.showDialog(getActivity());
            Toast.makeText(getContext(), "location service disabled", Toast.LENGTH_SHORT).show();
        }


    }


    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.view.getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
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

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
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
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    class DialogLocation extends android.app.Dialog {
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
    class DialogPrice extends Dialog {
        public DialogPrice(Activity a) {
            super(a);
        }

        public void showDialog(Activity activity) {
            final DialogPrice dialog = new DialogPrice(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.dialog_price);
            Button btn_ready = dialog.findViewById(R.id.btn_ready);
            final EditText price_edit = dialog.findViewById(R.id.price);
            btn_ready.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String price_text = price_edit.getText().toString();
                    PassengerCityFragment.price.setText(price_text);
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }
}
