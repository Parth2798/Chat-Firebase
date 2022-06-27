package com.weapp.chatemodule.customeClass;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public abstract class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {
    private static final String TAG = "EndlessRecyclerOnScrollListener";
//    public static String TAG = EndlessRecyclerOnScrollListener.class.getSimpleName();

    /**
     * The total number of items in the dataset after the last load
     */
    private int mPreviousTotal = 0;
    /**
     * True if we are still waiting for the last set of data to load.
     */
    private boolean mLoading = true;

    private boolean forward = true;

    private LinearLayoutManager mLinearLayoutManager;

    public int getmPreviousTotal() {
        return mPreviousTotal;
    }

    public void setmPreviousTotal(int mPreviousTotal) {
        this.mPreviousTotal = mPreviousTotal;
    }

    public EndlessRecyclerOnScrollListener(LinearLayoutManager linearLayoutManager, boolean b) {
        this.mLinearLayoutManager = linearLayoutManager;
        forward = b;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        onPosition(mLinearLayoutManager.findLastVisibleItemPosition());
        if (forward) {
            int visibleItemCount = recyclerView.getChildCount();
            int totalItemCount = mLinearLayoutManager.getItemCount();
            int firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();
            int visibleThreshold = 5;

            if (mLoading) {
                if (totalItemCount > mPreviousTotal) {
                    mLoading = false;
                    mPreviousTotal = totalItemCount;
                }
            }

            if (!mLoading && (totalItemCount - firstVisibleItem - visibleItemCount) <= visibleThreshold) {
                onLoadMore();
                mLoading = true;
            }

        } else {
            if (dy < 0) {
                int visibleItemCount = recyclerView.getChildCount();
                int totalItemCount = mLinearLayoutManager.getItemCount();
                int firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();

                if (mLoading) {
                    if (mPreviousTotal < totalItemCount) {
                        mPreviousTotal = totalItemCount;
                        mLoading = false;
                    }
                }
                int visibleThreshold = 5;
                if (!mLoading && (firstVisibleItem) <= (visibleThreshold)) {
                    onLoadMore();
                    mLoading = true;
                }
            }
        }
    }


    public abstract void onLoadMore();

    public abstract void onPosition(int position);
}