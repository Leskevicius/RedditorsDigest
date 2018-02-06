package com.magicbuddha.redditorsdigest.reddit;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import net.dean.jraw.RedditClient;
import net.dean.jraw.models.Subreddit;
import net.dean.jraw.pagination.SubredditSearchPaginator;

import java.util.List;

/**
 * Created by Magic_Buddha on 12/26/2017.
 */

public class SearchSubredditsTask extends AsyncTask<String, Void, List<Subreddit>> {

    private static final String TAG = SearchSubredditsTask.class.getCanonicalName();

    // used to retrieve bot info
    private SearchCallback callback;
    private boolean allowNSFW;

    public SearchSubredditsTask(@NonNull SearchCallback callback, boolean allowNSFW) {
        this.callback = callback;
        this.allowNSFW = allowNSFW;
    }

    @Override
    protected List<Subreddit> doInBackground(String... strings) {
        RedditClient reddit = Reddit.getInstance().getRedditClient();
        List<Subreddit> list = null;

        try {
            SubredditSearchPaginator paginator = reddit.searchSubreddits()
                    .query(strings[0])
                    .limit(SubredditSearchPaginator.RECOMMENDED_MAX_LIMIT)
                    .build();

            list = paginator.accumulateMerged(-1);

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
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
