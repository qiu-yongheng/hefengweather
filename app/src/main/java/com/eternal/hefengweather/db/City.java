package com.eternal.hefengweather.db;

import org.litepal.crud.DataSupport;

/**
 * @author 邱永恒
 * @time 2017/4/8 12:22
 * @desc 市的数据库
 */

public class City extends DataSupport{
    private int id;
    private String cityName;
    private int cityId;
    private int provinceId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }
}
