/*
 *
 *   JavNav
 *    a simple application for general use of the Texas A&M-Kingsville Campus.
 *
 *    Copyright (C) 2019  Manuel Gonzales Jr.
 *
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see [http://www.gnu.org/licenses/].
 *
 */

package com.senior.javnav;

import java.util.concurrent.TimeUnit;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

public class JavServiceScheduler
{
    public void schedule(String minuteMultiplier)
    {
        int setting = Integer.parseInt(minuteMultiplier);
        long minutes = PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS * setting;

        PeriodicWorkRequest.Builder scheduledWorkRequestBuild = new PeriodicWorkRequest.Builder(NewsUpdate.class, minutes, TimeUnit.MINUTES);
        scheduledWorkRequestBuild.addTag("JavServiceUpdater");
        scheduledWorkRequestBuild.setConstraints(new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build());
        PeriodicWorkRequest scheduledWorkRequest = scheduledWorkRequestBuild.build();
        WorkManager.getInstance().enqueueUniquePeriodicWork("PackageTrackerUpdater", ExistingPeriodicWorkPolicy.REPLACE, scheduledWorkRequest);
    }

    public void stop()
    {
        WorkManager.getInstance().cancelAllWorkByTag("JavServiceUpdater");
    }
}
