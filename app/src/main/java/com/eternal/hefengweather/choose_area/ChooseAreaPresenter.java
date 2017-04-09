package com.eternal.hefengweather.choose_area;

import android.content.Context;
import android.util.Log;

import com.eternal.hefengweather.api.ChooseApi;
import com.eternal.hefengweather.bean.CountyBean;
import com.eternal.hefengweather.bean.ProvinceBean;
import com.eternal.hefengweather.db.City;
import com.eternal.hefengweather.db.County;
import com.eternal.hefengweather.db.Province;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * @author 邱永恒
 * @time 2017/4/8 17:08
 * @desc ${TODO}
 */

public class ChooseAreaPresenter implements ChooseAreaContract.Presenter {
    private final Context context;
    private final ChooseAreaContract.View view;
    private static final int LEVEL_PROVINCE = 1;
    private static final int LEVEL_CITY = 2;
    private static final int LEVEL_COUNTY = 3;
    private List<String> dataList = new ArrayList<>();
    /**
     * 当前选中的级别
     */
    private int currentLevel = 0;
    private List<Province> mList;
    private List<City> mCities;
    private int mProvinceCode;
    private int mCityId;


    public ChooseAreaPresenter(Context context, ChooseAreaContract.View view) {
        this.context = context;
        this.view = view;
        view.setPresenter(this);
    }

    @Override
    public void start() {
        loadDatas(0, 0);
    }

    /**
     * 通过网络请求数据
     *
     * @param position
     * @param currentL
     */
    private void queryFromServer(final int position,final int currentL) {
        switch (currentL) {
            case 0:
                new Retrofit.Builder()
                        .baseUrl("http://guolin.tech/api/china/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                        .build()
                        .create(ChooseApi.class)
                        .getProvince("http://guolin.tech/api/china")
                        .flatMap(new Func1<ArrayList<ProvinceBean>, Observable<ProvinceBean>>() {
                            @Override
                            public Observable<ProvinceBean> call(ArrayList<ProvinceBean> provinceBeen) {
                                return Observable.from(provinceBeen);
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<ProvinceBean>() {
                            @Override
                            public void onCompleted() {
                                loadDatas(position, currentL);
                            }

                            @Override
                            public void onError(Throwable e) {
                                view.stopLoading();
                                view.showError();
                            }

                            @Override
                            public void onNext(ProvinceBean provinceBean) {
                                Province province = new Province();
                                province.setProvinceCode(provinceBean.getId());
                                province.setProvinceName(provinceBean.getName());
                                province.save();
                            }
                        });
                break;
            case LEVEL_PROVINCE:
                new Retrofit.Builder()
                        .baseUrl("http://guolin.tech/api/china/")
                        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                        .create(ChooseApi.class)
                        .getCity(mProvinceCode)
                        .flatMap(new Func1<ArrayList<ProvinceBean>, Observable<ProvinceBean>>() {
                            @Override
                            public Observable<ProvinceBean> call(ArrayList<ProvinceBean> provinceBeen) {
                                return Observable.from(provinceBeen);
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<ProvinceBean>() {
                            @Override
                            public void onCompleted() {
                                loadDatas(position, currentL);
                            }

                            @Override
                            public void onError(Throwable e) {
                                view.stopLoading();
                                view.showError();
                            }

                            @Override
                            public void onNext(ProvinceBean provinceBean) {
                                City city = new City();
                                city.setCityId(provinceBean.getId());
                                city.setCityName(provinceBean.getName());
                                city.setProvinceId(mProvinceCode);
                                city.save();
                            }
                        });
                break;
            case LEVEL_CITY:
                new Retrofit.Builder()
                        .baseUrl("http://guolin.tech/api/china/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                        .build()
                        .create(ChooseApi.class)
                        .getCounty(mProvinceCode, mCityId)
                        .flatMap(new Func1<ArrayList<CountyBean>, Observable<CountyBean>>() {
                            @Override
                            public Observable<CountyBean> call(ArrayList<CountyBean> countyBeen) {
                                return Observable.from(countyBeen);
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<CountyBean>() {
                            @Override
                            public void onCompleted() {
                                loadDatas(position, currentL);
                            }

                            @Override
                            public void onError(Throwable e) {
                                view.stopLoading();
                                view.showError();
                            }

                            @Override
                            public void onNext(CountyBean countyBean) {
                                Log.d("==", "haha");
                                County county = new County();
                                county.setCountyName(countyBean.getName());
                                county.setCityId(mCityId);
                                county.setWeatherId(countyBean.getWeather_id());
                                county.save();
                            }
                        });
                break;
        }
    }

    @Override
    public void loadDatas(int position, int currentL) {
        view.showLoading();

        switch (currentL) {
            case 0:
                mList = DataSupport.findAll(Province.class);
                if (!mList.isEmpty()) {
                    dataList.clear();
                    for (Province province : mList) {
                        dataList.add(province.getProvinceName());
                    }
                    currentLevel = LEVEL_PROVINCE;
                    view.showResults(dataList, "");
                    view.stopLoading();
                } else {
                    queryFromServer(position,currentL);
                }
                break;
            case LEVEL_PROVINCE:
                Province province = mList.get(position);
                mProvinceCode = province.getProvinceCode();
                mCities = DataSupport.where("provinceid = ?", String.valueOf(province.getId())).find(City.class);
                if (!mCities.isEmpty()) {
                    dataList.clear();
                    for (City city : mCities) {
                        dataList.add(city.getCityName());
                    }
                    currentLevel = LEVEL_CITY;
                    view.showResults(dataList, province.getProvinceName());
                    view.stopLoading();
                } else {
                    queryFromServer(position, currentL);
                }
                break;
            case LEVEL_CITY:
                City city1 = mCities.get(position);
                mCityId = city1.getCityId();
                List<County> counties = DataSupport.where("cityid = ?", String.valueOf(city1.getCityId())).find(County.class);
                if (!counties.isEmpty()) {
                    dataList.clear();
                    for (County county : counties) {
                        dataList.add(county.getCountyName());
                    }
                    currentLevel = LEVEL_COUNTY;
                    view.showResults(dataList, city1.getCityName());
                    view.stopLoading();
                } else {
                    queryFromServer(position, currentL);
                }
                break;
        }
    }


    @Override
    public void refresh() {

    }

    @Override
    public int currentLevel() {
        return currentLevel;
    }
}
