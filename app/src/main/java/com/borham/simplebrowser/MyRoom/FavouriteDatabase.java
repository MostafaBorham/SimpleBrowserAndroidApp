package com.borham.simplebrowser.MyRoom;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {FavouriteFormat.class}, version = 3)
public abstract class FavouriteDatabase extends RoomDatabase {

    private static FavouriteDatabase FavouriteDB;

    public static FavouriteDatabase getInstance(Context context) {
        if (null == FavouriteDB) {
            FavouriteDB = buildDatabaseInstance(context);
        }
        return FavouriteDB;
    }

    private static FavouriteDatabase buildDatabaseInstance(Context context) {
        return Room.databaseBuilder(context,
                FavouriteDatabase.class,
                Constants.DB_NAME1)
                .allowMainThreadQueries().build();
    }

    public abstract FavouriteDao getFavouriteDao();

    public void cleanUp() {
        FavouriteDB = null;
    }

}
