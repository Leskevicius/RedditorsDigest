package com.magicbuddha.redditorsdigest.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.magicbuddha.redditorsdigest.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Magic_Buddha on 12/28/2017.
 */

public class NoSubscriptionsFragment extends Fragment {

    private static final String LOG = NoSubscriptionsFragment.class.getCanonicalName();
    @BindView(R.id.no_subscription_button)
    Button searchButton;

    public static NoSubscriptionsFragment getInstance(Bundle bundle) {
        NoSubscriptionsFragment nsf = new NoSubscriptionsFragment();
        nsf.setArguments(bundle);
        return nsf;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.no_subscription, container, false);
        ButterKnife.bind(this, view);

        // set the button to launch search view
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() instanceof HomeActivity) {
                    ((HomeActivity) getActivity()).startSearchActivity();
                }
            }
        });
        return view;
    }
}
