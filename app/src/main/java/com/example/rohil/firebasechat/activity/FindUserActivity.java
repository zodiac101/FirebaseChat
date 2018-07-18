package com.example.rohil.firebasechat.activity;

import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LifecycleRegistryOwner;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import com.example.rohil.firebasechat.R;
import com.example.rohil.firebasechat.adapter.FindUserAdapter;
import com.example.rohil.firebasechat.viewModel.FindUserViewModel;

public class FindUserActivity extends AppCompatActivity implements LifecycleRegistryOwner {

    RecyclerView recyclerView;
    FindUserAdapter findUserAdapter;

    ProgressBar progressBar;

    LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);

    private FindUserViewModel findUserViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_user);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        findUserViewModel = ViewModelProviders.of(this).get(FindUserViewModel.class);

        progressBar = (ProgressBar)findViewById(R.id.progressBar);

        recyclerView = (RecyclerView)findViewById(R.id.recyclerViewUsers);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(gridLayoutManager);

        findUserAdapter = new FindUserAdapter(this, progressBar);
        recyclerView.setAdapter(findUserAdapter);
        findUserViewModel.setFindUserAdapter(findUserAdapter);
    }

    @Override
    public LifecycleRegistry getLifecycle() {
        return lifecycleRegistry;
    }
}
