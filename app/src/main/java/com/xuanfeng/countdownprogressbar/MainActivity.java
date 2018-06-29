package com.xuanfeng.countdownprogressbar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.xuanfeng.countdownprogressview.CountDownProgressBar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CountDownProgressBar count_down = findViewById(R.id.count_down);
        count_down.startCountDown();
    }
}
