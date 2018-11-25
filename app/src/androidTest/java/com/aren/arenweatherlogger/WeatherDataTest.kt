package com.aren.arenweatherlogger

import android.support.test.espresso.Espresso.onData
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withSpinnerText
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import org.hamcrest.CoreMatchers.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class WeatherDataTest {

    @get:Rule
    val mainActivityTestRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java, true, true)

    @Test
    fun getWeatherDataOfParis() {
        onView(withId(R.id.sp_cities)).perform(click())
        onData(allOf(`is`(instanceOf(String::class.java)), `is`("Paris"))).perform(click())
        onView(withId(R.id.sp_cities)).check(matches(withSpinnerText(containsString("Paris"))))
        onView(withId(R.id.btn_save)).perform(click())
    }
}