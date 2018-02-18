package com.magicbuddha.redditorsdigest.reddit;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import net.dean.jraw.RedditClient;
import net.dean.jraw.models.Submission;
import net.dean.jraw.pagination.DefaultPaginator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Magic_Buddha on 12/26/2017.
 */

public class GetSubmissionIdsTask extends AsyncTask<Void, Void, List<List<String>>> {

    private static final String TAG = GetSubmissionIdsTask.class.getCanonicalName();

    private SubredditsCallback callback;
    private List<DefaultPaginator<Submission>> submissionPaginatorList;

    public GetSubmissionIdsTask(@NonNull SubredditsCallback callback, @NonNull List<DefaultPaginator<Submission>> paginators) {
        this.callback = callback;
        this.submissionPaginatorList = paginators;
    }

    @Override
    protected List<List<String>> doInBackground(Void... voids) {
        RedditClient reddit = Reddit.getInstance().getRedditClient();

        List<List<String>> submissionsBySubredditList = new ArrayList<>();

        for (DefaultPaginator<Submission> paginator : submissionPaginatorList) {
            List<Submission> submissions = paginator.next();
            List<String> submissionIds = new ArrayList<>();
            for (Submission s : submissions) {
                submissionIds.add(s.getId());
            }

            submissionsBySubredditList.add(submissionIds);
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
