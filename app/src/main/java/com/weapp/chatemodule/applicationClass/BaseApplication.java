package com.weapp.chatemodule.applicationClass;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.weapp.chatemodule.di.DaggerAppComponent;
import com.weapp.chatemodule.util.PreferencesClass;

import dagger.android.AndroidInjector;
import dagger.android.support.DaggerApplication;

public class BaseApplication extends DaggerApplication implements LifecycleObserver {

    private static final String TAG = "BaseApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }

    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        return DaggerAppComponent.builder().application(this).build();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void onAppOnCreate() {
        PreferencesClass.isOnline(true);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onAppForegrounded() {
        PreferencesClass.isOnline(true);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onAppOnResume() {
        PreferencesClass.isOnline(true);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onAppOnPause() {
        PreferencesClass.isOnline(false);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onAppBackgrounded() {
        PreferencesClass.isOnline(false);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onAppOnDestroy() {
        PreferencesClass.isOnline(false);
    }
}






