# OpenWeatherMap

Displays weather information from openweathermap.org.

This project consists of 3 parts:
1. Json parsing
2. OpenWeatherMap API call
3. Data caching

## Installation

You will need the Jackson API. There are two ways to get it:
1. Download the following JAR files:
    [jackson-core](https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-core/latest), [jackson-databind](https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind/latest), [jackson-annotations](https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-annotations/latest)
    and add them to the classpath of your project (add them to your IDE Libraries).

2. Add the following Maven Dependencies to the project's pom.xml file:
    ```
    <dependency>
      <groupId>com.fasterxml.jackson.core</groupId>
      <artifactId>jackson-core</artifactId>
      <version>2.9.7</version>
    </dependency>
    
    <dependency>
      <groupId>com.fasterxml.jackson.core</groupId>
      <artifactId>jackson-annotations</artifactId>
      <version>2.9.7</version>
    </dependency>
    
    <dependency>
      <groupId>com.fasterxml.jackson.core</groupId>
      <artifactId>jackson-databind</artifactId>
      <version>2.9.7</version>
    </dependency>
    ```

## Usage

You can use the executable jar file to get coordinates and country information for a city, by searching the JSON file provided by [OpenWeatherMap](http://bulk.openweathermap.org/sample/city.list.json.gz).
The first exact match is returned.
You can run the file by writing:
```
Weather.jar <jsonfile> <cityname>
```

If the city exists in the JSON file, you can run the file to get weather information about that city by writing:
```
Weather.jar <jsonfile> <cityname> -w
```

The results are then saved in a local cache file and are valid for 30 minutes. If the same weather information is requested later, the results are retrieved directly from the cache, without using the API.

## Example

```
> Weather.jar city.list.json Kerkyra -w

City      : Kerkyra
Country   : GR
Longitude : 19.91972
Latitude  : 39.619999

********************************
*  Online weather information  *
********************************

Location    : Kerkyra, GR
Weather     : Clouds
Description : few clouds
Temperature : 24.0 C
```
