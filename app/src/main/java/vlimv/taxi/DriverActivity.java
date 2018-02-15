package vlimv.taxi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

public class DriverActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    SharedPreferences sPrefName;
    EditText name, surname, date, carNumber, carYear;
    String nameText, surnameText, birthDateText, genderText, carText, carModelText, carTypeText, carNumberText, carYearText;
    static Driver driver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);

        MaterialBetterSpinner spinner_gender = findViewById(R.id.gender);
        ArrayAdapter<CharSequence> adapter_gender = ArrayAdapter.createFromResource(this, R.array.gender_array, android.R.layout.simple_spinner_dropdown_item);
        adapter_gender.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_gender.setAdapter(adapter_gender);
        spinner_gender.setOnItemClickListener(this);

        MaterialBetterSpinner spinner_car = findViewById(R.id.car);
        ArrayAdapter<CharSequence> adapter_car = ArrayAdapter.createFromResource(this, R.array.car_array, android.R.layout.simple_spinner_dropdown_item);
        adapter_car.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_car.setAdapter(adapter_car);
        spinner_car.setOnItemClickListener(this);

        MaterialBetterSpinner spinner_car_model = findViewById(R.id.car_model);
        ArrayAdapter<CharSequence> adapter_car_model = ArrayAdapter.createFromResource(this, R.array.car_model_array, android.R.layout.simple_spinner_dropdown_item);
        adapter_car_model.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_car_model.setAdapter(adapter_car_model);
        spinner_car_model.setOnItemClickListener(this);

        MaterialBetterSpinner spinner_car_type = findViewById(R.id.car_type);
        ArrayAdapter<CharSequence> adapter_car_type = ArrayAdapter.createFromResource(this, R.array.car_type_array, android.R.layout.simple_spinner_dropdown_item);
        adapter_car_type.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_car_type.setAdapter(adapter_car_type);
        spinner_car_type.setOnItemClickListener(this);

        final Activity activity = this;

        //Initializing edit texts
        name = findViewById(R.id.name);
        surname = findViewById(R.id.surname);
        carNumber = findViewById(R.id.car_number);
        carYear = findViewById(R.id.car_year);

        date = findViewById(R.id.birthdate);
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                LayoutInflater inflater = activity.getLayoutInflater();
                builder.setTitle("Выберите дату рождения");
                builder.setCancelable(false);
                View dialogView = inflater.inflate(R.layout.dialog_date_pick, null);
                final DatePicker datePicker = dialogView.findViewById(R.id.date_picker);
                datePicker.setMaxDate(System.currentTimeMillis() - 1000);
                builder.setView(dialogView)
                        // Add action buttons
                        .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                String birthDate = datePicker.getDayOfMonth() + "." +
                                        (datePicker.getMonth() + 1) + "." +
                                        datePicker.getYear();
                                date.setText(birthDate);
                            }
                        });
                builder.create();
                builder.show();
            }
        });
        final Button next_btn = findViewById(R.id.button);
        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //saveName();
                nameText = name.getText().toString();
                surnameText = surname.getText().toString();
                birthDateText = date.getText().toString();
                carNumberText = carNumber.getText().toString();
                carYearText = carYear.getText().toString();

                if (nameText != null && surnameText != null && birthDateText != null && genderText != null
                        && carText != null && carModelText != null && carTypeText != null
                        && carNumberText != null && carYearText != null) {
                    next_btn.setEnabled(true);

                    driver = new Driver(nameText, surnameText, birthDateText, genderText, carText, carModelText,
                            carTypeText, carNumberText, carYearText);
                }


                Intent intent = new Intent(getApplicationContext(), DriverMainActivity.class);
                startActivity(intent);
            }
        });
    }

    void saveName() {
        sPrefName = getSharedPreferences("NAME", 0);
        SharedPreferences.Editor ed = sPrefName.edit();
        String name_text = name.getText().toString();
        ed.putString("NAME", name.getText().toString());
        ed.apply();
        Toast.makeText(this, name_text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.gender:
                genderText = parent.getItemAtPosition(position).toString();
                break;
            case R.id.car:
                carText = parent.getItemAtPosition(position).toString();
                break;
            case R.id.car_model:
                carModelText = parent.getItemAtPosition(position).toString();
                break;
            case R.id.car_type:
                carTypeText = parent.getItemAtPosition(position).toString();
                break;
        }
        Toast.makeText (getApplicationContext(), parent.getItemAtPosition(position).toString(),
                Toast.LENGTH_SHORT).show();

    }
    //checks whether edittext is empty or not
    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }
}
