package io.fruitful.collectionmodule.view;

import android.support.annotation.ColorInt;
import android.support.annotation.IntDef;
import android.support.annotation.LayoutRes;
import android.support.annotation.StyleRes;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by hieuxit on 10/13/16.
 */

public interface IEmptyView {
    int NORMAL = 0;
    int HIDE = 1;
    int PROGRESS = 2;
    int ERROR = 3;

    @IntDef({NORMAL, HIDE, PROGRESS, ERROR})
    @Retention(RetentionPolicy.SOURCE)
    @interface Mode {
    }

    void setEmptyMode(@EmptyView.Mode int mode);

    void setEmptyText(CharSequence emptyText);

    void setEmptyErrorText(CharSequence errorText);

    void setOnRetryClickListener(View.OnClickListener listener);

    void setEmptyTextAppearance(@StyleRes int resId);

    void setEmptyErrorTextAppearance(@StyleRes int resId);

    void setEmptyButtonTextColor(@ColorInt int color);

    void setEmptyButtonText(CharSequence retryText);

    void setEmptyButtonColor(@ColorInt int color);

    void setCustomEmptyView(@LayoutRes int resId);
}
