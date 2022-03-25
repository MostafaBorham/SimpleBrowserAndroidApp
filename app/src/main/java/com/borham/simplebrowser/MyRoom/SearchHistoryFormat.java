package com.borham.simplebrowser.MyRoom;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "SearchHistory")
public class SearchHistoryFormat {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "searchName")
    private String search_name;
    @ColumnInfo(name = "searchLink")
    private String search_link;
    @ColumnInfo(name = "searchDateTime")
    private String search_date_time;
    @ColumnInfo(name = "searchEngineName")
    private String search_engine_name;

    public SearchHistoryFormat(String search_name, String search_link, String search_date_time, String search_engine_name) {
        this.search_name = search_name;
        this.search_link = search_link;
        this.search_date_time = search_date_time;
        this.search_engine_name = search_engine_name;
    }

    public String getSearch_engine_name() {
        return search_engine_name;
    }

    public void setSearch_engine_name(String search_engine_name) {
        this.search_engine_name = search_engine_name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSearch_name() {
        return search_name;
    }

    public void setSearch_name(String search_name) {
        this.search_name = search_name;
    }

    public String getSearch_link() {
        return search_link;
    }

    public void setSearch_link(String search_link) {
        this.search_link = search_link;
    }

    public String getSearch_date_time() {
        return search_date_time;
    }

    public void setSearch_date_time(String search_date_time) {
        this.search_date_time = search_date_time;
    }
}
