package com.borham.simplebrowser.MyRoom;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {BrowseHistoryFormat.class}, version = 1)
public abstract class BrowseHistoryDatabase extends RoomDatabase {

    private static BrowseHistoryDatabase BrowseHistoryDB;

    public static BrowseHistoryDatabase getInstance(Context context) {
        if (null == BrowseHistoryDB) {
            BrowseHistoryDB = buildDatabaseInstance(context);
        }
        return BrowseHistoryDB;
    }

    private static BrowseHistoryDatabase buildDatabaseInstance(Context context) {
        return Room.databaseBuilder(context,
                BrowseHistoryDatabase.class,
                Constants.DB_NAME)
                .allowMainThreadQueries().build();
    }

    public abstract BrowseHistoryDao getBrowseHistoryDao();

    public void cleanUp() {
        BrowseHistoryDB = null;
    }
}