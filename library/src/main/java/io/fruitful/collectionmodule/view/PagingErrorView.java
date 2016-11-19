package io.fruitful.collectionmodule.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.StyleRes;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.TextViewCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import io.fruitful.collectionmodule.R;

/**
 * Created by hieuxit on 10/21/16.
 */

public class PagingErrorView extends FrameLayout {

    private TextView mTextError;
    private Button mBtnRetry;

    public PagingErrorView(Context context) {
        this(context, null);
    }

    public PagingErrorView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.defaultPagingErrorStyle);
    }

    public PagingErrorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.layout_paging_error, this, true);

        // Retrieve view
        mTextError = (TextView) view.findViewById(R.id.paging_text_error);
        mBtnRetry = (Button) view.findViewById(R.id.paging_bt_retry);

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PagingErrorView,
                    defStyleAttr, R.style.PagingErrorViewStyle);
            setErrorText(a.getString(R.styleable.PagingErrorView_pagingErrorText));
            setRetryButtonText(a.getString(R.styleable.PagingErrorView_pagingRetryButtonText));

            int errorTextAppearanceId = a.getResourceId(R.styleable.PagingErrorView_pagingErrorTextAppearance, -1);
            if (errorTextAppearanceId > 0) {
                setErrorTextAppearance(errorTextAppearanceId);
            }

            if (a.hasValue(R.styleable.PagingErrorView_pagingRetryButtonColor)) {
                setRetryButtonColor(a.getColor(R.styleable.PagingErrorView_pagingRetryButtonColor, Color.GRAY));
            }

            if (a.hasValue(R.styleable.PagingErrorView_pagingRetryButtonTextColor)) {
                setRetryButtonTextColor(a.getColor(R.styleable.PagingErrorView_pagingRetryButtonTextColor, Color.WHITE));
            }

            a.recycle();
        }
    }

    void setErrorTextAppearance(@StyleRes int resId) {
        TextViewCompat.setTextAppearance(mTextError, resId);
    }

    void setErrorText(CharSequence errorText) {
        mTextError.setText(errorText);
    }

    void setOnRetryClickListener(View.OnClickListener listener) {
        mBtnRetry.setOnClickListener(listener);
    }

    void setRetryButtonTextColor(@ColorInt int color) {
        mBtnRetry.setTextColor(color);
    }

    void setRetryButtonText(CharSequence retryText) {
        mBtnRetry.setText(retryText);
    }

    void setRetryButtonColor(int color) {
        ViewCompat.setBackgroundTintList(mBtnRetry, ColorStateList.valueOf(color));
    }
}
