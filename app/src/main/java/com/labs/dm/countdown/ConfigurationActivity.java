package com.labs.dm.countdown;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.labs.dm.com.countdown.R;

import static android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID;
import static android.appwidget.AppWidgetManager.INVALID_APPWIDGET_ID;

/**
 * Created by daniel on 2016-01-24.
 */
public class ConfigurationActivity extends Activity {

    private int mAppWidgetId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);
        setResult(RESULT_CANCELED);
        initListViews();
    }

    public void initListViews() {
        DatePicker date = (DatePicker) findViewById(R.id.datePicker);
        date.setMinDate(System.currentTimeMillis() - 1000);

        final TimePicker time = (TimePicker) findViewById(R.id.timePicker);

        CheckBox checkBox = (CheckBox) findViewById(R.id.checkBoxTime);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                time.setEnabled(isChecked);
            }
        });
        checkBox.setChecked(false);

        Button okButton = (Button) findViewById(R.id.okButton);
        okButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                handleOkButton();
            }
        });
    }

    private void handleOkButton() {
        showAppWidget();
    }

    private void showAppWidget() {

        mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(EXTRA_APPWIDGET_ID, INVALID_APPWIDGET_ID);

            if (mAppWidgetId == INVALID_APPWIDGET_ID) {
                Log.w("WidgetAdd", "Cannot continue. Widget ID incorrect");
                return;
            }

            DatePicker date = (DatePicker) findViewById(R.id.datePicker);
            TimePicker time = (TimePicker) findViewById(R.id.timePicker);
            CheckBox checkBox = (CheckBox) findViewById(R.id.checkBoxTime);

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            prefs.edit().putInt("widget." + mAppWidgetId + ".day", date.getDayOfMonth()).apply();
            prefs.edit().putInt("widget." + mAppWidgetId + ".month", date.getMonth()).apply();
            prefs.edit().putInt("widget." + mAppWidgetId + ".year", date.getYear()).apply();

            if (checkBox.isChecked()) {
                time.clearFocus();
                prefs.edit().putInt("widget." + mAppWidgetId + ".hour", time.getCurrentHour()).apply();
                prefs.edit().putInt("widget." + mAppWidgetId + ".minute", time.getCurrentMinute()).apply();
            }

            Intent startService = new Intent(ConfigurationActivity.this, CountdownWidget.class);
            startService.putExtra(EXTRA_APPWIDGET_ID, mAppWidgetId);
            startService.setAction("FROM CONFIGURATION ACTIVITY");
            setResult(RESULT_OK, startService);
            startService(startService);
            finish();
        }

        if (mAppWidgetId == INVALID_APPWIDGET_ID) {
            Log.e("Invalid app widget id", "Invalid app widget id");
            finish();
        }

    }
}