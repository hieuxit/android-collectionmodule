package io.fruitful.collectionmodule.viewmodel;

import android.os.Bundle;

import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by hieuxit on 4/13/16.
 */
public class BaseViewModel extends ViewModel {

    private PublishSubject<BaseViewModel> observableDestroy = PublishSubject.create();

    @Override
    public void onViewModelCreate(Bundle savedInstanceState) {

    }

    @Override
    public void onViewModelDestroy() {
        observableDestroy.onNext(this);
    }

    @Override
    protected Observable<? extends ViewModel> preDestroy() {
        return observableDestroy;
    }

}
