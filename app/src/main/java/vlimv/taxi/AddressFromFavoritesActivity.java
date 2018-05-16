package vlimv.taxi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HP on 13.02.2018.
 */

public class AddressFromFavoritesActivity extends AppCompatActivity {
    ArrayList<Address> list = new ArrayList<Address>();
    FavoritesAdapter adapter;
    private OnListItemClickListener mListItemClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorite_places);
        RecyclerView recyclerView = findViewById(R.id.main_recycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        list = SharedPref.loadFavoritesArray(this);
        adapter = new FavoritesAdapter(list);
        mListItemClickListener = new OnListItemClickListener() {
            @Override
            public void onListItemClick(Address address) {
                Intent resultIntent = new Intent();
                setResult(Activity.RESULT_OK, resultIntent);
                resultIntent.putExtra("ADDRESS", address.address);
                resultIntent.putExtra("LAT", address.lat);
                resultIntent.putExtra("LNG", address.lng);
                finish();
            }
        };
        adapter.setOnItemTapListener(mListItemClickListener);
        recyclerView.setAdapter(adapter);

        ImageButton backBtn = findViewById(R.id.back_button);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent resultIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, resultIntent);
                finish();
            }
        });
    }

    public interface OnListItemClickListener {
        void onListItemClick(Address address);
    }


    class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {
        private List<Address> favorites;
        private AddressFromFavoritesActivity.OnListItemClickListener mListItemClickListener;

        public FavoritesAdapter(List<Address> itemsList) {
            favorites = itemsList == null ? new ArrayList<Address>() : itemsList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // Create a new view by inflating the row item xml.
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycler_item_favorites_without_delete, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.address.setText(favorites.get(position).address);
            holder.placeName.setText(favorites.get(position).name);
        }

        @Override
        public int getItemCount() {
            return favorites.size();
        }

        // Create the ViewHolder class to keep references to your views
        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView address, placeName;

            /**
             * Constructor
             * @param v The container view which holds the elements from the row item xml
             */
            public ViewHolder(View v) {
                super(v);
                address = v.findViewById(R.id.address);
                placeName = v.findViewById(R.id.place_name);

                v.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                if (mListItemClickListener != null) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListItemClickListener.onListItemClick(favorites.get(getAdapterPosition()));
                }
            }
        }

        public void setOnItemTapListener(AddressFromFavoritesActivity.OnListItemClickListener itemClickListener) {
            mListItemClickListener = itemClickListener;
        }
    }

}


