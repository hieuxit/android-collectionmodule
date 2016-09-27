package io.fruitful.collectionmodule.rx;

import java.util.List;

import rx.Observable;
import rx.Scheduler;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.internal.util.UtilityFunctions;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;

/**
 * Created by hieuxit on 12/25/15.
 */
public class CollectionBinding<Item> {

    private final ItemAdapter<Item> adapter;
    private final ConnectableObservable<List<Item>> items;
    private Subscription sourceSubscription = Subscriptions.empty();

    CollectionBinding(Observable<List<Item>> source, ItemAdapter<Item> adapter) {
        this(source, adapter, AndroidSchedulers.mainThread());
    }

    CollectionBinding(Observable<List<Item>> source, ItemAdapter<Item> adapter, Scheduler scheduler) {
        this.items = source.observeOn(scheduler).unsubscribeOn(Schedulers.newThread()).replay();
        this.adapter = adapter;
    }

    public static <Item> Builder from(Observable<List<Item>> source) {
        return from(source, UtilityFunctions.<List<Item>>identity());
    }

    public static <S, Item, T extends Iterable<Item>> Builder<S, Item> from(Observable<S> source, Func1<S, T> transformer) {
        return new Builder(source, transformer);
    }

    public ItemAdapter<Item> adapter() {
        return this.adapter;
    }

    public Subscription connect() {
        this.sourceSubscription = this.items.connect();
        return this.sourceSubscription;
    }

    public void disconnect() {
        this.sourceSubscription.unsubscribe();
    }

    public Observable<List<Item>> items() {
        return this.items;
    }

    public static class Builder<S, Item> {
        private ItemAdapter<Item> adapter;
        private Pager.PagingFunction<S> pagingFunction;
        private final Observable<S> source;
        private final Func1<S, List<Item>> transformer;

        public Builder(final Observable<S> source, final Func1<S, List<Item>> transformer) {
            this.source = source;
            this.transformer = transformer;
        }

        public CollectionBinding<Item> build() {
            if (adapter == null) {
                throw new IllegalArgumentException("Adapter can't be null");
            }
            if (pagingFunction == null) {
                return new CollectionBinding<>(source.map(transformer), adapter);
            }
            if (!(this.adapter instanceof PagingAwareAdapter)) {
                throw new IllegalArgumentException("Adapter must implement " + PagingAwareAdapter.class + " when used in a paged binding");
            }
            final Pager<S, List<Item>> pager = Pager.create(pagingFunction, transformer);
            return (CollectionBinding<Item>) new PagedCollectionBinding(pager.page(source), (PagingAwareAdapter) adapter, pager);
        }

        public Builder<S, Item> withAdapter(final ItemAdapter<Item> adapter) {
            this.adapter = adapter;
            return this;
        }

        public Builder<S, Item> withPager(final Pager.PagingFunction<S> pagingFunction) {
            this.pagingFunction = pagingFunction;
            return this;
        }
    }
}
