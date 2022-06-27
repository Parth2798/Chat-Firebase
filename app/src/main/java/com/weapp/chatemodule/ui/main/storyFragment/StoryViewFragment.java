package com.weapp.chatemodule.ui.main.storyFragment;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.weapp.chatemodule.R;
import com.weapp.chatemodule.databinding.FragmentStoryViewBinding;
import com.weapp.chatemodule.ui.BaseFragment;
import com.weapp.chatemodule.ui.main.profileFragment.ProfileViewModel;

public class StoryViewFragment extends BaseFragment<FragmentStoryViewBinding> {

    private ProfileViewModel profileViewModel;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_story_view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = getViewDataBinding();

        profileViewModel = new ViewModelProvider(this, factory).get(ProfileViewModel.class);
    }
}
