package com.magicbuddha.redditorsdigest.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Magic_Buddha on 12/29/2017.
 */

public class SubscriptionsDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "subscriptions.db";

    private static final int VERSION = 2;

    public SubscriptionsDBHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_TABLE = "CREATE TABLE " + SubscriptionsContract.SubscriptionEntity.TABLE_NAME + " (" +
                SubscriptionsContract.SubscriptionEntity._ID                   + " INTEGER PRIMARY KEY, " +
                SubscriptionsContract.SubscriptionEntity.SUBSCRIPTION_COLUMN   + " TEXT NOT NULL);";

        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + SubscriptionsContract.SubscriptionEntity.TABLE_NAME);
        onCreate(db);
    }
}
