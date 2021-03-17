package com.develogical;

import com.weather.Day;
import com.weather.Forecast;
import com.weather.Forecaster;
import com.weather.Region;

import java.security.Provider;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;

public class CachingWeatherService implements WeatherService {


    Forecaster forecaster;
    LinkedHashMap<String, Forecast> forecastMap;
    int maxCache;


    public CachingWeatherService(Forecaster forecaster, int i) {
        this.forecaster=forecaster;
        this.forecastMap=new LinkedHashMap<>();
        maxCache=i;
    }

    @Override
    public Forecast forecast(Region region, Day day) {
        String key = region.toString()+day.toString();
        if (forecastMap.containsKey(key)){
            return forecastMap.get(key);
        }
        Forecast result =forecaster.forecastFor(region, day);
        if (forecastMap.size()>=maxCache){
            forecastMap.remove(forecastMap.entrySet().iterator().next().getKey());
        }
        forecastMap.put(key,result);
        return result;
    }



}
