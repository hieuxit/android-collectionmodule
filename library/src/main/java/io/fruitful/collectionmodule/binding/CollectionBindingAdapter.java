package io.fruitful.collectionmodule.binding;

import android.databinding.BindingAdapter;
import android.support.v7.widget.RecyclerView;

import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

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
}
