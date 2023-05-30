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
package com.technowapps.sunshine;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.preference.CheckBoxPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import com.example.android.sunshine.R;
import com.technowapps.sunshine.data.SunshinePreferences;
import com.technowapps.sunshine.sync.SunshineSyncUtils;
import com.technowapps.sunshine.utilities.AppExecutors;
import com.technowapps.sunshine.utilities.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

/**
 * The SettingsFragment serves as the display for all of the user's settings. In Sunshine, the
 * user will be able to change their preference for units of measurement from metric to imperial,
 * set their preferred weather location, and indicate whether or not they'd like to see
 * notifications.
 * <p>
 * Please note: If you are using our dummy weather services, the location returned will always be
 * Mountain View, California.
 */
public class SettingsFragment extends PreferenceFragmentCompat implements
        Preference.OnPreferenceChangeListener {





    private void setPreferenceSummary(Preference preference, Object value) {
        String stringValue = value.toString();

        if (preference instanceof ListPreference) {
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list (since they have separate labels/values).
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        } else {
            // For other preferences, set the summary to the value's simple string representation.
            preference.setSummary(stringValue);
        }
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        // Add 'general' preferences, defined in the XML file
        addPreferencesFromResource(R.xml.pref_general);

        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        PreferenceScreen prefScreen = getPreferenceScreen();
        int count = prefScreen.getPreferenceCount();
        for (int i = 0; i < count; i++) {
            Preference p = prefScreen.getPreference(i);
            p.setOnPreferenceChangeListener(this);

            if (!(p instanceof CheckBoxPreference)) {
                String value = sharedPreferences.getString(p.getKey(), "");
                setPreferenceSummary(p, value);
            }
        }
    }


    @Override
    public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {

        Activity activity = getActivity();
        String key = preference.getKey();

        if (key.equals(getString(R.string.pref_location_key))) {

            try {

                fetchAndStoreLonLat(activity, newValue.toString());
                SunshineSyncUtils.startImmediateSync(activity);

            } catch (Exception e) {

                if (e instanceof JSONException) {
                    Toast.makeText(activity, "Invalid Location", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(activity, "An error has occurred.", Toast.LENGTH_SHORT).show();

                }
                /* Returning false to avoid saving preference when it is invalid or an error has occurred*/
                return false;
            }


        } else if(key.equals(getString(R.string.pref_units_key))){
            SunshinePreferences.isMetricChanged = true;
        }
        if (!(preference instanceof CheckBoxPreference)) {
            setPreferenceSummary(preference, newValue);
        }
        return true;
    }

    public static void fetchAndStoreLonLat(Context context, String newLocation) throws Exception {

        URL urlForLonLat = NetworkUtils.getUrlForLongLatQuery(context, newLocation);

        /*
         * Using fetchResultInBackgroundAndReturnResultOnMainThread() method to perform query on background thread and get result
         * on main thread, and based on that result we will decide that whether we have to save preference or not
         */
        String response = AppExecutors.getInstance().fetchResultInBackgroundAndReturnResultOnMainThread(urlForLonLat);

        /* Destructuring the result response and then storing the latitude and longitude in shared preferences */
        JSONArray returnedArray = new JSONArray(response);
        JSONObject geologicalResponseJson = returnedArray.getJSONObject(0);
        double lat = Double.parseDouble(geologicalResponseJson.getString("lat"));
        double lon = Double.parseDouble(geologicalResponseJson.getString("lon"));
        SunshinePreferences.setLocationDetails(context, lat, lon);

    }




}
