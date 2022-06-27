package com.weapp.chatemodule.ui.main.friendGroupFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.weapp.chatemodule.R;
import com.weapp.chatemodule.callbacks.ClickCallbacks;
import com.weapp.chatemodule.databinding.AdapterFriendGroupBinding;
import com.weapp.chatemodule.models.Friend;
import com.weapp.chatemodule.ui.main.MainActivity;
import com.weapp.chatemodule.util.AuthUtils;
import com.weapp.chatemodule.util.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FriendGroupAdapter extends RecyclerView.Adapter {

    private List<Friend> list = new ArrayList<>();

    public static SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy", Locale.US);
    public static SimpleDateFormat displayDateFormat = new SimpleDateFormat("MMM d", Locale.US);
    public static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.US);

    private AuthUtils authUtils;
    private ClickCallbacks click;
    private MainActivity activity;

    public void setClick(ClickCallbacks click) {
        this.click = click;
    }

    public FriendGroupAdapter(MainActivity activity, AuthUtils authUtils) {
        this.authUtils = authUtils;
        this.activity = activity;
    }

    public void updateItems(final List<Friend> users) {
        final List<Friend> oldItems = new ArrayList<>(list);

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

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterFriendGroupBinding binding = AdapterFriendGroupBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new AdapterFriendGroupViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        AdapterFriendGroupViewHolder v = (AdapterFriendGroupViewHolder) holder;
        v.setData(list.get(position), authUtils, click);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class AdapterFriendGroupViewHolder extends RecyclerView.ViewHolder {

        private AdapterFriendGroupBinding binding;

        AdapterFriendGroupViewHolder(@NonNull AdapterFriendGroupBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void setData(Friend data, AuthUtils authUtils, ClickCallbacks click) {
            binding.img.setBackgroundColor(0);
            binding.img.setBackground(null);

            if (data.getUser().getImgName() != null && !data.getUser().getImgName().isEmpty()) {
                authUtils.setImage(activity, Constants.PROFILE + data.getUser().getImgName(), binding.img);
            } else {
                binding.img.setBackground(activity.getResources().getDrawable(R.drawable.ic_launcher_background));
            }
            binding.txtName.setText(data.getUser().getFullName());

            if (data.getUser().getMessage() != null) {
                String time = dateFormat.format(new Date(data.getUser().getMessage().getTimestamp()));
                String today = dateFormat.format(new Date(System.currentTimeMillis()));

                if (today.equals(time)) {
                    binding.txtTime.setText(timeFormat.format(new Date(data.getUser().getMessage().getTimestamp())));
                } else {
                    binding.txtTime.setText(displayDateFormat.format(new Date(data.getUser().getMessage().getTimestamp())));
                }

                if (data.getUser().getStatus() != null) {
                    binding.isOnline.setVisibility(data.getUser().getStatus().isOnline() && Constants.isDisplayOnline ? View.VISIBLE : View.GONE);
                }
                binding.txtMsg.setText(data.getUser().getMessage().getText());
            }

            binding.getRoot().setOnClickListener(v -> {
                if (click != null) {
                    click.getCallback(v, data);
                }
            });
        }
    }
}