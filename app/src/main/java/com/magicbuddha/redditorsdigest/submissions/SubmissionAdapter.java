package com.magicbuddha.redditorsdigest.submissions;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.magicbuddha.redditorsdigest.R;
import com.magicbuddha.redditorsdigest.views.CommentView;

import net.dean.jraw.models.PublicContribution;
import net.dean.jraw.models.Submission;
import net.dean.jraw.tree.CommentNode;

import java.util.List;

/**
 * Created by Magic_Buddha on 2/4/2018.
 */

public class SubmissionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_CONTENT_PICTURE = 0;
    private static final int TYPE_CONTENT_SELF = 1;
    private static final int TYPE_COMMENT = 2;

    private Submission submission;
    private List<CommentNode<PublicContribution<?>>> list;
    private double halfScreenPx;

    private Context context;

    public SubmissionAdapter(Context context, Submission submission, double halfScreenPx) {
        this.submission = submission;
        this.context = context;
        this.halfScreenPx = halfScreenPx;
    }

    public SubmissionAdapter(Context context, double halfScreenPx) {
        this(context, null, halfScreenPx);
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {
        CommentView view;

        public CommentViewHolder(View itemView) {
            super(itemView);
            if (itemView instanceof CommentView) {
                view = (CommentView) itemView;
            }
        }
    }

    public class ContentPictureViewHolder extends RecyclerView.ViewHolder {
        ImageView view;

        public ContentPictureViewHolder(View itemView) {
            super(itemView);
            if (itemView instanceof ImageView) {
                view = (ImageView) itemView;
            }
        }
    }

    public class ContentSelfViewHolder extends RecyclerView.ViewHolder {
        public ContentSelfViewHolder(View itemView) {
            super(itemView);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_COMMENT) {
            return new CommentViewHolder(new CommentView(parent.getContext()));
        } else if (viewType == TYPE_CONTENT_PICTURE) {
            return new ContentPictureViewHolder(new ImageView(parent.getContext()));
        } else if (viewType == TYPE_CONTENT_SELF) {
            return new ContentSelfViewHolder(new View(parent.getContext()));
        }

        throw new RuntimeException("No type matching: " + viewType + ".");
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CommentViewHolder) {
            CommentViewHolder viewHolder = (CommentViewHolder) holder;
            viewHolder.view.setBody(getItem(position).getSubject().getBody());
            viewHolder.view.setAuthor(context.getString(R.string.author_prefix) + getItem(position).getSubject().getAuthor());
            viewHolder.view.setDepth(getItem(position).getDepth());
        } else if (holder instanceof ContentPictureViewHolder) {
            ((ContentPictureViewHolder) holder).view.setScaleType(ImageView.ScaleType.CENTER_CROP);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.height = (int) halfScreenPx;
            ((ContentPictureViewHolder) holder).view.setLayoutParams(params);
            Glide.with(context)
                    .load(submission.getUrl())
                    .into(((ContentPictureViewHolder) holder).view);
        } else if (holder instanceof ContentSelfViewHolder) {
            // build self post
        }
    }

    @Override
    public int getItemViewType(int position) {
        int type;
        if (position == 0) {
            type = submission.isSelfPost() ? TYPE_CONTENT_SELF : TYPE_CONTENT_PICTURE;
        } else {
            type = TYPE_COMMENT;
        }
        return type;
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size() + 1;
    }

    private CommentNode<PublicContribution<?>> getItem(int position) {
        return list.get(position - 1);
    }

    public void setData(List<CommentNode<PublicContribution<?>>> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public void setData(List<CommentNode<PublicContribution<?>>> list, Submission submission) {
        this.submission = submission;
        setData(list);
    }
}
