package com.example.asmuniz.trojanow.util;

import com.example.asmuniz.trojanow.obj.User;

/**
 * Created by asmuniz on 4/16/15.
 */
public class UserSelectionModel {

    private User user;
    private boolean isSelected;

    public UserSelectionModel(User user) {
        this.user = user;
    }

    public User getUser() { return user; }

    public String getUsername() { return user.getUsername(); }

    public boolean isSelected() { return isSelected; }

    public void setSelected(boolean isSelected) { this.isSelected = isSelected; }
}
