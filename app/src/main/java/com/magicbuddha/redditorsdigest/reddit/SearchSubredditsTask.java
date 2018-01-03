package com.magicbuddha.redditorsdigest.reddit;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import net.dean.jraw.RedditClient;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by Magic_Buddha on 12/26/2017.
 */

public class SearchSubredditsTask extends AsyncTask<String, Void, List<String>> {

    private static final String TAG = AuthenticateBotTask.class.getCanonicalName();

    // used to retrieve bot info
    private WeakReference<Context> weakContext;
    private SearchCallback callback;
    private boolean allowNSFW;

    public SearchSubredditsTask(WeakReference<Context> weakContext, @NonNull SearchCallback callback, boolean allowNSFW) {
        this.weakContext = weakContext;
        this.callback = callback;
        this.allowNSFW = allowNSFW;
    }

    @Override
    protected List<String> doInBackground(String... strings) {
        RedditClient reddit = Reddit.getInstance().getRedditClient();
        List<String> subreddits = null;

        Context context = weakContext.get();

        if (context != null) {
            subreddits = reddit.searchSubreddits(strings[0], allowNSFW);
        } else {
            Log.w(TAG, "Failed find any subreddits. Context was null.");
        }

        return subreddits;
    }

    @Override
    protected void onPostExecute(List<String> subreddits) {
        super.onPostExecute(subreddits);
        callback.onSearchComplete(subreddits);
    }

    public interface SearchCallback {
        /**
         * Called when search for subreddits is complete.
         *
         * @param subreddits {@link List<String>} of subreddits found.
         */
        void onSearchComplete(List<String> subreddits);
    }
}
