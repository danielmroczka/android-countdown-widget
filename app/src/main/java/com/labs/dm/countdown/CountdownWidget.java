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

import com.labs.dm.com.countdown.R;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

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

            int hour = prefs.getInt("widget." + mAppWidgetId + ".hour", 0);
            int minute = prefs.getInt("widget." + mAppWidgetId + ".minute", 0);

            Calendar now = Calendar.getInstance();
            Calendar c = Calendar.getInstance();

            c.set(year, month, day, hour, minute, 0);
            long diff = c.getTimeInMillis() - now.getTimeInMillis();
            long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
            diff = diff - (days * 86400000L);
            long hours = TimeUnit.HOURS.convert(diff, TimeUnit.MILLISECONDS);
            CharSequence widgetText = (String.valueOf(days));

            remoteViews.setTextViewText(R.id.display, widgetText);

            if (hours > 0) {
                remoteViews.setTextViewText(R.id.display2, String.valueOf(hours));
                remoteViews.setTextViewText(R.id.lblhours, "hours");
            } else {
                remoteViews.setTextViewText(R.id.display2, "");
                remoteViews.setTextViewText(R.id.lblhours, "");
            }

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
            prefs.edit().remove("widget." + mAppWidgetId + ".day").commit();
            prefs.edit().remove("widget." + mAppWidgetId + ".month").commit();
            prefs.edit().remove("widget." + mAppWidgetId + ".year").commit();
            prefs.edit().remove("widget." + mAppWidgetId + ".hour").commit();
            prefs.edit().remove("widget." + mAppWidgetId + ".minute").commit();
        }
        super.onDeleted(context, appWidgetIds);
    }
}

