package com.magicbuddha.redditorsdigest.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.magicbuddha.redditorsdigest.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Magic_Buddha on 2/6/2018.
 */
// title, points, subreddit, author, body
public class SelfPostView extends LinearLayout {

    @BindView(R.id.self_post_title)
    TextView titleView;
    private String title;

    @BindView(R.id.self_post_author)
    TextView authorView;
    private  String author;

    @BindView(R.id.self_post_subreddit)
    TextView subredditView;
    private String subreddit;

    @BindView(R.id.self_post_points)
    TextView pointsView;
    private String points;

    @BindView(R.id.self_post_body)
    TextView bodyView;
    private String body;

    public SelfPostView(Context context) {
        super(context);

        init(context, null);
    }

    public SelfPostView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs);
    }

    public SelfPostView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.self_post, this);
        ButterKnife.bind(this, this);

        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
        setLayoutParams(linearParams);
        setOrientation(VERTICAL);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.CommentView,
                0, 0);

        int depth;

        try {
            depth = a.getInt(R.styleable.CommentView_depth, 0);
        } finally {
            a.recycle();
        }

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        titleView.setText(title);
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
        authorView.setText(author);
    }

    public String getSubreddit() {
        return subreddit;
    }

    public void setSubreddit(String subreddit) {
        this.subreddit = subreddit;
        subredditView.setText(subreddit);
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
        String displayText = points + " " + getResources().getString(R.string.points);
        pointsView.setText(displayText);
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
        bodyView.setText(body);
    }
}
