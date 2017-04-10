package com.eternal.hefengweather.api;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Url;
import rx.Observable;

/**
 * @author 邱永恒
 * @time 2017/4/10 21:15
 * @desc ${TODO}
 */

public interface BingPicApi {
    @GET
    Observable<ResponseBody> getBingPic (@Url String url);
}
