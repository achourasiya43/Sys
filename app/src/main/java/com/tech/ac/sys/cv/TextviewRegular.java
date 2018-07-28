package com.tech.ac.sys.cv;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * Created by Anil on 23/5/17.
 */

public class TextviewRegular extends AppCompatTextView {

    public TextviewRegular(Context context) {
        super(context);
        Typeface face= Typeface.createFromAsset(context.getAssets(), "font/Raleway_SemiBold.ttf");
        this.setTypeface(face);
    }

    public TextviewRegular(Context context, AttributeSet attrs) {
        super(context, attrs);
        Typeface face= Typeface.createFromAsset(context.getAssets(), "font/Raleway_SemiBold.ttf");
        this.setTypeface(face);
    }

    public TextviewRegular(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Typeface face= Typeface.createFromAsset(context.getAssets(), "font/Raleway_SemiBold.ttf");
        this.setTypeface(face);
    }

    protected void onDraw (Canvas canvas) {
        super.onDraw(canvas);


    }
}
