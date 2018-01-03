package com.magicbuddha.redditorsdigest.home;

import android.content.Intent;
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

import com.magicbuddha.redditorsdigest.R;
import com.magicbuddha.redditorsdigest.reddit.AuthenticateBotTask;
import com.magicbuddha.redditorsdigest.reddit.Reddit;
import com.magicbuddha.redditorsdigest.search.SearchSubredditActivity;

import net.dean.jraw.RedditClient;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity implements AuthenticateBotTask.AuthenticateCallback {

    @BindView(R.id.fragment_container)
    FrameLayout fragmentContainer;

    @BindView(R.id.home_layout)
    ConstraintLayout homeLayout;

    @BindView(R.id.home_progressbar)
    ProgressBar progressBar;

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
            NoSubscriptionsFragment fragment = NoSubscriptionsFragment.getInstance(null);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
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
            Log.w("ROKAS", "Reddit is null");
        } else {
            Log.w("ROKAS", "Reddit is NOT null");
            Reddit reddit = Reddit.getInstance();

            reddit.setRedditClient(redditClient);
        }

        setLoading(false);
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
}
