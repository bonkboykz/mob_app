package vlimv.taxi;

/**
 * Created by HP on 04-May-18.
 */

public class Address {
    double lat, lng;
    String name;
    String address;
    public Address(double lat, double lng, String name, String address) {
        this.lat = lat;
        this.lng = lng;
        this.name = name;
        this.address = address;
    }
}
