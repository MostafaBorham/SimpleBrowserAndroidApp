package com.borham.simplebrowser.MyRoom;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "BrowseHistory")
public class BrowseHistoryFormat {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "browsingName")
    private String browsing_name;
    @ColumnInfo(name = "browsingDate")
    private String browsing_date;

    public BrowseHistoryFormat(String browsing_name, String browsing_date) {
        this.browsing_name = browsing_name;
        this.browsing_date = browsing_date;
    }

    public String getBrowsing_name() {
        return browsing_name;
    }

    public void setBrowsing_name(String browsing_name) {
        this.browsing_name = browsing_name;
    }

    public String getBrowsing_date() {
        return browsing_date;
    }

    public void setBrowsing_date(String browsing_date) {
        this.browsing_date = browsing_date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}