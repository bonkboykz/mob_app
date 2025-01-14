package vlimv.taxi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zl.reik.dilatingdotsprogressbar.DilatingDotsProgressBar;

public class RegistrationActivity extends Activity implements ServerRequest.NextActivity, ServerRequest.SaveCode {

    DilatingDotsProgressBar progressBar;
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        final EditText editTextCode = findViewById(R.id.code);

        TextView tryAgain = findViewById(R.id.try_again);
        tryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "Новый код активации в пути.", Toast.LENGTH_LONG).show();
                String number = SharedPref.loadNumber(view.getContext());
                ServerRequest.getInstance(view.getContext()).signUp(number, view.getContext());
            }
        });

        progressBar = findViewById(R.id.progress);

        btn = findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                String code = editTextCode.getText().toString();
                ServerRequest.getInstance(view.getContext()).verify(code, view.getContext());
                btn.setVisibility(View.GONE);
                progressBar.showNow();
//                if (editTextCode.getText().toString().equals(code)) {
//                    ServerRequest.getInstance(view.getContext()).verify(code, view.getContext());
//                    btn.setVisibility(View.GONE);
//                    progressBar.showNow();
//                } else {
//                    Toast.makeText(view.getContext(), "Неправильный код.", Toast.LENGTH_LONG).show();
//                }
            }
        });
    }

//    @Override
//    public void onBackPressed() {
//        DialogQuitApp d = new DialogQuitApp(this);
//        d.showDialog(this);
//    }

    @Override
    public void goNext() {
        btn.setVisibility(View.VISIBLE);
        progressBar.hideNow();
        Log.d("After verify | goNext", SharedPref.loadIsRegistered(this) + "");
        if (SharedPref.loadIsRegistered(this)) {
            String type = SharedPref.loadUserType(this);
            if (type.equals("driver")) {
                Intent i = new Intent(this, DriverMainActivity.class);
                startActivity(i);
            } else if (type.equals("passenger")) {
                Intent i = new Intent(this, PassengerMainActivity.class);
                startActivity(i);
            } else if (type.equals("invalid")) {
                Intent i = new Intent(this, PassengerMainActivity.class);
                startActivity(i);
            }
        } else {
            Intent intent = new Intent(getApplicationContext(), AccountTypeActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void saveCode(String code) {

    }

    @Override
    public void tryAgain() {
        btn.setVisibility(View.VISIBLE);
        Toast.makeText(this, "Что-то пошло не так. Попробуйте еще раз.", Toast.LENGTH_LONG).show();
        progressBar.hideNow();
    }


}
