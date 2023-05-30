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
package com.technowapps.sunshine.sync;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.technowapps.sunshine.utilities.AppExecutors;


import java.util.concurrent.TimeUnit;

public class SunshineSyncUtils {

    private static boolean sInitialized;



    public static void initializeWorkManager(final Context context) {

        /*
         * Only perform initialization once per app lifetime. If initialization has already been
         * performed, we have nothing to do in this method.
         */

        if (sInitialized) return;
        sInitialized = true;


        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(SunshineWorker.class, 1, TimeUnit.DAYS)
                .setConstraints(constraints)
                .build();

        String UNIQUE_PERIODIC_WORK_NAME = "my-unique-work";

        WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(UNIQUE_PERIODIC_WORK_NAME, ExistingPeriodicWorkPolicy.KEEP,request);


    }


    /**
     * Helper method to perform a sync immediately using an IntentService for asynchronous
     * execution.
     */
    public static void startImmediateSync(@NonNull final Context context) {
        AppExecutors.getInstance().networkIO().execute(() -> SunshineSyncTask.syncWeather(context));
    }
}