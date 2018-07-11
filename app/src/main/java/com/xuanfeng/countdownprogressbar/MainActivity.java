package com.xuanfeng.countdownprogressbar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.xuanfeng.countdownprogressview.CountDownProgressBar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CountDownProgressBar count_down = findViewById(R.id.count_down);
        count_down.setOnCountDownFinishListener(new CountDownProgressBar.OnCountDownFinishListener() {
            @Override
            public void countDownFinished() {
                Toast.makeText(MainActivity.this, "计时结束", Toast.LENGTH_SHORT).show();
            }
        });
        count_down.startCountDown();
        count_down.removeOnCountDownFinishListener();
    }
}
