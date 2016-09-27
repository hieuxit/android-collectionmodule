package io.fruitful.collectionmodule.view;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import io.fruitful.collectionmodule.rx.ItemAdapter;
import io.fruitful.collectionmodule.view.filter.Filter;
import io.fruitful.collectionmodule.view.selector.MultiSelector;
import io.fruitful.collectionmodule.view.selector.Selector;
import io.fruitful.collectionmodule.view.selector.SingleSelector;

/**
 * Created by hieuxit on 12/11/15.
 */
public abstract class RecyclerAdapter<Item> extends RecyclerView.Adapter<SimpleViewHolder> implements ItemAdapter<Item>{

    static final int TYPE_HEADER = 0;
    static final int TYPE_FOOTER = 1;
    static final int TYPE_ITEM = 2;
    public static final int TYPE_MAX = TYPE_ITEM;

    /**
     * items list to display
     */
    private List<Item> items;

    /**
     * when filter is enabling -> this list is backup for original list
     */
    private List<Item> originItems;

    /**
     * List selector of origin Item
     */
    private List<Integer> originSelectedIndex;

    /**
     * Filter function
     */

    private SparseArray<Filter<Item>> filters = new SparseArray<>();

    /**
     * enable Header view
     */
    private boolean hasHeader;

    /**
     * enable Footer view
     */
    private boolean hasFooter;

    /**
     * allow/disallow deselect item in list
     */
    private boolean allowDeselectItem;

    /**
     * Item click listener
     */
    private OnItemClickListener<Item> onItemClickListener;

    /**
     * The enum that defines two types of selector.
     */
    public enum SelectorType{
        SINGLE, MULTI
    }

    private Selector selector;

    private SparseArray<Object> mBindingData = new SparseArray<>();

    public RecyclerAdapter(boolean hasHeader, boolean hasFooter) {
        this(hasHeader, hasFooter, false);
    }

    public RecyclerAdapter(boolean hasHeader, boolean hasFooter, boolean allowDeselectItem) {
        this.items = new ArrayList<>();
        this.hasHeader = hasHeader;
        this.hasFooter = hasFooter;
        this.allowDeselectItem = allowDeselectItem;
        selector = createSelector();
    }

    public RecyclerAdapter() {
        this(false, false);
    }

    public final boolean isHeaderView(int position) {
        return hasHeader && position == 0;
    }

    public final boolean isFooterView(int position) {
        int count = getItemCount();
        return hasFooter && position == count - 1;
    }

    @Override
    public final SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View viewItem;
        if (viewType == TYPE_HEADER) {
            viewItem = getHeaderView(LayoutInflater.from(parent.getContext()), parent);
        } else if (viewType == TYPE_FOOTER) {
            viewItem = getFooterView(LayoutInflater.from(parent.getContext()), parent);
        } else {
            viewItem = getBaseItemView(LayoutInflater.from(parent.getContext()), parent, viewType);
        }

        SimpleViewHolder holder;
        if (enableBinding(viewType)) {
            holder = new BindingViewHolder<>(viewItem, selector);
            ((BindingViewHolder<Item>) holder).setOnItemClickListener(onItemClickListener);
        } else {
            holder = new SimpleViewHolder(viewItem);
        }

        onAfterCreateViewHolder(holder);

        return holder;
    }

    @Override
    public final void onBindViewHolder(SimpleViewHolder holder, int position) {
        if (isHeaderView(position)) return;
        if (isFooterView(position)) return;
        int offsetPosition = hasHeader ? 1 : 0;
        if (holder instanceof BindingViewHolder) {
            int positionInCollection = position - offsetPosition;
            ((BindingViewHolder<Item>) holder).bind(getItem(positionInCollection),
                    new CollectionPosition(getCollectionItemCount(), position), mBindingData);
        }

        onAfterBindViewHolder(holder, position);
    }


    public void onAfterCreateViewHolder(SimpleViewHolder holder) {
        // Do something like add listener to view...
    }

    public void onAfterBindViewHolder(SimpleViewHolder holder, int position) {

    }

    @Override
    public final int getItemViewType(int position) {
        if (isHeaderView(position)) return TYPE_HEADER;
        if (isFooterView(position)) return TYPE_FOOTER;
        return getBaseItemViewType(position - (hasHeader ? 1 : 0));
    }

    /**
     * Get type of list item view bind with {@link Item item}. Always start from {@value TYPE_MAX}
     *
     * @param position: position in {@code List<Item>}
     * @return
     */
    public int getBaseItemViewType(int position) {
        return TYPE_ITEM;
    }

    public boolean enableBinding(int viewType) {
        if (viewType == TYPE_HEADER || viewType == TYPE_FOOTER) return false;
        return true;
    }

    @Override
    public int getCollectionItemCount() {
        return items.size();
    }

    @Override
    public int getItemCount() {
        int count = items.size();
        return count + (hasHeader ? 1 : 0) + (hasFooter ? 1 : 0);
    }

    @Override
    public void addItem(Item item) {
        if (!items.contains(item)) {
            items.add(item);
        }
    }

    @Override
    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    @Override
    public Item getItem(int position) {
        return items.get(position);
    }

    @Override
    public List<Item> getItems() {
        return items;
    }

    @Override
    public void setItems(List<Item> items) {
        recalculateSelector(getSelected(), this.items, items);
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public boolean isEmpty() {
        return items.isEmpty();
    }

    public boolean isFiltering() {
        return filters.size() > 0;
    }

    @Override
    public void prependItem(Item item) {
        items.add(0, item);
    }

    @Override
    public void removeItem(int position) {
        items.remove(position);
    }

    public abstract View getBaseItemView(LayoutInflater inflater, ViewGroup parent, int viewType);

    protected Selector createSelector() {
        return new SingleSelector(allowDeselectItem);
    }

    public Selector getSelector() {
        return selector;
    }

    public View getHeaderView(LayoutInflater inflater, ViewGroup parent) {
        return null;
    }

    public View getFooterView(LayoutInflater inflater, ViewGroup parent) {
        return null;
    }

    public void setOnItemClickListener(OnItemClickListener<Item> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void onCompleted() {
    }

    @Override
    public void onError(Throwable e) {
        notifyDataSetChanged();
        e.printStackTrace();
    }

    @Override
    public void onNext(List<Item> items) {
        if (this.items.isEmpty()) {
            if (items != null) {
                if (filters.size() > 0) {
                    originItems = items;
                    applyFilter();
                } else {
                    setItems(items);
                }
            }
            return;
        }
        if (items == null) return;

        // paging here

        // if has filter
        if (filters.size() > 0) {
            originItems.addAll(items);
            applyFilter();
        } else {
            this.items.addAll(items);
            notifyDataSetChanged();
        }
    }

    public void addBinding(int variableId, Object value) {
        mBindingData.put(variableId, value);
    }

    public void removeBinding(int variableId) {
        mBindingData.remove(variableId);
    }

    public void applyFilter() {
        if (filters != null && filters.size() > 0) {
            // if original Items is null, backup here
            if (originItems == null || originItems.size() == 0) {
                originItems = items;
                originSelectedIndex = getSelected();
            }

            List<Item> filterItems = originItems;
            for (int i = 0; i < filters.size(); i++) {
                int key = filters.keyAt(i);
                Filter<Item> filter = filters.get(key);
                filterItems = filter.call(filterItems);
            }

            setItems(filterItems);
            notifyDataSetChanged();
        } else {
            clearFilter();
        }
    }

    public boolean hasFooter() {
        return hasFooter;
    }

    public boolean hasHeader() {
        return hasHeader;
    }

    public boolean isAllowDeselectItem() {
        return allowDeselectItem;
    }

    public void setAllowDeselectItem(boolean allowDeselectItem) {
        this.allowDeselectItem = allowDeselectItem;
    }

    public void setSelected(int position) {
        setSelected(position, true);
    }

    public void setSelected(int position, boolean selected) {
        if (selector != null) {
            selector.setSelected(position, selected);
        }
    }

    public void setSelectorType(SelectorType type){
        switch (type){
            case SINGLE:
                selector = new SingleSelector();
                break;
            case MULTI:
                selector = new MultiSelector();
                break;
        }
    }

    public void addFilter(Filter<Item> filter) {
        if (filters == null) {
            filters = new SparseArray<>();
        }
        addFilter(filters.size(), filter);
    }

    public void addFilter(int filterIndex, Filter<Item> filter) {
        if (filters == null) {
            filters = new SparseArray<>();
        }
        filters.put(filterIndex, filter);

        applyFilter();
    }

    public void clearFilter() {
        this.filters.clear();
        if (originItems != null) {
            List<Integer> selected = getSelected();
            selector.clearSelected();
            if (selected.isEmpty()) {
                if (!originSelectedIndex.isEmpty()) {
                    for (int selectedIndex : originSelectedIndex) {
                        selector.setSelected(selectedIndex, true);
                    }
                }
            } else {
                recalculateSelector(selected, items, originItems);
            }
            this.items = new ArrayList<>(this.originItems);
            this.originItems = null;
        }
        notifyDataSetChanged();
    }

    public void removeFilter(int filterIndex) {
        if (filters != null) {
            filters.remove(filterIndex);
        }

        applyFilter();
    }

    public List<Integer> getSelected() {
        return selector.getSelected();
    }

    public List<Item> getSelectedObject() {
        List<Item> selectedObject = new ArrayList<>();
        List<Integer> selected = getSelected();
        if (selected == null || selected.isEmpty()) return selectedObject;

        for (int index : selected) {
            selectedObject.add(this.items.get(index));
        }
        return selectedObject;
    }

    private void recalculateSelector(List<Integer> selected, List<Item> oldList, List<Item> newList) {
        if (selected.isEmpty()) return;
        if (oldList.isEmpty()) return;

        // check if all selected is valid then recalculate
        // else keep the selector

        // 1. first check
        boolean valid = true;
        for (int index : selected) {
            if (oldList.size() <= index) {
                valid = false;
                break;
            }
        }
        if (!valid) return;

        // 2. recalculate
        selector.clearSelected();
        for (int index : selected) {
            Item item = oldList.get(index);
            int newIndex = newList.indexOf(item);
            if (newIndex >= 0) {
                selector.setSelected(newIndex, true);
            }
        }
    }
}