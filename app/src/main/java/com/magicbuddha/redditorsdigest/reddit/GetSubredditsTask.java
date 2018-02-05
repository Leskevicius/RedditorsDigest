package com.magicbuddha.redditorsdigest.reddit;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import net.dean.jraw.RedditClient;
import net.dean.jraw.models.Submission;
import net.dean.jraw.models.Subreddit;
import net.dean.jraw.models.SubredditSort;
import net.dean.jraw.pagination.DefaultPaginator;
import net.dean.jraw.pagination.Paginator;
import net.dean.jraw.references.SubredditReference;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Magic_Buddha on 12/26/2017.
 */

public class GetSubredditsTask extends AsyncTask<String, Void, List<List<String>>> {

    private static final String TAG = GetSubredditsTask.class.getCanonicalName();

    // used to retrieve bot info
    private SubredditsCallback callback;

    public GetSubredditsTask(@NonNull SubredditsCallback callback) {
        this.callback = callback;
    }

    @Override
    protected List<List<String>> doInBackground(String... strings) {
        RedditClient reddit = Reddit.getInstance().getRedditClient();
        List<SubredditReference> subredditReferences = new ArrayList<>();
        List<List<String>> submissionsBySubredditList = new ArrayList<>();
        try {

            for (String subredditName : strings) {
                subredditReferences.add(reddit.subreddit(subredditName));
            }

            for (SubredditReference sr : subredditReferences) {
                List<String> submissionIdList = new ArrayList<>();
                DefaultPaginator<Submission> paginator = sr.posts()
                        .sorting(SubredditSort.HOT)
                        .limit(10)
                        .build();

                List<Submission> submissionList = paginator.accumulateMerged(1);

                for (Submission s : submissionList) {
                    submissionIdList.add(s.getId());
                }

                Log.w(TAG, submissionIdList.toString());

                submissionsBySubredditList.add(submissionIdList);
            }

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return submissionsBySubredditList;
    }

    @Override
    protected void onPostExecute(List<List<String>> submissionsBySubredditList) {
        super.onPostExecute(submissionsBySubredditList);
        callback.onComplete(submissionsBySubredditList);
    }

    public interface SubredditsCallback {
        void onComplete(List<List<String>> submissionsBySubredditList);
    }
}
