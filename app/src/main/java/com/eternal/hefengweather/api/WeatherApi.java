package com.eternal.hefengweather.api;

import com.eternal.hefengweather.bean.weather.HeWeather5;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * @author 邱永恒
 * @time 2017/4/9 11:34
 * @desc ${TODO}
 */

public interface WeatherApi {
    @GET("weather")
    Observable<HeWeather5> getWeather(@Query("city") String cityId, @Query("key") String key);
}
