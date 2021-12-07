package dev.mfazio.pennydrop

import androidx.navigation.findNavController
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withParent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.allOf

@ExperimentalCoroutinesApi
fun startGame(scenario: ActivityScenario<MainActivity>) = runBlockingTest {
    scenario.onActivity { activity ->
        activity.findNavController(R.id.containerFragment).navigate(R.id.pickPlayersFragment)
    }

    typeInPlayerName(R.id.mainPlayer, "Michael")
    typeInPlayerName(R.id.player2, "Emily")

    clickPlayerCheckbox(R.id.player3)

    typeInPlayerName(R.id.player3, "Hazel")

    closeSoftKeyboard()

    onView(withId(R.id.buttonPlayGame)).perform(click())
}

fun typeInPlayerName(parentId: Int, text: String) {
    onView(
        allOf(
            withId(R.id.edit_text_player_name),
            withParent(withId(parentId))
        )
    ).perform(typeText(text))
}

fun clickPlayerCheckbox(parentId: Int) {
    onView(
        allOf(
            withId(R.id.checkbox_player_active),
            withParent(withId(parentId))
        )
    ).perform(click())
}