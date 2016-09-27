package io.fruitful.collectionmodule.view.filter;

import java.util.List;

import rx.functions.Func1;

/**
 * Created by hieuxit on 2/1/16.
 */
public interface Filter<Item> extends Func1<List<Item>, List<Item>> {
}
