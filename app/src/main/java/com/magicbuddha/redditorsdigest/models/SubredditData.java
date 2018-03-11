package com.magicbuddha.redditorsdigest.models;

import android.os.Parcel;
import android.os.Parcelable;

import net.dean.jraw.models.Subreddit;

/**
 * Created by Magic_Buddha on 3/11/2018.
 */

public class SubredditData implements Parcelable {
    private String name;
    private int subscribers;

    public static SubredditData parse(Subreddit subreddit) {
        return new SubredditData(subreddit.getName(), subreddit.getSubscribers());
    }

    private SubredditData(String name, int subscribers) {
        this.name = name;
        this.subscribers = subscribers;
    }

    protected SubredditData(Parcel in) {
        name = in.readString();
        subscribers = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(subscribers);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SubredditData> CREATOR = new Creator<SubredditData>() {
        @Override
        public SubredditData createFromParcel(Parcel in) {
            return new SubredditData(in);
        }

        @Override
        public SubredditData[] newArray(int size) {
            return new SubredditData[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(int subscribers) {
        this.subscribers = subscribers;
    }
}
