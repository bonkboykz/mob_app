package vlimv.taxi;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

/**
 * Created by HP on 15.02.2018.
 */

public class User {
    static String name, surname, phoneNumber, gender, type;
    int age;
    public User (String t, String n, String sn, int a, String g, String phone) {
        type = t;
        name = n;
        surname = sn;
        gender = g;
        age = a;
    }
}
