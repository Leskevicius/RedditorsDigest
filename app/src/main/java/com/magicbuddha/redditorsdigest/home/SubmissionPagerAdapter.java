package com.magicbuddha.redditorsdigest.home;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.magicbuddha.redditorsdigest.submissions.SubmissionFragment;

import java.util.List;

/**
 * Created by Magic_Buddha on 2/4/2018.
 */

public class SubmissionPagerAdapter extends FragmentStatePagerAdapter {

    private List<String> submissions;
    private SubmissionPagerAdapterListener listener;

    public SubmissionPagerAdapter(FragmentManager fm, SubmissionPagerAdapterListener listener) {
        super(fm);
        this.listener = listener;
    }

    @Override
    public Fragment getItem(int position) {
        if (submissions.size() - (position + 1) <= 0) {
            listener.requestSubmissions();
        }
        return SubmissionFragment.getInstance(submissions.get(position));
    }

    @Override
    public int getItemPosition(Object object) {
        if (object instanceof SubmissionFragment) {
            SubmissionFragment fragment = (SubmissionFragment) object;
            String subId = fragment.getSubmissionId();

            int position = submissions.indexOf(subId);

            if (position >= 0) {
                return position;
            } else {
                return POSITION_NONE;
            }
        }

        return super.getItemPosition(object);
    }

    @Override
    public int getCount() {
        return submissions == null ? 0 : submissions.size();
    }

    public void setData(List<String> submissionIds) {
        this.submissions = submissionIds;
        notifyDataSetChanged();
    }

    public void addData(List<String> submissionIds) {
        if (this.submissions == null) {
            setData(submissionIds);
            return;
        }

        this.submissions.addAll(submissionIds);
        notifyDataSetChanged();
    }

    public interface SubmissionPagerAdapterListener {
        void requestSubmissions();
    }
}
