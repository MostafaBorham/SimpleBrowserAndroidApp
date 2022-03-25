package com.borham.simplebrowser.MyRoom;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface FavouriteDao {
    @Query("SELECT * FROM Favourites")
    List<FavouriteFormat> getAll();


    @Insert
    void insert(FavouriteFormat favouriteFormat);

    @Update
    void update(FavouriteFormat favouriteFormat);


    @Delete
    void delete(FavouriteFormat favouriteFormat);


    @Delete
    void delete(FavouriteFormat... favouriteFormats);

}
