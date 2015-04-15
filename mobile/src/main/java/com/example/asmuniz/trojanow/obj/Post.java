package com.example.asmuniz.trojanow.obj;

import java.util.Date;

/**
 * Created by asmuniz on 3/30/15.
 *
 * A Post represents the actual post that a user publishes
 * to the system.
 */
public class Post {
    private int id;
    private int userId;
    private String username;
    private int feedId;
    private String message;
    private Date createdTs;
    private double latitude;
    private double longitude;

    private Post(Builder builder) {
        this.id = builder.id;
        this.userId = builder.userId;
        this.username = builder.username;
        this.feedId = builder.feedId;
        this.message = builder.message;
        this.createdTs = builder.createdTs;
        this.latitude = builder.latitude;
        this.longitude = builder.longitude;
    }

    public String getMessage() {
        return message;
    }

    public String getUsername() {
        return username;
    }

    public static class Builder {
        // required params
        private int id;
        private int userId;
        private String username;
        private int feedId;
        private Date createdTs;

        // optional params
        private String message = "";
        private double latitude = 0;
        private double longitude = 0;

        public Builder(int id, int userId, String username, int feedId, Date createdTs) {
            this.id = id;
            this.userId = userId;
            this.username = username;
            this.feedId = feedId;
            this.createdTs = createdTs;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder latitude(double latitude) {
            this.latitude = latitude;
            return this;
        }

        public Builder longitude(double longitude) {
            this.longitude = longitude;
            return this;
        }

        public Post build() {
            return new Post(this);
        }
    }
}
