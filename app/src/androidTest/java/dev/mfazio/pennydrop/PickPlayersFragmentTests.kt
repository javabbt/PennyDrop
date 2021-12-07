package dev.mfazio.pennydrop

import androidx.navigation.findNavController
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.mfazio.pennydrop.game.AI
import org.hamcrest.CoreMatchers.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PickPlayersFragmentTests {

    @get:Rule
    var activityScenarioRule = activityScenarioRule<MainActivity>()

    @Before
    fun goToPickPlayersFragment() {
        activityScenarioRule.scenario.onActivity { activity ->
            activity.findNavController(R.id.containerFragment).navigate(R.id.pickPlayersFragment)
        }
    }

    @Test
    fun testFindFab() {
        onView(withId(R.id.buttonPlayGame)).check(matches(isDisplayed()))
    }

    @Test
    fun testAddingNamedPlayers() {
        typeInPlayerName(R.id.mainPlayer, "Michael")
        typeInPlayerName(R.id.player2, "Emily")
        closeSoftKeyboard()

        onView(withId(R.id.buttonPlayGame)).perform(click())

        onView(withId(R.id.textCurrentPlayerName)).check(matches(withText("Michael")))

        onView(withId(R.id.textCurrentStandingsInfo)).check(
            matches(
                allOf(
                    withText(containsString("Michael - 10 pennies")),
                    withText(containsString("Emily - 10 pennies"))
                )
            )
        )

    }

    @Test
    fun testAddingThreeNamedPlayers() {
        typeInPlayerName(R.id.mainPlayer, "Michael")
        typeInPlayerName(R.id.player2, "Emily")

        clickPlayerCheckbox(R.id.player3)

        typeInPlayerName(R.id.player3, "Hazel")

        closeSoftKeyboard()

        onView(withId(R.id.buttonPlayGame)).perform(click())

        onView(withId(R.id.textCurrentPlayerName)).check(matches(withText("Michael")))

        onView(withId(R.id.textCurrentStandingsInfo)).check(
            matches(
                allOf(
                    withText(containsString("Michael - 10 pennies")),
                    withText(containsString("Emily - 10 pennies")),
                    withText(containsString("Hazel - 10 pennies"))
                )
            )
        )
    }

    @Test
    fun testAddingThirdAIPlayer() {
        typeInPlayerName(R.id.mainPlayer, "Michael")
        typeInPlayerName(R.id.player2, "Emily")

        closeSoftKeyboard()

        clickPlayerCheckbox(R.id.player3)

        onView(
            allOf(
                withId(R.id.switch_player_type),
                withParent(withId(R.id.player3))
            )
        ).perform(click())

        onView(
            allOf(
                withId(R.id.spinner_ai_name),
                withParent(withId(R.id.player3))
            )
        ).perform(click())

        //AI Position #3 is Fearful Fred
        onData(`is`(instanceOf(AI::class.java))).atPosition(3).perform(click())

        onView(withId(R.id.buttonPlayGame)).perform(click())

        onView(withId(R.id.textCurrentPlayerName)).check(matches(withText("Michael")))

        onView(withId(R.id.textCurrentStandingsInfo)).check(
            matches(
                allOf(
                    withText(containsString("Michael - 10 pennies")),
                    withText(containsString("Emily - 10 pennies")),
                    withText(containsString("Fearful Fred - 10 pennies"))
                )
            )
        )
    }
}