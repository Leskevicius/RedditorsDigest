package com.magicbuddha.redditorsdigest.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Magic_Buddha on 12/25/2017.
 */

public class SubmissionSelfPostFragment extends Fragment {

    private static final String SUBMISSION_POSITION = "submissionPosition";

    public static SubmissionSelfPostFragment getInstance(int position) {
        Bundle args = new Bundle();
        args.putInt(SUBMISSION_POSITION, position);
        SubmissionSelfPostFragment sf = new SubmissionSelfPostFragment();
        sf.setArguments(args);
        return sf;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        return null;
    }
}
