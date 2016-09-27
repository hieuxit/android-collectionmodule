package io.fruitful.collectionmodule.view.selector;

/**
 * Created by hieuxit on 10/23/15.
 */
public class SingleSelector extends Selector {
    private boolean isAllowDeselectItem;

    public SingleSelector() {
        this(false);
    }

    public SingleSelector(boolean isAllowDeselectItem) {
        this.isAllowDeselectItem = isAllowDeselectItem;
    }

    @Override
    public void setSelectedInternal(int position, boolean isSelected) {
        if (isSelected) {
            mSelected.clear();
            mSelected.put(position, isSelected);
        } else {
            if (isAllowDeselectItem) {
                mSelected.put(position, isSelected);
            }
        }
    }
}
