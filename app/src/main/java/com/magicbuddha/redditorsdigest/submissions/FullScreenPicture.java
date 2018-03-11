package com.magicbuddha.redditorsdigest.submissions;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.magicbuddha.redditorsdigest.AnalyticsApplication;
import com.magicbuddha.redditorsdigest.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FullScreenPicture extends AppCompatActivity {

    private static final String TAG = FullScreenPicture.class.getSimpleName();

    public static final String URL_EXTRA = "URL_EXTRA";
    @BindView(R.id.full_screen_image)
    ImageView fullScreenImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_picture);
        ButterKnife.bind(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        String url = intent.getStringExtra(URL_EXTRA);

        Glide.with(this)
                .load(url)
                .error(R.drawable.ic_cannot_load_picture)
                .into(fullScreenImage);

        AnalyticsApplication application = (AnalyticsApplication) getApplication();

        Tracker tracker = application.getDefaultTracker();
        tracker.setScreenName(TAG);
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
