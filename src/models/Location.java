package models;

import java.io.Serializable;

public class Location implements Serializable {

    private static final long serialVersionUID = 6174418428708492256L;

    private final double latitude;
    private final double longitude;

    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

	public double getLongitude() {
        return longitude;
    }

}
