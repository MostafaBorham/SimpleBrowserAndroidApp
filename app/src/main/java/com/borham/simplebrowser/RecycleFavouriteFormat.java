package com.borham.simplebrowser;

import android.graphics.drawable.Drawable;

import com.borham.simplebrowser.MyRoom.FavouriteFormat;

public class RecycleFavouriteFormat {
    private String name;
    private final Drawable icon;
    private final FavouriteFormat ref;

    public RecycleFavouriteFormat(String name, Drawable icon, FavouriteFormat ref) {
        this.name = name;
        this.icon = icon;
        this.ref = ref;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Drawable getIcon() {
        return icon;
    }

    public FavouriteFormat getRef() {
        return ref;
    }

}
