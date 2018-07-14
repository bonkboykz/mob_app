package vlimv.taxi;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HP on 16-Apr-18.
 */

public class CompletedOrdersAdapter extends RecyclerView.Adapter<CompletedOrdersAdapter.ViewHolder> {

    private List<JSONObject> mTrips;
    private CompletedOrdersFragment.OnListItemClickListener mListItemClickListener;

    public CompletedOrdersAdapter(List<JSONObject> itemsList) {
        mTrips = itemsList == null ? new ArrayList<JSONObject>() : itemsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new view by inflating the row item xml.
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_item_order, parent, false);
//
//        TextView from = v.findViewById(R.id.pointA);
//        TextView to = v.findViewById(R.id.pointB);
//        TextView price = v.findViewById(R.id.price);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try {
            //holder.setPointA(mTrips.get(position).getString("from"));
            holder.pointA.setText(mTrips.get(position).getString("from"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            holder.pointB.setText(mTrips.get(position).getString("to"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            holder.price.setText(mTrips.get(position).getString("cost"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mTrips.size();
    }

    // Create the ViewHolder class to keep references to your views
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView pointA, pointB, price, details;

        /**
         * Constructor
         * @param v The container view which holds the elements from the row item xml
         */
        public ViewHolder(View v) {
            super(v);

            pointA = v.findViewById(R.id.pointA);
            pointB = v.findViewById(R.id.pointB);
            price = v.findViewById(R.id.price);
            details = v.findViewById(R.id.details);
            v.setOnClickListener(this);
        }

        public void setPointA(String from) {
            if (null == pointA) return;
            pointA.setText(from);
        }


        @Override
        public void onClick(View view) {
            if (null != mListItemClickListener) {
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that an item has been selected.
                mListItemClickListener.onListItemClick(mTrips.get(getAdapterPosition()));
                //Log.d("TAG", "item " + view.)
            }
        }
    }

    public void setOnItemTapListener(CompletedOrdersFragment.OnListItemClickListener itemClickListener) {
        mListItemClickListener = itemClickListener;
    }
}

