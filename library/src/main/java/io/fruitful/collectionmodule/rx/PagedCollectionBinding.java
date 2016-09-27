package io.fruitful.collectionmodule.rx;

import java.util.List;

import rx.Observable;
import rx.Scheduler;

public class PagedCollectionBinding<Item> extends CollectionBinding<Item> {
    private final PagingAwareAdapter<Item> adapter;
    private final Pager<?, List<Item>> pager;

    PagedCollectionBinding(final Observable<List<Item>> source, final PagingAwareAdapter<Item> adapter, final Pager<?, List<Item>> pager) {
        super(source, adapter);
        this.adapter = adapter;
        this.pager = pager;
    }

    PagedCollectionBinding(final Observable<List<Item>> source, final PagingAwareAdapter<Item> adapter, final Pager<?, List<Item>> pager, final Scheduler scheduler) {
        super(source, adapter, scheduler);
        this.adapter = adapter;
        this.pager = pager;
    }

    @Override
    public PagingAwareAdapter<Item> adapter() {
        return this.adapter;
    }

    public PagedCollectionBinding<Item> fromCurrentPage() {
        return new PagedCollectionBinding<>(pager.currentPage(), adapter, pager);
    }

    public Pager<?, ? extends Iterable<Item>> pager() {
        return this.pager;
    }

}