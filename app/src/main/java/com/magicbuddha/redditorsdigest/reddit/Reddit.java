package com.magicbuddha.redditorsdigest.reddit;

import android.util.Log;

import net.dean.jraw.RedditClient;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.http.oauth.Credentials;
import net.dean.jraw.http.oauth.OAuthException;
import net.dean.jraw.http.oauth.OAuthHelper;

/**
 * Created by Magic_Buddha on 12/24/2017.
 */

public class Reddit {
    private RedditClient reddit;

    private static Reddit redditInstance;

    private static final String TAG = Reddit.class.getCanonicalName();

    public static Reddit getInstance() {
        if (redditInstance == null) {
            synchronized (Reddit.class) {
                if (redditInstance == null) {
                    redditInstance = new Reddit();
                }
            }
        }

        return redditInstance;
    }

    public boolean login(String username, String password) {
        UserAgent userAgent = UserAgent.of("bot", "com.magiicbuddha.redditorsdigest", "v0.1", "RedditorsDigestBot");
        // Create our credentials
        Credentials credentials = Credentials.script("<username>", "<password>",
                "<client ID>", "<client secret>");

        //reddit.getOAuthHelper()

        try {
            new OAuthHelper(reddit).easyAuth(credentials);
        } catch (Exception e) {
            Log.e(TAG, "Failed to authenticate.");
            return false;
        }

        return false;
    }

}
