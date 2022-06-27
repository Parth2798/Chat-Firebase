package com.weapp.chatemodule.di.chat;

import androidx.lifecycle.ViewModel;

import com.weapp.chatemodule.di.ViewModelKey;
import com.weapp.chatemodule.ui.chat.ChatViewModel;
import com.weapp.chatemodule.ui.login.LoginViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class ChatViewModelsModule {

    @Binds
    @IntoMap
    @ViewModelKey(ChatViewModel.class)
    public abstract ViewModel bindChatViewModel(ChatViewModel viewModel);
}
