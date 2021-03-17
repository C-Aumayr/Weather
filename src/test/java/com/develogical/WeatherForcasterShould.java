package com.develogical;

import com.weather.Day;
import com.weather.Forecast;
import com.weather.Forecaster;
import com.weather.Region;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Date;
import java.util.Random;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class WeatherForcasterShould {

    Forecaster mockForecaster = mock(Forecaster.class);
    WeatherService testObject = new CachingWeatherService(mockForecaster, 3);

    @Test
    public void passOnWeatherRequest() throws Exception {
        Forecast testForcast = new Forecast("Test", 123423);
        given(mockForecaster.forecastFor(any(), any())).willReturn(testForcast);
        Region region = Region.values()[new Random().nextInt(Region.values().length)];
        Day day = Day.values()[new Random().nextInt(Day.values().length)];

        testObject.forecast(region, day);
        verify(mockForecaster).forecastFor(eq(region), eq(day));

    }

    @Test
    public void returnAForecast() throws Exception {
        Forecast testForcast = new Forecast("Test", 123423);
        given(mockForecaster.forecastFor(any(), any())).willReturn(testForcast);
        Forecast result=testObject.forecast(Region.LONDON, Day.MONDAY);
        assertThat(result.summary(), equalTo("Test"));
        assertThat(result.temperature(), equalTo(123423));
    }

    @Test
    public void willCacheObject() throws Exception{
        Forecast testForcast = new Forecast("TestNew123123123", 79);
        given(mockForecaster.forecastFor(any(), any())).willReturn(testForcast);
        testObject.forecast(Region.LONDON, Day.MONDAY);
        verify(mockForecaster, Mockito.times(1)).forecastFor(any(), any());

        Forecast result=testObject.forecast(Region.LONDON, Day.MONDAY);
        verifyNoMoreInteractions(mockForecaster);
        assertThat(result.summary(), equalTo("TestNew123123123"));
        assertThat(result.temperature(), equalTo(79));
    }

    @Test
    public void willOnlyCacheLimitedAmount() throws Exception{
        Forecast testForcast = new Forecast("TestNew123123123", 79);
        given(mockForecaster.forecastFor(any(), any())).willReturn(testForcast);
        testObject.forecast(Region.LONDON, Day.MONDAY);
        testObject.forecast(Region.EDINBURGH, Day.TUESDAY);
        testObject.forecast(Region.LONDON, Day.WEDNESDAY);
        testObject.forecast(Region.EDINBURGH, Day.WEDNESDAY);
        testObject.forecast(Region.LONDON, Day.MONDAY);
        verify(mockForecaster, Mockito.times(5)).forecastFor(any(), any());
        testObject.forecast(Region.EDINBURGH, Day.WEDNESDAY);
        verifyNoMoreInteractions(mockForecaster);

    }

    @Test
    public void willOnlyCacheWithinLimitedTime() throws Exception {
        // options
        // 1. override test object- getNow

        testObject = new CachingWeatherService(mockForecaster, 3) {
            boolean flag = true;
            @Override
            public Date getNow() {
                if (flag) {
                    flag = false;
                    return new Date(System.currentTimeMillis() - 3600 * 1000);
                } else {
                    return super.getNow();
                }
            }
        };

        Forecast testForcast = new Forecast("TestNew123123123", 79);
        testObject.forecast(Region.LONDON, Day.MONDAY);
        testObject.forecast(Region.LONDON, Day.MONDAY);
        verify(mockForecaster, Mockito.times(2)).forecastFor(any(), any());
    }

}
