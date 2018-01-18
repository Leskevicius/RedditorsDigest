package com.magicbuddha.redditorsdigest.submissions;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.magicbuddha.redditorsdigest.R;
import com.magicbuddha.redditorsdigest.reddit.Reddit;

import net.dean.jraw.RedditClient;
import net.dean.jraw.models.Submission;
import net.dean.jraw.references.SubmissionReference;
import net.dean.jraw.tree.RootCommentNode;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Magic_Buddha on 1/17/2018.
 */

public class PictureSubmission extends Fragment implements GetSubmissionDataTask.SubmissionDataCallback {
    protected Submission submission;
    protected RootCommentNode rootCommentNode;

    @BindView(R.id.content_container)
    FrameLayout container;

    @BindView(R.id.submission_fob)
    FloatingActionButton fob;

    @BindView(R.id.submission_progressbar)
    ProgressBar progressBar;

    private static final String SUBMISSION_ID = "submissionId";

    public static PictureSubmission getInstance(String submissionId) {
        Bundle bundle = new Bundle();
        bundle.putString(SUBMISSION_ID, submissionId);
        PictureSubmission pictureSubmission = new PictureSubmission();
        pictureSubmission.setArguments(bundle);
        return pictureSubmission;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.picture_submission, container, false);
        ButterKnife.bind(this, view);

        Bundle bundle = getArguments();

        if (bundle != null) {
            new GetSubmissionDataTask(this).execute(bundle.getString(SUBMISSION_ID));
            progressBar.setVisibility(View.VISIBLE);
        }

        fob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(PictureSubmission.this.container, "Fob clicked!", Snackbar.LENGTH_SHORT);
            }
        });
        return view;
    }

    public Submission getSubmission() {
        return submission;
    }

    public void setSubmission(Submission submission) {
        this.submission = submission;
    }

    @Override
    public void onComplete(Submission submission, RootCommentNode rootCommentNode) {
        this.submission = submission;
        this.rootCommentNode = rootCommentNode;
        progressBar.setVisibility(View.GONE);
    }
}
