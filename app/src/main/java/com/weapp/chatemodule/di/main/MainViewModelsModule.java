package com.weapp.chatemodule.di.main;

import androidx.lifecycle.ViewModel;

import com.weapp.chatemodule.di.ViewModelKey;
import com.weapp.chatemodule.ui.main.MainViewModel;
import com.weapp.chatemodule.ui.main.friendGroupFragment.FriendViewModel;
import com.weapp.chatemodule.ui.main.groupFragment.GroupViewModel;
import com.weapp.chatemodule.ui.main.profileFragment.ProfileViewModel;
import com.weapp.chatemodule.ui.main.storyFragment.StoryViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class MainViewModelsModule {

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel.class)
    abstract ViewModel bindMainViewModel(MainViewModel mainViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(StoryViewModel.class)
    abstract ViewModel bindStoryViewModel(StoryViewModel storyViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ProfileViewModel.class)
    abstract ViewModel bindProfileViewModel(ProfileViewModel profileViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(FriendViewModel.class)
    abstract ViewModel bindFriendViewModel(FriendViewModel friendGroupFragment);

    @Binds
    @IntoMap
    @ViewModelKey(GroupViewModel.class)
    abstract ViewModel bindGroupViewModel(GroupViewModel friendGroupFragment);
}




