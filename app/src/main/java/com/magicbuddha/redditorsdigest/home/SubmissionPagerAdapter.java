package com.magicbuddha.redditorsdigest.home;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.magicbuddha.redditorsdigest.submissions.PictureSubmissionFragment;

import java.util.List;

/**
 * Created by Magic_Buddha on 2/4/2018.
 */

public class SubmissionPagerAdapter extends FragmentStatePagerAdapter {

    private List<String> submissions;

    public SubmissionPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return PictureSubmissionFragment.getInstance(submissions.get(position));
    }

    @Override
    public int getItemPosition(Object object) {
        if (object instanceof PictureSubmissionFragment) {
            PictureSubmissionFragment fragment = (PictureSubmissionFragment) object;
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
}
