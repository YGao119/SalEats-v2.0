package models;

import util.YelpAPIParser;

import java.util.LinkedList;
import java.util.List;

public class DriverInformation {

    private final Location HQLocation;
    private final LinkedList<Order> orders;

    public DriverInformation(DeliveryInformation info) {
        this.orders = new LinkedList<>();
        this.HQLocation = info.getLocation();
        for (int i = 0; i != info.getItems().size(); i++) {
            String restaurant = info.getRestaurants().get(i);
            String item = info.getItems().get(i);
            Location location = YelpAPIParser.getLocationSafe(restaurant, HQLocation);
            orders.add(new Order(restaurant, item, location, 0));
        }
        reorder(HQLocation);
    }

    public List<Order> getOrders() {
        return orders;
    }

    public Order getNext() {
        if (orders.size() == 0) {
            return null;
        } else {
            return orders.pollFirst();
        }
    }

    public void reorder(Location newLocation) {
        for (Order order : orders) {
            order.recalcDistance(newLocation);
        }
        orders.sort((o1, o2) -> {
            if (o1.getDistance() > o2.getDistance())
                return 1;
            else if (o1.getDistance() == o2.getDistance())
                return o1.getRestaurantName().compareTo(o2.getRestaurantName());
            else
                return -1;
        });
    }

    public Location getHQLocation() {
        return HQLocation;
    }

}
