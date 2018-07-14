package vlimv.taxi;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HP on 04-May-18.
 */

public class FavoritePlacesAdapter extends RecyclerView.Adapter<FavoritePlacesAdapter.ViewHolder> {
    private List<Address> favorites;
    private FavoritesFragment.OnListItemClickListener mListItemClickListener;

    public FavoritePlacesAdapter(List<Address> itemsList) {
        favorites = itemsList == null ? new ArrayList<Address>() : itemsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new view by inflating the row item xml.
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_item_favorites, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.address.setText(favorites.get(position).address);
        holder.placeName.setText(favorites.get(position).name);
        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int i = holder.getAdapterPosition();
                SharedPref.removeFavorite(view.getContext(), i);
                favorites.remove(i);
                notifyItemRemoved(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return favorites.size();
    }

    // Create the ViewHolder class to keep references to your views
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView address, placeName;
        ImageButton deleteBtn;

        /**
         * Constructor
         * @param v The container view which holds the elements from the row item xml
         */
        public ViewHolder(View v) {
            super(v);
            address = v.findViewById(R.id.address);
            placeName = v.findViewById(R.id.place_name);
            deleteBtn = v.findViewById(R.id.delete_button);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (null != mListItemClickListener) {
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that an item has been selected.
                mListItemClickListener.onListItemClick(favorites.get(getAdapterPosition()));
            }
        }
    }

    public void setOnItemTapListener(FavoritesFragment.OnListItemClickListener itemClickListener) {
        mListItemClickListener = itemClickListener;
    }
}
