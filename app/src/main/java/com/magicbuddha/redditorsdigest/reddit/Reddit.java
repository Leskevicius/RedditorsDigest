package com.magicbuddha.redditorsdigest.reddit;

import net.dean.jraw.RedditClient;

/**
 * Created by Magic_Buddha on 12/24/2017.
 */

public class Reddit {
    private RedditClient reddit;

    private static Reddit redditInstance;

    private static final String TAG = Reddit.class.getCanonicalName();

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
    public synchronized void setRedditClient(RedditClient client) {
        this.reddit = client;
    }

    /**
     *
     *
     * @return
     */
    public RedditClient getRedditClient() {
        return this.reddit;
    }

}
