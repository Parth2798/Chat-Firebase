package com.weapp.chatemodule.ui.chat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.daasuu.epf.EPlayerView;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.weapp.chatemodule.R;
import com.weapp.chatemodule.callbacks.CommonBtnClick;
import com.weapp.chatemodule.customeClass.PlayerTimer;
import com.weapp.chatemodule.databinding.ActivityChatBinding;
import com.weapp.chatemodule.models.Message;
import com.weapp.chatemodule.ui.BaseActivity;
import com.weapp.chatemodule.util.Constants;
import com.weapp.chatemodule.util.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class ChatActivity extends BaseActivity<ActivityChatBinding> implements CommonBtnClick {

    private static final int REQUEST_CODE_TAKE_PICTURE = 0x872;
    private String[] allPermission = new String[]{READ_EXTERNAL_STORAGE};

    private static final int GALLERY_CODE = 0x567;
    private ChatViewModel chatViewModel;
    private Uri imgPath;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_chat;
    }

    @Inject
    public ChatAdapter chatAdapter;

    private LinearLayoutManager manager;

    private boolean isCall = true;

    private EPlayerView ePlayerView;
    private SimpleExoPlayer player;
    private PlayerTimer playerTimer;

    private String extension = "";

    private List<Message> chats = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding.setClick(this);
        isCall = true;
        chatViewModel = new ViewModelProvider(this, factory).get(ChatViewModel.class);

        if (hasPermission(allPermission).size() > 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(allPermission, REQUEST_CODE_TAKE_PICTURE);
            }
        }

        manager = new LinearLayoutManager(activity);
        binding.data.setLayoutManager(manager);

        if (chatViewModel.getSessionManager().getFriend().getValue() != null) {
            chatAdapter.setLeftUser(chatViewModel.getSessionManager().getFriend().getValue().getUser());

            binding.txtTitle.setText(chatViewModel.getSessionManager().getFriend().getValue().getUser().getFullName());

            Logger.e("KKKKKKKK  ", "!!!!! 22222222  " + chatViewModel.getSessionManager().getFriend().getValue().getUser().getImgName());


            if (chatViewModel.getSessionManager().getFriend().getValue().getUser().getImgName() != null && !chatViewModel.getSessionManager().getFriend().getValue().getUser().getImgName().isEmpty()) {
                chatViewModel.loadImage(activity, binding.img, Constants.PROFILE + chatViewModel.getSessionManager().getFriend().getValue().getUser().getImgName());
            } else {
                binding.img.setBackground(activity.getResources().getDrawable(R.drawable.ic_launcher_background));
            }

            chatViewModel.getOpponentUser(chatViewModel.getSessionManager().getFriend().getValue().getUser().getUserId());
        }

        if (chatViewModel.getSessionManager().getUser().getValue() != null) {
            chatAdapter.setRightUser(chatViewModel.getSessionManager().getUser().getValue().data);
        }
        binding.data.setAdapter(chatAdapter);

        setObserve();

        if (isCall) {
            chatViewModel.getSingleChat(activity);
            isCall = false;
        }

        binding.pin.setVisibility(Constants.isMedia ? View.VISIBLE : View.GONE);
    }

    private List<String> hasPermission(String[] permission) {
        List<String> data = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String s : permission) {
                if (checkSelfPermission(s) != PackageManager.PERMISSION_GRANTED) {
                    data.add(s);
                }
            }
        }
        return data;
    }

    public void setObserve() {
        chatViewModel.getMessages().observe(this, messages -> {
            if (messages != null) {

                chats.clear();
                chats.addAll(messages);

                Parcelable recyclerViewState;
                recyclerViewState = manager.onSaveInstanceState();
                chatAdapter.updateItems(messages);
                manager.onRestoreInstanceState(recyclerViewState);

                manager.scrollToPosition(messages.size() - 1);
            }
        });

        chatViewModel.getMessage().observe(this, message -> {

            boolean isAdd = true;
            for (int i = 0; i < chats.size(); i++) {
                if (chats.get(i).getIdSender().equals(message.getIdSender()) && chats.get(i).getTimestamp() == message.getTimestamp()) {
                    isAdd = false;
                }
            }

            if (isAdd) {
                chats.add(message);
                Parcelable recyclerViewState;
                recyclerViewState = manager.onSaveInstanceState();
                chatAdapter.updateItems(chats);
                manager.onRestoreInstanceState(recyclerViewState);

                manager.scrollToPosition(chats.size() - 1);
            }
        });

        chatViewModel.getIsProgress().observe(this, aBoolean -> {
            if (aBoolean == 0) {
                isShowPg(true);
            } else if (aBoolean == 1) {
                isShowPg(false);
            } else if (aBoolean == 2) {
                isShowPg(false);
                Toast.makeText(activity, "Something Error.", Toast.LENGTH_SHORT).show();
            }
        });

//        chatViewModel.getOpponentUser().observe(this, user -> {
//            if (user != null) {
//                if (user.getStatus() != null) {
//                    binding.txtOnline.setVisibility(user.getStatus().isOnline ? View.VISIBLE : View.GONE);
//                }
//            }
//        });
    }

    @Override
    public void ClickData(View v) {
        if (v == binding.sendButton) {
            String s = Objects.requireNonNull(binding.editText.getText()).toString();

//            if (isCall) {
//                chatViewModel.getSingleChat(activity);
//                isCall = false;
//            }

            if (s.isEmpty() && imgPath == null) {
                return;
            }

            binding.editText.setText("");
            chatViewModel.sendMsg(activity, s, extension, imgPath);

            binding.close.performClick();

        } else if (v == binding.pin) {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK);
            galleryIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*, video/*");
            activity.startActivityForResult(galleryIntent, GALLERY_CODE);

        } else if (v == binding.close) {
            binding.imgBg.setVisibility(View.GONE);
            imgPath = null;
            extension = "";

        } else if (v == binding.videoView) {
            if (player == null) return;

            if (player.isPlaying()) {
                player.setPlayWhenReady(false);
            } else {
                if (player.getCurrentPosition() >= player.getDuration()) {
                    player.seekTo(0);
                }
                player.setPlayWhenReady(true);
            }
        }
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

                Logger.e("File Path:  ", "ImagePath:  " + ImagePath);
                Logger.e("File Path:  ", "URI:  " + new File(ImagePath).getAbsolutePath());
                String extension = "";
                if (selectedImage.getPath() != null) {
                    extension = selectedImage.getPath().substring(selectedImage.getPath().lastIndexOf("."));
                    Logger.e("File Path:  ", "Path Code:  " + extension);
                }

                this.extension = extension;

                if (!extension.isEmpty()) {
                    if (extension.equalsIgnoreCase(Constants.PNG) || extension.equalsIgnoreCase(Constants.JPG) || extension.equalsIgnoreCase(Constants.JPEG)) {
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                            binding.imgSelect.setImageBitmap(bitmap);
                            binding.imgBg.setVisibility(View.VISIBLE);
                            binding.imgSelect.setVisibility(View.VISIBLE);
                            binding.videoView.setVisibility(View.GONE);
                            imgPath = selectedImage;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (extension.equalsIgnoreCase(Constants.MP4) || extension.equalsIgnoreCase(Constants.MKV)) {
                        releasePlayer();
                        String finalImagePath = ImagePath;

                        binding.videoView.post(() -> setUpSimpleExoPlayer(new File(finalImagePath).getAbsolutePath()));
                        imgPath = selectedImage;

                        binding.imgBg.setVisibility(View.VISIBLE);
                        binding.imgSelect.setVisibility(View.GONE);
                        binding.videoView.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }

    private void setUpSimpleExoPlayer(String path) {

        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "yourApplicationName"));
        MediaSource videoSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse("file:///" + path));

        player = ExoPlayerFactory.newSimpleInstance(this);
        player.prepare(videoSource);
        player.setPlayWhenReady(false);

        setUoGlPlayerView();
    }

    private void setUoGlPlayerView() {
        ePlayerView = new EPlayerView(this);
        ePlayerView.setSimpleExoPlayer(player);
        ePlayerView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        binding.videoView.addView(ePlayerView);
        ePlayerView.onResume();

        setUpTimer();
    }


    private void setUpTimer() {
        playerTimer = new PlayerTimer();
        playerTimer.setCallback(timeMillis -> {
//            long position = player.getCurrentPosition();
//            long duration = player.getDuration();
//
//            if (duration <= 0) return;
        });
        playerTimer.start();
    }

    private void releasePlayer() {
        if (playerTimer != null) {
            playerTimer.stop();
            playerTimer = null;
        }
        if (ePlayerView != null) {
            ePlayerView.onPause();
            binding.videoView.removeAllViews();
            ePlayerView = null;
        }

        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CODE_TAKE_PICTURE) {
            if (hasPermission(allPermission).size() > 0) {
                Toast.makeText(activity, "Give Permissions.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}