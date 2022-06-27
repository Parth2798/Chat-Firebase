package com.weapp.chatemodule.ui.main.storyFragment;

import androidx.lifecycle.ViewModel;

import com.weapp.chatemodule.util.AuthUtils;
import com.weapp.chatemodule.util.SessionManager;

import javax.inject.Inject;

public class StoryViewModel extends ViewModel {

    private static final String TAG = "AuthViewModel";

    private final SessionManager sessionManager;
    private final AuthUtils authUtils;

    @Inject
    public StoryViewModel(SessionManager sessionManager, AuthUtils authUtils) {
        this.sessionManager = sessionManager;
        this.authUtils = authUtils;
    }
}