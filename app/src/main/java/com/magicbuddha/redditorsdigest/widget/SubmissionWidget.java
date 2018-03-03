package com.magicbuddha.redditorsdigest.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.magicbuddha.redditorsdigest.R;
import com.magicbuddha.redditorsdigest.data.SubscriptionsContract;
import com.magicbuddha.redditorsdigest.reddit.SubredditProvider;
import com.magicbuddha.redditorsdigest.submissions.GetSubmissionDataTask;

import net.dean.jraw.models.PublicContribution;
import net.dean.jraw.models.Submission;
import net.dean.jraw.models.SubredditSort;
import net.dean.jraw.tree.CommentNode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

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

