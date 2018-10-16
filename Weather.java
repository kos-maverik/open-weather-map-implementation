package weather;

import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.Normalizer;
import java.text.ParseException;
import java.net.URL;
import java.net.HttpURLConnection;
import javax.xml.ws.http.HTTPException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;



public class Weather {

    public static City cityInformation( String json, String cityName )
            throws JsonParseException, JsonMappingException,
            IOException, NullPointerException {
        /***********************
              Assignment 1      
        ***********************/         

        // city information
        City city = new City();

        // Jackson JSON streaming API
        ObjectMapper cityMapper = new ObjectMapper();
        JsonParser cityParser = cityMapper.getFactory().createParser(
                new File(json));

        // JSON file is an array of objects
        if ( cityParser.nextToken() != JsonToken.START_ARRAY ) {
            throw new JsonParseException( cityParser , "Invalid JSON" );
        }

        // read each object / city
        while( cityParser.nextToken() == JsonToken.START_OBJECT ) {
            // convert object to tree node
            JsonNode node = cityMapper.readTree(cityParser);

            // city name without accents
            // input arguments need not have accents
            String cityNoAccents = Normalizer.normalize(
                    node.get("name").textValue(),Normalizer.Form.NFD
            ).replaceAll("[\\p{InCombiningDiacriticalMarks}]", ""); 

            // city exists in json
            if ( cityName.equalsIgnoreCase(cityNoAccents) ) {
                /* NOTE: Only the last occurence of the city name is saved.
                 It is possible to save a list of the cities matching the name,
                 but it is out of the assignment's scope. */

                // saving values
                city.setId(node.get("id").intValue());
                city.setName(cityNoAccents);
                city.setCountry(node.get("country").textValue());
                city.setLongitude(node.get("coord").get("lon").doubleValue());
                city.setLatitude(node.get("coord").get("lat").doubleValue());
            }
        }
        // check for invalid JSON or unexpected EOF
        if ( cityParser.getCurrentToken() != JsonToken.END_ARRAY
            || cityParser.nextToken() != null ) {
            throw new JsonParseException( cityParser , "Invalid JSON" );
        }
        cityParser.close();

        return city;
    }

    

    
    public static City weatherInformation( City myCity )
            throws JsonParseException, JsonMappingException,
            IOException, NullPointerException, HTTPException, ParseException {
        /***********************
              Assignment 2      
        ***********************/

        // cached list manager with weather information
        CacheList weatherCache = CacheList.loadCache();
        // get cached object if exists, otherwise null
        City cachedCity = weatherCache.getCity(myCity);
        
        if ( cachedCity != null ) {
            // weather information is cached
            // pull results from the cache file
            myCity.setWeather(cachedCity.getWeather());
            myCity.setDescription(cachedCity.getDescription());
            myCity.setTemperature(cachedCity.getTemperature());
            myCity.setCached(true);
        }
        else {
            // API call
            /* NOTE: We prefer using the city ID instead of its name, because many
             names have accented characters which may not get encoded correctly,
             depending on the Operating System's language */
            String api = "36ecff0479fb070c0d2ec301883e85e3";
            String urlString =
                    "http://api.openweathermap.org/data/2.5/weather?id=" + myCity.getId()
                    + "&appid=" + api
                    + "&units=metric";

            // connect to API
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();

            // check for connection errors
            if ( connection.getResponseCode() != 200 ) {
                throw new HTTPException(0);
            }
            
            // read JSON result
            BufferedReader weatherReader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            String weatherJson = weatherReader.readLine();

            // convert JSON string to tree node
            JsonNode node = (new ObjectMapper()).readTree(weatherJson);

            // parse live results
            myCity.setWeather(node.get("weather").elements().next()
                            .get("main").textValue());
            myCity.setDescription(node.get("weather").elements().next()
                            .get("description").textValue());
            myCity.setTemperature(node.get("main").get("temp").floatValue());

            // append results to cached list
            weatherCache.addCity(myCity);
            weatherReader.close();
        }

        weatherCache.writeToFile();
        return myCity;
    }


    public static void printResults ( City city, boolean weather ) {
        if ( city.getId() == -1 ) {
            System.out.println("City does not exist.");
        }
        else {
            System.out.println("City      : " + city.getName());
            System.out.println("Country   : " + city.getCountry());
            System.out.println("Longitude : " + city.getLongitude());
            System.out.println("Latitude  : " + city.getLatitude());
        }
        if ( weather ) {
            // print weather information
            System.out.println("\n********************************");
            if ( city.isCached() ) {
                System.out.println("* Offline weather information! *");
            } else {
                System.out.println("*  Online weather information  *");
            }
            System.out.println("********************************\n");

            System.out.println("Location    : " + city.getName() + ", " + city.getCountry());
            System.out.println("Weather     : " + city.getWeather());
            System.out.println("Description : " + city.getDescription());
            System.out.println("Temperature : " + city.getTemperature() + " C");
        }
    }
    
    
    public static void main( String[] args ) {
            // error handling
            if ( (args.length < 2) || (args.length > 3) ||
               ( (args.length == 3) && !("-w".equals(args[2])) ) ) {
                    System.err.println("Wrong input format:");
                    System.err.println("Weather.jar <jsonfile> <cityname> [-w]");
                    System.exit(0);
            }
            // json file name
            String json = args[0];
            // city name
            String city = args[1];
            // weather info requested
            boolean weather = false;

            try {
                // assignment 1
                City myCity = cityInformation(json, city);
                // if weather information is requested
                if ( (myCity.getId() != -1) && (args.length == 3) ) {
                    weather = true;
                    // assignment 2
                    myCity = weatherInformation(myCity);
                }

                // print everything
                printResults(myCity, weather);
                
            }
            // error handling
            catch (JsonParseException e) {
                System.err.println("The JSON file is invalid.");
            } catch (JsonMappingException e) {
                System.err.println("The data format is invalid.");
            } catch (IOException e) {
                System.err.println("File does not exist.");
            } catch (NullPointerException e) {
                System.err.println("Error while parsing JSON.");
            } catch (ParseException e) {
                System.err.println("Error while parsing date.");
            } catch (HTTPException e) {
                System.err.println("Connection to OpenWeatherMap failed.");
            }
        }
}
