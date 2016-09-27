package io.fruitful.collectionmodule.view;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.util.SparseArray;
import android.view.View;

import io.fruitful.collectionmodule.BR;
import io.fruitful.collectionmodule.view.selector.Selector;

/**
 * Created by hieuxit on 1/5/16.
 */
public class BindingViewHolder<Item> extends SimpleViewHolder implements View.OnClickListener, Selector.OnSelectorChangeListener {

    private Item item;
    private ViewDataBinding binding;
    private OnItemClickListener<Item> onItemClickListener;
    private Selector selector;

    public BindingViewHolder(View itemView, Selector selector) {
        super(itemView);
        itemView.setOnClickListener(this);
        this.selector = selector;
        initSelector(selector);
        binding = DataBindingUtil.bind(itemView);
    }

    void bind(Item item, CollectionPosition position, SparseArray bindingMap) {
        this.item = item;
        if (binding != null) {
            binding.setVariable(BR.item, item);
            binding.setVariable(BR.itemPosition, position);
            int bindingSize = bindingMap.size();
            for (int i = 0; i < bindingSize; i++) {
                binding.setVariable(bindingMap.keyAt(i), bindingMap.valueAt(i));
            }

            binding.executePendingBindings();
        }
        if (selector != null) {
            setSelected(selector.isSelected(getAdapterPosition()));
        }
    }

    public Item getData() {
        return item;
    }

    void setOnItemClickListener(OnItemClickListener<Item> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void onClick(View itemView) {
        int position = getAdapterPosition();
        if (selector != null) {
            selector.toggle(position);
        }
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(itemView, getData(), position);
        }
        itemView.setSelected(selector.isSelected(position));
    }

    private void setSelected(boolean selected) {
        itemView.setSelected(selected);
    }

    private void initSelector(Selector selector) {
        this.selector = selector;
        selector.addOnSelectorChangeListener(this);
    }

    @Override
    public void onSelectorChanged() {
        setSelected(selector.isSelected(getAdapterPosition()));
    }
}
