package com.weapp.chatemodule.di.groupCreate;

import androidx.lifecycle.ViewModel;

import com.weapp.chatemodule.di.ViewModelKey;
import com.weapp.chatemodule.ui.groupcreate.GroupCreateViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class GroupCreateViewModelsModule {

    @Binds
    @IntoMap
    @ViewModelKey(GroupCreateViewModel.class)
    public abstract ViewModel bindAuthViewModel(GroupCreateViewModel viewModel);
}
