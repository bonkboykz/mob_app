package vlimv.taxi;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;

/**
 * Created by HP on 13.02.2018.
 */

public class AddressFromFavoritesActivity extends AppCompatActivity {
    ArrayList<String> places = new ArrayList<String>();
    ListViewAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorite_places);

        fillData();
        adapter = new ListViewAdapter(this, places);

        // настраиваем список
        ListView lvMain = findViewById(R.id.lvMain);
        lvMain.setAdapter(adapter);
        lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?>adapter, View v, int position, long l){
                Object item = adapter.getItemAtPosition(position);

                Toast.makeText(getBaseContext(), "clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }
    void fillData() {
        for (int i = 1; i <= 5; i++) {
            places.add("abay 130");
        }
    }


}


