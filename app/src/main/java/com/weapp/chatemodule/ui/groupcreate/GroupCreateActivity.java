package com.weapp.chatemodule.ui.groupcreate;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.weapp.chatemodule.R;
import com.weapp.chatemodule.callbacks.ClickCallbacks;
import com.weapp.chatemodule.callbacks.CommonBtnClick;
import com.weapp.chatemodule.databinding.ActivityGroupCreateBinding;
import com.weapp.chatemodule.models.GroupCreate;
import com.weapp.chatemodule.models.User;
import com.weapp.chatemodule.ui.BaseActivity;
import com.weapp.chatemodule.util.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

public class GroupCreateActivity extends BaseActivity<ActivityGroupCreateBinding> implements CommonBtnClick, ClickCallbacks {

    private static final int GALLERY_CODE = 0x126;

    private Bitmap bitmap;
    private Uri imgPath;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_group_create;
    }

    @Override
    public void getUser(User user) {
        this.user = user;
    }

    @Inject
    GroupCreateAdapter adapter;

    GroupCreateViewModel viewModel;

    private LinearLayoutManager manager;

    private List<GroupCreate> list = new ArrayList<>();

    private User user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding.setOnCLick(this);

        viewModel = new ViewModelProvider(this, factory).get(GroupCreateViewModel.class);

        manager = new LinearLayoutManager(activity);
        binding.data.setLayoutManager(manager);
        binding.data.setAdapter(adapter);
        adapter.setClick(this);

        setObserver();
    }

    private void setObserver() {
        viewModel.getGroupData().observe(this, groupCreates -> {

            List<GroupCreate> list = filterData(groupCreates);

            Parcelable recyclerViewState = manager.onSaveInstanceState();
            adapter.updateItems(list);
            manager.onRestoreInstanceState(recyclerViewState);
        });

        viewModel.getIsProgress().observe(this, aBoolean -> {
            if (aBoolean == 0) {
                isShowPg(true);
            } else if (aBoolean == 1) {
                isShowPg(false);
                finish();
            } else if (aBoolean == 2) {
                isShowPg(false);
                Toast.makeText(activity, "Something Error.", Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getAllUsers();
    }

    private List<GroupCreate> filterData(List<GroupCreate> friends) {
        List<GroupCreate> data = new ArrayList<>();

        for (int i = 0; i < friends.size(); i++) {
            if (!preferencesClass.getUserID().equals(friends.get(i).getUserId())) {
                data.add(friends.get(i));
            }
        }

        return data;
    }

    @Override
    public void ClickData(View v) {
        if (v == binding.profile) {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK);
            galleryIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            startActivityForResult(galleryIntent, GALLERY_CODE);

        } else if (v == binding.create) {
            String s = Objects.requireNonNull(binding.etGroupName.getText()).toString();

            if (imgPath == null) {
                Toast.makeText(activity, "Select Image", Toast.LENGTH_SHORT).show();
            } else if (s.isEmpty()) {
                Toast.makeText(activity, "Please enter group name", Toast.LENGTH_SHORT).show();
            }

            GroupCreate mUser = new GroupCreate("", Objects.requireNonNull(binding.etGroupName.getText()).toString(), ""
                    , "", "", "", (imgPath != null ? Objects.requireNonNull(binding.etGroupName.getText()).toString() + new Date().getTime() : "")
                    , imgPath, bitmap);


            List<GroupCreate> list1 = filterGroupUser(list);
            if (user != null) {
                list1.add(new GroupCreate(user.getUserId(), user.getfName(), user.getlName(), user.getMobile(), user.getEmail(), user.getPassword(), user.getImgName()));
            }

            viewModel.createGroup(mUser, list1);
        }
    }


    private List<GroupCreate> filterGroupUser(List<GroupCreate> friends) {
        List<GroupCreate> data = new ArrayList<>();

        for (int i = 0; i < friends.size(); i++) {
            if (friends.get(i).isSelect()) {
                data.add(friends.get(i));
            }
        }

        return data;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
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

                imgPath = selectedImage;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                    binding.profile.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    @Override
    public void getCallback(View v, Object o) {
        if (o != null) {
            list = (List<GroupCreate>) o;
        }
    }
}
