package io.fruitful.collectionmodule.view;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import io.fruitful.collectionmodule.rx.CollectionBinding;
import io.fruitful.collectionmodule.rx.PagedCollectionBinding;
import io.fruitful.collectionmodule.rx.Pager;
import io.fruitful.collectionmodule.rx.PagingAwareAdapter;

public final class PagingRecyclerScrollListener extends RecyclerView.OnScrollListener {
    private final PagingAwareAdapter<?> adapter;
    private final RecyclerView.LayoutManager layoutManager;
    private final int numColumns;
    private final CollectionBinding collectionBinding;

    public PagingRecyclerScrollListener(final CollectionBinding collectionBinding, final PagingAwareAdapter<?> adapter, final RecyclerView.LayoutManager layoutManager, final int numColumns) {
        this.collectionBinding = collectionBinding;
        this.adapter = adapter;
        this.layoutManager = layoutManager;
        this.numColumns = numColumns;
    }

    private int findFirstVisibleItemPosition() {
        if (this.layoutManager instanceof StaggeredGridLayoutManager) {
            return ((StaggeredGridLayoutManager) this.layoutManager).findFirstVisibleItemPositions(new int[this.numColumns])[0];
        }
        if (this.layoutManager instanceof LinearLayoutManager) {
            return ((LinearLayoutManager) this.layoutManager).findFirstVisibleItemPosition();
        }
        throw new IllegalArgumentException("Unknown LayoutManager type: " + this.layoutManager.getClass().getSimpleName());
    }

    private void onScroll(int firstVisiblePosition, final int childCount, final int itemCount) {
        if (itemCount > 0 && itemCount - childCount * 2 <= firstVisiblePosition) {
            firstVisiblePosition = 1;
        } else {
            firstVisiblePosition = 0;
        }
        final Pager pager = ((PagedCollectionBinding) this.collectionBinding).pager();
        if (firstVisiblePosition != 0 && this.adapter.isIdle() && pager.hasNext()) {
//            this.adapter.setLoading();
            pager.next();
        }
    }

    @Override
    public final void onScrolled(final RecyclerView recyclerView, final int dx, final int dy) {
        this.onScroll(this.findFirstVisibleItemPosition(), this.layoutManager.getChildCount(), this.layoutManager.getItemCount());
    }
}
