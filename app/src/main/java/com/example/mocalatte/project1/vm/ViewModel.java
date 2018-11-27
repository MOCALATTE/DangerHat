package com.example.mocalatte.project1.vm;

import android.view.View;

public abstract class ViewModel<T extends ViewModel> {
    private View mView;

    public ViewModel(View view) { mView = view; }

    public View getView() {
        return mView;
    }

    public abstract void binding();

    public abstract void initialization(T data);
}