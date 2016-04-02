package com.kyletung.dotsprogressbar;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * All rights reserved by Author<br>
 * Author: Dong YuHui<br>
 * Email: <a href="mailto:dyh920827@gmail.com">dyh920827@gmail.com</a><br>
 * Blog: <a href="http://www.kyletung.com">www.kyletung.com</a><br>
 * Create Time: 2016/04/02 at 12:56<br>
 * Test Fragment
 */
public class TestFragment extends Fragment {

    public static TestFragment newInstance(int position) {
        Bundle args = new Bundle();
        args.putInt("position", position);
        TestFragment fragment = new TestFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test, container, false);
        Bundle bundle = getArguments();
        int position = bundle.getInt("position", -1);
        TextView textView = (TextView) view.findViewById(R.id.text);
        textView.setText(String.format(getString(R.string.test_content), position));
        return view;
    }

}
