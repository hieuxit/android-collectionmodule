package io.fruitful.collectionmodule.view;

import android.view.View;

/**
 * Created by hieuxit on 12/11/15.
 */
public interface OnItemClickListener<T> {
    void onItemClick(View itemView, T data, int position);
}
