package io.fruitful.collectionmodule.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.IntDef;
import android.support.annotation.LayoutRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import io.fruitful.collectionmodule.R;

/**
 * EmptyView class with a text description and a button.
 */
public class EmptyView extends RelativeLayout {

    public static final int NORMAL = 0;
    public static final int HIDE = 1;
    public static final int PROGRESS = 2;
    public static final int ERROR = 3;

    @IntDef({NORMAL, HIDE, PROGRESS, ERROR})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Mode {
    }

    private View mEmptyView;
    private ViewGroup mCustomEmptyViewContainer;
    private TextView mTextDescription;
    private TextView mTextErrorDescription;
    private View mProgress;
    private View mErrorView;
    private View mBtnRetry;
    private View mCustomEmptyView;

    public EmptyView(Context context) {
        this(context, null);
    }

    public EmptyView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
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
        mBtnRetry = view.findViewById(R.id.empty_retry);

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.EmptyView,
                    defStyleAttr, 0);
            setEpv_description(a.getString(R.styleable.EmptyView_epv_description));
            setErrorDescription(a.getString(R.styleable.EmptyView_epv_error_description));
            int emptyViewResId = a.getResourceId(R.styleable.EmptyView_epv_empty_view, -1);
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

    public void setCustomEmptyView(@LayoutRes int resId) {
        setCustomEmptyView(LayoutInflater.from(getContext()).inflate(resId, mCustomEmptyViewContainer, false));
    }

    public View getCustomEmptyView() {
        return mCustomEmptyView;
    }

    public void setMode(@Mode int mode) {
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

    public void setEpv_description(CharSequence description) {
        mTextDescription.setText(description);
    }

    public void setErrorDescription(CharSequence errorDescription) {
        mTextErrorDescription.setText(errorDescription);
    }

    public void setOnRetryClickListener(OnClickListener listener) {
        mBtnRetry.setOnClickListener(listener);
    }
}
