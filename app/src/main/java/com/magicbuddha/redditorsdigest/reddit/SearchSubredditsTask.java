package com.magicbuddha.redditorsdigest.reddit;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.magicbuddha.redditorsdigest.models.SubredditData;

import net.dean.jraw.RedditClient;
import net.dean.jraw.models.Subreddit;
import net.dean.jraw.pagination.SubredditSearchPaginator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Magic_Buddha on 12/26/2017.
 */

public class SearchSubredditsTask extends AsyncTask<String, Void, List<SubredditData>> {

    private static final String TAG = SearchSubredditsTask.class.getCanonicalName();

    // used to retrieve bot info
    private SearchCallback callback;
    private boolean allowNSFW;

    public SearchSubredditsTask(@NonNull SearchCallback callback, boolean allowNSFW) {
        this.callback = callback;
        this.allowNSFW = allowNSFW;
    }

    @Override
    protected List<SubredditData> doInBackground(String... strings) {
        RedditClient reddit = Reddit.getInstance().getRedditClient();
        List<Subreddit> list;
        List<SubredditData> subredditDataList = new ArrayList<>();

        try {
            SubredditSearchPaginator paginator = reddit.searchSubreddits()
                    .query(strings[0])
                    .limit(SubredditSearchPaginator.RECOMMENDED_MAX_LIMIT)
                    .build();

            list = paginator.accumulateMerged(-1);

            if (!allowNSFW) {
                for (int i = list.size() - 1; i >= 0; i--) {
                    if (list.get(i).isNsfw()) {
                        list.remove(i);
                    }
                }
            }

            for (Subreddit s : list) {
                subredditDataList.add(SubredditData.parse(s));
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        return subredditDataList;
    }

    @Override
    protected void onPostExecute(List<SubredditData> subreddits) {
        super.onPostExecute(subreddits);
        callback.onSearchComplete(subreddits);
    }

    public interface SearchCallback {
        /**
         * Called when search for subreddits is complete.
         *
         * @param subreddits {@link List<SubredditData>} of subreddits found.
         */
        void onSearchComplete(List<SubredditData> subreddits);
    }
}
