package com.rdshoep.android.view;

/*
 * @description
 *   Please write the FontableTextView module's description
 * @author Zhang (rdshoep@126.com)
 *   http://www.rdshoep.com/
 * @version 
 *   1.0.0(5/4/2016)
 */

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

import com.rdshoep.android.fontableTextView.R;

public class FontableTextView extends TextView {
    public FontableTextView(Context context) {
        this(context, null);
    }

    public FontableTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FontableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FontableTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (isInEditMode()) return;

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FontableTextView);

        String customFontPath = a.getString(R.styleable.FontableTextView_fontPath);
        a.recycle();

        FontManager.setTextTypeface(context, this, customFontPath);
    }
}