package com.task.ui.base;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;

public abstract class BaseActivity<VB extends ViewBinding> extends AppCompatActivity {

    private static final String TAG = BaseActivity.class.getSimpleName();

    protected VB viewBinding;

    protected abstract VB getViewBinding();
    protected abstract void initializeObject();
    protected abstract void initializeToolBar();
    protected abstract void initializeCallbackListener();
    protected abstract void addTextChangedListener();
    protected abstract void setOnClickListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewBinding = getViewBinding();
        setContentView(viewBinding.getRoot());

        initializeObject();
        initializeToolBar();
        initializeCallbackListener();
        addTextChangedListener();
        setOnClickListener();
    }
}
