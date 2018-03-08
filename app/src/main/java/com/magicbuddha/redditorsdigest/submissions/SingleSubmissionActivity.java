package com.magicbuddha.redditorsdigest.submissions;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.FrameLayout;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.magicbuddha.redditorsdigest.AnalyticsApplication;
import com.magicbuddha.redditorsdigest.R;
import com.magicbuddha.redditorsdigest.reddit.AuthenticateBotTask;
import com.magicbuddha.redditorsdigest.reddit.Reddit;
import com.magicbuddha.redditorsdigest.reddit.SubredditProvider;

import net.dean.jraw.RedditClient;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SingleSubmissionActivity extends AppCompatActivity implements AuthenticateBotTask.AuthenticateCallback {

    private static final String TAG = SingleSubmissionActivity.class.getSimpleName();

    public static final String SUBMISSION_ID = "submissionId";

    private String submissionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_submission);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        submissionId = getIntent().getStringExtra(SUBMISSION_ID);

        if (Reddit.getInstance().initialized()) {
            showFragment();
        } else {
            new AuthenticateBotTask(new WeakReference<Context>(this), this);
        }

        AnalyticsApplication application = (AnalyticsApplication) getApplication();

        Tracker tracker = application.getDefaultTracker();
        tracker.setScreenName(TAG);
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    private void showFragment() {
        Fragment fragment = SubmissionFragment.getInstance(submissionId);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    @Override
    public void onAuthenticated(RedditClient reddit) {
        if (reddit != null) {
            showFragment();
        }
    }
}
