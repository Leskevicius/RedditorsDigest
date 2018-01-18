package com.magicbuddha.redditorsdigest.search;

import android.content.Context;
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
import android.view.View;
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

public class SearchSubredditActivity extends AppCompatActivity implements SearchSubredditsTask.SearchCallback {

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

    public static final int REQUEST_CODE = 37;
    public static final int RESULT_NEED_UPDATE = -2;

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
                String searchString = input.getText().toString();

                if (!TextUtils.isEmpty(searchString)) {
                    setLoading(true);
                    new SearchSubredditsTask(new WeakReference<>(getApplicationContext()),
                            SearchSubredditActivity.this, false).execute(searchString);

                    // dismiss keyboard
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
                    }

                    // dismiss no results
                    showNoResults(false);
                }
            }
        });

        // recycler view
        adapter = new SearchSubredditAdapter(getApplicationContext(), null);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);

//        Intent returnIntent = new Intent();
//        setResult(RESULT_NEED_UPDATE, returnIntent);
//        finish();

    }

    @Override
    public void onSearchComplete(List<Subreddit> subreddits) {
        if (subreddits != null) {
//            Log.w("ROKAS", subreddits.toString());

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
            while(!result.isAfterLast()) {
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
        } else {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void showNoResults(boolean show) {
        if (show) {
            noResultsView.setVisibility(View.VISIBLE);
        } else {
            noResultsView.setVisibility(View.GONE);
        }
    }
}
