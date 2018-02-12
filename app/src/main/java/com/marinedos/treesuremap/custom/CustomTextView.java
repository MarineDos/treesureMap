package com.marinedos.treesuremap.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.v4.util.LruCache;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.Log;

import com.marinedos.treesuremap.R;

/**
 * Created by Marine on 27/01/2018.
 */

public class CustomTextView extends AppCompatTextView {
    //créé un cache de Typeface, pouvant contenir 12 fonts
    private static LruCache<String, Typeface> TYPEFACE_CACHE = new LruCache<String, Typeface>(12);

    /**
     * Default constructor
     * @param context Context
     * @param attrs Attributes
     */
    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        //Typeface.createFromAsset doesn't work in the layout editor. Skipping...
        if (isInEditMode()) {
            return;
        }

        // Retrieve CustomTextView attributes
        TypedArray styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.com_marinedos_treesuremap_custom_CustomTextView);

        // Retrieve font attribute
        String fontName = styledAttrs.getString(R.styleable.com_marinedos_treesuremap_custom_CustomTextView_custom_font);
        styledAttrs.recycle();

        // Set font
        setTypeFace(fontName);
    }

    /**
     * Set font according fontName
     * @param fontName Name of the font
     */
    public void setTypeFace(String fontName) {
        if(fontName != null){
            try {
                Typeface typeface = TYPEFACE_CACHE.get(fontName);
                if (typeface == null) {
                    typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/" + fontName);
                    TYPEFACE_CACHE.put(fontName, typeface);
                }

                setTypeface(typeface);
            } catch (Exception e) {
                Log.e("FONT", fontName + " not found", e);
            }
        }
    }
}
