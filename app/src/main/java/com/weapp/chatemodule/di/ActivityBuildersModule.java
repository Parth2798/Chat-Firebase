package com.weapp.chatemodule.di;

import com.weapp.chatemodule.di.chat.ChatModule;
import com.weapp.chatemodule.di.chat.ChatScope;
import com.weapp.chatemodule.di.chat.ChatViewModelsModule;
import com.weapp.chatemodule.di.groupCreate.GroupCreateModule;
import com.weapp.chatemodule.di.groupCreate.GroupCreateScope;
import com.weapp.chatemodule.di.groupCreate.GroupCreateViewModelsModule;
import com.weapp.chatemodule.di.login.LoginModule;
import com.weapp.chatemodule.di.login.LoginScope;
import com.weapp.chatemodule.di.login.LoginViewModelsModule;
import com.weapp.chatemodule.di.main.MainFragmentBuildersModule;
import com.weapp.chatemodule.di.main.MainModule;
import com.weapp.chatemodule.di.main.MainScope;
import com.weapp.chatemodule.di.main.MainViewModelsModule;
import com.weapp.chatemodule.di.registration.RegistrationModule;
import com.weapp.chatemodule.di.registration.RegistrationScope;
import com.weapp.chatemodule.di.registration.RegistrationViewModelsModule;
import com.weapp.chatemodule.ui.chat.ChatActivity;
import com.weapp.chatemodule.ui.groupcreate.GroupCreateActivity;
import com.weapp.chatemodule.ui.login.LoginActivity;
import com.weapp.chatemodule.ui.main.MainActivity;
import com.weapp.chatemodule.ui.registration.RegistrationActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityBuildersModule {

    @LoginScope
    @ContributesAndroidInjector(
            modules = {LoginViewModelsModule.class, LoginModule.class}
    )
    abstract LoginActivity contributeAuthActivity();

    @RegistrationScope
    @ContributesAndroidInjector(
            modules = {RegistrationViewModelsModule.class, RegistrationModule.class}
    )
    abstract RegistrationActivity contributeRegistrationActivity();

    @MainScope
    @ContributesAndroidInjector(
            modules = {MainFragmentBuildersModule.class, MainViewModelsModule.class, MainModule.class}
    )
    abstract MainActivity contributeMainActivity();

    @ChatScope
    @ContributesAndroidInjector(
            modules = {ChatViewModelsModule.class, ChatModule.class}
    )
    abstract ChatActivity contributeChatActivity();

    @GroupCreateScope
    @ContributesAndroidInjector(
            modules = {GroupCreateViewModelsModule.class, GroupCreateModule.class}
    )
    abstract GroupCreateActivity contributeGroupCreateActivity();
}
