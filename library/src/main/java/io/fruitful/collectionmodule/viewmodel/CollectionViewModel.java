package io.fruitful.collectionmodule.viewmodel;

import android.databinding.Bindable;
import android.os.Bundle;
import android.view.View;

import java.util.List;

import io.fruitful.collectionmodule.BR;
import io.fruitful.collectionmodule.rx.CollectionBinding;
import io.fruitful.collectionmodule.view.EmptyView;
import io.fruitful.collectionmodule.view.RecyclerAdapter;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
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

    @Bindable
    public RecyclerAdapter getAdapter() {
        return adapter;
    }

    @Bindable
    @EmptyView.Mode
    public int emptyViewMode = EmptyView.HIDE;

    /**
     * binding with PTRL listener ( custom setter of Android Data Binding )
     */
    @Bindable
    public Action1<Void> onRefresh = new Action1<Void>() {
        @Override
        public void call(Void aVoid) {
            refresh();
        }
    };

    /**
     * Flag make PULL TO REFRESH show|hide the circle progress
     * set flag to false when received data
     */
    @Bindable
    public boolean refreshing;

    /**
     * flag regardless circle progress of PTRL showing when user pull to refresh
     * use must set to true with a paging view model (has its own progress) and
     * pull to refresh layout is implemented
     */
    private boolean disableRefreshing;

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
        if (!disableEmptyView) {
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
        if (disableRefreshing) {
            setRefreshing(false);
        }
        if (!disableEmptyView) {
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

    private void setEmptyViewMode(@EmptyView.Mode int mode) {
        this.emptyViewMode = mode;
        notifyPropertyChanged(BR.emptyViewMode);
    }

    public View.OnClickListener onRetryListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            refresh();
        }
    };

    public void setDisableEmptyView(boolean disableEmptyView) {
        this.disableEmptyView = disableEmptyView;
    }

    public void setDisableRefreshing(boolean disableRefreshing) {
        this.disableRefreshing = disableRefreshing;
    }

    public void setResetWhenRefresh(boolean flag) {
        this.resetWhenRefresh = flag;
    }

    private void setRefreshing(boolean refreshing) {
        this.refreshing = refreshing;
        notifyPropertyChanged(BR.refreshing);
    }

    class EmptyViewSubscriber extends Subscriber<List<Item>> {

        @Override
        public void onCompleted() {
        }

        @Override
        public void onError(Throwable e) {
            e.printStackTrace();
            setEmptyViewMode(EmptyView.ERROR);
        }

        @Override
        public void onNext(List<Item> items) {
            if ((items == null || items.isEmpty()) && isAdapterEmpty()) {
                setEmptyViewMode(EmptyView.NORMAL);
            } else {
                setEmptyViewMode(EmptyView.HIDE);
            }
        }

        private boolean isAdapterEmpty() {
            return adapter.isEmpty();
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
            setEmptyViewMode(EmptyView.ERROR);
        }

        @Override
        public void onNext(Iterable<Item> items) {
            collectionBinding.adapter().clear();
            resetToBinding(refreshBinding);
            refreshBinding = null;
            this.unsubscribe();
        }
    }

    class AfterSourceChangedSubscriber extends Subscriber<List<Item>> {
        @Override
        public void onCompleted() {
            setRefreshing(false);
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
