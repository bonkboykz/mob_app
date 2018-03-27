package vlimv.taxi;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

/**
 * Created by HP on 15.02.2018.
 */

public class Driver {
    static String name, surname, birthDate, carType, carModel, car, carNumber, phoneNumber, gender,
            carYear;
    public Driver (String n, String sn, String bd, String g, String c, String cm,
                   String ct, String cn, String cy) {
        name = n;
        surname = sn;
        birthDate = bd;
        gender = g;
        car = c;
        carType = ct;
        carModel = cm;
        carNumber = cn;
        carYear = cy;
    }

    public Driver (String n, String sn) {
        name = n;
        surname = sn;
    }
}
