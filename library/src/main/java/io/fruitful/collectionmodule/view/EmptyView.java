package io.fruitful.collectionmodule.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.LayoutRes;
import android.support.annotation.StyleRes;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.TextViewCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import io.fruitful.collectionmodule.R;

/**
 * EmptyView class with a text description and a button.
 */
public class EmptyView extends RelativeLayout implements IEmptyView {

    private View mEmptyView;
    private ViewGroup mCustomEmptyViewContainer;
    private TextView mTextDescription;
    private TextView mTextErrorDescription;
    private View mProgress;
    private View mErrorView;
    private Button mBtnRetry;
    private View mCustomEmptyView;

    public EmptyView(Context context) {
        this(context, null);
    }

    public EmptyView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.defaultEmptyViewStyle);
    }

    public EmptyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.layout_empty_view, this, true);

        // Retrieve view
        mEmptyView = view.findViewById(R.id.empty_view_container);
        mCustomEmptyViewContainer = (ViewGroup) view.findViewById(R.id.custom_empty_view);
        mTextDescription = (TextView) view.findViewById(R.id.empty_description);
        mTextErrorDescription = (TextView) view.findViewById(R.id.empty_error_description);
        mProgress = view.findViewById(R.id.empty_progress);
        mErrorView = view.findViewById(R.id.empty_error);
        mBtnRetry = (Button) view.findViewById(R.id.empty_retry);

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.EmptyView,
                    defStyleAttr, R.style.EmptyViewStyle);
            setEmptyText(a.getString(R.styleable.EmptyView_emptyText));
            setEmptyErrorText(a.getString(R.styleable.EmptyView_emptyErrorText));
            setEmptyButtonText(a.getString(R.styleable.EmptyView_emptyRetryButtonText));
            int textAppearanceId = a.getResourceId(R.styleable.EmptyView_emptyTextAppearance, -1);
            if (textAppearanceId > 0) {
                setEmptyTextAppearance(textAppearanceId);
            }

            int errorTextAppearanceId = a.getResourceId(R.styleable.EmptyView_emptyErrorTextAppearance, -1);
            if (errorTextAppearanceId > 0) {
                setEmptyErrorTextAppearance(errorTextAppearanceId);
            }

            if (a.hasValue(R.styleable.EmptyView_emptyRetryButtonColor)) {
                setEmptyButtonColor(a.getColor(R.styleable.EmptyView_emptyRetryButtonColor, Color.GRAY));
            }

            if (a.hasValue(R.styleable.EmptyView_emptyRetryButtonTextColor)) {
                setEmptyButtonTextColor(a.getColor(R.styleable.EmptyView_emptyRetryButtonTextColor, Color.WHITE));
            }

            int emptyViewResId = a.getResourceId(R.styleable.EmptyView_emptyCustomView, -1);
            if (emptyViewResId > 0) {
                setCustomEmptyView(inflater.inflate(emptyViewResId, mCustomEmptyViewContainer, false));
            }

            a.recycle();
        }
        ensureShowCustomEmptyView();
    }

    private void ensureShowCustomEmptyView() {
        mCustomEmptyViewContainer.setVisibility(mCustomEmptyView == null ? GONE : VISIBLE);
    }

    public void setCustomEmptyView(View customEmptyView) {
        mCustomEmptyViewContainer.removeAllViews();
        if (customEmptyView == null) {
            mCustomEmptyView = null;
            return;
        }
        if (customEmptyView.getParent() != null) {
            throw new IllegalStateException("Your empty view is inflated in another layout");
        }
        mCustomEmptyView = customEmptyView;
        mCustomEmptyViewContainer.addView(customEmptyView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ensureShowCustomEmptyView();
    }

    private void display(boolean visible, boolean emptyViewVisible, boolean progressVisible,
                         boolean errorVisible) {
        if (!visible) {
            setVisibility(GONE);
            return;
        }
        setVisibility(VISIBLE);
        mEmptyView.setVisibility(emptyViewVisible ? VISIBLE : GONE);
        mProgress.setVisibility(progressVisible ? VISIBLE : GONE);
        mErrorView.setVisibility(errorVisible ? VISIBLE : GONE);
    }

    public View getCustomEmptyView() {
        return mCustomEmptyView;
    }

    @Override
    public void setEmptyMode(@Mode int mode) {
        switch (mode) {
            case NORMAL:
                display(true, true, false, false);
                break;

            case PROGRESS:
                display(true, false, true, false);
                break;

            case ERROR:
                display(true, false, false, true);
                break;

            default:
                display(false, false, false, false);
                break;
        }
    }

    @Override
    public void setEmptyText(CharSequence emptyText) {
        mTextDescription.setText(emptyText);
    }

    @Override
    public void setEmptyErrorText(CharSequence errorText) {
        mTextErrorDescription.setText(errorText);
    }

    @Override
    public void setOnRetryClickListener(OnClickListener listener) {
        mBtnRetry.setOnClickListener(listener);
    }

    @Override
    public void setEmptyTextAppearance(@StyleRes int resId) {
        TextViewCompat.setTextAppearance(mTextDescription, resId);
    }

    @Override
    public void setEmptyErrorTextAppearance(@StyleRes int resId) {
        TextViewCompat.setTextAppearance(mTextErrorDescription, resId);
    }

    @Override
    public void setEmptyButtonTextColor(@ColorInt int color) {
        mBtnRetry.setTextColor(color);
    }

    @Override
    public void setEmptyButtonText(CharSequence retryText) {
        mBtnRetry.setText(retryText);
    }

    @Override
    public void setEmptyButtonColor(@ColorInt int color) {
        ViewCompat.setBackgroundTintList(mBtnRetry, ColorStateList.valueOf(color));
    }

    @Override
    public void setCustomEmptyView(@LayoutRes int resId) {
        setCustomEmptyView(LayoutInflater.from(getContext()).inflate(resId, mCustomEmptyViewContainer, false));
    }
}
