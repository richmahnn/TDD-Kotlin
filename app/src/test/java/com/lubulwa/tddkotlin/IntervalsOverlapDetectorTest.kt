package com.lubulwa.tddkotlin

import com.lubulwa.tddkotlin.example3.Interval
import com.lubulwa.tddkotlin.example3.IntervalsOverlapDetector
import org.hamcrest.CoreMatchers.`is`
import org.junit.Before

import org.junit.Assert.*
import org.junit.Test

class IntervalsOverlapDetectorTest {

    private lateinit var SUT: IntervalsOverlapDetector

    @Before
    fun setUp() {
        SUT = IntervalsOverlapDetector()
    }

    // Interval1 is before Interval2
    @Test
    fun isOverlap_interval1BeforeInterval2_falseReturned() {
        val interval1 = Interval(-1, 5)
        val interval2 = Interval(8, 12)

        val result = SUT.isOverlap(interval1, interval2)
        assertThat(result, `is`(false))
    }

    // Interval1 overlaps Interval2 on start
    @Test
    fun isOverlap_interval1OverlapsInterval2OnStart_trueReturned() {
        val interval1 = Interval(-1, 5)
        val interval2 = Interval(3, 12)

        val result = SUT.isOverlap(interval1, interval2)
        assertThat(result, `is`(true))
    }

    // Interval1 is contained in within Interval2
    @Test
    fun isOverlap_interval1ContainedInInterval2_trueReturned() {
        val interval1 = Interval(-1, 5)
        val interval2 = Interval(-4, 12)

        val result = SUT.isOverlap(interval1, interval2)
        assertThat(result, `is`(true))
    }

    // Interval1 contains Interval2
    @Test
    fun isOverlap_interval1ContainsInterval2_trueReturned() {
        val interval1 = Interval(-3, 5)
        val interval2 = Interval(0, 4)

        val result = SUT.isOverlap(interval1, interval2)
        assertThat(result, `is`(true))
    }

    // Interval1 overlaps Interval2 on end
    @Test
    fun isOverlap_interval1OverlapsInterval2OnEnd_trueReturned() {
        val interval1 = Interval(-1, 5)
        val interval2 = Interval(-4, 4)

        val result = SUT.isOverlap(interval1, interval2)
        assertThat(result, `is`(true))
    }

    // Interval1 is after interval2
    @Test
    fun isOverlap_interval1IsAfterInterval2_falseReturned() {
        val interval1 = Interval(6, 11)
        val interval2 = Interval(-4, 4)

        val result = SUT.isOverlap(interval1, interval2)
        assertThat(result, `is`(false))
    }
}