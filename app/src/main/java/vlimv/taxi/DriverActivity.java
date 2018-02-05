package vlimv.taxi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

public class DriverActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    SharedPreferences sPrefName;
    EditText name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);
        MaterialBetterSpinner spinner_gender = findViewById(R.id.gender);
        ArrayAdapter<CharSequence> adapter_gender = ArrayAdapter.createFromResource(this, R.array.gender_array, android.R.layout.simple_spinner_dropdown_item);
        adapter_gender.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_gender.setAdapter(adapter_gender);
        spinner_gender.setOnItemSelectedListener(this);

        MaterialBetterSpinner spinner_car = findViewById(R.id.car);
        ArrayAdapter<CharSequence> adapter_car = ArrayAdapter.createFromResource(this, R.array.car_array, android.R.layout.simple_spinner_dropdown_item);
        adapter_car.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_car.setAdapter(adapter_car);
        spinner_car.setOnItemSelectedListener(this);

        MaterialBetterSpinner spinner_car_model = findViewById(R.id.car_model);
        ArrayAdapter<CharSequence> adapter_car_model = ArrayAdapter.createFromResource(this, R.array.car_model_array, android.R.layout.simple_spinner_dropdown_item);
        adapter_car_model.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_car_model.setAdapter(adapter_car_model);
        spinner_car_model.setOnItemSelectedListener(this);

        MaterialBetterSpinner spinner_car_type = findViewById(R.id.car_type);
        ArrayAdapter<CharSequence> adapter_car_type = ArrayAdapter.createFromResource(this, R.array.car_type_array, android.R.layout.simple_spinner_dropdown_item);
        adapter_car_type.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_car_type.setAdapter(adapter_car_type);
        spinner_car_type.setOnItemSelectedListener(this);

        Button next_btn = findViewById(R.id.button);
        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveName();
                Intent intent = new Intent(getApplicationContext(), DriverMainActivity.class);
                startActivity(intent);
            }
        });
        name = findViewById(R.id.name);
    }

    void saveName() {
        sPrefName = getSharedPreferences("NAME", 0);
        SharedPreferences.Editor ed = sPrefName.edit();
        String name_text = name.getText().toString();
        ed.putString("NAME", name.getText().toString());
        ed.apply();
        Toast.makeText(this, name_text, Toast.LENGTH_SHORT).show();
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        switch(view.getId()) {
            case R.id.gender:
                String gender = parent.getItemAtPosition(pos).toString();
                break;
            case R.id.car:
                String car = parent.getItemAtPosition(pos).toString();
        }
        Toast.makeText (getApplicationContext(), "Selected: " + parent.getItemAtPosition(pos).toString(), Toast.LENGTH_SHORT).show();
    }
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

}
