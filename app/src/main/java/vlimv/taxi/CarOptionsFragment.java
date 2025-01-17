package vlimv.taxi;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;
import com.zl.reik.dilatingdotsprogressbar.DilatingDotsProgressBar;

import static vlimv.taxi.DriverOrderActivity.mActionBarDrawerToggle;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CarOptionsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CarOptionsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CarOptionsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    String carName, carModel, carType;
    MaterialBetterSpinner spinner_car, spinner_car_model, spinner_car_type;
    EditText numberEditText, yearEditText;

    DilatingDotsProgressBar progressBar;
    Button next_btn;

    private OnFragmentInteractionListener mListener;

    public CarOptionsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CarOptionsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CarOptionsFragment newInstance(String param1, String param2) {
        CarOptionsFragment fragment = new CarOptionsFragment();
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
        View view = inflater.inflate(R.layout.fragment_car_options, container, false);

        ServerRequest.getInstance(getContext()).getCar(SharedPref.loadUserId(getContext()), getContext());
        DriverMainActivity.free.setVisibility(View.GONE);
        DriverMainActivity.busy.setVisibility(View.GONE);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.car_options);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(
                Color.parseColor("#ffffff")));
        //        mActionBarDrawerToggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.colorPrimary));

        progressBar = view.findViewById(R.id.progress);

        spinner_car = view.findViewById(R.id.car);
        spinner_car.setEnabled(false);
        ArrayAdapter<CharSequence> adapter_car = ArrayAdapter.createFromResource(getContext(), R.array.car_array, android.R.layout.simple_spinner_dropdown_item);
        adapter_car.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_car.setAdapter(adapter_car);
        spinner_car.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                carName = adapterView.getItemAtPosition(position).toString();
            }
        });
        carName = spinner_car.getText().toString();

        spinner_car_model = view.findViewById(R.id.car_model);
        spinner_car_model.setEnabled(false);
        ArrayAdapter<CharSequence> adapter_car_model = ArrayAdapter.createFromResource(getContext(), R.array.car_model_array, android.R.layout.simple_spinner_dropdown_item);
        adapter_car_model.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_car_model.setAdapter(adapter_car_model);
        spinner_car_model.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                carModel = adapterView.getItemAtPosition(position).toString();
            }
        });
        carModel = spinner_car_model.getText().toString();

        spinner_car_type = view.findViewById(R.id.car_type);
        spinner_car_type.setEnabled(false);
        ArrayAdapter<CharSequence> adapter_car_type = ArrayAdapter.createFromResource(getContext(), R.array.car_type_array, android.R.layout.simple_spinner_dropdown_item);
        adapter_car_type.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_car_type.setAdapter(adapter_car_type);
        spinner_car_type.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                carType = adapterView.getItemAtPosition(position).toString();
            }
        });
        carType = spinner_car_type.getText().toString();

        numberEditText = view.findViewById(R.id.car_number);
        yearEditText = view.findViewById(R.id.car_year);

        next_btn = view.findViewById(R.id.next_button);
        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.showNow();
                next_btn.setVisibility(View.GONE);
                String driverId = SharedPref.loadUserId(view.getContext());
                String carNumber = numberEditText.getText().toString();
                String carYear = yearEditText.getText().toString();
                carName = spinner_car.getText().toString();
                carModel = spinner_car_model.getText().toString();
                carType = spinner_car_type.getText().toString();

                ServerRequest.getInstance(view.getContext()).updateCar(driverId, carName, carModel, carType,
                        carNumber, carYear, view.getContext());
            }
        });

        return view;

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
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
