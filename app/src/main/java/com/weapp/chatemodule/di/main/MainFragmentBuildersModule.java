package com.weapp.chatemodule.di.main;

import com.weapp.chatemodule.ui.main.friendGroupFragment.FriendFragment;
import com.weapp.chatemodule.ui.main.groupFragment.GroupFragment;
import com.weapp.chatemodule.ui.main.profileFragment.ProfileFragment;
import com.weapp.chatemodule.ui.main.storyFragment.StoryViewFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class MainFragmentBuildersModule {

    @ContributesAndroidInjector
    abstract ProfileFragment contributeProfileFragment();

    @ContributesAndroidInjector
    abstract StoryViewFragment contributeStoryViewFragment();

    @ContributesAndroidInjector
    abstract FriendFragment contributeFriendFragment();

    @ContributesAndroidInjector
    abstract GroupFragment contributeGroupFragment();
}
