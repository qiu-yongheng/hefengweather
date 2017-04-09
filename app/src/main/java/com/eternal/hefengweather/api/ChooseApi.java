package com.eternal.hefengweather.api;

import com.eternal.hefengweather.bean.CountyBean;
import com.eternal.hefengweather.bean.ProvinceBean;

import java.util.ArrayList;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Url;
import rx.Observable;

/**
 * @author 邱永恒
 * @time 2017/4/8 22:30
 * @desc ${TODO}
 */

public interface ChooseApi {
    @GET
    Observable<ArrayList<ProvinceBean>> getProvince(@Url String url);

    @GET("{id}")
    Observable<ArrayList<ProvinceBean>> getCity(@Path("id") int id);

    @GET("{city_id}/{county_id}")
    Observable<ArrayList<CountyBean>> getCounty(@Path("city_id") int city_id, @Path("county_id") int county_id);
}
