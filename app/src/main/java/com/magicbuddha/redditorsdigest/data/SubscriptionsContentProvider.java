package com.magicbuddha.redditorsdigest.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Magic_Buddha on 12/29/2017.
 */

public class SubscriptionsContentProvider extends ContentProvider {

    private static final String LOG = SubscriptionsContentProvider.class.getCanonicalName();

    private static final int SUBSCRIPTIONS = 100;

    private static final int SUBSCRIPTIONS_BY_NAME = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private SubscriptionsDBHelper mdbHelper;

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(SubscriptionsContract.AUTHORITY, SubscriptionsContract.PATH_SUBSCRIPTIONS, SUBSCRIPTIONS);
        uriMatcher.addURI(SubscriptionsContract.AUTHORITY, SubscriptionsContract.PATH_SUBSCRIPTIONS + "/*", SUBSCRIPTIONS_BY_NAME);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        mdbHelper = new SubscriptionsDBHelper(getContext());

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase database = mdbHelper.getReadableDatabase();

        int query = sUriMatcher.match(uri);
        Cursor cursor = null;

        switch (query) {
            case SUBSCRIPTIONS: {
                cursor = database.query(
                        SubscriptionsContract.SubscriptionEntity.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }

            default: {
                throw new UnsupportedOperationException("Could not recognize uri " + uri);
            }
        }

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase database = mdbHelper.getWritableDatabase();

        int query = sUriMatcher.match(uri);
        Uri returnUri = null;

        switch (query) {
            case SUBSCRIPTIONS: {
                long id = database.insert(
                        SubscriptionsContract.SubscriptionEntity.TABLE_NAME,
                        null,
                        values);

                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(SubscriptionsContract.SubscriptionEntity.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Could not insert a row to: " + uri);
                }
                break;
            }

            default: {
                throw new UnsupportedOperationException("Could not recognize uri " + uri);
            }
        }

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = mdbHelper.getWritableDatabase();

        int query = sUriMatcher.match(uri);
        int subscriptionsDeleted = 0;

        switch (query) {
            case SUBSCRIPTIONS_BY_NAME: {
                String subscription = uri.getPathSegments().get(1);

                subscriptionsDeleted = database.delete(
                        SubscriptionsContract.SubscriptionEntity.TABLE_NAME,
                        "subscription=?",
                        new String[]{subscription}
                );

                break;
            }
            default:
                throw new UnsupportedOperationException("Could not delete subscription uri: " + uri);
        }

        if (subscriptionsDeleted > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return subscriptionsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = mdbHelper.getWritableDatabase();

        int query = sUriMatcher.match(uri);
        int subscriptionsUpdated = 0;

        switch (query) {
            case SUBSCRIPTIONS: {
                subscriptionsUpdated = database.update(
                        SubscriptionsContract.SubscriptionEntity.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs
                );
                break;
            }
            case SUBSCRIPTIONS_BY_NAME: {
                String subscription = uri.getPathSegments().get(1);
                subscriptionsUpdated = database.update(
                        SubscriptionsContract.SubscriptionEntity.TABLE_NAME,
                        values,
                        "subscription=?",
                        new String[]{subscription}
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Could not recognize subscription uri " + uri);
        }

        if (subscriptionsUpdated > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return subscriptionsUpdated;
    }
}
