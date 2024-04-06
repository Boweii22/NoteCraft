package com.smartherd.notes;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesHelper {
    private static final String PREFERENCES_FILE = "theme_preferences";
    private static final String THEME_KEY = "theme_key";
    private static final String PREFS_NAME = "font_preferences";
    private static final String FONT_KEY = "fontStyle";

    public static void saveThemeChoice(Context context, boolean isBlackTheme) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(THEME_KEY, isBlackTheme);
        editor.apply();
    }

    public static boolean loadThemeChoice(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(THEME_KEY, false); // false by default means white theme
    }

    public static void setFontStyle(Context context, String fontStyle) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(FONT_KEY, fontStyle);
        editor.apply();
    }

    public static String getFontStyle(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return preferences.getString(FONT_KEY, "default");
    }
}
