package vlimv.taxi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

/**
 * Created by HP on 13.02.2018.
 */


public class ListViewAdapter extends BaseAdapter {
    Context context;
    LayoutInflater lInflater;
    ArrayList<String> places;

    ListViewAdapter(Context context, ArrayList<String> places) {
        this.context = context;
        this.places = places;
        lInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return places.size();
    }
    @Override
    public String getItem(int position) {
        return places.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.recycler_item_favorites, parent, false);
        }

        String title = getItem(position);
        return view;
    }

}