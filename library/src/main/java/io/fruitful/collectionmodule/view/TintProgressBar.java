package io.fruitful.collectionmodule.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import io.fruitful.collectionmodule.R;

/**
 * Created by hieuxit on 10/21/16.
 */

public class TintProgressBar extends ProgressBar {

    public TintProgressBar(Context context) {
        super(context);
    }

    public TintProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.defaultProgressViewStyle);
    }

    public TintProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TintProgressBar,
                    defStyleAttr, R.style.ProgressViewStyle);
            setProgressColor(a.getColor(R.styleable.TintProgressBar_progressColor, Color.GRAY));
            a.recycle();
        }
    }

    public void setProgressColor(@ColorInt int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setIndeterminateTintList(ColorStateList.valueOf(color));
            return;
        }
        Drawable progressDrawable = getIndeterminateDrawable();
        progressDrawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
    }


}
