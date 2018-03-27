package vlimv.taxi;

/**
 * Created by HP on 22-Mar-18.
 */

public class Car {
    String name, model, type, gosNumber;
    int year;
    String id;

    Car (String name, String model, String type, String gosNumber, int year) {
        this.name = name;
        this.model = model;
        this.type = type;
        this.gosNumber = gosNumber;
        this.year = year;
    }
}
