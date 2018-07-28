package com.tech.ac.sys.cv;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * Created by dharmraj on 23/5/17.
 */

public class TextviewLight extends AppCompatTextView {

    public TextviewLight(Context context) {
        super(context);
        Typeface face= Typeface.createFromAsset(context.getAssets(), "font/Roboto-Light.ttf");
        this.setTypeface(face);
    }

    public TextviewLight(Context context, AttributeSet attrs) {
        super(context, attrs);
        Typeface face= Typeface.createFromAsset(context.getAssets(), "font/Roboto-Light.ttf");
        this.setTypeface(face);
    }

    public TextviewLight(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Typeface face= Typeface.createFromAsset(context.getAssets(), "font/Roboto-Light.ttf");
        this.setTypeface(face);
    }

    protected void onDraw (Canvas canvas) {
        super.onDraw(canvas);


    }
}
