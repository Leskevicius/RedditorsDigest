package com.magicbuddha.redditorsdigest.search;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.magicbuddha.redditorsdigest.views.SubredditListItemView;

import java.util.List;

/**
 * Created by Magic_Buddha on 12/28/2017.
 */

public class SearchSubredditAdapter extends RecyclerView.Adapter<SearchSubredditAdapter.SubredditViewHolder> {

    private List<String> subreddits;
    private SubredditAdapterListener listener;

    public SearchSubredditAdapter(SubredditAdapterListener listener) {
        this.listener = listener;
    }

    public class SubredditViewHolder extends RecyclerView.ViewHolder {

        SubredditListItemView view;

        public SubredditViewHolder(View itemView) {
            super(itemView);

            if (itemView instanceof SubredditListItemView) {
                view = (SubredditListItemView) itemView;
                view.setOnSubscribeListener(new SubredditListItemView.OnSubscribeListener() {
                    @Override
                    public void onSubscribeClicked(boolean isSubscribed) {
                        int adapterPosition = getAdapterPosition();
                        if (listener != null) {
                            listener.onClick(subreddits.get(adapterPosition));
                        }

                        // think I can just call notifydatasetchanged instead of directly setting view stuff
                        // not sure whats the best approach.
                        view.setSubscribed(isSubscribed);

                        // write to content provider?
                    }
                });
            }
        }
    }
    @Override
    public SearchSubredditAdapter.SubredditViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SubredditViewHolder(new SubredditListItemView(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(SearchSubredditAdapter.SubredditViewHolder holder, int position) {
        holder.view.setTitle(subreddits.get(position));

        // set favorite from content provider?
    }

    @Override
    public int getItemCount() {
        return this.subreddits == null ? 0 : this.subreddits.size();
    }

    public void setData(List<String> data) {
        this.subreddits = data;
        notifyDataSetChanged();
    }

    public interface SubredditAdapterListener {
        void onClick(String subredditTaskData);
    }


}
