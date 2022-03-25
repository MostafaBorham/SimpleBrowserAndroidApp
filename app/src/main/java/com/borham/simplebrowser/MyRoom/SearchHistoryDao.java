package com.borham.simplebrowser.MyRoom;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface SearchHistoryDao {
    @Query("SELECT * FROM SearchHistory")
    List<SearchHistoryFormat> getAll();

    @Insert
    void insert(SearchHistoryFormat searchHistoryFormat);

    @Update
    void update(SearchHistoryFormat searchHistoryFormat);


    @Delete
    void delete(SearchHistoryFormat searchHistoryFormat);


    @Delete
    void delete(SearchHistoryFormat... searchHistoryFormats);
}
