package io.fruitful.collectionmodule.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import io.fruitful.collectionmodule.R;
import io.fruitful.collectionmodule.rx.PagingAwareAdapter;

public abstract class PagingRecyclerAdapter<Item> extends RecyclerAdapter<Item> implements PagingAwareAdapter<Item> {

    private static final int TYPE_LOADING = 2;
    private static final int TYPE_ERROR = 3;
    public static final int TYPE_ITEM = 4;
    public static final int TYPE_MAX = TYPE_ITEM;

    private PagingAwareAdapter.PagingState pagingState = PagingState.IDLE;
    private View.OnClickListener onRetryClickListener;
    private OnNextPageListener onNextPageListener;

    public PagingRecyclerAdapter(boolean hasHeader, boolean hasFooter) {
        super(hasHeader, hasFooter);
    }

    public PagingRecyclerAdapter(boolean hasHeader, boolean hasFooter, boolean allowDeselectItem) {
        super(hasHeader, hasFooter, allowDeselectItem);
    }

    public PagingRecyclerAdapter() {
        super();
    }

    @Override
    public void setPagingState(PagingState state) {
        this.pagingState = state;
        notifyDataSetChanged();
    }

    public PagingState getPagingState() {
        return pagingState;
    }

    @Override
    public boolean isIdle() {
        return pagingState == PagingState.IDLE;
    }

    @Override
    public void setOnErrorRetryListener(View.OnClickListener onClickListener) {
        this.onRetryClickListener = onClickListener;
    }

    @Override
    public void setOnNexPageListener(OnNextPageListener onNextPageListener) {
        this.onNextPageListener = onNextPageListener;
    }

    @Override
    public boolean enableBinding(int viewType) {
        if (viewType == TYPE_LOADING || viewType == TYPE_ERROR) return false;
        return super.enableBinding(viewType);
    }

    @Override
    public final View getBaseItemView(LayoutInflater inflater, ViewGroup parent, int viewType) {
        if (viewType == TYPE_LOADING) {
            return createPagingLoadingView(inflater, parent);
        }
        if (viewType == TYPE_ERROR) {
            return createPagingErrorView(inflater, parent);
        }
        return getPagingItemView(inflater, parent, viewType);
    }

    @Override
    public final int getBaseItemViewType(int position) {
        if (position < getCollectionItemCount()) {
            return getPagingItemViewType(position);
        }
        return pagingState == PagingState.LOADING ? TYPE_LOADING : TYPE_ERROR;
    }

    @Override
    public final int getItemCount() {
        int count = super.getItemCount();
        if (pagingState != PagingState.IDLE) {
            count++;
        }
        return count;
    }

    @Override
    public void onBindViewHolder(SimpleViewHolder holder, int position, List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        if (position == getItemCount() - 1) {
            if (onNextPageListener != null) {
                if (isIdle()) {
                    // trick do not update when recycler view in-layout
                    holder.getItemView().post(new Runnable() {
                        @Override
                        public void run() {
                            onNextPageListener.onNextPage();
                        }
                    });
                }
            }
        }
    }

    @Override
    public void onError(Throwable e) {
        setPagingState(PagingState.ERROR);
        super.onError(e);
    }

    @Override
    public void onCompleted() {
        super.onCompleted();
        setPagingState(PagingState.IDLE);
    }

    protected View createPagingLoadingView(LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(R.layout.layout_paging_progress, parent, false);
    }

    protected View createPagingErrorView(LayoutInflater inflater, ViewGroup parent) {
        PagingErrorView errorView = (PagingErrorView) inflater.inflate(R.layout.paging_error_view, parent, false);
        errorView.setOnRetryClickListener(onRetryClickListener);
        return errorView;
    }

    @Override
    public void onNext(List<Item> items) {
        this.pagingState = PagingState.IDLE;
        super.onNext(items);
    }

    public abstract View getPagingItemView(LayoutInflater inflater, ViewGroup parent, int viewType);

    public int getPagingItemViewType(int position) {
        return TYPE_ITEM;
    }
}