package com.magicbuddha.redditorsdigest.subscriptions;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.magicbuddha.redditorsdigest.views.SubredditListItemView;

import java.util.List;

/**
 * Created by Magic_Buddha on 2/19/2018.
 */

public class ManageSubscriptionsAdapter extends RecyclerView.Adapter<ManageSubscriptionsAdapter.SubscriptionViewHolder> {

    private List<String> subscriptions;
    private ManageSubscriptionsAdapterListener listener;

    public ManageSubscriptionsAdapter(ManageSubscriptionsAdapterListener listener) {
        this.listener = listener;
    }

    public void removeItem(String subreddit) {
        subscriptions.remove(subreddit);
        notifyDataSetChanged();
    }

    public class SubscriptionViewHolder extends RecyclerView.ViewHolder {

        SubredditListItemView view;

        public SubscriptionViewHolder(View itemView) {
            super(itemView);
            if (itemView instanceof SubredditListItemView) {
                view = (SubredditListItemView) itemView;
            }
        }
    }

    @Override
    public SubscriptionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SubscriptionViewHolder(new SubredditListItemView(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(final SubscriptionViewHolder holder, int position) {
        holder.view.setSubscribed(true);
        holder.view.setTitle(subscriptions.get(position));
        holder.view.setOnSubscribeListener(new SubredditListItemView.OnSubscribeListener() {
            @Override
            public void onSubscribeClicked(boolean isSubscribed) {
                listener.onUnsubscribed(holder.view.getTitle());
            }
        });
    }

    @Override
    public int getItemCount() {
        return subscriptions == null ? 0 : subscriptions.size();
    }

    public void setData(List<String> subscriptions) {
        this.subscriptions = subscriptions;
        notifyDataSetChanged();
    }

    public interface ManageSubscriptionsAdapterListener {
        void onUnsubscribed(String subreddit);
    }
}
