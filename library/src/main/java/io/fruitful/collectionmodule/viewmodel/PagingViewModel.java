package io.fruitful.collectionmodule.viewmodel;

import android.os.Bundle;
import android.view.View;

import io.fruitful.collectionmodule.rx.CollectionBinding;
import io.fruitful.collectionmodule.rx.PagedCollectionBinding;
import io.fruitful.collectionmodule.rx.Pager;
import io.fruitful.collectionmodule.rx.PagingAwareAdapter;
import rx.Observable;

/**
 * Created by hieuxit on 1/4/16.
 */
public abstract class PagingViewModel<ResponseType, Item> extends CollectionViewModel<ResponseType, Item> implements View.OnClickListener, PagingAwareAdapter.OnNextPageListener {

    private static final int DEFAULT_PAGE_SIZE = 20;
    private PagingAwareAdapter pagedAdapter;
    private PagedCollectionBinding pagedCollectionBinding;
    private int pageSize = DEFAULT_PAGE_SIZE;
    /**
     * start of paging index
     */
    private int pageIndex = 0;
    private PagingAwareAdapter.PagingState pagingState = PagingAwareAdapter.PagingState.IDLE;

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
        ensurePagingState();
        pagedAdapter.setOnErrorRetryListener(this);
        pagedAdapter.setOnNexPageListener(this);
    }

    void setPagingState(PagingAwareAdapter.PagingState pagingState) {
        this.pagingState = pagingState;
        if (pagedAdapter != null) {
            pagedAdapter.setPagingState(pagingState);
        }
    }

    void ensurePagingState() {
        if (pagedAdapter != null) {
            pagedAdapter.setPagingState(pagingState);
        }
    }

    @Override
    public void onClick(View v) {
        // retry to get collection if error
        resetToBinding(pagedCollectionBinding.fromCurrentPage());
        setPagingState(PagingAwareAdapter.PagingState.LOADING);
    }

    @Override
    public void onNextPage() {
        pagedCollectionBinding.pager().next();
    }

    protected abstract Observable<ResponseType> sourceOfPage(int pageIndex);

    @Override
    protected Observable<ResponseType> source() {
        setPagingState(PagingAwareAdapter.PagingState.LOADING);
        pageIndex = getStartIndex();
        return sourceOfPage(pageIndex); // only call with page 0
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

    protected abstract boolean noMorePages(ResponseType response);

    protected Pager.PagingFunction<ResponseType> pagingFunction() {

        return new Pager.PagingFunction<ResponseType>() {
            @Override
            public Observable<ResponseType> call(ResponseType responseType) {
                if (noMorePages(responseType)) {
                    if (pagedAdapter != null) {
                        pagedAdapter.setPagingState(PagingAwareAdapter.PagingState.IDLE);
                    }
                    return Pager.finish();
                }
                if (pagedAdapter != null) {
                    pagedAdapter.setPagingState(PagingAwareAdapter.PagingState.LOADING);
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
    public void reset() {
        super.reset();
        pagedAdapter.setPagingState(PagingAwareAdapter.PagingState.IDLE);
    }
}
