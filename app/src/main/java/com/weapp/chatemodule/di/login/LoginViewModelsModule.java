package com.weapp.chatemodule.di.login;

import androidx.lifecycle.ViewModel;

import com.weapp.chatemodule.di.ViewModelKey;
import com.weapp.chatemodule.ui.login.LoginViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class LoginViewModelsModule {

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel.class)
    public abstract ViewModel bindAuthViewModel(LoginViewModel viewModel);
}
