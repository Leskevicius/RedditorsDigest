package com.magicbuddha.redditorsdigest.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.magicbuddha.redditorsdigest.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Magic_Buddha on 2/4/2018.
 */

public class CommentView extends RelativeLayout {
    @BindView(R.id.comment_body)
    TextView bodyView;

    @BindView(R.id.comment_author)
    TextView authorView;

    @BindView(R.id.line_container)
    LinearLayout lineContainer;

    private int depth;

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

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.CommentView,
                0, 0);

        String body;
        String subtext;
        int depth;

        try {
            body = a.getString(R.styleable.CommentView_text);
            subtext = a.getString(R.styleable.CommentView_subtext);
            depth = a.getInt(R.styleable.CommentView_depth, 0);
        } finally {
            a.recycle();
        }

        bodyView.setText(body);
        authorView.setText(subtext);

        for (int i = 0; i < depth; i++) {
            View view = LayoutInflater.from(context).inflate(R.layout.vertical_line, lineContainer, false);
            lineContainer.addView(view);
        }
    }

    public void setBody(String text) {
        bodyView.setText(text);
    }

    public String getBody() {
        return bodyView.getText().toString();
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
}
