package vlimv.taxi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;
import com.zl.reik.dilatingdotsprogressbar.DilatingDotsProgressBar;

import java.util.Calendar;

public class DriverRegActivity extends AppCompatActivity implements ServerRequest.NextActivity {


    EditText name, surname, date, carNumber, carYear;
    String nameText, surnameText, genderText, carText, carModelText, carTypeText, carNumberText, carYearText;
    //static Driver driver;
    int age;
    DilatingDotsProgressBar progressBar;
    Button next_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_reg);

        progressBar = findViewById(R.id.progress);

        //Initializing edit texts
        name = findViewById(R.id.name);
        surname = findViewById(R.id.surname);
        carNumber = findViewById(R.id.car_number);
        carYear = findViewById(R.id.car_year);

        MaterialBetterSpinner spinner_gender = findViewById(R.id.gender);
        ArrayAdapter<CharSequence> adapter_gender = ArrayAdapter.createFromResource(this, R.array.gender_array, android.R.layout.simple_spinner_dropdown_item);
        adapter_gender.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_gender.setAdapter(adapter_gender);
        spinner_gender.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                genderText = adapterView.getItemAtPosition(position).toString();
            }
        });

        MaterialBetterSpinner spinner_car = findViewById(R.id.car);
        ArrayAdapter<CharSequence> adapter_car = ArrayAdapter.createFromResource(this, R.array.car_array, android.R.layout.simple_spinner_dropdown_item);
        adapter_car.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_car.setAdapter(adapter_car);
        spinner_car.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                carText = adapterView.getItemAtPosition(position).toString();
            }
        });

        MaterialBetterSpinner spinner_car_model = findViewById(R.id.car_model);
        ArrayAdapter<CharSequence> adapter_car_model = ArrayAdapter.createFromResource(this, R.array.car_model_array, android.R.layout.simple_spinner_dropdown_item);
        adapter_car_model.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_car_model.setAdapter(adapter_car_model);
        spinner_car_model.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                carModelText = adapterView.getItemAtPosition(position).toString();
            }
        });

        MaterialBetterSpinner spinner_car_type = findViewById(R.id.car_type);
        ArrayAdapter<CharSequence> adapter_car_type = ArrayAdapter.createFromResource(this, R.array.car_type_array, android.R.layout.simple_spinner_dropdown_item);
        adapter_car_type.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_car_type.setAdapter(adapter_car_type);
        spinner_car_type.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                carTypeText = adapterView.getItemAtPosition(position).toString();
            }
        });

        final Activity activity = this;

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
                                int birthYear = datePicker.getYear();
                                int birthMonth = datePicker.getMonth();
                                int birthDay = datePicker.getDayOfMonth();
                                calculateAge(birthYear, birthMonth, birthDay);
                            }
                        });
                builder.create();
                builder.show();
            }
        });
        next_btn = findViewById(R.id.button);

        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (age == 0) {
                    Toast.makeText(view.getContext(), "Возраст не может быть меньше одного.", Toast.LENGTH_LONG).show();
                } else if (isEmpty(name) || isEmpty(surname) || genderText == null || carText == null
                        || carModelText == null || carTypeText == null || isEmpty(carNumber) || isEmpty(carYear))
                    Toast.makeText(view.getContext(), "Заполните пустые поля.", Toast.LENGTH_LONG).show();
                else {
                    nameText = name.getText().toString();
                    surnameText = surname.getText().toString();
                    carNumberText = carNumber.getText().toString();
                    carYearText = carYear.getText().toString();
                    if (genderText.equals("Мужской"))
                        genderText = "male";
                    else
                        genderText = "female";

                    next_btn.setVisibility(View.GONE);

                    progressBar.showNow();

                    Car car = new Car(carText, carModelText, carTypeText, carNumberText, Integer.parseInt(carYearText),
                            SharedPref.loadUserId(view.getContext()));

                    ServerRequest.getInstance(view.getContext()).updateDriver(SharedPref.loadUserId(view.getContext()),
                            nameText, surnameText, age, genderText, "driver", car, view.getContext());
                }



//                    driver = new Driver(nameText, surnameText, birthDateText, genderText, carText, carModelText,
//                    driver = new Driver(nameText, surnameText, birthDateText, genderText, carText, carModelText,
//                            carTypeText, carNumberText, carYearText);


            }
        });

        ImageButton back_btn = findViewById(R.id.back_button);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    //checks whether editText is empty or not
    private boolean isEmpty(EditText etText) {
        if (etText.getText().toString().trim().length() > 0)
            return false;

        return true;
    }

    //function to calculate user's age from its birth date
    public void calculateAge(int birthYear, int birthMonth, int birthDay) {
        Calendar calendar = Calendar.getInstance();
        int curYear = calendar.get(Calendar.YEAR);
        int age = curYear - birthYear;
        int curMonth = calendar.get(Calendar.MONTH);
        if (birthMonth > curMonth) {
            age--;
        } else if (curMonth == birthMonth) {
            int curDay = calendar.get(Calendar.DAY_OF_MONTH);
            if (birthDay > curDay) {
                age--;
            }
        }
        this.age = age;
    }

    @Override
    public void goNext() {
        SharedPref.saveUserType(this, "driver");
        Intent intent = new Intent(getApplicationContext(), DriverMainActivity.class);
        startActivity(intent);
    }

    @Override
    public void tryAgain() {
        next_btn.setVisibility(View.VISIBLE);
        Toast.makeText(this, "Не удалось создать пользователя. Попробуйте еще раз.", Toast.LENGTH_LONG).show();
        progressBar.hideNow();
    }
}
