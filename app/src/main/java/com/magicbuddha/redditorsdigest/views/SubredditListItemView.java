package com.magicbuddha.redditorsdigest.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.magicbuddha.redditorsdigest.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Magic_Buddha on 12/28/2017.
 */

public class SubredditListItemView extends RelativeLayout {

    @BindView(R.id.title)
    TextView title;

    @BindView(R.id.hint)
    TextView hint;

    @BindView(R.id.subscribe_button)
    ImageView subscribeButton;

    private boolean isSubscribed;
    private OnSubscribeListener listener;

    public SubredditListItemView(Context context) {
        super(context);

        init(context, null);
    }

    public SubredditListItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs);
    }

    public SubredditListItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs);
    }

    void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.subreddit_list_item, this);
        ButterKnife.bind(this, this);

        int horizontalMarginPx = (int) getResources().getDimension(R.dimen.subreddit_list_item_horizontal_margin);
        int verticalMarginPx = (int) getResources().getDimension(R.dimen.subreddit_list_item_vertical_margin);

        LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        ll.setMargins(horizontalMarginPx, verticalMarginPx, horizontalMarginPx, verticalMarginPx);

        setLayoutParams(ll);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.SubredditListItemView,
                0, 0);

        String title;
        String hint;
        final boolean isSubscribed;

        try {
            title = a.getString(R.styleable.SubredditListItemView_titleText);
            hint = a.getString(R.styleable.SubredditListItemView_hintText);
            isSubscribed = a.getBoolean(R.styleable.SubredditListItemView_subscribed, false);
        } finally {
            a.recycle();
        }

        this.title.setText(title);
        this.hint.setText(hint);
        setSubscribed(isSubscribed);
        this.subscribeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    setSubscribed(!isSubscribed());
                    listener.onSubscribeClicked(isSubscribed());
                }
            }
        });
    }

    public void setTitle(String title) {
        this.title.setText(title);
    }

    public String getTitle() {
        return this.title.getText().toString();
    }

    public void setHint(String hint) {
        this.hint.setText(hint);
    }

    public String getHint() {
        return this.hint.getText().toString();
    }

    public void setSubscribed(boolean isSubscribed) {
        this.isSubscribed = isSubscribed;
        this.subscribeButton.setBackground(isSubscribed ?
                ContextCompat.getDrawable(getContext(), R.drawable.ic_favorite) :
                ContextCompat.getDrawable(getContext(), R.drawable.ic_not_favorite));
    }

    public boolean isSubscribed() {
        return this.isSubscribed;
    }

    public void setOnSubscribeListener(OnSubscribeListener listener) {
        this.listener = listener;
    }

    public OnSubscribeListener getOnSubscribeListener() {
        return this.listener;
    }

    public interface OnSubscribeListener {

        void onSubscribeClicked(boolean isSubscribed);
    }
}
