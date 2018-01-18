package com.magicbuddha.redditorsdigest.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Magic_Buddha on 12/29/2017.
 */

public class SubscriptionsContract {

    public static final String AUTHORITY = "com.magicbuddha.redditorsdigest";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_SUBSCRIPTIONS = "subscriptions";

    public static final class SubscriptionEntity implements BaseColumns {
        public final static Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_SUBSCRIPTIONS).build();

        public final static String TABLE_NAME = "subscriptions";

        public final static String SUBSCRIPTION_COLUMN = "subscription";
    }
}
