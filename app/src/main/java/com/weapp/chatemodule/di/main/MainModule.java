package com.weapp.chatemodule.di.main;

import com.weapp.chatemodule.ui.main.MainActivity;
import com.weapp.chatemodule.ui.main.friendGroupFragment.FriendGroupAdapter;
import com.weapp.chatemodule.util.AuthUtils;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public class MainModule {

    @MainScope
    @Provides
    @Named("friend")
    static FriendGroupAdapter provideFrinedAdapter(MainActivity activity, AuthUtils authUtils){
        return new FriendGroupAdapter(activity, authUtils);
    }

    @MainScope
    @Provides
    @Named("group")
    static FriendGroupAdapter provideGroupAdapter(MainActivity activity, AuthUtils authUtils){
        return new FriendGroupAdapter(activity, authUtils);
    }

}