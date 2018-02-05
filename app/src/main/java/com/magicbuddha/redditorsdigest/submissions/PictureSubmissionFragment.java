package com.magicbuddha.redditorsdigest.submissions;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.magicbuddha.redditorsdigest.R;

import net.dean.jraw.models.PublicContribution;
import net.dean.jraw.models.Submission;
import net.dean.jraw.tree.CommentNode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Magic_Buddha on 1/17/2018.
 */

public class PictureSubmissionFragment extends Fragment implements GetSubmissionDataTask.SubmissionDataCallback {
    protected String submissionId;
    protected Submission submission;
    protected List<CommentNode<PublicContribution<?>>> comments;
    protected SubmissionAdapter adapter;

    @BindView(R.id.submission_fob)
    FloatingActionButton fob;

    @BindView(R.id.submission_progressbar)
    ProgressBar progressBar;

    @BindView(R.id.comment_recycler_view)
    RecyclerView recyclerView;

    private static final String SUBMISSION_ID = "submissionId";

    public static PictureSubmissionFragment getInstance(String submissionId) {
        Bundle bundle = new Bundle();
        bundle.putString(SUBMISSION_ID, submissionId);
        PictureSubmissionFragment pictureSubmissionFragment = new PictureSubmissionFragment();
        pictureSubmissionFragment.setArguments(bundle);
        return pictureSubmissionFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.picture_submission, container, false);
        ButterKnife.bind(this, view);

        Bundle bundle = getArguments();
        submissionId = bundle.getString(SUBMISSION_ID);

        if (bundle != null) {
            new GetSubmissionDataTask(this).execute(bundle.getString(SUBMISSION_ID));
            progressBar.setVisibility(View.VISIBLE);
        }

        fob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w("Rokas", "Fab Clicked");
            }
        });

        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        DisplayMetrics dm = resources.getDisplayMetrics();

        double screenWidthInPixels = (double) config.screenWidthDp * dm.density;
        double screenHeightInPixels = screenWidthInPixels * dm.heightPixels / dm.widthPixels;

        adapter = new SubmissionAdapter(getContext(), screenHeightInPixels * 0.5f);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        return view;
    }

    public String getSubmissionId() {
        return submissionId;
    }

    @Override
    public void onComplete(Submission submission, List<CommentNode<PublicContribution<?>>> comments) {
        this.submission = submission;
        this.comments = comments;
        progressBar.setVisibility(View.GONE);
        adapter.setData(comments, submission);
    }
}
