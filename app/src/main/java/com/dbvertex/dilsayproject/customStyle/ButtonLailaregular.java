package com.dbvertex.dilsayproject.customStyle;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by admin on 17-04-2018.
 */

@SuppressLint("AppCompatCustomView")
public class ButtonLailaregular extends Button
{
    public ButtonLailaregular(Context context) {
        super(context);

        applyCustomFont(context);
    }

    public ButtonLailaregular(Context context, AttributeSet attrs) {
        super(context, attrs);

        applyCustomFont(context);
    }

    public ButtonLailaregular(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
        Typeface customFont = Typeface.createFromAsset(context.getAssets(),"fonts/BodoniFLF-Roman.ttf");
        setTypeface(customFont);
    }
}
