package com.magicbuddha.redditorsdigest.search;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.magicbuddha.redditorsdigest.data.SubscriptionsContract;
import com.magicbuddha.redditorsdigest.views.SubredditListItemView;

import net.dean.jraw.models.Subreddit;

import java.util.List;

/**
 * Created by Magic_Buddha on 12/28/2017.
 */

public class SearchSubredditAdapter extends RecyclerView.Adapter<SearchSubredditAdapter.SubredditViewHolder> {

    private Context context;
    private List<Subreddit> subreddits;
    private List<String> subscriptions;
    private SubredditAdapterListener listener;

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
                        if (isSubscribed) {
                            ContentValues cv = new ContentValues();
                            cv.put(SubscriptionsContract.SubscriptionEntity.SUBSCRIPTION_COLUMN, subreddits.get(adapterPosition).getTitle());

                            context.getContentResolver().insert(
                                    SubscriptionsContract.SubscriptionEntity.CONTENT_URI, cv);
                        } else {
                            Uri unsubscribeUri = SubscriptionsContract.SubscriptionEntity.CONTENT_URI.buildUpon()
                                    .appendPath(subreddits.get(adapterPosition).getTitle())
                                    .build();

                            context.getContentResolver().delete(unsubscribeUri, null, null);
                        }
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
        holder.view.setTitle(subreddits.get(position).getTitle());
        holder.view.setHint("Subs: " + subreddits.get(position).getSubscribers());
        if (subscriptions.contains(subreddits.get(position).getTitle())) {
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
        void onClick(Subreddit subreddit);
    }
}
