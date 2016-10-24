package io.fruitful.collectionmodule.viewmodel;

import android.os.Bundle;
import android.view.View;

import java.util.List;

import io.fruitful.collectionmodule.rx.CollectionBinding;
import io.fruitful.collectionmodule.rx.PagedCollectionBinding;
import io.fruitful.collectionmodule.rx.Pager;
import io.fruitful.collectionmodule.rx.PagingAwareAdapter;
import io.fruitful.collectionmodule.view.IEmptyView;
import rx.Observable;

/**
 * Created by hieuxit on 1/4/16.
 */
public abstract class PagingViewModel<ResponseType, Item> extends CollectionViewModel<ResponseType, Item> implements PagingAwareAdapter.OnNextPageListener {

    private static final int DEFAULT_PAGE_SIZE = 20;
    private PagingAwareAdapter pagedAdapter;
    private PagedCollectionBinding pagedCollectionBinding;
    private int pageSize = DEFAULT_PAGE_SIZE;
    /**
     * start of paging index
     */
    private int pageIndex = 0;

    @Override
    public void resetToBinding(CollectionBinding<Item> collectionBinding) {
        init(collectionBinding);
        super.resetToBinding(collectionBinding);
    }

    private void init(CollectionBinding<Item> collectionBinding) {
        if (!(collectionBinding instanceof PagedCollectionBinding)) {
            throw new IllegalArgumentException("CollectionBinding must implement " + PagedCollectionBinding.class + " when used in a paged viewmodel");
        }

        pagedCollectionBinding = (PagedCollectionBinding) collectionBinding;
        pagedAdapter = pagedCollectionBinding.adapter();

        pagedAdapter.setOnErrorRetryListener(onPagingRetryClickListener);
        pagedAdapter.setOnNexPageListener(this);
        pagedAdapter.setPagingState(PagingAwareAdapter.PagingState.IDLE);
    }

    private View.OnClickListener onPagingRetryClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // retry to get collection if error
            resetToBinding(pagedCollectionBinding.fromCurrentPage());
            setPagingState(PagingAwareAdapter.PagingState.LOADING);
            setDisableEmptyView(true);
            connect();
        }
    };

    @Override
    public void onNextPage() {
        // only call next if hasNext :)
        if (pagedCollectionBinding != null && pagedCollectionBinding.pager().hasNext()) {
            pagedCollectionBinding.pager().next();
            pagedAdapter.setPagingState(PagingAwareAdapter.PagingState.LOADING);
        }
    }

    protected abstract Observable<ResponseType> sourceOfPage(int pageIndex);

    @Override
    protected final Observable<ResponseType> source() {
        pageIndex = getStartIndex();
        return sourceOfPage(pageIndex); // only call with page 0
    }

    @Override
    public void refresh() {
        super.refresh();
        pagedCollectionBinding.pager().reset();
        if (pagedAdapter != null && isDisableEmptyView()) {
            // clear all
            pagedAdapter.setPagingState(PagingAwareAdapter.PagingState.LOADING);
        }
    }

    protected CollectionBinding<Item> onBuildCollectionBinding(Bundle args) {
        return CollectionBinding.from(source(), transformer())
                .withAdapter(adapter)
                .withPager(pagingFunction())
                .build();
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    protected abstract boolean noMorePages(ResponseType response);

    protected Pager.PagingFunction<ResponseType> pagingFunction() {
        return new Pager.PagingFunction<ResponseType>() {
            @Override
            public Observable<ResponseType> call(ResponseType responseType) {
                if (noMorePages(responseType)) {
                    return Pager.finish();
                }
                pageIndex++;
                return sourceOfPage(pageIndex);
            }
        };
    }

    protected int getStartIndex() {
        return 0;
    }

    @Override
    public void pullToRefresh() {
        super.pullToRefresh();
        if (pagedAdapter != null) {
            pagedAdapter.setPagingState(PagingAwareAdapter.PagingState.IDLE);
        }
    }

    @Override
    void onClickRetryEmptyView() {
        super.onClickRetryEmptyView();
    }

    @Override
    void onNextEmptyView(List<Item> items) {
        super.onNextEmptyView(items);
    }

    protected void setPagingState(PagingAwareAdapter.PagingState state) {
        if (pagedAdapter == null) return;
        pagedAdapter.setPagingState(state);
    }

    @Override
    void onErrorEmptyView() {
        // always hide the pull to refresh
        setRefreshing(false);
        if (isAdapterEmpty()) {
            // Show empty error and hide paging error
            setEmptyViewMode(IEmptyView.ERROR);
            setPagingState(PagingAwareAdapter.PagingState.IDLE);
        } else {
            setEmptyViewMode(IEmptyView.HIDE);
            setPagingState(PagingAwareAdapter.PagingState.ERROR);
        }
    }
}
