package com.borham.simplebrowser.MyRoom;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface BrowseHistoryDao {
    @Query("SELECT * FROM BrowseHistory")
    List<BrowseHistoryFormat> getAll();

    @Insert
    void insert(BrowseHistoryFormat inserted_history);

    @Update
    void update(BrowseHistoryFormat updated_history);

    @Delete
    void delete(BrowseHistoryFormat deleted_history);

    @Delete
    void delete(BrowseHistoryFormat... histories);
}