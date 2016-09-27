package io.fruitful.collectionmodule.rx;

import android.view.View;

/**
 * Created by hieuxit on 12/30/15.
 */
public interface PagingAwareAdapter<Item> extends ItemAdapter<Item> {
    boolean isIdle();

    void setPagingState(PagingState pagingState);

    void setOnErrorRetryListener(final View.OnClickListener onClickListener);

    void setOnNexPageListener(OnNextPageListener onNexPageListener);

    enum PagingState {
        IDLE,
        LOADING,
        ERROR
    }

    interface OnNextPageListener {
        void onNextPage();
    }
}