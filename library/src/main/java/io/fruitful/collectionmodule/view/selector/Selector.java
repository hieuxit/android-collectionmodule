package io.fruitful.collectionmodule.view.selector;

import android.util.SparseBooleanArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hieuxit on 10/23/15.
 */
public abstract class Selector {
    SparseBooleanArray mSelected = new SparseBooleanArray();
    private List<OnSelectorChangeListener> mChangeListener = new ArrayList<>();

    public void setSelected(int position, boolean isSelected) {
        setSelectedInternal(position, isSelected);
        notifySelectorChange();
    }

    protected abstract void setSelectedInternal(int position, boolean isSelected);

    public void toggle(int position) {
        setSelected(position, !isSelected(position));
    }

    public List<Integer> getSelected() {
        List<Integer> selected = new ArrayList<>();
        int size = mSelected.size();
        for (int i = 0; i < size; i++) {
            if (mSelected.valueAt(i)) {
                selected.add(mSelected.keyAt(i));
            }
        }
        return selected;
    }

    public boolean isSelected(int position) {
        return mSelected.get(position, false);
    }

    public void addOnSelectorChangeListener(OnSelectorChangeListener listener) {
        if (!mChangeListener.contains(listener)) {
            mChangeListener.add(listener);
        }
    }

    public void removeOnSelectorChangeListener(OnSelectorChangeListener listener) {
        mChangeListener.remove(listener);
    }

    private void notifySelectorChange() {
        for (OnSelectorChangeListener listener : mChangeListener) {
            if (listener != null) {
                listener.onSelectorChanged();
            }
        }
    }

    public void clearSelected() {
        mSelected.clear();
    }

    public interface OnSelectorChangeListener {
        void onSelectorChanged();
    }
}
