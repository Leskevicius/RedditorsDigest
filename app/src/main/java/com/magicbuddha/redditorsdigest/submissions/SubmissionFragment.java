package com.magicbuddha.redditorsdigest.submissions;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class SubmissionFragment extends Fragment implements GetSubmissionDataTask.SubmissionDataCallback {
    protected String submissionId;
    protected Submission submission;
    protected List<CommentNode<PublicContribution<?>>> comments;
    protected SubmissionAdapter adapter;

    Parcelable layoutManagerState;

    @BindView(R.id.submission_fob)
    FloatingActionButton fob;

    @BindView(R.id.submission_progressbar)
    ProgressBar progressBar;

    @BindView(R.id.comment_recycler_view)
    RecyclerView recyclerView;

    private static final String SUBMISSION_ID = "submissionId";
    private static final String REDDIT_BASE_URL = "https://www.reddit.com";
    private static final String LAYOUT_MANAGER_STATE = "LAYOUT_MANAGER_STATE";

    public static SubmissionFragment getInstance(String submissionId) {
        Bundle bundle = new Bundle();
        bundle.putString(SUBMISSION_ID, submissionId);
        SubmissionFragment submissionFragment = new SubmissionFragment();
        submissionFragment.setArguments(bundle);
        return submissionFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.submission, container, false);
        ButterKnife.bind(this, view);

        Bundle bundle = getArguments();
        submissionId = bundle.getString(SUBMISSION_ID);
        progressBar.setVisibility(View.VISIBLE);

        fob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(REDDIT_BASE_URL + submission.getPermalink()));
                startActivity(i);
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

        new GetSubmissionDataTask(this).execute(bundle.getString(SUBMISSION_ID));
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
        fob.setVisibility(View.VISIBLE);
        adapter.setData(comments, submission);

        recyclerView.getLayoutManager().onRestoreInstanceState(layoutManagerState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (savedInstanceState != null) {
            layoutManagerState = savedInstanceState.getParcelable(LAYOUT_MANAGER_STATE);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(LAYOUT_MANAGER_STATE, recyclerView.getLayoutManager().onSaveInstanceState());
    }
}
