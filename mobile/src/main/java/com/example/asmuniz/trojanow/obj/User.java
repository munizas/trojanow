package com.example.asmuniz.trojanow.obj;

/**
 * Created by asmuniz on 3/30/15.
 *
 * A user of the system.
 */
public class User {

    private static User activeUser;

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

    public static User getActiveUser() { return activeUser; }

    public static void setActiveUser(User user) { activeUser = user; }

    public String toString() {
        return "User (" + id + "): " + username;
    }
}
