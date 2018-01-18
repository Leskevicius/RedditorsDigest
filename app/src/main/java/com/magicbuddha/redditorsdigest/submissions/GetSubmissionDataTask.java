package com.magicbuddha.redditorsdigest.submissions;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.magicbuddha.redditorsdigest.reddit.Reddit;

import net.dean.jraw.RedditClient;
import net.dean.jraw.models.Submission;
import net.dean.jraw.references.SubmissionReference;
import net.dean.jraw.tree.RootCommentNode;

/**
 * Created by Magic_Buddha on 12/26/2017.
 */

public class GetSubmissionDataTask extends AsyncTask<String, Void, Void> {

    private static final String TAG = GetSubmissionDataTask.class.getCanonicalName();

    private SubmissionDataCallback callback;

    private Submission submission;
    private RootCommentNode rootCommentNode;

    public GetSubmissionDataTask(@NonNull SubmissionDataCallback callback) {
        this.callback = callback;
    }

    @Override
    protected Void doInBackground(String... strings) {
        RedditClient reddit = Reddit.getInstance().getRedditClient();

        try {
            SubmissionReference ref = reddit.submission(strings[0]);
            rootCommentNode = ref.comments();
            submission = rootCommentNode.getSubject();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void v) {
        super.onPostExecute(v);
        callback.onComplete(submission, rootCommentNode);
    }

    public interface SubmissionDataCallback {
        void onComplete(Submission submission, RootCommentNode rootCommentNode);
    }
}
