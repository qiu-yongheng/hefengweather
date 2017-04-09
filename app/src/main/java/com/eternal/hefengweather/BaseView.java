package com.eternal.hefengweather;

import android.view.View;

/**
 * @author 邱永恒
 * @time 2017/2/16 20:47
 * @desc view基类
 */

public interface BaseView<T> {
    //为view设置presenter
    void setPresenter(T presenter);
    //初始化界面控件
    void initViews(View view);
}
