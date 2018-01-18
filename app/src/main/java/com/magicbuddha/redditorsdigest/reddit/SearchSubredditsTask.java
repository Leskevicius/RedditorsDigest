package com.magicbuddha.redditorsdigest.reddit;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import net.dean.jraw.RedditClient;
import net.dean.jraw.models.Subreddit;
import net.dean.jraw.pagination.SearchPaginator;
import net.dean.jraw.pagination.SubredditSearchPaginator;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by Magic_Buddha on 12/26/2017.
 */

public class SearchSubredditsTask extends AsyncTask<String, Void, List<Subreddit>> {

    private static final String TAG = SearchSubredditsTask.class.getCanonicalName();

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
    protected List<Subreddit> doInBackground(String... strings) {
        RedditClient reddit = Reddit.getInstance().getRedditClient();
        List<Subreddit> list = null;

        Context context = weakContext.get();

        if (context != null) {
            try {
                SubredditSearchPaginator paginator = reddit.searchSubreddits()
                        .query(strings[0])
                        .limit(SubredditSearchPaginator.RECOMMENDED_MAX_LIMIT)
                        .build();

                list = paginator.accumulateMerged(-1);



            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        } else {
            Log.w(TAG, "Failed find any subreddits. Context was null.");
        }

        return list;
    }

    @Override
    protected void onPostExecute(List<Subreddit> subreddits) {
        super.onPostExecute(subreddits);
        callback.onSearchComplete(subreddits);
    }

    public interface SearchCallback {
        /**
         * Called when search for subreddits is complete.
         *
         * @param subreddits {@link List<String>} of subreddits found.
         */
        void onSearchComplete(List<Subreddit> subreddits);
    }
}
