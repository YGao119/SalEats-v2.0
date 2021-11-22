package util;

import com.google.gson.*;
import models.Location;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Objects;

public class YelpAPIParser {
    public static Location getLocation(String restaurant, Location location) {
        try {
            OkHttpClient client = new OkHttpClient().newBuilder().build();
            String builder = "https://api.yelp.com/v3/businesses/search" + "?term=" + restaurant +
                    "&latitude=" + location.getLatitude() + "&longitude=" + location.getLongitude();
            Request request = new Request.Builder().url(builder).method("GET", null).addHeader(
                    "Authorization",
                    "Bearer HFMjs3QoBAIG7WpHv-TOTNM13sLqwnFRR2_vTPJ-KSwv0e3Npna7KbJkZPTKT0Vwa4Sag1GKgGigzUOqtznJ9_kIH912TvvUgczjfshVvYZsvDYZbjBIJ4rN48KEYXYx")
                    .build();
            Response response = client.newCall(request).execute();

            Gson gson = new GsonBuilder().registerTypeAdapter(Location.class, new MyDeserializer()).create();
            return gson.fromJson(Objects.requireNonNull(response.body()).string(), Location.class);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return null;
    }
    
    // handle 500's type error
    public static Location getLocationSafe(String restaurant, Location location) {
    	Location result;
    	while(true) {
    		try {
    			result = getLocation(restaurant, location);
    			return result;
    		}
    		catch(NullPointerException e) {
    			continue;
    		}
    	}
    }
}

// Code adapted from https://stackoverflow.com/questions/23070298/get-nested-json-object-with-gson-using-retrofit
class MyDeserializer implements JsonDeserializer<Location> {
    @Override
    public Location deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) throws JsonParseException {
        JsonElement content = je.getAsJsonObject().getAsJsonArray("businesses").get(0).getAsJsonObject()
                .get("coordinates");

        return new Gson().fromJson(content, Location.class);
    }
}
