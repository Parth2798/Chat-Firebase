package com.weapp.chatemodule.ui.chat;

import android.content.Context;
import android.net.Uri;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.daasuu.epf.EPlayerView;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.firebase.storage.FirebaseStorage;
import com.weapp.chatemodule.customeClass.PlayerTimer;
import com.weapp.chatemodule.databinding.AdapterChatLeftBinding;
import com.weapp.chatemodule.databinding.AdapterChatRightBinding;
import com.weapp.chatemodule.models.Message;
import com.weapp.chatemodule.models.User;
import com.weapp.chatemodule.util.AuthUtils;
import com.weapp.chatemodule.util.Constants;
import com.weapp.chatemodule.util.Logger;
import com.weapp.chatemodule.util.PreferencesClass;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter {

    private static final int LEFT = 0;
    private static final int RIGHT = 1;

    private PreferencesClass preferencesClass;
    private AuthUtils authUtils;
    private ChatActivity activity;

    public ChatAdapter(ChatActivity activity, PreferencesClass preferencesClass, AuthUtils authUtils) {
        this.preferencesClass = preferencesClass;
        this.authUtils = authUtils;
        this.activity = activity;
    }

    private List<Message> list = new ArrayList<>();

    public static SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy", Locale.US);
    public static SimpleDateFormat displayDateFormat = new SimpleDateFormat("MMM d", Locale.US);
    public static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.US);

    private User rightUser;
    private User leftUser;

    public void setLeftUser(User leftUser) {
        this.leftUser = leftUser;
    }

    public void setRightUser(User rightUser) {
        this.rightUser = rightUser;
    }

    public void updateItems(final List<Message> users) {
        final List<Message> oldItems = new ArrayList<>(list);

        if (users != null) {
            list.clear();
            this.list = new ArrayList<>(users);
        }
        DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return oldItems.size();
            }

            @Override
            public int getNewListSize() {
                return list.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                if (users != null) {
                    return oldItems.get(oldItemPosition).equals(users.get(newItemPosition));
                }
                return false;
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                if (users != null) {
                    return oldItems.get(oldItemPosition).equals(users.get(newItemPosition));
                }
                return false;
            }
        }).dispatchUpdatesTo(this);
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position).getIdSender().equals(preferencesClass.getUserID())) {
            return RIGHT;
        } else {
            return LEFT;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == RIGHT) {
            AdapterChatRightBinding binding = AdapterChatRightBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new AdapterAdapterChatRightViewHolder(binding);

        } else {
            AdapterChatLeftBinding binding = AdapterChatLeftBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new AdapterAdapterChatLeftViewHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == RIGHT) {
            AdapterAdapterChatRightViewHolder v = (AdapterAdapterChatRightViewHolder) holder;
            v.setData(list.get(position));
        } else {
            AdapterAdapterChatLeftViewHolder v = (AdapterAdapterChatLeftViewHolder) holder;
            v.setData(list.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class AdapterAdapterChatRightViewHolder extends RecyclerView.ViewHolder {

        EPlayerView ePlayerView;
        SimpleExoPlayer player;
        PlayerTimer playerTimer;

        private AdapterChatRightBinding binding;

        AdapterAdapterChatRightViewHolder(@NonNull AdapterChatRightBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void setData(Message data) {
            binding.txtMsg.setText(Html.fromHtml(data.getText() + " &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;"));

            String time = dateFormat.format(new Date(data.getTimestamp()));
            String today = dateFormat.format(new Date(System.currentTimeMillis()));

            if (today.equals(time)) {
                binding.txtTime.setText(timeFormat.format(new Date(data.getTimestamp())));
            } else {
                binding.txtTime.setText(displayDateFormat.format(new Date(data.getTimestamp())));
            }

            binding.videoView.setOnClickListener(v -> {
                if (player == null) return;

                if (player.isPlaying()) {
                    player.setPlayWhenReady(false);
                } else {
                    if (player.getCurrentPosition() >= player.getDuration()) {
                        player.seekTo(0);
                    }
                    player.setPlayWhenReady(true);
                }
            });

            Logger.e("KKKKK      ", "!!!!!!! RIGHT  + " + data.getIdSenderName() + "          " + data.getUrl());
            if (data.getUrl() != null && !data.getUrl().isEmpty()) {
                if (data.getExt().equalsIgnoreCase(Constants.PNG) || data.getExt().equalsIgnoreCase(Constants.JPG) || data.getExt().equalsIgnoreCase(Constants.JPEG)) {
                    binding.videoView.setVisibility(View.GONE);
                    binding.img.setVisibility(View.VISIBLE);
                    binding.mediaBg.setVisibility(View.VISIBLE);
                    authUtils.setImage(activity, Constants.CHAT + data.getUrl(), binding.img);

                } else if (data.getExt().equalsIgnoreCase(Constants.MP4) || data.getExt().equalsIgnoreCase(Constants.MKV)) {
                    binding.img.setVisibility(View.GONE);
                    binding.videoView.setVisibility(View.VISIBLE);
                    binding.mediaBg.setVisibility(View.VISIBLE);

                    FirebaseStorage
                            .getInstance()
                            .getReference()
                            .child(Constants.CHAT + data.getUrl())
                            .getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                releasePlayer(binding.videoView);
                                setUpSimpleExoPlayer(binding.getRoot().getContext(), binding.videoView, uri);
                            });
                }

            } else {
                binding.img.setVisibility(View.GONE);
                binding.videoView.setVisibility(View.GONE);
                binding.mediaBg.setVisibility(View.GONE);
            }
        }

        private void setUpSimpleExoPlayer(Context context, FrameLayout view, Uri path) {

            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context, Util.getUserAgent(context, "yourApplicationName"));
            MediaSource videoSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(path);

            player = new SimpleExoPlayer.Builder(context).build();
            player.prepare(videoSource);
            player.setPlayWhenReady(false);

            setUoGlPlayerView(context, view);
        }

        private void setUoGlPlayerView(Context context, FrameLayout view) {
            ePlayerView = new EPlayerView(context);
            ePlayerView.setSimpleExoPlayer(player);
            ePlayerView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            view.addView(ePlayerView);
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

        private void releasePlayer(FrameLayout view) {
            if (playerTimer != null) {
                playerTimer.stop();
                playerTimer = null;
            }
            if (ePlayerView != null) {
                ePlayerView.onPause();
                view.removeAllViews();
                ePlayerView = null;
            }

            if (player != null) {
                player.stop();
                player.release();
                player = null;
            }
        }
    }

    public class AdapterAdapterChatLeftViewHolder extends RecyclerView.ViewHolder {

        private AdapterChatLeftBinding binding;

        EPlayerView ePlayerView;
        SimpleExoPlayer player;
        PlayerTimer playerTimer;

        AdapterAdapterChatLeftViewHolder(@NonNull AdapterChatLeftBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void setData(Message data) {
            binding.txtName.setText(data.getIdSenderName());
            binding.txtMsg.setText(Html.fromHtml(data.getText() + " &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;"));

            if (data.getUrl() != null && !data.getUrl().isEmpty()) {
                if (data.getText().length() > 26) {
                    binding.txtTime.setVisibility(View.VISIBLE);
                    binding.txtTime1.setVisibility(View.GONE);
                } else {
                    binding.txtTime.setVisibility(View.GONE);
                    binding.txtTime1.setVisibility(View.VISIBLE);
                }
            }
            String time = dateFormat.format(new Date(data.getTimestamp()));
            String today = dateFormat.format(new Date(System.currentTimeMillis()));

            if (today.equals(time)) {
                binding.txtTime.setText(timeFormat.format(new Date(data.getTimestamp())));
                binding.txtTime1.setText( timeFormat.format(new Date(data.getTimestamp())));
            } else {
                binding.txtTime.setText(displayDateFormat.format(new Date(data.getTimestamp())));
                binding.txtTime1.setText( displayDateFormat.format(new Date(data.getTimestamp())));
            }
            binding.videoView.setOnClickListener(v -> {
                if (player == null) return;

                if (player.isPlaying()) {
                    player.setPlayWhenReady(false);
                } else {
                    if (player.getCurrentPosition() >= player.getDuration()) {
                        player.seekTo(0);
                    }
                    player.setPlayWhenReady(true);
                }
            });

            Logger.e("KKKKK      ", "!!!!!!! LEFT1  + " + data.getIdSenderName() + "          " + data.getUrl());
            if (data.getUrl() != null && !data.getUrl().isEmpty()) {
                if (data.getExt().equalsIgnoreCase(Constants.PNG) || data.getExt().equalsIgnoreCase(Constants.JPG) || data.getExt().equalsIgnoreCase(Constants.JPEG)) {
                    binding.videoView.setVisibility(View.GONE);
                    binding.img.setVisibility(View.VISIBLE);
                    binding.mediaBg.setVisibility(View.VISIBLE);
                    authUtils.setImage(activity, Constants.CHAT + data.getUrl(), binding.img);

                } else if (data.getExt().equalsIgnoreCase(Constants.MP4) || data.getExt().equalsIgnoreCase(Constants.MKV)) {
                    binding.img.setVisibility(View.GONE);
                    binding.videoView.setVisibility(View.VISIBLE);
                    binding.mediaBg.setVisibility(View.VISIBLE);

                    FirebaseStorage
                            .getInstance()
                            .getReference()
                            .child(Constants.CHAT + data.getUrl())
                            .getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                releasePlayer(binding.videoView);
                                setUpSimpleExoPlayer(binding.getRoot().getContext(), binding.videoView, uri);
                            });
                }

            } else {
                binding.img.setVisibility(View.GONE);
                binding.videoView.setVisibility(View.GONE);
                binding.mediaBg.setVisibility(View.GONE);
            }
        }

        private void setUpSimpleExoPlayer(Context context, FrameLayout view, Uri path) {

            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context, Util.getUserAgent(context, "yourApplicationName"));
            MediaSource videoSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(path);

            player = new SimpleExoPlayer.Builder(context).build();
            player.prepare(videoSource);
            player.setPlayWhenReady(false);

            setUoGlPlayerView(context, view);
        }

        private void setUoGlPlayerView(Context context, FrameLayout view) {


            ePlayerView = new EPlayerView(context);
            ePlayerView.setSimpleExoPlayer(player);
            ePlayerView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            view.addView(ePlayerView);
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

        private void releasePlayer(FrameLayout view) {
            if (playerTimer != null) {
                playerTimer.stop();
                playerTimer = null;
            }
            if (ePlayerView != null) {
                ePlayerView.onPause();
                view.removeAllViews();
                ePlayerView = null;
            }

            if (player != null) {
                player.stop();
                player.release();
                player = null;
            }
        }
    }
}