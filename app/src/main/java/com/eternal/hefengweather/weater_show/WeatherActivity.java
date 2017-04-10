package com.eternal.hefengweather.weater_show;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.eternal.hefengweather.R;
import com.eternal.hefengweather.bean.weather.Forecast;
import com.eternal.hefengweather.bean.weather.Weather;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author 邱永恒
 * @time 2017/4/9 10:55
 * @desc ${TODO}
 */

public class WeatherActivity extends AppCompatActivity implements WeatherContract.View {
    @BindView(R.id.nav_button)
    Button mNavButton;
    @BindView(R.id.title_city)
    TextView mTitleCity;
    @BindView(R.id.title_update_time)
    TextView mTitleUpdateTime;
    @BindView(R.id.degree_text)
    TextView mDegreeText;
    @BindView(R.id.weather_info_text)
    TextView mWeatherInfoText;
    @BindView(R.id.forecast_layout)
    LinearLayout mForecastLayout;
    @BindView(R.id.aqi_text)
    TextView mAqiText;
    @BindView(R.id.pm25_text)
    TextView mPm25Text;
    @BindView(R.id.comfort_text)
    TextView mComfortText;
    @BindView(R.id.car_wash_text)
    TextView mCarWashText;
    @BindView(R.id.sport_text)
    TextView mSportText;
    @BindView(R.id.weather_layout)
    ScrollView mWeatherLayout;
    @BindView(R.id.bing_pic_img)
    ImageView mBingPicImg;
    private WeatherContract.Presenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 实现背景图与状态栏融合
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);
        ButterKnife.bind(this);
        new WeatherPresenter(this, this);
        // 加载数据
        presenter.start();
    }

    @Override
    public void setPresenter(WeatherContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void initViews(View view) {

    }

    @Override
    public void showError() {
        Snackbar.make(mWeatherLayout, R.string.error, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        presenter.refresh();
                    }
                }).show();
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void stopLoading() {

    }

    @Override
    public void hideWeatherLayout() {
        mWeatherLayout.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showResults(Weather weather) {
        showWeatherInfo(weather);
    }

    @Override
    public void showBackground(String picUrl) {
        Glide.with(this)
                .load(picUrl)
                .into(mBingPicImg);
    }

    /**
     * 处理并展示Weather实体类中的数据。
     */
    private void showWeatherInfo(Weather weather) {
        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature + "℃";
        String weatherInfo = weather.now.more.info;
        mTitleCity.setText(cityName);
        mTitleUpdateTime.setText(updateTime);
        mDegreeText.setText(degree);
        mWeatherInfoText.setText(weatherInfo);
        mForecastLayout.removeAllViews();
        for (Forecast forecast : weather.forecastList) {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, mForecastLayout, false);
            TextView dateText = (TextView) view.findViewById(R.id.date_text);
            TextView infoText = (TextView) view.findViewById(R.id.info_text);
            TextView maxText = (TextView) view.findViewById(R.id.max_text);
            TextView minText = (TextView) view.findViewById(R.id.min_text);
            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max);
            minText.setText(forecast.temperature.min);
            mForecastLayout.addView(view);
        }
        if (weather.aqi != null) {
            mAqiText.setText(weather.aqi.city.aqi);
            mPm25Text.setText(weather.aqi.city.pm25);
        }
        String comfort = "舒适度：" + weather.suggestion.comfort.info;
        String carWash = "洗车指数：" + weather.suggestion.carWash.info;
        String sport = "运行建议：" + weather.suggestion.sport.info;
        mComfortText.setText(comfort);
        mCarWashText.setText(carWash);
        mSportText.setText(sport);
        mWeatherLayout.setVisibility(View.VISIBLE);
    }
}
