package com.labs.dm.countdown;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import countdown.labs.dm.com.countdown.R;

/**
 * Implementation of App Widget functionality.
 */
public class CountdownWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        ComponentName thisWidget = new ComponentName(context, CountdownWidget.class);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        for (int mAppWidgetId : allWidgetIds) {

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.countdown_widget);

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            int day = prefs.getInt("widget." + mAppWidgetId + ".day", 0);
            int month = prefs.getInt("widget." + mAppWidgetId + ".month", 0);
            int year = prefs.getInt("widget." + mAppWidgetId + ".year", 0);

            Date now = new Date();
            Calendar c = Calendar.getInstance();

            c.set(year, month, day);
            long diff = c.getTimeInMillis() - now.getTime();
            long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
            CharSequence widgetText = (String.valueOf(days));
            remoteViews.setTextViewText(R.id.display, widgetText);

            Intent intent = new Intent(context, CountdownWidget.class);

            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                    0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.display, pendingIntent);
            appWidgetManager.updateAppWidget(mAppWidgetId, remoteViews);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        for (int mAppWidgetId : appWidgetIds) {
            prefs.edit().remove("widget." + mAppWidgetId + ".day").commit();
            prefs.edit().remove("widget." + mAppWidgetId + ".month").commit();
            prefs.edit().remove("widget." + mAppWidgetId + ".year").commit();
        }
        super.onDeleted(context, appWidgetIds);
    }
}

