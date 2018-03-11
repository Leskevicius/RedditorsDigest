package com.magicbuddha.redditorsdigest.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;

/**
 * Implementation of App Widget functionality.
 */
public class SubmissionWidget extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        context.startService(SubmissionWidgetService.getRandomSubmissionActionIntent(context));
    }

    @Override
    public void onEnabled(Context context) {
        // no op
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

