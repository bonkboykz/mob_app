package vlimv.taxi;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.util.Calendar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

public class PassengerActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    EditText birthdate;
    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger);
        final Activity activity = this;

        MaterialBetterSpinner spinner_gender = findViewById(R.id.gender);
        ArrayAdapter<CharSequence> adapter_gender = ArrayAdapter.createFromResource(this, R.array.gender_array, android.R.layout.simple_spinner_dropdown_item);
        adapter_gender.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_gender.setAdapter(adapter_gender);
        spinner_gender.setOnItemSelectedListener(this);

        btn = findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PassengerMapsActivity.class);
                startActivity(intent);
            }
        });

        birthdate = findViewById(R.id.birthdate);
        birthdate.setOnClickListener(new View.OnClickListener() {
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
                                birthdate.setText(birthDate);
                            }
                        });
                builder.create();
                builder.show();
            }
        });

    }
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        Toast.makeText (getApplicationContext(), "Selected: " + parent.getItemAtPosition(pos).toString(), Toast.LENGTH_SHORT).show();
        String gender = parent.getItemAtPosition(pos).toString();
    }
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }
}
