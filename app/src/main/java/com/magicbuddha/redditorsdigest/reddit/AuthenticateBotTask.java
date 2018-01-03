package com.magicbuddha.redditorsdigest.reddit;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.magicbuddha.redditorsdigest.R;

import net.dean.jraw.RedditClient;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.http.oauth.Credentials;
import net.dean.jraw.http.oauth.OAuthData;
import net.dean.jraw.http.oauth.OAuthException;

import java.lang.ref.WeakReference;
import java.util.UUID;

/**
 * Created by Magic_Buddha on 12/26/2017.
 */

public class AuthenticateBotTask extends AsyncTask<Void, Void, RedditClient> {

    private static final String TAG = AuthenticateBotTask.class.getCanonicalName();

    // used to retrieve bot info
    private WeakReference<Context> weakContext;
    private AuthenticateCallback callback;

    public AuthenticateBotTask(WeakReference<Context> weakContext, @NonNull AuthenticateCallback callback) {
        this.weakContext = weakContext;
        this.callback = callback;
    }

    @Override
    protected RedditClient doInBackground(Void... voids) {
        RedditClient reddit = null;

        Context context = weakContext.get();
        if (context != null) {
            UserAgent userAgent = UserAgent.of(
                    context.getString(R.string.bot_platform),
                    context.getString(R.string.bot_appId),
                    context.getString(R.string.bot_version),
                    context.getString(R.string.bot_reddit_username));

            reddit = new RedditClient(userAgent);

            Credentials credentials = Credentials.userless(
                    context.getString(R.string.client_id),
                    context.getString(R.string.client_secret),
                    UUID.randomUUID()
            );

            try {
                OAuthData authData = reddit.getOAuthHelper().easyAuth(credentials);

                reddit.authenticate(authData);
            } catch (OAuthException e) {
                Log.w(TAG, e);
                reddit = null;
            }

        } else {
            Log.w(TAG, "Failed authenticating the bot. Context was null.");
        }

        return reddit;
    }

    @Override
    protected void onPostExecute(RedditClient redditClient) {
        super.onPostExecute(redditClient);
        callback.onAuthenticated(redditClient);
    }

    public interface AuthenticateCallback {
        /**
         * Called then {@link RedditClient} is authenticated.
         *
         * @param reddit {@link RedditClient}. Null if authentication failed.
         */
        void onAuthenticated(RedditClient reddit);
    }
}
