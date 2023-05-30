package com.technowapps.sunshine.utilities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.ContextCompat;

import com.technowapps.sunshine.DetailActivity;
import com.example.android.sunshine.R;
import com.technowapps.sunshine.data.SunshinePreferences;
import com.technowapps.sunshine.data.Weather;
import com.technowapps.sunshine.data.WeatherDatabase;


public class NotificationUtils {


    public static final String CHANNEL_ID = "default";
    /*
     * This notification ID can be used to access our notification after we've displayed it. This
     * can be handy when we need to cancel the notification, or perhaps update it. This number is
     * arbitrary and can be set to whatever you like. 3004 is in no way significant.
     */
    private static final int WEATHER_NOTIFICATION_ID = 3004;

    /**
     * Constructs and displays a notification for the newly updated weather for today.
     *
     * @param context Context used to query our ContentProvider and use various Utility methods
     */
    public static void notifyUserOfNewWeather(Context context) {

        createNotificationChannel(context);

        WeatherDatabase mDb = WeatherDatabase.getInstance(context);


        String todayNormalizedDate = String.valueOf(SunshineDateUtils.normalizeDate(System.currentTimeMillis()));
        Weather weather = mDb.weatherDao().loadWeatherDataForDay(todayNormalizedDate);
        /*
         * If todayWeatherCursor is empty, moveToFirst will return false. If our cursor is not
         * empty, we want to show the notification.
         */


        /* Weather ID as returned by API, used to identify the icon to be used */
        int weatherId = weather.getWeatherIdFromServer();
        double high = weather.getMaxTemp();
        double low = weather.getMinTemp();

        Resources resources = context.getResources();
        int largeArtResourceId = SunshineWeatherUtils
                .getLargeArtResourceIdForWeatherCondition(weatherId);

        Bitmap largeIcon = BitmapFactory.decodeResource(
                resources,
                largeArtResourceId);

        String notificationTitle = context.getString(R.string.app_name);

        String notificationText = getNotificationText(context, weatherId, high, low);

        /* getSmallArtResourceIdForWeatherCondition returns the proper art to show given an ID */
        int smallArtResourceId = SunshineWeatherUtils
                .getSmallArtResourceIdForWeatherCondition(weatherId);

        /*
         * NotificationCompat Builder is a very convenient way to build backward-compatible
         * notifications. In order to use it, we provide a context and specify a color for the
         * notification, a couple of different icons, the title for the notification, and
         * finally the text of the notification, which in our case in a summary of today's
         * forecast.
         */
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(smallArtResourceId)
                .setLargeIcon(largeIcon)
                .setContentTitle(notificationTitle)
                .setContentText(notificationText)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setAutoCancel(true);

        /*
         * This Intent will be triggered when the user clicks the notification. In our case,
         * we want to open Sunshine to the DetailActivity to display the newly updated weather.
         */
        Intent detailIntentForToday = new Intent(context, DetailActivity.class);
        detailIntentForToday.putExtra("date", todayNormalizedDate);

        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
        taskStackBuilder.addNextIntentWithParentStack(detailIntentForToday);
        PendingIntent resultPendingIntent;
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            resultPendingIntent = taskStackBuilder
                    .getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE);
        } else {
            resultPendingIntent = taskStackBuilder
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        }


        notificationBuilder.setContentIntent(resultPendingIntent);

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        /* WEATHER_NOTIFICATION_ID allows you to update or cancel the notification later on */
        notificationManager.notify(WEATHER_NOTIFICATION_ID, notificationBuilder.build());

        /*
         * Since we just showed a notification, save the current time. That way, we can check
         * next time the weather is refreshed if we should show another notification.
         */
        SunshinePreferences.saveLastNotificationTime(context, System.currentTimeMillis());


    }

    /**
     * Constructs and returns the summary of a particular day's forecast using various utility
     * methods and resources for formatting. This method is only used to create the text for the
     * notification that appears when the weather is refreshed.
     * <p>
     * The String returned from this method will look something like this:
     * <p>
     * Forecast: Sunny - High: 14°C Low 7°C
     *
     * @param context   Used to access utility methods and resources
     * @param weatherId ID as determined by Open Weather Map
     * @param high      High temperature (either celsius or fahrenheit depending on preferences)
     * @param low       Low temperature (either celsius or fahrenheit depending on preferences)
     * @return Summary of a particular day's forecast
     */
    private static String getNotificationText(Context context, int weatherId, double high, double low) {

        /*
         * Short description of the weather, as provided by the API.
         * e.g "clear" vs "sky is clear".
         */
        String shortDescription = SunshineWeatherUtils
                .getStringForWeatherCondition(context, weatherId);

        String notificationFormat = context.getString(R.string.format_notification);

        /* Using String's format method, we create the forecast summary */
        String notificationText = String.format(notificationFormat,
                shortDescription,
                SunshineWeatherUtils.formatTemperature(context, high),
                SunshineWeatherUtils.formatTemperature(context, low));

        return notificationText;
    }

    private static void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Default";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_HIGH);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
