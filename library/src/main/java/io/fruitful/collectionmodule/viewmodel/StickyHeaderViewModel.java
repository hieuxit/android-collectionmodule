package io.fruitful.collectionmodule.viewmodel;

import android.databinding.Bindable;

import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import io.fruitful.collectionmodule.BR;
import io.fruitful.collectionmodule.rx.CollectionBinding;
import io.fruitful.collectionmodule.rx.ItemAdapter;
import io.fruitful.collectionmodule.rx.StickyHeaderAwareAdapter;

/**
 * Created by hieuxit on 1/7/16.
 */
public abstract class StickyHeaderViewModel<ResponseType, Item> extends CollectionViewModel<ResponseType, Item> {

    @Bindable
    public StickyRecyclerHeadersDecoration headerDecoration;

    StickyHeaderAwareAdapter stickyHeaderAdapter;

    @Override
    public void resetToBinding(CollectionBinding<Item> collectionBinding) {
        super.resetToBinding(collectionBinding);
        init(collectionBinding);
    }

    private void init(CollectionBinding<Item> collectionBinding) {
        ItemAdapter adapter = collectionBinding.adapter();
        if(!(adapter instanceof StickyHeaderAwareAdapter)){
            throw new IllegalArgumentException("Adapter must implement " + StickyHeaderAwareAdapter.class + " when used in a sticky header viewmodel");
        }
        stickyHeaderAdapter = (StickyHeaderAwareAdapter) adapter;
        headerDecoration = stickyHeaderAdapter.getHeaderItemDecoration();
        notifyPropertyChanged(BR.headerDecoration);
    }
}
