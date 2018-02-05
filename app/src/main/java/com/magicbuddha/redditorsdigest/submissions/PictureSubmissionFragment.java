package com.magicbuddha.redditorsdigest.submissions;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import net.dean.jraw.tree.RootCommentNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Magic_Buddha on 1/17/2018.
 */

public class PictureSubmissionFragment extends Fragment implements GetSubmissionDataTask.SubmissionDataCallback {
    protected Submission submission;
    protected RootCommentNode rootCommentNode;
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

        adapter = new SubmissionAdapter(getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onComplete(Submission submission, RootCommentNode rootCommentNode) {
        this.submission = submission;
        this.rootCommentNode = rootCommentNode;
        progressBar.setVisibility(View.GONE);

        Iterator<CommentNode<PublicContribution<?>>> i = rootCommentNode.walkTree().iterator();
        List<CommentNode<PublicContribution<?>>> list = new ArrayList<>();
        while (i.hasNext()) {
            list.add(i.next());
        }

        adapter.setData(list, submission);
    }
}
