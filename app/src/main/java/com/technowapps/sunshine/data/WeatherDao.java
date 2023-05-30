package com.technowapps.sunshine.data;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface WeatherDao {


    @Query("SELECT * FROM weather WHERE date >= :normalizedUtcNow")
    LiveData<List<Weather>> loadWeatherDataForTodayOnwards(String normalizedUtcNow);

    @Query("SELECT * FROM weather WHERE date = :normalizedUtc")
    Weather loadWeatherDataForDay(String normalizedUtc);


    @Query("DELETE FROM weather")
    void deleteWeatherData();

    @Insert
    long[] insertAllWeatherData(Weather... weathers);
}
