package com.aren.arenweatherlogger

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import com.aren.arenweatherlogger.Constants.Companion.PREFS_FILENAME
import com.aren.arenweatherlogger.Constants.Companion.PREF_CITY_NAME
import com.aren.arenweatherlogger.Constants.Companion.PREF_TEMPERATURE

import java.util.Random

class WidgetHelper : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {

        // Get all ids
        val thisWidget = ComponentName(context,
                WidgetHelper::class.java)
        val allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)
        for (widgetId in allWidgetIds) {
            var prefs = context .getSharedPreferences(PREFS_FILENAME, 0)
            var cityTemperature: String? = prefs?.getString(PREF_TEMPERATURE, "")
            var cityName: String? = prefs?.getString(PREF_CITY_NAME, "")
            val remoteViews = RemoteViews(context.packageName,
                    R.layout.widget_layout)
            remoteViews.setTextViewText(R.id.tv_city, cityName)
            remoteViews.setTextViewText(R.id.tv_temperature, cityTemperature + " °C")

            // Register an onClickListener
//            val intent = Intent(context, WidgetHelper::class.java)
//
//            intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
//            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)

//            val pendingIntent = PendingIntent.getBroadcast(context,
//                    0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
//            remoteViews.setOnClickPendingIntent(R.id.update, pendingIntent)
            appWidgetManager.updateAppWidget(widgetId, remoteViews)
        }
    }

    companion object {

        private val ACTION_CLICK = "ACTION_CLICK"
    }
}