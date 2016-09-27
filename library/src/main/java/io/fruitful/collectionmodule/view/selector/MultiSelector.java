package io.fruitful.collectionmodule.view.selector;

/**
 * Created by hieuxit on 10/23/15.
 */
public class MultiSelector extends Selector {

    @Override
    public void setSelectedInternal(int position, boolean isSelected) {
        mSelected.put(position, isSelected);
    }
}
