package vlimv.taxi;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.List;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FavoritesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FavoritesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FavoritesFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private OnListItemClickListener mListItemClickListener;

    private int NEW_FAVORITE_REQUEST_CODE = 1;
    FavoritePlacesAdapter adapter;
    List<Address> list;

    public FavoritesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FavoritesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FavoritesFragment newInstance(String param1, String param2) {
        FavoritesFragment fragment = new FavoritesFragment();
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
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.favorites);
        RecyclerView recyclerView = view.findViewById(R.id.main_recycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        list = SharedPref.loadFavoritesArray(getContext());

        adapter = new FavoritePlacesAdapter(list);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemTapListener(mListItemClickListener);

//        if (list != null) {
//            adapter = new FavoritePlacesAdapter(list);
//            recyclerView.setAdapter(adapter);
//            adapter.setOnItemTapListener(mListItemClickListener);
//        } else {
//            Toast.makeText(getContext(), getResources().getString(R.string.empty), Toast.LENGTH_LONG).show();
//        }

        RelativeLayout addFavorite = view.findViewById(R.id.add_favorite);
        addFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addFavoritesIntent = new Intent(view.getContext(), AddFavoritePlaceActivity.class);
                startActivityForResult(addFavoritesIntent, NEW_FAVORITE_REQUEST_CODE);
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NEW_FAVORITE_REQUEST_CODE && resultCode == RESULT_OK) {
            list.clear();
            list.addAll(SharedPref.loadFavoritesArray(getContext()));
            adapter.notifyDataSetChanged();
            Toast.makeText(getContext(), "Новое место успешно создано.", Toast.LENGTH_LONG).show();
        } else if (resultCode == RESULT_CANCELED) {
            // The user canceled the operation.
        }
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
        mListItemClickListener = new OnListItemClickListener() {
            @Override
            public void onListItemClick(Address address) {
                Toast.makeText(getView().getContext(), address.toString(), Toast.LENGTH_SHORT).show();
            }
        };
//        try {
//            mListItemClickListener = (OnListItemClickListener) context;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(context.toString()
//                    + " must implement OnListItemClickListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        mListItemClickListener = null;
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
        void onFragmentInteraction(Uri uri);
    }
    public interface OnListItemClickListener {
        void onListItemClick(Address address);
    }
//    public interface OnListItemClickListener {
//        void onListItemClick(Address address);
//    }
}
//
//class FavoritesRecyclerAdapter extends RecyclerView.Adapter<FavoritesRecyclerAdapter.ViewHolder>{
//    private List<Address> mItemsList;
//    private FavoritesFragment.OnListItemClickListener mListItemClickListener;
//
//    public FavoritesRecyclerAdapter(List<Address> itemsList) {
//        mItemsList = itemsList == null ? new ArrayList<Address>() : itemsList;
//    }
//
//    @Override
//    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        // Create a new view by inflating the row item xml.
//        View v = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.recycler_item_favorites, parent, false);
//
//        // Set the view to the ViewHolder
//        return new ViewHolder(v);
//    }
//
//    @Override
//    public void onBindViewHolder(ViewHolder holder, int position) {
////        final String itemTitle = mItemsList.get(position);
////        holder.title.setText(itemTitle);
//    }
//
//    @Override
//    public int getItemCount() {
//        return mItemsList.size();
//    }
//
//    // Create the ViewHolder class to keep references to your views
//    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
//        public ViewHolder(View v) {
//            super(v);
//            TextView address = v.findViewById(R.id.address);
//            TextView placeName = v.findViewById(R.id.place_name);
//            v.setOnClickListener(this);
//        }
//
//
//        @Override
//        public void onClick(View view) {
//            if (null != mListItemClickListener) {
//                // Notify the active callbacks interface (the activity, if the
//                // fragment is attached to one) that an item has been selected.
//                mListItemClickListener.onListItemClick(mItemsList.get(getAdapterPosition()));
//            }
//        }
//    }
//
//    public void setOnItemTapListener(FavoritesFragment.OnListItemClickListener itemClickListener) {
//        mListItemClickListener = itemClickListener;
//    }
//}
