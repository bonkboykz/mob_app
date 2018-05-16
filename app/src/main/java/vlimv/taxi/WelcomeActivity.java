package vlimv.taxi;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.zl.reik.dilatingdotsprogressbar.DilatingDotsProgressBar;

import br.com.sapereaude.maskedEditText.MaskedEditText;
public class WelcomeActivity extends AppCompatActivity implements ServerRequest.SaveCode {

    DilatingDotsProgressBar progressBar;
    Button next_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        // TODO sockets connection
        ServerSocket.getInstance(this.getApplicationContext()).connect();

        final MaskedEditText phone = findViewById(R.id.phone_input);
        next_btn = findViewById(R.id.button);
        progressBar = findViewById(R.id.progress);

        Log.d("check token on welcome", "welcome token:" + SharedPref.loadToken(this));

        ServerRequest.getInstance(this).getUser(SharedPref.loadToken(this), this, 0);

        if (SharedPref.loadUserType(this).equals("driver")) {
            Intent i = new Intent(this, DriverMainActivity.class);
            startActivity(i);
        } else if (SharedPref.loadUserType(this).equals("passenger")) {
            Intent i = new Intent(this, PassengerMainActivity.class);
            startActivity(i);
        } else if (SharedPref.loadUserType(this).equals("invalid")) {
            Intent i = new Intent(this, PassengerMainActivity.class);
            startActivity(i);
        }

        next_btn.setVisibility(View.VISIBLE);

        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number = phone.getText().toString();
                number = number.replaceAll("[^0-9]", "");
                number = "+" + number;
                String regexStr = "^\\+77[04567][0-9]{8}$";
                if (!number.matches(regexStr)) {
                    Toast.makeText(WelcomeActivity.this, "Формат телефона неверен", Toast.LENGTH_SHORT).show();
                } else {
                    SharedPref.saveNumber(view.getContext(), number);
                    next_btn.setVisibility(View.GONE);
                    progressBar.showNow();
                    ServerRequest.getInstance(view.getContext()).signUp(number, view.getContext());
                }
            }
        });

        //checking network connection
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if ((wifiInfo != null && wifiInfo.isConnected()) || (mobileInfo != null && mobileInfo.isConnected())) {
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Для работы с приложением подключитесь к интернету.")
                    .setCancelable(false)
                    .setPositiveButton("Включить интернет", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            startActivity(new Intent(Settings.ACTION_SETTINGS));
                        }
                    })
                    .setNegativeButton("Выйти из приложения", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finishAffinity();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }
    @Override
    public void onBackPressed() {
        DialogQuitApp d = new DialogQuitApp(this);
        d.showDialog(this);
    }

    @Override
    public void saveCode (String code) {
        next_btn.setVisibility(View.VISIBLE);
        progressBar.hideNow();
//        String lname = SharedPref.loadUserSurname(this);
//        Log.d("saveCode", lname);
//        boolean isUserFilledInfo = !lname.isEmpty() && !lname.contains("default");
        Intent intent = new Intent(getApplicationContext(), RegistrationActivity.class);
        intent.putExtra("CODE", code);
        startActivity(intent);
//        if (!isUserFilledInfo) {
//
//        } else {
//            Intent intent;
//            switch (SharedPref.loadUserType(this)) {
//                case "invalid":
//                case "passenger":
//                    intent = new Intent(getApplicationContext(), PassengerMainActivity.class);
//                    intent.putExtra("CODE", code);
//                    startActivity(intent);
//                    break;
//                case "driver":
//                    intent = new Intent(getApplicationContext(), DriverMainActivity.class);
//                    intent.putExtra("CODE", code);
//                    startActivity(intent);
//                    break;
//                default:
//                    Log.e("saveCode", "No user type in saveCode");
//            }
//        }
    }

    @Override
    public void tryAgain () {
        Toast.makeText(this, "Что-то пошло не так. Попробуйте еще раз.", Toast.LENGTH_LONG).show();
        next_btn.setVisibility(View.VISIBLE);
        progressBar.hideNow();
    }

}
