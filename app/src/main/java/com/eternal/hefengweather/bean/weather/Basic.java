package com.eternal.hefengweather.bean.weather;

import com.google.gson.annotations.SerializedName;

/**
 * @author 邱永恒
 * @time 2017/4/9 10:44
 * @desc ${TODO}
 */

public class Basic {
    @SerializedName("city")
    public String cityName;
    @SerializedName("id")
    public String weatherId;
    public Update update;

    public class Update {
        @SerializedName("loc")
        public String updateTime;
    }
}
