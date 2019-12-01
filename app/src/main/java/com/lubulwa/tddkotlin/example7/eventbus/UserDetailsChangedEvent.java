package com.lubulwa.tddkotlin.example7.eventbus;

import com.lubulwa.tddkotlin.example7.users.User;

public class UserDetailsChangedEvent {

    private final User mUser;

    public UserDetailsChangedEvent(User user) {
        mUser = user;
    }

    public User getUser() {
        return mUser;
    }
}
