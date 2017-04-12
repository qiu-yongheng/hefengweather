package com.eternal.hefengweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.eternal.hefengweather.api.BingPicApi;
import com.eternal.hefengweather.api.WeatherApi;
import com.eternal.hefengweather.bean.weather.HeWeather5;
import com.eternal.hefengweather.bean.weather.Weather;
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
 * @time 2017/4/12 20:21
 * @desc ${TODO}
 */

public class AutoUpdateService extends Service {
    private Gson gson = new Gson();
    private final String KEY = "50e68789a6924d4db1c9b5d2ce36736a";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("==", "后台任务已创建");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("==", "后台任务已启动");
        updateWeather();
        updateBingPic();
        // 创建定时任务
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour = 8 * 60 * 60 * 1000;
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        Intent i = new Intent(this, AutoUpdateService.class);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        return super.onStartCommand(intent, flags, startId);
    }

    private void updateBingPic() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather", null);
        if (weatherString != null) {
            // 有缓存时直接解析天气数据, 返回解析后的数据
            Weather weather = gson.fromJson(weatherString, Weather.class);
            new Retrofit.Builder()
                    .baseUrl("https://free-api.heweather.com/v5/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build()
                    .create(WeatherApi.class)
                    .getWeather(weather.basic.weatherId, KEY)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<HeWeather5>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(HeWeather5 heWeather5) {
                            Weather weather = heWeather5.getHeWeather5().get(0);
                            if (weather != null && "ok".equals(weather.status)) {
                                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                                editor.putString("weather", gson.toJson(weather));
                                editor.apply();
                            }
                        }
                    });
        }
    }

    private void updateWeather() {
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
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        try {
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                            editor.putString("bing_pic", responseBody.string());
                            editor.apply();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
