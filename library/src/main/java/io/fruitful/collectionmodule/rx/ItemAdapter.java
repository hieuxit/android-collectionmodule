package io.fruitful.collectionmodule.rx;

import java.util.List;

import rx.Observer;

/**
 * Created by hieuxit on 12/25/15.
 */
public interface ItemAdapter<Item> extends Observer<List<Item>> {

    void addItem(Item item);

    void clear();

    Item getItem(int position);

    int getCollectionItemCount();

    List<Item> getItems();

    void setItems(List<Item> items);

    boolean isEmpty();

    void notifyDataSetChanged();

    void prependItem(Item item);

    void removeItem(int position);
}