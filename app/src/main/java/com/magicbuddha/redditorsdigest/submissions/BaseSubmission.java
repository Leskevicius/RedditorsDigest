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

import com.magicbuddha.redditorsdigest.R;

import net.dean.jraw.models.Submission;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Magic_Buddha on 1/17/2018.
 */

public abstract class BaseSubmission extends Fragment {
    protected Submission submission;

    @BindView(R.id.content_container)
    FrameLayout container;

    @BindView(R.id.submission_fob)
    FloatingActionButton fob;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.base_submission, container, false);
        ButterKnife.bind(this, view);

        fob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(BaseSubmission.this.container, "Fob clicked!", Snackbar.LENGTH_SHORT);
            }
        });
        populateContent(this.container);
        return view;
    }

    public abstract void populateContent(View container);


}
