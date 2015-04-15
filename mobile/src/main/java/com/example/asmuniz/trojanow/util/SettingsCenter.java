package com.example.asmuniz.trojanow.util;

/**
 * Created by asmuniz on 3/30/15.
 *
 * Contains common operations that will be perform regarding Settings.
 */
public class SettingsCenter {

    private int fontSize;
    private boolean useLocation;

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
        // also need to persist this change
    }

    public boolean isUseLocation() {
        return useLocation;
    }

    public void setUseLocation(boolean useLocation) {
        this.useLocation = useLocation;
        // also need to persist this change
    }
}
