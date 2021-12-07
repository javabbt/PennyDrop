package dev.mfazio.pennydrop

import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.PositionAssertions.isCompletelyBelow
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher
import org.hamcrest.core.AllOf.allOf
import org.hamcrest.core.AnyOf.anyOf
import org.hamcrest.core.IsNot.not
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class GameFragmentTests {
    @get:Rule
    var activityScenarioRule = activityScenarioRule<MainActivity>()

    private val coinSlotMap = mapOf(
        "1" to R.id.coinSlot1,
        "2" to R.id.coinSlot2,
        "3" to R.id.coinSlot3,
        "4" to R.id.coinSlot4,
        "5" to R.id.coinSlot5,
        "6" to R.id.coinSlot6
    )

    @Before
    fun startNewGameBeforeEachTest() = runBlockingTest {
        startGame(activityScenarioRule.scenario)
    }

    @Test
    fun checkStartingSlots() {
        coinSlotMap.forEach { (slotNumber, slotId) ->
            onView(
                allOf(withId(R.id.slotNumberCoinSlot), withParent(withId(slotId)))
            ).check(matches(withText(slotNumber)))

            onView(
                allOf(withId(R.id.coinImageCoinSlot), withParent(withId(slotId)))
            ).check(matches(not(isDisplayed())))

            if (slotId != R.id.coinSlot6) {
                onView(withId(R.id.coinSlot6)).check(isCompletelyBelow(withId(slotId)))
            }
        }
    }

    @Test
    fun checkSingleRollResult() = runBlockingTest {
        onView(withId(R.id.rollButton)).perform(click())

        onView(withId(R.id.textCurrentPlayerName)).check(matches(withText("Michael")))
        onView(withId(R.id.textCurrentPlayerCoinsLeft)).check(matches(withText("9")))

        onView(
            allOf(
                withId(R.id.bottomViewCoinSlot),
                isLastRolled(),
                anyOf(
                    hasSibling(
                        allOf(withId(R.id.coinImageCoinSlot), isDisplayed())
                    ),
                    hasSibling(
                        allOf(withId(R.id.slotNumberCoinSlot), withText("6"))
                    )
                )
            )
        ).check { view, noViewFoundException ->
            assertNull(noViewFoundException)
            assertNotNull(view)
        }
    }

    private fun isLastRolled() = object : TypeSafeMatcher<View>() {
        override fun describeTo(description: Description?) {
            description?.appendText("The View is activated.")
        }

        override fun matchesSafely(view: View?): Boolean = view?.isActivated == true
    }
}