package com.develogical;

import com.weather.Day;
import com.weather.Forecast;
import com.weather.Forecaster;
import com.weather.Region;

import java.security.Provider;
import java.util.Date;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;

public class CachingWeatherService implements WeatherService {

    public class ForecastResults {
        public ForecastResults(Forecast forecast, Date timestamp) {
            this.forecast = forecast;
            this.timestamp = timestamp;
        }

        Forecast forecast;

        public Forecast getForecast() {
            return forecast;
        }

        public Date getTimestamp() {
            return timestamp;
        }

        Date timestamp;
    }

    Forecaster forecaster;
    private LinkedHashMap<String, ForecastResults> forecastMap;
    int maxCache;


    public CachingWeatherService(Forecaster forecaster, int i) {
        this.forecaster=forecaster;
        this.forecastMap=new LinkedHashMap<>();
        maxCache=i;
    }

    @Override
    public Date getNow() {
        return new Date(System.currentTimeMillis());
    }

    @Override
    public Forecast forecast(Region region, Day day) {
        String key = region.toString()+day.toString();
        // check exists
        if (forecastMap.containsKey(key)){
            ForecastResults tempResults = forecastMap.get(key);
            // check expiry
            if (getNow().getTime() - tempResults.getTimestamp().getTime() < 3600 * 1000) {
                return tempResults.getForecast();
            }
        }
        Forecast result =forecaster.forecastFor(region, day);
        if (forecastMap.size()>=maxCache){
            forecastMap.remove(forecastMap.entrySet().iterator().next().getKey());
        }
        forecastMap.put(key,new ForecastResults(result, getNow()));
        return result;
    }



}
