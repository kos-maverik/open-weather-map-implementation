package weather;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

// cache list manager with useful functions
public class CacheList {
    
    // constant cache filename
    private static final String CACHE_FILE = "dump.json";
    
    // actual cache list, private
    private LinkedList<City> weatherCache = new LinkedList<>();
    
    // singleton design pattern
    private static final CacheList myCacheList = new CacheList();
    
    // private constructor
    private CacheList() {
        weatherCache = new LinkedList<>();
    }
    
    // primary function, global access to list manager
    public static CacheList loadCache()
            throws JsonParseException, JsonMappingException,
            IOException, NullPointerException, ParseException {
        
        // API static dump - cached file
        File dump = new File(CACHE_FILE);

        if ( dump.exists() ) {
            
            // Jackson JSON streaming API
            ObjectMapper weatherMapper = new ObjectMapper();
            JsonParser weatherParser = weatherMapper.getFactory().createParser(dump);

            // JSON file is an array of objects
            if ( weatherParser.nextToken() != JsonToken.START_ARRAY ) {
                throw new JsonParseException( weatherParser , "Invalid JSON" );
            }

            // read each object / cached weather information
            while( weatherParser.nextToken() == JsonToken.START_OBJECT ) {
                // convert object to tree node
                JsonNode node = weatherMapper.readTree(weatherParser);
                // get cached City object
                City cachedCity = weatherMapper.treeToValue(node, City.class);
                // if the cached object has not expired, keep it
                if ( !cachedCity.hasExpired() ) {
                    myCacheList.addCity(cachedCity);
                }
            }

            // check for corrupted cache file
            if ( weatherParser.currentToken() != JsonToken.END_ARRAY 
                || weatherParser.nextToken() != null ) {
                throw new JsonParseException( weatherParser , "Invalid JSON" );
            }
            weatherParser.close();
            
        }
        
        return myCacheList;
    }
    
    public void addCity(City city) {
        
        if ( city.getDateCached() == null ) {
            // city was not previously cached
            SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentDate = myFormat.format(new Date());

            city.setDateCached(currentDate);
        }
        weatherCache.add(city);
    }
    
    public City getCity(City myCity) {
        // return city object if it is cached, or else null
        return (weatherCache.stream()
                .filter(c -> c.getId() == myCity.getId())
                .findFirst().orElse(null));
    }
    
    public void writeToFile() throws IOException {
        // append results to cache file
        ObjectWriter writer = (new ObjectMapper()).writer(
                new DefaultPrettyPrinter());
        writer.writeValue(new File(CACHE_FILE), weatherCache);
    }
}
