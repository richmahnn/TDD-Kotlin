package com.lubulwa.tddkotlin.example3

class Interval(private val mStart: Int, private val mEnd: Int) {

    init {
        require(mStart < mEnd) { "Invalid interval range" }
    }

    fun getStart(): Int = mStart

    fun getEnd(): Int = mEnd

}
