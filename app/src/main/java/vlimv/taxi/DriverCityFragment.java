package vlimv.taxi;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

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
    private final LatLng mDefaultLocation = new LatLng(43.238949, 76.889709);
    private static final int DEFAULT_ZOOM = 17;
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    private OnFragmentInteractionListener mListener;

    FloatingActionButton fab;
    TextView min_5, min_10, min_15, min_20, cancel_time, arrived, cancel_order;
    static TextView time;
    long timeInMillis;
    View chooseTime, showAddress;
    LinearLayout textLayout;
    View view;

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

        if (DriverOrderActivity.orderState.equals("new")) {
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
                ((AppCompatActivity)getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(
                        Color.parseColor("#f7ce68")));
                top_layout.setBackgroundResource(R.color.yellow);
                cancel_order.setText("Приехали!");
                arrived.setText("Навигатор");
                time.setText("600 тг" + "\n" + "до проспекта Гоголя, 20");
                DriverOrderActivity.timer.cancel();
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

        textLayout.setVisibility(View.VISIBLE);
        showAddress.setVisibility(View.VISIBLE);
        chooseTime.setVisibility(View.GONE);
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
        map.setLatLngBoundsForCameraTarget(new LatLngBounds(new LatLng(43.143121, 76.691608),
                new LatLng(43.396356, 77.134495)));
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
