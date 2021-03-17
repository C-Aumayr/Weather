package com.develogical;

import com.weather.Day;
import com.weather.Forecast;
import com.weather.Region;

import java.util.Date;

public interface WeatherService {
    public Date getNow();
    public Forecast forecast (Region region, Day day);

}
