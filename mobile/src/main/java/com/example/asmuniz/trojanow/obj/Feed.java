package com.example.asmuniz.trojanow.obj;

import com.example.asmuniz.trojanow.util.FeedCenter;

/**
 * Created by asmuniz on 3/30/15.
 *
 * Represents a Feed object.  A Feed can be described as a
 * container for Posts.  Each Post belongs to a Feed.
 */
public class Feed {

    private static int activeFeedId = 1;

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

    public static int getActiveFeedId() {
        return activeFeedId;
    }

    public static void setActiveFeedId(int feed) {
        activeFeedId = feed;
    }

}
