package com.magicbuddha.redditorsdigest.home;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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
import com.magicbuddha.redditorsdigest.reddit.GetSubmissionIdsTask;
import com.magicbuddha.redditorsdigest.reddit.Reddit;
import com.magicbuddha.redditorsdigest.reddit.SubredditProvider;
import com.magicbuddha.redditorsdigest.search.SearchSubredditActivity;

import net.dean.jraw.RedditClient;
import net.dean.jraw.models.SubredditSort;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity implements AuthenticateBotTask.AuthenticateCallback, SubredditProvider.SubmissionListener, LoaderManager.LoaderCallbacks<Cursor>,SubmissionPagerAdapter.SubmissionPagerAdapterListener {

    private static final String TAG = HomeActivity.class.getCanonicalName();
    private static final String SUBMISSIONS = "submissions";
    private static final String SUBSCRIPTIONS = "subscriptions";
    private static final int LOADER_ID = 0x01;

    @BindView(R.id.fragment_container)
    FrameLayout fragmentContainer;

    @BindView(R.id.home_layout)
    ConstraintLayout homeLayout;

    @BindView(R.id.home_progressbar)
    ProgressBar progressBar;

    @BindView(R.id.fragment_viewpager)
    ViewPager viewPager;
    private List<String> subscriptions = new ArrayList<>();
    private List<String> submissions = new ArrayList<>();

    private SubmissionPagerAdapter adapter;

    private boolean init = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        adapter = new SubmissionPagerAdapter(getSupportFragmentManager(), this);
        viewPager.setAdapter(adapter);

        if (savedInstanceState == null) {
            init = true;
            new AuthenticateBotTask(new WeakReference<>(getApplicationContext()), this).execute();
            setLoading(true);
        } else {
            if (savedInstanceState.containsKey(SUBSCRIPTIONS)) {
                subscriptions = savedInstanceState.getStringArrayList(SUBSCRIPTIONS);
            }

            if (savedInstanceState.containsKey(SUBMISSIONS)) {
                submissions = savedInstanceState.getStringArrayList(SUBMISSIONS);
            }

            if (subscriptions.size() > 0) {
                if (submissions.size() > 0) {
                    adapter.setData(submissions);
                    showContent();
                } else {
                    nextPage();
                }
            } else {
                showNoSubscriptions();
            }
        }
    }

    private void showNoSubscriptions() {
        NoSubscriptionsFragment fragment = NoSubscriptionsFragment.getInstance(null);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();

        setLoading(false);
        showFragment();
    }

    private void showContent() {
        showPager();
    }

    private void nextPage() {
        SubredditProvider provider = SubredditProvider.getInstance();
        provider.getNextSubmissions(this);
    }

    @Override
    public void onNextSubmissions(List<String> submissionIds) {
        adapter.addData(submissionIds);
        submissions.addAll(submissionIds);
        showPager();
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
                getSupportLoaderManager().initLoader(LOADER_ID, null, this);
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

        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }

    private void initSubredditProvider() {
        SubredditProvider provider = SubredditProvider.getInstance();
        provider.init(subscriptions, SubredditSort.HOT, 1, 1);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                SubscriptionsContract.SubscriptionEntity.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        data.moveToFirst();
        while (!data.isAfterLast()) {
            subscriptions.add(data.getString(data.getColumnIndex(SubscriptionsContract.SubscriptionEntity.SUBSCRIPTION_COLUMN))); //add the item
            data.moveToNext();
        }

        if (subscriptions.size() > 0) {
            initSubredditProvider();
            if (init) {
                setLoading(true);
                nextPage();
            } else {
                showContent();
            }
        } else {
            showNoSubscriptions();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

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
        progressBar.setVisibility(View.GONE);
    }

    private void showFragment() {
        viewPager.setVisibility(View.INVISIBLE);
        fragmentContainer.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList(SUBSCRIPTIONS, new ArrayList<>(subscriptions));
        outState.putStringArrayList(SUBMISSIONS, new ArrayList<>(submissions));
    }

    @Override
    public void requestSubmissions() {
        nextPage();
    }
}
