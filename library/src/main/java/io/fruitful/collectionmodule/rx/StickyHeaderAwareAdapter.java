package io.fruitful.collectionmodule.rx;

import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

/**
 * Created by hieuxit on 1/7/16.
 */
public interface StickyHeaderAwareAdapter<Item> extends ItemAdapter<Item>, StickyRecyclerHeadersAdapter {
    StickyRecyclerHeadersDecoration getHeaderItemDecoration();
}
