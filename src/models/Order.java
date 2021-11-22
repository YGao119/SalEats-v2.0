package models;

import util.DistanceCalc;

public class Order {

    private final String restaurantName;
    private final String itemName;
    private final Location location;
    private double distance;

    public Order(String restaurantName, String itemName, Location location, double distance) {
        this.restaurantName = restaurantName;
        this.itemName = itemName;
        this.location = location;
        this.distance = distance;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public String getItemName() {
        return itemName;
    }

    public Location getLocation() {
        return location;
    }

    public double getDistance() {
        return distance;
    }

    public void recalcDistance(Location newLocation) {
        distance = DistanceCalc.getDistance(location, newLocation);
    }

}
