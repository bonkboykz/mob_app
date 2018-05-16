package vlimv.taxi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HP on 16-Apr-18.
 */

public class CompletedOrdersFragment extends Fragment {
    private static final String TAG = "RecyclerViewFragment";
    private static final String KEY_LAYOUT_MANAGER = "layoutManager";
    private static final int DATASET_COUNT = 15;

    private enum LayoutManagerType {
        LINEAR_LAYOUT_MANAGER
    }

    protected LayoutManagerType mCurrentLayoutManagerType;

    private OnListItemClickListener mListItemClickListener;
    protected RecyclerView mRecyclerView;
    protected static CompletedOrdersAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected static List<JSONObject> trips = new ArrayList<>();

    private static TextView emptyTV;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context context = this.getContext();
        if (SharedPref.loadUserType(context).equals("driver")) {
            ServerRequest.getInstance(context).getDriverTrips(SharedPref.loadToken(context),
                    SharedPref.loadUserId(context));
        } else {
            ServerRequest.getInstance(context).getUserTrips(SharedPref.loadToken(context),
                    SharedPref.loadUserId(context));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_page, container, false);
        rootView.setTag(TAG);

        if (SharedPref.loadUserType(getContext()).equals("driver")) {
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("");
        } else {
            if (((AppCompatActivity)getActivity()).getSupportActionBar() != null) {
                ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Поездки");
            }
        }

        mRecyclerView = rootView.findViewById(R.id.main_recycler);

        mLayoutManager = new LinearLayoutManager(getActivity());

        mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;

        if (savedInstanceState != null) {
            mCurrentLayoutManagerType = (LayoutManagerType) savedInstanceState
                    .getSerializable(KEY_LAYOUT_MANAGER);
        }
        setRecyclerViewLayoutManager(mCurrentLayoutManagerType);

        mAdapter = new CompletedOrdersAdapter(trips);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemTapListener(mListItemClickListener);
        setRecyclerViewLayoutManager(LayoutManagerType.LINEAR_LAYOUT_MANAGER);

        emptyTV = rootView.findViewById(R.id.empty_text_view);
        return rootView;
    }

    /**
     * Set RecyclerView's LayoutManager to the one given.
     *
     * @param layoutManagerType Type of layout manager to switch to.
     */
    public void setRecyclerViewLayoutManager(LayoutManagerType layoutManagerType) {
        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        if (mRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }

        switch (layoutManagerType) {
            case LINEAR_LAYOUT_MANAGER:
                mLayoutManager = new LinearLayoutManager(getActivity());
                mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
                break;
            default:
                mLayoutManager = new LinearLayoutManager(getActivity());
                mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        }

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(scrollPosition);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save currently selected layout manager.
        savedInstanceState.putSerializable(KEY_LAYOUT_MANAGER, mCurrentLayoutManagerType);
        super.onSaveInstanceState(savedInstanceState);
    }

    public static void initArray(ArrayList<JSONObject> array) {
        if (array.size() == 0)
            emptyTV.setVisibility(View.VISIBLE);
        for (int i = 0; i < array.size(); i++) {
            trips.add(array.get(i));
            Log.d("in completed orders", array.get(i).toString());
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListItemClickListener = new OnListItemClickListener() {
            @Override
            public void onListItemClick(JSONObject trip) {
                Dialog_details dialog = new Dialog_details(getActivity());
                dialog.showDialog(getActivity(), trip);
            }
        };
    }
    @Override
    public void onDetach() {
        super.onDetach();
        //mListItemClickListener = null;
    }

    public interface OnListItemClickListener {
        void onListItemClick(JSONObject trip);
    }
    void startOrder() {
        Intent intent = new Intent(getContext(), DriverOrderActivity.class);
        startActivity(intent);

    }

    public class Dialog_details extends android.app.Dialog {
        public Dialog_details(Activity a) {
            super(a);
        }

        public void showDialog(Activity activity, JSONObject trip){
            final Dialog_details dialog = new Dialog_details(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.dialog_details);

            TextView pointA = dialog.findViewById(R.id.dialogPointA);
            TextView pointB = dialog.findViewById(R.id.dialogPointB);
            TextView price = dialog.findViewById(R.id.dialogPrice);

            try {
                pointA.setText(trip.getString("from"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                pointB.setText(trip.getString("to"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                price.setText(trip.getString("cost"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            TextView text_cancel = dialog.findViewById(R.id.text_cancel);
            TextView text_accept = dialog.findViewById(R.id.text_accept);
            text_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), "Заказ не беру.", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });
            text_accept.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Toast.makeText(getContext(), "Заказ принят.", Toast.LENGTH_SHORT).show();
                    startOrder();
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }
}
