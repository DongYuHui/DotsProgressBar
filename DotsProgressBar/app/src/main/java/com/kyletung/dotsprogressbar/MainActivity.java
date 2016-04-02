package com.kyletung.dotsprogressbar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * All rights reserved by Author<br>
 * Author: Dong YuHui<br>
 * Email: <a href="mailto:dyh920827@hotmail.com">dyh920827@hotmail.com</a><br>
 * Blog: <a href="http://www.kyletung.com">www.kyletung.com</a><br>
 * Create Time: 2016/3/30 at 9:52<br>
 * 展示页面
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
        // 用按钮控制的 DotsProgressBar
        mDotsProgressBar = (DotsProgressBar) findViewById(R.id.dots_progress_bar_one);
        mBack = (Button) findViewById(R.id.back);
        mForward = (Button) findViewById(R.id.forward);
        // 用 ViewPager 控制的 DotsProgressBar
        DotsProgressBar mDotsPagerBar = (DotsProgressBar) findViewById(R.id.dots_progress_bar_two);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        DotsPagerAdapter dotsPagerAdapter = new DotsPagerAdapter(getSupportFragmentManager());
        if (viewPager != null && mDotsPagerBar != null) {
            viewPager.setAdapter(dotsPagerAdapter);
            mDotsPagerBar.setViewPager(viewPager);
        }
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

    class DotsPagerAdapter extends FragmentPagerAdapter {

        public DotsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return TestFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return 5;
        }

    }

}
