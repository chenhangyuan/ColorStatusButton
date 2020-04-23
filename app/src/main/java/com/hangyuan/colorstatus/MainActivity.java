package com.hangyuan.colorstatus;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    ColorStatusView cv_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cv_view = findViewById(R.id.cv_view);
        cv_view.startAnim();
    }
}
