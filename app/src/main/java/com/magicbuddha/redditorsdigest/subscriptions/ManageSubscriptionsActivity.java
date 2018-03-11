package com.magicbuddha.redditorsdigest.subscriptions;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.magicbuddha.redditorsdigest.AnalyticsApplication;
import com.magicbuddha.redditorsdigest.R;
import com.magicbuddha.redditorsdigest.data.SubscriptionsContract;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ManageSubscriptionsActivity extends AppCompatActivity implements ManageSubscriptionsAdapter.ManageSubscriptionsAdapterListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = ManageSubscriptionsActivity.class.getSimpleName();

    public static final int REQUEST_CODE = 38;
    public static final int RESULT_NEED_UPDATE = -2;
    private static final int LOADER_ID = 0x02;
    private static final String SUBSCRIPTIONS = "subscriptions";

    @BindView(R.id.manage_recycler_view)
    RecyclerView recyclerView;

    private ManageSubscriptionsAdapter adapter;
    private List<String> subscriptions = new ArrayList<>();
    private boolean changed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_subscriptions);
        ButterKnife.bind(this);

        // tool bar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        adapter = new ManageSubscriptionsAdapter(this);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(SUBSCRIPTIONS)) {
                subscriptions = savedInstanceState.getStringArrayList(SUBSCRIPTIONS);
            }
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);

        getSupportLoaderManager().initLoader(LOADER_ID, null, this);

        AnalyticsApplication application = (AnalyticsApplication) getApplication();

        Tracker tracker = application.getDefaultTracker();
        tracker.setScreenName(TAG);
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public void onUnsubscribed(String subreddit) {
        adapter.removeItem(subreddit);
        // remove from content provider
        Uri unsubscribeUri = SubscriptionsContract.SubscriptionEntity.CONTENT_URI.buildUpon()
                .appendPath(subreddit)
                .build();

        getContentResolver().delete(unsubscribeUri, null, null);

        changed = true;
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(changed ? RESULT_NEED_UPDATE : RESULT_OK, returnIntent);
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent returnIntent = new Intent();
                setResult(changed ? RESULT_NEED_UPDATE : RESULT_OK, returnIntent);                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
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

        adapter.setData(subscriptions);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // no op
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (subscriptions.size() > 0) {
            outState.putStringArrayList(SUBSCRIPTIONS, new ArrayList<>(subscriptions));
        }
    }
}
