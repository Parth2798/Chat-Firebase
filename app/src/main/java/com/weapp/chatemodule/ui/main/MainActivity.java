package com.weapp.chatemodule.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.weapp.chatemodule.R;
import com.weapp.chatemodule.callbacks.CommonBtnClick;
import com.weapp.chatemodule.databinding.ActivityMainBinding;
import com.weapp.chatemodule.ui.BaseActivity;
import com.weapp.chatemodule.ui.groupcreate.GroupCreateActivity;
import com.weapp.chatemodule.ui.main.friendGroupFragment.FriendFragment;
import com.weapp.chatemodule.ui.main.groupFragment.GroupFragment;
import com.weapp.chatemodule.ui.main.profileFragment.ProfileFragment;
import com.weapp.chatemodule.ui.main.storyFragment.StoryViewFragment;

import java.util.Objects;

public class MainActivity extends BaseActivity<ActivityMainBinding> implements CommonBtnClick {

    ViewPagerAdapter adapter;

    private MainViewModel mainViewModel;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding.setOnCLick(this);
        mainViewModel = new ViewModelProvider(this, factory).get(MainViewModel.class);

        setSupportActionBar(binding.toolbar);
        setTabs();
    }

    private void setTabs() {
        binding.tabs.setSelectedTabIndicatorColor(getResources().getColor(R.color.astronautBlue));
        setupViewPager(binding.viewpager);
        binding.tabs.setupWithViewPager(binding.viewpager);
        setupTabIcons();
    }

    private void setupTabIcons() {
        String[] tabIcons = {"CHATS", "GROUPS", "STATUS", "PROFILE"};

        for (int i = 0; i < binding.tabs.getTabCount(); i++) {
            Objects.requireNonNull(binding.tabs.getTabAt(i)).setText(tabIcons[i]);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new FriendFragment());
        adapter.addFrag(new GroupFragment());
        adapter.addFrag(new StoryViewFragment());
        adapter.addFrag(new ProfileFragment());


        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(adapter.getCount());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                binding.addGroup.setVisibility(position == 1 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void ClickData(View v) {
        if (v == binding.logOut) {
            mainViewModel.setIsOnline(false);

            mainViewModel.logOut();

        } else if (v == binding.addGroup) {
            startActivity(new Intent(activity, GroupCreateActivity.class));
        }
    }

    @Override
    public void onBackPressed() {
        mainViewModel.setIsOnline(false);
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mainViewModel.setIsOnline(true);
    }
}