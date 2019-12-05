package com.lubulwa.tddkotlin.example9

class PingServerSyncUseCase {


    enum class UseCaseResult {
        SUCCESS
    }

    fun pingServer(): UseCaseResult {
        return UseCaseResult.SUCCESS
    }

}