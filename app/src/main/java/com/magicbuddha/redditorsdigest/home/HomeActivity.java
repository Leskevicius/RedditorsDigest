package com.magicbuddha.redditorsdigest.home;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.magicbuddha.redditorsdigest.R;
import com.magicbuddha.redditorsdigest.data.SubscriptionsContract;
import com.magicbuddha.redditorsdigest.reddit.AuthenticateBotTask;
import com.magicbuddha.redditorsdigest.reddit.GetSubredditsTask;
import com.magicbuddha.redditorsdigest.reddit.Reddit;
import com.magicbuddha.redditorsdigest.search.SearchSubredditActivity;

import net.dean.jraw.RedditClient;
import net.dean.jraw.models.Submission;
import net.dean.jraw.models.Subreddit;
import net.dean.jraw.pagination.DefaultPaginator;
import net.dean.jraw.references.SubredditReference;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity implements AuthenticateBotTask.AuthenticateCallback, GetSubredditsTask.SubredditsCallback {

    private static final String TAG = HomeActivity.class.getCanonicalName();

    @BindView(R.id.fragment_container)
    FrameLayout fragmentContainer;

    @BindView(R.id.home_layout)
    ConstraintLayout homeLayout;

    @BindView(R.id.home_progressbar)
    ProgressBar progressBar;

    private List<Subreddit> subreddits;
    private List<String> subscriptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        if (savedInstanceState == null) {
            new AuthenticateBotTask(new WeakReference<>(getApplicationContext()), this).execute();
            setLoading(true);
        } else {
            fragmentContainer.setVisibility(View.VISIBLE);
        }
    }

    private void showContent() {
        subscriptions = getSubscriptions();
        if (subscriptions.size() == 0) {
            NoSubscriptionsFragment fragment = NoSubscriptionsFragment.getInstance(null);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        } else {
            new GetSubredditsTask(this).execute(subscriptions.toArray(new String[0]));
            setLoading(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            startSearchActivity();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Start {@link SearchSubredditActivity} activity.
     */
    public void startSearchActivity() {
        Intent intent = new Intent(this, SearchSubredditActivity.class);
        startActivityForResult(intent, SearchSubredditActivity.REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SearchSubredditActivity.REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // no new subscriptions, continue with business as usual
                Snackbar.make(homeLayout, "No updated needed.", Snackbar.LENGTH_SHORT).show();
            } else if (resultCode == SearchSubredditActivity.RESULT_NEED_UPDATE) {
                // subscriptions change, update the adapter for submissions
                Snackbar.make(homeLayout, "Updated needed.", Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onAuthenticated(RedditClient redditClient) {
        if (redditClient == null) {
            Log.w(TAG, "Reddit is null");
            Toast.makeText(this, "Could not authenticate at this time. Please try later.", Toast.LENGTH_SHORT).show();
            setLoading(false);
            return;
        }

        Reddit reddit = Reddit.getInstance();
        reddit.setRedditClient(redditClient);

        setLoading(false);

        showContent();
    }

    private void setLoading(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            fragmentContainer.setVisibility(View.INVISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
            fragmentContainer.setVisibility(View.VISIBLE);
        }
    }

    private List<String> getSubscriptions() {
        Cursor result = getContentResolver().query(
                SubscriptionsContract.SubscriptionEntity.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        List<String> subscriptions = new ArrayList<>();
        result.moveToFirst();
        while (!result.isAfterLast()) {
            subscriptions.add(result.getString(result.getColumnIndex(SubscriptionsContract.SubscriptionEntity.SUBSCRIPTION_COLUMN))); //add the item
            result.moveToNext();
        }

        return subscriptions;
    }

    @Override
    public void onComplete(List<Subreddit> subreddits) {
        Log.w("ROKASSS", subreddits.get(0).getFullName());
        setLoading(false);

//        subreddits.get(0).toReference(Reddit.getInstance().getRedditClient()).posts().build().accumulateMerged(DefaultPaginator.RECOMMENDED_MAX_LIMIT).get(0).getId();
    }
}
