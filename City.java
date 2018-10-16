package weather;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// name and country are saved in the json cache file for easier readability
// they are not used while parsing cached data
@JsonIgnoreProperties({"longitude", "latitude", "cached"})

public class City {

    // default id -1, city does not exist
    private int id = -1;
    private String name = null;
    private String country = null;
    private double longitude = 0;
    private double latitude = 0;
    private String weather = null;
    private String description = null;
    private float temperature = 0;

    // "saved in dump file" flag
    // used for pretty printing
    private boolean cached = false;
    // date cached, used for expiring
    private String dateCached = null;
    // constant variable, cached information is valid for 30 minutes
    private static final int MINUTES_TO_LIVE = 30;
    
    
    // check if cache item has expired
    public boolean hasExpired() throws ParseException {
        SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date cachedDate = myFormat.parse(dateCached);
        Date currentDate = new Date();
        long diff = currentDate.getTime() - cachedDate.getTime();
        // minutes that have passed since caching
        long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
        
        return ( minutes > MINUTES_TO_LIVE );
    }

    // automated getters and setters
    // needed for Jackson API

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public boolean isCached() {
        return cached;
    }

    public void setCached(boolean cached) {
        this.cached = cached;
    }

    public String getDateCached() {
        return dateCached;
    }

    public void setDateCached(String dateCached) {
        this.dateCached = dateCached;
    }
    
}
