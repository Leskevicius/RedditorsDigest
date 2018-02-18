package com.magicbuddha.redditorsdigest.reddit;

import net.dean.jraw.models.Submission;
import net.dean.jraw.models.SubredditSort;
import net.dean.jraw.pagination.DefaultPaginator;
import net.dean.jraw.references.SubredditReference;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Magic_Buddha on 2/11/2018.
 */

public class SubredditProvider implements GetSubmissionIdsTask.SubredditsCallback {

    private static SubredditProvider instance;
    private static final Object lock = new Object();

    private List<DefaultPaginator<Submission>> submissionPaginatorList;
    private SubmissionListener listener;

    private SubredditProvider() {
        // hide constructor
    }

    public static SubredditProvider getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new SubredditProvider();
                }
            }
        }

        return instance;
    }

    public void init(List<String> subredditNames, SubredditSort sort, int limit, int pages) {
        submissionPaginatorList = new ArrayList<>();

        for (String subredditName : subredditNames) {
            Reddit reddit = Reddit.getInstance();
            SubredditReference sr = reddit.getRedditClient().subreddit(subredditName);

            DefaultPaginator<Submission> paginator = sr.posts()
                    .sorting(sort)
                    .limit(limit)
                    .build();

            submissionPaginatorList.add(paginator);
        }
    }

    /**
     * Blocking call. Fetches next set of submission ID's for use with {@link com.magicbuddha.redditorsdigest.submissions.SubmissionFragment}.
     */
    public void getNextSubmissions(SubmissionListener listener) {
        List<String> submissionIds = new ArrayList<>();
        this.listener = listener;
        new GetSubmissionIdsTask(this, submissionPaginatorList).execute();
    }

    @Override
    public void onComplete(List<List<String>> submissionsBySubredditList) {
        listener.onNextSubmissions(consolidate(submissionsBySubredditList));
    }

    private List<String> consolidate(List<List<String>> strings) {
        List<String> list = new ArrayList<>();
        int maxDepth = 0;
        for (List<String> l : strings) {
            maxDepth = l.size() > maxDepth ? l.size() : maxDepth;
        }

        for (int i = 0; i < maxDepth; i++) {
            for (int j = 0; j < strings.size(); j++) {
                if (strings.get(j).size() > i) {
                    list.add(strings.get(j).get(i));
                }
            }
        }

        return list;
    }

    public interface SubmissionListener {
        void onNextSubmissions(List<String> submissionIds);
    }
}
