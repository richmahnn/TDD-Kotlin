package com.lubulwa.tddkotlin.example7.users

interface UsersCache {

    fun cacheUser(user: User)

    fun getUser(userId: String): User

}
