package com.eternal.hefengweather;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.eternal.hefengweather.choose_area.ChooseAreaFragment;
import com.eternal.hefengweather.choose_area.ChooseAreaPresenter;

public class MainActivity extends AppCompatActivity {

    private ChooseAreaFragment chooseFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState != null) {
            chooseFragment = (ChooseAreaFragment) getSupportFragmentManager().getFragment(savedInstanceState, "chooseFragment");
        } else {
            chooseFragment = ChooseAreaFragment.getInstance();
        }

        if (!chooseFragment.isAdded()) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.layout_fragment, chooseFragment, "ChooseFragment")
                    .commit();
        }
        new ChooseAreaPresenter(this, chooseFragment);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        FragmentManager manager = getSupportFragmentManager();
        if (chooseFragment.isAdded()) {
            manager.putFragment(outState, "chooseFragment", chooseFragment);
        }
    }
}
