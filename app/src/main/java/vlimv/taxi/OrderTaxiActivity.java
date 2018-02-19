package vlimv.taxi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

public class OrderTaxiActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    TextView address_from_to;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_taxi);

        Bundle extras = this.getIntent().getExtras();
        String address = extras.getString("ADDRESS");

        MaterialBetterSpinner spinner_gender = findViewById(R.id.car_type);
        ArrayAdapter<CharSequence> adapter_gender = ArrayAdapter.createFromResource(this,
                R.array.car_type_array, android.R.layout.simple_spinner_dropdown_item);
        adapter_gender.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_gender.setAdapter(adapter_gender);
        spinner_gender.setOnItemSelectedListener(this);

        address_from_to = findViewById(R.id.address_from_to);
        address_from_to.setText(address);

        Button order_btn = findViewById(R.id.button_order);
        order_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("COMMENT", "this is comment");
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        Toast.makeText (getApplicationContext(), "Selected: " +
                parent.getItemAtPosition(pos).toString(), Toast.LENGTH_SHORT).show();
        String car_type = parent.getItemAtPosition(pos).toString();
    }
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }
}
