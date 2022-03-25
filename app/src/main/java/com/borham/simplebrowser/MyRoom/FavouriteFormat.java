package com.borham.simplebrowser.MyRoom;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Favourites")
public class FavouriteFormat {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "favName")
    private String fav_name;

    public FavouriteFormat(String fav_name) {
        this.fav_name = fav_name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFav_name() {
        return fav_name;
    }

    public void setFav_name(String fav_name) {
        this.fav_name = fav_name;
    }

}
