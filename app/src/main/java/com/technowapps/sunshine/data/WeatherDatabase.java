package com.technowapps.sunshine.data;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Weather.class}, version = 1, exportSchema = false)
public abstract class WeatherDatabase extends RoomDatabase {

    public static final String DATABASE_NAME = "weatherdb";
    private static WeatherDatabase sInstance;
    private static final Object LOCK = new Object();

    public abstract WeatherDao weatherDao();

    public static WeatherDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {

                sInstance = Room.databaseBuilder(context.getApplicationContext(), WeatherDatabase.class, DATABASE_NAME).build();
            }
        }
        return sInstance;
    }
}
