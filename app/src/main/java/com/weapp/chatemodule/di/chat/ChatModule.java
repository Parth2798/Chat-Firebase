package com.weapp.chatemodule.di.chat;

import com.weapp.chatemodule.di.main.MainScope;
import com.weapp.chatemodule.ui.chat.ChatActivity;
import com.weapp.chatemodule.ui.chat.ChatAdapter;
import com.weapp.chatemodule.util.AuthUtils;
import com.weapp.chatemodule.util.PreferencesClass;

import dagger.Module;
import dagger.Provides;

@Module
public class ChatModule {

    @ChatScope
    @Provides
    static ChatAdapter provideAdapter(ChatActivity activity, PreferencesClass preferencesClass, AuthUtils authUtils){
        return new ChatAdapter(activity, preferencesClass, authUtils);
    }
}
