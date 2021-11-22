package models;

import java.io.Serializable;
import java.util.List;

public class DeliveryInformation implements Serializable {

    private static final long serialVersionUID = 383927069613799417L;

    private final List<String> restaurants;
    private final List<String> items;

    private final Location location;

    public DeliveryInformation(List<String> restaurants, List<String> items, Location location) {
        this.restaurants = restaurants;
        this.location = location;
        this.items = items;
    }

    public List<String> getRestaurants() {
        return restaurants;
    }

    public List<String> getItems() {
        return items;
    }

    public Location getLocation() {
        return location;
    }

}
