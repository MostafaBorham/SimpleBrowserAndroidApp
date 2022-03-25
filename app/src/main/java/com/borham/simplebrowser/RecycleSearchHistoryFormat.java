package com.borham.simplebrowser;

import android.graphics.drawable.Drawable;

import com.borham.simplebrowser.MyRoom.SearchHistoryFormat;

public class RecycleSearchHistoryFormat {
    private String detail;
    private Drawable icon;
    private SearchHistoryFormat ref;

    public RecycleSearchHistoryFormat(String detail, Drawable icon, SearchHistoryFormat ref) {
        this.detail = detail;
        this.icon = icon;
        this.ref = ref;
    }

    public String getDetail() {
        return detail;
    }

    public Drawable getIcon() {
        return icon;
    }

    public SearchHistoryFormat getRef() {
        return ref;
    }
}
