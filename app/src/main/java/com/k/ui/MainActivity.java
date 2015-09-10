package com.k.ui;

import android.app.Activity;
import android.os.Bundle;

import com.k.widgets.AutoGridView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private AutoGridView mAutoGridView;

    private AutoAdapter mAutoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
        initData();
    }

    private void initUI() {
        mAutoGridView = (AutoGridView) findViewById(R.id.id_auto_grid_view);
    }

    private void initData() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 52; i++) {
            list.add(i + "");
        }
        mAutoAdapter = new AutoAdapter(this);
        mAutoAdapter.setDatas(list);
        mAutoGridView.setAdapter(mAutoAdapter);
    }
}
