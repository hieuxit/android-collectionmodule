package io.fruitful.collectionmodule.binding;

import android.databinding.BindingAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import io.fruitful.collectionmodule.view.EmptyView;

/**
 * Created by hieuxit on 10/4/16.
 */

public class CollectionBindingAdapter {
    @BindingAdapter({"bind:adapter"})
    public static void setRecyclerAdapter(RecyclerView recyclerView, RecyclerView.Adapter adapter) {
        recyclerView.setAdapter(adapter);
    }

    @BindingAdapter({"bind:sticky_header"})
    public static void setStickyHeader(RecyclerView recyclerView, StickyRecyclerHeadersDecoration headerDecoration) {
        recyclerView.removeItemDecoration(headerDecoration);
        recyclerView.addItemDecoration(headerDecoration);
    }

    @BindingAdapter({"bind:itemDecoration"})
    public static void addItemDecoration(RecyclerView recyclerView, RecyclerView.ItemDecoration itemDecoration) {
        recyclerView.removeItemDecoration(itemDecoration);
        recyclerView.addItemDecoration(itemDecoration);
    }

    @BindingAdapter("bind:emptyViewMode")
    public static void setEmptyViewMode(EmptyView emptyView, @EmptyView.Mode int mode) {
        emptyView.setMode(mode);
    }

    @BindingAdapter("bind:emptyViewRetry")
    public static void setEmptyRetry(EmptyView emptyView, View.OnClickListener listener) {
        emptyView.setOnRetryClickListener(listener);
    }
}
