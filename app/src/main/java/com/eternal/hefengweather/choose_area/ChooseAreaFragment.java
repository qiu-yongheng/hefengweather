package com.eternal.hefengweather.choose_area;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.eternal.hefengweather.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author 邱永恒
 * @time 2017/4/8 16:34
 * @desc ${TODO}
 */

public class ChooseAreaFragment extends Fragment implements ChooseAreaContract.View {
    @BindView(R.id.title_text)
    TextView mTitleText;
    @BindView(R.id.back_button)
    Button mBackButton;
    @BindView(R.id.lv_choose)
    ListView mLvChoose;
    private ChooseAreaContract.Presenter presenter;
    private List<String> dataList = new ArrayList<>();
    private ProgressDialog progressDialog;
    private static final int LEVEL_PROVINCE = 1;
    private static final int LEVEL_CITY = 2;
    private static final int LEVEL_COUNTY = 3;
    public int position;

    public ChooseAreaFragment() {
    }

    public static ChooseAreaFragment getInstance() {
        return new ChooseAreaFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container, false);
        ButterKnife.bind(this, view);
        // fragment创建好时获取数据
        presenter.start();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // 点击item获取数据
        mLvChoose.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (presenter.currentLevel() == LEVEL_PROVINCE) {
                    position = i;
                }
                presenter.loadDatas(i, presenter.currentLevel());
            }
        });

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("==", presenter.currentLevel() + "");
                switch (presenter.currentLevel()) {
                    case LEVEL_CITY:
                        presenter.loadDatas(position, 0);
                        break;
                    case LEVEL_COUNTY:
                        presenter.loadDatas(position, LEVEL_PROVINCE);
                        break;
                }
            }
        });
    }

    @Override
    public void setPresenter(ChooseAreaContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void initViews(View view) {

    }

    @Override
    public void showError() {
        Snackbar.make(mLvChoose, R.string.error, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        presenter.refresh();
                    }
                }).show();
    }

    @Override
    public void showLoading() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    @Override
    public void stopLoading() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    /**
     * 返回数据
     *
     * @param dataList
     * @param provinceName
     */
    @Override
    public void showResults(List<String> dataList, String provinceName) {
        switch (presenter.currentLevel()) {
            case LEVEL_PROVINCE:
                mBackButton.setVisibility(View.GONE);
                mTitleText.setText("中国");
                break;
            case LEVEL_CITY:
                mBackButton.setVisibility(View.VISIBLE);
                mTitleText.setText(provinceName);
                break;
        }
        mLvChoose.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, dataList));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
