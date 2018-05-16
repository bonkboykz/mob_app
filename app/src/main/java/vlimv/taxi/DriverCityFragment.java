package vlimv.taxi;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.*;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DriverCityFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DriverCityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DriverCityFragment extends Fragment implements View.OnClickListener, OnMapReadyCallback {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //Map variables
    Place from, to;
    Marker markerFrom, markerTo;
    private GoogleMap map;
    SupportMapFragment mapFragment;
    GeoDataClient mGeoDataClient;
    PlaceDetectionClient mPlaceDetectionClient;
    FusedLocationProviderClient mFusedLocationProviderClient;
    private final double DEF_LAT = 45.017711;
    private final double DEF_LNG = 78.380442;
    private final LatLng mDefaultLocation = new LatLng(DEF_LAT, DEF_LNG);
    private static final int DEFAULT_ZOOM = 17;
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    private OnFragmentInteractionListener mListener;
    private static DriverCityFragment mFragment;


    FloatingActionButton fab;
    TextView min_5, min_10, min_15, min_20, cancel_time, arrived, cancel_order, arrived2;
    static TextView time;
    long timeInMillis;
    View chooseTime, showAddress;
    LinearLayout textLayout;
    View view;
//    Socket mSocket;
    private String mTripId;
    private String mTripPrice;
    private String mTripTo;
    private String mTripLat;
    private String mTripLng;

    LinearLayout top_layout;

    public DriverCityFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DriverCityFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DriverCityFragment newInstance(String param1, String param2) {
        DriverCityFragment fragment = new DriverCityFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        TaxiApplication app = (TaxiApplication) getActivity().getApplication();
//        mSocket = app.getSocket();
//        mSocket.connect();
        mFragment = this;
        mTripId = getArguments().getString("TRIP_ID");
        mTripPrice = getArguments().getString("TRIP_PRICE");
        mTripTo = getArguments().getString("TRIP_TO");
        //mTripLat = getArguments().getString("TRIP_LAT");
        //mTripLng = getArguments().getString("TRIP_LNG");
        Log.d("DriverCityFragment", "tripId: " + mTripId);
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_driver_city, container, false);
        //Setting up map
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

        chooseTime = view.findViewById(R.id.choose_time);
        fab = view.findViewById(R.id.fab);
//        fab.setVisibility(View.VISIBLE);
        chooseTime.setVisibility(View.VISIBLE);
        textLayout = view.findViewById(R.id.text_layout);
        showAddress = view.findViewById(R.id.show_address);

        min_5 = view.findViewById(R.id.min_5);
        min_10 = view.findViewById(R.id.min_10);
        min_15 = view.findViewById(R.id.min_15);
        min_20 = view.findViewById(R.id.min_20);
        min_5.setOnClickListener(this);
        min_10.setOnClickListener(this);
        min_15.setOnClickListener(this);
        min_20.setOnClickListener(this);
        time = view.findViewById(R.id.time);

        if (DriverOrderActivity.next_btn != null) {
            DriverOrderActivity.next_btn.setVisibility(View.GONE);
        }

        if (DriverOrderActivity.orderState.equals("new")) {
            ((AppCompatActivity)getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(
                    Color.parseColor("#08aeea")));
            Spannable text = new SpannableString(getResources().getString(R.string.order_accepted));
            text.setSpan(new ForegroundColorSpan(Color.WHITE), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(text);

            time.setText(getResources().getString(R.string.time));
            textLayout.setVisibility(View.GONE);
            showAddress.setVisibility(View.GONE);
        } else if (DriverOrderActivity.orderState.equals("arriving")) {
            chooseTime.setVisibility(View.GONE);
        }

        top_layout = view.findViewById(R.id.top_layout);
        arrived = view.findViewById(R.id.arrived);
        cancel_order = view.findViewById(R.id.cancel_order);
        arrived.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (arrived.getText().equals("Навигатор")) {
                    Log.d("asd", mTripTo);
                    String formattedTo = mTripTo.replaceAll("\\s+", "+");
                    Log.d("asd", formattedTo);
                    Uri gmmIntentUri = Uri.parse("google.navigation:q=" + formattedTo);
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);
                } else {
                    ((AppCompatActivity)getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(
                            Color.parseColor("#f7ce68")));
                    top_layout.setBackgroundResource(R.color.yellow);
                    cancel_order.setText("Начать поездку");
                    arrived.setText("Навигатор");
                    time.setText(mTripPrice + " тг\n" + "до " + mTripTo);
                    ServerSocket.getInstance(getActivity().getApplicationContext()).sendDriverCame(mTripId);
                    DriverOrderActivity.timer.cancel();
                }

            }
        });
        cancel_order = view.findViewById(R.id.cancel_order);
        cancel_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DriverOrderActivity.timer.cancel();
                if (cancel_order.getText().equals("Приехали!")) {
                    DriverOrderActivity.orderState = "new";
                    ServerSocket.getInstance(getActivity().getApplicationContext()).sendEndTrip(mTripId);
                    Intent intent = new Intent(view.getContext(), DriverMainActivity.class);
                    startActivity(intent);
                }
                if (cancel_order.getText().equals("Начать поездку")) {
                    DriverOrderActivity.orderState = "started";
                    ((AppCompatActivity)getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(
                            Color.parseColor("#25d485")));
                    cancel_order.setText("Приехали!");
                    top_layout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    Spannable text = new SpannableString("Вы в пути");
                    text.setSpan(new ForegroundColorSpan(Color.WHITE), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(text);
                    ServerSocket.getInstance(getActivity().getApplicationContext()).sendDriverStart(mTripId);
                }
                if (cancel_order.getText().equals("Отмена")) {
                    DriverOrderActivity.orderState = "new";
                    ServerSocket.getInstance(view.getContext()).sendCancelTrip(mTripId);
                    Toast.makeText(view.getContext(), "Отмена", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(view.getContext(), DriverMainActivity.class);
                    startActivity(intent);
                }
            }
        });
        return view;
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

        DriverOrderActivity.startTimer(timeInMillis);
        DriverOrderActivity.orderState = "arriving";
        sendTripAccepted(mTripId, SharedPref.loadUserId(getContext()), "", timeInMillis + "");
        textLayout.setVisibility(View.VISIBLE);
        showAddress.setVisibility(View.VISIBLE);
        chooseTime.setVisibility(View.GONE);

    }
    public static void clientReadyDriver() {
        if (mFragment != null) {
            mFragment.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mFragment.clientReady();
                }
            });
        }
    }
    public static void tripCanceledDriver() {
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
        DriverOrderActivity.timer.cancel();
        DriverOrderActivity.orderState = "new";
        Toast.makeText(view.getContext(), "Заказ был отменен", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(view.getContext(), DriverMainActivity.class);
        startActivity(intent);
    }
    public void clientReady() {
        Toast.makeText(getActivity(), "Клиент готов!", Toast.LENGTH_SHORT).show();
    }
    public void sendTripAccepted(final String tripId, final String driverId, final String vehicleId, final String expTime) {
//        Log.d("sendTripAccepted", "Emitting trip_accepted");
//        JSONObject tripAcceptedObj = new JSONObject();
//        try {
//            tripAcceptedObj.put("id", tripId);
//            tripAcceptedObj.put("driverId", driverId);
//            tripAcceptedObj.put("vehicleId", vehicleId);
//            tripAcceptedObj.put("expTime", (expTime != null) ? expTime : "");
//        } catch (JSONException e) {
//            Log.e("sendTripAccepted", e.getMessage());
//        }
//        mSocket.emit("trip_accepted", tripAcceptedObj);
        ServerSocket.getInstance(getActivity().getApplicationContext()).sendTripAccepted(tripId, driverId, vehicleId, expTime);
    }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

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
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
