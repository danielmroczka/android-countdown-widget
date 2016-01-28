package com.labs.dm.countdown;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import java.util.Calendar;
import java.util.Map;

/**
 * Implementation of App Widget functionality.
 */
public class CountdownWidget extends AppWidgetProvider {

    private long getEndDate(Context context, int mAppWidgetId) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getLong(getKey(mAppWidgetId, "end"), 0);
    }

    private String getKey(int widgetId, String key) {
        return "widget." + widgetId + "." + key;
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        ComponentName thisWidget = new ComponentName(context, CountdownWidget.class);
        TimeCalculator tc = new TimeCalculator();
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        for (int mAppWidgetId : allWidgetIds) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.countdown_widget);
            long now = Calendar.getInstance().getTimeInMillis();
            long endDate = getEndDate(context, mAppWidgetId);

            Log.i("end", String.valueOf(endDate));

            TimeCalculator.TimeContainer container = tc.calculate(endDate, now, 0);
            long days = container.days;
            long hours = container.hours;
            long start = prefs.getLong(getKey(mAppWidgetId, "start"), 0);

            remoteViews.setTextViewText(R.id.displayDay, String.valueOf(days));
            remoteViews.setTextViewText(R.id.displayHour, String.valueOf(hours));

            int progress = (endDate - start) > 0 ? (int) (100 * (now - start) / (endDate - start)) : 100;
            remoteViews.setInt(R.id.progressBar, "setProgress", progress);
            remoteViews.setViewVisibility(R.id.layoutHour, prefs.getBoolean(getKey(mAppWidgetId, "onlyDays"), false) ? View.GONE : View.VISIBLE);

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
                    prefs.edit().remove(entry.getKey()).apply();
                }
            }
        }
        super.onDeleted(context, appWidgetIds);
    }
}

