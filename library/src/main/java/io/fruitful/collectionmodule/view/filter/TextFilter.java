package io.fruitful.collectionmodule.view.filter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hieuxit on 2/2/16.
 */
public class TextFilter<T extends ItemText> implements Filter<T> {

    private String filterText;

    public TextFilter(String filterText) {
        this.filterText = filterText.toLowerCase();
    }

    @Override
    public List<T> call(List<T> ts) {
        if (ts.isEmpty()) return new ArrayList<>();
        List<T> result = new ArrayList<>();
        for (T item : ts) {
            if (item.getText().toLowerCase().contains(filterText)) {
                result.add(item);
            }
        }
        return result;
    }
}
