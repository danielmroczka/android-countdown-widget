package com.labs.dm.countdown;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.RemoteViews;

import com.labs.dm.com.countdown.R;

import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of App Widget functionality.
 */
public class CountdownWidget extends AppWidgetProvider {

    private Calendar getEndDate(Context context, int mAppWidgetId) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        int day = prefs.getInt(getKey(mAppWidgetId, "day"), 0);
        int month = prefs.getInt(getKey(mAppWidgetId, "month"), 0);
        int year = prefs.getInt(getKey(mAppWidgetId, "year"), 0);
        int hour = prefs.getInt(getKey(mAppWidgetId, "hour"), 0);
        int minute = prefs.getInt(getKey(mAppWidgetId, "minute"), 0);
        Calendar c = Calendar.getInstance();

        c.set(year, month, day, hour, minute, 0);
        return c;
    }

    private String getKey(int widgetId, String key) {
        return "widget." + widgetId + "." + key;
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        ComponentName thisWidget = new ComponentName(context, CountdownWidget.class);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        for (int mAppWidgetId : allWidgetIds) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.countdown_widget);
            long now = Calendar.getInstance().getTimeInMillis();
            long endDate = getEndDate(context, mAppWidgetId).getTimeInMillis();
            long diff = endDate - now;
            long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
            diff = diff - (days * 86400000L);
            long hours = TimeUnit.HOURS.convert(diff, TimeUnit.MILLISECONDS);
            long start = prefs.getLong(getKey(mAppWidgetId, "start"), 0);

            remoteViews.setTextViewText(R.id.displayDay, String.valueOf(days));
            remoteViews.setTextViewText(R.id.displayHour, String.valueOf(hours));

            int progress = (int) (100 * (now - start) / (endDate - start));
            remoteViews.setInt(R.id.progressBar, "setProgress", progress);
            remoteViews.setViewVisibility(R.id.layoutHour, hours > 0 ? View.VISIBLE : View.GONE);

            Intent intent = new Intent(context, CountdownWidget.class);
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.widgetLayout, pendingIntent);
            appWidgetManager.updateAppWidget(mAppWidgetId, remoteViews);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        for (int mAppWidgetId : appWidgetIds) {
            for (Map.Entry<String, ?> entry : prefs.getAll().entrySet()) {
                if (entry.getKey().startsWith("widget." + mAppWidgetId + ".")) {
                    prefs.edit().remove(entry.getKey()).commit();
                }
            }
        }
        super.onDeleted(context, appWidgetIds);
    }
}

