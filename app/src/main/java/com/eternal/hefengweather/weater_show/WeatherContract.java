package com.eternal.hefengweather.weater_show;

import com.eternal.hefengweather.BasePresenter;
import com.eternal.hefengweather.BaseView;
import com.eternal.hefengweather.bean.weather.Weather;

/**
 * @author 邱永恒
 * @time 2017/4/9 11:24
 * @desc ${TODO}
 */

public interface WeatherContract {
    interface View extends BaseView<Presenter> {
        //显示加载或其他类型的错误
        void showError();
        //显示正在加载
        void showLoading();
        //停止显示正在加载
        void stopLoading();
        void hideWeatherLayout();
        //成功获取到数据后, 在界面中显示
        void showResults(Weather weather);
        //设置背景图
        void showBackground(String picUrl);
    }

    interface Presenter extends BasePresenter {
        //请求数据
        void loadDatas();
        void refresh();
    }
}
