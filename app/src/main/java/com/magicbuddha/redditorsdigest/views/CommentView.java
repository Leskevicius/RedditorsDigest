package com.magicbuddha.redditorsdigest.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.magicbuddha.redditorsdigest.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Magic_Buddha on 2/4/2018.
 */

public class CommentView extends LinearLayout {
    @BindView(R.id.comment_body)
    TextView bodyView;

    @BindView(R.id.comment_author)
    TextView authorView;

    @BindView(R.id.line_container)
    LinearLayout lineContainer;

    @BindView(R.id.comment_body_container)
    LinearLayout commentBodyContainer;

    private int depth;
    private String text;

    public CommentView(Context context) {
        super(context);

        init(context, null);
    }

    public CommentView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs);
    }

    public CommentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.comment, this);
        ButterKnife.bind(this, this);

        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
        setLayoutParams(linearParams);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.CommentView,
                0, 0);

        int depth;

        String author;
        try {
            text = a.getString(R.styleable.CommentView_text);
            author = a.getString(R.styleable.CommentView_subtext);
            depth = a.getInt(R.styleable.CommentView_depth, 0);
        } finally {
            a.recycle();
        }


        for (int i = 0; i < depth; i++) {
            View view = LayoutInflater.from(context).inflate(R.layout.vertical_line, lineContainer, false);
            lineContainer.addView(view);
        }

        bodyView.setText(text);
        authorView.setText(author);
    }

    public void setBody(String text) {
        this.text = text;
        bodyView.setText(text);

    }

    public String getBody() {
        return text;
    }

    public void setDepth(int depth) {
        this.depth = depth;
        lineContainer.removeAllViews();
        for (int i = 0; i < depth; i++) {
            LayoutInflater.from(getContext()).inflate(R.layout.vertical_line, lineContainer);
        }
    }

    public int getDepth() {
        return depth;
    }

    public void setAuthor(String author) {
        authorView.setText(author);
    }

    public String getAuthor() {
        return authorView.getText().toString();
    }

    public void setCommentBackgroundColor(@ColorInt int colorId) {
        commentBodyContainer.setBackgroundColor(colorId);
    }
}
