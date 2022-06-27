package com.weapp.chatemodule.di.groupCreate;

import com.weapp.chatemodule.di.main.MainScope;
import com.weapp.chatemodule.ui.groupcreate.GroupCreateActivity;
import com.weapp.chatemodule.ui.groupcreate.GroupCreateAdapter;
import com.weapp.chatemodule.ui.main.friendGroupFragment.FriendGroupAdapter;
import com.weapp.chatemodule.util.AuthUtils;

import dagger.Module;
import dagger.Provides;

@Module
public class GroupCreateModule {

    @GroupCreateScope
    @Provides
    static GroupCreateAdapter provideAdapter(GroupCreateActivity activity, AuthUtils authUtils){
        return new GroupCreateAdapter(activity, authUtils);
    }

}
