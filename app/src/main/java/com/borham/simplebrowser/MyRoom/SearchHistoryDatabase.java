package com.borham.simplebrowser.MyRoom;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {SearchHistoryFormat.class}, version = 4)
public abstract class SearchHistoryDatabase extends RoomDatabase {
    private static SearchHistoryDatabase searchHistoryDB;

    public static SearchHistoryDatabase getInstance(Context context) {
        if (null == searchHistoryDB) {
            searchHistoryDB = buildDatabaseInstance(context);
        }
        return searchHistoryDB;
    }

    private static SearchHistoryDatabase buildDatabaseInstance(Context context) {
        return Room.databaseBuilder(context,
                SearchHistoryDatabase.class,
                Constants.DB_NAME2)
                .allowMainThreadQueries().build();
    }

    public abstract SearchHistoryDao getSearchHistoryDao();

    public void cleanUp() {
        searchHistoryDB = null;
    }
}
