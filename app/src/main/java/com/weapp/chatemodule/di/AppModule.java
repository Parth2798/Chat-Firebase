package com.weapp.chatemodule.di;

import android.app.Application;

import com.weapp.chatemodule.models.User;
import com.weapp.chatemodule.util.AuthUtils;
import com.weapp.chatemodule.util.PreferencesClass;
import com.weapp.chatemodule.util.SessionManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    @Singleton
    @Provides
    static AuthUtils provideRetrofitInstance(SessionManager sessionManager){
        return new AuthUtils(sessionManager);
    }


    @Singleton
    @Provides
    static PreferencesClass providePreferencesClass(SessionManager sessionManager, AuthUtils utils, Application application){
        return new PreferencesClass(sessionManager, utils, application);
    }

//    @Singleton
//    @Provides
//    static User provideUser(){
//        return new User();
//    }
}
















