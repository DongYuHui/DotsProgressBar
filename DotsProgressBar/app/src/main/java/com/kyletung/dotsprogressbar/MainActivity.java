package com.kyletung.dotsprogressbar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * All rights reserved by Author<br>
 * Author: Dong YuHui<br>
 * Email: <a href="mailto:dyh920827@hotmail.com">dyh920827@hotmail.com</a><br>
 * Blog: <a href="http://www.kyletung.com">www.kyletung.com</a><br>
 * Create Time: 2016/3/30 at 9:52<br>
 * FixMe
 */
public class MainActivity extends AppCompatActivity {

    private DotsProgressBar mDotsProgressBar;

    private Button mBack;
    private Button mForward;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        setListener();
    }

    private void initView() {
        mDotsProgressBar = (DotsProgressBar) findViewById(R.id.dots_progress_bar_one);
        mBack = (Button) findViewById(R.id.back);
        mForward = (Button) findViewById(R.id.forward);
    }

    private void setListener() {
        mForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDotsProgressBar.startForward();
            }
        });
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDotsProgressBar.startBack();
            }
        });
    }

}
