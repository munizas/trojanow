package com.example.asmuniz.trojanow.obj;

/**
 * Created by asmuniz on 3/30/15.
 *
 * Represents an association between a User and a Post.
 * Each Post belongs to one User.
 * A User can have many Posts.
 */
public class UserFeed {
    private int id;
    private int userId;
    private int feedId;

    public UserFeed(int id, int userId, int feedId) {
        this.id = id;
        this.userId = userId;
        this.feedId = feedId;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public int getFeedId() {
        return feedId;
    }
}
