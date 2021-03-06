package com.magicbuddha.redditorsdigest.widget;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.RemoteViews;

import com.magicbuddha.redditorsdigest.R;
import com.magicbuddha.redditorsdigest.data.SubscriptionsContract;
import com.magicbuddha.redditorsdigest.home.HomeActivity;
import com.magicbuddha.redditorsdigest.reddit.AuthenticateBotTask;
import com.magicbuddha.redditorsdigest.reddit.Reddit;
import com.magicbuddha.redditorsdigest.reddit.SubredditProvider;
import com.magicbuddha.redditorsdigest.submissions.GetSubmissionDataTask;
import com.magicbuddha.redditorsdigest.submissions.SingleSubmissionActivity;

import net.dean.jraw.RedditClient;
import net.dean.jraw.http.NetworkAdapter;
import net.dean.jraw.http.OkHttpNetworkAdapter;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.models.PublicContribution;
import net.dean.jraw.models.Submission;
import net.dean.jraw.models.SubredditSort;
import net.dean.jraw.oauth.Credentials;
import net.dean.jraw.oauth.OAuthHelper;
import net.dean.jraw.tree.CommentNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class SubmissionWidgetService extends IntentService implements AuthenticateBotTask.AuthenticateCallback {

    private static final String ACTION_GET_RANDOM_SUBMISSION = "actionGetRandomSubmission";

    public SubmissionWidgetService() {
        super("SubmissionWidgetService");
    }

    public static Intent getRandomSubmissionActionIntent(Context context) {
        Intent intent = new Intent(context, SubmissionWidgetService.class);
        intent.setAction(ACTION_GET_RANDOM_SUBMISSION);
        return intent;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_GET_RANDOM_SUBMISSION.equals(action)) {
                if (redditApiInitialized()) {
                    handleActionRandomGetSubmission();
                }
            }
        }
    }

    private boolean redditApiInitialized() {
        Reddit reddit = Reddit.getInstance();
        if (reddit.initialized()) {
            return true;
        } else {
            initReddit();
            return false;
        }
    }

    private void initReddit() {
        Reddit reddit = Reddit.getInstance();
        UserAgent userAgent = new UserAgent(getString(R.string.user_agent));
        Credentials credentials = Credentials.userless(
                getString(R.string.client_id),
                getString(R.string.client_secret),
                UUID.randomUUID()
        );

        NetworkAdapter adapter = new OkHttpNetworkAdapter(userAgent);
        RedditClient redditClient = OAuthHelper.automatic(adapter, credentials);
        reddit.setRedditClient(redditClient);
        
        handleActionRandomGetSubmission();
    }

    private void handleActionRandomGetSubmission() {
        Cursor cursor = getContentResolver().query(
                SubscriptionsContract.SubscriptionEntity.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        List<String> subscriptions = new ArrayList<>();

        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                subscriptions.add(cursor.getString(cursor.getColumnIndex(SubscriptionsContract.SubscriptionEntity.SUBSCRIPTION_COLUMN))); //add the item
                cursor.moveToNext();
            }

            // now grab a random subscription
            Random r = new Random();
            int randomInt = r.nextInt(subscriptions.size());

            List<String> subscription = new ArrayList<>();
            subscription.add(subscriptions.get(randomInt));

            // init SubredditProvider
            SubredditProvider provider = SubredditProvider.getInstance();
            provider.init(subscription, SubredditSort.HOT, 10, 1);


            // Listener for grabbing the actual submission object.
            final GetSubmissionDataTask.SubmissionDataCallback submissionDataCallback = new GetSubmissionDataTask.SubmissionDataCallback() {
                @Override
                public void onComplete(Submission submission, List<CommentNode<PublicContribution<?>>> comments) {
                    updateAppWidget(submission);
                }
            };

            // Listener for grabbing submissionIds.
            final SubredditProvider.SubmissionListener submissionListener = new SubredditProvider.SubmissionListener() {
                @Override
                public void onNextSubmissions(List<String> submissionIds) {
                    // now grab a random submission
                    if (submissionIds.size() > 0) {
                        Random r = new Random();
                        int randomInt = r.nextInt(submissionIds.size());

                        new GetSubmissionDataTask(submissionDataCallback).execute(submissionIds.get(randomInt));
                    } else {
                        updateAppWidget(null);
                    }
                }
            };

            // initiate api call to grab the submission ids
            // this will later initiate an api call to get the actual submission.
            // see callbacks above. In production code I would hopefully use 3rd party library
            // chain these nicely (Looking at you rxjava)
            provider.getNextSubmissions(submissionListener);
        } else {
            updateAppWidget(null);
        }
        if (cursor != null) {
            cursor.close();
        }
    }

    private void updateAppWidget(@Nullable Submission submission) {
        AppWidgetManager manager = AppWidgetManager.getInstance(this);
        int[] widgetIds = manager.getAppWidgetIds(new ComponentName(this, SubmissionWidget.class));

        RemoteViews views = new RemoteViews(getPackageName(), R.layout.submission_widget);

        // populate the views with data
        if (submission != null) {
            views.setTextViewText(R.id.widget_subreddit, submission.getSubreddit());
            views.setTextViewText(R.id.widget_title, submission.getTitle());
            views.setTextViewText(R.id.widget_author, getString(R.string.author_prefix) + " " + submission.getAuthor());
            views.setViewVisibility(R.id.widget_author, View.VISIBLE);
            views.setViewVisibility(R.id.widget_title, View.VISIBLE);
            views.setViewVisibility(R.id.widget_progressbar, View.GONE);

            Intent backIntent = new Intent(this, HomeActivity.class);
            backIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // set up a way to view the post
            Intent submissionIntent = new Intent(this, SingleSubmissionActivity.class);
            submissionIntent.putExtra(SingleSubmissionActivity.SUBMISSION_ID, submission.getId());
            Intent[] intents = {backIntent, submissionIntent};
            PendingIntent pendingIntent = PendingIntent.getActivities(this, 0, intents, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.widget_content, pendingIntent);

            // set up a way to get a new random post
            Intent randomSubmissionActionIntent = getRandomSubmissionActionIntent(this);
            PendingIntent randomSubmissionPendingIntent = PendingIntent.getService(this, 0, randomSubmissionActionIntent, 0);
            views.setOnClickPendingIntent(R.id.widget_bar, randomSubmissionPendingIntent);

            manager.updateAppWidget(widgetIds, views);

        } else {
            views.setTextViewText(R.id.widget_subreddit, getString(R.string.app_name));
            views.setTextViewText(R.id.widget_title, getString(R.string.widget_default_title));
            views.setViewVisibility(R.id.widget_title, View.VISIBLE);
            views.setViewVisibility(R.id.widget_author, View.GONE);
            views.setViewVisibility(R.id.widget_progressbar, View.GONE);
        }
    }

    @Override
    public void onAuthenticated(RedditClient reddit) {
        if (reddit != null) {
            handleActionRandomGetSubmission();
        }
    }
}
