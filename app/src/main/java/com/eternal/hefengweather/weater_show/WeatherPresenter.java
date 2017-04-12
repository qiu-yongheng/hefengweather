package com.eternal.hefengweather.weater_show;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.eternal.hefengweather.api.BingPicApi;
import com.eternal.hefengweather.api.WeatherApi;
import com.eternal.hefengweather.bean.weather.HeWeather5;
import com.eternal.hefengweather.bean.weather.Weather;
import com.eternal.hefengweather.service.AutoUpdateService;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author 邱永恒
 * @time 2017/4/9 11:24
 * @desc ${TODO}
 */

public class WeatherPresenter implements WeatherContract.Presenter {

    private final Context context;
    private final WeatherContract.View view;
    private final String KEY = "50e68789a6924d4db1c9b5d2ce36736a";
    private Gson gson = new Gson();

    public WeatherPresenter(Context context, WeatherContract.View view) {
        this.context = context;
        this.view = view;
        view.setPresenter(this);
    }

    @Override
    public void start() {
        loadDatas();
    }

    @Override
    public void loadDatas() {
        view.showLoading();
        // 获取天气数据的缓存
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String weatherString = prefs.getString("weather", null);
        if (weatherString != null) {
            // 有缓存时直接解析天气数据, 返回解析后的数据
            Weather weather = gson.fromJson(weatherString, Weather.class);
            view.showResults(weather);
            view.stopLoading();

            // 启动定时更新服务
            Intent intent = new Intent(context, AutoUpdateService.class);
            context.startService(intent);
        } else {
            // 无缓存时去服务器查询天气, 返回解析后的数据
            Activity activity = (Activity) context;
            String weatherId = activity.getIntent().getStringExtra("weather_id");
            view.hideWeatherLayout();
            requestWeather(weatherId);
        }

        // 获取背景图的缓存
        String bingPic = prefs.getString("bing_pic", null);
        if (bingPic != null) {
            view.showBackground(bingPic);
        } else {
            loadBingPic();
        }
    }

    /**
     * 获取背景图
     */
    private void loadBingPic() {
        new Retrofit.Builder()
                .baseUrl("http://guolin.tech/api/bing_pic/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
                .create(BingPicApi.class)
                .getBingPic("http://guolin.tech/api/bing_pic")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onCompleted() {
                        loadDatas();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        try {
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
                            editor.putString("bing_pic", responseBody.string());
                            editor.apply();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * 网络请求
     *
     * @param weatherId
     */
    private void requestWeather(final String weatherId) {
        new Retrofit.Builder()
                .baseUrl("https://free-api.heweather.com/v5/")
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(WeatherApi.class)
                .getWeather(weatherId, KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<HeWeather5>() {
                    @Override
                    public void onCompleted() {
                        loadDatas();
                    }

                    @Override
                    public void onError(Throwable e) {
                        view.stopLoading();
                        view.showError();
                    }

                    @Override
                    public void onNext(HeWeather5 heWeather5) {
                        Weather weather = heWeather5.getHeWeather5().get(0);
                        if (weather != null && "ok".equals(weather.status)) {
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
                            editor.putString("weather", gson.toJson(weather));
                            editor.apply();
                        }
                    }
                });
        loadBingPic();
    }

    @Override
    public void refresh() {
        Activity activity = (Activity) context;
        String weatherId = activity.getIntent().getStringExtra("weather_id");
        requestWeather(weatherId);
    }

    @Override
    public void refresh(String weatherId) {
        requestWeather(weatherId);
    }
}
