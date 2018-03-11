package com.magicbuddha.redditorsdigest.reddit;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.magicbuddha.redditorsdigest.R;

import net.dean.jraw.RedditClient;
import net.dean.jraw.http.NetworkAdapter;
import net.dean.jraw.http.OkHttpNetworkAdapter;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.oauth.Credentials;
import net.dean.jraw.oauth.OAuthHelper;

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
            UserAgent userAgent = new UserAgent(context.getString(R.string.user_agent));
            Credentials credentials = Credentials.userless(
                    context.getString(R.string.client_id),
                    context.getString(R.string.client_secret),
                    UUID.randomUUID()
            );

            NetworkAdapter adapter = new OkHttpNetworkAdapter(userAgent);
            reddit = OAuthHelper.automatic(adapter, credentials);
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
