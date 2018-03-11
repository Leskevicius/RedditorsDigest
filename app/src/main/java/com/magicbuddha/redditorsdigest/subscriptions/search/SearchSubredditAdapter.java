package com.magicbuddha.redditorsdigest.subscriptions.search;

import android.app.Application;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.magicbuddha.redditorsdigest.R;
import com.magicbuddha.redditorsdigest.views.SubredditListItemView;

import net.dean.jraw.models.Subreddit;

import java.util.List;

/**
 * Created by Magic_Buddha on 12/28/2017.
 */

public class SearchSubredditAdapter extends RecyclerView.Adapter<SearchSubredditAdapter.SubredditViewHolder> {

    private List<Subreddit> subreddits;
    private List<String> subscriptions;
    private SubredditAdapterListener listener;
    private Context context;

    public SearchSubredditAdapter(Context context, SubredditAdapterListener listener) {
        this.listener = listener;
        this.context = context;
    }

    public class SubredditViewHolder extends RecyclerView.ViewHolder {

        SubredditListItemView view;

        public SubredditViewHolder(View itemView) {
            super(itemView);

            if (itemView instanceof SubredditListItemView) {
                view = (SubredditListItemView) itemView;
            }
        }
    }

    @Override
    public SearchSubredditAdapter.SubredditViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SubredditViewHolder(new SubredditListItemView(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(final SearchSubredditAdapter.SubredditViewHolder holder, int position) {
        holder.view.setOnSubscribeListener(new SubredditListItemView.OnSubscribeListener() {
            @Override
            public void onSubscribeClicked(boolean isSubscribed) {
                int adapterPosition = holder.getAdapterPosition();
                if (listener != null) {
                    listener.onSubscribed(subreddits.get(adapterPosition), isSubscribed);
                }

                holder.view.setSubscribed(isSubscribed);
                if (isSubscribed) {
                    subscriptions.add(subreddits.get(adapterPosition).getName());
                } else {
                    subscriptions.remove(subreddits.get(adapterPosition).getName());
                }
            }
        });
        holder.view.setTitle(subreddits.get(position).getName());
        holder.view.setHint(context.getString(R.string.subs) + " " + subreddits.get(position).getSubscribers());
        if (subscriptions.contains(subreddits.get(position).getName())) {
            holder.view.setSubscribed(true);
        } else {
            holder.view.setSubscribed(false);
        }
    }

    @Override
    public int getItemCount() {
        return this.subreddits == null ? 0 : this.subreddits.size();
    }

    public void setData(List<Subreddit> data, List<String> subscriptions) {
        this.subreddits = data;
        this.subscriptions = subscriptions;
        notifyDataSetChanged();
    }

    public interface SubredditAdapterListener {
        void onSubscribed(Subreddit subreddit, boolean subscribed);
    }
}
