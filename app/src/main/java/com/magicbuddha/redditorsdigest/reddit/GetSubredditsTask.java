package com.magicbuddha.redditorsdigest.reddit;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import net.dean.jraw.RedditClient;
import net.dean.jraw.models.Submission;
import net.dean.jraw.models.Subreddit;
import net.dean.jraw.pagination.Paginator;
import net.dean.jraw.references.SubredditReference;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Magic_Buddha on 12/26/2017.
 */

public class GetSubredditsTask extends AsyncTask<String, Void, List<Subreddit>> {

    private static final String TAG = GetSubredditsTask.class.getCanonicalName();

    // used to retrieve bot info
    private SubredditsCallback callback;

    public GetSubredditsTask(@NonNull SubredditsCallback callback) {
        this.callback = callback;
    }

    @Override
    protected List<Subreddit> doInBackground(String... strings) {
        RedditClient reddit = Reddit.getInstance().getRedditClient();
        List<SubredditReference> subredditReferences = new ArrayList<>();
        List<Subreddit> subreddits = new ArrayList<>();

            try {

                for (String subredditName : strings) {
                    subredditReferences.add(reddit.subreddit(subredditName));
                }

                subreddits.add(subredditReferences.get(0).about());

            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }

        return subreddits;
    }

    @Override
    protected void onPostExecute(List<Subreddit> subreddits) {
        super.onPostExecute(subreddits);
        callback.onComplete(subreddits);
    }

    public interface SubredditsCallback {
        /**
         * Called when search for subreddits is complete.
         *
         * @param subreddits {@link List<Submission>} of subreddits found.
         */
        void onComplete(List<Subreddit> subreddits);
    }
}
