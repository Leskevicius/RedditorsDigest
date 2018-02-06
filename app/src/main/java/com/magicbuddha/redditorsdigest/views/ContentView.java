package com.magicbuddha.redditorsdigest.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by Magic_Buddha on 2/4/2018.
 */

public class ContentView extends RelativeLayout {
    public ContentView(Context context) {
        super(context);

        init(context, null);
    }

    public ContentView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs);
    }

    public ContentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {

    }
}
