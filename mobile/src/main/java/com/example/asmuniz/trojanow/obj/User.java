package com.example.asmuniz.trojanow.obj;

/**
 * Created by asmuniz on 3/30/15.
 *
 * A user of the system.
 */
public class User {

    private static int activeUserId;

    private int id;
    private String username;
    private String email;

    public User(int id, String username, String email) {
        this.id = id;
        this.email = email;
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public static int getActiveUserId() {
        return activeUserId;
    }

    public static void setActiveUserId(int userId) {
        activeUserId = userId;
    }
}
