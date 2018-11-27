package com.example.mocalatte.project1.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.example.mocalatte.project1.R;
import com.example.mocalatte.project1.base.SlidingTabsFragment;

public class FragActivity extends AppCompatActivity {
    private SlidingTabsFragment t_fragment;

    FragmentTransaction transaction;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frag_layout);
        if (savedInstanceState == null) {
            transaction = getSupportFragmentManager().beginTransaction();
            SlidingTabsFragment fragment1 = new SlidingTabsFragment();
            t_fragment = fragment1;
            transaction.replace(R.id.step_content_fragment, fragment1);
            transaction.commit();
        }
    }
}
