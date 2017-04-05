package com.cuihai.commonadapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView通用的adapter
 * （1）简单的无加载更多的信息直接显示
 * （2）添加数据 活动数据  删除数据更新<br>
 * 新增“下一页”功能，使用方法：<br>
 * 1.将{@link #enablePagination}置为true，详见构造方法<br>
 * 2.然后根据实际情况调用{@link #showNextPageBtn()} 和 {@link #hideNextPageBtn()}来将“下一页”按钮显示或者隐藏
 */
public abstract class GCommonRVAdapter<T> extends RecyclerView.Adapter<GViewHolder> {

    protected Context mContext;
    protected int mLayoutId;
    protected List<T> mDatas;
    protected LayoutInflater mInflater;
    private OnGItemClickListener onGItemClickListener;
    private boolean enablePagination;
    /**
     * item原始的高度
     */
    private int mItemOrgHeight = 0;
    private NextPageViewHolder mCurLoadMoreViewHolder;
    private boolean isLoadingMore;
    /**
     * 是否显示下一页按钮
     */
    private boolean needShowNextPageBtn;

    public GCommonRVAdapter(Context context, int layoutId, List<T> datas) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mLayoutId = layoutId;
        mDatas = datas;
    }

    /**
     * @param context
     * @param layoutId
     * @param datas
     * @param enablePagination 是否支持分页
     */
    public GCommonRVAdapter(Context context, int layoutId, List<T> datas, boolean enablePagination) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mLayoutId = layoutId;
        mDatas = datas;
        this.enablePagination = enablePagination;
    }

    @Override
    public GViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        GViewHolder gViewHolder = GViewHolder.get(mContext, null, parent, mLayoutId, -1);
        setListener(parent, gViewHolder, viewType);
        return gViewHolder;
    }

    @Override
    public void onBindViewHolder(GViewHolder holder, int position) {
        holder.updatePosition(position);
        //处理分页模式下最后一个item（下一页按钮）
        if (enablePagination) {
            if (holder.getConvertView() instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) holder.getConvertView();
                boolean hasView = holder.hasView(R.id.next_page);
                if (!hasView) {
                    ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    final NextPageViewHolder npVH = getNextPageView(viewGroup.getContext());
                    npVH.mNextPageContainer.setBackgroundColor(Color.WHITE);
                    npVH.mNextPageContainer.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            npVH.mNextPageContainer.setEnabled(false);
                            npVH.mTextView.setText("正在加载下一页");
                            npVH.mProgress.setVisibility(View.VISIBLE);
                            mCurLoadMoreViewHolder = npVH;
                            onLoadMore();
                        }
                    });
                    viewGroup.addView(npVH.mNextPageContainer, lp);
                }
            } else {
                throw new RuntimeException("in pagination mode，the content view must be a ViewGroup");
            }

            View nextPage = holder.getView(R.id.next_page);
            if (nextPage != null) {
                ViewGroup viewGroup = (ViewGroup) holder.getConvertView();
                ViewGroup.LayoutParams lp = viewGroup.getLayoutParams();
                if ((needShowNextPageBtn && position == getItemCount() - 1)) {
                    setViewVisibility(viewGroup, View.GONE);
                    nextPage.setVisibility(View.VISIBLE);
                    if (mItemOrgHeight == 0) {
                        mItemOrgHeight = lp.height;
                    }
                    lp.height = 100;
                } else {
                    setViewVisibility(viewGroup, View.VISIBLE);
                    nextPage.setVisibility(View.GONE);
                    if (mItemOrgHeight != 0) {
                        lp.height = mItemOrgHeight;
                    }
                }
            }
        }

        if (!enablePagination || position != getItemCount() - 1 || !needShowNextPageBtn) {
            convert(holder, mDatas.get(position), position);
        }
    }

    private void setViewVisibility(ViewGroup viewGroup, int visibility) {
        int childCount = viewGroup.getChildCount();
        if (childCount > 0) {
            for (int i = 0; i < childCount; i++) {
                viewGroup.getChildAt(i).setVisibility(visibility);
            }
        }
    }

    private NextPageViewHolder getNextPageView(Context context) {
        final ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.next_page, null);
        viewGroup.setId(R.id.next_page);
        final TextView nextPage = (TextView) viewGroup.findViewById(R.id.next_page_text);
        nextPage.setGravity(Gravity.CENTER);
        nextPage.setText("点击加载下一页");
        NextPageViewHolder nextPageViewHolder = new NextPageViewHolder();
        nextPageViewHolder.mNextPageContainer = viewGroup;
        nextPageViewHolder.mProgress = (ContentLoadingProgressBar) viewGroup.findViewById(R.id.progress);
        nextPageViewHolder.mTextView = nextPage;
        return nextPageViewHolder;
    }

    public void showNextPageBtn() {
        //如果已经显示了，则直接返回
        if (needShowNextPageBtn) {
            return;
        }
        needShowNextPageBtn = true;
        notifyDataSetChanged();
    }

    public void hideNextPageBtn() {
        //如果已经隐藏了，则直接返回
        if (!needShowNextPageBtn) {
            return;
        }
        needShowNextPageBtn = false;
        notifyDataSetChanged();
    }

    public abstract void convert(GViewHolder gViewHolder, T t, int position);

    public void onLoadMore() {
        isLoadingMore = true;
        finishLoadMore();
    }

    public void setIsLoadingMore(boolean isLoadingMore) {
        this.isLoadingMore = isLoadingMore;
    }

    public boolean isLoadingMore() {
        return isLoadingMore;
    }

    public void finishLoadMore() {
        isLoadingMore = false;
        if (mCurLoadMoreViewHolder != null) {
            mCurLoadMoreViewHolder.mTextView.setText("点击加载下一页");
            mCurLoadMoreViewHolder.mProgress.setVisibility(View.GONE);
            mCurLoadMoreViewHolder.mNextPageContainer.setEnabled(true);
        }
    }

    @Override
    public int getItemCount() {
        if (mDatas == null) {
            mDatas = new ArrayList<>();
            return 0;
        }
        return mDatas.size() + (enablePagination && needShowNextPageBtn ? 1 : 0);
    }

    /**
     * 添加数据
     */
    public void addAll(List<T> datas) {
        mDatas.addAll(datas);
        notifyDataSetChanged();
    }

    /**
     * 获得数据
     */
    public List<T> getAll() {
        return mDatas;
    }

    /**
     * 增加一行数据
     */
    public void addItem(int position, T data) {
        mDatas.add(data);
        notifyItemInserted(position);
    }

    /**
     * 删除一行数据
     */
    public void removeItem(int position) {
        mDatas.remove(position);
        notifyItemRemoved(position);
    }

    public void removeItem(T item) {
        if (mDatas.contains(item)) {
            int position = mDatas.indexOf(item);
            removeItem(position);
        }
    }

    /**
     * 清空数据
     */
    public void clear() {
        mDatas.clear();
        notifyDataSetChanged();
    }

    /**
     * 获得position
     */
    protected int getPosition(RecyclerView.ViewHolder viewHolder) {
        return viewHolder.getAdapterPosition();
    }

    protected boolean isEnabled(int viewType) {
        return true;
    }

    /**
     * 设置item点击
     */
    protected void setListener(final ViewGroup parent, final GViewHolder viewHolder, int viewType) {
        if (!isEnabled(viewType)) return;
        viewHolder.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onGItemClickListener != null) {
                    int position = getPosition(viewHolder);
                    if (position == -1) {
                        return;
                    }
                    if (!enablePagination || position != getItemCount() - 1 || !needShowNextPageBtn) {
                        onGItemClickListener.onItemClick(parent, v, mDatas.get(position), position);
                    }
                }
            }
        });
    }

    /**
     * 设置RV的item点击监听
     *
     * @param onGItemClickListener
     */
    public void setOnGItemClickListener(OnGItemClickListener onGItemClickListener) {
        this.onGItemClickListener = onGItemClickListener;
    }

    public interface OnGItemClickListener<T> {
        void onItemClick(ViewGroup parent, View view, T data, int position);
    }

    public static class NextPageViewHolder {
        public ViewGroup mNextPageContainer;
        public ContentLoadingProgressBar mProgress;
        public TextView mTextView;
    }
}
