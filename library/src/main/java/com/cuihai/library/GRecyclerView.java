package com.cuihai.library;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * 扩展的RecyclerView
 * （1）实现设置EmptyView
 */
public class GRecyclerView<T> extends RecyclerView {

    public View mEmptyView;

    public GRecyclerView(Context context) {
        super(context);
    }

    public GRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void checkIfEmpty() {
        if (mEmptyView != null) {
            if (getAdapter() != null && getAdapter().getItemCount() > 0) {
                mEmptyView.setVisibility(GONE);
                setVisibility(VISIBLE);
            } else {
                mEmptyView.setVisibility(VISIBLE);
                setVisibility(GONE);
            }
        }
    }

    /**
     * 观察adapter的变化
     */
    final AdapterDataObserver observer = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            checkIfEmpty();
        }
    };

    @Override
    public void swapAdapter(Adapter adapter, boolean removeAndRecycleExistingViews) {
        final Adapter oldAdapter = getAdapter();
        if (oldAdapter != null) {
            adapter.unregisterAdapterDataObserver(observer);
        }
        if (adapter != null) {
            adapter.registerAdapterDataObserver(observer);
        }
        super.swapAdapter(adapter, removeAndRecycleExistingViews);
        checkIfEmpty();
    }

    @Override
    public void setAdapter(Adapter adapter) {
        final Adapter oldAdapter = getAdapter();
        if (oldAdapter != null) {
            oldAdapter.unregisterAdapterDataObserver(observer);
        }
        super.setAdapter(adapter);
        if (adapter != null) {
            adapter.registerAdapterDataObserver(observer);
        }
    }

    /**
     * 设置EmptyView
     */
    public void setEmptyView(View emptyView) {
        this.mEmptyView = emptyView;
        checkIfEmpty();
    }

    public View getEmptyView() {
        return this.mEmptyView;
    }

    RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            //停止滚动
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                int itemCount = recyclerView.getAdapter().getItemCount();
                if (itemCount > 0) {
                    int lastPos = 0;
                    RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
                    if (manager instanceof LinearLayoutManager) {
                        lastPos = ((LinearLayoutManager) manager).findLastVisibleItemPosition();
                    }

                    if (lastPos + 1 >= itemCount) {
                        if (getAdapter() instanceof GCommonRVAdapter) {
                            ((GCommonRVAdapter) getAdapter()).onLoadMore();
                        }
                    }
                }

            }

        }
    };

    public void enableScrollToNextPage(boolean enable) {
        if (enable) {
            //如果已经有了，则先remove，防止重复add的情况出现
            removeOnScrollListener(onScrollListener);
            addOnScrollListener(onScrollListener);
        } else {
            clearOnScrollListeners();
        }
    }
}
