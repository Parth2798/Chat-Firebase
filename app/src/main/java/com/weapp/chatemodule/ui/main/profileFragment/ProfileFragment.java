package com.weapp.chatemodule.ui.main.profileFragment;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.weapp.chatemodule.R;
import com.weapp.chatemodule.callbacks.ClickCallbacks;
import com.weapp.chatemodule.callbacks.CommonBtnClick;
import com.weapp.chatemodule.databinding.FragmentProfileBinding;
import com.weapp.chatemodule.dialogs.EditProfileDialog;
import com.weapp.chatemodule.models.User;
import com.weapp.chatemodule.ui.BaseFragment;
import com.weapp.chatemodule.util.Constants;
import com.weapp.chatemodule.util.Logger;

public class ProfileFragment extends BaseFragment<FragmentProfileBinding> implements CommonBtnClick, ClickCallbacks {

    private static final int GALLERY_CODE = 0x567;

    @Override
    public void getUser(User user) {
        setUser(user);
    }

    private User user;

    private void setUser(User user) {
        this.user = user;

        profileViewModel.loadImage(activity, binding.profile, Constants.PROFILE + user.getImgName());

        binding.txtName.setText(user.getFullName());
        binding.txtID.setText(user.getUserId());
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            binding.txtEmail.setVisibility(View.GONE);
            binding.Email.setVisibility(View.GONE);
        }
        binding.txtEmail.setText(user.getEmail());

        if (user.getMobile() == null || user.getMobile().isEmpty()) {
            binding.txtMobile.setVisibility(View.GONE);
            binding.Mobile.setVisibility(View.GONE);
        }
        binding.txtMobile.setText(user.getMobile());
    }

    private ProfileViewModel profileViewModel;

    private EditProfileDialog editProfileDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_profile;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = getViewDataBinding();
        binding.setClick(this);

        profileViewModel = new ViewModelProvider(this, factory).get(ProfileViewModel.class);

        setObserver();
    }

    private void setObserver() {
        profileViewModel.getIsProgress().observe(getViewLifecycleOwner(), integer -> {
            if (integer != null) {
                if (integer == 0) {
                    isShowPg(true);
                } else if (integer == 1) {
                    isShowPg(false);
                } else if (integer == 2) {
                    isShowPg(false);
                    Toast.makeText(activity, "Something Error.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void ClickData(View v) {
        if (v == binding.imgEdit) {
            if (editProfileDialog == null) {
                editProfileDialog = new EditProfileDialog(activity, user, this);
            }

            editProfileDialog.openDialog();

        } else if (v == binding.profile) {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK);
            galleryIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            startActivityForResult(galleryIntent, GALLERY_CODE);
        }
    }

    @Override
    public void getCallback(View v, Object o) {
        if (o instanceof User) {
            editProfileDialog.finishDialog();
            profileViewModel.updateUser((User) o);
        }

        profileViewModel.saveUserInfo();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_CODE && null != data) {
            Uri selectedImage = data.getData();
            String ImagePath = "";
            if (selectedImage != null) {
                String[] projection = {MediaStore.Images.Media.DATA};
                Cursor cursor = activity.getContentResolver().query(selectedImage, projection, null, null, null);
                if (cursor != null) {
                    cursor.moveToFirst();
                    int column_index = cursor.getColumnIndex(projection[0]);
                    ImagePath = cursor.getString(column_index);
                    cursor.close();
                }

                Logger.e("File Path:  ", "Path:  " + ImagePath);

                profileViewModel.updateProfile(selectedImage, Constants.PROFILE + user.getImgName());
            }
        }
    }
}