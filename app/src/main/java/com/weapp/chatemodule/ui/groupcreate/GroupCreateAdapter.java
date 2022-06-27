package com.weapp.chatemodule.ui.groupcreate;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.weapp.chatemodule.callbacks.ClickCallbacks;
import com.weapp.chatemodule.databinding.AdapterGroupCreateBinding;
import com.weapp.chatemodule.models.GroupCreate;
import com.weapp.chatemodule.util.AuthUtils;
import com.weapp.chatemodule.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class GroupCreateAdapter extends RecyclerView.Adapter {

    private List<GroupCreate> list = new ArrayList<>();

    private AuthUtils authUtils;
    private ClickCallbacks click;
    private GroupCreateActivity activity;

    public void setClick(ClickCallbacks click) {
        this.click = click;
    }

    public GroupCreateAdapter(GroupCreateActivity activity, AuthUtils authUtils) {
        this.authUtils = authUtils;
        this.activity = activity;
    }

    public void updateItems(final List<GroupCreate> users) {
        final List<GroupCreate> oldItems = new ArrayList<>(list);

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
        AdapterGroupCreateBinding binding = AdapterGroupCreateBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new AdapterAdapterGroupCreateViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        AdapterAdapterGroupCreateViewHolder v = (AdapterAdapterGroupCreateViewHolder) holder;
        v.setData(list.get(position), authUtils, click, position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class AdapterAdapterGroupCreateViewHolder extends RecyclerView.ViewHolder {

        private AdapterGroupCreateBinding binding;

        AdapterAdapterGroupCreateViewHolder(@NonNull AdapterGroupCreateBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void setData(GroupCreate data, AuthUtils authUtils, ClickCallbacks click, int position) {
            authUtils.setImage(activity, Constants.PROFILE + data.getImgName(), binding.img);
            binding.txtName.setText(data.getFullName());
            binding.check.setSelected(data.isSelect());

            binding.check.setOnClickListener(v -> {
                if (list.get(position).isSelect()) {
                    list.get(position).setSelect(false);
                } else {
                    list.get(position).setSelect(true);
                }

                click.getCallback(v, list);
            });
        }
    }
}