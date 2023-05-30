package com.technowapps.sunshine.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "weather", indices = {@Index(value = {"date"}, unique = true)})

public class Weather {

    /* This constructor will be used by Room */
    public Weather(int mWeatherId, long mDate, int mWeatherIdFromServer, double mMinTemp, double mMaxTemp, int mHumidity, double mPressure, double mSpeed, double mMeteorologicalDegrees) {
        this.mWeatherId = mWeatherId;
        this.mDate = mDate;
        this.mWeatherIdFromServer = mWeatherIdFromServer;
        this.mMinTemp = mMinTemp;
        this.mMaxTemp = mMaxTemp;
        this.mHumidity = mHumidity;
        this.mPressure = mPressure;
        this.mSpeed = mSpeed;
        this.mMeteorologicalDegrees = mMeteorologicalDegrees;
    }

    /* This constructor will be used by us */
    @Ignore
    public Weather(long mDate, int mWeatherIdFromServer, double mMinTemp, double mMaxTemp, int mHumidity, double mPressure, double mSpeed, double mMeteorologicalDegrees) {
        this.mDate = mDate;
        this.mWeatherIdFromServer = mWeatherIdFromServer;
        this.mMinTemp = mMinTemp;
        this.mMaxTemp = mMaxTemp;
        this.mHumidity = mHumidity;
        this.mPressure = mPressure;
        this.mSpeed = mSpeed;
        this.mMeteorologicalDegrees = mMeteorologicalDegrees;
    }

    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    private int mWeatherId;

    @ColumnInfo(name = "date")
    private long mDate;

    @ColumnInfo(name = "weather_id")
    private int mWeatherIdFromServer;

    @ColumnInfo(name = "min")
    private double mMinTemp;

    @ColumnInfo(name = "max")
    private double mMaxTemp;

    @ColumnInfo(name = "humidity")
    private int mHumidity;

    @ColumnInfo(name = "pressure")
    private double mPressure;

    @ColumnInfo(name = "wind")
    private double mSpeed;

    @ColumnInfo(name = "degrees")
    private double mMeteorologicalDegrees;

    public int getWeatherId() {
        return mWeatherId;
    }

    public void setWeatherId(int mWeatherId) {
        this.mWeatherId = mWeatherId;
    }

    public long getDate() {
        return mDate;
    }

    public void setDate(long mDate) {
        this.mDate = mDate;
    }

    public int getWeatherIdFromServer() {
        return mWeatherIdFromServer;
    }

    public void setWeatherIdFromServer(int mWeatherIdFromServer) {
        this.mWeatherIdFromServer = mWeatherIdFromServer;
    }

    public double getMinTemp() {
        return mMinTemp;
    }

    public void setMinTemp(double mMinTemp) {
        this.mMinTemp = mMinTemp;
    }

    public double getMaxTemp() {
        return mMaxTemp;
    }

    public void setMaxTemp(double mMaxTemp) {
        this.mMaxTemp = mMaxTemp;
    }

    public int getHumidity() {
        return mHumidity;
    }

    public void setHumidity(int mHumidity) {
        this.mHumidity = mHumidity;
    }

    public double getPressure() {
        return mPressure;
    }

    public void setPressure(double mPressure) {
        this.mPressure = mPressure;
    }

    public double getSpeed() {
        return mSpeed;
    }

    public void setSpeed(double mSpeed) {
        this.mSpeed = mSpeed;
    }

    public double getMeteorologicalDegrees() {
        return mMeteorologicalDegrees;
    }

    public void setMeteorologicalDegrees(double mMeteorologicalDegrees) {
        this.mMeteorologicalDegrees = mMeteorologicalDegrees;
    }
}
