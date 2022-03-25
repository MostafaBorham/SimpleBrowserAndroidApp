package com.borham.simplebrowser;

import android.graphics.drawable.Drawable;

public class RecycleBrowseHistoryFormat {
    private String detail;
    private Drawable icon;
    private com.borham.simplebrowser.MyRoom.BrowseHistoryFormat ref;

    public RecycleBrowseHistoryFormat(String detail, Drawable icon, com.borham.simplebrowser.MyRoom.BrowseHistoryFormat ref) {
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

    public com.borham.simplebrowser.MyRoom.BrowseHistoryFormat getRef() {
        return ref;
    }
}
