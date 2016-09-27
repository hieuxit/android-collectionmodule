package io.fruitful.collectionmodule.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by hieuxit on 1/8/16.
 */
public class SimpleViewHolder extends RecyclerView.ViewHolder {
    public SimpleViewHolder(View itemView) {
        super(itemView);
    }

    public <V extends View> V getItemView(){
        V itemView = (V) this.itemView;
        if(itemView == null) throw new IllegalArgumentException("Cannot cast itemView to your wanted View");
        return itemView;
    }
}
