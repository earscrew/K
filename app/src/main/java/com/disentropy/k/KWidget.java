package com.disentropy.k;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class KWidget extends AppWidgetProvider {
    private static final String BUTTON_CLICKED = "kWidgetButtonClicked";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.kwidget);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean widgetState = prefs.getBoolean("widgetIsActive", false);

        if (widgetState) {
            views.setImageViewResource(R.id.imageButton, R.drawable.k_button_active);
        } else {
            views.setImageViewResource(R.id.imageButton, R.drawable.k_button_inactive);
        }
        ComponentName thisWidget = new ComponentName(context, KWidget.class);
        AppWidgetManager appWidgetMan = AppWidgetManager.getInstance(context);
        appWidgetMan.updateAppWidget(thisWidget, views);

        // There may be multiple widgets active, so update all of them
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (BUTTON_CLICKED.equals(intent.getAction())) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            boolean widgetState = prefs.getBoolean("widgetIsActive", false);
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.kwidget);
            PackageManager pm = context.getPackageManager();
            ComponentName compName = new ComponentName(context, SMSReceiver.class);

            if (widgetState) {
                views.setImageViewResource(R.id.imageButton, R.drawable.k_button_inactive);
                pm.setComponentEnabledSetting(compName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
                editor.putBoolean("widgetIsActive", false);
            } else {
                views.setImageViewResource(R.id.imageButton, R.drawable.k_button_active);
                pm.setComponentEnabledSetting(compName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
                editor.putBoolean("widgetIsActive", true);
            }
            editor.apply();

            ComponentName thisWidget = new ComponentName(context, KWidget.class);
            AppWidgetManager appWidgetMan = AppWidgetManager.getInstance(context);
            appWidgetMan.updateAppWidget(thisWidget, views);
        }
    }

    public void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.kwidget);
        views.setOnClickPendingIntent(R.id.imageButton, getPendingSelfIntent(context, BUTTON_CLICKED));
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    // Action is important to catch the view which is clicked
    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}

