package com.nullwolf.mubutterknifedemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.nullwolf.mubutterknife.MuButterKnife;
import com.nullwolf.mubutterknife.Unbinder;
import com.nullwolf.mubutterknife_annotations.BindView;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.tv_test)
    TextView tv_test;

    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        unbinder = MuButterKnife.bind(this);

        tv_test.setText("测试");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
