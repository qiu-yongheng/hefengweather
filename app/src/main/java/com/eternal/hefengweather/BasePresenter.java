package com.eternal.hefengweather;

/**
 * @author 邱永恒
 * @time 2017/2/16 20:47
 * @desc presenter基类
 */

public interface BasePresenter {
    //获取数据并改变界面显示, 在todo-mvp的项目中的调用时机为Fragment的onResume()方法中
    void start();
}
