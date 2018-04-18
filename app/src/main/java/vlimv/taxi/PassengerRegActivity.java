package vlimv.taxi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class PassengerRegActivity extends AppCompatActivity implements ServerRequest.NextActivity{
    EditText birthDate, name, surname;
    Button next_btn;
    int age = 0;
    String nameText, surnameText, genderText = null;
    DilatingDotsProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_reg);
        final Activity activity = this;

        progressBar = findViewById(R.id.progress);

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

        //Initializing edit texts
        name = findViewById(R.id.name);
        surname = findViewById(R.id.surname);

        birthDate = findViewById(R.id.birthdate);
        birthDate.setOnClickListener(new View.OnClickListener() {
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
                                PassengerRegActivity.this.birthDate.setText(birthDate);

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
                if (isEmpty(name) || isEmpty(surname) || age == 0 || genderText == null)
                    Toast.makeText(view.getContext(), "Заполните пустые поля.", Toast.LENGTH_LONG).show();
                else {
                    nameText = name.getText().toString();
                    surnameText = surname.getText().toString();
                    next_btn.setVisibility(View.GONE);

                    if (genderText.equals("Мужской"))
                        genderText = "male";
                    else
                        genderText = "female";

                    progressBar.showNow();

                    String id = SharedPref.loadUserId(view.getContext());

                    ServerRequest.getInstance(getBaseContext()).updateUser(id, nameText, surnameText,
                            age, genderText, SharedPref.loadUserType(view.getContext()), view.getContext());
                    //ServerRequest.getInstance(getBaseContext()).getUser(SharedPref.loadToken(view.getContext()), view.getContext());
                }
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

    //checks whether editText is empty or not
    private boolean isEmpty(EditText etText) {
        if (etText.getText().toString().trim().length() > 0)
            return false;

        return true;
    }

    @Override
    public void goNext() {
        Intent intent = new Intent(getApplicationContext(), PassengerMainActivity.class);
        startActivity(intent);
    }
    @Override
    public void tryAgain() {
        next_btn.setVisibility(View.VISIBLE);
        Toast.makeText(this, "Не удалось создать пользователя. Попробуйте еще раз.", Toast.LENGTH_LONG).show();
        progressBar.hideNow();
    }
}
