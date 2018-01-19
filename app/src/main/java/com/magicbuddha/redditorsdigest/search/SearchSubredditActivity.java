package com.magicbuddha.redditorsdigest.search;

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
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.magicbuddha.redditorsdigest.R;
import com.magicbuddha.redditorsdigest.data.SubscriptionsContract;
import com.magicbuddha.redditorsdigest.reddit.Reddit;
import com.magicbuddha.redditorsdigest.reddit.SearchSubredditsTask;

import net.dean.jraw.models.Subreddit;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Magic_Buddha on 12/28/2017.
 */

public class SearchSubredditActivity extends AppCompatActivity implements SearchSubredditsTask.SearchCallback, SearchSubredditAdapter.SubredditAdapterListener {

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

    private SearchSubredditAdapter adapter;

    private ArrayList<String> subscriptionsChanged;

    public static final int REQUEST_CODE = 37;
    public static final int RESULT_NEED_UPDATE = -2;

    private static final String SUBSCRIPTIONS_CHANGED= "subscriptionsChanged";
    private static final String SEARCH_TEXT = "searchText";

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

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        adapter = new SearchSubredditAdapter(getApplicationContext(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);

        if (savedInstanceState != null) {
            subscriptionsChanged = savedInstanceState.getStringArrayList(SUBSCRIPTIONS_CHANGED);

            String searchText = savedInstanceState.getString(SEARCH_TEXT, null);

            if (!TextUtils.isEmpty(searchText)) {
                search(searchText);
            }
        } else {
            subscriptionsChanged = new ArrayList<>();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (subscriptionsChanged != null && subscriptionsChanged.size() > 0) {
            outState.putStringArrayList(SUBSCRIPTIONS_CHANGED, subscriptionsChanged);
        }

        if (!TextUtils.isEmpty(input.getText().toString())) {
            outState.putString(SEARCH_TEXT, input.getText().toString());
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onSearchComplete(List<Subreddit> subreddits) {
        if (subreddits != null) {
            Uri SubscriptionsUri = SubscriptionsContract.SubscriptionEntity.CONTENT_URI;

            Cursor result = getContentResolver().query(
                    SubscriptionsUri,
                    null,
                    null,
                    null,
                    null
            );

            List<String> subscriptions = new ArrayList<String>();
            result.moveToFirst();
            while (!result.isAfterLast()) {
                subscriptions.add(result.getString(result.getColumnIndex(SubscriptionsContract.SubscriptionEntity.SUBSCRIPTION_COLUMN))); //add the item
                result.moveToNext();
            }

            adapter.setData(subreddits, subscriptions);

            if (subreddits.size() == 0) {
                showNoResults(true);
            }
        }
        setLoading(false);
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
        } else {
            noResultsView.setVisibility(View.GONE);
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
                setResult(subscriptionsChanged.size() > 0 ? RESULT_NEED_UPDATE : RESULT_OK, returnIntent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSubscribed(Subreddit subreddit, boolean subscribed) {
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
