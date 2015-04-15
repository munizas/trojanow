package com.example.asmuniz.trojanow.obj;

import com.example.asmuniz.trojanow.util.FeedCenter;

/**
 * Created by asmuniz on 3/30/15.
 *
 * Represents a Feed object.  A Feed can be described as a
 * container for Posts.  Each Post belongs to a Feed.
 */
public class Feed {

    private static Feed activeFeed;

    static {
        activeFeed = new Feed("Public");
        activeFeed.setId(1);
    }

    private int id;
    private String name;

    public Feed(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static Feed getActiveFeed() { return activeFeed; }

    public static void setActiveFeed(Feed feed) { activeFeed = feed; }

    public static void setToPublicFeed() {
        activeFeed.setId(1);
        activeFeed.setName("Public");
    }

}
