package com.magicbuddha.redditorsdigest.reddit;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import net.dean.jraw.RedditClient;
import net.dean.jraw.models.Submission;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by Magic_Buddha on 12/26/2017.
 */

public class GetSubredditsTask extends AsyncTask<String, Void, List<Submission>> {

    private static final String TAG = GetSubredditsTask.class.getCanonicalName();

    // used to retrieve bot info
    private WeakReference<Context> weakContext;
    private SubredditsCallback callback;
    private boolean allowNSFW;

    public GetSubredditsTask(WeakReference<Context> weakContext, @NonNull SubredditsCallback callback, boolean allowNSFW) {
        this.weakContext = weakContext;
        this.callback = callback;
        this.allowNSFW = allowNSFW;
    }

    @Override
    protected List<Submission> doInBackground(String... strings) {
        RedditClient reddit = Reddit.getInstance().getRedditClient();
        List<Submission> subreddits = null;

        Context context = weakContext.get();

        if (context != null) {
            try {

//                SubredditPaginator paginator = new SubredditPaginator(reddit, "pics", "pcgaming");
//                List<Listing<Submission>> submissionListing = paginator.accumulate(SubredditPaginator.RECOMMENDED_MAX_LIMIT);
//
//                subreddits = submissionListing.get(0).getChildren();
//                for (Submission s : subreddits) {
//                    Log.w("ROKAS", s.getTitle());
//                }

            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        } else {
            Log.w(TAG, "Failed find any subreddits. Context was null.");
        }

        return subreddits;
    }

    @Override
    protected void onPostExecute(List<Submission> subreddits) {
        super.onPostExecute(subreddits);
        callback.onComplete(subreddits);
    }

    public interface SubredditsCallback {
        /**
         * Called when search for subreddits is complete.
         *
         * @param submissions {@link List<Submission>} of subreddits found.
         */
        void onComplete(List<Submission> submissions);
    }
}
