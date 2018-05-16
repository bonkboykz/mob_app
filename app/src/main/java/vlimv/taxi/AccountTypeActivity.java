package vlimv.taxi;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class AccountTypeActivity extends AppCompatActivity implements View.OnClickListener{
    TextView driver, passenger, invalid;
    String type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_type);
        driver = findViewById(R.id.driver);
        passenger = findViewById(R.id.passenger);
        invalid = findViewById(R.id.invalid);
        driver.setOnClickListener(this);
        passenger.setOnClickListener(this);
        invalid.setOnClickListener(this);
        Log.d("acc type token", "acc type token:" + SharedPref.loadToken(this));
        if (SharedPref.loadToken(this) == null && SharedPref.loadToken(this).isEmpty()) {
            Intent i = new Intent(this, WelcomeActivity.class);
            startActivity(i);
        }
        Log.d("user", "pas in acc type");


        final ImageButton next_btn = findViewById(R.id.button_next);
        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {;
                Intent intent;
                if (type == null) {
                    Toast.makeText(getApplicationContext(), "Выберите тип аккаунта", Toast.LENGTH_LONG).show();
                } else if (type.equals("Пассажир")) {
                    intent = new Intent(getApplicationContext(), PassengerRegActivity.class);
                    SharedPref.saveUserType(view.getContext(), "passenger");
                    startActivity(intent);
                } else if (type.equals("Пассажир с инвалидностью")) {
                    intent = new Intent(getApplicationContext(), PassengerRegActivity.class);
                    SharedPref.saveUserType(view.getContext(), "invalid");
                    startActivity(intent);
                } else if (type.equals("Водитель")){
                    intent = new Intent(getApplicationContext(), DriverRegActivity.class);
                    SharedPref.saveUserType(view.getContext(), "driver");
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Выберите тип аккаунта", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.driver:
                type = "Водитель";
                driver.setTextColor(Color.parseColor("#25d485"));
                passenger.setTextColor(Color.parseColor("#000000"));
                invalid.setTextColor(Color.parseColor("#000000"));
                break;
            case R.id.passenger:
                type = "Пассажир";
                driver.setTextColor(Color.parseColor("#000000"));
                passenger.setTextColor(Color.parseColor("#25d485"));
                invalid.setTextColor(Color.parseColor("#000000"));
                break;
            case R.id.invalid:
                type = "Пассажир с инвалидностью";
                driver.setTextColor(Color.parseColor("#000000"));
                passenger.setTextColor(Color.parseColor("#000000"));
                invalid.setTextColor(Color.parseColor("#25d485"));
                break;
        }
    }
}
