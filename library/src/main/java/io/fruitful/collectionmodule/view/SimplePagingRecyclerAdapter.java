package io.fruitful.collectionmodule.view;

import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by hieuxit on 1/8/16.
 */
public class SimplePagingRecyclerAdapter<Item> extends PagingRecyclerAdapter<Item> {

    private @LayoutRes
    int itemLayout;

    public SimplePagingRecyclerAdapter(@LayoutRes int itemLayout){
        this.itemLayout = itemLayout;
    }

    public SimplePagingRecyclerAdapter(@LayoutRes int itemLayout, boolean hasHeader, boolean hasFooter) {
        super(hasHeader, hasFooter);
        this.itemLayout = itemLayout;
    }

    @Override
    public View getPagingItemView(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return inflater.inflate(itemLayout, parent, false);
    }
}