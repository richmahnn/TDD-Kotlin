package com.lubulwa.tddkotlin.example5.users;

import org.jetbrains.annotations.Nullable;

public interface UsersCache {

    void cacheUser(User user);

    @Nullable User getUser(String userId);

}
