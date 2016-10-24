package io.fruitful.collectionmodule.viewmodel;

import android.databinding.Bindable;
import android.os.Bundle;
import android.view.View;

import java.util.List;

import io.fruitful.collectionmodule.BR;
import io.fruitful.collectionmodule.rx.CollectionBinding;
import io.fruitful.collectionmodule.view.EmptyView;
import io.fruitful.collectionmodule.view.IEmptyView;
import io.fruitful.collectionmodule.view.RecyclerAdapter;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.subjects.BehaviorSubject;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by hieuxit on 12/31/15.
 */
public abstract class CollectionViewModel<ResponseType, Item> extends ViewModel {

    private BehaviorSubject<CollectionViewModel> observableDestroy = BehaviorSubject.create();

    CompositeSubscription viewLifeCycle = new CompositeSubscription();
    Bundle args;

    RecyclerAdapter adapter;

    @IEmptyView.Mode
    private int emptyViewMode = EmptyView.HIDE;

    /**
     * Flag make PULL TO REFRESH show|hide the circle progress
     * set flag to false when received data
     */
    private boolean refreshing;

    /**
     * flag regardless empty view.
     * It has effect when user wants to refresh a list without show progress
     */
    private boolean disableEmptyView;

    /**
     * Flag that mark recycler view will be clear all item whenever refresh call
     * If not set recycler view keeps this old data until new data is received
     */
    private boolean resetWhenRefresh;

    protected CollectionBinding collectionBinding;
    protected CollectionBinding refreshBinding;

    public synchronized void resetToBinding(CollectionBinding<Item> collectionBinding) {
        this.collectionBinding = collectionBinding;
        subscribeBinding();
    }

    private void subscribeBinding() {
        viewLifeCycle.add(collectionBinding.items().subscribe(new BeforeSourceChangedSubscriber()));
        viewLifeCycle.add(collectionBinding.items().subscribe(collectionBinding.adapter()));
        viewLifeCycle.add(collectionBinding.items().subscribe(new EmptyViewSubscriber()));
        viewLifeCycle.add(collectionBinding.items().subscribe(new AfterSourceChangedSubscriber()));
    }

    protected abstract RecyclerAdapter createAdapter();

    protected abstract Observable<ResponseType> source();

    protected abstract Func1<ResponseType, List<Item>> transformer();

    protected CollectionBinding getCollectionBinding() {
        return collectionBinding;
    }

    public void connect() {
        if (disableEmptyView) {
            setEmptyViewMode(EmptyView.HIDE);
        } else {
            setEmptyViewMode(EmptyView.PROGRESS);
        }
        collectionBinding.connect();
    }

    public void refresh() {
        if (this.collectionBinding != null) {
            this.collectionBinding.disconnect();
        }

        if (this.refreshBinding != null) {
            this.refreshBinding.disconnect();
        }
        if (disableEmptyView) {
            setEmptyViewMode(EmptyView.HIDE);
        } else {
            setEmptyViewMode(EmptyView.PROGRESS);
        }
        if (resetWhenRefresh && collectionBinding != null) {
            collectionBinding.adapter().clear();
        }
        this.refreshBinding = onBuildCollectionBinding(this.args);
        this.refreshBinding.items().subscribe(new RefreshSubscriber());
        this.refreshBinding.connect();
    }

    protected CollectionBinding<Item> onBuildCollectionBinding(Bundle args) {
        this.args = args;
        return CollectionBinding.from(source(), transformer())
                .withAdapter(adapter)
                .build();
    }

    /**
     * Do not manually call this method. This method is public for binding purpose
     */
    public void pullToRefresh() {
        // Pull to refresh. Hide all empty view or progress item on PagingRecyclerAdapter
        setDisableEmptyView(true);
        setEmptyViewMode(EmptyView.HIDE);
        refresh();
    }

    @Override
    public void onViewModelCreate(Bundle savedInstanceState) {
        adapter = createAdapter();
        resetToBinding(onBuildCollectionBinding(savedInstanceState));
    }

    @Override
    public void onViewModelDestroy() {
        viewLifeCycle.unsubscribe();
        if (collectionBinding != null) {
            collectionBinding.disconnect();
        }
        observableDestroy.onNext(this);
    }

    public void reset() {
        if (this.refreshBinding != null) {
            this.refreshBinding.disconnect();
        }
        setEmptyViewMode(EmptyView.HIDE);
    }

    @Override
    protected Observable<? extends ViewModel> preDestroy() {
        return observableDestroy;
    }

    protected void onBeforeSourceChanged(List<Item> items) {
        // do whatever you want with the response data
    }

    protected void onAfterSourceChanged(List<Item> items) {
        // do whatever you want with the response data
    }

    protected void setEmptyViewMode(@EmptyView.Mode int mode) {
        this.emptyViewMode = mode;
        notifyPropertyChanged(BR.emptyViewMode);
    }

    private View.OnClickListener onRetryListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onClickRetryEmptyView();
        }
    };

    void setDisableEmptyView(boolean disableEmptyView) {
        this.disableEmptyView = disableEmptyView;
    }

    public void setResetWhenRefresh(boolean flag) {
        this.resetWhenRefresh = flag;
    }

    public boolean isDisableEmptyView() {
        return disableEmptyView;
    }

    protected void setRefreshing(boolean refreshing) {
        this.refreshing = refreshing;
        notifyPropertyChanged(BR.refreshing);
    }

    void onClickRetryEmptyView() {
        // Hide pull to refresh
        // hide EmptyView error
        setRefreshing(false);
        setDisableEmptyView(false);
        refresh();
    }

    void onNextEmptyView(List<Item> items) {
        setRefreshing(false);
        if ((items == null || items.isEmpty()) && isAdapterEmpty()) {
            setEmptyViewMode(EmptyView.NORMAL);
        } else {
            setEmptyViewMode(EmptyView.HIDE);
        }
    }

    void onErrorEmptyView() {
        setEmptyViewMode(EmptyView.ERROR);
        // If pull to refresh, hide it
        setRefreshing(false);
        // If we pull to refresh, disableEmptyView is true. We make it false to later use
        setDisableEmptyView(false);
    }

    protected boolean isAdapterEmpty() {
        return adapter.isEmpty();
    }

    // *********************************   BINDING   ***************************************

    @Bindable
    @IEmptyView.Mode
    public int getEmptyViewMode() {
        return emptyViewMode;
    }

    @Bindable
    public RecyclerAdapter getAdapter() {
        return adapter;
    }

    @Bindable
    public boolean isRefreshing() {
        return refreshing;
    }

    public View.OnClickListener getOnRetryListener() {
        return onRetryListener;
    }

    class EmptyViewSubscriber extends Subscriber<List<Item>> {

        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
            e.printStackTrace();
            onErrorEmptyView();
        }

        @Override
        public void onNext(List<Item> items) {
            onNextEmptyView(items);
        }
    }

    class RefreshSubscriber extends Subscriber<Iterable<Item>> {

        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
            e.printStackTrace();
            if (collectionBinding != null) {
                collectionBinding.adapter().clear();
                collectionBinding.adapter().onError(e);
            }
            refreshBinding = null;
            onErrorEmptyView();
        }

        @Override
        public void onNext(Iterable<Item> items) {
            // Clear all adapter after got items
            collectionBinding.adapter().clear();
            // Then using refreshBinding to bind with the current adapter
            resetToBinding(refreshBinding);
            refreshBinding = null;
            // If we pull to refresh, disableEmptyView is true. We make it false to later use
            setDisableEmptyView(false);
            // hide pull to refresh
            setRefreshing(false);
            // Do not subscribe anymore
            this.unsubscribe();
        }
    }

    class AfterSourceChangedSubscriber extends Subscriber<List<Item>> {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
            e.printStackTrace();
        }

        @Override
        public void onNext(List<Item> items) {
            if (items != null && !items.isEmpty()) {
                onAfterSourceChanged(items);
            }
        }
    }

    class BeforeSourceChangedSubscriber extends Subscriber<List<Item>> {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
            e.printStackTrace();
        }

        @Override
        public void onNext(List<Item> items) {
            if (items != null && !items.isEmpty()) {
                onBeforeSourceChanged(items);
            }
        }
    }
}
