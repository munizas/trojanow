package com.example.asmuniz.trojanow.obj;

/**
 * Created by asmuniz on 3/30/15.
 *
 * A FeedPost object represents an association between
 * a Feed and a Post object.
 * A Post belongs to one Feed.
 * A Feed can have many Posts.
 */
public class FeedPost {
    private int id;
    private int feedId;
    private int postId;

    public FeedPost(int id, int feedId, int postId) {
        this.id = id;
        this.feedId = feedId;
        this.postId = postId;
    }

    public int getId() {
        return id;
    }

    public int getFeedId() {
        return feedId;
    }

    public int getPostId() {
        return postId;
    }
}
