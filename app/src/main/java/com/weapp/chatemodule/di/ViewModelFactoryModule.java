package com.weapp.chatemodule.di;

import androidx.lifecycle.ViewModelProvider;

import com.weapp.chatemodule.viewmodels.ViewModelProviderFactory;

import dagger.Binds;
import dagger.Module;


@Module
public abstract class ViewModelFactoryModule {

    @Binds
    public abstract ViewModelProvider.Factory bindViewModelFactory(ViewModelProviderFactory viewModelFactory);

}
