package com.magicbuddha.redditorsdigest.submissions;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.magicbuddha.redditorsdigest.reddit.Reddit;

import net.dean.jraw.RedditClient;
import net.dean.jraw.models.PublicContribution;
import net.dean.jraw.models.Submission;
import net.dean.jraw.references.SubmissionReference;
import net.dean.jraw.tree.CommentNode;
import net.dean.jraw.tree.RootCommentNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Magic_Buddha on 12/26/2017.
 */

public class GetSubmissionDataTask extends AsyncTask<String, Void, Void> {

    private static final String TAG = GetSubmissionDataTask.class.getCanonicalName();

    private SubmissionDataCallback callback;

    private Submission submission;
    private RootCommentNode rootCommentNode;
    private List<CommentNode<PublicContribution<?>>> comments;

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

            Iterator<CommentNode<PublicContribution<?>>> i = rootCommentNode.walkTree().iterator();
            comments = new ArrayList<>();
            // skipping over empty comment by author.
            i.next();
            while (i.hasNext()) {
                comments.add(i.next());
            }

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void v) {
        super.onPostExecute(v);
        callback.onComplete(submission, comments);
    }

    public interface SubmissionDataCallback {
        void onComplete(Submission submission, List<CommentNode<PublicContribution<?>>> comments);
    }
}
