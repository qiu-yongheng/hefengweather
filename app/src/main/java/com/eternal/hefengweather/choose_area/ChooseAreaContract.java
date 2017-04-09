package com.eternal.hefengweather.choose_area;

import com.eternal.hefengweather.BasePresenter;
import com.eternal.hefengweather.BaseView;

import java.util.List;

/**
 * @author 邱永恒
 * @time 2017/4/8 16:35
 * @desc ${TODO}
 */

public interface ChooseAreaContract {
    interface View extends BaseView<Presenter> {
        //显示加载或其他类型的错误
        void showError();
        //显示正在加载
        void showLoading();
        //停止显示正在加载
        void stopLoading();
        //成功获取到数据后, 在界面中显示
        void showResults(List<String> dataList, String provinceName);
    }
    interface Presenter extends BasePresenter {
        //请求数据
        void loadDatas(int position, int currentLevel);
        void refresh();
        int currentLevel();
    }
}
