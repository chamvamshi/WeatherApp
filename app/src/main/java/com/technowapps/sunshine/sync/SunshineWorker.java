package com.technowapps.sunshine.sync;

import android.content.Context;
import android.text.format.DateUtils;

import androidx.annotation.NonNull;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.technowapps.sunshine.data.SunshinePreferences;
import com.technowapps.sunshine.utilities.NotificationUtils;

public class SunshineWorker extends Worker {

    private final Context context;


    public SunshineWorker(@NonNull Context appContext, @NonNull WorkerParameters workerParams) {
        super(appContext, workerParams);
        this.context = appContext;

    }

    @NonNull
    @Override
    public Result doWork() {
        SunshineSyncTask.syncWeather(context);
        notifyUser(context);
        return Result.success();
    }


    private void notifyUser(Context context) {
        /*
         * Finally, after we insert data into the database, determine whether or not
         * we should notify the user that the weather has been refreshed.
         */
        boolean notificationsEnabled = SunshinePreferences.areNotificationsEnabled(context);

        /*
         * If the last notification was shown was more than 1 day ago, we want to send
         * another notification to the user that the weather has been updated. Remember,
         * it's important that you shouldn't spam your users with notifications.
         */
        long timeSinceLastNotification = SunshinePreferences
                .getEllapsedTimeSinceLastNotification(context);

        boolean oneDayPassedSinceLastNotification = timeSinceLastNotification >= DateUtils.DAY_IN_MILLIS;

        /*
         * We only want to show the notification if the user wants them shown and we
         * haven't shown a notification in the past day.
         */
        if (notificationsEnabled && oneDayPassedSinceLastNotification) {
            NotificationUtils.notifyUserOfNewWeather(context);
        }
    }

}
