package com.develogical;

import com.weather.Day;
import com.weather.Forecast;
import com.weather.Region;

public interface WeatherService {
    public Forecast forecast (Region region, Day day);

}
