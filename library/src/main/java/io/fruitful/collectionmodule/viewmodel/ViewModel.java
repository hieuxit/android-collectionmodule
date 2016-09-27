package io.fruitful.collectionmodule.viewmodel;

import android.databinding.BaseObservable;
import android.os.Bundle;

import rx.Observable;

/**
 * Basic ViewModel to provide out {@link #preDestroy()} of {@link Observable}
 * And using LightCycle by SoundCloud to make {@link CollectionViewModel} work
 * because {@link CollectionViewModel} uses {@link #onViewModelCreate(Bundle)} to create adapter
 */
public abstract class ViewModel extends BaseObservable {
    public abstract void onViewModelCreate(Bundle savedInstanceState);

    public abstract void onViewModelDestroy();

    protected abstract Observable<? extends ViewModel> preDestroy();
}
