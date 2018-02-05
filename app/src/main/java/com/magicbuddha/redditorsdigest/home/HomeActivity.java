package com.magicbuddha.redditorsdigest.home;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
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
import com.magicbuddha.redditorsdigest.submissions.PictureSubmissionFragment;

import net.dean.jraw.RedditClient;

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

    @BindView(R.id.fragment_viewpager)
    ViewPager viewPager;

    private List<String> subscriptions;
    private SubmissionPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        adapter = new SubmissionPagerAdapter(getSupportFragmentManager());

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
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    NoSubscriptionsFragment fragment = NoSubscriptionsFragment.getInstance(null);

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, fragment)
                            .addToBackStack(null)
                            .commit();

                    setLoading(false);
                    showFragment();
                }
            });

        } else {
            new GetSubredditsTask(this).execute(subscriptions.toArray(new String[0]));
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
                setLoading(true);
                showContent();
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

        showContent();
    }

    private void setLoading(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            fragmentContainer.setVisibility(View.INVISIBLE);
            viewPager.setVisibility(View.INVISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void showPager() {
        fragmentContainer.setVisibility(View.INVISIBLE);
        viewPager.setVisibility(View.VISIBLE);
    }

    private void showFragment() {
        viewPager.setVisibility(View.INVISIBLE);
        fragmentContainer.setVisibility(View.VISIBLE);
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
    public void onComplete(List<List<String>> submissionsBySubredditList) {

        PictureSubmissionFragment ps = PictureSubmissionFragment.getInstance(submissionsBySubredditList.get(0).get(5));

        adapter.setData(submissionsBySubredditList.get(0));
        viewPager.setAdapter(adapter);

        setLoading(false);
        showPager();
    }
}
