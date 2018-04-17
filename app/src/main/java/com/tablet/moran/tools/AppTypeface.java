package com.tablet.moran.tools;

import android.content.Context;
import android.graphics.Typeface;

import java.lang.reflect.Field;

/**
 * Created by ASUS on 2017/1/17.
 */

public final class AppTypeface {

    private enum DefaultFont {
        DEFAULT("DEFAULT"),
        DEFAULT_BOLD("DEFAULT_BOLD"),
        MONOSPACE("MONOSPACE"),
        SERIF("SERIF"),
        SANS_SERIF("SANS_SERIF");

        private String value;

        DefaultFont(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public static boolean replace = true;

    public static final String REPLACE_FONT = "tablet.TTF";

    /**
     * @param context
     */
    public static final void init(Context context) {
        if (replace) {
            final Typeface replace = Typeface.createFromAsset(context.getAssets(), REPLACE_FONT);
            for (DefaultFont defaultFont : DefaultFont.values()) {
                replaceFont(defaultFont.getValue(), replace);
            }
        }
    }

    public static final void replaceFont(String fontName, Typeface typeface) {
        try {
            final Field staticField = Typeface.class.getDeclaredField(fontName);
            staticField.setAccessible(true);
            staticField.set(null, typeface);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static Typeface getFangZhengTypeface(Context context) {
        return Typeface.createFromAsset(context.getAssets(), "fangzhengxiaosong.ttf");
    }

    public static Typeface getLantingLightTypeface(Context context) {
        return Typeface.createFromAsset(context.getAssets(), "fangzhenglanting_light.ttf");
    }

}