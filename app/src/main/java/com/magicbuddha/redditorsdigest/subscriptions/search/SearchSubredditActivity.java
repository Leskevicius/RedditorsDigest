package com.magicbuddha.redditorsdigest.subscriptions.search;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.magicbuddha.redditorsdigest.AnalyticsApplication;
import com.magicbuddha.redditorsdigest.R;
import com.magicbuddha.redditorsdigest.Utils;
import com.magicbuddha.redditorsdigest.data.SubscriptionsContract;
import com.magicbuddha.redditorsdigest.models.SubredditData;
import com.magicbuddha.redditorsdigest.reddit.SearchSubredditsTask;

import net.dean.jraw.models.Subreddit;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Magic_Buddha on 12/28/2017.
 */

public class SearchSubredditActivity extends AppCompatActivity implements SearchSubredditsTask.SearchCallback, SearchSubredditAdapter.SubredditAdapterListener {

    private static final String TAG = SearchSubredditActivity.class.getSimpleName();
    @BindView(R.id.search_recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.input_layout_search)
    TextInputLayout inputLayout;

    @BindView(R.id.input_search)
    EditText input;

    @BindView(R.id.search_button)
    ImageView searchButton;

    @BindView(R.id.search_progressbar)
    ProgressBar progressBar;

    @BindView(R.id.search_no_results)
    TextView noResultsView;

    @BindView(R.id.search_no_internet)
    TextView searchNoInternet;

    @BindView(R.id.ad_view)
    AdView adView;

    private SearchSubredditAdapter adapter;

    private List<String> subscriptionsChanged;
    private List<SubredditData> subreddits;
    private List<String> subscriptions;
    private boolean searchClicked;
    public static final int REQUEST_CODE = 37;
    public static final int RESULT_NEED_UPDATE = -2;

    private static final String SUBSCRIPTIONS_CHANGED = "subscriptionsChanged";
    private static final String SUBREDDITS = "subreddits";
    private static final String SUBSCRIPTIONS = "subscriptions";
    private static final String SEARCH_TEXT = "searchText";
    private static final String SEARCH_CLICKED = "searchClicked";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_subreddit);
        ButterKnife.bind(this);


        // tool bar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (!Utils.isNetworkAvailable(this)) {
            searchNoInternet.setVisibility(View.VISIBLE);
            return;
        }

        adView.loadAd(new AdRequest.Builder().build());

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchClicked = true;
                search(input.getText().toString());
            }
        });

        input.setOnEditorActionListener(new EditText.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    searchButton.performClick();
                    return true;
                }
                return false;
            }
        });

        // recycler view
        adapter = new SearchSubredditAdapter(this,this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);

        if (savedInstanceState != null) {
            subscriptionsChanged = savedInstanceState.containsKey(SUBSCRIPTIONS_CHANGED) ? savedInstanceState.getStringArrayList(SUBSCRIPTIONS_CHANGED) : new ArrayList<String>();
            if (savedInstanceState.containsKey(SUBREDDITS)) {
                subreddits = savedInstanceState.getParcelableArrayList(SUBREDDITS);
            } else {
                subreddits = new ArrayList<>();
            }
            subscriptions = savedInstanceState.containsKey(SUBSCRIPTIONS) ? savedInstanceState.getStringArrayList(SUBSCRIPTIONS) : new ArrayList<String>();
            searchClicked = savedInstanceState.getBoolean(SEARCH_CLICKED);

            if (subreddits.isEmpty() && searchClicked) {
                showNoResults(true);
            } else if (!subreddits.isEmpty()) {
                adapter.setData(subreddits, subscriptions);
            }
        } else {
            subscriptionsChanged = new ArrayList<>();
            subreddits = new ArrayList<>();
        }

        AnalyticsApplication application = (AnalyticsApplication) getApplication();

        Tracker tracker = application.getDefaultTracker();
        tracker.setScreenName(TAG);
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (subscriptionsChanged != null && subscriptionsChanged.size() > 0) {
            outState.putStringArrayList(SUBSCRIPTIONS_CHANGED, new ArrayList<>(subscriptionsChanged));
        }

        if (subreddits != null && subreddits.size() > 0) {
            outState.putParcelableArrayList(SUBREDDITS, new ArrayList<>(subreddits));
        }

        if (subscriptions != null && subscriptions.size() > 0) {
            outState.putStringArrayList(SUBSCRIPTIONS, new ArrayList<>(subscriptions));
        }

        if (!TextUtils.isEmpty(input.getText().toString())) {
            outState.putString(SEARCH_TEXT, input.getText().toString());
        }

        outState.putBoolean(SEARCH_CLICKED, searchClicked);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onSearchComplete(List<SubredditData> subreddits) {
        setLoading(false);

        if (subreddits != null) {
            this.subreddits = subreddits;
            Uri SubscriptionsUri = SubscriptionsContract.SubscriptionEntity.CONTENT_URI;

            Cursor result = getContentResolver().query(
                    SubscriptionsUri,
                    null,
                    null,
                    null,
                    null
            );

            subscriptions = new ArrayList<>();
            result.moveToFirst();
            while (!result.isAfterLast()) {
                subscriptions.add(result.getString(result.getColumnIndex(SubscriptionsContract.SubscriptionEntity.SUBSCRIPTION_COLUMN))); //add the item
                result.moveToNext();
            }

            result.close();

            adapter.setData(subreddits, subscriptions);

            if (subreddits.size() == 0) {
                showNoResults(true);
            }
        } else {
            showNoResults(true);
        }
    }

    private void setLoading(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void showNoResults(boolean show) {
        if (show) {
            noResultsView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
        } else {
            noResultsView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void search(String text) {
        setLoading(true);
        new SearchSubredditsTask(SearchSubredditActivity.this, false).execute(text);

        // dismiss keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
        }

        // dismiss no results
        showNoResults(false);
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(subscriptionsChanged.size() > 0 ? RESULT_NEED_UPDATE : RESULT_OK, returnIntent);
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent returnIntent = new Intent();
                if (subscriptionsChanged != null) {
                    setResult(subscriptionsChanged.size() > 0 ? RESULT_NEED_UPDATE : RESULT_OK, returnIntent);
                }
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSubscribed(SubredditData subreddit, boolean subscribed) {
        if (subscribed) {
            ContentValues cv = new ContentValues();
            cv.put(SubscriptionsContract.SubscriptionEntity.SUBSCRIPTION_COLUMN, subreddit.getName());

            getContentResolver().insert(
                    SubscriptionsContract.SubscriptionEntity.CONTENT_URI, cv);
        } else {
            Uri unsubscribeUri = SubscriptionsContract.SubscriptionEntity.CONTENT_URI.buildUpon()
                    .appendPath(subreddit.getName())
                    .build();

            getContentResolver().delete(unsubscribeUri, null, null);
        }

        if (subscriptionsChanged.contains(subreddit.getName())) {
            subscriptionsChanged.remove(subreddit.getName());
        } else {
            subscriptionsChanged.add(subreddit.getName());
        }
    }
}
