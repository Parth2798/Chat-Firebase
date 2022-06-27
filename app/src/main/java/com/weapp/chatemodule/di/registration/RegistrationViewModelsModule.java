package com.weapp.chatemodule.di.registration;

import androidx.lifecycle.ViewModel;

import com.weapp.chatemodule.di.ViewModelKey;
import com.weapp.chatemodule.ui.registration.RegistrationViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class RegistrationViewModelsModule {

    @Binds
    @IntoMap
    @ViewModelKey(RegistrationViewModel.class)
    public abstract ViewModel bindAuthViewModel(RegistrationViewModel viewModel);
}
