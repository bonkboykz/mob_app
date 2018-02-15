package vlimv.taxi;

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
}
