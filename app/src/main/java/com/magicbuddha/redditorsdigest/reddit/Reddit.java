package com.magicbuddha.redditorsdigest.reddit;

import android.support.annotation.NonNull;

import net.dean.jraw.RedditClient;

/**
 * Created by Magic_Buddha on 12/24/2017.
 */

public class Reddit {
    private RedditClient reddit;

    private static Reddit redditInstance;

    private static final String TAG = Reddit.class.getCanonicalName();
    private boolean initialized;

    /**
     * Safe way of accessing a {@link Reddit} singleton reference.
     *
     * @return {@link Reddit}.
     */
    public static Reddit getInstance() {
        if (redditInstance == null) {
            synchronized (Reddit.class) {
                if (redditInstance == null) {
                    redditInstance = new Reddit();
                }
            }
        }

        return redditInstance;
    }

    /**
     * Set the {@link RedditClient} for {@link Reddit}. Other views will grab this client to initiate
     * tasks that require reddit data.
     *
     * @param client {@link RedditClient}.
     */
    public synchronized void setRedditClient(@NonNull RedditClient client) {
        this.reddit = client;
        this.initialized = true;
    }

    /**
     * Grab {@link RedditClient}.
     *
     * @return {@link RedditClient}.
     */
    public RedditClient getRedditClient() {
        checkClient();
        return this.reddit;
    }

    // makes sure that client has been initialized
    private void checkClient() {
        if (this.reddit == null) {
            throw new IllegalStateException("Reddit client was not initialized before using Reddit api.");
        }
    }

    public boolean initialized() {
        return initialized;
    }
}
