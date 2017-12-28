package com.magicbuddha.redditorsdigest.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.magicbuddha.redditorsdigest.R;
import com.magicbuddha.redditorsdigest.reddit.AuthenticateBotTask;

import net.dean.jraw.RedditClient;

import java.lang.ref.WeakReference;

public class HomeActivity extends AppCompatActivity implements AuthenticateBotTask.AuthenticateCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        new AuthenticateBotTask(new WeakReference<Context>(getApplicationContext()), this).execute();
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
        if (id == R.id.action_login) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onAuthenticated(RedditClient reddit) {
        if (reddit == null) {
            Log.w("ROKAS", "Reddit is null");
        } else {
            Log.w("ROKAS", "Reddit is NOT null");
        }
    }
}
