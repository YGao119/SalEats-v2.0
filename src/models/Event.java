package models;

public class Event {

    private final int time;
    private final String startLocation;
    private final String itemName;

    public Event(int time, String startLocation, String itemName) {
        this.time = time;
        this.startLocation = startLocation;
        this.itemName = itemName;
    }

    public int getTime() {
        return time;
    }

    public String getStartLocation() {
        return startLocation;
    }

    public String getItemName() {
        return itemName;
    }

}
