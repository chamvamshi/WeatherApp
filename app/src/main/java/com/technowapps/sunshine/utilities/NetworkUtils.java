/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.technowapps.sunshine.utilities;

import android.content.Context;
import android.net.Uri;

import com.example.android.sunshine.R;
import com.technowapps.sunshine.data.SunshinePreferences;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * These utilities will be used to communicate with the weather servers.
 */
public final class NetworkUtils {


    /*
     * Sunshine was originally built to use OpenWeatherMap's API. However, we wanted to provide
     * a way to much more easily test the app and provide more varied weather data. After all, in
     * Mountain View (Google's HQ), it gets very boring looking at a forecast of perfectly clear
     * skies at 75°F every day... (UGH!) The solution we came up with was to host our own fake
     * weather server. With this server, there are two URL's you can use. The first (and default)
     * URL will return dynamic weather data. Each time the app refreshes, you will get different,
     * completely random weather data. This is incredibly useful for testing the robustness of your
     * application, as different weather JSON will provide edge cases for some of your methods.
     *
     * If you'd prefer to test with the weather data that you will see in the videos on Udacity,
     * you can do so by setting the FORECAST_BASE_URL to STATIC_WEATHER_URL below.
     */


    private static final String FORECAST_BASE_URL = "https://api.openweathermap.org/data/3.0/onecall";

    private static final String GEOCODING_API_BASE_URL = "https://api.openweathermap.org/geo/1.0/direct";

    /*
     * NOTE: These values only effect responses from OpenWeatherMap, NOT from the fake weather
     * server. They are simply here to allow us to teach you how to build a URL if you were to use
     * a real API.If you want to connect your app to OpenWeatherMap's API, feel free to! However,
     * we are not going to show you how to do so in this course.
     */

    /* The format we want our API to return */
    private static final String format = "json";
    /* The units we want our API to return */
    private static final String units = "metric";


    /* The query parameter allows us to provide a location string to the API */
    private static final String QUERY_PARAM = "q";

    private static final String LAT_PARAM = "lat";
    private static final String LON_PARAM = "lon";

    /* The format parameter allows us to designate whether we want JSON or XML from our API */
    private static final String FORMAT_PARAM = "mode";
    /* The units parameter allows us to designate whether we want metric units or imperial units */
    private static final String UNITS_PARAM = "units";

    private static final String API_KEY = "appid";

    private static final String excluded_fields_encoded_query = "exclude=current,minutely,hourly";


    public static URL getUrlForWeatherForecast(Context context) {


        String api_key = context.getResources().getString(R.string.open_weather_api_key);

        double[] preferredCoordinates = SunshinePreferences.getLocationCoordinates(context);
        double latitude = preferredCoordinates[0];
        double longitude = preferredCoordinates[1];
        Uri weatherQueryUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                .encodedQuery(excluded_fields_encoded_query)
                .appendQueryParameter(LAT_PARAM, String.valueOf(latitude))
                .appendQueryParameter(LON_PARAM, String.valueOf(longitude))
                .appendQueryParameter(FORMAT_PARAM, format)
                .appendQueryParameter(UNITS_PARAM, units)
                .appendQueryParameter(API_KEY, api_key)
                .build();

        try {
            URL weatherQueryUrl = new URL(weatherQueryUri.toString());

            return weatherQueryUrl;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }

    }

    /*
     * We will use geolocation api of openweathermap api to fetch
     * coordinates of a particular location
     *
     */
    public static URL getUrlForLongLatQuery(Context context, String newLocation) {
        String api_key = context.getResources().getString(R.string.open_weather_api_key);

        Uri weatherQueryUri = Uri.parse(GEOCODING_API_BASE_URL).buildUpon()
                .appendQueryParameter(QUERY_PARAM, newLocation)
                .appendQueryParameter(API_KEY, api_key)
                .build();

        try {
            URL weatherQueryUrl = new URL(weatherQueryUri.toString());

            return weatherQueryUrl;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response, null if no response
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {


        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();

        return response.body().string();
    }


}